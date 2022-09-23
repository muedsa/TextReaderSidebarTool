package com.muedsa.intellij.textReader.core.bus;

import com.intellij.util.messages.Topic;

public interface ChapterClearNotifier {
    Topic<ChapterClearNotifier> TOPIC = Topic.create("TextReaderChapterClearNotifier", ChapterClearNotifier.class);
    void notifyClear();
}
