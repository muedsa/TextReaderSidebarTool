package com.muedsa.intellij.textReader.composes;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBList;
import com.muedsa.intellij.textReader.Chapter;
import com.muedsa.intellij.textReader.TextFile;
import com.muedsa.intellij.textReader.factory.NotificationFactory;
import com.muedsa.intellij.textReader.file.TextFileChooserDescriptor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.IOException;
import java.util.Vector;

public class ReaderWindow {
    private JPanel readerPanel;
    private JBList<Chapter> titleList;
    private JButton fileButton;
    private JSpinner fontSizeSpinner;
    private JButton previousButton;
    private JButton nextButton;
    private JTextArea textContent;
    private JTabbedPane tab;
    private JScrollPane textContentScroll;
    private JTextField chapterPrefix;
    private JTextField chapterSuffix;

    private Project project;
    private ToolWindow toolWindow;

    private TextFile textFile;

    public ReaderWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        new NotificationFactory(project);
        createUIComponents();
    }

    private void createUIComponents(){
        SpinnerModel spinnerModel = new SpinnerNumberModel(12, 0, 100, 1);

        fontSizeSpinner.setModel(spinnerModel);

        fontSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float fontSize = (float)((int)fontSizeSpinner.getValue());
                textContent.setFont(textContent.getFont().deriveFont(fontSize));
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
                    try {
                        textFile = new TextFile(file);
                        Vector<Chapter> list = Chapter.getChapters(textFile, chapterPrefix.getText(), chapterSuffix.getText());
                        titleList.setListData(list);
                    }
                    catch (IOException error){
                        error.printStackTrace();
                        NotificationFactory.sendNotify("文件读取错误", error.getLocalizedMessage(), NotificationType.ERROR);
                    }
                }
            }
        });

        titleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    setTextContent();
                    tab.setSelectedIndex(1);
                }
            }
        });

        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int count = titleList.getItemsCount();
                int index = titleList.getSelectedIndex() + 1;
                if(index + 1 <= count){
                    titleList.setSelectedIndex(index);
                    setTextContent();
                }
            }

        });

        previousButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = titleList.getSelectedIndex() - 1;
                if(index >= 0){
                    titleList.setSelectedIndex(index);
                    setTextContent();
                }
            }
        });
    }

    public JPanel getContent(){
        return readerPanel;
    }

    private void setTextContent(){
        Chapter chapter = titleList.getSelectedValue();
        if(chapter != null){
            textContent.setText(Chapter.getChapterContent(textFile, chapter));
            textContent.setCaretPosition(0);
        }
    }

}
