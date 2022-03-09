package com.muedsa.intellij.textReader.core.event;

public abstract class TextReaderEvent {

    private final Object data;

    public TextReaderEvent(Object data){
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public abstract String getEventId();
}
