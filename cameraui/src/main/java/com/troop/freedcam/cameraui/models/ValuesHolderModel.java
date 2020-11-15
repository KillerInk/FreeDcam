package com.troop.freedcam.cameraui.models;

import androidx.databinding.Bindable;

public class ValuesHolderModel extends VisibilityEnableModel {
    private ButtonModel buttonModel;
    private boolean fromLeft;

    public void setButtonModel(ButtonModel buttonModel, boolean fromLeft)
    {
        if (buttonModel != null) {
            this.buttonModel = buttonModel;
            this.fromLeft = fromLeft;
        }
        notifyChange();
    }

    @Bindable
    public ValuesHolderModel getButtonModel() {
        return this;
    }
}
