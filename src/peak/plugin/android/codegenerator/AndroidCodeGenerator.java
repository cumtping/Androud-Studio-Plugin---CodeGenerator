package peak.plugin.android.codegenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import peak.plugin.android.codegenerator.utils.ActionUtils;

/**
 * Created by wenping on 2016/1/31.
 */
public class AndroidCodeGenerator extends AnAction {
    private CodeGeneratorDialog codeGeneratorDialog;
    private AnActionEvent event;

    @Override
    public void actionPerformed(AnActionEvent e) {
        event = e;
        if (codeGeneratorDialog == null) {
            codeGeneratorDialog = new CodeGeneratorDialog();
        }

        ActionUtils.showDialog(codeGeneratorDialog, "Code generator", event);
    }
}
