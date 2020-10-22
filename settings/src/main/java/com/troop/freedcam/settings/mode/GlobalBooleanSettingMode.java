package com.troop.freedcam.settings.mode;

import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.XmlElement;
import com.troop.freedcam.utils.XmlUtil;

/**
 * Created by KillerInk on 04.01.2018.
 */

public class GlobalBooleanSettingMode extends AbstractSettingMode implements BooleanSettingModeInterface {

    private boolean value;

    public GlobalBooleanSettingMode(SettingKeys.Key key) {
        super(key);
    }

    @Override
    public boolean get()
    {
        return value;
    }

    @Override
    public void set(boolean bool) {
        this.value = bool;
    }

    @Override
    public String getXmlString() {
        String t = "<setting name = \""+ ContextApplication.getStringFromRessources(settingKey.getRessourcesStringID()) +"\" type = \""+ GlobalBooleanSettingMode.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("value", String.valueOf(value));
        t += "</setting>\r\n";
        return t;
    }

    @Override
    public void loadXmlNode(XmlElement node) {
        set(node.findChild("value").getBooleanValue());
    }
}
