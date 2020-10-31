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

    public enum LeftbarButtons
    {
        histogram,
        clipping,
        wb,
        iso,
        flash,
        focus,
        exposure,
        aepriority,
        contshot,
        hdr,
        night,
        imageformat,
    }

    public enum RightbarButtons
    {
        close,
        module,
        focuspeak,
        selftimer,
        aelock,
        cameraswitch,
    }

    private HashMap<ManualButtons, ManualButtonModel> manualButtonModelHashMap;
    private HashMap<LeftbarButtons, ManualButtonModel> leftbarButtonsManualButtonModelHashMap;
    private HashMap<RightbarButtons, ManualButtonModel> rightbarButtonsManualButtonModelHashMap;

    public CameraUiViewModel() {
        manualButtonModelHashMap = new HashMap<>();
        ManualButtons buttons[] = ManualButtons.values();
        for (ManualButtons b : buttons)
            manualButtonModelHashMap.put(b, new ManualButtonModel());
        leftbarButtonsManualButtonModelHashMap = new HashMap<>();
        LeftbarButtons leftbarButtons[] = LeftbarButtons.values();
        for (LeftbarButtons buttons1: leftbarButtons)
            leftbarButtonsManualButtonModelHashMap.put(buttons1, new ManualButtonModel());

        rightbarButtonsManualButtonModelHashMap = new HashMap<>();
        RightbarButtons rightbarButtons[] = RightbarButtons.values();
        for (RightbarButtons buttons1: rightbarButtons)
            rightbarButtonsManualButtonModelHashMap.put(buttons1, new ManualButtonModel());

    }

    public ManualButtonModel getManualButtonModel(ManualButtons manualButtons)
    {
        return manualButtonModelHashMap.get(manualButtons);
    }

    public ManualButtonModel getManualButtonModel(LeftbarButtons manualButtons)
    {
        return leftbarButtonsManualButtonModelHashMap.get(manualButtons);
    }

    public ManualButtonModel getManualButtonModel(RightbarButtons manualButtons)
    {
        return rightbarButtonsManualButtonModelHashMap.get(manualButtons);
    }
}