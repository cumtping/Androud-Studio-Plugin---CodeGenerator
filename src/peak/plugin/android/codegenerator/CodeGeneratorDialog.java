package peak.plugin.android.codegenerator;

import peak.plugin.android.codegenerator.new_activity_instance.NewActivityInstanceGenerator;
import peak.plugin.android.codegenerator.utils.BaseJDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by wenping on 2016/1/31.
 */
public class CodeGeneratorDialog extends BaseJDialog{

    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JPanel tabFindViewByMe;
    private JCheckBox chbAddRootView;
    private JTextField textRootView;
    private JCheckBox chbAddM;
    private JButton btnAddRootView;
    private JCheckBox chbIsViewHolder;
    private JTable tableViews;
    private JButton btnSelectNone;
    private JButton btnNegativeSelect;
    private JButton btnSelectAll;
    public JTextArea textCode;
    private JTextField textBeanClass;
    private JButton btnCopyCode;
    private JButton btnClose;
    private JPanel tabNewActivityInstance;
    private JCheckBox chbForResult;
    private JTable tableField;

    private FindViewByMeListener findViewByMeListener;
    private CodeGeneratorListener codeGeneratorListener;
    private NewActivityInstanceListener newActivityInstanceListener;

    public interface CodeGeneratorListener{
        void onCopyCode();
        void onFinish();
        void onCreate();
    }

    public interface FindViewByMeListener {
        void onAddRootView();
        void onSwitchAddRootView(boolean isAddRootView);
        void onSwitchAddM(boolean addM);
        void onSwitchIsViewHolder(boolean isViewHolder);
        void onSelectAll();
        void onSelectNone();
        void onNegativeSelect();
        void onConditionChanged();
    }

    public interface NewActivityInstanceListener{
        void onSelectAll();
        void onSelectNone();
        void onNegativeSelect();
        void onConditionChanged();
    }

    public CodeGeneratorDialog() {
        setContentPane(contentPane);
        setModal(true);

        textRootView.setEnabled(false);
        btnAddRootView.setEnabled(false);
        chbAddRootView.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean isAdd = chbAddRootView.isSelected();
                if (findViewByMeListener != null) {
                    findViewByMeListener.onSwitchAddRootView(isAdd);
                }
                textRootView.setEnabled(isAdd);
                btnAddRootView.setEnabled(isAdd);
            }
        });
        chbIsViewHolder.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (findViewByMeListener != null) {
                    findViewByMeListener.onSwitchIsViewHolder(chbIsViewHolder.isSelected());
                }
            }
        });
        chbAddM.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (findViewByMeListener != null) {
                    findViewByMeListener.onSwitchAddM(chbAddM.isSelected());
                }
            }
        });
        btnAddRootView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (findViewByMeListener != null) {
                    findViewByMeListener.onAddRootView();
                }
            }
        });
        btnCopyCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (codeGeneratorListener != null) {
                    codeGeneratorListener.onCopyCode();
                    onCancel();
                }
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        btnSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFindViewByMeTabSelected() && findViewByMeListener != null) {
                    findViewByMeListener.onSelectAll();
                } else if (isNewActivityInstanceTabSelected() && newActivityInstanceListener != null) {
                    newActivityInstanceListener.onSelectAll();
                }
            }
        });
        btnSelectNone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFindViewByMeTabSelected() && findViewByMeListener != null) {
                    findViewByMeListener.onSelectNone();
                } else if (isNewActivityInstanceTabSelected() && newActivityInstanceListener != null) {
                    newActivityInstanceListener.onSelectNone();
                }
            }
        });
        btnNegativeSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFindViewByMeTabSelected() && findViewByMeListener != null) {
                    findViewByMeListener.onNegativeSelect();
                } else if (isNewActivityInstanceTabSelected() && newActivityInstanceListener != null) {
                    newActivityInstanceListener.onNegativeSelect();
                }
            }
        });
        chbForResult.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (newActivityInstanceListener != null) {
                    newActivityInstanceListener.onConditionChanged();
                }
            }
        });

        tabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (isFindViewByMeTabSelected() && findViewByMeListener != null) {
                    findViewByMeListener.onConditionChanged();
                } else if (isNewActivityInstanceTabSelected() && newActivityInstanceListener != null) {
                    newActivityInstanceListener.onConditionChanged();
                }
            }
        });
    }

    private boolean isFindViewByMeTabSelected() {
        String title = tabbedPane1.getTitleAt(tabbedPane1.getSelectedIndex());
        return "FindViewByMe".equals(title.trim());
    }

    private boolean isNewActivityInstanceTabSelected() {
        String title = tabbedPane1.getTitleAt(tabbedPane1.getSelectedIndex());
        return "NewActivityInstance".equals(title.trim());
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        if (codeGeneratorListener != null){
            codeGeneratorListener.onFinish();
        }
    }

    public CodeGeneratorListener getCodeGeneratorListener() {
        return codeGeneratorListener;
    }

    public void setCodeGeneratorListener(CodeGeneratorListener codeGeneratorListener) {
        this.codeGeneratorListener = codeGeneratorListener;
    }

    public void setModel(DefaultTableModel model) {
        tableViews.setModel(model);
        tableViews.getColumnModel().getColumn(0).setPreferredWidth(20);
    }

    public void setFieldModel(DefaultTableModel model) {
        tableField.setModel(model);
        tableField.getColumnModel().getColumn(0).setPreferredWidth(20);
    }

    public void setTextCode(String codeStr) {
        textCode.setText(codeStr);
    }

    public String getRootView() {
        return textRootView.getText();
    }

    public String getJavaBean() {
        return textBeanClass.getText();
    }

    public FindViewByMeListener getFindViewByMeListener() {
        return findViewByMeListener;
    }

    public void setFindViewByMeListener(FindViewByMeListener findViewByMeListener) {
        this.findViewByMeListener = findViewByMeListener;
    }

    public void setNewActivityInstanceListener(NewActivityInstanceListener l) {
        newActivityInstanceListener = l;
    }

    public boolean isForResult() {
        return chbForResult.isSelected();
    }
}
