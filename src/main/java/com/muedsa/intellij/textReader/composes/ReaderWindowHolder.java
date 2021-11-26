package com.muedsa.intellij.textReader.composes;

import com.intellij.openapi.project.Project;

import java.util.concurrent.ConcurrentHashMap;

public class ReaderWindowHolder {
    public static final ConcurrentHashMap<Project, ReaderWindow> holder = new ConcurrentHashMap<>();

    public static void put(Project project, ReaderWindow readerWindow){
        holder.put(project, readerWindow);
    }

    public static ReaderWindow get(Project project){
        return holder.get(project);
    }
}
