package com.muedsa.intellij.textReader.core.event;

import javax.annotation.Nullable;

public class ChaptersClearEvent extends TextReaderEvent{

    public final static String EVENT_ID = "ClearChaptersEvent";

    public ChaptersClearEvent(@Nullable Object data) {
        super(data);
    }

    @Override
    public String getEventId() {
        return EVENT_ID;
    }
}
