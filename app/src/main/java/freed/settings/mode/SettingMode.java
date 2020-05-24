package freed.settings.mode;

import com.troop.freedcam.R;

import freed.settings.SettingsManagerInterface;
import freed.utils.XmlUtil;

/**
 * Created by KillerInk on 31.12.2017.
 */

public class SettingMode extends AbstractSettingMode<String> {

    protected boolean preseted;
    //String to get if supported
    private boolean supported;
    //String to get the values
    private String[] values;
    //String to get the value
    private String value;

    public SettingMode(String key)
    {
        super(key);
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
        return supported;
    }

    public void setIsSupported(boolean supported)
    {
        this.supported = supported;
    }

    @Override
    public String get()
    {
        return value;
    }

    @Override
    public void set(String valueToSet)
    {
        this.value = valueToSet;
    }

    public void setValues(String[] ar)
    {
        this.values = ar;
    }

    public String[] getValues()
    {
        return values;
    }

    public boolean contains(String value)
    {
        String[] values = getValues();
        for (String v : values)
        {
            if (v.contains(value))
                return true;
        }
        return false;
    }

    @Override
    public String getXmlString() {
        String t = "<setting name = \""+ KEY_value +"\" type = \""+ SettingMode.class.getSimpleName() +"\">";
        t+= XmlUtil.getTagStringWithValue("preseted", String.valueOf(preseted));
        t+= XmlUtil.getTagStringWithValue("supported", String.valueOf(supported));
        t+= XmlUtil.getTagStringWithValue("value", String.valueOf(value));
        String sub = "";
        if (values != null) {
            for (int i = 0; i < values.length; i++)
                sub += XmlUtil.getTagStringWithValue("val", values[i]);
            t += XmlUtil.getTagStringWithValue("values", sub);
        }
        t += "</setting>\r\n";
        return t;
    }
}
