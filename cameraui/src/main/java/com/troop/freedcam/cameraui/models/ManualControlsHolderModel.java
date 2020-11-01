package com.troop.freedcam.cameraui.models;

import android.view.View;

public class ManualControlsHolderModel extends VisibilityEnableModel {
    private RotatingSeekbarModel rotatingSeekbarModel;

    public ManualControlsHolderModel(RotatingSeekbarModel rotatingSeekbarModel)
    {
        this.rotatingSeekbarModel = rotatingSeekbarModel;
    }

    public void onManualButtonClick(ManualButtonModel manualButtonModel)
    {
        if (rotatingSeekbarModel.getManualButtonModel() != manualButtonModel)
        {
            rotatingSeekbarModel.setManualButtonModel(manualButtonModel);
        }
        else
            rotatingSeekbarModel.setManualButtonModel(null);
    }
}
