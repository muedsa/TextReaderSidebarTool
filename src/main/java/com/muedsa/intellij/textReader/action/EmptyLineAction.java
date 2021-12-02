package com.muedsa.intellij.textReader.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.muedsa.intellij.textReader.notify.Notification;
import org.jetbrains.annotations.NotNull;

public class EmptyLineAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        int i = 0;
        while (i < 40){
            Notification.sendHiddenNotify(e.getProject(), ".", NotificationType.INFORMATION);
            i++;
        }
    }
}
