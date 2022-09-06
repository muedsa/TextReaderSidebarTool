package com.muedsa.intellij.textReader.util;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.muedsa.intellij.textReader.composes.ReaderLineWidget;
import com.muedsa.intellij.textReader.composes.ReaderLineWidgetHolder;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.core.config.TextReaderConfig;
import com.muedsa.intellij.textReader.notify.Notification;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ReaderLineUtil {

    private static final Set<Project> historyFlag = new HashSet<>();
    private static Project LAST_PROJECT = null;
    private static TextReaderConfig.ShowReaderLineType SHOW_READER_LINE_TYPE_FROM_LAST = TextReaderConfig.getShowReaderLineType();

    public static void nextLine(Project project) {
        String line;
        NotificationType type;
        TextReaderCore textReaderCore = TextReaderCore.getInstance();
        if(textReaderCore.isReady()){
            type = NotificationType.INFORMATION;
            line = textReaderCore.nextLine();
        }else{
            type = NotificationType.WARNING;
            line = Notification.MSG_NOT_LOAD_FILE;
        }
        dispatchLineAction(project, line, type);
    }

    public static void previousLine(Project project) {
        String line;
        NotificationType type;
        TextReaderCore textReaderCore = TextReaderCore.getInstance();
        if(textReaderCore.isReady()){
            type = NotificationType.INFORMATION;
            line = textReaderCore.previousLine();
        }else{
            type = NotificationType.WARNING;
            line = Notification.MSG_NOT_LOAD_FILE;
        }
        dispatchLineAction(project, line, type);
    }

    private static void dispatchLineAction(Project project, String line, NotificationType type) {
        switch (TextReaderConfig.getShowReaderLineType()){
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
                    EditorBackgroundUtil.setTextBackground(editor, line);
                }
                break;
        }
        afterAction(project);
    }

    public static void clear(Project project, TextReaderConfig.ShowReaderLineType showReaderLineType) {
        switch (showReaderLineType){
            case NOTIFY:
                ReaderLineWidget readerLineWidget = ReaderLineWidgetHolder.get(project);
                if(readerLineWidget != null){
                    readerLineWidget.setLine(ReaderLineWidget.DEFAULT_LINE);
                }
                break;
            case STATUS_BAR:
                int i = 0;
                while (i < 40){
                    Notification.sendHiddenNotify(project, Notification.MSG_DOT, NotificationType.INFORMATION);
                    i++;
                }
                break;
            case EDITOR_BACKGROUND:
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                if(editor != null){
                    EditorBackgroundUtil.setTextBackground(editor, null);
                }
                break;
        }
    }

    private static void afterAction(Project project) {
        if(!historyFlag.contains(project)){
            historyFlag.add(project);
            registerClearHistoryDispose(project);
        }
        if(LAST_PROJECT != null
                && (!Objects.equals(project, LAST_PROJECT)
                    || SHOW_READER_LINE_TYPE_FROM_LAST != TextReaderConfig.getShowReaderLineType())){
            clear(LAST_PROJECT, SHOW_READER_LINE_TYPE_FROM_LAST);
        }
        LAST_PROJECT = project;
        SHOW_READER_LINE_TYPE_FROM_LAST = TextReaderConfig.getShowReaderLineType();
    }

    public static void registerClearHistoryDispose(Project project){
        Disposer.register(project, () -> {
            historyFlag.remove(project);
            if(Objects.equals(project, LAST_PROJECT)) LAST_PROJECT = null;
        });
    }
}
