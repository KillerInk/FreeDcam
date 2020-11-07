package com.troop.freedcam.cameraui.models;

import android.view.View;
import android.widget.SeekBar;

import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;

public class ManualButtonModel extends ButtonModel implements SeekBar.OnSeekBarChangeListener {
    private ManualButtonModelClickedEvent manualControlsHolderModel;
    private ParameterInterface parameterInterface;

    public ManualButtonModel(ManualButtonModelClickedEvent manualControlsHolderModel)
    {
        this.manualControlsHolderModel = manualControlsHolderModel;
    }

    public void setParameterInterface(ParameterInterface parameterInterface) {
        this.parameterInterface = parameterInterface;
        if (parameterInterface == null)
            setVisibility(View.GONE);
        else
        {
            setViewState(parameterInterface);
        }
    }

    private void setViewState(ParameterInterface parameterInterface) {
        switch (parameterInterface.getViewState())
        {
            case Hidden:
                setVisibility(View.GONE);
                break;
            case Visible:
                setVisibility(View.VISIBLE);
                break;
            case Enabled:
                setEnabled(true);
                break;
            case Disabled:
                setEnabled(false);
        }
    }

    public String[] getValues()
    {
        return parameterInterface.getStringValues();
    }

    public int getIndex()
    {
        return parameterInterface.GetValue();
    }

    @Override
    public void onClick(View v) {
        manualControlsHolderModel.onManualButtonClicked(ManualButtonModel.this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        parameterInterface.SetValue(progress,fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
