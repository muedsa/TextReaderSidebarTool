package com.muedsa.intellij.textReader.composes;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetSettings;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager;
import com.intellij.util.Consumer;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.core.config.TextReaderConfig;
import com.muedsa.intellij.textReader.core.event.ConfigChangeEvent;
import com.muedsa.intellij.textReader.core.event.TextReaderEventListener;
import com.muedsa.intellij.textReader.util.ReaderLineUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ReaderLineWidget implements StatusBarWidget, Disposable {
    public static final String ID = "com.muedsa.intellij.textReader.ReaderLineWidget";
    public static final String DISPLAY_NAME = "Text Reader Line";

    public static class ReaderLinePresentation implements TextPresentation, Disposable {

        public static final String DEFAULT_TEXT = "";
        private String text = DEFAULT_TEXT;

        public void setText(String text) {
            if(text == null) {
                this.text = DEFAULT_TEXT;
            }else{
                this.text = text.trim();
            }
        }

        @Override
        public @Nullable @Nls(capitalization = Nls.Capitalization.Sentence) String getTooltipText() {
            return DISPLAY_NAME;
        }

        @Override
        public @Nullable String getShortcutText() {
            return text;
        }

        @Override
        public @Nullable Consumer<MouseEvent> getClickConsumer() {
            return e -> System.out.println("Clicked");
        }

        @Override
        public @NotNull @Nls String getText() {
            return text;
        }

        @Override
        public float getAlignment() {
            return Component.LEFT_ALIGNMENT;
        }

        @Override
        public void dispose() {
            text = null;
        }
    }

    private ReaderLinePresentation presentation;
    private Project project;
    private StatusBar statusBar;
    private TextReaderEventListener listener;

    public ReaderLineWidget(Project project) {
        presentation = new ReaderLinePresentation();
        Disposer.register(this, presentation);
        this.project = project;
        ReaderLineWidgetHolder.put(project, this);
        listener = event -> {
            ConfigChangeEvent configChangeEvent = (ConfigChangeEvent) event;
            if(configChangeEvent.getConfigKey() == TextReaderConfig.ConfigKey.SHOW_READER_LINT_AT_STATUS_BAR) {
                boolean show = (boolean) configChangeEvent.getData();
                setText(ReaderLinePresentation.DEFAULT_TEXT);
                if(show){
                    ReaderLineUtil.clear(project);
                    StatusBarWidgetFactory widgetFactory = project.getService(StatusBarWidgetsManager.class).findWidgetFactory(ID);
                    if(widgetFactory != null) {
                        ServiceManager.getService(StatusBarWidgetSettings.class).setEnabled(widgetFactory, true);
                    }
                }
            }
        };
        TextReaderCore.getInstance().getEventManage().addListener(ConfigChangeEvent.EVENT_ID, listener);
    }

    public void setText(String text) {
        presentation.setText(text);
        statusBar.updateWidget(ID);
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public @Nullable WidgetPresentation getPresentation() {
        return presentation;
    }

    @Override
    public void dispose() {
        TextReaderCore.getInstance().getEventManage().removeListener(ConfigChangeEvent.EVENT_ID, listener);
        ReaderLineWidgetHolder.remove(project);
        presentation = null;
    }

    @Override
    public @NotNull String ID() {
        return ID;
    }
}
