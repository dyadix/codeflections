package com.wordpress.codeflections.typengo;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rustam Vishnyakov
 */
public class MnemonicMap {
    private final static Map<String,String> mnemonicMap = new HashMap<String,String>();

    static {
        mnemonicMap.put("fm", "ReformatCode");
        mnemonicMap.put("fi", "FindInPath");
        mnemonicMap.put("fu", "FindUsages");
        mnemonicMap.put("gs", "GotoSymbol");
        mnemonicMap.put("gf", "GotoFile");
        mnemonicMap.put("gc", "GotoClass");
        mnemonicMap.put("gl", "GotoLine");
        mnemonicMap.put("gd", "GotoDeclaration");
        mnemonicMap.put("gi", "GotoImplementations");
        mnemonicMap.put("gt", "GotoTypeDeclaration");
        mnemonicMap.put("rc", "RunConfiguration");
        mnemonicMap.put("se", "ShowSettings");
        mnemonicMap.put("su", "SurroundWith");
    }

    @Nullable
    public static AnAction findAction(String mnemonics) {
        ActionManager actionManager = com.intellij.openapi.actionSystem.ActionManager.getInstance();
        String actionName = mnemonicMap.get(mnemonics);
        return actionManager != null && actionName != null ? actionManager.getAction(actionName) : null;
    }
}
