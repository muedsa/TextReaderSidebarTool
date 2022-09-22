package com.muedsa.intellij.textReader.ui;

import com.muedsa.intellij.textReader.config.EditorBackgroundOffsetType;

import java.awt.*;

public class TextOffset {

    private EditorBackgroundOffsetType type;
    private int offsetX;
    private int offsetY;
    private boolean measure;
    private float x = -1;
    private float y = -1;

    public TextOffset(EditorBackgroundOffsetType type, int offsetX, int offsetY) {
        this.type = type;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.measure = false;
    }

    public TextOffset(EditorBackgroundOffsetType type, int offsetX, int offsetY, Rectangle visibleArea) {
        this(type, offsetX, offsetY);
        measure(visibleArea);
    }

    public void measure(Rectangle visibleArea) {
        if(!isMeasure()){
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
            this.measure = true;
        }
    }

    public boolean isMeasure() {
        return measure;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
