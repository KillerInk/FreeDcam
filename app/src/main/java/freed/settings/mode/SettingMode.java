package freed.settings.mode;

import java.util.Arrays;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.XmlUtil;

/**
 * Created by KillerInk on 31.12.2017.
 */

public class SettingMode extends AbstractSettingMode {

    private static final String TAG = SettingMode.class.getSimpleName();
    protected boolean preseted;
    //String to get if supported
    private boolean supported;
    //String to get the values
    private String[] values;
    //String to get the value
    private String value;

    public SettingMode(SettingKeys.Key key)
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

    public String get()
    {
        return value;
    }

    public void set(String valueToSet)
    {
        this.value = valueToSet;
    }

    public void setValues(String[] ar)
    {
        Log.d(TAG, settingKey.toString() +" " + Arrays.toString(ar));
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
        StringBuilder sub = new StringBuilder();
        sub.append("<setting name = \""+ FreedApplication.getStringFromRessources(settingKey.getRessourcesStringID()) +"\" type = \""+ SettingMode.class.getSimpleName() +"\">\r\n");
        sub.append(XmlUtil.getTagStringWithValue("cam1key", camera1ParameterKEY_value));
        sub.append(XmlUtil.getTagStringWithValue("preseted", String.valueOf(preseted))).append(XmlUtil.LINE_END);
        sub.append(XmlUtil.getTagStringWithValue("supported", String.valueOf(supported))).append(XmlUtil.LINE_END);
        sub.append(XmlUtil.getTagStringWithValue("value", String.valueOf(value))).append(XmlUtil.LINE_END);
        if (values != null) {
            sub.append("<values>\r\n");
            for (int i = 0; i < values.length; i++)
                sub.append(XmlUtil.getTagStringWithValue("val", values[i])).append("\r\n");
            sub.append("</values>\r\n");
        }
        /*else
            Log.d(TAG, "values are null: " + getCamera1ParameterKEY());*/
        sub.append("</setting>\r\n");
        //Log.d(TAG, sub.toString());
        return sub.toString();
    }

    @Override
    public void loadXmlNode(XmlElement node) {
        set(node.findChild("value").getValue());
        if (get().equals("null"))
            set(null);
        XmlElement values = node.findChild("values");
        List<XmlElement> strinarr = values.findChildren("val");
        String[] tosetar = new String[strinarr.size()];
        for (int i = 0; i < strinarr.size(); i++)
        {
            tosetar[i] = strinarr.get(i).getValue();
        }
        setValues(tosetar);
        setIsPresetted(node.findChild("preseted").getBooleanValue());
        setIsSupported(node.findChild("supported").getBooleanValue());
        setCamera1ParameterKEY(node.findChild("cam1key").getValue());
    }
}
