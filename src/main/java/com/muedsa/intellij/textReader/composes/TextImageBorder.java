package com.muedsa.intellij.textReader.composes;

import com.intellij.util.ui.JBUI;
import com.muedsa.intellij.textReader.core.config.TextReaderConfig;

import javax.swing.border.Border;
import java.awt.*;

public class TextImageBorder implements Border {

    private String text;
    private Font font;
    private Color color;

    private Rectangle visibleArea;

    public TextImageBorder(String text, Font font, Color color, Rectangle visibleArea) {
        this.text = text;
        this.font = font;
        this.color = color;
        this.visibleArea = visibleArea;
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setFont(font);
        g2.setColor(color);
        int drawX = 0;
        int drawY = 0;
        int offsetX = TextReaderConfig.getEditBackgroundOffsetX();
        int offsetY = TextReaderConfig.getEditBackgroundOffsetY();
        switch (TextReaderConfig.getEditBackgroundOffsetType()){
            case LEFT_TOP:
                drawX = (int) visibleArea.getX() + offsetX;
                drawY = (int) visibleArea.getY() + offsetY;
                break;
            case LEFT_BOTTOM:
                drawX = (int) visibleArea.getX() + offsetX;
                drawY = (int) visibleArea.getY() + (int) visibleArea.getHeight() - offsetY;
                break;
            case RIGHT_TOP:
                drawX = (int) visibleArea.getX() + (int) visibleArea.getWidth() - offsetX;
                drawY = (int) visibleArea.getY() + offsetY;
                break;
            case RIGHT_BOTTOM:
                drawX = (int) visibleArea.getX() + (int) visibleArea.getWidth() - offsetX;
                drawY = (int) visibleArea.getY() + (int) visibleArea.getHeight() - offsetY;
                break;
        }
        g2.drawString(text, drawX, drawY);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return JBUI.emptyInsets();
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

}
