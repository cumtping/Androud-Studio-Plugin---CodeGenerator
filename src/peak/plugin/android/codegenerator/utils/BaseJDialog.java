package peak.plugin.android.codegenerator.utils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by wenping on 2016/1/31.
 */
public class BaseJDialog extends JDialog{
    public BaseJDialog() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

    }

    protected void onCancel() {
        dispose();
    }
}
