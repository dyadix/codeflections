package codeflections.typengo;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;

/**
 * @author dyadix
 */
public class CommandInputForm extends JDialog {
    private final static String TYPE_N_GO_PLACE = "TypeNGo Plugin";

    private JPanel topPanel;
    private JTextField commandField;
    private final Component sourceComponent;
    private AnActionEvent originalEvent;
    private String currTyped;
    private final JPopupMenu popupMenu;
    private final Project project;

    private static CommandInputForm currInstance;

    private CommandInputForm(Component sourceComponent, AnActionEvent originalEvent) {
        this.setUndecorated(true);
        this.sourceComponent = sourceComponent;
        this.originalEvent = originalEvent;
        this.add(topPanel);
        this.pack();
        this.setModal(true);
        popupMenu = new JPopupMenu();
        topPanel.setComponentPopupMenu(popupMenu);
        this.setAlwaysOnTop(true);
        commandField.setRequestFocusEnabled(true);
        commandField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        KeyStroke escKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        commandField.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.setVisible(false);
                CommandInputForm.this.setVisible(false);
                CommandInputForm.this.dispose();
                currTyped = null;
            }
        }, escKeyStroke, JComponent.WHEN_FOCUSED);
        commandField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                AnAction action = null;
                boolean isCommandTyped = false;
                if (Character.isLetter(c) || c == '-' || c == '+') {
                    currTyped = commandField.getText();
                    if (currTyped != null) {
                        currTyped += c;
                        action = ActionFinder.findAction(currTyped);
                    }
                    isCommandTyped = true;
                } else {
                    popupMenu.setVisible(false);
                }
                if (action != null) {
                    popupMenu.setVisible(false);
                    CommandInputForm.this.setVisible(false);
                    CommandInputForm.this.dispose();
                    invokeAction(action);
                    currTyped = null;
                } else {
                    if (isCommandTyped && currTyped != null) {
                        popupMenu.setVisible(false);
                        updatePopup(popupMenu, currTyped);
                        Point location = commandField.getLocationOnScreen();
                        location = new Point(location.x, location.y + commandField.getHeight());
                        popupMenu.setLocation(location);
                        popupMenu.setVisible(true);
                    } else {
                        popupMenu.setVisible(false);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        project = originalEvent.getProject();
    }

    private void invokeAction(final AnAction action) {
        if (action == null) return;
        JFrame ideFrame = project != null ? WindowManager.getInstance().getFrame(project) : null;
        ActionManager.getInstance().tryToExecute(action, originalEvent.getInputEvent(), sourceComponent, TYPE_N_GO_PLACE, true);
        if (ideFrame != null) ideFrame.requestFocus();
    }

    public static void show(Component sourceComponent, AnActionEvent originalEvent) {
        if (currInstance != null) {
            currInstance.setVisible(false);
            currInstance.dispose();
        }
        currInstance = new CommandInputForm(sourceComponent, originalEvent);
        currInstance.centerOnIdeFrameOrScreen(originalEvent);
        currInstance.setVisible(true);
    }

    public static boolean isShown() {
        return currInstance != null && currInstance.isVisible();
    }

    private void updatePopup(@NotNull JPopupMenu popupMenu, @NotNull String typedStr) {
        popupMenu.removeAll();
        Collection<ActionFinder.ActionInfo> foundActions = ActionFinder.findActions(typedStr);
        for (ActionFinder.ActionInfo actionInfo: foundActions) {
            Presentation presentation = actionInfo.getAction().getTemplatePresentation();
            StringBuilder sb = new StringBuilder();
            sb.append("<html><b>").append(actionInfo.getAbbreviation()).append("</b>&nbsp;&nbsp;");
            String desc = presentation.getDescription();
            if (desc != null && !desc.isEmpty()) {
                sb.append(desc);
            }
            else {
                String text = presentation.getText();
                if (text != null && !text.isEmpty()) {
                    sb.append(text);
                }
            }
            sb.append("</html>");
            popupMenu.add(new JMenuItem(sb.toString()));
        }
    }

    private void centerOnIdeFrameOrScreen(@NotNull AnActionEvent actionEvent) {
        WindowManagerEx windowManager = WindowManagerEx.getInstanceEx();
        IdeFrame frame = windowManager.getFrame(actionEvent.getProject());
        int x = 0;
        int y = 0;
        if (frame != null) {
            Component frameComponent = frame.getComponent();
            if (frameComponent != null) {
                Point origin = frameComponent.getLocationOnScreen();
                x = (int)(origin.getX() + (frameComponent.getWidth() - this.getWidth()) / 2);
                y = (int)(origin.getY() + (frameComponent.getHeight() - this.getHeight()) / 2);
            }
        }
        else {
            Rectangle screenBounds = windowManager.getScreenBounds();
            x = (int)(screenBounds.getX()  + (screenBounds.getWidth() - this.getWidth()) / 2);
            y = (int)(screenBounds.getY() + (screenBounds.getHeight() - this.getHeight()) / 2);
        }
        this.setLocation(x, y);
    }
}
