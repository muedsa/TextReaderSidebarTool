package com.muedsa.intellij.textReader.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.muedsa.intellij.textReader.ui.ReaderWindow;
import org.jetbrains.annotations.NotNull;

public class ReaderWindowFactoryImpl implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ReaderWindow readerWindow = new ReaderWindow(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentFactory.createContent(readerWindow.getContent(), "", false);
        Disposer.register(content, readerWindow);
        contentManager.addContent(content);
    }
}
