package freed.settings.mode;

import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.settings.SettingKeys;
import freed.utils.XmlUtil;

/**
 * Created by KillerInk on 31.12.2017.
 */

public class TypedSettingMode extends SettingMode {

    private final String TAG = TypedSettingMode.class.getSimpleName();

    private int type;
    private String mode;

    public TypedSettingMode(SettingKeys.Key value_key) {
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
        StringBuilder sub = new StringBuilder();
        sub.append("<setting name = \""+ FreedApplication.getStringFromRessources(settingKey.getRessourcesStringID()) +"\" type = \""+ TypedSettingMode.class.getSimpleName() +"\">\r\n");
        sub.append(XmlUtil.getTagStringWithValue("cam1key", camera1ParameterKEY_value));
        sub.append(XmlUtil.getTagStringWithValue("preseted", String.valueOf(preseted))).append(XmlUtil.LINE_END);
        sub.append(XmlUtil.getTagStringWithValue("supported", String.valueOf(isSupported()))).append(XmlUtil.LINE_END);
        sub.append(XmlUtil.getTagStringWithValue("value", String.valueOf(get()))).append(XmlUtil.LINE_END);
        sub.append(XmlUtil.getTagStringWithValue("mode", mode)).append(XmlUtil.LINE_END);
        sub.append(XmlUtil.getTagStringWithValue("type", String.valueOf(type))).append(XmlUtil.LINE_END);
        if (getValues() != null) {
            sub.append("<values>\r\n");
            for (int i = 0; i < getValues().length; i++)
                sub.append(XmlUtil.getTagStringWithValue("val", getValues()[i])).append("\r\n");
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
        setType(node.findChild("type").getIntValue(0));
        setMode(node.findChild("mode").getValue());
        setCamera1ParameterKEY(node.findChild("cam1key").getValue());
    }
}
