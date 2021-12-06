package com.muedsa.intellij.textReader.notify;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notification {

    private static final NotificationGroup NORMAL_Notify_GROUP = new NotificationGroup(
            "com.muedsa.intellij.textReader.notify",
            NotificationDisplayType.BALLOON,
            true);

    private static final NotificationGroup HIDDEN_NOTIFY_GROUP = new NotificationGroup(
            "com.muedsa.intellij.textReader.hiddenNotify",
            NotificationDisplayType.BALLOON,
            true);

    public static void sendNotify(Project project, String title, String content, NotificationType type){
        NORMAL_Notify_GROUP.createNotification(content, type).setTitle(title).notify(project);
    }

    public static void sendHiddenNotify(Project project, String content, NotificationType type){
        HIDDEN_NOTIFY_GROUP.createNotification(content, type).notify(project);
    }
}
