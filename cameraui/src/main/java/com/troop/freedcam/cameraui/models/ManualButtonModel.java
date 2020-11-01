package com.troop.freedcam.cameraui.models;

import android.view.View;

public class ManualButtonModel extends VisibilityEnableModel {
    private ManualControlsHolderModel manualControlsHolderModel;
    public ManualButtonModel(ManualControlsHolderModel manualControlsHolderModel)
    {
        this.manualControlsHolderModel = manualControlsHolderModel;
    }

    public String[] getValues()
    {
        return null;
    }

    public int getIndex()
    {
        return 0;
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            manualControlsHolderModel.onManualButtonClick(ManualButtonModel.this);
        }
    };

}
