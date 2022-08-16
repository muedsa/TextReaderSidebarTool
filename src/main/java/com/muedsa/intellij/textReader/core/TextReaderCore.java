package com.muedsa.intellij.textReader.core;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.muedsa.intellij.textReader.core.config.TextReaderConfig;
import com.muedsa.intellij.textReader.core.event.ChapterChangeEvent;
import com.muedsa.intellij.textReader.core.event.ChaptersClearEvent;
import com.muedsa.intellij.textReader.core.event.TextReaderEventManage;
import com.muedsa.intellij.textReader.core.util.ChapterUtil;
import com.muedsa.intellij.textReader.state.TextReaderStateService;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

public class TextReaderCore {

    private final static TextReaderCore INSTANCE = new TextReaderCore();
    private final TextReaderEventManage eventManage = new TextReaderEventManage();
    private TextReaderStateService textReaderStateService;

    private TextFile textFile;
    private Vector<Chapter> chapterList = new Vector<>(0);
    private int chapterIndex = 0;

    private String noBlankChapterText = "";
    private int positionInChapter = 0;

    private TextReaderCore() {
        initStateService();
    }

    public void initStateService(){
        if(textReaderStateService == null){
            textReaderStateService = TextReaderStateService.getInstance();
            if(textReaderStateService != null && textReaderStateService.getFilePath() != null && textReaderStateService.getChapters() != null){
                try {
                    String filePath = textReaderStateService.getFilePath();
                    if(StringUtils.isNotBlank(filePath)){
                        initWithFileAndChapters(filePath, textReaderStateService.getChapters());
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

    public String readChapterContent() throws IOException {
        String text = "";
        Chapter chapter = getChapter();
        if(chapter != null){
            text = ChapterUtil.formatChapterContent(textFile, chapter);
            noBlankChapterText = text.replaceFirst("\n", "##").replaceAll("\\s*", "");
            positionInChapter = 0;
        }
        return text;
    }

    public String nextLine(){
        String line;
        if(StringUtils.isEmpty(noBlankChapterText) || positionInChapter > noBlankChapterText.length()){
            if(nextChapter() < 0){
                line = "读取下一章失败!";
                return line;
            }
        }
        line = StringUtils.mid(noBlankChapterText, positionInChapter, TextReaderConfig.getReaderLineSize());
        positionInChapter += TextReaderConfig.getReaderLineSize();
        if(StringUtils.isAllBlank(line)){
            line = nextLine();
        }
        return line;
    }

    public String previousLine(){
        String line;
        if(positionInChapter - TextReaderConfig.getReaderLineSize() == 0){
            if(previousChapter() < 0){
                line = "读取上一章失败!";
                positionInChapter = 0;
                return line;
            }
            positionInChapter = noBlankChapterText.length() - TextReaderConfig.getReaderLineSize();
        }else{
            positionInChapter -= TextReaderConfig.getReaderLineSize() * 2;
            if(positionInChapter < 0){
                positionInChapter = 0;
            }
        }
        line = nextLine();
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
        textReaderStateService.setFilePath(textFile == null? "" : textFile.getFilePath());
        textReaderStateService.setChapters(chapterList);
    }

    public boolean isReady(){
        return textFile != null && chapterList != null && chapterList.size() > 0;
    }
}
