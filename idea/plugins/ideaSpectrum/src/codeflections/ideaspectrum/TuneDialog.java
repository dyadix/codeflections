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
public class TuneDialog extends DialogWrapper {

    private Editor editor;
    private EditorColorsScheme scheme;
    private Project project;

    protected TuneDialog(@NotNull Project project, @NotNull Editor editor) {
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
                changeBrightness(1);
            }
        });
        darker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeBrightness(-1);
            }
        });
        centerPanel.add(darker, BorderLayout.WEST);
        centerPanel.add(brighter, BorderLayout.EAST);
        return centerPanel;
    }
    
    private void changeBrightness(int change) {
        setSchemeToModify();
        changeBrightness(HighlighterColors.TEXT, change);
        changeBrightness(DefaultLanguageHighlighterColors.KEYWORD, change);
        changeBrightness(DefaultLanguageHighlighterColors.BRACES, change);
        changeBrightness(DefaultLanguageHighlighterColors.BRACKETS, change);
        changeBrightness(DefaultLanguageHighlighterColors.PARENTHESES, change);
        changeBrightness(EditorColors.CARET_ROW_COLOR, change);
        rehighlight();
    }
    
    @SuppressWarnings("UseJBColor")
    private void changeBrightness(@NotNull TextAttributesKey key, int change) {
        TextAttributes textAttributes = this.scheme.getAttributes(key).clone();
        if (textAttributes == null) textAttributes = new TextAttributes();
        Color background = textAttributes.getBackgroundColor();
        if (background == null) {
            background = Color.WHITE;
        }
        textAttributes.setBackgroundColor(getAdjustedColor(background, change));
        Color foreground = textAttributes.getForegroundColor();
        if (foreground == null) {
            foreground = Color.BLACK;
        }
        textAttributes.setForegroundColor(getAdjustedColor(foreground, change));
        scheme.setAttributes(key, textAttributes);
    }
    
    @SuppressWarnings("UseJBColor")
    private void changeBrightness(@NotNull ColorKey key, int change) {
        Color color = this.scheme.getColor(key);
        if (color == null) color = Color.GRAY;
        scheme.setColor(key, getAdjustedColor(color, change));
    }
    
    private Color getAdjustedColor(@NotNull Color color, int change) {
        return change > 0 ? color.brighter() : color.darker();
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
