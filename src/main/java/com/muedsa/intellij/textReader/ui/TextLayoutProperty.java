package com.muedsa.intellij.textReader.ui;

import java.awt.*;

public class TextLayoutProperty {

    private Font font; //字体

    private Color color; //字体颜色

    private float interlineSpacingScale;   //行间距

    private float interParagraphSpacingScale; //段落间距

    private int breakWidth;

    public TextLayoutProperty(Font font, Color color, float interlineSpacingScale,
                              float interParagraphSpacingScale, int breakWidth) {
        this.font = font;
        this.color = color;
        this.interlineSpacingScale = interlineSpacingScale;
        this.interParagraphSpacingScale = interParagraphSpacingScale;
        this.breakWidth = breakWidth;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getInterlineSpacingScale() {
        return interlineSpacingScale;
    }

    public void setInterlineSpacingScale(float interlineSpacingScale) {
        this.interlineSpacingScale = interlineSpacingScale;
    }

    public float getInterParagraphSpacingScale() {
        return interParagraphSpacingScale;
    }

    public void setInterParagraphSpacingScale(float interParagraphSpacingScale) {
        this.interParagraphSpacingScale = interParagraphSpacingScale;
    }

    public int getBreakWidth() {
        return breakWidth;
    }

    public void setBreakWidth(int breakWidth) {
        this.breakWidth = breakWidth;
    }
}
