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
    private final JPopupMenu popupMenu;

    private static CommandInputForm currInstance;

    private CommandInputForm(Point location, Component sourceComponent, AnActionEvent originalEvent) {
        this.setUndecorated(true);
        this.sourceComponent = sourceComponent;
        this.originalEvent = originalEvent;
        this.setLocation(location);
        this.add(topPanel);
        this.pack();
        popupMenu = new JPopupMenu("Foo");
        popupMenu.add(new JMenuItem("More"));
        popupMenu.add(new JMenuItem("COmmands"));
        topPanel.setComponentPopupMenu(popupMenu);
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
                    /*
                    Point location = commandField.getLocationOnScreen();
                    location = new Point(location.x, location.y + commandField.getHeight());
                    popupMenu.setLocation(location);
                    popupMenu.setVisible(true);
                    */
                }
                else {
                    popupMenu.setVisible(false);
                }
                if (action != null || isEscape) {
                    CommandInputForm.this.setVisible(false);
                    CommandInputForm.this.dispose();
                    invokeAction(action);
                    currTyped = null;
                    popupMenu.setVisible(false);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

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
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ActionUtil.performActionDumbAware(action, createNewEvent(action));
            }
        });
    }

    public static void show(Point location, Component sourceComponent, AnActionEvent originalEvent) {
        if (currInstance != null) {
            currInstance.setVisible(false);
            currInstance.dispose();
        }
        currInstance = new CommandInputForm(location, sourceComponent, originalEvent);
        currInstance.setVisible(true);
    }

    public static boolean isShown() {
        return currInstance != null && currInstance.isVisible();
    }
}
