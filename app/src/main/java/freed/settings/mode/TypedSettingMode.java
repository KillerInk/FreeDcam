package freed.settings.mode;

import com.troop.freedcam.R;

import freed.settings.SettingsManagerInterface;
import freed.utils.XmlUtil;

/**
 * Created by KillerInk on 31.12.2017.
 */

public class TypedSettingMode extends SettingMode {

    private int type;
    private String mode;

    public TypedSettingMode(String value_key) {
        super(value_key);
    }

    public int getType()
    {
        return type;
    }

    public void setType(int typevalue)
    {
        this.type = typevalue;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String modevalue)
    {
        this.mode = modevalue;
    }

    @Override
    public String getXmlString() {
        String t = "<setting name = \""+ KEY_value +"\" type = \""+ TypedSettingMode.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("preseted", String.valueOf(preseted));
        t+= XmlUtil.getTagStringWithValue("supported", String.valueOf(isSupported()));
        t+= XmlUtil.getTagStringWithValue("mode", mode);
        t+= XmlUtil.getTagStringWithValue("type", String.valueOf(type));
        t+= XmlUtil.getTagStringWithValue("value", String.valueOf(get()));
        String sub = "";
        for (int i = 0; i< getValues().length; i++)
            sub += XmlUtil.getTagStringWithValue("val", getValues()[i]);
        t+= XmlUtil.getTagStringWithValue("values", sub);
        t += "</setting>\r\n";
        return t;
    }
}
