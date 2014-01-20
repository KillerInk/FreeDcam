package com.troop.freecam.controls.menu;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.NumericUpDownControl;
import com.troop.freecam.interfaces.INumericUpDownValueCHanged;

/**
 * Created by troop on 20.01.14.
 */
public class HdrSubMenuControl extends BaseSubMenu
{
    NumericUpDownControl highExposure;
    NumericUpDownControl normalExposure;
    NumericUpDownControl lowExposure;
    public HdrSubMenuControl(Context context) {
        super(context);
    }

    public HdrSubMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.hdr_submenu, this);

    }

    public HdrSubMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Init(MainActivity activity, CameraManager cameraManager)
    {
        super.Init(activity, cameraManager);
        highExposure = (NumericUpDownControl)findViewById(R.id.numericUpDown_HighExposure);
        normalExposure = (NumericUpDownControl)findViewById(R.id.numericUpDown_NormalExposure);
        lowExposure = (NumericUpDownControl)findViewById(R.id.numericUpDown_LowExposure);

    }

    public void UpdateUI()
    {
        highExposure.setMinMax(cameraManager.parametersManager.manualExposure.getMin(), cameraManager.parametersManager.manualExposure.getMax());
        highExposure.setCurrent(cameraManager.Settings.HDRSettings.getHighExposure());
        highExposure.setOnValueCHanged(new INumericUpDownValueCHanged() {
            @Override
            public void ValueHasCHanged(int value) {
                cameraManager.Settings.HDRSettings.setHighExposure(value);
            }
        });

        normalExposure.setMinMax(cameraManager.parametersManager.manualExposure.getMin(), cameraManager.parametersManager.manualExposure.getMax());
        normalExposure.setCurrent(cameraManager.Settings.HDRSettings.getNormalExposure());
        normalExposure.setOnValueCHanged(new INumericUpDownValueCHanged() {
            @Override
            public void ValueHasCHanged(int value) {
                cameraManager.Settings.HDRSettings.setNormalExposure(value);
            }
        });

        lowExposure.setMinMax(cameraManager.parametersManager.manualExposure.getMin(), cameraManager.parametersManager.manualExposure.getMax());
        lowExposure.setCurrent(cameraManager.Settings.HDRSettings.getLowExposure());
        lowExposure.setOnValueCHanged(new INumericUpDownValueCHanged() {
            @Override
            public void ValueHasCHanged(int value) {
                cameraManager.Settings.HDRSettings.setLowExposure(value);
            }
        });
    }
}
