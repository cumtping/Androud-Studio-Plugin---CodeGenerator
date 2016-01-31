package peak.plugin.android.codegenerator.findviewbyme;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Jaeger
 * 15/11/25
 */
public class ViewPart {
    // declare
    private static final String OUTPUT_DECLARE_STRING = "private %s %s;\n";
    private static final String OUTPUT_DECLARE_STRING_NOT_PRIVATE = "%s %s;\n";
    // find view
    private static final String OUTPUT_FIND_VIEW_STRING = "%s = (%s) findViewById(R.id.%s);\n";
    private static final String OUTPUT_FIND_VIEW_STRING_WITH_ROOT_VIEW = "%s = (%s) %s.findViewById(R.id.%s);\n";
    private static final String OUTPUT_FIND_VIEW_STRING_FOR_VIEW_HOLDER = "viewHolder.%s = (%s) %s.findViewById(R.id.%s);\n";
    // click listener
    private static final String OUTPUT_SET_ON_CLICK_LISTENER = "%s.setOnClickListener(this);\n";
    private static final String OUTPUT_SET_ON_CLICK_LISTENER_WITH_ROOT_VIEW = "%s.%s.setOnClickListener(this);\n";
    private static final String OUTPUT_SET_ON_CLICK_LISTENER_FOR_VIEW_HOLDER = "viewHolder.%s.setOnClickListener(this);\n";
    // set text
    private static final String OUTPUT_SET_TEXT = "%s.setText(%s);\n";
    private static final String OUTPUT_SET_TEXT_WITH_ROOT_VIEW = "%s.%s.setText(%s);\n";
    private static final String OUTPUT_SET_TEXT_FOR_VIEW_HOLDER = "viewHolder.%s.setText(%s);\n";
    // get text
    private static final String OUTPUT_GET_TEXT = "%s.set%s(%s.getText());\n";
    private static final String OUTPUT_GET_TEXT_WITH_ROOT_VIEW = "%s.set%s(%s.%s.getText());\n";
    private static final String OUTPUT_GET_TEXT_FOR_VIEW_HOLDER = "%s.set%s(viewHolder.%s.getText());\n";

    private String type;
    private String id;
    private String name;
    private boolean selected;
    private boolean click;
    private boolean bean;

    public ViewPart() {
        selected = true;
    }

    private void generateName(String id) {
        Pattern pattern = Pattern.compile("_([a-zA-Z])");
        Matcher matcher = pattern.matcher(id);

        char[] chars = id.toCharArray();
        while (matcher.find()) {
            int index = matcher.start(1);
            chars[index] = Character.toUpperCase(chars[index]);
        }
        String name = String.copyValueOf(chars);
        name = name.replaceAll("_", "");
        setName(name);
    }

    public boolean isClick() {
        return click;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public boolean isBean() {
        return bean;
    }

    public void setBean(boolean bean) {
        this.bean = bean;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type.contains(".")) {
            String[] strings = type.split("\\.");
            this.type = strings[strings.length - 1];
        } else {
            this.type = type;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        generateName(id);
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

    public String getDeclareString(boolean isViewHolder) {
        if (isViewHolder) {
            return String.format(OUTPUT_DECLARE_STRING_NOT_PRIVATE, type, name);
        } else {
            return String.format(OUTPUT_DECLARE_STRING, type, name);
        }
    }

    public String getDeclareStringForViewHolder() {
        return String.format(OUTPUT_DECLARE_STRING, type, name);
    }

    public String getFindViewStringWithRootView(String rootView) {
        return String.format(OUTPUT_FIND_VIEW_STRING_WITH_ROOT_VIEW, name, type, rootView, id);
    }

    public String getFindViewString() {
        return String.format(OUTPUT_FIND_VIEW_STRING, name, type, id);
    }

    public void resetName() {
        generateName(id);
    }

    public void addM2Name() {
        generateName("m_" + id);
    }

    public String getFindViewStringForViewHolder(String rootView) {
        return String.format(OUTPUT_FIND_VIEW_STRING_FOR_VIEW_HOLDER, name, type, rootView, id);
    }
    public String getSetOnClickListener() {
        return String.format(OUTPUT_SET_ON_CLICK_LISTENER, name);
    }
    public String getSetOnClickListenerWithRootView(String rootView) {
        return String.format(OUTPUT_SET_ON_CLICK_LISTENER_WITH_ROOT_VIEW,rootView, name);
    }

    public String getSetOnClickListenerrForViewHolder() {
        return String.format(OUTPUT_SET_ON_CLICK_LISTENER_FOR_VIEW_HOLDER, name);
    }

    public String getSetText(String view, String text) {
        return String.format(OUTPUT_SET_TEXT, view, text);
    }

    public String getSetTextWithRootView(String rootView, String view, String text) {
        return String.format(OUTPUT_SET_TEXT_WITH_ROOT_VIEW,rootView, view, text);
    }

    public String getSetTextWithViewHolder(String view, String text) {
        return String.format(OUTPUT_SET_TEXT_FOR_VIEW_HOLDER, view, text);
    }
    public String getGetText(String view, String bean, String field) {
        return String.format(OUTPUT_GET_TEXT, bean, field, view);
    }

    public String getGetTextWithRootView(String rootView, String view, String bean, String field) {
        return String.format(OUTPUT_GET_TEXT_WITH_ROOT_VIEW, bean, field, rootView, view);
    }

    public String getGetTextWithViewHolder(String view, String bean, String field) {
        return String.format(OUTPUT_GET_TEXT_FOR_VIEW_HOLDER, bean, field, view);
    }
}

