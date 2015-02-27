package codeflections.typengo;

import com.intellij.openapi.actionSystem.AbbreviationManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author dyadix
 */
public class ActionFinder {

    @Nullable
    public static AnAction findAction(String abbreviation) {
        String actionId = null;
        List<String> abbrActionIds = AbbreviationManager.getInstance().findActions(abbreviation);
        if (abbrActionIds.size() == 1) {
            actionId = abbrActionIds.get(0);
        }
        return actionId != null ? ActionManager.getInstance().getAction(actionId) : null;
    }
}
