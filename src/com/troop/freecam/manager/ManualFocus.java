package com.troop.freecam.manager;

/**
 * Created by George on 12/4/13.
 */
import android.widget.SeekBar;

import com.troop.freecam.CameraManager;

public class ManualFocus implements SeekBar.OnSeekBarChangeListener {
    CameraManager cameraManager;

    public ManualFocus(CameraManager cameraManager){
        this.cameraManager = cameraManager;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        cameraManager.parametersManager.SetMFocus(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
