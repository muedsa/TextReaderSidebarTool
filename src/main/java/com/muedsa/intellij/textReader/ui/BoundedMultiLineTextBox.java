package com.muedsa.intellij.textReader.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.muedsa.intellij.textReader.bus.ConfigChangeNotifier;
import com.muedsa.intellij.textReader.core.TextReaderCore;
import com.muedsa.intellij.textReader.core.bus.ChapterChangeNotifier;
import com.muedsa.intellij.textReader.state.TextReaderConfigStateService;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class BoundedMultiLineTextBox {
    private static final BoundedMultiLineTextBox INSTANCE = new BoundedMultiLineTextBox(Disposer.newDisposable());

    private TextReaderCore textReaderCore;

    private boolean isMultiLine;
    private int boundedWidth; // 边界宽度
    private int boundedHeight; // 边界高度
    private Font font; //字体
    private Color color; //字体颜色
    private float interlineSpacingScale;   //行间距
    private float interParagraphSpacingScale; //段落间距

    private int readLineSize;

    private String text;

    private String[] paragraphs; // 章节段落

    private int paragraphPos;   // 当前页首段的段落位置

    private int paragraphLimit; // 当前页段落数量

    private int firstParagraphTextPos;  // 当前页第一段的文本位置
    private int nextPageFirstParagraphTextPos; // 下一页第一段的文本位置

    private boolean previousPageFlag;

    private boolean previousChapterFlag;


    private BoundedMultiLineTextBox(Disposable disposable) {
        TextReaderConfigStateService config = TextReaderConfigStateService.getInstance();
        textReaderCore = TextReaderCore.getInstance();
        font = new Font(config.getFontFamily(), Font.PLAIN, config.getFontSize());
        color = config.getReaderLineColor();
        interlineSpacingScale = (float) config.getLineSpace();
        interParagraphSpacingScale = (float) config.getParagraphSpace();
        boundedWidth = config.getMultiLineTextBoxWidth();
        boundedHeight = config.getMultiLineTextBoxHeight();
        isMultiLine = config.isMultiLineTextBox();
        readLineSize = config.getReaderLineSize();
        registerConfigChange(disposable);
        registerChapterChange(disposable);
        updateText();
    }
    private void registerConfigChange(Disposable disposable) {
        ApplicationManager.getApplication().getMessageBus().connect(disposable).subscribe(ConfigChangeNotifier.TOPIC,
                (key, data, source) -> {
                    switch (key) {
                        case FONT_FAMILY:
                            font = new Font((String) data, Font.PLAIN, font.getSize());
                        case FONT_SIZE:
                            font = new Font(font.getFamily(), Font.PLAIN, (int) data);
                            break;
                        case READER_LINE_COLOR:
                            color = (Color) data;
                            break;
                        case LINE_SPACE:
                            interlineSpacingScale = (float) data;
                            break;
                        case PARAGRAPH_SPACE:
                            interParagraphSpacingScale = (float) data;
                            break;
                        case MULTI_LINE_TEXT_BOX:
                            isMultiLine = (boolean) data;
                            text = textReaderCore.nextLine(readLineSize);
                            updateText();
                            break;
                        case MULTI_LINE_TEXT_BOX_WIDTH:
                            boundedWidth = (int) data;
                            break;
                        case MULTI_LINE_TEXT_BOX_HEIGHT:
                            boundedHeight = (int) data;
                            break;
                        case READER_LINE_SIZE:
                            readLineSize = (int) data;
                            break;
                    }
                });
    }

    private void registerChapterChange(Disposable disposable) {
        ApplicationManager.getApplication().getMessageBus().connect(disposable).subscribe(ChapterChangeNotifier.TOPIC,
                chapter -> updateText());
    }

    private void updateText() {
        if(isMultiLine){
            try {
                text = textReaderCore.readChapterContent(0);
            } catch (IOException e){
                text = e.getMessage();
            }
        }else{
            if(Objects.isNull(text)) text = "";
        }
        paragraphs = text.split("\n");
        paragraphPos = 0;
        paragraphLimit = 0;
        firstParagraphTextPos = 0;
        nextPageFirstParagraphTextPos = 0;
    }


    public int getParagraphPos() {
        return paragraphPos;
    }

    public void setPreviousPagePosParams(int paragraphPos, int firstParagraphTextPos) {
        if(previousPageFlag) {
            this.paragraphPos = paragraphPos;
            this.firstParagraphTextPos = firstParagraphTextPos;
            clearPreviousPageFlag();
        }
    }

    private void clearPreviousPageFlag(){
        previousPageFlag = false;
        previousChapterFlag = false;
    }

    public void setParagraphLimit(int paragraphLimit) {
        this.paragraphLimit = paragraphLimit;
    }

    public String[] getLines() {
        return paragraphs;
    }

    public void next(){
        if(isMultiLine){
            nextPage();
        } else {
            text = textReaderCore.nextLine(readLineSize);
            updateText();
        }
    }

    private void nextPage() {
        if(paragraphPos + paragraphLimit < paragraphs.length) {
            paragraphPos = paragraphPos + paragraphLimit;
            firstParagraphTextPos = nextPageFirstParagraphTextPos;
            nextPageFirstParagraphTextPos = 0;
        }else{
            textReaderCore.nextChapter();
        }
    }

    public void previous(){
        if(isMultiLine) {
            previousPage();
        } else {
            text = textReaderCore.previousLine(readLineSize);
            updateText();
        }
    }

    public void previousPage(){
        if(paragraphPos == 0) {
            previousChapterFlag = true;
            textReaderCore.previousChapter();
        }
        previousPageFlag = true;
    }

    public int getFirstParagraphTextPos() {
        return firstParagraphTextPos;
    }

    public void setNextPageFirstParagraphTextPos(int nextPageFirstParagraphTextPos) {
        this.nextPageFirstParagraphTextPos = nextPageFirstParagraphTextPos;
    }

    public boolean previousPageFlag() {
        return previousPageFlag;
    }

    public boolean previousChapterFlag() {
        return previousChapterFlag;
    }

    public int getBoundedWidth() {
        return boundedWidth;
    }

    public int getBoundedHeight() {
        return boundedHeight;
    }

    public Font getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

    public float getInterlineSpacingScale() {
        return interlineSpacingScale;
    }

    public float getInterParagraphSpacingScale() {
        return interParagraphSpacingScale;
    }

    public static BoundedMultiLineTextBox getInstance() {
        return INSTANCE;
    }
}
