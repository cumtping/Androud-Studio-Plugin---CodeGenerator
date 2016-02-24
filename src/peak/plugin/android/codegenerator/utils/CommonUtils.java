package peak.plugin.android.codegenerator.utils;

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
     *
     * @param javaFileFullPath
     * @return
     */
    public static int makeJavaFile(String javaFileFullPath, String fileEncoding) {
        int exitValue = 0;

        if (javaFileFullPath != null) {
            deleteFileIfExists(javaFileFullPath.replace(".java", ".class"));

            String fileEncodingParam = "";
            if (fileEncoding != null && !fileEncoding.trim().equals("")) {
                fileEncodingParam = " -encoding " + fileEncoding + " ";
            }
            String command = "cmd /c javac " + fileEncodingParam + javaFileFullPath;
            try {
                exitValue = ProcessUtils.executeCommandWithExecutors(command, true, true, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return exitValue;
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
