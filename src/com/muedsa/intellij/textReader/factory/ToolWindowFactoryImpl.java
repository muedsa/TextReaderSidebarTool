package com.muedsa.intellij.textReader.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.muedsa.intellij.textReader.composes.ReaderWindow;
import org.jetbrains.annotations.NotNull;

public class ToolWindowFactoryImpl implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ReaderWindow readerWindow = new ReaderWindow(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(readerWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
