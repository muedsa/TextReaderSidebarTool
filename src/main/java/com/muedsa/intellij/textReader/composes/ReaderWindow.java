package com.muedsa.intellij.textReader.composes;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBList;
import com.muedsa.intellij.textReader.Chapter;
import com.muedsa.intellij.textReader.TextFile;
import com.muedsa.intellij.textReader.file.TextFileChooserDescriptor;
import com.muedsa.intellij.textReader.notify.Notification;
import com.muedsa.intellij.textReader.state.TextReaderStateService;
import com.muedsa.intellij.textReader.util.ChapterUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ReaderWindow {
    private final Project project;
    private final ToolWindow toolWindow;

    private JPanel readerPanel;
    private JBList<Chapter> titleList;
    private JButton fileButton;
    private JSpinner fontSizeSpinner;
    private JButton previousButton;
    private JButton nextButton;
    private JTextPane textContent;
    private JTabbedPane tab;
    private JScrollPane textContentScroll;
    private JTextField chapterPrefixEl;
    private JTextField chapterSuffixEl;
    private JTextField regexStringEl;
    private JTextField fixTitleEl;
    private JSpinner maxLineSizeSpinner;
    private JSpinner lineSpaceSpinner;
    private JSpinner firstLineIndentSpinner;
    private JComboBox<String> fontFamilyEl;
    private JButton clearButton;
    private JSpinner paragraphSpaceSpinner;
    private JTextField searchTextField;
    private JSpinner lineSizeSpinner;

    private TextFile textFile;

    private TextReaderStateService textReaderStateService;

    private static Vector<Chapter> CHAPTER_LIST = new Vector<>(0);

    private Integer positionInChapter = 0;
    private String noBlankChapterText;

    public ReaderWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        toolWindow.setTitle("TextReader");
        createUIComponents();
        init();
        updateRegex();
        ReaderWindowHolder.put(project, this);
    }

    public JPanel getContent(){
        return readerPanel;
    }

    private void createUIComponents(){
        //字体
        String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(fontFamilyNames);
        fontFamilyEl.setModel(comboBoxModel);
        fontFamilyEl.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
                updateFontFamily();
            }
        });
        String currentFontFamily = textContent.getFont().getFamily();
        int index = Arrays.binarySearch(fontFamilyNames, currentFontFamily);
        if(index > 0){
            fontFamilyEl.setSelectedIndex(index);
        }

        //字体大小
        SpinnerModel fontSizeSpinnerModel = new SpinnerNumberModel(12, 0, 100, 1);
        fontSizeSpinner.setModel(fontSizeSpinnerModel);
        fontSizeSpinner.addChangeListener(e -> updateFontSize());
        //字体行间距
        SpinnerModel lineSpaceSpinnerModel = new SpinnerNumberModel(0.5, 0, 2.5, 0.1);
        lineSpaceSpinner.setModel(lineSpaceSpinnerModel);
        lineSpaceSpinner.addChangeListener(e -> updateLineSpace());
        //首行缩进
        SpinnerModel firstLineIndentSpinnerModel = new SpinnerNumberModel(2, 0, 4, 1);
        firstLineIndentSpinner.setModel(firstLineIndentSpinnerModel);
        firstLineIndentSpinner.addChangeListener(e -> updateFirstLineIndent());

        //段落间隔
        SpinnerModel paragraphSpaceSpinnerModel = new SpinnerNumberModel(1, 0, 4, 1);
        paragraphSpaceSpinner.setModel(paragraphSpaceSpinnerModel);
        paragraphSpaceSpinner.addChangeListener(e -> updateParagraphSpace());

        //标题解析最大字数限制设置
        SpinnerModel maxLineSizeSpinnerModel = new SpinnerNumberModel(30, 1, 200, 1);
        maxLineSizeSpinner.setModel(maxLineSizeSpinnerModel);

        //添加文件
        fileButton.addActionListener(e -> {
            VirtualFile file = FileChooser.chooseFile(new TextFileChooserDescriptor(), project, null);
            if(file != null){
                try {
                    updateRegex();
                    Pattern pattern = Pattern.compile(regexStringEl.getText().trim());
                    textFile = new TextFile(file);
                    try {
                        CHAPTER_LIST = ChapterUtil.getChapters(textFile, (int)maxLineSizeSpinner.getValue(), pattern);
                        sendNotify("加载成功", textFile.getFilePath()+"<br><em>"+textFile.getCharset().displayName()+"</em> 共"+CHAPTER_LIST.size()+"章", NotificationType.INFORMATION);
                    }catch (IOException error){
                        error.printStackTrace();
                        sendNotify("加载失败", error.getLocalizedMessage(), NotificationType.WARNING);
                    }
                    catch (PatternSyntaxException error){
                        error.printStackTrace();
                        sendNotify("正则错误", error.getLocalizedMessage(), NotificationType.ERROR);
                    }
                    titleList.setListData(CHAPTER_LIST);
                    textReaderStateService.setFilePath(textFile.getFilePath());
                    textReaderStateService.setChapters(CHAPTER_LIST);
                    searchTextField.setEnabled(true);
                }
                catch (PatternSyntaxException error){
                    error.printStackTrace();
                    sendNotify("正则错误", error.getLocalizedMessage(), NotificationType.ERROR);
                }
                catch (IOException error){
                    error.printStackTrace();
                    sendNotify("文件读取错误", error.getLocalizedMessage(), NotificationType.ERROR);
                }
                catch (Exception error){
                    error.printStackTrace();
                    sendNotify("其他错误", error.getLocalizedMessage(), NotificationType.ERROR);
                }
            }
        });

        //清除
        clearButton.addActionListener(e -> {
            textFile = null;
            CHAPTER_LIST = new Vector<>(0);
            titleList.setListData(CHAPTER_LIST);
            textContent.setText("");
            textContent.setCaretPosition(0);
            textReaderStateService.setFilePath("");
            textReaderStateService.setChapters(CHAPTER_LIST);
            searchTextField.setText("");
            searchTextField.setEnabled(false);
        });


        searchTextField.getDocument().addDocumentListener(new DocumentAdapter() {

            private final List<searchThread> threadList = new Vector<>();

            private void search(String searchText){
                if(StringUtils.isEmpty(searchText)){
                    if(titleList.getModel().getSize() != CHAPTER_LIST.size()){
                        titleList.setListData(CHAPTER_LIST);
                    }
                }else{
                    Vector<Chapter> tempChapterList = CHAPTER_LIST.stream().filter(c -> c.getTitle().contains(searchText)).collect(Collectors.toCollection(Vector::new));
                    if(tempChapterList.size() != CHAPTER_LIST.size()){
                        titleList.setListData(tempChapterList);
                    }
                }
            }

            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent){
                threadList.forEach(i -> {
                    if(i != null){
                        i.cancel();
                    }
                });
                String searchText = searchTextField.getText();
                searchThread thread = new searchThread(searchText);
                thread.start();
                threadList.add(thread);
            }

            class searchThread extends Thread{
                private boolean cancel = false;
                private final String searchText;

                public searchThread(String searchText){
                    super();
                    this.searchText = searchText;
                }

                @Override
                public void run(){
                    try{
                        Thread.sleep(300);
                        if(!cancel){
                            search(searchText);
                        }
                        threadList.remove(this);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
                public void cancel(){
                    cancel = true;
                }
            }
        });

        //点击章节
        titleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    Chapter temp = titleList.getSelectedValue();
                    titleList.setListData(CHAPTER_LIST);
                    titleList.setSelectedValue(temp, false);
                    setTextContent();
                    tab.setSelectedIndex(1);
                }
            }
        });

        //下一章
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                nextChapter();
            }
        });

        //上一章
        previousButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = titleList.getSelectedIndex() - 1;
                if(index >= 0){
                    titleList.setSelectedIndex(index);
                    setTextContent();
                }
            }
        });

        //每行字数
        SpinnerModel lineSizeSpinnerModel = new SpinnerNumberModel(15, 0, 100, 1);
        lineSizeSpinner.setModel(lineSizeSpinnerModel);
    }

    private void init(){
        //持久化配置初始化
        textReaderStateService = TextReaderStateService.getInstance();
        if(textReaderStateService != null && textReaderStateService.getFilePath() != null && textReaderStateService.getChapters() != null){
            try {
                String filePath = textReaderStateService.getFilePath();
                if(StringUtils.isNotBlank(filePath)){
                    textFile = new TextFile(filePath);
                    CHAPTER_LIST = textReaderStateService.getChapters();
                    titleList.setListData(CHAPTER_LIST);
                    searchTextField.setEnabled(true);
                }
            }
            catch (IOException error){
                error.printStackTrace();
                sendNotify("持久化文件加载错误", error.getLocalizedMessage(), NotificationType.ERROR);
            }
        }

        //字体风格初始化
        updateFontSize();
        updateLineSpace();
        updateFirstLineIndent();
    }

    private void updateRegex(){
        StringBuilder regexString = new StringBuilder(regexStringEl.getText().trim());
        if(regexString.length() == 0){
            //章节
            regexString = new StringBuilder(chapterPrefixEl.getText().trim());
            regexString.append("[0-9零一二三四五六七八九十百千万]+");
            regexString.append(chapterSuffixEl.getText().trim());

            //固定标题
            String[] fixTitles = fixTitleEl.getText().trim().split("\\|");
            for(String fixTitle : fixTitles){
                if(fixTitle.length() > 0){
                    regexString.append("|\\s*").append(fixTitle).append("\\s*");
                }
            }
            regexStringEl.setText(regexString.toString());
        }
    }

    private void updateFontFamily(){
        String fontFamily = (String)fontFamilyEl.getSelectedItem();
        StyledDocument styledDocument = textContent.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attributes, fontFamily);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), attributes, false);
    }

    private void updateFontSize(){
        int fontSize = (int)fontSizeSpinner.getValue();
        StyledDocument styledDocument = textContent.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFontSize(attributes, fontSize);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), attributes, false);
    }

    private void updateLineSpace(){
        float lineSpace = ((Double)lineSpaceSpinner.getValue()).floatValue();
        StyledDocument styledDocument = textContent.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(attributes, lineSpace);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), attributes, false);
    }

    private void updateFirstLineIndent(){
        float firstLineIndent = ((Integer)firstLineIndentSpinner.getValue()) * ((Integer)fontSizeSpinner.getValue()).floatValue();
        StyledDocument styledDocument = textContent.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFirstLineIndent(attributes, firstLineIndent);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), attributes, false);
    }

    private void updateParagraphSpace(){
        ChapterUtil.CONFIG_PARAGRAPH_SPACE = (int) paragraphSpaceSpinner.getValue();
        setTextContent();
    }

    private void setTextContent(){
        Chapter chapter = titleList.getSelectedValue();
        if(chapter != null){
            String text = "";
            try{
                text = ChapterUtil.formatChapterContent(textFile, chapter);
            }
            catch (IOException e){
                e.printStackTrace();
                sendNotify("文件读取错误", e.getLocalizedMessage(), NotificationType.ERROR);
            }
            textContent.setText(text);
            noBlankChapterText = text.replaceAll("\\s*", "");
            textContent.setCaretPosition(0);
            positionInChapter = 0;
        }
    }

    private void sendNotify(String title, String content, NotificationType type){
        Notification.sendNotify(project, title, content, type);
    }

    public String nextLine(){
        String line = "正在初始化~~";
        if(toolWindow.isAvailable()){
            int lineSize = (int)lineSizeSpinner.getValue();
            if(lineSize > 0){
                if(StringUtils.isEmpty(noBlankChapterText) || positionInChapter > noBlankChapterText.length()){
                    nextChapter();
                }
                line = StringUtils.mid(noBlankChapterText, positionInChapter, lineSize);
                positionInChapter += lineSize;
                if(StringUtils.isAllBlank(line)){
                    line = nextLine();
                }
            }else{
                line = ">.< 0?";
            }

        }
        return line;
    }

    public String previousLine(){
        String line = "正在初始化~~";
        if(toolWindow.isAvailable()){
            int lineSize = (int)lineSizeSpinner.getValue();
            if(lineSize > 0){
                if(positionInChapter == 0){
                    previousChapter();
                }else{
                    positionInChapter -= lineSize * 2;
                    if(positionInChapter < 0){
                        positionInChapter = 0;
                    }
                }
                line = nextLine();
            }else{
                line = ">.< 0?";
            }
        }
        return line;
    }

    private void nextChapter(){
        int count = titleList.getItemsCount();
        int index = titleList.getSelectedIndex() + 1;
        if(index + 1 <= count){
            titleList.setSelectedIndex(index);
            setTextContent();
        }
    }

    private void previousChapter(){
        int index = titleList.getSelectedIndex() - 1;
        if(index >= 0){
            titleList.setSelectedIndex(index);
            setTextContent();
        }
    }
}
