package com.troop.freedcam.eventbus.updater;

import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.ValueChangedEvent;
import com.troop.freedcam.settings.SettingKeys;

public class ValueUpdater {

    public static <T> void updateValue(SettingKeys.Key key, T newValue, Class<T> type)
    {
        EventBusHelper.post(new ValueChangedEvent<>(key, newValue, type));
    }
}
