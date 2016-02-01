package peak.plugin.android.codegenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import peak.plugin.android.codegenerator.findviewbyme.FindViewByMeGenerator;
import peak.plugin.android.codegenerator.new_activity_instance.NewActivityInstanceGenerator;
import peak.plugin.android.codegenerator.utils.ActionUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * Created by wenping on 2016/1/31.
 */
public class AndroidCodeGenerator extends AnAction implements CodeGeneratorDialog.CodeGeneratorListener{
    private CodeGeneratorDialog codeGeneratorDialog;
    private AnActionEvent event;
    FindViewByMeGenerator findViewByMeGenerator;
    NewActivityInstanceGenerator newActivityInstanceGenerator;

    @Override
    public void actionPerformed(AnActionEvent e) {
        event = e;
        if (codeGeneratorDialog == null) {
            codeGeneratorDialog = new CodeGeneratorDialog();
        }
        codeGeneratorDialog.setCodeGeneratorListener(this);
        // Find view by me.
        findViewByMeGenerator = new FindViewByMeGenerator(codeGeneratorDialog, event);
        // new activity instance
        newActivityInstanceGenerator = new NewActivityInstanceGenerator(codeGeneratorDialog, event);

        ActionUtils.showDialog(codeGeneratorDialog, "Code generator", event);
    }

    @Override
    public void onCopyCode() {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(codeGeneratorDialog.textCode.getText());
        clip.setContents(tText, null);
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCreate() {

    }
}
