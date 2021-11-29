package com.muedsa.intellij.textReader.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.muedsa.intellij.textReader.composes.ReaderWindow;
import com.muedsa.intellij.textReader.composes.ReaderWindowHolder;
import com.muedsa.intellij.textReader.notify.Notification;
import org.jetbrains.annotations.NotNull;

public class PreviousLineAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ReaderWindow readerWindow = ReaderWindowHolder.get(e.getProject());
        if(readerWindow == null){
            Notification.sendHiddenNotify(e.getProject(), "未初始化ToolWindow, 请点击工具栏打开一次窗口", NotificationType.INFORMATION);
        }else{
            Notification.sendHiddenNotify(e.getProject(), readerWindow.previousLine(), NotificationType.INFORMATION);
        }
    }
}
