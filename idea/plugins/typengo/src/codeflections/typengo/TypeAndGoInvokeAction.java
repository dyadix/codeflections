package codeflections.typengo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author dyadix
 */
public class TypeAndGoInvokeAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component component = anActionEvent.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (component == null) {
            component = anActionEvent.getInputEvent().getComponent();
        }
        JFrame mnemonicsInputForm = new CommandInputForm(calcPopupLocation(anActionEvent), component, anActionEvent);
        mnemonicsInputForm.setVisible(true);
    }

    private Point calcPopupLocation(@NotNull AnActionEvent actionEvent) {
        RelativePoint point = JBPopupFactory.getInstance().guessBestPopupLocation(actionEvent.getDataContext());
        return point.getScreenPoint();
    }
}
