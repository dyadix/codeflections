package com.wordpress.codeflections.typengo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Rustam Vishnyakov
 */
public class TypeAndGoInvokeAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component component = anActionEvent.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (component == null) {
            component = anActionEvent.getInputEvent().getComponent();
        }
        JFrame mnemonicsInputForm = new MnemonicsInputForm(calcPopupLocation(anActionEvent), component, anActionEvent);
        mnemonicsInputForm.setVisible(true);
    }

    private Point calcPopupLocation(@NotNull AnActionEvent actionEvent) {
        WindowManagerEx windowManager = WindowManagerEx.getInstanceEx();
        IdeFrame frame = windowManager.getFrame(actionEvent.getProject());
        int x = -1;
        int y = -1;
        if (frame != null) {
            Component frameComponent = frame.getComponent();
            if (frameComponent != null) {
                x = frameComponent.getX() + frameComponent.getWidth() / 3;
                y = frameComponent.getY() + frameComponent.getHeight() / 3;
            }
        }
        else {
            Rectangle screenBounds = windowManager.getScreenBounds();
            x = screenBounds.x + screenBounds.width / 3;
            y = screenBounds.y + screenBounds.height / 3;
        }
        return new Point(x,y);
    }
}
