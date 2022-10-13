package com.muedsa.intellij.textReader.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.VisibleAreaListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.util.Disposer;
import com.muedsa.intellij.textReader.bus.ConfigChangeNotifier;
import com.muedsa.intellij.textReader.config.EditorBackgroundOffsetType;
import com.muedsa.intellij.textReader.state.TextReaderConfigStateService;

import java.awt.*;

public class DynamicBasicTextOffset implements TextOffset {

    private Editor editor;

    private Disposable disposable = Disposer.newDisposable();


    private Rectangle visibleArea;
    private EditorBackgroundOffsetType type;
    private int offsetX;
    private int offsetY;
    private float x = -1;
    private float y = -1;

    public DynamicBasicTextOffset(EditorImpl editor) {
        this.editor = editor;
        visibleArea = editor.getScrollingModel().getVisibleArea();
        VisibleAreaListener visibleAreaListener = e -> {
            visibleArea = e.getNewRectangle();
            measure();
        };
        editor.getScrollingModel().addVisibleAreaListener(visibleAreaListener);
        Disposer.register(editor.getDisposable(), () -> editor.getScrollingModel().removeVisibleAreaListener(visibleAreaListener));
        TextReaderConfigStateService config = TextReaderConfigStateService.getInstance();
        type = config.getEditorBackgroundOffsetType();
        offsetX = config.getEditBackgroundOffsetX();
        offsetY = config.getEditBackgroundOffsetY();
        measure();
        ApplicationManager.getApplication().getMessageBus().connect(editor.getDisposable()).subscribe(ConfigChangeNotifier.TOPIC,
                (key, data, source) -> {
            switch (key) {
                case EDITOR_BACKGROUND_OFFSET_TYPE:
                    type = (EditorBackgroundOffsetType) data;
                    measure();
                    break;
                case EDITOR_BACKGROUND_OFFSET_X:
                    offsetX = (int) data;
                    measure();
                    break;
                case EDITOR_BACKGROUND_OFFSET_Y:
                    offsetY = (int) data;
                    measure();
                    break;
            }
        });
    }

    private void measure() {
        switch (type){
            case LEFT_TOP:
                x = (float) visibleArea.getX() + offsetX;
                y = (float) visibleArea.getY() + offsetY;
                break;
            case LEFT_BOTTOM:
                x = (float) visibleArea.getX() + offsetX;
                y = (float) visibleArea.getY() + (float) visibleArea.getHeight() - offsetY;
                break;
            case RIGHT_TOP:
                x = (float) visibleArea.getX() + (float) visibleArea.getWidth() - offsetX;
                y = (float) visibleArea.getY() + offsetY;
                break;
            case RIGHT_BOTTOM:
                x = (float) visibleArea.getX() + (float) visibleArea.getWidth() - offsetX;
                y = (float) visibleArea.getY() + (float) visibleArea.getHeight() - offsetY;
                break;
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
