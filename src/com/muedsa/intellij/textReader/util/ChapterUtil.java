package com.muedsa.intellij.textReader.util;

import com.intellij.notification.NotificationType;
import com.muedsa.intellij.textReader.Chapter;
import com.muedsa.intellij.textReader.TextFile;
import com.muedsa.intellij.textReader.factory.NotificationFactory;
import com.muedsa.intellij.textReader.io.MyBufferedReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChapterUtil {

    public static final String LF_CR = "\n\r";
    public static final String LF = "\n";
    public static final char HALF_WIDTH_SPACE = ' ';
    public static final char FULL_WIDTH_SPACE = '　';

    public static int CONFIG_PARAGRAPH_SPACE = 1;

    public static Vector<Chapter> getChapters(TextFile textFile, int maxLineSize, Pattern pattern) {
        Vector<Chapter> list = new Vector<>();
        int offset = 0;
        try {
            InputStreamReader reader = new InputStreamReader(textFile.getInputStream(), textFile.getCharset());
            MyBufferedReader bufferedReader = new MyBufferedReader(reader);
            String lineContent;
            Chapter previousChapter = null;
            while ((lineContent = bufferedReader.readLineWithCRLF()) != null){
                if(lineContent.length() <= maxLineSize){
                    Matcher matcher = pattern.matcher(lineContent);
                    if(matcher.find()){
                        if(previousChapter != null){
                            previousChapter.setLength(offset - previousChapter.getStartOffset());
                        }else if(offset > 0){
                            list.add(new Chapter(0, offset, "章节前部分"));
                        }
                        previousChapter = new Chapter(offset, lineContent.trim());
                        list.add(previousChapter);
                    }
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
        catch (PatternSyntaxException error){
            error.printStackTrace();
            NotificationFactory.sendNotify("正则错误", error.getLocalizedMessage(), NotificationType.ERROR);
        }
        return list;
    }

    public static String getChapterContent(TextFile textFile, Chapter chapter){
        String content = null;
        RandomAccessFile file = null;
        try{
            file = new RandomAccessFile(textFile.getFilePath(), "r");
            file.skipBytes(chapter.getStartOffset());
            byte[] bytes = new byte[chapter.getLength()];
            file.read(bytes);
            content = new String(bytes, textFile.getCharset());
        }
        catch (IOException e){
            e.printStackTrace();
            NotificationFactory.sendNotify("文件读取错误", e.getLocalizedMessage(), NotificationType.ERROR);
        }
        finally{
            if(file != null){
                try{
                    file.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                    NotificationFactory.sendNotify("文件读取close错误", e.getLocalizedMessage(), NotificationType.ERROR);
                }

            }
        }
        return content;
    }

    public static String formatChapterContent(TextFile textFile, Chapter chapter){
        String chapterContent = getChapterContent(textFile, chapter);
        return formatChapterContent(chapterContent);
    }

    public static String formatChapterContent(String chapterContent){
        chapterContent = StringUtils.replace(chapterContent, LF_CR, LF);
        String[] paragraphs = StringUtils.split(chapterContent, LF);
        StringBuilder formatContentBuilder = new StringBuilder(chapterContent.length());
        for(String paragraph : paragraphs){
            if(StringUtils.isNotBlank(paragraph)){
                String newParagraph = trim(paragraph);
                formatContentBuilder.append(newParagraph);
                int i = 0;
                while(CONFIG_PARAGRAPH_SPACE + 1 > 0 && i < CONFIG_PARAGRAPH_SPACE + 1){
                    formatContentBuilder.append(LF);
                    i++;
                }
            }
        }
        return formatContentBuilder.toString();
    }

    public static String trim(String text) {
        char[] val = text.toCharArray();
        int st = 0;
        int len = val.length;

        while ((st < len) && (val[st] <= HALF_WIDTH_SPACE || val[st] == FULL_WIDTH_SPACE)) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= HALF_WIDTH_SPACE || val[st] == FULL_WIDTH_SPACE)) {
            len--;
        }
        return ((st > 0) || (len < val.length)) ? text.substring(st, len) : text;
    }
}
