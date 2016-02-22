package peak.plugin.android.codegenerator.base;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import peak.plugin.android.codegenerator.CodeGeneratorDialog;
import peak.plugin.android.codegenerator.utils.ActionUtils;

/**
 * Created by wenping on 2016/1/31.
 */
public abstract class BaseGenerator {
    protected CodeGeneratorDialog dialog;
    protected AnActionEvent event;
    protected PsiClass psiClass;
    protected PsiFile psiFile;

    public BaseGenerator(CodeGeneratorDialog dialog, AnActionEvent event) {
        this.dialog = dialog;
        this.event = event;
        psiFile = event.getData(LangDataKeys.PSI_FILE);;
        psiClass = ActionUtils.getPsiClassFromContext(event);
    }

    public abstract void generateCode();
}
