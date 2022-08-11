package com.muedsa.intellij.textReader.composes;

import com.intellij.openapi.project.Project;

import java.util.concurrent.ConcurrentHashMap;

public class ReaderLineWidgetHolder {
    public static final ConcurrentHashMap<Project, ReaderLineWidget> holder = new ConcurrentHashMap<>();

    public static void put(Project project, ReaderLineWidget readerLineWidget){
        holder.put(project, readerLineWidget);
    }

    public static ReaderLineWidget get(Project project){
        return holder.get(project);
    }

    public static void remove(Project project) {
        holder.remove(project);
    }
}
