package freed.settings;

/**
 * Created by KillerInk on 31.12.2017.
 */

public interface SettingsManagerInterface {
    boolean getApiBoolean(String settings_key, boolean defaultValue);
    boolean getBoolean(String settings_key, boolean defaultValue);
    void setApiBoolean(String settings_key, boolean valuetoSet);
    void setBoolean(String settings_key, boolean valuetoSet);
    void setApiInt(String key,int valueToSet);
    int getApiInt(String valueToGet);
    void setApiString(String settingsName, String Value);
    String getApiString(String valueToGet);
    void setStringArray(String settingsName, String[] Value);
    String[] getStringArray(String settingsname);
    String getResString(int id);

}
