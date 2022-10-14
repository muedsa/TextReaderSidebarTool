package com.muedsa.intellij.textReader.util;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.editor.Editor;
import com.muedsa.intellij.textReader.ui.BoundedMultiLineTextBox;
import com.muedsa.intellij.textReader.ui.PaintMultiLineTextEditorBorder;

public class UiUtil {

    public static final String PLUGIN_ID = "com.intellij.plugins.expui";

    public static boolean isIntelliJNewUI() {
        return PluginManager.getLoadedPlugins().stream()
                .anyMatch(plugin -> PLUGIN_ID.equals(plugin.getPluginId().getIdString())
                        && plugin.isEnabled());
    }

    public static void initPaintMultiLineTextEditorBorder(Editor editor, BoundedMultiLineTextBox box) {
        editor.getContentComponent().setBorder(new PaintMultiLineTextEditorBorder(editor, box));
    }

    public static void removeBorder(Editor editor) {
        editor.getContentComponent().setBorder(null);
    }
}
