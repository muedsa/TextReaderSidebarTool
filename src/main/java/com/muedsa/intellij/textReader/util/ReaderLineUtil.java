package com.muedsa.intellij.textReader.util;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.impl.EditorImpl;
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
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Objects;


public class ReaderLineUtil {

    private static Project LAST_PROJECT = null;

    private static Editor LAST_EDITOR = null;

    private static EditorMouseListener LAST_EDITOR_LISTENER = null;

    private static MouseWheelListener LAST_EDITOR_MOUSE_WHEEL_LISTENER = null;

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
        Editor editor = null;
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
                editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                if(editor != null){
                    UiUtil.setEditorTextBackground(editor, line, config);
                }
                break;
        }
        afterAction(project, editor, config);
    }

    public static void clear(Project project, Editor editor) {
        if(SHOW_READER_LINE_TYPE_FROM_LAST != null){
            switch (SHOW_READER_LINE_TYPE_FROM_LAST){
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
                    if(editor != null){
                        UiUtil.resetEditorBackground(editor);
                    }
                    break;
            }
        }
    }

    public static void clearLast() {
        clear(LAST_PROJECT, LAST_EDITOR);
        if(LAST_EDITOR != null && LAST_EDITOR_LISTENER != null){
            LAST_EDITOR.removeEditorMouseListener(LAST_EDITOR_LISTENER);
            LAST_EDITOR.getContentComponent().removeMouseWheelListener(LAST_EDITOR_MOUSE_WHEEL_LISTENER);
            LAST_EDITOR = null;
            LAST_EDITOR_LISTENER = null;
            LAST_EDITOR_MOUSE_WHEEL_LISTENER = null;
        }
    }

    private static void afterAction(Project project, Editor editor, TextReaderConfig config) {
        boolean clearFlag = !config.getShowReaderLineType().equals(SHOW_READER_LINE_TYPE_FROM_LAST);

        if(LAST_PROJECT != project){
            registerClearHistoryDispose(project);
            clearFlag = true;
        }
        EditorMouseListener listener = null;
        MouseWheelListener mouseWheelListener = null;
        if(editor != null && LAST_EDITOR != editor){
            listener = new EditorMouseListener() {
                @Override
                public void mouseClicked(@NotNull EditorMouseEvent event) {
                    MouseEvent mouseEvent = event.getMouseEvent();
                    if(mouseEvent.getButton() == MouseEvent.BUTTON1){
                        if(mouseEvent.isAltDown()){
                            ReaderLineUtil.previousLine(TextReaderConfigStateService.getInstance(),
                                    event.getEditor().getProject());
                        }else{
                            ReaderLineUtil.nextLine(TextReaderConfigStateService.getInstance(),
                                    event.getEditor().getProject());
                        }
                    }
                }
            };
            if (config.isEnableNextLineActionByScrollRadioButton()) {
                mouseWheelListener = e -> {
                    if (config.isEnableNextLineActionByScrollRadioButton()
                            && e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                        if (e.getWheelRotation() > 0) {
                            ReaderLineUtil.nextLine(TextReaderConfigStateService.getInstance(),
                                    editor.getProject());
                        } else {
                            ReaderLineUtil.previousLine(TextReaderConfigStateService.getInstance(),
                                    editor.getProject());
                        }
                    }
                };
            }
            editor.addEditorMouseListener(listener);
            registerClearEditorHistoryDispose(editor);
            editor.getContentComponent().addMouseWheelListener(mouseWheelListener);
            clearFlag = true;
        }

        if(clearFlag){
            clearLast();
        }

        LAST_PROJECT = project;
        if(editor != null && LAST_EDITOR != editor){
            LAST_EDITOR_LISTENER = listener;
            LAST_EDITOR_MOUSE_WHEEL_LISTENER = mouseWheelListener;
        }
        LAST_EDITOR = editor;
        SHOW_READER_LINE_TYPE_FROM_LAST = config.getShowReaderLineType();
    }

    public static void registerClearHistoryDispose(Project project){
        Disposer.register(project, () -> {
            if(Objects.equals(project, LAST_PROJECT)) LAST_PROJECT = null;
        });
    }

    public static void registerClearEditorHistoryDispose(Editor editor){
        if (editor instanceof EditorImpl editorImpl) {
            Disposer.register(editorImpl.getDisposable(), () -> {
                if(Objects.equals(editor, LAST_EDITOR)) LAST_EDITOR = null;
            });
        }
    }
}
