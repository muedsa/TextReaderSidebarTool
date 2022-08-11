package com.muedsa.intellij.textReader.core.event;

public abstract class TextReaderEvent {

    private final Object data;

    //source of the event
    private Object tag;

    public TextReaderEvent(Object data){
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public abstract String getEventId();
}
