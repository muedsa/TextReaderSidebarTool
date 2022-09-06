package com.muedsa.intellij.textReader.core.config;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.core.event.ConfigChangeEvent;
import com.muedsa.intellij.textReader.util.ExpUIUtil;

import java.awt.*;

public class TextReaderConfig {
    //字体
    private static String fontFamily = "SansSerif";

    //字体大小
    private static int fontSize = 12;

    //行间距
    private static double lineSpace = 0.5;

    //首行缩进
    private static int firstLineIndent = 2;

    //段落间隔
    private static int paragraphSpace = 1;

    //标题解析最大字数限制设置 如果某行超过这个限制就跳过标题判断
    private static int maxTitleLineSize = 30;

    //按行读取时的行大小
    private static int readerLineSize = 30;

    //按行读取的内容展示位置
    private static ShowReaderLineType showReaderLineType = ExpUIUtil.isIntelliJNewUI() ? ShowReaderLineType.STATUS_BAR :
            ShowReaderLineType.NOTIFY;

    //状态栏部件文本颜色
    private static JBColor readerLineColor = new JBColor(UIUtil.getPanelBackground().brighter(), UIUtil.getPanelBackground().brighter());

    //编辑器背景偏移类型
    private static EditBackgroundOffsetType editBackgroundOffsetType = EditBackgroundOffsetType.LEFT_TOP;

    //编辑器背景水平偏移量
    private static int editBackgroundOffsetX = 100;

    //编辑器背景竖直偏移量
    private static int editBackgroundOffsetY = 400;

//    //章节标题前缀
//    public static String chapterTitlePrefix = "第";
//
//    //章节标题前缀
//    public static String chapterTitleSuffix = "章";
//
//    //固定标题
//    public static String fixChapterTitle = "前言|引子";

    public static String getFontFamily() {
        return fontFamily;
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static double getLineSpace() {
        return lineSpace;
    }

    public static int getFirstLineIndent() {
        return firstLineIndent;
    }

    public static int getParagraphSpace() {
        return paragraphSpace;
    }

    public static int getMaxTitleLineSize() {
        return maxTitleLineSize;
    }

    public static int getReaderLineSize() {
        return readerLineSize;
    }

    public static ShowReaderLineType getShowReaderLineType() {
        return showReaderLineType;
    }

    public static Color getReaderLineColor() {
        return readerLineColor;
    }

    public static EditBackgroundOffsetType getEditBackgroundOffsetType() {
        return editBackgroundOffsetType;
    }

    public static int getEditBackgroundOffsetX() {
        return editBackgroundOffsetX;
    }

    public static int getEditBackgroundOffsetY() {
        return editBackgroundOffsetY;
    }

    public static void setConfigValue(ConfigKey configKey, Object configValue, Object tag) {
        switch (configKey){
            case FONT_FAMILY:
                fontFamily = (String) configValue;
                break;
            case FONT_SIZE:
                fontSize = (int) configValue;
                break;
            case LINE_SPACE:
                lineSpace = (double) configValue;
                break;
            case FIRST_LINE_INDENT:
                firstLineIndent = (int) configValue;
                break;
            case PARAGRAPH_SPACE:
                paragraphSpace = (int) configValue;
                break;
            case MAX_TITLE_LINE_SIZE:
                maxTitleLineSize = (int) configValue;
                break;
            case READER_LINE_SIZE:
                readerLineSize = (int) configValue;
                break;
            case SHOW_READER_LINE_TYPE:
                showReaderLineType = (ShowReaderLineType) configValue;
                break;
            case READER_LINE_COLOR:
                readerLineColor = (JBColor) configValue;
                break;
            case EDITOR_BACKGROUND_OFFSET_TYPE:
                editBackgroundOffsetType = (EditBackgroundOffsetType) configValue;
                break;
            case EDITOR_BACKGROUND_OFFSET_X:
                editBackgroundOffsetX = (int) configValue;
                break;
            case EDITOR_BACKGROUND_OFFSET_Y:
                editBackgroundOffsetY = (int) configValue;
                break;
        }
        ConfigChangeEvent event = new ConfigChangeEvent(configKey, configValue);
        event.setTag(tag);
        TextReaderCore.getInstance().getEventManage().notifyEvent(event);
    }

    public enum ConfigKey {
        FONT_FAMILY,
        FONT_SIZE,
        LINE_SPACE,
        FIRST_LINE_INDENT,
        PARAGRAPH_SPACE,
        MAX_TITLE_LINE_SIZE,
        READER_LINE_SIZE,
        SHOW_READER_LINE_TYPE,
        READER_LINE_COLOR,
        EDITOR_BACKGROUND_OFFSET_TYPE,
        EDITOR_BACKGROUND_OFFSET_X,
        EDITOR_BACKGROUND_OFFSET_Y
    }

    public enum ShowReaderLineType {
        NOTIFY,
        STATUS_BAR,
        EDITOR_BACKGROUND
    }

    public enum EditBackgroundOffsetType {
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_BOTTOM
    }
}
