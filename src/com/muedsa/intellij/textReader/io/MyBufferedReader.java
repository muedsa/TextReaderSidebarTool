package com.muedsa.intellij.textReader.io;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class MyBufferedReader extends BufferedReader {
    /**
     * Creates a buffering character-input stream that uses an input buffer of
     * the specified size.
     *
     * @param in A Reader
     * @param sz Input-buffer size
     * @throws IllegalArgumentException If {@code sz <= 0}
     */
    public MyBufferedReader(@NotNull Reader in, int sz) {
        super(in, sz);
    }

    /**
     * Creates a buffering character-input stream that uses a default-sized
     * input buffer.
     *
     * @param in A Reader
     */
    public MyBufferedReader(@NotNull Reader in) {
        super(in);
    }

    /**
     * 读取一行，返回带着换行符的一行
     * @return
     * @throws IOException
     */
    public String readLineWithCRLF() throws IOException {
        StringBuilder content = new StringBuilder();
        int read = super.read();
        if(read >= 0){
            while (true){
                if(read == '\n'){
                    content.append('\n');
                    break;
                }
                if(read < 0){
                    break;
                }
                content.append((char)read);
                read = super.read();
            }
        }else{
            return null;
        }
        return content.toString();
    }
}
