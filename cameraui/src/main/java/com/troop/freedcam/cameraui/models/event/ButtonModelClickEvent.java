package com.troop.freedcam.cameraui.models.event;

import com.troop.freedcam.cameraui.models.ButtonModel;

public interface ButtonModelClickEvent
{
    void onButtonClick(ButtonModel model, boolean fromLeft);
}
