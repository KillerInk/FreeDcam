package com.troop.freedcam.eventbus.updater;

import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.ModuleHasChangedEvent;

public class ModuleUpdater {

    public static void sendModuleChanged(String module)
    {
        EventBusHelper.post(new ModuleHasChangedEvent(module));
    }
}
