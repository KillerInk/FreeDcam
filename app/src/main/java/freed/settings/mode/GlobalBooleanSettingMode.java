package freed.settings.mode;

import freed.utils.XmlUtil;

/**
 * Created by KillerInk on 04.01.2018.
 */

public class GlobalBooleanSettingMode extends AbstractSettingMode<Boolean> implements BooleanSettingModeInterface {

    private boolean value;

    public GlobalBooleanSettingMode(String key) {
        super(key);
    }

    @Override
    public Boolean get()
    {
        return value;
    }

    @Override
    public void set(Boolean bool) {
        this.value = value;
    }

    @Override
    public String getXmlString() {
        String t = "<setting name = \""+ KEY_value +"\" type = \""+ GlobalBooleanSettingMode.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("value", String.valueOf(value));
        t += "</setting>\r\n";
        return t;
    }
}
