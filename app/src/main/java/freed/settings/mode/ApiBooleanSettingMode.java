package freed.settings.mode;

import freed.settings.SettingsManagerInterface;
import freed.utils.XmlUtil;

/**
 * Created by KillerInk on 04.01.2018.
 */

public class ApiBooleanSettingMode extends GlobalBooleanSettingMode implements BooleanSettingModeInterface {
    private boolean preseted;
    private boolean value;

    public ApiBooleanSettingMode(String key) {
        super(key);
    }

    @Override
    public Boolean get()
    {
        return value;
    }
    @Override
    public void set(Boolean enable)
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
        String t = "<setting name = \""+ KEY_value +"\" type = \""+ ApiBooleanSettingMode.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("value", String.valueOf(value));
        t+= XmlUtil.getTagStringWithValue("preseted", String.valueOf(preseted));
        t += "</setting>\r\n";
        return t;
    }
}
