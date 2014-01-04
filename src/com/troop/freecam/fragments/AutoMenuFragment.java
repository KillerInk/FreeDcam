package com.troop.freecam.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
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
    Switch checkboxHDR;
    MenuItemFragment switchAF;
    MenuItemFragment switchScene;
    MenuItemFragment switchWhiteBalance;
    MenuItemFragment switchColor;
    MenuItemFragment switchIso;
    MenuItemFragment switchExposure;
    MenuItemFragment switchMetering;

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
        checkboxHDR = (Switch)view.findViewById(R.id.checkBox_hdr);
        checkboxHDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.HDRMode = checkboxHDR.isChecked();
            }
        });


        FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
        switchAF = new MenuItemFragment(camMan, activity, "AutoFocusPrioritys", "", new AFPriorityMenu(camMan,activity));
        transaction.add(R.id.auto_menu_fragment_layout, switchAF);
        switchScene = new MenuItemFragment(camMan,activity,"ScenesModes", "", new SceneMenu(camMan,activity));
        transaction.add(R.id.auto_menu_fragment_layout, switchScene);

        switchWhiteBalance = new MenuItemFragment(camMan,activity,"WhiteBalanceModes", "", new WhiteBalanceMenu(camMan,activity));
        transaction.add(R.id.auto_menu_fragment_layout, switchWhiteBalance);

        switchColor = new MenuItemFragment(camMan,activity,"ColorModes", "", new ColorMenu(camMan,activity));
        switchIso = new MenuItemFragment(camMan,activity,"IsoModes", "", new IsoMenu(camMan,activity));
        switchExposure = new MenuItemFragment(camMan,activity, "ExposureModes", "", new ExposureMenu(camMan,activity));
        switchMetering = new MenuItemFragment(camMan,activity,"MeteringModes","", new MeteringMenu(camMan,activity));
        transaction.add(R.id.auto_menu_fragment_layout, switchMetering);
        transaction.add(R.id.auto_menu_fragment_layout, switchColor);
        transaction.add(R.id.auto_menu_fragment_layout, switchIso);
        transaction.add(R.id.auto_menu_fragment_layout, switchExposure);
        transaction.commit();
    }

    public void UpdateUI(boolean settingsReloaded)
    {
        if (settingsReloaded)
            checkVisibility();
        updateValues();
    }

    private void checkVisibility()
    {

        if (camMan.parametersManager.getSupportAfpPriority())
            switchAF.view.setVisibility(View.VISIBLE);
        else
            switchAF.view.setVisibility(View.GONE);

        if (!camMan.parametersManager.getSupportAutoExposure())
        {
            switchMetering.view.setVisibility(View.GONE);
        }
        else
            switchMetering.view.setVisibility(View.VISIBLE);
        if (!camMan.parametersManager.getSupportWhiteBalance())
        {
            switchWhiteBalance.view.setVisibility(View.GONE);
        }
        else
            switchWhiteBalance.view.setVisibility(View.VISIBLE);
        if (camMan.parametersManager.getSupportIso())
            switchIso.view.setVisibility(View.VISIBLE);
        else
            switchIso.view.setVisibility(View.GONE);
        if (camMan.parametersManager.getSupportExposureMode())
            switchExposure.view.setVisibility(View.VISIBLE);
        else
            switchExposure.view.setVisibility(View.GONE);
        if (camMan.parametersManager.getSupportScene())
            switchScene.view.setVisibility(View.VISIBLE);
        else
            switchScene.view.setVisibility(View.GONE);
        view.findViewById(R.id.auto_menu_fragment_layout).requestLayout();
    }

    private void updateValues() {
        if (camMan.parametersManager.getSupportScene())
            switchScene.SetButtonText(camMan.parametersManager.getParameters().getSceneMode());
        switchColor.SetButtonText(camMan.parametersManager.getParameters().getColorEffect());
        if (camMan.parametersManager.getSupportExposureMode())
            switchExposure.SetButtonText(camMan.parametersManager.ExposureMode.get());
        if (camMan.parametersManager.getSupportIso())
            switchIso.SetButtonText(camMan.parametersManager.Iso.get());
        //AF Priority
        if (camMan.parametersManager.getSupportAfpPriority())
        {
            switchAF.SetButtonText(camMan.parametersManager.AfPriority.Get());
        }

        //AutoExposure
        if (camMan.parametersManager.getSupportAutoExposure())
        {
            switchMetering.SetButtonText(camMan.parametersManager.getParameters().get("auto-exposure"));
        }
        if (camMan.parametersManager.getSupportWhiteBalance())
        {
            switchWhiteBalance.SetButtonText(camMan.parametersManager.WhiteBalance.get());
        }
    }
}
