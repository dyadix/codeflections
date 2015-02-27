package com.wordpress.codeflections.typengo;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Rustam Vishnyakov
 */
public class MnemonicsInputForm extends JFrame {
    private JPanel topPanel;
    private JTextField mnemonicsField;
    private final Component sourceComponent;
    private AnActionEvent originalEvent;
    private String currMnemonics;

    protected MnemonicsInputForm(Point location, Component sourceComponent, AnActionEvent originalEvent) {
        this.setUndecorated(true);
        this.sourceComponent = sourceComponent;
        this.originalEvent = originalEvent;
        this.setLocation(location);
        this.add(topPanel);
        this.pack();
        this.setAlwaysOnTop(true);
        mnemonicsField.setRequestFocusEnabled(true);
        mnemonicsField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        mnemonicsField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                AnAction action = null;
                boolean isEscape = false;
                if (c == 27) {
                        isEscape = true;
                } else if (Character.isLetter(c)) {
                    currMnemonics = mnemonicsField.getText();
                    if (currMnemonics != null) {
                        currMnemonics += c;
                        action = MnemonicMap.findAction(currMnemonics);
                    }
                }
                if (action != null || isEscape) {
                    MnemonicsInputForm.this.setVisible(false);
                    MnemonicsInputForm.this.dispose();
                    invokeAction(action);
                    currMnemonics = null;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Ignore
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Ignore
            }
        });
    }

    private AnActionEvent createNewEvent(AnAction action) {
        final Presentation presentation = action.getTemplatePresentation().clone();
        final DataContext context = DataManager.getInstance().getDataContext(sourceComponent);
        return new AnActionEvent(originalEvent.getInputEvent(), context,
                originalEvent.getPlace(), presentation,
                com.intellij.openapi.actionSystem.ActionManager.getInstance(),
                originalEvent.getModifiers());
    }

    private void invokeAction(final AnAction action) {
        if (action == null) return;
        ApplicationManager.getApplication().invokeLater(new Runnable(){
            @Override
            public void run() {
                ActionUtil.performActionDumbAware(action, createNewEvent(action));
            }
        });
    }

}
