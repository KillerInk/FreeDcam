package freed.settings.mode;

import com.troop.freedcam.R;

import freed.settings.SettingsManagerInterface;

/**
 * Created by KillerInk on 31.12.2017.
 */

public class SettingMode extends AbstractSettingMode {

    protected String presetKey;
    //String to get if supported
    private String supported_key;
    //String to get the values
    private String values_key;
    //String to get the value
    private String value_key;

    public SettingMode(SettingsManagerInterface settingsManagerInterface, String key)
    {
        super(settingsManagerInterface,key);
        this.presetKey = key + "preset";
        this.supported_key= key + settingsManagerInterface.getResString(R.string.aps_supported);
        this.value_key = key + settingsManagerInterface.getResString(R.string.aps_key);
        this.values_key = key + settingsManagerInterface.getResString(R.string.aps_values);
    }

    public boolean isPresetted()
    {
        return settingsManagerInterface.getApiBoolean(presetKey,false);
    }

    public void setIsPresetted(boolean preset)
    {
        settingsManagerInterface.setApiBoolean(presetKey, preset);
    }

    public boolean isSupported()
    {
        return settingsManagerInterface.getApiBoolean(supported_key,false);
    }

    public void setIsSupported(boolean supported)
    {
        settingsManagerInterface.setApiBoolean(supported_key, supported);
    }

    public String getKEY()
    {
        return settingsManagerInterface.getApiString(KEY_value);
    }

    public void setKEY(String KEY)
    {
        settingsManagerInterface.setApiString(KEY_value,KEY);
    }

    public String get()
    {
        return settingsManagerInterface.getApiString(value_key);
    }

    public void set(String valueToSet)
    {
        settingsManagerInterface.setApiString(value_key,valueToSet);
    }

    public void setValues(String[] ar)
    {
        settingsManagerInterface.setStringArray(values_key, ar);
    }

    public String[] getValues()
    {
        return settingsManagerInterface.getStringArray(values_key);
    }

    public boolean contains(String value)
    {
        String[] values = getValues();
        for (String v : values)
        {
            if (v.equals(value))
                return true;
        }
        return false;
    }
}
