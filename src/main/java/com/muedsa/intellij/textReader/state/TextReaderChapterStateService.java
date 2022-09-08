package com.muedsa.intellij.textReader.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.muedsa.intellij.textReader.core.Chapter;
import org.jetbrains.annotations.NotNull;

import java.util.Vector;
@State(name = "com.muedsa.intellij.textReader.ChapterConfig", storages = {@Storage(value = StoragePathMacros.NON_ROAMABLE_FILE)})
public class TextReaderChapterStateService implements PersistentStateComponent<TextReaderChapterStateService> {

    public static TextReaderChapterStateService getInstance() {
       return ApplicationManager.getApplication().getService(TextReaderChapterStateService.class);
    }

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
    public TextReaderChapterStateService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TextReaderChapterStateService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
