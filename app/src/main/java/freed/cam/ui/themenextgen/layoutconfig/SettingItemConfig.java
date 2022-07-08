package freed.cam.ui.themenextgen.layoutconfig;

import freed.settings.SettingKeys;

public class SettingItemConfig extends ManualItemConfig<Integer>
{
    int description;
    boolean settingmanager;
    ViewType type;

    public enum ViewType
    {
        Boolean,
        Text,
        Button,
        Custom,
    }

    public SettingItemConfig(SettingKeys.Key key, int header, int description, boolean settingmanager, ViewType type)
    {
        super(key,header);
        this.description = description;
        this.settingmanager = settingmanager;
        this.type = type;
    }

    public int getDescription() {
        return description;
    }

    public boolean getFromSettingManager()
    {
        return settingmanager;
    }

    public ViewType getViewType() {
        return type;
    }
}
