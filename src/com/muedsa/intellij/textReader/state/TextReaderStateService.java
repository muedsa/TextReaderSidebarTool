package com.muedsa.intellij.textReader.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.muedsa.intellij.textReader.Chapter;
import org.jetbrains.annotations.NotNull;

import java.util.Vector;
@State(name = "com.muedsa.intellij.textReader.chapter", storages = {@Storage(value = "$APP_CONFIG$/muedsa_tr.xml")})
public class TextReaderStateService implements PersistentStateComponent<TextReaderStateService> {

    private Vector<Chapter> chapters = new Vector<>();

    private String filePath;

    public Vector<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(Vector<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public TextReaderStateService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TextReaderStateService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
