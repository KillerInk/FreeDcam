package com.troop.freecam.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.controls.ExtendedButton;
import com.troop.menu.AFPriorityMenu;
import com.troop.menu.ColorMenu;
import com.troop.menu.ExposureMenu;
import com.troop.menu.IsoMenu;
import com.troop.menu.MeteringMenu;
import com.troop.menu.SceneMenu;
import com.troop.menu.WhiteBalanceMenu;

/**
 * Created by troop on 02.01.14.
 */
public class AutoMenuFragment extends BaseFragment
{
    public ExtendedButton buttonAfPriority;
    public ExtendedButton sceneButton;
    public ExtendedButton whitebalanceButton;
    public ExtendedButton colorButton;
    public ExtendedButton isoButton;
    public ExtendedButton exposureButton;
    CheckBox checkboxHDR;
    public ExtendedButton buttonMetering;

    public AutoMenuFragment(CameraManager camMan, MainActivity activity)
    {
        super(camMan,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.automenufragment, container, false);
        init();
        return view;
    }

    private void init()
    {
        buttonAfPriority = (ExtendedButton)view.findViewById(R.id.buttonAFPriority);
        buttonAfPriority.setOnClickListener(new AFPriorityMenu(camMan,activity));

        sceneButton = (ExtendedButton) view.findViewById(R.id.buttonScene);
        sceneButton.setOnClickListener(new SceneMenu(camMan, activity));

        whitebalanceButton = (ExtendedButton) view.findViewById(R.id.buttonwhiteBalance);
        whitebalanceButton.setOnClickListener(new WhiteBalanceMenu(camMan, activity));

        colorButton = (ExtendedButton) view.findViewById(R.id.buttoncolor);
        colorButton.setOnClickListener(new ColorMenu(camMan, activity));

        isoButton = (ExtendedButton) view.findViewById(R.id.buttoniso);
        isoButton.setOnClickListener(new IsoMenu(camMan, activity));

        exposureButton = (ExtendedButton) view.findViewById(R.id.button_exposure);
        exposureButton.setOnClickListener(new ExposureMenu(camMan, activity));

        //06-12-13*************************************************************
        buttonMetering = (ExtendedButton)view.findViewById(R.id.buttonMetering);
        buttonMetering.setOnClickListener(new MeteringMenu(camMan,activity));
        //**********************************************************************

        checkboxHDR = (CheckBox)view.findViewById(R.id.checkBox_hdr);
        checkboxHDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.HDRMode = checkboxHDR.isChecked();
            }
        });
    }

    public void UpdateUI(boolean settingsReloaded)
    {
        if (settingsReloaded)
            checkVisibility();
        updateValues();
    }

    private void checkVisibility()
    {
        if (!camMan.parametersManager.getSupportAfpPriority())
            buttonAfPriority.setVisibility(View.GONE);
        else
        {
            if (buttonAfPriority.getVisibility() == View.GONE)
                buttonAfPriority.setVisibility(View.VISIBLE);
        }
        if (!camMan.parametersManager.getSupportAutoExposure())
        {
            buttonMetering.setVisibility(View.GONE);
        }
        else
            buttonMetering.setVisibility(View.VISIBLE);
        if (!camMan.parametersManager.getSupportWhiteBalance())
        {
            whitebalanceButton.setVisibility(View.GONE);
        }
        else
            whitebalanceButton.setVisibility(View.VISIBLE);
        if (camMan.parametersManager.getSupportIso())
            isoButton.setVisibility(View.VISIBLE);
        else
            isoButton.setVisibility(View.GONE);
        if (camMan.parametersManager.getSupportExposureMode())
            exposureButton.setVisibility(View.VISIBLE);
        else
            exposureButton.setVisibility(View.GONE);
    }

    private void updateValues() {
        sceneButton.SetValue(camMan.parametersManager.getParameters().getSceneMode());
        colorButton.SetValue(camMan.parametersManager.getParameters().getColorEffect());
        if (camMan.parametersManager.getSupportExposureMode())
            exposureButton.SetValue(camMan.parametersManager.getParameters().getExposureCompensation() + "");
        if (camMan.parametersManager.getSupportIso())
            isoButton.SetValue(camMan.parametersManager.Iso.get());
        //AF Priority
        if (camMan.parametersManager.getSupportAfpPriority())
        {
            buttonAfPriority.SetValue(camMan.parametersManager.AfPriority.Get());
        }

        //AutoExposure
        if (camMan.parametersManager.getSupportAutoExposure())
        {
            buttonMetering.SetValue(camMan.parametersManager.getParameters().get("auto-exposure"));
        }
        if (camMan.parametersManager.getSupportWhiteBalance())
        {
            whitebalanceButton.SetValue(camMan.parametersManager.WhiteBalance.get());
        }
    }
}
