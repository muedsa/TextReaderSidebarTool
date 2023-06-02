package com.muedsa.intellij.textReader.util;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.editor.Editor;
import com.muedsa.intellij.textReader.config.TextReaderConfig;
import com.muedsa.intellij.textReader.ui.MultiLineTextImageBorder;
import com.muedsa.intellij.textReader.ui.TextLayoutProperty;
import com.muedsa.intellij.textReader.ui.TextOffset;

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
            resetEditorBackground(editor);
        }else{
            Font font = new Font(config.getFontFamily(), Font.PLAIN, config.getFontSize());
            TextLayoutProperty textLayoutProperty = new TextLayoutProperty(font, config.getReaderLineColor(),
                    (float) config.getLineSpace(), (float) config.getParagraphSpace(), 0);
            TextOffset textOffset = new TextOffset(config.getEditorBackgroundOffsetType(), config.getEditBackgroundOffsetX(),
                    config.getEditBackgroundOffsetY(), editor.getScrollingModel().getVisibleAreaOnScrollingFinished());
            MultiLineTextImageBorder border = new MultiLineTextImageBorder(text, textLayoutProperty, textOffset);
            editor.getContentComponent().setBorder(border);
        }
    }

    public static void resetEditorBackground(Editor editor){
        editor.getContentComponent().setBorder(null);
    }
}
