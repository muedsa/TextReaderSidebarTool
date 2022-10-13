package com.muedsa.intellij.textReader.ui;

import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

public class Paragraph2 {

    private int paragraphNum;

    private LineBreakMeasurer lineBreakMeasurer;
    private int paragraphStart;
    private int paragraphEnd;

    private float breakWidth;

    private float interlineSpacingScale;

    private List<TextLayout> lines;

    private int breakPosition;

    public Paragraph2(int paragraphNum, @Nullable AttributedString line, FontRenderContext fontRenderContext, float breakWidth, float interlineSpacingScale) {
        this.paragraphNum = paragraphNum;
        this.breakWidth = breakWidth;
        this.interlineSpacingScale = interlineSpacingScale;
        if(line != null) {
            AttributedCharacterIterator paragraph = line.getIterator();
            paragraphStart = paragraph.getBeginIndex();
            paragraphEnd = paragraph.getEndIndex();
            lineBreakMeasurer = new LineBreakMeasurer(paragraph, fontRenderContext);
            initLines(paragraphStart, paragraphEnd);
        }
    }

    public int getParagraphStart() {
        return paragraphStart;
    }

    public int getParagraphEnd() {
        return paragraphEnd;
    }

    public int getLinesCount() {
        return lines.size();
    }

    public float measureLineHeight(int index) {
        TextLayout textLayout = lines.get(index);
        return textLayout.getAscent() + textLayout.getDescent()
                + (textLayout.getAscent() + textLayout.getDescent()) * interlineSpacingScale;
    }

    public int getCharacterCount() {
        return getCharacterCount(0, lines.size() - 1);
    }

    public int getCharacterCount(int end) {
        return getCharacterCount(0, end);
    }

    public int getCharacterCount(int start, int end) {
        int count = 0;
        for (; start <= end; start++) {
            count += lines.get(start).getCharacterCount();
        }
        return count;
    }

    /**
     * @return 渲染完段落后的Y坐标
     */
    public float paintLine(Graphics2D g2d, float x, float y, float bottomBound) {
        if(lineBreakMeasurer != null) {
            float debuggerX = x + breakWidth + 10;
            for (TextLayout textLayout : lines) {
                if (y >= bottomBound) {
                    break;
                }
                float lineY = y + textLayout.getAscent();
                textLayout.draw(g2d, x, lineY);
                y = lineY + textLayout.getDescent() + (textLayout.getAscent() + textLayout.getDescent()) * interlineSpacingScale;
                breakPosition += textLayout.getCharacterCount();
                paintDebugger(g2d, debuggerX, lineY);
            }
        }
        return y;
    }

//    public float paintReverse(Graphics2D g2d, float x, float y, float limitY) {
//        if(lineBreakMeasurer != null) {
//            float debuggerX = x + breakWidth + 10;
//            for (int i = textLayoutList.size() - 1; i >= 0; i--) {
//                if (y <= limitY) {
//                    break;
//                }
//                TextLayout textLayout = textLayoutList.get(i);
//                float lineY = y - textLayout.getAscent();
//                textLayout.draw(g2d, x, lineY);
//                y = lineY - textLayout.getDescent() - (textLayout.getAscent() + textLayout.getDescent()) * interlineSpacingScale;
//                endPosition -= textLayout.getCharacterCount();
//                paintDebugger(g2d, debuggerX, lineY);
//            }
//        }
//        return y;
//    }

    public void initLines(int start, int end) {
        lineBreakMeasurer.setPosition(start);
        breakPosition = start;
        lines = new ArrayList<>();
        while (lineBreakMeasurer.getPosition() < end) {
            TextLayout textLayout = lineBreakMeasurer.nextLayout(breakWidth);
            lines.add(textLayout);
        }
    }

    public int getEndPosition() {
        return breakPosition;
    }

    public boolean isEnd() {
        return breakPosition == paragraphEnd;
    }

    private void paintDebugger(Graphics2D g2d, float x, float y) {
        Color tempColor = g2d.getColor();
        g2d.setColor(JBColor.RED);
        g2d.drawString("[" + paragraphNum + (isEnd()? " End" : "") + "]", x, y);
        g2d.setColor(tempColor);
    }
}
