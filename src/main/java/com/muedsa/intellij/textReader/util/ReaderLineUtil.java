package com.muedsa.intellij.textReader.util;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.muedsa.intellij.textReader.config.ShowReaderLineType;
import com.muedsa.intellij.textReader.config.TextReaderConfig;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.notify.Notification;
import com.muedsa.intellij.textReader.state.TextReaderConfigStateService;
import com.muedsa.intellij.textReader.ui.ReaderLineWidget;
import com.muedsa.intellij.textReader.ui.ReaderLineWidgetHolder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ReaderLineUtil {

    private static final Set<Project> historyFlag = new HashSet<>();
    private static Project LAST_PROJECT = null;
    private static ShowReaderLineType SHOW_READER_LINE_TYPE_FROM_LAST = null;

    public static void nextLine(TextReaderConfig config, Project project) {
        String line;
        NotificationType type;
        TextReaderCore textReaderCore = TextReaderCore.getInstance();
        if(textReaderCore.isReady()){
            type = NotificationType.INFORMATION;
            line = textReaderCore.nextLine(config.getReaderLineSize());
        }else{
            type = NotificationType.WARNING;
            line = Notification.MSG_NOT_LOAD_FILE;
        }
        dispatchLineAction(line, type, config, project);
    }

    public static void previousLine(TextReaderConfigStateService config, Project project) {
        String line;
        NotificationType type;
        TextReaderCore textReaderCore = TextReaderCore.getInstance();
        if(textReaderCore.isReady()){
            type = NotificationType.INFORMATION;
            line = textReaderCore.previousLine(config.getReaderLineSize());
        }else{
            type = NotificationType.WARNING;
            line = Notification.MSG_NOT_LOAD_FILE;
        }
        dispatchLineAction(line, type, config, project);
    }

    private static void dispatchLineAction(String line, NotificationType type, TextReaderConfig config, Project project) {
        switch (config.getShowReaderLineType()){
            case NOTIFY:
                Notification.sendHiddenNotify(project, line, type);
                break;
            case STATUS_BAR:
                ReaderLineWidget readerLineWidget = ReaderLineWidgetHolder.get(project);
                if(readerLineWidget != null){
                    readerLineWidget.setLine(line);
                }
                break;
            case EDITOR_BACKGROUND:
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                if(editor != null){
                    UiUtil.setEditorTextBackground(editor, line, config);
                }
                break;
        }
        afterAction(project, config);
    }

    public static void clear(ShowReaderLineType showReaderLineType, TextReaderConfig config, Project project) {
        switch (showReaderLineType){
            case NOTIFY:
                int i = 0;
                while (i < 40){
                    Notification.sendHiddenNotify(project, Notification.MSG_DOT, NotificationType.INFORMATION);
                    i++;
                }
                break;
            case STATUS_BAR:
                ReaderLineWidget readerLineWidget = ReaderLineWidgetHolder.get(project);
                if(readerLineWidget != null){
                    readerLineWidget.setLine(ReaderLineWidget.DEFAULT_LINE);
                }
                break;
            case EDITOR_BACKGROUND:
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                if(editor != null){
                    UiUtil.setEditorTextBackground(editor, null, config);
                }
                break;
        }
    }

    private static void afterAction(Project project, TextReaderConfig config) {
        if(!historyFlag.contains(project)){
            historyFlag.add(project);
            registerClearHistoryDispose(project);
        }
        if(LAST_PROJECT != null
                && (!Objects.equals(project, LAST_PROJECT)
                    || SHOW_READER_LINE_TYPE_FROM_LAST != config.getShowReaderLineType())){
            clear(SHOW_READER_LINE_TYPE_FROM_LAST, config, LAST_PROJECT);
        }
        LAST_PROJECT = project;
        SHOW_READER_LINE_TYPE_FROM_LAST = config.getShowReaderLineType();
    }

    public static void registerClearHistoryDispose(Project project){
        Disposer.register(project, () -> {
            historyFlag.remove(project);
            if(Objects.equals(project, LAST_PROJECT)) LAST_PROJECT = null;
        });
    }
}
