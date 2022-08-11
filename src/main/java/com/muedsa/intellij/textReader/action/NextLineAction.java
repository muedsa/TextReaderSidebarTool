package com.muedsa.intellij.textReader.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.muedsa.intellij.textReader.util.ReaderLineUtil;

public class NextLineAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ReaderLineUtil.nextLine(e.getProject());
    }
}
