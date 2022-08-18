package com.muedsa.intellij.textReader.composes;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBList;
import com.muedsa.intellij.textReader.core.Chapter;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.core.config.TextReaderConfig;
import com.muedsa.intellij.textReader.core.event.*;
import com.muedsa.intellij.textReader.file.TextFileChooserDescriptor;
import com.muedsa.intellij.textReader.notify.Notification;
import com.muedsa.intellij.textReader.util.ReaderLineUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ReaderWindow implements Disposable {
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
    private JSpinner maxTitleLineSizeSpinner;
    private JSpinner lineSpaceSpinner;
    private JSpinner firstLineIndentSpinner;
    private JComboBox<String> fontFamilyEl;
    private JButton clearButton;
    private JSpinner paragraphSpaceSpinner;
    private JTextField searchTextField;
    private JSpinner readerLineSizeSpinner;
    private JRadioButton atHiddenNotifyRadioButton;
    private JRadioButton atStatusBarRadioButton;
    private ButtonGroup showReaderLineAtRadioButtonGroup;

    private final TextReaderCore textReaderCore;


    public ReaderWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        this.textReaderCore = TextReaderCore.getInstance();
        createUIComponents();
        init();
        updateRegex();
        eventRegister();
        if(textReaderCore.getChapters().isEmpty()){
            this.searchTextField.setEnabled(true);
        }
        ReaderWindowHolder.put(project, this);
    }

    private void createUIComponents(){
        TextReaderEventManage eventManage = textReaderCore.getEventManage();

        //字体
        String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(fontFamilyNames);
        fontFamilyEl.setModel(comboBoxModel);
        fontFamilyEl.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
                TextReaderConfig.setConfigValue(TextReaderConfig.ConfigKey.FONT_FAMILY, e.getItem(), eventManage, ReaderWindow.this);
            }
        });
        int index = Arrays.binarySearch(fontFamilyNames, TextReaderConfig.getFontFamily());
        if(index > 0){
            fontFamilyEl.setSelectedIndex(index);
        }

        //字体大小
        SpinnerModel fontSizeSpinnerModel = new SpinnerNumberModel(TextReaderConfig.getFontSize(), 0, 100, 1);
        fontSizeSpinner.setModel(fontSizeSpinnerModel);
        fontSizeSpinner.addChangeListener(e -> TextReaderConfig.setConfigValue(TextReaderConfig.ConfigKey.FONT_SIZE,
                fontSizeSpinner.getValue(), eventManage, ReaderWindow.this));

        //字体行间距
        SpinnerModel lineSpaceSpinnerModel = new SpinnerNumberModel(TextReaderConfig.getLineSpace(), 0, 2.5, 0.1);
        lineSpaceSpinner.setModel(lineSpaceSpinnerModel);
        lineSpaceSpinner.addChangeListener(e -> TextReaderConfig.setConfigValue(TextReaderConfig.ConfigKey.LINE_SPACE,
                lineSpaceSpinner.getValue(), eventManage, ReaderWindow.this));

        //首行缩进
        SpinnerModel firstLineIndentSpinnerModel = new SpinnerNumberModel(TextReaderConfig.getFirstLineIndent(), 0, 4, 1);
        firstLineIndentSpinner.setModel(firstLineIndentSpinnerModel);
        firstLineIndentSpinner.addChangeListener(e -> TextReaderConfig.setConfigValue(TextReaderConfig.ConfigKey.FIRST_LINE_INDENT,
                firstLineIndentSpinner.getValue(), eventManage, ReaderWindow.this));

        //段落间隔
        SpinnerModel paragraphSpaceSpinnerModel = new SpinnerNumberModel(TextReaderConfig.getParagraphSpace(), 0, 4, 1);
        paragraphSpaceSpinner.setModel(paragraphSpaceSpinnerModel);
        paragraphSpaceSpinner.addChangeListener(e -> TextReaderConfig.setConfigValue(TextReaderConfig.ConfigKey.PARAGRAPH_SPACE,
                paragraphSpaceSpinner.getValue(), eventManage, ReaderWindow.this));

        //标题解析最大字数限制设置
        SpinnerModel maxLineSizeSpinnerModel = new SpinnerNumberModel(TextReaderConfig.getMaxTitleLineSize(), 1, 200, 1);
        maxTitleLineSizeSpinner.setModel(maxLineSizeSpinnerModel);
        maxTitleLineSizeSpinner.addChangeListener(e -> TextReaderConfig.setConfigValue(TextReaderConfig.ConfigKey.MAX_TITLE_LINE_SIZE,
                maxTitleLineSizeSpinner.getValue(), eventManage, ReaderWindow.this));

        //每行字数
        SpinnerModel lineSizeSpinnerModel = new SpinnerNumberModel(TextReaderConfig.getReaderLineSize(), 0, 100, 1);
        readerLineSizeSpinner.setModel(lineSizeSpinnerModel);
        lineSizeSpinnerModel.addChangeListener(e -> TextReaderConfig.setConfigValue(TextReaderConfig.ConfigKey.READER_LINE_SIZE,
                readerLineSizeSpinner.getValue(), eventManage, ReaderWindow.this));

        //按行读取时展示的位置
        showReaderLineAtRadioButtonGroup = new ButtonGroup();
        showReaderLineAtRadioButtonGroup.add(atHiddenNotifyRadioButton);
        showReaderLineAtRadioButtonGroup.add(atStatusBarRadioButton);
        showReaderLineAtRadioButtonGroup.setSelected(TextReaderConfig.isShowReaderLineAtStatusBar()?
                atStatusBarRadioButton.getModel() : atHiddenNotifyRadioButton.getModel(), true);
        ActionListener actionListener = e -> TextReaderConfig.setConfigValue(TextReaderConfig.ConfigKey.SHOW_READER_LINT_AT_STATUS_BAR,
                showReaderLineAtRadioButtonGroup.getSelection().equals(atStatusBarRadioButton.getModel()), eventManage, ReaderWindow.this);
        atHiddenNotifyRadioButton.addActionListener(actionListener);
        atStatusBarRadioButton.addActionListener(actionListener);

        //添加文件
        fileButton.addActionListener(e -> {
            VirtualFile file = FileChooser.chooseFile(new TextFileChooserDescriptor(), project, null);
            if(file != null){
                try {
                    updateRegex();
                    Pattern pattern = Pattern.compile(regexStringEl.getText().trim());
                    textReaderCore.initWithFile(file, (int) maxTitleLineSizeSpinner.getValue(), pattern);
                    if(!Objects.isNull(textReaderCore.getTextFile())){
                        String filePath;
                        String fileCharset;
                        filePath = textReaderCore.getTextFile().getFilePath();
                        fileCharset = textReaderCore.getTextFile().getCharset().displayName();
                        sendNotify(Notification.TITLE_LOAD_FILE_SUCCESS, String.format("%s<br><em>%s</em> 共%d章", filePath, fileCharset, textReaderCore.getChapters().size()), NotificationType.INFORMATION);
                    }else{
                        sendNotify(Notification.TITLE_LOAD_FILE_ERROR, "........", NotificationType.ERROR);
                    }
                }
                catch (PatternSyntaxException error){
                    error.printStackTrace();
                    sendNotify(Notification.TITLE_LOAD_FILE_REGEX_ERROR, error.getLocalizedMessage(), NotificationType.ERROR);
                }
                catch (IOException error){
                    error.printStackTrace();
                    sendNotify(Notification.TITLE_READ_CHAPTER_ERROR, error.getLocalizedMessage(), NotificationType.ERROR);
                }
                catch (Exception error){
                    error.printStackTrace();
                    sendNotify(Notification.TITLE_ERROR, error.getLocalizedMessage(), NotificationType.ERROR);
                }
            }
        });

        //清除
        clearButton.addActionListener(e -> {
            textReaderCore.clear();
        });

        //搜素输入
        searchTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            private SearchTask task = null;

            private void search(String searchText){
                if(StringUtils.isEmpty(searchText)){
                    if(titleList.getModel().getSize() != textReaderCore.getChapters().size()){
                        titleList.setListData(textReaderCore.getChapters());
                    }
                }else{
                    Vector<Chapter> tempChapterList = textReaderCore.getChapters().stream().filter(c -> c.getTitle().contains(searchText)).collect(Collectors.toCollection(Vector::new));
                    if(tempChapterList.size() != textReaderCore.getChapters().size()){
                        titleList.setListData(tempChapterList);
                    }
                }
            }

            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent){
                if(task != null) {
                    task.cancel();
                }
                String searchText = searchTextField.getText();
                task = new SearchTask(searchText);
                task.start();
            }

            class SearchTask extends Thread {
                private boolean cancelOrComplete = false;
                private final String searchText;

                public SearchTask(String searchText){
                    super();
                    this.searchText = searchText;
                }

                @Override
                public void run(){
                    try{
                        Thread.sleep(300);
                        if(!cancelOrComplete){
                            search(searchText);
                            cancelOrComplete = true;
                        }
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
                public void cancel(){
                    cancelOrComplete = true;
                }
            }
        });

        //点击章节
        titleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    Chapter chapter = titleList.getSelectedValue();
                    if(textReaderCore.changeChapter(chapter) >= 0){
                        tab.setSelectedIndex(1);
                    }
                }
            }
        });

        //下一章
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textReaderCore.nextChapter();
            }
        });

        //上一章
        previousButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textReaderCore.previousChapter();
            }
        });
    }

    private void eventRegister(){
        TextReaderEventManage eventManage = textReaderCore.getEventManage();
        TextReaderEventListener chapterChangeEventListener = event -> {
            Chapter chapter = (Chapter) event.getData();
            titleList.setListData(textReaderCore.getChapters());
            if(chapter != null){
                titleList.setSelectedValue(chapter, true);
            }
            if(textReaderCore.getChapters().isEmpty()){
                searchTextField.setEnabled(false);
            }else{
                searchTextField.setEnabled(true);
            }
            setTextContent();
        };
        TextReaderEventListener chaptersClearEventListener = event -> {
            titleList.setListData(textReaderCore.getChapters());
            titleList.clearSelection();
            textContent.setText("");
            textContent.setCaretPosition(0);
            searchTextField.setText("");
            searchTextField.setEnabled(false);
            ReaderLineUtil.clear(project, TextReaderConfig.isShowReaderLineAtStatusBar());
        };
        TextReaderEventListener configChangeEventListener = event -> {
            ConfigChangeEvent configChangeEvent = (ConfigChangeEvent) event;
            boolean notSelf = Objects.equals(ReaderWindow.this, event.getTag());
            switch (configChangeEvent.getConfigKey()){
                case FONT_FAMILY:
                    if(notSelf) fontFamilyEl.getModel().setSelectedItem(event.getData());
                    updateFontFamily();
                    break;
                case FONT_SIZE:
                    if(notSelf) fontSizeSpinner.setValue(event.getData());
                    updateFontSize();
                    break;
                case LINE_SPACE:
                    if(notSelf) lineSpaceSpinner.setValue(event.getData());
                    updateLineSpace();
                    break;
                case FIRST_LINE_INDENT:
                    if(notSelf) firstLineIndentSpinner.setValue(event.getData());
                    updateFirstLineIndent();
                    break;
                case PARAGRAPH_SPACE:
                    if(notSelf) paragraphSpaceSpinner.setValue(event.getData());
                    setTextContent();
                    break;
                case MAX_TITLE_LINE_SIZE:
                    if(notSelf) maxTitleLineSizeSpinner.setValue(event.getData());
                    break;
                case READER_LINE_SIZE:
                    if(notSelf) readerLineSizeSpinner.setValue(event.getData());
                    break;
                case SHOW_READER_LINT_AT_STATUS_BAR:
                    if(notSelf) showReaderLineAtRadioButtonGroup.setSelected((boolean)event.getData()?
                            atStatusBarRadioButton.getModel() : atHiddenNotifyRadioButton.getModel(), true);
                    break;
            }
        };
        eventManage.addListener(ChapterChangeEvent.EVENT_ID, chapterChangeEventListener);
        eventManage.addListener(ChaptersClearEvent.EVENT_ID, chaptersClearEventListener);
        eventManage.addListener(ConfigChangeEvent.EVENT_ID, configChangeEventListener);
        Disposable eventDisposable = () -> {
            eventManage.removeListener(ChapterChangeEvent.EVENT_ID, chapterChangeEventListener);
            eventManage.removeListener(ChaptersClearEvent.EVENT_ID, chaptersClearEventListener);
            eventManage.removeListener(ConfigChangeEvent.EVENT_ID, configChangeEventListener);
        };
        Disposer.register(this, eventDisposable);
    }

    private void init(){
        //字体风格初始化
        updateFontFamily();
        updateFontSize();
        updateLineSpace();
        updateFirstLineIndent();

        Chapter chapter = textReaderCore.getChapter();
        if(chapter != null){
            titleList.setListData(textReaderCore.getChapters());
            titleList.setSelectedValue(chapter, true);
            searchTextField.setEnabled(true);
            setTextContent();
        }
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

    private void setTextContent(){
        try{
            String text = textReaderCore.readChapterContent();
            textContent.setText(text);
            textContent.setCaretPosition(0);
        }
        catch (IOException e){
            e.printStackTrace();
            sendNotify(Notification.TITLE_READ_CHAPTER_ERROR, e.getLocalizedMessage(), NotificationType.ERROR);
        }
    }

    private void sendNotify(String title, String content, NotificationType type){
        Notification.sendNotify(project, title, content, type);
    }

    public JPanel getContent(){
        return readerPanel;
    }

    @Override
    public void dispose() {
        ReaderWindowHolder.remove(project);
    }
}
