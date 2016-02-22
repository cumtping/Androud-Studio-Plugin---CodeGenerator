package peak.plugin.android.codegenerator.custom_template;

import com.intellij.psi.PsiMethod;

/**
 * Created by wenping on 2016/1/31.
 */
public class MethodPart {
    private boolean selected;
    private String name;
    private PsiMethod method;

    public MethodPart(boolean selected, PsiMethod method) {
        this.selected = selected;
        this.method = method;
        name = method.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public PsiMethod getMethod() {
        return method;
    }

    public void setMethod(PsiMethod method) {
        this.method = method;
    }
}
