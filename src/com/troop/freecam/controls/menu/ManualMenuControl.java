package com.troop.freecam.controls.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.InfoScreenControl;
import com.troop.freecam.controls.StyleAbelSlider;
import com.troop.freecam.manager.ManualSaturationManager;

/**
 * Created by troop on 17.01.14.
 */
public class ManualMenuControl extends LinearLayout
{
    CameraManager camMan;
    MainActivity activity;

    public CheckBox manualExposure;
    public CheckBox manualShaprness;
    public CheckBox manualFocus;
    public CheckBox contrastcheckBox;
    CheckBox brightnessCheckBox;
    public CheckBox saturationCheckBox;
    StyleAbelSlider manualExposureSlider;
    StyleAbelSlider manualShaprnessSlider;
    StyleAbelSlider manualSaturationSlider;
    StyleAbelSlider manualBrightnesSlider;
    StyleAbelSlider manualFocusSlider;
    StyleAbelSlider manualContrastSlider;

    public ManualMenuControl(Context context) {
        super(context);
    }

    public ManualMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void SetStuff(CameraManager cameraManager, MainActivity activity)
    {
        this.camMan = cameraManager;
        this.activity = activity;
        init();
    }

    private void init()
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.manual_menu_control, this);

        manualExposure = (CheckBox)findViewById(R.id.checkBox_exposureManual);
        manualExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (manualExposure.isChecked())
                {
                    manualExposureSlider.setVisibility(View.VISIBLE);
                }
                else
                {
                    manualExposureSlider.setVisibility(View.GONE);
                }
            }
        });

        manualShaprness = (CheckBox) findViewById(R.id.checkBox_sharpness);
        manualShaprness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualShaprness.isChecked())
                {
                    manualShaprnessSlider.setVisibility(View.VISIBLE);
                }
                else
                {
                    manualShaprnessSlider.setVisibility(View.GONE);
                }
                //sharpnessRow.invalidate();
            }
        });

        //********************ManualFocus******************************************


        manualFocus = (CheckBox)findViewById(R.id.checkBox_focus);
        manualFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualFocus.isChecked())

                    manualFocusSlider.setVisibility(View.VISIBLE);

                else
                    manualFocusSlider.setVisibility(View.GONE);
                //focusButton.setEnabled(true);
            }
        });


        //*****************************************End********************************************


        contrastcheckBox = (CheckBox)findViewById(R.id.checkBox_contrast);
        contrastcheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contrastcheckBox.isChecked())
                    manualContrastSlider.setVisibility(View.VISIBLE);
                else
                    manualContrastSlider.setVisibility(View.GONE);
            }
        });





        brightnessCheckBox = (CheckBox)findViewById(R.id.checkBox_brightness);
        brightnessCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brightnessCheckBox.isChecked())
                    manualBrightnesSlider.setVisibility(View.VISIBLE);
                else
                    manualBrightnesSlider.setVisibility(View.GONE);
            }
        });


        saturationCheckBox = (CheckBox) findViewById(R.id.checkBox_saturation);
        saturationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saturationCheckBox.isChecked())
                    manualSaturationSlider.setVisibility(View.VISIBLE);
                else
                    manualSaturationSlider.setVisibility(View.GONE);
            }
        });

        manualExposureSlider = (StyleAbelSlider)findViewById(R.id.slider_Exposure);
        manualExposureSlider.OnValueCHanged(camMan.manualExposureManager);
        manualExposureSlider.setVisibility(GONE);

        manualShaprnessSlider = (StyleAbelSlider)findViewById(R.id.slider_Sharpness);
        manualShaprnessSlider.OnValueCHanged(camMan.manualSharpnessManager);
        manualShaprnessSlider.setVisibility(GONE);

        manualSaturationSlider = (StyleAbelSlider)findViewById(R.id.slider_Saturation);
        manualSaturationSlider.OnValueCHanged(new ManualSaturationManager(camMan));
        if (manualSaturationSlider.getVisibility() == VISIBLE)
            manualSaturationSlider.setVisibility(GONE);

        manualBrightnesSlider = (StyleAbelSlider)findViewById(R.id.slider_Brightness);
        manualBrightnesSlider.OnValueCHanged(camMan.manualBrightnessManager);
        manualBrightnesSlider.setVisibility(GONE);

        manualFocusSlider = (StyleAbelSlider)findViewById(R.id.slider_Focus);
        manualFocusSlider.OnValueCHanged(camMan.manualFocus);
        manualFocusSlider.setVisibility(GONE);

        manualContrastSlider = (StyleAbelSlider)findViewById(R.id.slider_Contrast);
        manualContrastSlider.OnValueCHanged(camMan.manualContrastManager);
        manualContrastSlider.setVisibility(GONE);




    }

    public void UpdateUI(boolean restarted)
    {
        if (restarted)
        {
            int min = camMan.parametersManager.manualExposure.getMin();
            if (min < 0)
                min *= -1;
            int max = camMan.parametersManager.manualExposure.getMax() + min;
            camMan.manualExposureManager.SetMinMax(camMan.parametersManager.manualExposure.getMin(), camMan.parametersManager.manualExposure.getMax());
            manualExposureSlider.SetCurrentValue(camMan.parametersManager.manualExposure.getMax());
            manualExposureSlider.SetMaxValue(max);

            if (!camMan.parametersManager.getSupportBrightness())
                brightnessCheckBox.setVisibility(View.GONE);
            else
            {
                brightnessCheckBox.setVisibility(View.VISIBLE);
                manualBrightnesSlider.SetMaxValue(100);
                manualBrightnesSlider.SetCurrentValue(camMan.parametersManager.Brightness.Get());
            }
            if (!camMan.parametersManager.getSupportSharpness())
                manualShaprness.setVisibility(View.GONE);
            else
            {
                manualShaprness.setVisibility(View.VISIBLE);
                manualShaprnessSlider.SetMaxValue(camMan.parametersManager.manualSharpness.getMax());
                manualShaprnessSlider.SetCurrentValue(camMan.parametersManager.manualSharpness.getValue());
            }
            if (!camMan.parametersManager.getSupportSaturation())
                saturationCheckBox.setVisibility(View.GONE);
            else
            {
                saturationCheckBox.setVisibility(View.VISIBLE);
                manualSaturationSlider.SetMaxValue(100);
                manualSaturationSlider.SetMaxValue(camMan.parametersManager.getParameters().getInt("saturation"));
            }
            if (!camMan.parametersManager.getSupportManualFocus())
                manualFocus.setVisibility(GONE);
            else
                manualFocus.setVisibility(VISIBLE);

        }
    }
}
