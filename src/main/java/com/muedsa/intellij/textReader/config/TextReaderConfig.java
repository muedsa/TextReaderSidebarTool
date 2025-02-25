package com.muedsa.intellij.textReader.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Transient;
import com.muedsa.intellij.textReader.bus.ConfigChangeNotifier;
import com.muedsa.intellij.textReader.state.JBColorConverter;
import com.muedsa.intellij.textReader.util.UiUtil;

public abstract class TextReaderConfig {

    //字体
    private String fontFamily = "SansSerif";

    //字体大小
    private int fontSize = 12;

    //行间距
    private double lineSpace = 0.5;

    //首行缩进
    private int firstLineIndent = 2;

    //段落间隔
    private int paragraphSpace = 1;

    //标题解析最大字数限制设置 如果某行超过这个限制就跳过标题判断
    private int maxTitleLineSize = 30;

    //按行读取时的行大小
    private int readerLineSize = 30;

    //按行读取的内容展示位置
    private ShowReaderLineType showReaderLineType = UiUtil.isIntelliJNewUI() ? ShowReaderLineType.STATUS_BAR :
            ShowReaderLineType.NOTIFY;


    //状态栏部件文本颜色
    @OptionTag(converter = JBColorConverter.class)
    private JBColor readerLineColor = new JBColor(UIUtil.getPanelBackground().brighter(), UIUtil.getPanelBackground().brighter());

    //编辑器背景偏移类型
    private EditorBackgroundOffsetType editorBackgroundOffsetType = EditorBackgroundOffsetType.LEFT_TOP;

    //编辑器背景水平偏移量
    private int editBackgroundOffsetX = 100;

    //编辑器背景竖直偏移量
    private int editBackgroundOffsetY = 400;

    private boolean enableNextLineActionByScrollRadioButton = false;

    @Transient
    private ConfigChangeNotifier notifier;

    protected TextReaderConfig() {
        notifier = ApplicationManager.getApplication().getMessageBus().syncPublisher(ConfigChangeNotifier.TOPIC);
    }

    public void changeConfig(ConfigKey key, Object value, Object source) {
        switch (key){
            case FONT_FAMILY:
                fontFamily = (String) value;
                break;
            case FONT_SIZE:
                fontSize = (int) value;
                break;
            case LINE_SPACE:
                lineSpace = (double) value;
                break;
            case FIRST_LINE_INDENT:
                firstLineIndent = (int) value;
                break;
            case PARAGRAPH_SPACE:
                paragraphSpace = (int) value;
                break;
            case MAX_TITLE_LINE_SIZE:
                maxTitleLineSize = (int) value;
                break;
            case READER_LINE_SIZE:
                readerLineSize = (int) value;
                break;
            case SHOW_READER_LINE_TYPE:
                showReaderLineType = (ShowReaderLineType) value;
                break;
            case READER_LINE_COLOR:
                readerLineColor = (JBColor) value;
                break;
            case EDITOR_BACKGROUND_OFFSET_TYPE:
                editorBackgroundOffsetType = (EditorBackgroundOffsetType) value;
                break;
            case EDITOR_BACKGROUND_OFFSET_X:
                editBackgroundOffsetX = (int) value;
                break;
            case EDITOR_BACKGROUND_OFFSET_Y:
                editBackgroundOffsetY = (int) value;
                break;
            case ENABLE_NEXT_LINE_ACTION_BY_SCROLL:
                enableNextLineActionByScrollRadioButton = (boolean) value;
        }
        notifier.configChanged(key, value, source);
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public double getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(double lineSpace) {
        this.lineSpace = lineSpace;
    }

    public int getFirstLineIndent() {
        return firstLineIndent;
    }

    public void setFirstLineIndent(int firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public int getParagraphSpace() {
        return paragraphSpace;
    }

    public void setParagraphSpace(int paragraphSpace) {
        this.paragraphSpace = paragraphSpace;
    }

    public int getMaxTitleLineSize() {
        return maxTitleLineSize;
    }

    public void setMaxTitleLineSize(int maxTitleLineSize) {
        this.maxTitleLineSize = maxTitleLineSize;
    }

    public int getReaderLineSize() {
        return readerLineSize;
    }

    public void setReaderLineSize(int readerLineSize) {
        this.readerLineSize = readerLineSize;
    }

    public ShowReaderLineType getShowReaderLineType() {
        return showReaderLineType;
    }

    public void setShowReaderLineType(ShowReaderLineType showReaderLineType) {
        this.showReaderLineType = showReaderLineType;
    }

    public JBColor getReaderLineColor() {
        return readerLineColor;
    }

    public void setReaderLineColor(JBColor readerLineColor) {
        this.readerLineColor = readerLineColor;
    }

    public EditorBackgroundOffsetType getEditorBackgroundOffsetType() {
        return editorBackgroundOffsetType;
    }

    public void setEditorBackgroundOffsetType(EditorBackgroundOffsetType editorBackgroundOffsetType) {
        this.editorBackgroundOffsetType = editorBackgroundOffsetType;
    }

    public int getEditBackgroundOffsetX() {
        return editBackgroundOffsetX;
    }

    public void setEditBackgroundOffsetX(int editBackgroundOffsetX) {
        this.editBackgroundOffsetX = editBackgroundOffsetX;
    }

    public int getEditBackgroundOffsetY() {
        return editBackgroundOffsetY;
    }

    public void setEditBackgroundOffsetY(int editBackgroundOffsetY) {
        this.editBackgroundOffsetY = editBackgroundOffsetY;
    }

    public boolean isEnableNextLineActionByScrollRadioButton() {
        return enableNextLineActionByScrollRadioButton;
    }

    public void setEnableNextLineActionByScrollRadioButton(boolean enableNextLineActionByScrollRadioButton) {
        this.enableNextLineActionByScrollRadioButton = enableNextLineActionByScrollRadioButton;
    }
}
