package com.troop.freedcam.cameraui.viewmodels;

import androidx.lifecycle.ViewModel;

import com.troop.freedcam.cameraui.models.ManualButtonModel;
import com.troop.freedcam.cameraui.views.ManualButton;

import java.util.HashMap;

public class CameraUiViewModel extends ViewModel {

    public enum ManualButtons
    {
        zoom,
        focus,
        iso,
        shutter,
        fnum,
        shift,
        ev,
        saturation,
        sharpness,
        tonecurve,
        wb,
        contrast,
        fx,
        burst,
    }

    private HashMap<ManualButtons, ManualButtonModel> manualButtonModelHashMap;

    public CameraUiViewModel() {
        manualButtonModelHashMap = new HashMap<>();
        ManualButtons buttons[] = ManualButtons.values();
        for (ManualButtons b : buttons)
            manualButtonModelHashMap.put(b, new ManualButtonModel());
    }

    public ManualButtonModel getManualButtonModel(ManualButtons manualButtons)
    {
        return manualButtonModelHashMap.get(manualButtons);
    }
}