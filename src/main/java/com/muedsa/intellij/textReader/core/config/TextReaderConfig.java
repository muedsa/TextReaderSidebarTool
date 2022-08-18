package com.muedsa.intellij.textReader.core.config;

import com.intellij.util.ui.UIUtil;
import com.muedsa.intellij.textReader.core.event.ConfigChangeEvent;
import com.muedsa.intellij.textReader.core.event.TextReaderEventManage;
import com.muedsa.intellij.textReader.util.ExpUIUtil;

import java.awt.*;

public class TextReaderConfig {
    //字体
    private static String fontFamily = "Microsoft YaHei UI";

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

    //使用状态栏部件展示按行读取的内容
    private static boolean showReaderLineAtStatusBar = ExpUIUtil.isIntelliJNewUI();

    //状态栏部件文本颜色
    private static Color readerLineColor = UIUtil.getPanelBackground().brighter();

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

    public static boolean isShowReaderLineAtStatusBar() {
        return showReaderLineAtStatusBar;
    }

    public static Color getReaderLineColor() {
        return readerLineColor;
    }

    public static void setConfigValue(ConfigKey configKey, Object configValue, TextReaderEventManage eventManage, Object tag) {
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
            case SHOW_READER_LINT_AT_STATUS_BAR:
                showReaderLineAtStatusBar = (boolean) configValue;
                break;
        }
        ConfigChangeEvent event = new ConfigChangeEvent(configKey, configValue);
        event.setTag(tag);
        eventManage.notifyEvent(event);
    }

    public enum ConfigKey {
        FONT_FAMILY,
        FONT_SIZE,
        LINE_SPACE,
        FIRST_LINE_INDENT,
        PARAGRAPH_SPACE,
        MAX_TITLE_LINE_SIZE,
        READER_LINE_SIZE,
        SHOW_READER_LINT_AT_STATUS_BAR
    }
}
