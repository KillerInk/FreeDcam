package com.troop.freecam.controls.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.MenuItemControl;
import com.troop.menu.AntibandingMenu;
import com.troop.menu.DenoiseMenu;
import com.troop.menu.IppMenu;
import com.troop.menu.PreviewFormatMenu;
import com.troop.menu.PreviewFpsMenu;
import com.troop.menu.PreviewSizeMenu;
import com.troop.menu.ZslMenu;

/**
 * Created by troop on 13.01.14.
 */
public class QualitySubMenuControl extends BaseSubMenu
{
    MenuItemControl switchIPP;
    MenuItemControl switchDenoise;
    MenuItemControl switchZSL;
    MenuItemControl switchAntibanding;
    Switch switchLensShade;

    public QualitySubMenuControl(Context context) {
        super(context);
    }

    public QualitySubMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.quality_submenu, this);
    }

    public QualitySubMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Init(MainActivity activity, final CameraManager cameraManager)
    {
        super.Init(activity, cameraManager);

        switchIPP = (MenuItemControl)findViewById(R.id.switch_ipp_control);
        switchIPP.SetOnClickListner(new IppMenu(cameraManager,activity));

        switchDenoise = (MenuItemControl)findViewById(R.id.switch_denoise_control);
        switchDenoise.SetOnClickListner(new DenoiseMenu(cameraManager,activity));

        switchZSL = (MenuItemControl)findViewById(R.id.switch_zsl_control);
        switchZSL.SetOnClickListner(new ZslMenu(cameraManager,activity));

        switchAntibanding = (MenuItemControl)findViewById(R.id.switch_antibanding);
        switchAntibanding.SetOnClickListner(new AntibandingMenu(cameraManager, activity));

        switchLensShade = (Switch) findViewById(R.id.switch_lensShade);
        switchLensShade.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.parametersManager.LensShade.set(switchLensShade.isChecked());
                cameraManager.Settings.LensShade.set(switchLensShade.isChecked());
            }
        });
    }

    CompoundButton.OnCheckedChangeListener lensSwitch = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked)
                isChecked = false;
            else
                isChecked = true;
            cameraManager.parametersManager.LensShade.set(isChecked);
            cameraManager.Settings.LensShade.set(isChecked);
        }
    };

    public void UpdateUI()
    {
        if (cameraManager.parametersManager.getSupportVNF())
        {
            switchDenoise.SetButtonText(cameraManager.parametersManager.Denoise.getDenoiseValue());
            if (switchDenoise.getVisibility() != VISIBLE)
                switchDenoise.setVisibility(VISIBLE);
        }
        else
        {
            if (switchDenoise.getVisibility() != GONE)
                switchDenoise.setVisibility(GONE);
        }
        if (cameraManager.parametersManager.getSupportZSL())
        {
            if (switchZSL.getVisibility() == View.GONE)
                switchZSL.setVisibility(View.VISIBLE);
            switchZSL.SetButtonText(cameraManager.parametersManager.ZSLModes.getValue());
        }
        else
            if (switchZSL.getVisibility() == View.VISIBLE)
                switchZSL.setVisibility(View.GONE);

        //ImagePostProcessing
        if (cameraManager.parametersManager.getSupportIPP())
        {
            if (switchIPP.getVisibility() == View.GONE)
                switchIPP.setVisibility(View.VISIBLE);
            switchIPP.SetButtonText(cameraManager.parametersManager.ImagePostProcessing.Get());
        }
        else
            switchIPP.setVisibility(View.GONE);

        if (cameraManager.parametersManager.getSupportAntibanding())
        {
            if (switchAntibanding.getVisibility() == View.GONE)
                switchAntibanding.setVisibility(VISIBLE);
            switchAntibanding.SetButtonText(cameraManager.parametersManager.Antibanding.Get());
        }
        else
            switchAntibanding.setVisibility(GONE);

        if (cameraManager.parametersManager.getSupportLensShade())
        {
            switchLensShade.setVisibility(VISIBLE);
            switchLensShade.setChecked(cameraManager.Settings.LensShade.get());
        }
        else
            switchLensShade.setVisibility(GONE);
    }
}
