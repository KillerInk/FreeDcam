package com.troop.freedcam.settings.mode;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.XmlElement;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.utils.XmlUtil;

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
        String t = "<setting name = \""+ ContextApplication.getStringFromRessources(settingKey.getRessourcesStringID()) +"\" type = \""+ ApiBooleanSettingMode.class.getSimpleName() +"\">";
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
