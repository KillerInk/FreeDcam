package com.troop.freedcam.eventbus.events;


import com.troop.freedcam.settings.SettingKeys;

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
