package freed.settings.mode;

import freed.FreedApplication;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.settings.SettingKeys;
import freed.utils.XmlUtil;

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
        String t = "<setting name = \""+ FreedApplication.getStringFromRessources(settingKey.getRessourcesStringID()) +"\" type = \""+ GlobalBooleanSettingMode.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("value", String.valueOf(value));
        t += "</setting>\r\n";
        return t;
    }

    @Override
    public void loadXmlNode(XmlElement node) {
        set(node.findChild("value").getBooleanValue());
    }
}
