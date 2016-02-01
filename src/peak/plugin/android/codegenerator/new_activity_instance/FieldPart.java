package peak.plugin.android.codegenerator.new_activity_instance;

import com.intellij.psi.PsiField;

/**
 * Created by wenping on 2016/1/31.
 */
public class FieldPart {
    private boolean selected;
    private String type;
    private String name;
    private PsiField field;

    public FieldPart(boolean selected, PsiField field) {
        this.selected = selected;
        this.field = field;
        name = field.getName();
        type = field.getType().getPresentableText();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public PsiField getField() {
        return field;
    }

    public void setField(PsiField field) {
        this.field = field;
    }
}
