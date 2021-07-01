package com.muedsa.intellij.textReader;

import com.intellij.openapi.vfs.VirtualFile;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.*;
import java.net.URI;
import java.net.URL;
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
        detector.add(ASCIIDetector.getInstance());
        detector.add(UnicodeDetector.getInstance());
        charset = detector.detectCodepage(new File(filePath).toURI().toURL());
    }

    public TextFile(String filePath) throws IOException {
        this.filePath = filePath;
        File file = new File(filePath);
        inputStream = new BufferedInputStream(new FileInputStream(file));
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(JChardetFacade.getInstance());
        charset = detector.detectCodepage(file.toURI().toURL());
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
