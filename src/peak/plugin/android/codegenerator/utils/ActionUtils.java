package peak.plugin.android.codegenerator.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;

import javax.swing.*;

/**
 * Created by wenping on 2016/1/31.
 */
public class ActionUtils {
    /**
     * Show a JDialog.
     * @param dialog
     * @param title
     * @param event
     * @return
     */
    public static JDialog showDialog(JDialog dialog, String title, AnActionEvent event) {
        dialog.setTitle(title);
        dialog.pack();
        dialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(event.getProject()));
        dialog.setVisible(true);

        return dialog;
    }

    /**
     * Get psi class.
     * @param e
     * @return
     */
    public static PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        if (psiFile == null || editor == null) {
            return null;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);

        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }

    /**
     * Get current file name without extension.
     * @param psiFile
     * @return
     */
    public static String getSimpleName(final PsiFile psiFile) {
        if (psiFile == null) {
            return null;
        }
        return psiFile.getVirtualFile().getNameWithoutExtension();
    }

    /**
     * Get selection text from AnActionEvent.
     * @param event
     * @return
     */
    public static String getSelection(AnActionEvent event) {
        if (event != null) {
            Editor editor = event.getData(PlatformDataKeys.EDITOR);
            if (editor != null) {
                SelectionModel sel = editor.getSelectionModel();
                if (sel != null) {
                    return sel.getSelectedText();
                }
            }
        }
        return null;
    }

    public static String getJavaClass(final PsiClass psiClass) {
        if (psiClass == null) {
            return null;
        }
        if (psiClass.getContainingFile() instanceof  PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
            return javaFile.getPackageName() + "." + javaFile.getName().replace(".java", "");
        }
        return null;
    }

    public static String getFileFullPath(PsiFile psiFile, boolean onlyDir) {
        if (psiFile == null) {
            return null;
        }
        return psiFile.getContainingDirectory().toString().replace("PsiDirectory:", "") +
                "\\" + (onlyDir ? "" : psiFile.getVirtualFile().getName());
    }
}
