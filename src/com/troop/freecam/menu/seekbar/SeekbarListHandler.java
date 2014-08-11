package com.troop.freecam.menu.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 11.08.2014.
 */
public class SeekbarListHandler extends LinearLayout
{
    CameraManager cameraManager;
    MainActivity activity;

    public ManualBrightnessSeekbar manualBrightnessSeekbar;
    public ManualExposureSeekbar manualExposureSeekbar;
    public SeekbarListHandler(Context context) {
        super(context);
    }

    public SeekbarListHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekbarListHandler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Init(CameraManager cameraManager, MainActivity activity)
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seekbars_list, this);
        this.cameraManager = cameraManager;
        this.activity = activity;

        if (cameraManager.parametersManager.getSupportBrightness()) {
            manualBrightnessSeekbar = (ManualBrightnessSeekbar) findViewById(R.id.brightness_seekbar);
            manualBrightnessSeekbar.SetCameraManager(cameraManager);
            manualBrightnessSeekbar.SetMinMaxValues(cameraManager.parametersManager.Brightness.GetMinValue(), cameraManager.parametersManager.Brightness.GetMaxValue());
            manualBrightnessSeekbar.SetCurrentValue(cameraManager.parametersManager.Brightness.Get());
        }

        if (cameraManager.parametersManager.getSupportExposureMode()) {
            manualExposureSeekbar = (ManualExposureSeekbar) findViewById(R.id.exposure_seekbar);
            manualExposureSeekbar.SetCameraManager(cameraManager);
            manualExposureSeekbar.SetMinMaxValues(cameraManager.parametersManager.manualExposure.getMin(), cameraManager.parametersManager.manualExposure.getMax());
            manualExposureSeekbar.SetCurrentValue(cameraManager.parametersManager.manualExposure.getValue());
        }
    }
}
