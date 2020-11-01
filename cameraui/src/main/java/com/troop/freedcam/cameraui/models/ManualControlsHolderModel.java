package com.troop.freedcam.cameraui.models;

import android.view.View;

public class ManualControlsHolderModel extends VisibilityEnableModel implements ManualButtonModelClickedEvent {
    private RotatingSeekbarModel rotatingSeekbarModel;

    public ManualControlsHolderModel(RotatingSeekbarModel rotatingSeekbarModel)
    {
        this.rotatingSeekbarModel = rotatingSeekbarModel;
    }

    @Override
    public void onManualButtonClicked(ManualButtonModel manualButtonModel) {
        if (rotatingSeekbarModel.getManualButtonModel() != manualButtonModel)
        {
            rotatingSeekbarModel.setManualButtonModel(manualButtonModel);
        }
        else
            rotatingSeekbarModel.setManualButtonModel(null);
    }
}
