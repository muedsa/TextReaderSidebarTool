package com.muedsa.intellij.textReader.core.event;

import com.muedsa.intellij.textReader.core.config.TextReaderConfig;

public class ConfigChangeEvent extends TextReaderEvent {

    public static final String EVENT_ID = "ConfigChangeEvent";

    private final TextReaderConfig.ConfigKey configKey;

    public ConfigChangeEvent(TextReaderConfig.ConfigKey configKey, Object data) {
        super(data);
        this.configKey = configKey;
    }

    public TextReaderConfig.ConfigKey getConfigKey() {
        return configKey;
    }

    @Override
    public String getEventId() {
        return EVENT_ID;
    }
}
