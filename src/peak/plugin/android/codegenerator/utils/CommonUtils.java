package peak.plugin.android.codegenerator.utils;

import java.io.DataInputStream;
import java.io.File;

/**
 * Created by wenping on 2016/1/31.
 */
public class CommonUtils {
    public static String upperCaseFirstLetter(String word) {
        if (null == word || word.equals("")) {
            return word;
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String lowerCaseFirstLetter(String word) {
        if (null == word || word.equals("")) {
            return word;
        }
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }

    /**
     * Get field name of a bean class from a widget name. <br>
     *
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

    /**
     * Make java file and product .class file using javac.
     * @param javaFileFullPath
     * @return
     */
    public static int makeJavaFile(String javaFileFullPath) {
        int ret = 0;

        if (javaFileFullPath != null) {
            deleteFileIfExists(javaFileFullPath.replace(".java", ".class"));
            try {
                Runtime rt = Runtime.getRuntime();
                Process ps = rt.exec("cmd /c javac " + javaFileFullPath);
                ps.waitFor();
                byte[] out = new byte[1024];
                DataInputStream dos = new DataInputStream(ps.getInputStream());
                dos.read(out);
                String s = new String(out);
                if (s.indexOf("Exception") > 0) {
                    ret = -1;
                }
            } catch (Exception e) {
                ret = -1;
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static boolean deleteFileIfExists(final String fileName) {
        if (fileName == null) {
            return false;
        }
        File file = new File(fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
