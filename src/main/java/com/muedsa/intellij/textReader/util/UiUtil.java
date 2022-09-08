package com.muedsa.intellij.textReader.util;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.editor.Editor;
import com.muedsa.intellij.textReader.composes.TextImageBorder;
import com.muedsa.intellij.textReader.config.TextReaderConfig;

import java.awt.*;

public class UiUtil {

    public static final String PLUGIN_ID = "com.intellij.plugins.expui";

    public static boolean isIntelliJNewUI() {
        return PluginManager.getLoadedPlugins().stream()
                .anyMatch(plugin -> PLUGIN_ID.equals(plugin.getPluginId().getIdString())
                        && plugin.isEnabled());
    }

    public static void setEditorTextBackground(Editor editor, String text, TextReaderConfig config){
        if(text == null){
            editor.getContentComponent().setBorder(null);
        }else{
            Font font = new Font(config.getFontFamily(), Font.PLAIN, config.getFontSize());
            TextImageBorder border = new TextImageBorder(text, font, config.getReaderLineColor(),
                    config.getEditorBackgroundOffsetType(), config.getEditBackgroundOffsetX(),
                    config.getEditBackgroundOffsetY(), editor.getScrollingModel().getVisibleAreaOnScrollingFinished());
            editor.getContentComponent().setBorder(border);
        }
    }
}
