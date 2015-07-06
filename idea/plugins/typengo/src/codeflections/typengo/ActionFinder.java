package codeflections.typengo;

import com.intellij.openapi.actionSystem.AbbreviationManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author dyadix
 */
public class ActionFinder {
    private final static Map<String,String> BUILT_IN = new HashMap<String,String>();

    static {
        //
        // General
        //
        BUILT_IN.put("ps", "ShowProjectStructureSettings");
        BUILT_IN.put("ss", "ShowSettings");
        //
        // Navigation
        //
        BUILT_IN.put("gr", "GotoRelated");
        BUILT_IN.put("gb", "GotoSuperMethod");
        BUILT_IN.put("gt", "GotoTypeDeclaration");
        BUILT_IN.put("gc", "GotoClass");
        BUILT_IN.put("gd", "GotoDeclaration");
        BUILT_IN.put("gf", "GotoFile");
        BUILT_IN.put("gs", "GotoSymbol");
        BUILT_IN.put("gl", "GotoLine");
        BUILT_IN.put("gi", "GotoImplementation");
        //
        // Editor
        //
        BUILT_IN.put("dd", "EditorDeleteLine");
        BUILT_IN.put("su", "SurroundWith");
        BUILT_IN.put("du", "EditorDuplicateLines");
        BUILT_IN.put("clp", "CloseAllUnpinnedEditors");
        BUILT_IN.put("clu", "CloseAllUnmodifiedEditors");
        BUILT_IN.put("cla", "CloseAllEditors");
        BUILT_IN.put("clo", "CloseAllEditorsButActive");
        //
        // Tool Windows
        //
        BUILT_IN.put("te", "ActivateTerminalToolWindow");
        BUILT_IN.put("pr", "ActivateProjectToolWindow");
        BUILT_IN.put("vc", "ActivateChangesToolWindow");
        //
        // Find
        //
        BUILT_IN.put("fu", "FindUsages");
        BUILT_IN.put("fp", "FindInPath");
        BUILT_IN.put("fa", "GotoAction");
        BUILT_IN.put("se", "SearchEverywhere");
        //
        // Run/Debug
        //
        BUILT_IN.put("de", "DebugClass");
        BUILT_IN.put("dc", "ChooseDebugConfiguration");
        BUILT_IN.put("ds", "Debug");
        BUILT_IN.put("rs", "Run");
        BUILT_IN.put("ru", "RunClass");
        BUILT_IN.put("rc", "ChooseRunConfiguration");
        //
        // Version control
        //
        BUILT_IN.put("up", "Vcs.UpdateProject");
        BUILT_IN.put("hi", "Vcs.ShowTabbedFileHistory");
        BUILT_IN.put("cm", "ChangesView.Commit");
        BUILT_IN.put("pu", "Vcs.Push");
        BUILT_IN.put("cp", "Vcs.CherryPick");
        BUILT_IN.put("an", "Annotate");
        BUILT_IN.put("lh", "LocalHistory.ShowHistory");
        //
        // Format
        //
        BUILT_IN.put("fm", "ReformatCode");
        BUILT_IN.put("oi", "OptimizeImports");
        //
        // Compilation
        //
        BUILT_IN.put("mk", "CompileDirty");
        //
        // Other
        //
        BUILT_IN.put("rf", "RecentFiles");
        BUILT_IN.put("cs", "ChangeCodeStyleScheme");
        BUILT_IN.put("cc", "ChangeColorScheme");
        BUILT_IN.put("km", "ChangeKeymap");
        BUILT_IN.put("lf", "ChangeLaf");
        BUILT_IN.put("vm", "ChangeView");
        BUILT_IN.put("clt", "CloseActiveTab");
    }


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
        else if (abbrActionIds.size() == 0) {
            actionId = BUILT_IN.get(abbreviation);
        }
        return actionId != null ? ActionManager.getInstance().getAction(actionId) : null;
    }

    public static Collection<ActionInfo> findActions(String typedStr) {
        Set<String> abbreviations = AbbreviationManager.getInstance().getAbbreviations();
        Map<String,ActionInfo> foundActions = new TreeMap<String,ActionInfo>();
        for (String abbr : abbreviations) {
            if (abbr.startsWith(typedStr)) {
                AnAction found = findAction(abbr);
                if (found != null && !foundActions.containsKey(abbr)) {
                    foundActions.put(abbr,new ActionInfo(found, abbr));
                }
            }
        }
        for (String abbr : BUILT_IN.keySet()) {
            if (abbr.startsWith(typedStr)) {
                AnAction found = findAction(abbr);
                if (found != null && !foundActions.containsKey(abbr)) {
                    foundActions.put(abbr,new ActionInfo(found, abbr));
                }
            }
        }
        return foundActions.values();
    }
}
