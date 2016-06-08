package codeflections.ideaspectrum;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorComponentImpl;

import java.awt.*;

/**
 * @author dydix
 */
public class TuneAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Component component = anActionEvent.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (component instanceof EditorComponentImpl) {
            Editor editor = ((EditorComponentImpl) component).getEditor();
            TuneDialog dialog = new TuneDialog(editor.getProject(), editor);
            dialog.show();
        }
    }
}
