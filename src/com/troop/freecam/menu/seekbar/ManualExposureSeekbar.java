package com.troop.freecam.menu.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.LandscapeSeekbarControl;
import com.troop.freecam.enums.E_ManualSeekbar;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 03.09.13.
 */
public class ManualExposureSeekbar extends LandscapeSeekbarControl {



    public ManualExposureSeekbar(Context context) {
        super(context);
    }

    public ManualExposureSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualExposureSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void SetCameraManager(CameraManager cameraManager) {
        super.SetCameraManager(cameraManager);
        e_manualSeekbar = E_ManualSeekbar.Exposure;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (fromUser) {
            current = progress + min;
            if (current >= cameraManager.parametersManager.manualExposure.getMin() && current <= cameraManager.parametersManager.manualExposure.getMax()) {
                cameraManager.parametersManager.manualExposure.set(current);
                textView_currentValue.setText("Exposure: " + current);
            }
        }
    }

    @Override
    public void SetMinMaxValues(int min, int max)
    {
        this.min = min;
        this.max = max;
        int maxnegativ = max;
        if (min < 0)
            maxnegativ = max + min * -1;
        seekBar.setMax(maxnegativ);

    }



    @Override
    public void SetCurrentValue(int current) {
        //super.SetCurrentValue(current + max);
        if(current <= min || current >= max)
            return;
        this.current = current;
        if (min < 0)
            seekBar.setProgress(current + max);
        else
            seekBar.setProgress(current);
        textView_currentValue.setText("Exposure: " + current);
    }
}
