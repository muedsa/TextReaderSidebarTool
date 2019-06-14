package com.muedsa.intellij.textReader;

import com.intellij.openapi.vfs.VirtualFile;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class TextFile {

    private String filePath;

    private InputStream inputStream;

    private Charset charset;

    public TextFile(VirtualFile file) throws IOException {
        filePath = file.getPath();
        inputStream = file.getInputStream();
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(JChardetFacade.getInstance());
        charset = detector.detectCodepage(inputStream, 200);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void readLine(){

    }
}
