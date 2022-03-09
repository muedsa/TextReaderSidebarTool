package com.muedsa.intellij.textReader.file;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;

public class TextFileChooserDescriptor extends FileChooserDescriptor {
    public TextFileChooserDescriptor() {
        super(true, false, false, false, false, false);
    }

    @Override
    public boolean isFileSelectable(VirtualFile file) {
        return file != null && file.getExtension() != null && "txt".equalsIgnoreCase(file.getExtension());
    }
}
