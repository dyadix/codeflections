package codeflections.typengo;

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
 * @author dyadix
 */
public class CommandInputForm extends JFrame {
    private JPanel topPanel;
    private JTextField commandField;
    private final Component sourceComponent;
    private AnActionEvent originalEvent;
    private String currTyped;

    protected CommandInputForm(Point location, Component sourceComponent, AnActionEvent originalEvent) {
        this.setUndecorated(true);
        this.sourceComponent = sourceComponent;
        this.originalEvent = originalEvent;
        this.setLocation(location);
        this.add(topPanel);
        this.pack();
        this.setAlwaysOnTop(true);
        commandField.setRequestFocusEnabled(true);
        commandField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        commandField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                AnAction action = null;
                boolean isEscape = false;
                if (c == 27) {
                    isEscape = true;
                } else if (Character.isLetter(c)) {
                    currTyped = commandField.getText();
                    if (currTyped != null) {
                        currTyped += c;
                        action = ActionFinder.findAction(currTyped);
                    }
                }
                if (action != null || isEscape) {
                    CommandInputForm.this.setVisible(false);
                    CommandInputForm.this.dispose();
                    invokeAction(action);
                    currTyped = null;
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
