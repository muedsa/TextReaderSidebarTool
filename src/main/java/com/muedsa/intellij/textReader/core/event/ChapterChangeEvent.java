package com.muedsa.intellij.textReader.core.event;

public class ChapterChangeEvent extends TextReaderEvent{

    public final static String EVENT_ID = ChapterChangeEvent.class.getSimpleName();

    public ChapterChangeEvent(Object data) {
        super(data);
    }

    @Override
    public String getEventId() {
        return EVENT_ID;
    }
}
