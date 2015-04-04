package codeflections.typengo;

import com.intellij.openapi.actionSystem.AbbreviationManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author dyadix
 */
public class ActionFinder {

    public static class ActionInfo {
        private AnAction action;
        private String abbreviation;

        public ActionInfo(AnAction action, String abbreviation) {
            this.action = action;
            this.abbreviation = abbreviation;
        }

        public AnAction getAction() {
            return action;
        }

        public String getAbbreviation() {
            return abbreviation;
        }
    }

    @Nullable
    public static AnAction findAction(String abbreviation) {
        String actionId = null;
        List<String> abbrActionIds = AbbreviationManager.getInstance().findActions(abbreviation);
        if (abbrActionIds.size() == 1) {
            actionId = abbrActionIds.get(0);
        }
        return actionId != null ? ActionManager.getInstance().getAction(actionId) : null;
    }

    public static Collection<ActionInfo> findActions(String typedStr) {
        Set<String> abbreviations = AbbreviationManager.getInstance().getAbbreviations();
        List<ActionInfo> foundActions = new ArrayList<>();
        for (String abbr : abbreviations) {
            if (abbr.startsWith(typedStr)) {
                AnAction found = findAction(abbr);
                if (found != null) {
                    foundActions.add(new ActionInfo(found, abbr));
                }
            }
        }
        return foundActions;
    }
}
