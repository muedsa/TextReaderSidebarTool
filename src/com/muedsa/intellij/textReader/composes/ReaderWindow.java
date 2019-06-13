package com.muedsa.intellij.textReader.composes;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBList;
import com.muedsa.intellij.textReader.Chapter;
import com.muedsa.intellij.textReader.file.TextFileChooserDescriptor;

import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;

public class ReaderWindow {
    private JPanel readerPanel;
    private JBList<Chapter> titleList;
    private JButton fileButton;
    private JSpinner fontSizeSpinner;
    private JButton previousButton;
    private JButton nextButton;
    private JTextArea textContent;

    private Project project;
    private ToolWindow toolWindow;

    public ReaderWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        createUIComponents();
    }

    private void createUIComponents(){

        readerPanel.addKeyListener(new KeyAdapter(){
            /**
             * Invoked when a key has been pressed.
             *
             * @param e
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.isAltDown() && e.getKeyCode() == KeyEvent.VK_DOWN){
                    toolWindow.hide(null);
                }
            }
        });

        fileButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualFile file = FileChooser.chooseFile(new TextFileChooserDescriptor(), project, null);
                if(file != null){
                    Vector<Chapter> list = Chapter.getChapters(file);
                    titleList.setListData(list);
                }
            }
        });

        titleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                   Chapter chapter = titleList.getSelectedValue();
                   if(chapter != null){
                       System.out.println(chapter.getStartOffset());
                   }
                }
            }
        });
    }

    public JPanel getContent(){
        return readerPanel;
    }
}
