package com.muedsa.intellij.textReader.util;

import com.intellij.notification.NotificationType;
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

    private static Set<Project> historyFlag = new HashSet<>();
    private static Project LAST_PROJECT = null;
    private static boolean IS_SHOW_READER_LINT_AT_STATUS_BAR_FROM_LAST = TextReaderConfig.isShowReaderLintAtStatusBar();

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
        if(TextReaderConfig.isShowReaderLintAtStatusBar()){
            ReaderLineWidget readerLineWidget = ReaderLineWidgetHolder.get(project);
            if(readerLineWidget != null){
                readerLineWidget.setLine(line);
            }
        }else{
            Notification.sendHiddenNotify(project, line, type);
        }
        afterAction(project);
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
        if(TextReaderConfig.isShowReaderLintAtStatusBar()){
            ReaderLineWidget readerLineWidget = ReaderLineWidgetHolder.get(project);
            if(readerLineWidget != null){
                readerLineWidget.setLine(line);
            }
        }else{
            Notification.sendHiddenNotify(project, line, type);
        }
        afterAction(project);
    }

    public static void clear(Project project, boolean isShowReaderLintAtStatusBar) {
        if(isShowReaderLintAtStatusBar){
            ReaderLineWidget readerLineWidget = ReaderLineWidgetHolder.get(project);
            if(readerLineWidget != null){
                readerLineWidget.setLine(ReaderLineWidget.DEFAULT_LINE);
            }
        }else{
            int i = 0;
            while (i < 40){
                Notification.sendHiddenNotify(project, Notification.MSG_DOT, NotificationType.INFORMATION);
                i++;
            }
        }
    }

    private static void afterAction(Project project) {
        if(!historyFlag.contains(project)){
            historyFlag.add(project);
            registerClearHistoryDispose(project);
        }
        if(LAST_PROJECT != null && !Objects.equals(project, LAST_PROJECT)){
            clear(LAST_PROJECT, IS_SHOW_READER_LINT_AT_STATUS_BAR_FROM_LAST);
        }
        LAST_PROJECT = project;
        IS_SHOW_READER_LINT_AT_STATUS_BAR_FROM_LAST = TextReaderConfig.isShowReaderLintAtStatusBar();
    }

    public static void registerClearHistoryDispose(Project project){
        Disposer.register(project, () -> {
            historyFlag.remove(project);
            if(Objects.equals(project, LAST_PROJECT)) LAST_PROJECT = null;
        });
    }
}
