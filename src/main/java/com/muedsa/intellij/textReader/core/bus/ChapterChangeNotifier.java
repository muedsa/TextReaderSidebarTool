package com.muedsa.intellij.textReader.core.bus;

import com.intellij.util.messages.Topic;
import com.muedsa.intellij.textReader.core.Chapter;

public interface ChapterChangeNotifier {
    Topic<ChapterChangeNotifier> TOPIC = Topic.create("TextReaderChapterChangeNotifier", ChapterChangeNotifier.class);
    void notifyChange(Chapter chapter);
}
