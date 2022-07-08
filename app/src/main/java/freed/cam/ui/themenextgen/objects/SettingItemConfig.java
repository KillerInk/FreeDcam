package freed.cam.ui.themenextgen.objects;

import freed.settings.SettingKeys;

public class SettingItemConfig
{
    SettingKeys.Key key;
    int header;
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
        this.key = key;
        this.header = header;
        this.description = description;
        this.settingmanager = settingmanager;
        this.type = type;
    }

    public SettingKeys.Key getKey() {
        return key;
    }

    public int getHeader() {
        return header;
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
