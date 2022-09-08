package com.muedsa.intellij.textReader.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.muedsa.intellij.textReader.state.TextReaderConfigStateService;
import com.muedsa.intellij.textReader.util.ReaderLineUtil;
import org.jetbrains.annotations.NotNull;

public class PreviousLineAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ReaderLineUtil.previousLine(TextReaderConfigStateService.getInstance(), e.getProject());
    }
}
