package com.troop.freedcam.cameraui.models;

import android.view.View;
import android.widget.SeekBar;

public class ManualButtonModel extends ButtonModel implements SeekBar.OnSeekBarChangeListener {
    private ManualButtonModelClickedEvent manualControlsHolderModel;
    public ManualButtonModel(ManualButtonModelClickedEvent manualControlsHolderModel)
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

    @Override
    public void onClick(View v) {
        manualControlsHolderModel.onManualButtonClicked(ManualButtonModel.this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
