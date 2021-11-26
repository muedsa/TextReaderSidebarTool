package com.muedsa.intellij.textReader.notify;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notification {

    public static void sendNotify(Project project, String title, String content, NotificationType type){
        NotificationGroupManager.getInstance().getNotificationGroup("com.muedsa.intellij.textReader.notify")
                .createNotification(content, type)
                .setTitle(title)
                .notify(project);
    }

    public static void sendHiddenNotify(Project project, String content, NotificationType type){
        NotificationGroupManager.getInstance().getNotificationGroup("com.muedsa.intellij.textReader.hiddenNotify")
                .createNotification(content, type)
                .notify(project);
    }
}
