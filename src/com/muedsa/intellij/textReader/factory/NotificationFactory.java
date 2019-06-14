package com.muedsa.intellij.textReader.factory;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class NotificationFactory {

    private static NotificationFactory instance;

    private Project project;

    public NotificationFactory(Project project){
        this.project = project;
        instance = this;
    }

    public static void sendNotify(String title, String content, NotificationType type){
        Notification notification = new Notification("TextReaderSiderTool", title,content, type);
        notification.notify(instance.project);
    }
}
