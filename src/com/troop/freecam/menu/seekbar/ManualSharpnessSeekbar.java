package com.troop.freecam.menu.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.LandscapeSeekbarControl;
import com.troop.freecam.enums.E_ManualSeekbar;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 07.09.13.
 */
public class ManualSharpnessSeekbar extends LandscapeSeekbarControl
{


    public ManualSharpnessSeekbar(Context context) {
        super(context);
    }

    public ManualSharpnessSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualSharpnessSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void SetCameraManager(CameraManager cameraManager) {
        super.SetCameraManager(cameraManager);
        e_manualSeekbar = E_ManualSeekbar.Sharpness;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (cameraManager.parametersManager.getSupportSharpness() && fromUser)
        {
            cameraManager.parametersManager.manualSharpness.set(progress);
            textView_currentValue.setText("Sharpness: " + progress);
        }
    }

    @Override
    public void SetCurrentValue(int current) {
        super.SetCurrentValue(current);
        textView_currentValue.setText("Sharpness: " + current);
    }
}
