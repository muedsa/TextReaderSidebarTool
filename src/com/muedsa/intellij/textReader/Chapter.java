package com.muedsa.intellij.textReader;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.vfs.VirtualFile;
import com.muedsa.intellij.textReader.composes.ReaderWindow;
import info.monitorenter.cpdetector.io.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

public class Chapter {
    private int startOffset;
    private String title;

    public Chapter(int startOffset, String title) {
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

    @Override
    public String toString() {
        return "●" + title;
    }

    public static Vector<Chapter> getChapters(VirtualFile file, ReaderWindow readerWindow){
        Vector<Chapter> list = new Vector<>();
        int offset = 0;
        try{
            CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
            detector.add(JChardetFacade.getInstance());
            Charset charset = detector.detectCodepage(file.getInputStream(), 200);
            InputStreamReader reader = new InputStreamReader(file.getInputStream(), charset);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineContent = null;
            while ((lineContent = bufferedReader.readLine()) != null){
                offset += lineContent.getBytes(file.getCharset()).length;
                int startIndex = lineContent.indexOf("第");
                int endIndex =  lineContent.indexOf("章");
                if (startIndex >= 0 && endIndex > 0 && endIndex > startIndex && endIndex - startIndex < 10) {
                    list.add(new Chapter(offset, lineContent.trim()));
                }
            }
            readerWindow.sendNotify("加载成功", file.getPath()+"<br><em>"+charset.displayName()+"</em> 共"+list.size()+"章", NotificationType.INFORMATION);
        }
        catch (IOException e){
            e.printStackTrace();
            readerWindow.sendNotify("加载失败", e.getLocalizedMessage(), NotificationType.WARNING);
        }
        return list;
    }
}
