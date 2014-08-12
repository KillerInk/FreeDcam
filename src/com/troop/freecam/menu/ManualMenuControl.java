package com.troop.freecam.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.ExtendedCheckBox;
import com.troop.freecam.enums.E_ManualSeekbar;
import com.troop.freecam.menu.seekbar.SeekbarListHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 17.01.14.
 */
public class ManualMenuControl extends LinearLayout
{
    CameraManager camMan;
    MainActivity activity;

    public ExtendedCheckBox manualExposure;
    public ExtendedCheckBox manualShaprness;
    public ExtendedCheckBox manualFocus;
    public ExtendedCheckBox manualShutter;
    public ExtendedCheckBox contrastcheckBox;
    ExtendedCheckBox brightnessCheckBox;
    public ExtendedCheckBox saturationCheckBox;

    private List<ExtendedCheckBox> checkBoxes;

    SeekbarListHandler seekbarListHandler;

    ExtendedCheckBox checkbox_convergence;

    public ManualMenuControl(Context context) {
        super(context);
    }

    public ManualMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void SetStuff(CameraManager cameraManager, MainActivity activity, SeekbarListHandler seekbarListHandler)
    {
        this.camMan = cameraManager;
        this.activity = activity;
        this.seekbarListHandler = seekbarListHandler;
        init();
    }

    private void init()
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.manual_menu_control, this);

        checkBoxes = new ArrayList<ExtendedCheckBox>();

        manualExposure = (ExtendedCheckBox)findViewById(R.id.checkBox_exposureManual);
        manualExposure.SetSeekbarValue(E_ManualSeekbar.Exposure);
        checkBoxes.add(manualExposure);
        manualExposure.setOnClickListener(onCheckBoxClick);

        manualShaprness = (ExtendedCheckBox) findViewById(R.id.checkBox_sharpness);
        manualShaprness.SetSeekbarValue(E_ManualSeekbar.Sharpness);
        checkBoxes.add(manualShaprness);
        manualShaprness.setOnClickListener(onCheckBoxClick);

        manualFocus = (ExtendedCheckBox)findViewById(R.id.checkBox_focus);
        manualFocus.SetSeekbarValue(E_ManualSeekbar.Focus);
        checkBoxes.add(manualFocus);
        manualFocus.setOnClickListener(onCheckBoxClick);

        manualShutter = (ExtendedCheckBox)findViewById(R.id.checkBox_manualShutter);
        manualShutter.SetSeekbarValue(E_ManualSeekbar.Shutter);
        checkBoxes.add(manualShutter);
        manualShutter.setOnClickListener(onCheckBoxClick);

        contrastcheckBox = (ExtendedCheckBox)findViewById(R.id.checkBox_contrast);
        contrastcheckBox.SetSeekbarValue(E_ManualSeekbar.Contrast);
        checkBoxes.add(contrastcheckBox);
        contrastcheckBox.setOnClickListener(onCheckBoxClick);

        brightnessCheckBox = (ExtendedCheckBox)findViewById(R.id.checkBox_brightness);
        brightnessCheckBox.SetSeekbarValue(E_ManualSeekbar.Brightness);
        checkBoxes.add(brightnessCheckBox);
        brightnessCheckBox.setOnClickListener(onCheckBoxClick);

        saturationCheckBox = (ExtendedCheckBox) findViewById(R.id.checkBox_saturation);
        saturationCheckBox.SetSeekbarValue(E_ManualSeekbar.Saturation);
        checkBoxes.add(saturationCheckBox);
        saturationCheckBox.setOnClickListener(onCheckBoxClick);

        checkbox_convergence = (ExtendedCheckBox)findViewById(R.id.checkBox_manualConvergence);
        checkbox_convergence.SetSeekbarValue(E_ManualSeekbar.Convergence);
        checkBoxes.add(checkbox_convergence);
        checkbox_convergence.setOnClickListener(onCheckBoxClick);
    }

    OnClickListener onCheckBoxClick = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            CheckBox box = (CheckBox) v;
            for (int i = 0; i < checkBoxes.size(); i++)
            {
                if (checkBoxes.get(i) != box)
                    checkBoxes.get(i).setChecked(false);
                else
                {
                    seekbarListHandler.setVisibility(checkBoxes.get(i).GetSeekBarValue(), checkBoxes.get(i).isChecked() );
                }

            }
        }
    };

    public void UpdateUI(boolean restarted)
    {
        if (restarted)
        {
            if (!camMan.parametersManager.getSupportBrightness())
                brightnessCheckBox.setVisibility(View.GONE);
            else
            {
                brightnessCheckBox.setVisibility(View.VISIBLE);
            }
            if (!camMan.parametersManager.getSupportSharpness())
                manualShaprness.setVisibility(View.GONE);
            else
            {
                manualShaprness.setVisibility(View.VISIBLE);
            }
            if (!camMan.parametersManager.getSupportSaturation())
                saturationCheckBox.setVisibility(View.GONE);
            else
            {
                saturationCheckBox.setVisibility(View.VISIBLE);
            }
            if (!camMan.parametersManager.getSupportContrast())
                contrastcheckBox.setVisibility(GONE);
            else
            {
                contrastcheckBox.setVisibility(VISIBLE);
            }
            if (camMan.parametersManager.getSupportManualConvergence())
            {
                checkbox_convergence.setVisibility(VISIBLE);
            }
            else
            {
                checkbox_convergence.setVisibility(GONE);
            }
            if (!camMan.parametersManager.getSupportManualFocus())
                manualFocus.setVisibility(View.GONE);
            else
            {
                manualFocus.setVisibility(View.VISIBLE);
            }
            if (!camMan.parametersManager.getSupportManualShutter())
                manualShutter.setVisibility(View.GONE);
            else
            {
                manualShutter.setVisibility(View.VISIBLE);
            }

        }
    }
}
