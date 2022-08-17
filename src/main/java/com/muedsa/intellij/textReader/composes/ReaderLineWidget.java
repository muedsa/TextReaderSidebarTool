package com.muedsa.intellij.textReader.composes;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetSettings;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetWrapper;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ReaderLineWidget implements StatusBarWidget, CustomStatusBarWidget, StatusBarWidget.TextPresentation {
    public static final String ID = "com.muedsa.intellij.textReader.ReaderLineWidget";
    public static final String DISPLAY_NAME = "Text Reader Line";
    public static final String DEFAULT_LINE = "";
    private String line = DEFAULT_LINE;
    private ReaderLineComponent component;
    private Project project;
    private StatusBar statusBar;
    private TextReaderEventListener listener;

    @Override
    public @Nullable @Nls(capitalization = Nls.Capitalization.Sentence) String getTooltipText() {
        return DISPLAY_NAME;
    }

    @Override
    public @Nullable String getShortcutText() {
        return null;
    }

    @Override
    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        return null;
    }

    @Override
    public @NotNull @Nls String getText() {
        return line;
    }

    @Override
    public float getAlignment() {
        return Component.CENTER_ALIGNMENT;
    }


    public final class ReaderLineComponent extends TextPanel implements StatusBarWidgetWrapper{

        private TextPresentation presentation;

        ReaderLineComponent(TextPresentation presentation) {
            this.presentation = presentation;
            setVisible(!presentation.getText().isEmpty());
            setBorder(StatusBarWidget.WidgetBorder.INSTANCE);
            Consumer<MouseEvent> clickConsumer = presentation.getClickConsumer();
            if (clickConsumer != null) {
                new StatusBarWidgetClickListener(clickConsumer).installOn(this, true);
            }
        }

        @Override
        public @NotNull WidgetPresentation getPresentation() {
            return presentation;
        }

        @Override
        public void beforeUpdate() {
            String text = presentation.getText();
            setText(text);
            setVisible(!text.isEmpty());
        }
    }


    public ReaderLineWidget(Project project) {
        component = new ReaderLineComponent(this);
        this.project = project;
        ReaderLineWidgetHolder.put(project, this);
        listener = event -> {
            ConfigChangeEvent configChangeEvent = (ConfigChangeEvent) event;
            if(configChangeEvent.getConfigKey() == TextReaderConfig.ConfigKey.SHOW_READER_LINT_AT_STATUS_BAR) {
                boolean show = (boolean) configChangeEvent.getData();
                setLine(DEFAULT_LINE);
                if(show){
                    ReaderLineUtil.clear(project, TextReaderConfig.isShowReaderLintAtStatusBar());
                    StatusBarWidgetFactory widgetFactory = project.getService(StatusBarWidgetsManager.class).findWidgetFactory(ID);
                    if(widgetFactory != null) {
                        ServiceManager.getService(StatusBarWidgetSettings.class).setEnabled(widgetFactory, true);
                    }
                }
            }
        };
        TextReaderCore.getInstance().getEventManage().addListener(ConfigChangeEvent.EVENT_ID, listener);
        setColor(Color.BLACK);
    }

    public void setLine(String text) {
        if(text != null){
            line = text;
        }else{
            line = DEFAULT_LINE;
        }
        component.beforeUpdate();
    }

    public void setColor(Color color) {
        component.setForeground(color);
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public @Nullable WidgetPresentation getPresentation() {
        return this;
    }

    @Override
    public JComponent getComponent() {
        return component;
    }

    @Override
    public void dispose() {
        TextReaderCore.getInstance().getEventManage().removeListener(ConfigChangeEvent.EVENT_ID, listener);
        ReaderLineWidgetHolder.remove(project);
        this.line = null;
    }

    @Override
    public @NotNull String ID() {
        return ID;
    }
}
