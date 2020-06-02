package freed.settings.mode;

import freed.FreedApplication;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.settings.SettingKeys;
import freed.utils.XmlUtil;

/**
 * Created by KillerInk on 04.01.2018.
 */

public class ApiBooleanSettingMode extends GlobalBooleanSettingMode implements BooleanSettingModeInterface {
    private boolean preseted;
    private boolean value;
    private boolean issupported;

    public ApiBooleanSettingMode(SettingKeys.Key key) {
        super(key);
    }

    @Override
    public boolean get()
    {
        return value;
    }
    @Override
    public void set(boolean enable)
    {
        this.value = enable;
    }

    public boolean isPresetted()
    {
        return preseted;
    }

    public void setIsPresetted(boolean preset)
    {
        this.preseted = preset;
    }

    public boolean isSupported()
    {
        return issupported;
    }

    public void setIsSupported(boolean preset)
    {
        this.issupported = preset;
    }

    @Override
    public String getXmlString() {
        String t = "<setting name = \""+ FreedApplication.getStringFromRessources(settingKey.getRessourcesStringID()) +"\" type = \""+ ApiBooleanSettingMode.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("value", String.valueOf(value));
        t+= XmlUtil.getTagStringWithValue("preseted", String.valueOf(preseted));
        t+= XmlUtil.getTagStringWithValue("supported", String.valueOf(issupported));
        t += "</setting>\r\n";
        return t;
    }

    @Override
    public void loadXmlNode(XmlElement node) {
        set(node.findChild("value").getBooleanValue());
        setIsPresetted(node.findChild("preseted").getBooleanValue());
        setIsSupported(node.findChild("supported").getBooleanValue());
    }
}
