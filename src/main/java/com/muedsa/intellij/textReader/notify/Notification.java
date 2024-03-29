package com.muedsa.intellij.textReader.notify;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notification {

    public static final String TITLE_LOAD_FILE_SUCCESS = "加载文件成功";
    public static final String TITLE_LOAD_FILE_ERROR = "加载文件失败";
    public static final String TITLE_LOAD_FILE_REGEX_ERROR = "正则错误";
    public static final String TITLE_ERROR = "错误";
    public static final String TITLE_READ_CHAPTER_ERROR = "文件读取失败";
    public static final String MSG_NOT_LOAD_FILE = "未初始化文章, 请在工具窗口载入文章";
    public static final String MSG_DOT = ".";

    private static final NotificationGroup NORMAL_Notify_GROUP = new NotificationGroup(
            "com.muedsa.intellij.textReader.notify",
            NotificationDisplayType.BALLOON,
            true);

    private static final NotificationGroup HIDDEN_NOTIFY_GROUP = new NotificationGroup(
            "com.muedsa.intellij.textReader.hiddenNotify",
            NotificationDisplayType.NONE,
            true);

    public static void sendNotify(Project project, String title, String content, NotificationType type){
        NORMAL_Notify_GROUP.createNotification(content, type).setTitle(title).notify(project);
    }

    public static void sendHiddenNotify(Project project, String content, NotificationType type){
        HIDDEN_NOTIFY_GROUP.createNotification(content, type).notify(project);
    }
}
