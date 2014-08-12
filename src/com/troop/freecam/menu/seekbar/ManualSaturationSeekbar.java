package com.troop.freecam.menu.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.LandscapeSeekbarControl;
import com.troop.freecam.enums.E_ManualSeekbar;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 08.09.13.
 */
public class ManualSaturationSeekbar extends LandscapeSeekbarControl{

    public ManualSaturationSeekbar(Context context) {
        super(context);
    }

    public ManualSaturationSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualSaturationSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void SetCameraManager(CameraManager cameraManager) {
        super.SetCameraManager(cameraManager);
        e_manualSeekbar = E_ManualSeekbar.Saturation;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (cameraManager.parametersManager.getSupportSaturation()&& fromUser)
        {
            cameraManager.parametersManager.manualSaturation.set(progress);
            textView_currentValue.setText("Saturation: " + progress);
            //cameraManager.ReloadCameraParameters(false);
        }
    }
    @Override
    public void SetCurrentValue(int current) {
        super.SetCurrentValue(current);
        textView_currentValue.setText("Saturation: " + current);
    }
}
