package peak.plugin.android.codegenerator.utils;

/**
 * Created by wenping on 2016/1/31.
 */
public class CommonUtils {
    public static String upperCaseFirstLetter(String word) {
        if (null == word || word.equals("")) {
            return  word;
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String lowerCaseFirstLetter(String word) {
        if (null == word || word.equals("")) {
            return  word;
        }
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }

    /**
     * Get field name of a bean class from a widget name. <br>
     * @param widgetName wiget name. eg. tvBookName
     * @return bean filed. eg. bookName
     */
    public static String getBeanFieldFromWidget(String widgetName, boolean upperFirstLetter) {
        int i;
        for (i = 0; i < widgetName.length(); i++) {
            char c = widgetName.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                break;
            }
        }
        if (i < widgetName.length()) {
            String newWidgetName = widgetName.substring(i);
            return upperFirstLetter ? newWidgetName : lowerCaseFirstLetter(newWidgetName);
        } else {
            return widgetName;
        }
    }
}
