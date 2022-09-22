package com.muedsa.intellij.textReader.ui;

import com.intellij.util.ui.JBUI;

import javax.swing.border.Border;
import java.awt.*;
import java.text.AttributedString;


public class MultiLineTextImageBorder implements Border {

    private AttributedString[] lines;

    private TextLayoutProperty textLayoutProperty;

    private TextOffset textOffset;

    private Paragraph[] paragraphs;

    private float lineHeight = 0;

    public MultiLineTextImageBorder(String text, TextLayoutProperty textLayoutProperty, TextOffset textOffset) {
        this.textLayoutProperty = textLayoutProperty;
        this.textOffset = textOffset;
        String[] lines = text.split("\n");
        this.lines = new AttributedString[lines.length];
        for (int i = 0; i < lines.length; i++) {
            if(!lines[i].isEmpty()){
                this.lines[i] = new AttributedString(lines[i], textLayoutProperty.getFont().getAttributes());
            }
        }
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setFont(textLayoutProperty.getFont());
        g2d.setColor(textLayoutProperty.getColor());
        float drawX = textOffset.getX();
        float drawY = textOffset.getY();
        initParagraph(g2d);
        if(lineHeight == 0){
            lineHeight = textLayoutProperty.getFont().getLineMetrics(" ", g2d.getFontRenderContext()).getHeight();
        }
        float breakWidth = textLayoutProperty.getBreakWidth() <= 0? width - x : textLayoutProperty.getBreakWidth();
        for (int i = 0; i < paragraphs.length; i++) {
            drawY = paragraphs[i].paint(g2d, drawX, drawY, breakWidth, textLayoutProperty.getInterlineSpacingScale());
            drawY += lineHeight * textLayoutProperty.getInterParagraphSpacingScale();
        }
    }


    private void initParagraph(Graphics2D g2d){
        if(paragraphs == null) {
            paragraphs = new Paragraph[lines.length];
            for (int i = 0; i < lines.length; i++) {
                paragraphs[i] = new Paragraph(lines[i], g2d.getFontRenderContext());
            }
        }
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
