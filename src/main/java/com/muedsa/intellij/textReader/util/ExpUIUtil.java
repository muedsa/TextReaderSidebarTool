package com.muedsa.intellij.textReader.util;

import com.intellij.ide.plugins.PluginManager;

public class ExpUIUtil {

    public static final String PLUGIN_ID = "com.intellij.plugins.expui";

    public static boolean isIntelliJNewUI() {
        return PluginManager.getLoadedPlugins().stream()
                .anyMatch(plugin -> PLUGIN_ID.equals(plugin.getPluginId().getIdString())
                        && plugin.isEnabled());
    }
}
