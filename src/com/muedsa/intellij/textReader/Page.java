//package com.muedsa.intellij.textReader;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//
//public class Page {
//
//    private Integer previousOffset;
//
//    private Integer offset;
//
//    private StringBuilder content;
//
//    private Integer pageRows;
//
//    private Integer pageColumns;
//
//    private RandomAccessFile book;
//    private Charset charset;
//
//    public Page(Integer offset, Integer width, Integer height, Integer fontSize, Integer rowHeight, Charset charset, String filePath)throws IOException {
//        this.previousOffset = offset;
//        this.offset = offset;
//        pageColumns = (int) (width / (fontSize * 1.2));
//        pageRows =  (int) (height / (rowHeight * 1.1));
//        content = new StringBuilder();
//        this.charset = charset;
//        book = new RandomAccessFile(new File(filePath), "r");
//    }
//
//    public String nextPage(){
//        try{
//            book.seek(offset);
//            previousOffset = offset;
//            Integer rows = 0;
//            while(rows < pageRows){
//                String lineContent = new String(book.readLine().getBytes(StandardCharsets.ISO_8859_1), charset);
//                Integer tempRows = (int)Math.ceil(lineContent.length() / pageColumns);
//                Integer needRows = pageRows - tempRows;
//                if(needRows < tempRows){
//                    rows += needRows;
//                    lineContent = lineContent.substring(0, pageColumns * needRows);
//                }else{
//                    rows += tempRows;
//                }
//                content.append(lineContent);
//                content.append("\r\n");
//            }
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//        offset += content.toString().getBytes().length;
//        return content.toString();
//    }
//
//    public void previousPage(){
//        offset = previousOffset;
//
//        previousOffset = previousOffset - content.toString().getBytes().length;
//    }
//}
