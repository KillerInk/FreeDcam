package freed.settings.mode;

import freed.utils.XmlUtil;

public class GlobalStringSetting extends AbstractSettingMode<String> {


    public GlobalStringSetting(String key) {
        super(key);
    }

    @Override
    public String getXmlString() {
        String t = "<setting name = \""+ KEY_value +"\" type = \""+ GlobalStringSetting.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("value", get());
        t += "</setting>\r\n";
        return t;
    }
}
