package com.muedsa.intellij.textReader.ui;

import com.intellij.openapi.application.ApplicationManager;
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
import com.muedsa.intellij.textReader.bus.ConfigChangeNotifier;
import com.muedsa.intellij.textReader.config.ConfigKey;
import com.muedsa.intellij.textReader.config.ShowReaderLineType;
import com.muedsa.intellij.textReader.state.TextReaderConfigStateService;
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
    private TextReaderConfigStateService config;

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
        config = TextReaderConfigStateService.getInstance();
        ReaderLineWidgetHolder.put(project, this);
        initConfigMessageBusSubscribe();
    }

    private void initConfigMessageBusSubscribe(){
        ApplicationManager.getApplication().getMessageBus()
                .connect(this)
                .subscribe(ConfigChangeNotifier.CHANGE_ACTION_TOPIC, ((key, data, source) -> {
                    if(ConfigKey.SHOW_READER_LINE_TYPE.equals(key)) {
                        ShowReaderLineType showReaderLineType = (ShowReaderLineType) data;
                        setLine(DEFAULT_LINE);
                        if(ShowReaderLineType.STATUS_BAR.equals(showReaderLineType)){
                            ReaderLineUtil.clear(config.getShowReaderLineType(), config, project);
                            StatusBarWidgetFactory widgetFactory = project.getService(StatusBarWidgetsManager.class).findWidgetFactory(ID);
                            if(widgetFactory != null) {
                                ServiceManager.getService(StatusBarWidgetSettings.class).setEnabled(widgetFactory, true);
                            }
                        }
                    }else if(ConfigKey.READER_LINE_COLOR.equals(key)){
                        Color color = (Color) data;
                        component.setForeground(color);
                    }
                }));
    }

    public void setLine(String text) {
        if(text != null){
            line = text;
        }else{
            line = DEFAULT_LINE;
        }
        component.beforeUpdate();
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        component.setForeground(config.getReaderLineColor());
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

    @Override
    public void dispose() {
        ReaderLineWidgetHolder.remove(project);
        this.line = null;
    }

    @Override
    public @NotNull String ID() {
        return ID;
    }
}
