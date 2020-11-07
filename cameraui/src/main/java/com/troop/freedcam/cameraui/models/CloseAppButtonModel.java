package com.troop.freedcam.cameraui.models;

import android.view.View;

import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.CloseAppEvent;

public class CloseAppButtonModel extends ManualButtonModel
{

    public CloseAppButtonModel() {
        super(null);
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        EventBusHelper.post(new CloseAppEvent());
    }
}
