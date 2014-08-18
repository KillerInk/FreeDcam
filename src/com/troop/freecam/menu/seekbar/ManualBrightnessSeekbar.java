package com.troop.freecam.menu.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.troop.freecam.camera.old.CameraManager;
import com.troop.freecam.controls.LandscapeSeekbarControl;
import com.troop.freecam.enums.E_ManualSeekbar;

/**
 * Created by troop on 07.09.13.
 */
public class ManualBrightnessSeekbar extends LandscapeSeekbarControl
{
    public ManualBrightnessSeekbar(Context context) {
        super(context);
    }

    public ManualBrightnessSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualBrightnessSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void SetCameraManager(CameraManager cameraManager) {
        super.SetCameraManager(cameraManager);
        e_manualSeekbar = E_ManualSeekbar.Brightness;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (cameraManager.parametersManager.getSupportBrightness() && progress >= cameraManager.parametersManager.Brightness.GetMinValue() && progress <= cameraManager.parametersManager.Brightness.GetMaxValue())
        {
            current = progress;
            cameraManager.parametersManager.Brightness.Set(progress);
            SetText("Brightness: ");
        }
    }

    @Override
    public void SetCurrentValue(int current) {
        super.SetCurrentValue(current);
        SetText("Brightness: ");
    }


}
