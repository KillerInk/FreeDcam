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
public class ManualFocusSeekbar extends LandscapeSeekbarControl
{


    public ManualFocusSeekbar(Context context) {
        super(context);
    }

    public ManualFocusSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualFocusSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void SetCameraManager(CameraManager cameraManager) {
        super.SetCameraManager(cameraManager);
        e_manualSeekbar = E_ManualSeekbar.Focus;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (cameraManager.parametersManager.getSupportManualFocus() && fromUser)
        {
            if (progress >= cameraManager.parametersManager.manualFocus.getMin() && progress <= cameraManager.parametersManager.manualFocus.getMax()) {
                cameraManager.parametersManager.manualFocus.set(progress);
                textView_currentValue.setText("Focus: " + current);
            }

            //cameraManager.ReloadCameraParameters(false);
        }
    }

    @Override
    public void SetCurrentValue(int current) {
        super.SetCurrentValue(current);
        textView_currentValue.setText("Focus: " + current);
    }

}
