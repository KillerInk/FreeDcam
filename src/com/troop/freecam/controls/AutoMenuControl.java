package com.troop.freecam.controls;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.fragments.BaseFragment;
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
public class AutoMenuControl extends LinearLayout
{
    Switch checkboxHDR;
    MenuItemControl switchAF;
    MenuItemControl switchScene;
    MenuItemControl switchWhiteBalance;
    MenuItemControl switchColor;
    MenuItemControl switchIso;
    MenuItemControl switchExposure;
    MenuItemControl switchMetering;
    CameraManager camMan;
    MainActivity activity;

    /*public AutoMenuControl(CameraManager camMan, MainActivity activity)
    {
        super(camMan,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.automenufragment, container, false);
        init();
        return view;
    }*/

    public AutoMenuControl(Context context) {
        super(context);
    }

    public AutoMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AutoMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraManager(CameraManager camMan, MainActivity activity)
    {
        this.camMan = camMan;
        this.activity = activity;
        init();
    }

    private void init()
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.automenufragment, this);

        checkboxHDR = (Switch)findViewById(R.id.checkBox_hdr);
        checkboxHDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.HDRMode = checkboxHDR.isChecked();
            }
        });


        //FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
        //switchAF = new MenuItemFragment(camMan, activity, "AutoFocusPrioritys", "", new AFPriorityMenu(camMan,activity));
        switchAF = (MenuItemControl)findViewById(R.id.switch_AfPriority_control);
        switchAF.SetOnClickListner(new AFPriorityMenu(camMan,activity));
        //transaction.add(R.id.auto_menu_fragment_layout, switchAF);
        switchScene = (MenuItemControl)findViewById(R.id.switch_scenemode_control);
        switchScene.SetOnClickListner(new SceneMenu(camMan,activity));
        //switchScene = new MenuItemFragment(camMan,activity,"ScenesModes", "", new SceneMenu(camMan,activity));
        //transaction.add(R.id.auto_menu_fragment_layout, switchScene);
        switchWhiteBalance = (MenuItemControl)findViewById(R.id.switch_wbModes_control);
        switchWhiteBalance.SetOnClickListner(new WhiteBalanceMenu(camMan,activity));
        //switchWhiteBalance = new MenuItemFragment(camMan,activity,"WhiteBalanceModes", "", new WhiteBalanceMenu(camMan,activity));
        //transaction.add(R.id.auto_menu_fragment_layout, switchWhiteBalance);
        switchColor = (MenuItemControl)findViewById(R.id.switch_colormode_control);
        switchColor.SetOnClickListner(new ColorMenu(camMan,activity));
        //switchColor = new MenuItemFragment(camMan,activity,"ColorModes", "", new ColorMenu(camMan,activity));
        switchIso = (MenuItemControl)findViewById(R.id.switch_isomode_control);
        switchIso.SetOnClickListner(new IsoMenu(camMan,activity));
        //switchIso = new MenuItemFragment(camMan,activity,"IsoModes", "", new IsoMenu(camMan,activity));
        switchExposure = (MenuItemControl)findViewById(R.id.switch_exposuremode_control);
        switchExposure.SetOnClickListner(new ExposureMenu(camMan,activity));
        //switchExposure = new MenuItemFragment(camMan,activity, "ExposureModes", "", new ExposureMenu(camMan,activity));
        switchMetering = (MenuItemControl)findViewById(R.id.switch_meteringmode_control);
        switchMetering.SetOnClickListner(new MeteringMenu(camMan,activity));
        /*switchMetering = new MenuItemFragment(camMan,activity,"MeteringModes","", new MeteringMenu(camMan,activity));
        transaction.add(R.id.auto_menu_fragment_layout, switchMetering);
        transaction.add(R.id.auto_menu_fragment_layout, switchColor);
        transaction.add(R.id.auto_menu_fragment_layout, switchIso);
        transaction.add(R.id.auto_menu_fragment_layout, switchExposure);
        transaction.commit();*/
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
            switchAF.setVisibility(View.VISIBLE);
        else
            switchAF.setVisibility(View.GONE);

        if (!camMan.parametersManager.getSupportAutoExposure())
        {
            switchMetering.setVisibility(View.GONE);
        }
        else
            switchMetering.setVisibility(View.VISIBLE);
        if (!camMan.parametersManager.getSupportWhiteBalance())
        {
            switchWhiteBalance.setVisibility(View.GONE);
        }
        else
            switchWhiteBalance.setVisibility(View.VISIBLE);
        if (camMan.parametersManager.getSupportIso())
            switchIso.setVisibility(View.VISIBLE);
        else
            switchIso.setVisibility(View.GONE);
        if (camMan.parametersManager.getSupportExposureMode())
            switchExposure.setVisibility(View.VISIBLE);
        else
            switchExposure.setVisibility(View.GONE);
        if (camMan.parametersManager.getSupportScene())
            switchScene.setVisibility(View.VISIBLE);
        else
            switchScene.setVisibility(View.GONE);
        //findViewById(R.id.auto_menu_fragment_layout).requestLayout();
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
