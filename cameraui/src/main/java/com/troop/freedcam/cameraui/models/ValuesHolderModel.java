package com.troop.freedcam.cameraui.models;

import android.view.View;
import android.widget.Button;

import androidx.databinding.Bindable;

public class ValuesHolderModel extends VisibilityEnableModel implements View.OnClickListener {
    private ButtonModel buttonModel;
    private boolean fromLeft;

    public void setButtonModel(ButtonModel buttonModel, boolean fromLeft)
    {
        if (buttonModel != null) {
            if (buttonModel == this.buttonModel)
            {
                setVisibility(View.GONE);
                this.buttonModel = null;
            }
            else {
                this.buttonModel = buttonModel;
                this.fromLeft = fromLeft;
            }
        }
        notifyChange();
    }

    @Bindable
    public ButtonModel getButtonModel()
    {
        return buttonModel;
    }

    @Override
    public void onClick(View v) {
        Button button = (Button)v;
        buttonModel.parameterInterface.SetValue((String)button.getText(),true);
        setVisibility(View.GONE);
    }

    public boolean isFromLeft() {
        return fromLeft;
    }
}
