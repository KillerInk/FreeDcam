package freed.settings.mode;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
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
        sub.append("<setting name = \""+ SettingsManager.getInstance().getResString(settingKey.getRessourcesStringID()) +"\" type = \""+ TypedSettingMode.class.getSimpleName() +"\">\r\n");
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
        else
            Log.d(TAG, "values are null: " + getCamera1ParameterKEY());
        sub.append("</setting>\r\n");
        Log.d(TAG, sub.toString());
        return sub.toString();
    }
}
