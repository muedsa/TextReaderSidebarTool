package com.muedsa.intellij.textReader.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.text.AttributedString;
import java.util.Objects;

public class PaintMultiLineTextEditorBorder implements Border {
    private final GraphicsDrawDebugger debugger = new GraphicsDrawDebugger();

    private boolean immutable;

    private TextOffset textOffset;

    private BoundedMultiLineTextBox box;
    private Paragraph2[] paragraphs;

    private float lineHeight = 0;

    public PaintMultiLineTextEditorBorder(Editor editor, BoundedMultiLineTextBox box) {
        if(editor instanceof EditorImpl) {
            textOffset = new DynamicBasicTextOffset((EditorImpl) editor);
            immutable = false;
        } else {
            textOffset = new BasicTextOffset(editor.getScrollingModel().getVisibleAreaOnScrollingFinished());
            immutable = true;
        }
        this.box = box;
    }


    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        debugger.startFrame();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(box.getFont());
        g2d.setColor(box.getColor());
        initParagraph(g2d.getFontRenderContext());
        lineHeight = box.getFont().getLineMetrics(" ", g2d.getFontRenderContext()).getHeight();
        if(box.previousPageFlag()){
            measurePreviousPageAndJump();
        }
        pointText(g2d);
        debugger.endFrame();
        paintDebugger(g2d);
    }

    private void pointText(Graphics2D g2d) {
        float drawX = textOffset.getX();
        float drawY = textOffset.getY();
        float bottomBound = drawY + box.getBoundedHeight();
        int limit = 0;
        paragraphs[box.getParagraphPos()].initLines(box.getFirstParagraphTextPos(), paragraphs[box.getParagraphPos()].getParagraphEnd());
        box.setNextPageFirstParagraphTextPos(0);
        for (int i = box.getParagraphPos(); i < paragraphs.length; i++) {
            drawY = paragraphs[i].paintLine(g2d, drawX, drawY, bottomBound);
            limit++;
            if(paragraphs[i].isEnd()){
                drawY += lineHeight * box.getInterParagraphSpacingScale();
                if(drawY >= bottomBound) {
                    break;
                }
            }else{
                box.setNextPageFirstParagraphTextPos(paragraphs[i].getEndPosition());
                limit--;
                break;
            }
        }
        box.setParagraphLimit(limit);
    }

    private void reversePointText(Graphics2D g2d){
//        float drawX = textOffset.getX();
//        float drawY = textOffset.getY() + box.getBoundedHeight();
//        float limitY = textOffset.getY();
//        int limit = 0;
//        paragraphs[box.getParagraphPos()].setPosition(0);
//        for (int i = box.getParagraphPos(); i >= 0; i--) {
//            drawY = paragraphs[i].paintReverse(g2d, drawX, drawY, limitY);
//            limit++;
//            if(paragraphs[i].isEndReverse()){
//                //box.setNextPageFirstParagraphTextPos(0);
//                drawY -= lineHeight * box.getInterParagraphSpacingScale();
//                if(drawY <= limitY) {
//                    break;
//                }
//            }else{
//                //box.setNextPageFirstParagraphTextPos(paragraphs[i].getEndPosition());
//                limit--;
//                break;
//            }
//        }
    }

    private void measurePreviousPageAndJump() {
        float drawY = textOffset.getY() + box.getBoundedHeight(); // 从底部开始倒推
        float topBound = textOffset.getY();
        int lastParagraphPos;
        if(box.previousChapterFlag()){
            lastParagraphPos = box.getLines().length - 1;
        }else{
            if(box.getFirstParagraphTextPos() > 0) {
                lastParagraphPos =  box.getParagraphPos() == 0? 0 : box.getParagraphPos() - 1;
                paragraphs[lastParagraphPos].initLines(paragraphs[lastParagraphPos].getParagraphStart(), box.getFirstParagraphTextPos());
            }else{
                lastParagraphPos =  box.getParagraphPos() - 1;
            }
        }
        boolean finish = false;
        int previousPageParagraphPos = 0;
        int previousPageFirstParagraphTextPos = 0;
        for (int paragraphPos = lastParagraphPos; paragraphPos >= 0; paragraphPos--) {
            Paragraph2 paragraph = paragraphs[paragraphPos];
            for (int linePos = paragraph.getLinesCount() - 1; linePos >= 0; linePos--) {
                float lineHeight = paragraph.measureLineHeight(linePos);
                drawY -= lineHeight;
                if(drawY < topBound) {
                    if(linePos == paragraph.getLinesCount()) {
                        //最后一行
                        previousPageParagraphPos = paragraphPos + 1;
                    }else{
                        previousPageParagraphPos = paragraphPos;
                        previousPageFirstParagraphTextPos = paragraph.getCharacterCount(linePos - 1);
                    }
                    finish = true;
                }
            }
            if(finish){
                break;
            }
            drawY -= lineHeight * box.getInterParagraphSpacingScale();
        }
        box.setPreviousPagePosParams(previousPageParagraphPos, previousPageFirstParagraphTextPos);
    }

    private void initParagraph(FontRenderContext fontRenderContext){
        if(!immutable || Objects.isNull(paragraphs)) {
            String[] lines = box.getLines();
            paragraphs = new Paragraph2[lines.length];
            for (int i = 0; i < lines.length; i++) {
                paragraphs[i] = new Paragraph2(i + 1,
                        new AttributedString(lines[i], box.getFont().getAttributes()),
                        fontRenderContext,
                        box.getBoundedWidth(),
                        box.getInterlineSpacingScale());
            }
        }
    }

    private void paintDebugger(Graphics2D g2d) {
        Color tempColor = g2d.getColor();
        g2d.setColor(JBColor.RED);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        float drawX = textOffset.getX() + box.getBoundedWidth() + 10;
        float drawY = textOffset.getY() + box.getBoundedHeight() + 10;
        drawY += fontMetrics.getAscent();
        g2d.drawString(debugger.formatAvgFps("Avg FPS: %.2f"), drawX, drawY);
        drawY += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        g2d.drawString(debugger.formatAvgFrameTime("Avg Frame Time: %.2f"), drawX, drawY);
        g2d.setColor(tempColor);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return JBUI.emptyInsets();
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
