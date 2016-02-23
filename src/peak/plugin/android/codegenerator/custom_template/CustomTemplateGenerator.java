package peak.plugin.android.codegenerator.custom_template;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiMethod;
import peak.plugin.android.codegenerator.CodeGeneratorDialog;
import peak.plugin.android.codegenerator.utils.ActionUtils;
import peak.plugin.android.codegenerator.base.BaseGenerator;
import peak.plugin.android.codegenerator.utils.CommonUtils;
import peak.plugin.android.codegenerator.utils.reflection.JavaClassExecuter;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by wenping on 2016/2/17.
 */
public class CustomTemplateGenerator extends BaseGenerator implements CodeGeneratorDialog.CustomTemplateListener {
    private String javaFileFullPath;
    private String javaFileDir;
    private String classFilePath;
    private List<MethodPart> methodPartList = new ArrayList<>();
    private DefaultTableModel tableModel;

    public CustomTemplateGenerator(CodeGeneratorDialog dialog, AnActionEvent event) {
        super(dialog, event);

        dialog.setCustomTemplateListener(this);

        getTemplatePath();
        getMethodList();
        updateTable(false);
    }

    private void getMethodList() {
        PsiMethod[] methods = psiClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            methodPartList.add(new MethodPart(false, methods[i]));
        }
    }

    private void getTemplatePath() {
        javaFileFullPath = ActionUtils.getFileFullPath(psiFile, false);
        javaFileDir = ActionUtils.getFileFullPath(psiFile, true);
        classFilePath = javaFileFullPath.replace(".java", ".class");
    }

    @Override
    public void generateCode() {
        if (CommonUtils.makeJavaFile(javaFileFullPath) == 0) {
            executeJavaCode();
        } else {
            dialog.textCode.setText("Can not make this java file!");
        }
        CommonUtils.deleteFileIfExists(classFilePath);
    }

    public void executeJavaCode() {
        try {
            InputStream is = new FileInputStream(classFilePath);
            byte[] b = new byte[is.available()];
            is.read(b);
            is.close();
            String result = "";

            for (int i = 0; i < methodPartList.size(); i++) {
                MethodPart part = methodPartList.get(i);
                if (part.isSelected()) {
                    result += JavaClassExecuter.execute(b, part.getName()) + "\n\n";
                }
            }
            dialog.textCode.setText(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTable(boolean toGenerateCode) {
        if (methodPartList == null || methodPartList.size() == 0) {
            return;
        }
        int size = methodPartList.size();
        String[] headers = new String[]{"selected", "method"};

        Object[][] cellData = new Object[size][headers.length];
        for (int i = 0; i < size; i++) {
            MethodPart methodPart = methodPartList.get(i);
            for (int j = 0; j < headers.length; j++) {
                switch (j) {
                    case 0:
                        cellData[i][j] = methodPart.isSelected();
                        break;
                    case 1:
                        cellData[i][j] = methodPart.getName();
                        break;
                }
            }
        }
        tableModel = new DefaultTableModel(cellData, headers) {
            final Class[] typeArray = {Boolean.class, Object.class};

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @SuppressWarnings("rawtypes")
            public Class getColumnClass(int column) {
                return typeArray[column];
            }
        };
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent event) {
                int row = event.getFirstRow();
                int column = event.getColumn();
                if (column == 0) {
                    Boolean isSelected = (Boolean) tableModel.getValueAt(row, column);
                    methodPartList.get(row).setSelected(isSelected);

                    generateCode();
                }
            }
        });
        dialog.setTabCustomTemplateModel(tableModel);

        if (toGenerateCode) {
            generateCode();
        }
    }

    @Override
    public void onSelectAll() {
        for (MethodPart methodPart : methodPartList) {
            methodPart.setSelected(true);
        }
        updateTable(true);
    }

    @Override
    public void onSelectNone() {
        for (MethodPart methodPart : methodPartList) {
            methodPart.setSelected(false);
        }
        updateTable(true);
    }

    @Override
    public void onNegativeSelect() {
        for (MethodPart methodPart : methodPartList) {
            methodPart.setSelected(!methodPart.isSelected());
        }
        updateTable(true);
    }
}