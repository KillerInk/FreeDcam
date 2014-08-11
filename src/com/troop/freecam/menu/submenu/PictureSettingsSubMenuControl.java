package com.troop.freecam.menu.submenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.MenuItemControl;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.camera_parameters.BaseParametersManager;
import com.troop.freecam.manager.camera_parameters.ParametersManager;
import com.troop.freecam.menu.popupmenu.PictureFormatMenu;
import com.troop.freecam.menu.popupmenu.PictureSizeMenu;

/**
 * Created by troop on 11.08.2014.
 */
public class PictureSettingsSubMenuControl extends BaseSubMenu
{
    MenuItemControl switchPictureFormat;
    MenuItemControl switchPictureSize;
    public Switch ExynosRaw;
    public Switch tripod;

    public PictureSettingsSubMenuControl(Context context) {
        super(context);
    }

    public PictureSettingsSubMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.picturesettings_submenu, this);
    }

    public PictureSettingsSubMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Init(MainActivity activity, final CameraManager cameraManager)
    {
        super.Init(activity, cameraManager);
        switchPictureFormat = (MenuItemControl)findViewById(R.id.switch_pictureFormat);
        switchPictureFormat.SetOnClickListner(new PictureFormatMenu(cameraManager, activity));

        switchPictureSize = (MenuItemControl)findViewById(R.id.switch_picturesize_control);
        switchPictureSize.SetOnClickListner(new PictureSizeMenu(cameraManager, activity));

        ExynosRaw = (Switch)findViewById(R.id.button_rawsave);
        ExynosRaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (ExynosRaw.isChecked())
                {
                    cameraManager.parametersManager.setExynosRaw("raw-save");

                }
                else
                {
                    cameraManager.parametersManager.setExynosRaw("single");

                }
            }
        });

        tripod = (Switch)findViewById(R.id.button_tripod);
        tripod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (tripod.isChecked())
                {
                    cameraManager.parametersManager.setNightEnable("tripod");

                }
                else
                {
                    cameraManager.parametersManager.setNightEnable("off");

                }
            }
        });

    }

    public void UpdateUI(ParametersManager.enumParameters paras)
    {
        if (paras == ParametersManager.enumParameters.All)
            switchPictureFormat.SetButtonText(cameraManager.parametersManager.getPictureFormat());

        if (paras == ParametersManager.enumParameters.PictureSize || paras == ParametersManager.enumParameters.All)
        {
            String size1 = String.valueOf(cameraManager.parametersManager.getParameters().getPictureSize().width) + "x" + String.valueOf(cameraManager.parametersManager.getParameters().getPictureSize().height);
            switchPictureSize.SetButtonText(size1);
        }
        if (cameraManager.Settings.CameraMode.get().equals(AppSettingsManager.Preferences.MODE_PIC) || cameraManager.Settings.CameraMode.get().equals(AppSettingsManager.Preferences.MODE_HDR))
        {
            switchPictureSize.setVisibility(VISIBLE);
        }
        else
            switchPictureSize.setVisibility(GONE);
    }
}
