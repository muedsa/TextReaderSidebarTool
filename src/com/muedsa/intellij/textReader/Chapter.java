package com.muedsa.intellij.textReader;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.*;
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

    public static Vector<Chapter> getChapters(VirtualFile file){
        Vector<Chapter> list = new Vector<>();
        int offset = 0;
        try{
            InputStreamReader reader = new InputStreamReader(file.getInputStream(), file.getCharset());
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
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }
}
