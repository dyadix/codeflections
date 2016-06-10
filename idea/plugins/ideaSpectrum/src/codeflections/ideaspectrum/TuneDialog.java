package codeflections.ideaspectrum;

import codeflections.ideaspectrum.algorithms.BrightnessAdjustmentAlgorithm;
import codeflections.ideaspectrum.algorithms.ContrastAdjustmentAlgorithm;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.editor.colors.impl.ReadOnlyColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.FileContentUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author dyadix
 */
@SuppressWarnings("UseJBColor")
class TuneDialog extends DialogWrapper {

    private Editor editor;
    private EditorColorsScheme scheme;
    private Project project;

    TuneDialog(@NotNull Project project, @NotNull Editor editor) {
        super(project);
        this.editor = editor;
        this.scheme = EditorColorsManager.getInstance().getGlobalScheme();
        this.project = project;
        init();
    }
    
    private void setSchemeToModify() {
        if (scheme instanceof ReadOnlyColorsScheme) {
            EditorColorsScheme newScheme = (EditorColorsScheme)this.scheme.clone();
            newScheme.setName(this.scheme.getName() + " adjusted");
            EditorColorsManager.getInstance().addColorsScheme(newScheme);
            EditorColorsManager.getInstance().setGlobalScheme(newScheme);
            this.scheme = newScheme;
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(
                createButtonPanel(
                        "B+", 
                        "B-",
                        new BrightnessAdjustmentAlgorithm(+5),
                        new BrightnessAdjustmentAlgorithm(-5)
                ),
                BorderLayout.NORTH
        );
        centerPanel.add(
                createButtonPanel(
                        "C+", 
                        "C-",
                        new ContrastAdjustmentAlgorithm(1.05f),
                        new ContrastAdjustmentAlgorithm(0.95f)
                ),
                BorderLayout.CENTER
        );
        return centerPanel;
    }
    
    private JPanel createButtonPanel(@NotNull String incLabel, 
                                     @NotNull String decLabel, 
                                     @NotNull final ColorAdjustmentAlgorithm incAlgorithm,
                                     @NotNull final ColorAdjustmentAlgorithm decAlgorithm) {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        JButton incButton = new JButton(incLabel);
        JButton decButton = new JButton(decLabel);
        incButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustColors(incAlgorithm);
            }
        });
        decButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustColors(decAlgorithm);
            }
        });
        centerPanel.add(decButton, BorderLayout.WEST);
        centerPanel.add(incButton, BorderLayout.EAST);
        return centerPanel;
    }
    
    private void adjustColors(ColorAdjustmentAlgorithm algorithm) {
        setSchemeToModify();
        adjustColors(HighlighterColors.TEXT, algorithm, true, true);
        adjustColors(DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR, algorithm, false, true);
        adjustColors(DefaultLanguageHighlighterColors.IDENTIFIER, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.STRING, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.KEYWORD, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.BLOCK_COMMENT, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.LINE_COMMENT, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.DOC_COMMENT, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.OPERATION_SIGN, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.DOT, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.SEMICOLON, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.COMMA, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.LABEL, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.CONSTANT, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.LOCAL_VARIABLE, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.GLOBAL_VARIABLE, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.FUNCTION_CALL, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.PARAMETER, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.CLASS_NAME, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.INTERFACE_NAME, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.CLASS_REFERENCE, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.INSTANCE_METHOD, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.INSTANCE_FIELD, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.STATIC_METHOD, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.STATIC_FIELD, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG, algorithm, true, true);
        adjustColors(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.METADATA, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.MARKUP_TAG, algorithm, true, true);
        adjustColors(DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.MARKUP_ENTITY, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.BRACES, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.BRACKETS, algorithm);
        adjustColors(DefaultLanguageHighlighterColors.PARENTHESES, algorithm);
        adjustColors(EditorColors.CARET_ROW_COLOR, algorithm);
        rehighlight();
    }
    
    private void adjustColors(
            @NotNull TextAttributesKey key, 
            @NotNull ColorAdjustmentAlgorithm algorithm) {
        adjustColors(key, algorithm, true, false);
    }
    
    private void adjustColors(
            @NotNull TextAttributesKey key, 
            @NotNull ColorAdjustmentAlgorithm algorithm,
            boolean adjustForeground,
            boolean adjustBackground) {
        TextAttributes textAttributes = this.scheme.getAttributes(key);
        textAttributes = textAttributes != null ? textAttributes.clone() : new TextAttributes();
        if (adjustForeground) {
            Color foreground = textAttributes.getForegroundColor();
            if (foreground == null) {
                foreground = this.scheme.getDefaultForeground();
            }
            textAttributes.setForegroundColor(algorithm.adjust(foreground));
        }
        if (adjustBackground) {
            Color background = textAttributes.getBackgroundColor();
            if (background == null) {
                background = this.scheme.getDefaultBackground();
            }
            textAttributes.setBackgroundColor(algorithm.adjust(background));
        }
        scheme.setAttributes(key, textAttributes);
    }
    
    private void adjustColors(@NotNull ColorKey key, ColorAdjustmentAlgorithm algorithm) {
        Color color = this.scheme.getColor(key);
        if (color == null) color = key.getDefaultColor();
        scheme.setColor(key, algorithm.adjust(color));
    }
    
    private void rehighlight() {
        PsiFile file = PsiDocumentManager.getInstance(project).getCachedPsiFile(editor.getDocument());
        if (file != null) {
            Collection<VirtualFile> filesToReparse = new ArrayList<VirtualFile>();
            filesToReparse.add(file.getVirtualFile());
            FileContentUtilCore.reparseFiles(filesToReparse);
        }
    }
    
}
