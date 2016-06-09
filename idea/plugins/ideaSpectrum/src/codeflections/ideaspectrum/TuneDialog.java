package codeflections.ideaspectrum;

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
        centerPanel.setLayout(new FlowLayout());
        JButton brighter = new JButton("B+");
        JButton darker = new JButton("B-");
        brighter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeBrightness(+5);
            }
        });
        darker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeBrightness(-5);
            }
        });
        centerPanel.add(darker, BorderLayout.WEST);
        centerPanel.add(brighter, BorderLayout.EAST);
        return centerPanel;
    }
    
    private void changeBrightness(int change) {
        setSchemeToModify();
        changeBrightness(HighlighterColors.TEXT, change);
        changeBrightness(DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR, change);
        changeBrightness(DefaultLanguageHighlighterColors.IDENTIFIER, change);
        changeBrightness(DefaultLanguageHighlighterColors.STRING, change);
        changeBrightness(DefaultLanguageHighlighterColors.KEYWORD, change);
        changeBrightness(DefaultLanguageHighlighterColors.BLOCK_COMMENT, change);
        changeBrightness(DefaultLanguageHighlighterColors.LINE_COMMENT, change);
        changeBrightness(DefaultLanguageHighlighterColors.DOC_COMMENT, change);
        changeBrightness(DefaultLanguageHighlighterColors.OPERATION_SIGN, change);
        changeBrightness(DefaultLanguageHighlighterColors.DOT, change);
        changeBrightness(DefaultLanguageHighlighterColors.SEMICOLON, change);
        changeBrightness(DefaultLanguageHighlighterColors.COMMA, change);
        changeBrightness(DefaultLanguageHighlighterColors.LABEL, change);
        changeBrightness(DefaultLanguageHighlighterColors.CONSTANT, change);
        changeBrightness(DefaultLanguageHighlighterColors.LOCAL_VARIABLE, change);
        changeBrightness(DefaultLanguageHighlighterColors.GLOBAL_VARIABLE, change);
        changeBrightness(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION, change);
        changeBrightness(DefaultLanguageHighlighterColors.FUNCTION_CALL, change);
        changeBrightness(DefaultLanguageHighlighterColors.PARAMETER, change);
        changeBrightness(DefaultLanguageHighlighterColors.CLASS_NAME, change);
        changeBrightness(DefaultLanguageHighlighterColors.INTERFACE_NAME, change);
        changeBrightness(DefaultLanguageHighlighterColors.CLASS_REFERENCE, change);
        changeBrightness(DefaultLanguageHighlighterColors.INSTANCE_METHOD, change);
        changeBrightness(DefaultLanguageHighlighterColors.INSTANCE_FIELD, change);
        changeBrightness(DefaultLanguageHighlighterColors.STATIC_METHOD, change);
        changeBrightness(DefaultLanguageHighlighterColors.STATIC_FIELD, change);
        changeBrightness(DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP, change);
        changeBrightness(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG, change);
        changeBrightness(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE, change);
        changeBrightness(DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE, change);
        changeBrightness(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE, change);
        changeBrightness(DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL, change);
        changeBrightness(DefaultLanguageHighlighterColors.METADATA, change);
        changeBrightness(DefaultLanguageHighlighterColors.MARKUP_TAG, change);
        changeBrightness(DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE, change);
        changeBrightness(DefaultLanguageHighlighterColors.MARKUP_ENTITY, change);
        changeBrightness(DefaultLanguageHighlighterColors.BRACES, change);
        changeBrightness(DefaultLanguageHighlighterColors.BRACKETS, change);
        changeBrightness(DefaultLanguageHighlighterColors.PARENTHESES, change);
        changeBrightness(EditorColors.CARET_ROW_COLOR, change);
        rehighlight();
    }
    
    private void changeBrightness(@NotNull TextAttributesKey key, int change) {
        TextAttributes textAttributes = this.scheme.getAttributes(key).clone();
        if (textAttributes == null) textAttributes = new TextAttributes();
        Color background = textAttributes.getBackgroundColor();
        if (background == null) {
            background = this.scheme.getDefaultBackground();
        }
        textAttributes.setBackgroundColor(changeBrightness(background, change));
        Color foreground = textAttributes.getForegroundColor();
        if (foreground == null) {
            foreground = this.scheme.getDefaultForeground();
        }
        textAttributes.setForegroundColor(changeBrightness(foreground, change));
        scheme.setAttributes(key, textAttributes);
    }
    
    private void changeBrightness(@NotNull ColorKey key, int change) {
        Color color = this.scheme.getColor(key);
        if (color == null) color = key.getDefaultColor();
        scheme.setColor(key, changeBrightness(color, change));
    }
    
    private Color changeBrightness(@NotNull Color color, int change) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int newR = adjustComponent(r, change);
        int newG = adjustComponent(g, change);
        int newB = adjustComponent(b, change);
        return new Color(newR , newG, newB);
    }
    
    private int adjustComponent(int component, int change) {
        return Math.min(255, (int)((component * (100. + change))/100 + 0.5));
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
