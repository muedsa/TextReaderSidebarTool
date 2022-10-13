package com.muedsa.intellij.textReader.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.muedsa.intellij.textReader.state.TextReaderConfigStateService;
import com.muedsa.intellij.textReader.util.ReaderPageLineUtil;

public class NextLineAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ReaderPageLineUtil.nextLine(TextReaderConfigStateService.getInstance(), e.getProject());
    }
}
