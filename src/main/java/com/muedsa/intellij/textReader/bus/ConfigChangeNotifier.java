package com.muedsa.intellij.textReader.bus;

import com.intellij.util.messages.Topic;
import com.muedsa.intellij.textReader.config.ConfigKey;

public interface ConfigChangeNotifier {

    Topic<ConfigChangeNotifier> CHANGE_ACTION_TOPIC = Topic.create("TextReaderConfigChangeNotifier", ConfigChangeNotifier.class);

    void configChanged(ConfigKey key, Object data, Object source);
}
