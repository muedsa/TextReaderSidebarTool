package com.muedsa.intellij.textReader.core;

public class Chapter {
    private int startOffset;
    private int length;
    private String title;

    public Chapter() {
        this.startOffset = 0;
        this.length = 0;
        this.title = null;
    }

    public Chapter(int startOffset, String title) {
        this.startOffset = startOffset;
        this.title = title;
    }

    public Chapter(int startOffset, int length, String title) {
        this.startOffset = startOffset;
        this.length = length;
        this.title = title;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "●" + title;
    }
}
