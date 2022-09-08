package com.muedsa.intellij.textReader.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.muedsa.intellij.textReader.config.TextReaderConfig;
import org.jetbrains.annotations.NotNull;

@State(name = "com.muedsa.intellij.textReader.CoreConfig", storages = {@Storage(value = StoragePathMacros.NON_ROAMABLE_FILE)})
public class TextReaderConfigStateService extends TextReaderConfig implements PersistentStateComponent<TextReaderConfigStateService> {

    public static TextReaderConfigStateService getInstance() {
        return ApplicationManager.getApplication().getService(TextReaderConfigStateService.class);
    }

    @Override
    public TextReaderConfigStateService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TextReaderConfigStateService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
