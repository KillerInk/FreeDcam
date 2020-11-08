package com.troop.freedcam.cameraui.models;

import android.view.View;
import android.widget.SeekBar;

import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;

public class ManualButtonModel extends ButtonModel implements SeekBar.OnSeekBarChangeListener {
    private ManualButtonModelClickedEvent manualControlsHolderModel;

    public ManualButtonModel(ManualButtonModelClickedEvent manualControlsHolderModel)
    {
        this.manualControlsHolderModel = manualControlsHolderModel;
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
