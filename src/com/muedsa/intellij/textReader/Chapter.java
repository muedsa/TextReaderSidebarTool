package com.muedsa.intellij.textReader;

import com.intellij.notification.NotificationType;
import com.muedsa.intellij.textReader.factory.NotificationFactory;
import com.muedsa.intellij.textReader.io.MyBufferedReader;

import java.io.*;
import java.util.Vector;
import java.util.regex.Pattern;

public class Chapter {
    private int startOffset;
    private int length;
    private String title;

    public Chapter() {
        this.startOffset = 0;
        this.length = 0;
        this.title = null;
    }

    private Chapter(int startOffset, String title) {
        this.startOffset = startOffset;
        this.title = title;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "●" + title;
    }

    public static Vector<Chapter> getChapters(TextFile textFile, String chapterPrefix, String chapterSuffix){
        Vector<Chapter> list = new Vector<>();
        int offset = 0;
        try{
            InputStreamReader reader = new InputStreamReader(textFile.getInputStream(), textFile.getCharset());
            MyBufferedReader bufferedReader = new MyBufferedReader(reader);
            String lineContent;
            Chapter previousChapter = null;
            while ((lineContent = bufferedReader.readLineWithCRLF()) != null){
                int startIndex = lineContent.indexOf(chapterPrefix);
                int endIndex =  lineContent.indexOf(chapterSuffix);
                if (startIndex >= 0 && endIndex > 0 && endIndex > startIndex && endIndex - startIndex < 10 && lineContent.length()-endIndex < 50 && Pattern.matches("[0-9]+|[零一二三四五六七八九十百千万]+", lineContent.substring(startIndex + 1, endIndex))) {
                    if(previousChapter != null){
                        previousChapter.setLength(offset - previousChapter.getStartOffset());
                    }
                    previousChapter = new Chapter(offset, lineContent.trim());
                    list.add(previousChapter);
                }
                offset += lineContent.getBytes(textFile.getCharset()).length;
            }
            if(previousChapter != null){
                previousChapter.setLength(offset - previousChapter.getStartOffset());
            }
            NotificationFactory.sendNotify("加载成功", textFile.getFilePath()+"<br><em>"+textFile.getCharset().displayName()+"</em> 共"+list.size()+"章", NotificationType.INFORMATION);
        }
        catch (IOException e){
            e.printStackTrace();
            NotificationFactory.sendNotify("加载失败", e.getLocalizedMessage(), NotificationType.WARNING);
        }
        return list;
    }

    public static String getChapterContent(TextFile textFile, Chapter chapter){
        String content = null;
        try{
            RandomAccessFile file = new RandomAccessFile(textFile.getFilePath(), "r");
            file.skipBytes(chapter.getStartOffset());
            byte[] bytes = new byte[chapter.getLength()];
            file.read(bytes);
            content = new String(bytes, textFile.getCharset());
        }
        catch (IOException e){
            e.printStackTrace();
            NotificationFactory.sendNotify("文件读取错误", e.getLocalizedMessage(), NotificationType.ERROR);
        }
        return content;
    }
}
