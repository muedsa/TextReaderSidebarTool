package com.muedsa.intellij.textReader.util;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.muedsa.intellij.textReader.composes.ReaderLineWidget;
import com.muedsa.intellij.textReader.composes.ReaderLineWidgetHolder;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.core.config.TextReaderConfig;
import com.muedsa.intellij.textReader.notify.Notification;

public class ReaderLineUtil {
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
                readerLineWidget.setText(line);
            }
        }else{
            Notification.sendHiddenNotify(project, line, type);
        }
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
                readerLineWidget.setText(line);
            }
        }else{
            Notification.sendHiddenNotify(project, line, type);
        }
    }

    public static void clear(Project project) {
        if(TextReaderConfig.isShowReaderLintAtStatusBar()){
            ReaderLineWidget readerLineWidget = ReaderLineWidgetHolder.get(project);
            if(readerLineWidget != null){
                readerLineWidget.setText(null);
            }
        }else{
            int i = 0;
            while (i < 40){
                Notification.sendHiddenNotify(project, Notification.MSG_DOT, NotificationType.INFORMATION);
                i++;
            }
        }
    }
}
