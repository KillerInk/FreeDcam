package com.troop.freecam.menu.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.LandscapeSeekbarControl;
import com.troop.freecam.enums.E_ManualSeekbar;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 24.01.14.
 */
public class ManualConvergenceSeekbar extends LandscapeSeekbarControl
{

    public ManualConvergenceSeekbar(Context context) {
        super(context);
    }

    public ManualConvergenceSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualConvergenceSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void SetCameraManager(CameraManager cameraManager) {
        super.SetCameraManager(cameraManager);
        e_manualSeekbar = E_ManualSeekbar.Convergence;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        current = progress + min;
        //if (maxValue < 61)
        //{
        if (current >= cameraManager.parametersManager.manualConvergence.getMin() && current <= cameraManager.parametersManager.manualConvergence.getMax())
        {
            cameraManager.parametersManager.manualConvergence.set(current);
            textView_currentValue.setText("Convergence: " + current);
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
        this.current = current;
        if (min < 0)
            seekBar.setProgress(current + max);
        else
            seekBar.setProgress(current);
        textView_currentValue.setText("Convergence: " + current);
    }
}
