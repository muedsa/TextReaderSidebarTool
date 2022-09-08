package com.muedsa.intellij.textReader.core;

import com.intellij.openapi.vfs.VirtualFile;
import com.muedsa.intellij.textReader.core.event.ChapterChangeEvent;
import com.muedsa.intellij.textReader.core.event.ChaptersClearEvent;
import com.muedsa.intellij.textReader.core.event.TextReaderEventManage;
import com.muedsa.intellij.textReader.core.util.ChapterUtil;
import com.muedsa.intellij.textReader.state.TextReaderChapterStateService;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

public class TextReaderCore {

    private final static TextReaderCore INSTANCE = new TextReaderCore();
    private final TextReaderEventManage eventManage = new TextReaderEventManage();
    private TextReaderChapterStateService textReaderChapterStateService;

    private TextFile textFile;
    private Vector<Chapter> chapterList = new Vector<>(0);
    private int chapterIndex = 0;

    private String noBlankChapterText = "";
    private int positionInChapter = 0;

    private TextReaderCore() {
        initStateService();
    }

    public void initStateService(){
        if(textReaderChapterStateService == null){
            textReaderChapterStateService = TextReaderChapterStateService.getInstance();
            if(textReaderChapterStateService != null && textReaderChapterStateService.getFilePath() != null && textReaderChapterStateService.getChapters() != null){
                try {
                    String filePath = textReaderChapterStateService.getFilePath();
                    if(StringUtils.isNotBlank(filePath)){
                        initWithFileAndChapters(filePath, textReaderChapterStateService.getChapters());
                    }
                }
                catch (IOException error){
                    error.printStackTrace();
                }
            }
        }
    }

    public void initWithFile(VirtualFile file, int maxLineSize, Pattern pattern) throws IOException {
        textFile = new TextFile(file);
        chapterList = ChapterUtil.getChapters(textFile, maxLineSize, pattern);
        chapterIndex = 0;
        noBlankChapterText = "";
        positionInChapter = 0;
        saveState();
        eventManage.notifyEvent(new ChapterChangeEvent(chapterList.get(chapterIndex)));
    }

    private void initWithFileAndChapters(String filePath, Vector<Chapter> chapterList) throws IOException {
        textFile = new TextFile(filePath);
        this.chapterList = chapterList;
        chapterIndex = 0;
        noBlankChapterText = "";
        positionInChapter = 0;
        eventManage.notifyEvent(new ChapterChangeEvent(chapterList.get(chapterIndex)));
    }

    public Vector<Chapter> getChapters(){
        return chapterList;
    }

    public Chapter getChapter(){
        Chapter chapter = null;
        if(chapterIndex >= 0 && chapterIndex < chapterList.size()){
            chapter = chapterList.get(chapterIndex);
        }
        return chapter;
    }

    public TextFile getTextFile(){
        return textFile;
    }

    public void clear(){
        textFile = null;
        chapterList = new Vector<>(0);
        saveState();
        eventManage.notifyEvent(new ChaptersClearEvent(null));
    }

    public int changeChapter(Chapter chapter) {
        int index = chapterList.indexOf(chapter);
        if(index >= 0){
            chapterIndex = index;
            eventManage.notifyEvent(new ChapterChangeEvent(chapter));
        }
        return index;
    }

    public String readChapterContent(int paragraphSpace) throws IOException {
        String text = "";
        Chapter chapter = getChapter();
        if(chapter != null){
            text = ChapterUtil.formatChapterContent(textFile, chapter, paragraphSpace);
            noBlankChapterText = text.replaceFirst("\n", "##").replaceAll("\\s*", "");
            positionInChapter = 0;
        }
        return text;
    }

    public String nextLine(int lineSize){
        String line;
        if(StringUtils.isEmpty(noBlankChapterText) || positionInChapter > noBlankChapterText.length()){
            if(nextChapter() < 0){
                line = "读取下一章失败!";
                return line;
            }
        }
        line = StringUtils.mid(noBlankChapterText, positionInChapter, lineSize);
        positionInChapter += lineSize;
        if(StringUtils.isAllBlank(line)){
            line = nextLine(lineSize);
        }
        return line;
    }

    public String previousLine(int lineSize){
        String line;
        if(positionInChapter - lineSize == 0){
            if(previousChapter() < 0){
                line = "读取上一章失败!";
                positionInChapter = 0;
                return line;
            }
            positionInChapter = noBlankChapterText.length() - lineSize;
        }else{
            positionInChapter -= lineSize * 2;
            if(positionInChapter < 0){
                positionInChapter = 0;
            }
        }
        line = nextLine(lineSize);
        return line;
    }

    public int nextChapter(){
        int count = chapterList.size();
        int index = chapterIndex + 1;
        if(index + 1 <= count){
            chapterIndex = index;
            eventManage.notifyEvent(new ChapterChangeEvent(chapterList.get(index)));
        }else{
            index = -1;
        }
        return index;
    }

    public int previousChapter(){
        int index = chapterIndex - 1;
        if(index >= 0 && index < chapterList.size()){
            chapterIndex = index;
            eventManage.notifyEvent(new ChapterChangeEvent(chapterList.get(index)));
        }else{
            index = -1;
        }
        return index;
    }

    public static TextReaderCore getInstance(){
        return INSTANCE;
    }

    public TextReaderEventManage getEventManage(){
        return eventManage;
    }

    private void saveState(){
        textReaderChapterStateService.setFilePath(textFile == null? "" : textFile.getFilePath());
        textReaderChapterStateService.setChapters(chapterList);
    }

    public boolean isReady(){
        return textFile != null && chapterList != null && chapterList.size() > 0;
    }
}
