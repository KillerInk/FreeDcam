package com.troop.freecam.manager;

/**
 * Created by George on 12/4/13.
 */

import com.troop.freecam.camera.old.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

public class ManualFocus implements IStyleAbleSliderValueHasChanged {
    CameraManager cameraManager;

    public ManualFocus(CameraManager cameraManager){
        this.cameraManager = cameraManager;
    }


    @Override
    public void ValueHasChanged(int value) {
        cameraManager.parametersManager.SetMFocus(value);
    }
}
