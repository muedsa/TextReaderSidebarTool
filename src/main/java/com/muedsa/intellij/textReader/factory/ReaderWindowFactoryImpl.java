package com.muedsa.intellij.textReader.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.*;
import com.muedsa.intellij.textReader.composes.ReaderWindow;
import org.jetbrains.annotations.NotNull;

public class ReaderWindowFactoryImpl implements ToolWindowFactory {
    public static final String DEFAULT_TOOL_WINDOW_NAME = "Text Reader";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ReaderWindow readerWindow = new ReaderWindow(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentFactory.createContent(readerWindow.getContent(), DEFAULT_TOOL_WINDOW_NAME, false);
        Disposer.register(content, readerWindow);
        contentManager.addContent(content);
    }
}
