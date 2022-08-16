package com.muedsa.intellij.textReader.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.muedsa.intellij.textReader.core.config.TextReaderConfig;
import com.muedsa.intellij.textReader.util.ReaderLineUtil;
import org.jetbrains.annotations.NotNull;

public class EmptyLineAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ReaderLineUtil.clear(e.getProject(), TextReaderConfig.isShowReaderLintAtStatusBar());
    }
}
