package com.muedsa.intellij.textReader.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.util.messages.MessageBusConnection;
import com.muedsa.intellij.textReader.bus.ConfigChangeNotifier;
import com.muedsa.intellij.textReader.config.ConfigKey;
import com.muedsa.intellij.textReader.config.EditorBackgroundOffsetType;
import com.muedsa.intellij.textReader.config.ShowReaderLineType;
import com.muedsa.intellij.textReader.core.Chapter;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.core.bus.ChapterChangeNotifier;
import com.muedsa.intellij.textReader.core.bus.ChapterClearNotifier;
import com.muedsa.intellij.textReader.file.TextFileChooserDescriptor;
import com.muedsa.intellij.textReader.notify.Notification;
import com.muedsa.intellij.textReader.state.TextReaderConfigStateService;
import com.muedsa.intellij.textReader.util.ReaderLineUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ReaderWindow implements Disposable {
    private final Project project;
    private final TextReaderConfigStateService config;
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
    private JSpinner readerLineColorAlphaChannelSpinner;
    private JSpinner readerLineColorRedChannelSpinner;
    private JSpinner readerLineColorGreenChannelSpinner;
    private JSpinner readerLineColorBlueChannelSpinner;
    private JRadioButton atEditorBackgroundRadioButton;
    private JRadioButton atLeftTopRadioButton;
    private JRadioButton atRightBottomRadioButton;
    private JRadioButton atLeftBottomRadioButton;
    private JRadioButton atRightTopRadioButton;
    private ButtonGroup editorBackgroundOffsetTypeButtonGroup;
    private JSpinner offsetXSpinner;
    private JSpinner offsetYSpinner;
    private final TextReaderCore textReaderCore;

    private int id;

    public int getId(){
        return id;
    }

    public static int NUM = 0;
    private synchronized void initId(){
        NUM++;
        id = NUM;
    }

    public ReaderWindow(Project project) {
        initId();
        this.project = project;
        this.config = TextReaderConfigStateService.getInstance();
        this.textReaderCore = TextReaderCore.getInstance();
        createUIComponents();
        init();
        updateRegex();
        eventRegister();
        initConfigMessageBusSubscribe();
        if(textReaderCore.getChapters().isEmpty()){
            this.searchTextField.setEnabled(true);
        }
        ReaderWindowHolder.put(project, this);
    }

    private void createUIComponents(){
        //字体
        String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(fontFamilyNames);
        fontFamilyEl.setModel(comboBoxModel);
        fontFamilyEl.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
                config.changeConfig(ConfigKey.FONT_FAMILY, e.getItem(), ReaderWindow.this);
            }
        });
        updateFontFamilyEl(config.getFontFamily());

        //字体大小
        SpinnerModel fontSizeSpinnerModel = new SpinnerNumberModel(config.getFontSize(), 0, 100, 1);
        fontSizeSpinner.setModel(fontSizeSpinnerModel);
        fontSizeSpinner.addChangeListener(e -> config.changeConfig(ConfigKey.FONT_SIZE,
                fontSizeSpinner.getValue(), ReaderWindow.this));

        //字体行间距
        SpinnerModel lineSpaceSpinnerModel = new SpinnerNumberModel(config.getLineSpace(), 0, 2.5, 0.1);
        lineSpaceSpinner.setModel(lineSpaceSpinnerModel);
        lineSpaceSpinner.addChangeListener(e -> config.changeConfig(ConfigKey.LINE_SPACE,
                lineSpaceSpinner.getValue(), ReaderWindow.this));

        //首行缩进
        SpinnerModel firstLineIndentSpinnerModel = new SpinnerNumberModel(config.getFirstLineIndent(), 0, 4, 1);
        firstLineIndentSpinner.setModel(firstLineIndentSpinnerModel);
        firstLineIndentSpinner.addChangeListener(e -> config.changeConfig(ConfigKey.FIRST_LINE_INDENT,
                firstLineIndentSpinner.getValue(), ReaderWindow.this));

        //段落间隔
        SpinnerModel paragraphSpaceSpinnerModel = new SpinnerNumberModel(config.getParagraphSpace(), 0, 4, 1);
        paragraphSpaceSpinner.setModel(paragraphSpaceSpinnerModel);
        paragraphSpaceSpinner.addChangeListener(e -> config.changeConfig(ConfigKey.PARAGRAPH_SPACE,
                paragraphSpaceSpinner.getValue(), ReaderWindow.this));

        //标题解析最大字数限制设置
        SpinnerModel maxLineSizeSpinnerModel = new SpinnerNumberModel(config.getMaxTitleLineSize(), 1, 200, 1);
        maxTitleLineSizeSpinner.setModel(maxLineSizeSpinnerModel);
        maxTitleLineSizeSpinner.addChangeListener(e -> config.changeConfig(ConfigKey.MAX_TITLE_LINE_SIZE,
                maxTitleLineSizeSpinner.getValue(), ReaderWindow.this));

        //每行字数
        SpinnerModel lineSizeSpinnerModel = new SpinnerNumberModel(config.getReaderLineSize(), 0, 100, 1);
        readerLineSizeSpinner.setModel(lineSizeSpinnerModel);
        lineSizeSpinnerModel.addChangeListener(e -> config.changeConfig(ConfigKey.READER_LINE_SIZE,
                readerLineSizeSpinner.getValue(), ReaderWindow.this));

        //按行读取时展示的位置
        showReaderLineAtRadioButtonGroup = new ButtonGroup();
        showReaderLineAtRadioButtonGroup.add(atHiddenNotifyRadioButton);
        showReaderLineAtRadioButtonGroup.add(atStatusBarRadioButton);
        showReaderLineAtRadioButtonGroup.add(atEditorBackgroundRadioButton);
        updateShowReaderLineTypeButtonGroup(config.getShowReaderLineType());
        ActionListener showReaderLineAtRadioButtonActionListener = e -> {
            if(showReaderLineAtRadioButtonGroup.getSelection().equals(atHiddenNotifyRadioButton.getModel())){
                config.changeConfig(ConfigKey.SHOW_READER_LINE_TYPE,
                        ShowReaderLineType.NOTIFY, ReaderWindow.this);
            }else if(showReaderLineAtRadioButtonGroup.getSelection().equals(atStatusBarRadioButton.getModel())){
                config.changeConfig(ConfigKey.SHOW_READER_LINE_TYPE,
                        ShowReaderLineType.STATUS_BAR, ReaderWindow.this);
            }else if(showReaderLineAtRadioButtonGroup.getSelection().equals(atEditorBackgroundRadioButton.getModel())){
                config.changeConfig(ConfigKey.SHOW_READER_LINE_TYPE,
                        ShowReaderLineType.EDITOR_BACKGROUND, ReaderWindow.this);
            }
        };
        atHiddenNotifyRadioButton.addActionListener(showReaderLineAtRadioButtonActionListener);
        atStatusBarRadioButton.addActionListener(showReaderLineAtRadioButtonActionListener);
        atEditorBackgroundRadioButton.addActionListener(showReaderLineAtRadioButtonActionListener);

        //文本颜色
        SpinnerModel readerLineColorRedChannelSpinnerModel = new SpinnerNumberModel(config.getReaderLineColor().getRed(), 0, 255, 1);
        SpinnerModel readerLineColorGreenChannelSpinnerModel = new SpinnerNumberModel(config.getReaderLineColor().getGreen(), 0, 255, 1);
        SpinnerModel readerLineColorBlueChannelSpinnerModel = new SpinnerNumberModel(config.getReaderLineColor().getGreen(), 0, 255, 1);
        SpinnerModel readerLineColorAlphaChannelSpinnerModel = new SpinnerNumberModel(config.getReaderLineColor().getAlpha(), 0, 255, 1);
        readerLineColorRedChannelSpinner.setModel(readerLineColorRedChannelSpinnerModel);
        readerLineColorGreenChannelSpinner.setModel(readerLineColorGreenChannelSpinnerModel);
        readerLineColorBlueChannelSpinner.setModel(readerLineColorBlueChannelSpinnerModel);
        readerLineColorAlphaChannelSpinner.setModel(readerLineColorAlphaChannelSpinnerModel);
        ChangeListener readLineColorChangeListener = e -> {
            int a = (int) readerLineColorAlphaChannelSpinner.getValue();
            int r = (int) readerLineColorRedChannelSpinner.getValue();
            int g = (int) readerLineColorGreenChannelSpinner.getValue();
            int b = (int) readerLineColorBlueChannelSpinner.getValue();
            int color = (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
            JBColor newColor = new JBColor(color, color);
            config.changeConfig(ConfigKey.READER_LINE_COLOR, newColor, ReaderWindow.this);
        };
        readerLineColorRedChannelSpinner.addChangeListener(readLineColorChangeListener);
        readerLineColorGreenChannelSpinner.addChangeListener(readLineColorChangeListener);
        readerLineColorBlueChannelSpinner.addChangeListener(readLineColorChangeListener);
        readerLineColorAlphaChannelSpinner.addChangeListener(readLineColorChangeListener);

        //编辑器背景偏移类型
        editorBackgroundOffsetTypeButtonGroup = new ButtonGroup();
        editorBackgroundOffsetTypeButtonGroup.add(atLeftTopRadioButton);
        editorBackgroundOffsetTypeButtonGroup.add(atLeftBottomRadioButton);
        editorBackgroundOffsetTypeButtonGroup.add(atRightTopRadioButton);
        editorBackgroundOffsetTypeButtonGroup.add(atRightBottomRadioButton);
        updateEditorBackgroundOffsetTypeButtonGroup(config.getEditorBackgroundOffsetType());
        ActionListener editorBackgroundOffsetTypeRadioButtonActionListener = e -> {
            if(editorBackgroundOffsetTypeButtonGroup.getSelection().equals(atLeftTopRadioButton.getModel())){
                config.changeConfig(ConfigKey.EDITOR_BACKGROUND_OFFSET_TYPE,
                        EditorBackgroundOffsetType.LEFT_TOP, ReaderWindow.this);
            }else if(editorBackgroundOffsetTypeButtonGroup.getSelection().equals(atLeftBottomRadioButton.getModel())){
                config.changeConfig(ConfigKey.EDITOR_BACKGROUND_OFFSET_TYPE,
                        EditorBackgroundOffsetType.LEFT_BOTTOM, ReaderWindow.this);
            }else if(editorBackgroundOffsetTypeButtonGroup.getSelection().equals(atRightTopRadioButton.getModel())){
                config.changeConfig(ConfigKey.EDITOR_BACKGROUND_OFFSET_TYPE,
                        EditorBackgroundOffsetType.RIGHT_TOP, ReaderWindow.this);
            } if(editorBackgroundOffsetTypeButtonGroup.getSelection().equals(atRightBottomRadioButton.getModel())){
                config.changeConfig(ConfigKey.EDITOR_BACKGROUND_OFFSET_TYPE,
                        EditorBackgroundOffsetType.RIGHT_BOTTOM, ReaderWindow.this);
            }
        };
        atLeftTopRadioButton.addActionListener(editorBackgroundOffsetTypeRadioButtonActionListener);
        atLeftBottomRadioButton.addActionListener(editorBackgroundOffsetTypeRadioButtonActionListener);
        atRightTopRadioButton.addActionListener(editorBackgroundOffsetTypeRadioButtonActionListener);
        atRightBottomRadioButton.addActionListener(editorBackgroundOffsetTypeRadioButtonActionListener);

        //编辑器背景偏移量X
        SpinnerModel editBackgroundOffsetXSpinnerModel = new SpinnerNumberModel(config.getEditBackgroundOffsetX(), 0, 10000, 1);
        offsetXSpinner.setModel(editBackgroundOffsetXSpinnerModel);
        editBackgroundOffsetXSpinnerModel.addChangeListener(e -> config.changeConfig(ConfigKey.EDITOR_BACKGROUND_OFFSET_X,
                offsetXSpinner.getValue(), ReaderWindow.this));

        //编辑器背景偏移量Y
        SpinnerModel editBackgroundOffsetYSpinnerModel = new SpinnerNumberModel(config.getEditBackgroundOffsetY(), 0, 10000, 1);
        offsetYSpinner.setModel(editBackgroundOffsetYSpinnerModel);
        editBackgroundOffsetYSpinnerModel.addChangeListener(e -> config.changeConfig(ConfigKey.EDITOR_BACKGROUND_OFFSET_Y,
                offsetYSpinner.getValue(), ReaderWindow.this));

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
        clearButton.addActionListener(e -> textReaderCore.clear());

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
        MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus()
                .connect(this);
        connect.subscribe(ChapterChangeNotifier.TOPIC, chapter -> {
            titleList.setListData(textReaderCore.getChapters());
            if(chapter != null){
                titleList.setSelectedValue(chapter, true);
            }
            searchTextField.setEnabled(!textReaderCore.getChapters().isEmpty());
            setTextContent();
        });
        connect.subscribe(ChapterClearNotifier.TOPIC, () -> {
            titleList.setListData(textReaderCore.getChapters());
            titleList.clearSelection();
            textContent.setText("");
            textContent.setCaretPosition(0);
            searchTextField.setText("");
            searchTextField.setEnabled(false);
            ReaderLineUtil.clear(config.getShowReaderLineType(), config, project);
        });
    }

    private void initConfigMessageBusSubscribe(){
        ApplicationManager.getApplication().getMessageBus()
                .connect(this)
                .subscribe(ConfigChangeNotifier.TOPIC, ((key, data, source) -> {
                    boolean notSelf = !Objects.equals(ReaderWindow.this, source);
                    switch (key){
                        case FONT_FAMILY:
                            if(notSelf) updateFontFamilyEl((String) data);
                            updateEditorFontFamily();
                            break;
                        case FONT_SIZE:
                            if(notSelf) fontSizeSpinner.setValue(data);
                            updateEditorFontSize();
                            break;
                        case LINE_SPACE:
                            if(notSelf) lineSpaceSpinner.setValue(data);
                            updateEditorLineSpace();
                            break;
                        case FIRST_LINE_INDENT:
                            if(notSelf) firstLineIndentSpinner.setValue(data);
                            updateEditorFirstLineIndent();
                            break;
                        case PARAGRAPH_SPACE:
                            if(notSelf) paragraphSpaceSpinner.setValue(data);
                            setTextContent();
                            break;
                        case MAX_TITLE_LINE_SIZE:
                            if(notSelf) maxTitleLineSizeSpinner.setValue(data);
                            break;
                        case READER_LINE_SIZE:
                            if(notSelf) readerLineSizeSpinner.setValue(data);
                            break;
                        case SHOW_READER_LINE_TYPE:
                            if(notSelf) updateShowReaderLineTypeButtonGroup((ShowReaderLineType) data);
                            break;
                        case READER_LINE_COLOR:
                            if(notSelf) {
                                JBColor color = (JBColor) data;
                                readerLineColorRedChannelSpinner.setValue(color.getRed());
                                readerLineColorGreenChannelSpinner.setValue(color.getGreen());
                                readerLineColorBlueChannelSpinner.setValue(color.getBlue());
                                readerLineColorAlphaChannelSpinner.setValue(color.getAlpha());
                            }
                            break;
                        case EDITOR_BACKGROUND_OFFSET_TYPE:
                            if(notSelf) updateEditorBackgroundOffsetTypeButtonGroup((EditorBackgroundOffsetType) data);
                            break;
                        case EDITOR_BACKGROUND_OFFSET_X:
                            if(notSelf) offsetXSpinner.setValue(data);
                            break;
                        case EDITOR_BACKGROUND_OFFSET_Y:
                            if(notSelf) offsetYSpinner.setValue(data);
                            break;
                    }
                }));
    }

    private void init(){
        //字体风格初始化
        updateEditorFontFamily();
        updateEditorFontSize();
        updateEditorLineSpace();
        updateEditorFirstLineIndent();

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

    private void updateFontFamilyEl(String newFontFamily) {
        ComboBoxModel<String> model = fontFamilyEl.getModel();
        int size = model.getSize();
        int newSelectedIndex = -1;
        for (int i = 0; i < size; i++) {
            String fontFamily = model.getElementAt(i);
            if(fontFamily.equals(newFontFamily)){
                newSelectedIndex = i;
                break;
            }
        }
        if(newSelectedIndex > -1 && fontFamilyEl.getSelectedIndex() != newSelectedIndex) {
            fontFamilyEl.setSelectedIndex(newSelectedIndex);
        }
    }

    private void updateEditorFontFamily(){
        String fontFamily = (String)fontFamilyEl.getSelectedItem();
        StyledDocument styledDocument = textContent.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attributes, fontFamily);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), attributes, false);
    }

    private void updateEditorFontSize(){
        int fontSize = (int)fontSizeSpinner.getValue();
        StyledDocument styledDocument = textContent.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFontSize(attributes, fontSize);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), attributes, false);
    }

    private void updateEditorLineSpace(){
        float lineSpace = ((Double)lineSpaceSpinner.getValue()).floatValue();
        StyledDocument styledDocument = textContent.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(attributes, lineSpace);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), attributes, false);
    }

    private void updateEditorFirstLineIndent(){
        float firstLineIndent = ((Integer)firstLineIndentSpinner.getValue()) * ((Integer)fontSizeSpinner.getValue()).floatValue();
        StyledDocument styledDocument = textContent.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFirstLineIndent(attributes, firstLineIndent);
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), attributes, false);
    }

    private void setTextContent(){
        try{
            String text = textReaderCore.readChapterContent(config.getParagraphSpace());
            textContent.setText(text);
            textContent.setCaretPosition(0);
        }
        catch (IOException e){
            e.printStackTrace();
            sendNotify(Notification.TITLE_READ_CHAPTER_ERROR, e.getLocalizedMessage(), NotificationType.ERROR);
        }
    }

    private void updateShowReaderLineTypeButtonGroup(ShowReaderLineType showReaderLineType){
        switch (showReaderLineType){
            case NOTIFY:
                showReaderLineAtRadioButtonGroup.setSelected(atHiddenNotifyRadioButton.getModel(), true);
                break;
            case STATUS_BAR:
                showReaderLineAtRadioButtonGroup.setSelected(atStatusBarRadioButton.getModel(), true);
                break;
            case EDITOR_BACKGROUND:
                showReaderLineAtRadioButtonGroup.setSelected(atEditorBackgroundRadioButton.getModel(), true);
                break;
        }
    }
    private void updateEditorBackgroundOffsetTypeButtonGroup(EditorBackgroundOffsetType editorBackgroundOffsetType){
        switch (editorBackgroundOffsetType){
            case LEFT_TOP:
                editorBackgroundOffsetTypeButtonGroup.setSelected(atLeftTopRadioButton.getModel(), true);
                break;
            case LEFT_BOTTOM:
                editorBackgroundOffsetTypeButtonGroup.setSelected(atLeftBottomRadioButton.getModel(), true);
                break;
            case RIGHT_TOP:
                editorBackgroundOffsetTypeButtonGroup.setSelected(atRightTopRadioButton.getModel(), true);
                break;
            case RIGHT_BOTTOM:
                editorBackgroundOffsetTypeButtonGroup.setSelected(atRightBottomRadioButton.getModel(), true);
                break;
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
