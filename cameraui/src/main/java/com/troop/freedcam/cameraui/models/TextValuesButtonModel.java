package com.troop.freedcam.cameraui.models;

import android.view.View;

public class TextValuesButtonModel extends ButtonModel
{
    private ButtonValuesControllerModel buttonValuesControllerModel;
    private boolean fromLeft;

    public TextValuesButtonModel(ButtonValuesControllerModel buttonValuesControllerModel, boolean fromLeft)
    {
        this.buttonValuesControllerModel = buttonValuesControllerModel;
        this.fromLeft = fromLeft;
    }

    @Override
    public void onClick(View v) {
        buttonValuesControllerModel.onButtonClick(this,fromLeft);
    }
}
