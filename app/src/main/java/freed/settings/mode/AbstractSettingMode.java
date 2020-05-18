package freed.settings.mode;

import freed.settings.SettingsManagerInterface;

/**
 * Created by KillerInk on 04.01.2018.
 */

public abstract class AbstractSettingMode<T> implements SettingInterface
{
    protected SettingsManagerInterface settingsManagerInterface;
    //key to identify this settings
    protected String KEY_value;
    private T value;

    public AbstractSettingMode(String key)
    {
        this.KEY_value = key;
    }

    public String getKEY()
    {
        return KEY_value;
    }

    public void setKEY(String KEY)
    {
        this.KEY_value = KEY;
    }

    public T get(){return value;};
    public void set(T value){this.value = value;};
}
