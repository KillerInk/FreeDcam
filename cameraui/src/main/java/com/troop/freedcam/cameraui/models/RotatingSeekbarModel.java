package com.troop.freedcam.cameraui.models;

import androidx.databinding.Bindable;

import com.troop.freedcam.cameraui.BR;

public class RotatingSeekbarModel extends VisibilityEnableModel {
    private ManualButtonModel manualButtonModel;

    public void setManualButtonModel(ManualButtonModel manualButtonModel) {
        this.manualButtonModel = manualButtonModel;
        notifyPropertyChanged(BR.manualButtonModel);
    }

    @Bindable
    public ManualButtonModel getManualButtonModel() {
        return manualButtonModel;
    }
}
