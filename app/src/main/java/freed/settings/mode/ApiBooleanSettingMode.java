package freed.settings.mode;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.XmlUtil;

/**
 * Created by KillerInk on 04.01.2018.
 */

public class ApiBooleanSettingMode extends GlobalBooleanSettingMode implements BooleanSettingModeInterface {
    private boolean preseted;
    private boolean value;

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

    @Override
    public String getXmlString() {
        String t = "<setting name = \""+ SettingsManager.getInstance().getResString(settingKey.getRessourcesStringID()) +"\" type = \""+ ApiBooleanSettingMode.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("value", String.valueOf(value));
        t+= XmlUtil.getTagStringWithValue("preseted", String.valueOf(preseted));
        t += "</setting>\r\n";
        return t;
    }
}
