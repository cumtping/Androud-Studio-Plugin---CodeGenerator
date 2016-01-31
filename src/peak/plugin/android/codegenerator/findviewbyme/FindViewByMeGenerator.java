package peak.plugin.android.codegenerator.findviewbyme;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.http.util.TextUtils;
import org.xml.sax.SAXException;
import peak.plugin.android.codegenerator.CodeGeneratorDialog;
import peak.plugin.android.codegenerator.utils.CommonUtils;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by wenping on 2016/1/31.
 */
public class FindViewByMeGenerator implements CodeGeneratorDialog.FindViewByMeListener{
    private boolean isAddRootView;
    private boolean isViewHolder;
    private ViewSaxHandler viewSaxHandler;
    private List<ViewPart> viewParts;
    private DefaultTableModel tableModel;
    CodeGeneratorDialog dialog;
    AnActionEvent event;

    public FindViewByMeGenerator(CodeGeneratorDialog dialog, AnActionEvent event) {
        isAddRootView = false;
        isViewHolder = false;
        viewSaxHandler = new ViewSaxHandler();
        this.dialog = dialog;
        this.event = event;

        dialog.setFindViewByMeListener(this);
        getViewList();
        updateTable();
    }
    /**
     * 获取View列表
     *
     * @param event 触发事件
     */
    public void getViewList() {
        PsiFile psiFile = getXmlPisFile(event);//event.getData(LangDataKeys.PSI_FILE);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return;
        }
        String contentStr = psiFile.getText();
        if (psiFile.getParent() != null) {
            viewSaxHandler.setLayoutPath(psiFile.getContainingDirectory().toString().replace("PsiDirectory:", ""));
            viewSaxHandler.setProject(event.getProject());
        }
        try {
            viewSaxHandler.createViewList(contentStr);
            viewParts = viewSaxHandler.getViewPartList();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PsiFile getXmlPisFile(AnActionEvent event) {
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        if (null == psiFile || null == editor) {
            return null;
        }

        SelectionModel sel = editor.getSelectionModel();
        if (sel != null) {
            String classFilePath = psiFile.getContainingDirectory().toString().replace("PsiDirectory:", "");
            String xmlFileName = sel.getSelectedText();
            String xmlFilePath = classFilePath.substring(0, classFilePath.indexOf("java")) + "res\\layout\\" + xmlFileName + ".xml";
            System.out.println(xmlFilePath);
            File file = new File(xmlFilePath);
            if (file.exists()) {
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
                if (virtualFile == null) {
                    return psiFile;
                }
                return  PsiManager.getInstance(event.getProject()).findFile(virtualFile);
            }
        }
        return  psiFile;
    }

    /**
     * 更新 View 表格
     */
    public void updateTable() {
        if (viewParts == null || viewParts.size() == 0) {
            return;
        }
        int size = viewParts.size();
        String[] headers = {"selected", "click", "bean", "type", "id", "name"};
        Object[][] cellData = new Object[size][headers.length];
        for (int i = 0; i < size; i++) {
            ViewPart viewPart = viewParts.get(i);
            for (int j = 0; j < headers.length; j++) {
                switch (j) {
                    case 0:
                        cellData[i][j] = viewPart.isSelected();
                        break;
                    case 1:
                        cellData[i][j] = viewPart.isClick();
                        break;
                    case 2:
                        cellData[i][j] = viewPart.isBean();
                        break;
                    case 3:
                        cellData[i][j] = viewPart.getType();
                        break;
                    case 4:
                        cellData[i][j] = viewPart.getId();
                        break;
                    case 5:
                        cellData[i][j] = viewPart.getName();
                        break;
                }
            }
        }
        tableModel = new DefaultTableModel(cellData, headers) {
            final Class[] typeArray = {Boolean.class, Boolean.class, Boolean.class, Object.class, Object.class, Object.class};

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 1 || column == 2;
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
                if (column == 0 || column == 1 || column == 2) {
                    if (column == 0) {
                        Boolean isSelected = (Boolean) tableModel.getValueAt(row, column);
                        viewSaxHandler.getViewPartList().get(row).setSelected(isSelected);
                    } else if (column == 1) {
                        Boolean isClick = (Boolean) tableModel.getValueAt(row, column);
                        viewSaxHandler.getViewPartList().get(row).setClick(isClick);
                    } else if (column == 2) {
                        Boolean isBean = (Boolean) tableModel.getValueAt(row, column);
                        viewSaxHandler.getViewPartList().get(row).setBean(isBean);
                    }
                    generateCode();
                }
            }
        });
        dialog.setModel(tableModel);
        generateCode();
    }

    /**
     * 生成FindViewById代码
     */
    private void generateCode() {
        String javaBean = dialog.getJavaBean();
        String javaBeanUppercase = "";
        String javaBeanLowercase = "";
        if (javaBean != null && !javaBean.equals("") ) {
            javaBeanUppercase = CommonUtils.upperCaseFirstLetter(javaBean);
            javaBeanLowercase = CommonUtils.lowerCaseFirstLetter(javaBean);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (ViewPart viewPart : viewParts) {
            if (viewPart.isSelected()) {
                stringBuilder.append(viewPart.getDeclareString(isViewHolder));
            }
        }
        stringBuilder.append("\n");
        for (ViewPart viewPart : viewParts) {
            if (viewPart.isSelected()) {
                if (isViewHolder) {
                    stringBuilder.append(viewPart.getFindViewStringForViewHolder("convertView"));
                    if(viewPart.isClick()) {
                        stringBuilder.append(viewPart.getSetOnClickListenerrForViewHolder());
                    }
                    if (viewPart.isBean()) {
                        String filedName = CommonUtils.getBeanFieldFromWidget(viewPart.getName(), true);
                        stringBuilder.append(viewPart.getSetTextWithViewHolder(viewPart.getName(), javaBeanLowercase + ".get" + filedName));
                        stringBuilder.append(viewPart.getGetTextWithViewHolder(viewPart.getName(), javaBeanLowercase, filedName));
                    }
                } else if (isAddRootView && !TextUtils.isEmpty(dialog.getRootView())) {
                    stringBuilder.append(viewPart.getFindViewStringWithRootView(dialog.getRootView()));
                    if(viewPart.isClick()) {
                        stringBuilder.append(viewPart.getSetOnClickListenerWithRootView(dialog.getRootView()));
                    }
                    if (viewPart.isBean()) {
                        String filedName = CommonUtils.getBeanFieldFromWidget(viewPart.getName(), true);
                        stringBuilder.append(viewPart.getSetTextWithRootView(dialog.getRootView(), viewPart.getName(), javaBeanLowercase + ".get" + filedName));
                        stringBuilder.append(viewPart.getGetTextWithRootView(dialog.getRootView(), viewPart.getName(), javaBeanLowercase, filedName));
                    }
                } else {
                    stringBuilder.append(viewPart.getFindViewString());
                    if (viewPart.isClick()) {
                        stringBuilder.append(viewPart.getSetOnClickListener());
                    }
                    if (viewPart.isBean()) {
                        String filedName = CommonUtils.getBeanFieldFromWidget(viewPart.getName(), true);
                        stringBuilder.append(viewPart.getSetText(viewPart.getName(), javaBeanLowercase + ".get" + filedName));
                        stringBuilder.append(viewPart.getGetText(viewPart.getName(), javaBeanLowercase, filedName));
                    }
                }
            }
        }
        // bean
        if (javaBean != null && !javaBean.equals("")) {
            stringBuilder.append("\n").append(javaBeanUppercase).append("{\n");

            for (ViewPart viewPart : viewParts) {
                if (viewPart.isSelected() && viewPart.isBean()) {
                    String filedName = CommonUtils.getBeanFieldFromWidget(viewPart.getName(), false);
                    stringBuilder.append("    ").append("String ").append(filedName).append(";\n");
                }
            }
            stringBuilder.append("}\n");
        }
        dialog.setTextCode(stringBuilder.toString());
    }

    @Override
    public void onAddRootView() {
        generateCode();
    }

    @Override
    public void onSwitchAddRootView(boolean isAddRootView) {
        this.isAddRootView = isAddRootView;
    }

    @Override
    public void onSwitchAddM(boolean addM) {
        switchViewName(addM);
    }

    @Override
    public void onSwitchIsViewHolder(boolean isViewHolder) {
        this.isViewHolder = isViewHolder;
        generateCode();
    }

    @Override
    public void onSelectAll() {
        for (ViewPart viewPart : viewParts) {
            viewPart.setSelected(true);
        }
        updateTable();
    }

    @Override
    public void onSelectNone() {
        for (ViewPart viewPart : viewParts) {
            viewPart.setSelected(false);
        }
        updateTable();
    }

    @Override
    public void onNegativeSelect() {
        for (ViewPart viewPart : viewParts) {
            viewPart.setSelected(!viewPart.isSelected());
        }
        updateTable();
    }


    /**
     * 切换控件名称
     *
     * @param isAddM 是否添加"m"
     */
    private void switchViewName(boolean isAddM) {
        if (isAddM) {
            for (ViewPart viewPart : viewParts) {
                viewPart.addM2Name();
            }
        } else {
            for (ViewPart viewPart : viewParts) {
                viewPart.resetName();
            }
        }
        updateTable();
    }

}