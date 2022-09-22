package com.muedsa.intellij.textReader.ui;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class Paragraph {
    private LineBreakMeasurer lineBreakMeasurer;
    private int paragraphStart;
    private int paragraphEnd;

    public Paragraph(@Nullable AttributedString text, FontRenderContext fontRenderContext) {
        if(text != null) {
            AttributedCharacterIterator paragraph = text.getIterator();
            paragraphStart = paragraph.getBeginIndex();
            paragraphEnd = paragraph.getEndIndex();
            lineBreakMeasurer = new LineBreakMeasurer(paragraph, fontRenderContext);
        }
    }

    /**
     * @return 渲染完段落后的Y坐标
     */
    public float paint(Graphics2D g2d, float x, float y, float width, float interlineSpacingScale) {
        if(lineBreakMeasurer != null) {
            lineBreakMeasurer.setPosition(paragraphStart);
            while (lineBreakMeasurer.getPosition() < paragraphEnd) {
                TextLayout layout = lineBreakMeasurer.nextLayout(width);
                y += layout.getAscent();
                layout.draw(g2d, x, y);
                //y += layout.getDescent() + layout.getLeading();
                y += layout.getDescent() + (layout.getAscent() + layout.getDescent()) * interlineSpacingScale;
            }
        }
        return y;
    }
}
