package freed.settings.mode;

import com.troop.freedcam.R;

import freed.settings.SettingsManagerInterface;

/**
 * Created by KillerInk on 31.12.2017.
 */

public class TypedSettingMode extends SettingMode {

    private String type;
    private String mode;

    public TypedSettingMode(SettingsManagerInterface settingsManagerInterface, String value_key) {
        super(settingsManagerInterface,value_key);
        this.type = value_key + settingsManagerInterface.getResString(R.string.aps_type);
        this.mode = value_key + settingsManagerInterface.getResString(R.string.aps_mode);
    }

    public int getType()
    {
        return settingsManagerInterface.getApiInt(type);
    }

    public void setType(int typevalue)
    {
        settingsManagerInterface.setApiInt(type,typevalue);
    }

    public String getMode()
    {
        return settingsManagerInterface.getApiString(mode);
    }

    public void setMode(String modevalue)
    {
        settingsManagerInterface.setApiString(mode,modevalue);
    }



}
