package peak.plugin.android.codegenerator.new_activity_instance;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import peak.plugin.android.codegenerator.CodeGeneratorDialog;
import peak.plugin.android.codegenerator.utils.ActionUtils;
import peak.plugin.android.codegenerator.utils.BaseGenerator;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenping on 2016/1/31.
 */
public class NewActivityInstanceGenerator extends BaseGenerator implements CodeGeneratorDialog.NewActivityInstanceListener{
    PsiClass psiClass;
    private List<FieldPart> fieldParts = new ArrayList<>();
    private DefaultTableModel tableModel;

    public NewActivityInstanceGenerator(CodeGeneratorDialog dialog, AnActionEvent event) {
        super(dialog, event);

        psiClass = ActionUtils.getPsiClassFromContext(event);
        dialog.setNewActivityInstanceListener(this);
        getFieldList();
        updateTable();
    }

    @Override
    public void onSelectAll() {
        for (FieldPart fieldPart : fieldParts) {
            fieldPart.setSelected(true);
        }
        updateTable();
    }

    @Override
    public void onSelectNone() {
        for (FieldPart fieldPart : fieldParts) {
            fieldPart.setSelected(false);
        }
        updateTable();
    }

    @Override
    public void onNegativeSelect() {
        for (FieldPart fieldPart : fieldParts) {
            fieldPart.setSelected(!fieldPart.isSelected());
        }
        updateTable();
    }

    @Override
    public void onConditionChanged() {
        generateCode();
    }

    @Override
    public void generateCode() {
        boolean forResult = dialog.isForResult();
        StringBuilder stringBuilder = new StringBuilder();
        // new instance method
        stringBuilder.append("public static void newInstance");
        if (forResult) {
            stringBuilder.append("ForResult(Activity c, int reqCode, ");
        } else {
            stringBuilder.append("(Context c, ");
        }
        for (FieldPart fieldPart : fieldParts) {
            if (fieldPart.isSelected()) {
                stringBuilder.append(fieldPart.getType()).append(" ").append(fieldPart.getName()).append(", ");
            }
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        stringBuilder.append("{\n    Intent intent = new Intent();\n");

        for (FieldPart fieldPart : fieldParts) {
            if (fieldPart.isSelected()) {
                stringBuilder.append("    ").append("intent.putExtra(\"").append(fieldPart.getName()).append("\", ").
                        append(fieldPart.getName()).append("}\n");
            }
        }
        if (forResult) {
            stringBuilder.append("    c.startActivityResult(intent, ").append("reqCode").append(");\n}\n");
        } else {
            stringBuilder.append("    c.startActivity(intent);\n}\n");
        }
        // get data
        stringBuilder.append("private void getData() {\n    Intent intent = getIntent();\n    if (null == intent){\n        return;\n    }\n");
        for (FieldPart fieldPart : fieldParts) {
            if (fieldPart.isSelected()) {
                stringBuilder.append("    ").append(fieldPart.getName());
                if ("String".equals(fieldPart.getType())) {
                    stringBuilder.append("= intent.getStringExtra(\"").append(fieldPart.getName()).append("\")\n");
                } else if ("int".equals(fieldPart.getType())) {
                    stringBuilder.append("= intent.getIntExtra(\"").append(fieldPart.getName()).append("\", defaultValue)\n");
                } else if ("boolean".equals(fieldPart.getType())) {
                    stringBuilder.append("= intent.getBooleanExtra(\"").append(fieldPart.getName()).append("\", defaultValue)\n");
                } else {
                    stringBuilder.append("= intent.getParcelable / Serializable Extra(\"").append(fieldPart.getName()).append("\", defaultValue)\n");
                }
            }
        }
        stringBuilder.append("}");

        dialog.setTextCode(stringBuilder.toString());
    }

    private void getFieldList() {
        fieldParts.clear();
        if (psiClass == null) {
            return;
        }

        PsiField[] fields = psiClass.getFields();
        if (null == fields) {
            return;
        }
        for (int i = 0; i < fields.length; i++) {
            fieldParts.add(new FieldPart(true, fields[i]));
        }
    }

    private void updateTable() {
        if (fieldParts == null || fieldParts.size() == 0) {
            return;
        }
        int size = fieldParts.size();
        String[] headers = {"selected", "type", "name"};
        Object[][] cellData = new Object[size][3];
        for (int i = 0; i < fieldParts.size(); i++) {
            FieldPart fieldPart = fieldParts.get(i);
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0:
                        cellData[i][j] = fieldPart.isSelected();
                        break;
                    case 1:
                        cellData[i][j] = fieldPart.getType();
                        break;
                    case 2:
                        cellData[i][j] = fieldPart.getName();
                        break;
                }
            }
        }
        tableModel = new DefaultTableModel(cellData, headers) {
            final Class[] typeArray = {Boolean.class, Object.class, Object.class};

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
                    fieldParts.get(row).setSelected(isSelected);

                    generateCode();
                }
            }
        });
        dialog.setFieldModel(tableModel);
    }
}
