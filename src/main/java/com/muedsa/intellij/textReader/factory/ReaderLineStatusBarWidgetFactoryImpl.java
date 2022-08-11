package com.muedsa.intellij.textReader.factory;

import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.muedsa.intellij.textReader.composes.ReaderLineWidget;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ReaderLineStatusBarWidgetFactoryImpl implements StatusBarWidgetFactory, LightEditCompatible {

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new ReaderLineWidget(project);
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        Disposer.dispose(widget);
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }

    @Override
    public @NotNull String getId() {
        return ReaderLineWidget.ID;
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return ReaderLineWidget.DISPLAY_NAME;
    }
}
