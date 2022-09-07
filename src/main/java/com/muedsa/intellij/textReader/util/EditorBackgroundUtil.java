package com.muedsa.intellij.textReader.util;

import com.intellij.openapi.editor.Editor;
import com.muedsa.intellij.textReader.composes.TextImageBorder;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.core.config.TextReaderConfig;
import com.muedsa.intellij.textReader.core.event.ConfigChangeEvent;

import java.awt.*;


public class EditorBackgroundUtil {

    public static Font FONT = new Font(TextReaderConfig.getFontFamily(), Font.PLAIN, TextReaderConfig.getFontSize());
    static {
        TextReaderCore.getInstance().getEventManage().addListener(ConfigChangeEvent.EVENT_ID, e -> {
            ConfigChangeEvent event = (ConfigChangeEvent) e;
            if(TextReaderConfig.ConfigKey.FONT_FAMILY.equals(event.getConfigKey())
                || TextReaderConfig.ConfigKey.FONT_SIZE.equals(event.getConfigKey())){
                FONT = new Font(TextReaderConfig.getFontFamily(), Font.PLAIN, TextReaderConfig.getFontSize());
            }
        });
    }

    public static void setTextBackground(Editor editor, String text){
        if(text == null){
            editor.getContentComponent().setBorder(null);
        }else{
            editor.getContentComponent().setBorder(new TextImageBorder(text, FONT, TextReaderConfig.getReaderLineColor(),
                    editor.getScrollingModel().getVisibleAreaOnScrollingFinished()));
        }
    }
}
