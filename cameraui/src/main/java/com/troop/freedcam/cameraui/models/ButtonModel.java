package com.troop.freedcam.cameraui.models;

import android.view.View;

import androidx.databinding.Bindable;

import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.cameraui.BR;

public class ButtonModel extends VisibilityEnableModel implements View.OnClickListener, ParameterInterface.ValueChangedEvent {
    protected ParameterInterface parameterInterface;

    public void setParameterInterface(ParameterInterface parameterInterface) {
        this.parameterInterface = parameterInterface;
        if (parameterInterface == null)
            setVisibility(View.GONE);
        else
        {
            parameterInterface.setViewStateEventListner(this::onViewStateChanged);
            parameterInterface.setValueChangedEventListner(this::onValueChanged);
            setViewState(parameterInterface.getViewState());
        }
    }

    public String[] getValues()
    {
        return parameterInterface.getStringValues();
    }

    @Bindable
    public String getValue()
    {
        if (parameterInterface == null)
            return "";
        return parameterInterface.GetStringValue();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onValueChanged(String val) {
        notifyPropertyChanged(BR.value);
    }
}
