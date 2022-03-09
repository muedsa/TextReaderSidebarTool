package com.muedsa.intellij.textReader.core.config;

import com.muedsa.intellij.textReader.core.event.ConfigChangeEvent;
import com.muedsa.intellij.textReader.core.event.TextReaderEventManage;

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

    public static void setConfigValue(ConfigKey configKey, Object configValue, TextReaderEventManage eventManage){
        switch (configKey){
            case FONT_FAMILY:
                fontFamily = (String) configValue;
                eventManage.notifyEvent(new ConfigChangeEvent(configKey, configValue));
                break;
            case FONT_SIZE:
                fontSize = (int) configValue;
                eventManage.notifyEvent(new ConfigChangeEvent(configKey, configValue));
                break;
            case LINE_SPACE:
                lineSpace = (double) configValue;
                eventManage.notifyEvent(new ConfigChangeEvent(configKey, configValue));
                break;
            case FIRST_LINE_INDENT:
                firstLineIndent = (int) configValue;
                eventManage.notifyEvent(new ConfigChangeEvent(configKey, configValue));
                break;
            case PARAGRAPH_SPACE:
                paragraphSpace = (int) configValue;
                eventManage.notifyEvent(new ConfigChangeEvent(configKey, configValue));
                break;
            case MAX_TITLE_LINE_SIZE:
                maxTitleLineSize = (int) configValue;
                eventManage.notifyEvent(new ConfigChangeEvent(configKey, configValue));
                break;
            case READER_LINE_SIZE:
                readerLineSize = (int) configValue;
                eventManage.notifyEvent(new ConfigChangeEvent(configKey, configValue));
                break;
        }
    }

    public enum ConfigKey {
        FONT_FAMILY,
        FONT_SIZE,
        LINE_SPACE,
        FIRST_LINE_INDENT,
        PARAGRAPH_SPACE,
        MAX_TITLE_LINE_SIZE,
        READER_LINE_SIZE
    }
}
