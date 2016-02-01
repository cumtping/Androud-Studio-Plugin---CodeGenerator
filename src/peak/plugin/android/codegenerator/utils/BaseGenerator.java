package peak.plugin.android.codegenerator.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import peak.plugin.android.codegenerator.CodeGeneratorDialog;

/**
 * Created by wenping on 2016/1/31.
 */
public abstract class BaseGenerator {
    protected CodeGeneratorDialog dialog;
    protected AnActionEvent event;

    public BaseGenerator(CodeGeneratorDialog dialog, AnActionEvent event) {
        this.dialog = dialog;
        this.event = event;
    }

    public abstract void generateCode();
}
