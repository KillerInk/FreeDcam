package com.troop.freedcam.eventbus.events;

import com.troop.freedcam.settings.SettingKeys;

public class IsoChangedEvent {

    public final String  newValue;
    public final SettingKeys.Key key;
    public final Class<String> type;

    public IsoChangedEvent(SettingKeys.Key key, String newValue, Class type) {
        this.key =key;
        this.newValue = newValue;
        this.type = type;
    }
}
