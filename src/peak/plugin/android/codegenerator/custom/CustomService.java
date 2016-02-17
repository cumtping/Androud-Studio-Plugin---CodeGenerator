package peak.plugin.android.codegenerator.custom;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by wenping on 2016/2/17.
 */
@State()
public class CustomService implements PersistentStateComponent<CustomService> {
    Map<String, String> customTempletesMap;

    @Nullable
    @Override
    public CustomService getState() {
        return this;
    }

    @Override
    public void loadState(CustomService customService) {
        XmlSerializerUtil.copyBean(customTempletesMap, this);
    }
}
