package codeflections.tabman;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * @author dyadix
 */
public class TabEnforcementPostprocessor implements PostFormatProcessor {
    @Override
    public PsiElement processElement(@NotNull PsiElement psiElement, @NotNull CodeStyleSettings codeStyleSettings) {
        replacesSpacesWithTabs(psiElement, psiElement.getTextRange(), codeStyleSettings);
        return psiElement;
    }

    @Override
    public TextRange processText(@NotNull PsiFile psiFile, @NotNull TextRange textRange, @NotNull CodeStyleSettings codeStyleSettings) {
        return replacesSpacesWithTabs(psiFile, textRange, codeStyleSettings);
    }

    private TextRange replacesSpacesWithTabs(@NotNull PsiElement element,
                                             @NotNull TextRange textRange,
                                             @NotNull CodeStyleSettings settings) {
        if (element.isValid()) {
            Project project = element.getProject();
            PsiFile file = element instanceof PsiFile ? (PsiFile)element : element.getContainingFile();
            CommonCodeStyleSettings.IndentOptions indentOptions = settings.getIndentOptionsByFile(file, textRange);
            if (indentOptions.USE_TAB_CHARACTER && !indentOptions.SMART_TABS) {
                PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
                Document document = documentManager.getDocument(file);
                if (document != null) {
                    documentManager.doPostponedOperationsAndUnblockDocument(document);
                    return processSpaces(document, textRange, indentOptions.TAB_SIZE);
                }
            }
        }
        return textRange;
    }

    private TextRange processSpaces(@NotNull Document document, @NotNull TextRange textRange, int tabSize) {
        TextRange resultRange = TextRange.create(textRange.getStartOffset(), textRange.getEndOffset());
        CharSequence docSequence = document.getCharsSequence();
        for (int line = 0; line < document.getLineCount(); line ++) {
            int lineStart = document.getLineStartOffset(line);
            int lineEnd = document.getLineEndOffset(line);
            if (resultRange.contains(lineStart) && resultRange.contains(lineEnd)) {
                String newLineText = fixIndent(docSequence.subSequence(lineStart, lineEnd), tabSize);
                resultRange.grown(newLineText.length() - (lineEnd - lineStart));
                document.replaceString(lineStart, lineEnd, newLineText);
            }
        }
        return resultRange;
    }

    private String fixIndent(@NotNull CharSequence lineText, int tabSize) {
        StringBuilder sb = new StringBuilder(lineText.length());
        int spaceCount = 0;
        boolean atIndent = true;
        for (int i = 0; i < lineText.length(); i ++) {
            char c = lineText.charAt(i);
            if (c == '\t') {
                sb.append(c);
            }
            else if (c == ' ') {
                if (atIndent) {
                    spaceCount++;
                    if (spaceCount >= tabSize) {
                        spaceCount = 0;
                        sb.append('\t');
                    }
                }
                else {
                    sb.append(c);
                }
            }
            else {
                if (spaceCount > 0 && atIndent) {
                    spaceCount = 0;
                    sb.append('\t');
                }
                atIndent = false;
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
