package com.troop.freecam.menu.submenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.old.CameraManager;
import com.troop.freecam.controls.MenuItemControl;
import com.troop.freecam.manager.camera_parameters.ParametersManager;
import com.troop.freecam.menu.popupmenu.PreviewFormatMenu;
import com.troop.freecam.menu.popupmenu.PreviewFpsMenu;
import com.troop.freecam.menu.popupmenu.PreviewSizeMenu;

/**
 * Created by troop on 13.01.14.
 */
public class PreviewSubMenuControl extends BaseSubMenu
{
    MenuItemControl switchPreviewSize;
    MenuItemControl switchPreviewFormat;
    MenuItemControl switchPreviewFps;


    public PreviewSubMenuControl(Context context) {
        super(context);
    }

    public PreviewSubMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preview_submenu, this);
    }

    public PreviewSubMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Init(MainActivity activity, CameraManager cameraManager)
    {
        super.Init(activity,cameraManager);

        switchPreviewSize = (MenuItemControl)findViewById(R.id.switch_previewsize_control);
        switchPreviewSize.SetOnClickListner(new PreviewSizeMenu(cameraManager, activity));

        //switchPreviewFormat = new MenuItemFragment(camMan, activity, "Preview Format", "", new PreviewFormatMenu(camMan, activity));
        switchPreviewFormat = (MenuItemControl)findViewById(R.id.switch_previewformat_control);
        switchPreviewFormat.SetOnClickListner(new PreviewFormatMenu(cameraManager, activity));

        switchPreviewFps = (MenuItemControl)findViewById(R.id.switch_previewfps_control);
        switchPreviewFps.SetOnClickListner(new PreviewFpsMenu(cameraManager, activity));
    }

    public void UpdateUI(ParametersManager.enumParameters paras)
    {
        if (paras == ParametersManager.enumParameters.All || paras == ParametersManager.enumParameters.PreviewFormat)
            switchPreviewFormat.SetButtonText(cameraManager.parametersManager.PreviewFormat.Get());
        if (paras == ParametersManager.enumParameters.All || paras == ParametersManager.enumParameters.PreviewSize)
            switchPreviewSize.SetButtonText(cameraManager.parametersManager.getParameters().getPreviewSize().width + "x" + cameraManager.parametersManager.getParameters().getPreviewSize().height);
        if (paras == ParametersManager.enumParameters.All || paras == ParametersManager.enumParameters.PreviewFps)
            switchPreviewFps.SetButtonText(cameraManager.parametersManager.PreviewFps.Get()+ "");
    }

}
