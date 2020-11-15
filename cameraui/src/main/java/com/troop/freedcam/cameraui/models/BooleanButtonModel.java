package com.troop.freedcam.cameraui.models;

import android.view.View;

import com.troop.freedcam.utils.ContextApplication;

public class BooleanButtonModel extends ButtonModel {

    @Override
    public void onClick(View v) {
        if (parameterInterface.GetStringValue().equals(ContextApplication.getContext().getString(com.troop.freedcam.camera.R.string.true_)))
        {
            parameterInterface.SetValue(ContextApplication.getContext().getString(com.troop.freedcam.camera.R.string.false_), true);
        }
        else
        {
            parameterInterface.SetValue(ContextApplication.getContext().getString(com.troop.freedcam.camera.R.string.true_),true);
        }
    }
}
