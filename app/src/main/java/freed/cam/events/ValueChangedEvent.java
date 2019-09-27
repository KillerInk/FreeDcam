package freed.cam.events;

import freed.settings.SettingKeys;

public class ValueChangedEvent<T> {
    public final T newValue;
    public final SettingKeys.Key key;
    public final Class<T> type;

    public ValueChangedEvent(SettingKeys.Key key, T newValue, Class<T> type)
    {
        this.key =key;
        this.newValue = newValue;
        this.type = type;
    }
}
