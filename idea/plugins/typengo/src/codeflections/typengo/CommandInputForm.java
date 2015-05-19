package codeflections.typengo;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.IdeFrame;
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
    private JPanel topPanel;
    private JTextField commandField;
    private final Component sourceComponent;
    private AnActionEvent originalEvent;
    private String currTyped;
    private final JPopupMenu popupMenu;

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
