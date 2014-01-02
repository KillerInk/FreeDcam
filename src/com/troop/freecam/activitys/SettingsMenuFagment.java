package com.troop.freecam.activitys;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.controls.ExtendedButton;
import com.troop.freecam.manager.ParametersManager;
import com.troop.menu.DenoiseMenu;
import com.troop.menu.FlashMenu;
import com.troop.menu.FocusMenu;
import com.troop.menu.IppMenu;
import com.troop.menu.PictureFormatMenu;
import com.troop.menu.PictureSizeMenu;
import com.troop.menu.PreviewFormatMenu;
import com.troop.menu.PreviewSizeMenu;
import com.troop.menu.VideoSizesMenu;
import com.troop.menu.ZslMenu;
import com.troop.menu.switchcameramenu;

/**
 * Created by troop on 01.01.14.
 */
public class SettingsMenuFagment extends BaseFragment
{
    public ExtendedButton switch3dButton;
    public ExtendedButton flashButton;
    public ExtendedButton focusButton;
    public ExtendedButton buttonPictureFormat;
    public ExtendedButton pictureSizeButton;
    public ExtendedButton previewSizeButton;
    public ExtendedButton buttonPreviewFormat;
    public ExtendedButton videoSizeButton;
    public ExtendedButton ippButton;
    public ExtendedButton button_denoise;
    //ExtendedButton button_stab;
    public ExtendedButton button_zsl;

    public CheckBox upsidedown;
    public CheckBox crop_box;
    InfoScreenFragment infoScreenFragment;
    public CheckBox checkBoxOnScreen;

    public SettingsMenuFagment(CameraManager camMan, MainActivity activity, InfoScreenFragment infoScreenFragment)
    {
        super(camMan, activity);
        this.infoScreenFragment = infoScreenFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.settingsmenufragment,
                container, false);
        initSettingsMenuButtons();
        return view;
    }

    private void initSettingsMenuButtons()
    {
        switch3dButton = (ExtendedButton) view.findViewById(R.id.button_switch3d);
        switch3dButton.setOnClickListener(new switchcameramenu(camMan, activity));

        flashButton = (ExtendedButton) view.findViewById(R.id.button_flash);
        flashButton.setOnClickListener(new FlashMenu(camMan, activity));

        focusButton = (ExtendedButton) view.findViewById(R.id.button_focus);
        focusButton.setOnClickListener(new FocusMenu(camMan, activity));

        buttonPictureFormat = (ExtendedButton)view.findViewById(R.id.button_pictureFormat);
        buttonPictureFormat.setOnClickListener(new PictureFormatMenu(camMan,activity));
        //is hidden because the values are not from the cameraParameters
        buttonPictureFormat.setVisibility(View.GONE);

        buttonPreviewFormat = (ExtendedButton)view.findViewById(R.id.buttonPreviewFormat);
        buttonPreviewFormat.setOnClickListener(new PreviewFormatMenu(camMan,activity));

        pictureSizeButton = (ExtendedButton) view.findViewById(R.id.button_pictureSize);
        pictureSizeButton.setOnClickListener(new PictureSizeMenu(camMan, activity));

        previewSizeButton = (ExtendedButton)view.findViewById(R.id.button_previewsize);
        previewSizeButton.setOnClickListener(new PreviewSizeMenu(camMan,activity));

        videoSizeButton = (ExtendedButton)view.findViewById(R.id.button_videoSize);
        videoSizeButton.setOnClickListener(new VideoSizesMenu(camMan,activity));

        ippButton = (ExtendedButton)view.findViewById(R.id.button_ipp);
        ippButton.setOnClickListener(new IppMenu(camMan, activity));

        button_denoise = (ExtendedButton)view.findViewById(R.id.button_denoise);
        button_denoise.setOnClickListener(new DenoiseMenu(camMan, activity));

        //07-12-13
        // button_stab = (Button)findViewById(R.id.button_ipp);
        // button_stab.setOnClickListener(new IppMenu(camMan, this));

        button_zsl = (ExtendedButton)view.findViewById(R.id.buttonZsl);
        button_zsl.setOnClickListener(new ZslMenu(camMan, activity));

        upsidedown = (CheckBox) view.findViewById(R.id.button_fixupsidedown);

        if (camMan.parametersManager.isOrientationFIX())
            upsidedown.setChecked(true);
        upsidedown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (upsidedown.isChecked())
                {
                    camMan.parametersManager.setOrientationFix(true);
                    camMan.Stop();
                    camMan.Start();

                    camMan.Restart(true);
                }
                else
                {
                    camMan.parametersManager.setOrientationFix(false);
                    camMan.Stop();
                    camMan.Start();
                    camMan.Restart(true);
                }

            }
        });
        crop_box = (CheckBox)view.findViewById(R.id.checkBox_crop);
        crop_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (crop_box.isChecked())
                {
                    camMan.crop = true;
                    camMan.preferences.edit().putBoolean("crop", true).commit();
                }
                else
                {
                    camMan.crop = false;
                    camMan.preferences.edit().putBoolean("crop", false).commit();
                }
            }
        });
        if(!camMan.preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D).equals(ParametersManager.SwitchCamera_MODE_3D))
        {
            crop_box.setVisibility(View.GONE);
        }
        else
        {
            crop_box.setChecked(true);
        }
        checkBoxOnScreen = (CheckBox)view.findViewById(R.id.checkBoxOnscreen);
        checkBoxOnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (checkBoxOnScreen.isChecked())
                {
                    infoScreenFragment.showCurrentConfig();
                }
                else
                {
                    infoScreenFragment.hideCurrentConfig();
                }
            }
        });

    }
    public void Hide()
    {
        view.setVisibility(View.GONE);
    }

    public void Show()
    {
        view.setVisibility(View.VISIBLE);
    }

    public void UpdateUI(boolean parametersReseted)
    {
        buttonPreviewFormat.SetValue(camMan.parametersManager.getParameters().get("preview-format"));

        previewSizeButton.SetValue(camMan.parametersManager.getParameters().getPreviewSize().width + "x" + camMan.parametersManager.getParameters().getPreviewSize().height);
        button_denoise.SetValue(camMan.parametersManager.Denoise.getDenoiseValue());
        String size1 = String.valueOf(camMan.parametersManager.getParameters().getPictureSize().width) + "x" + String.valueOf(camMan.parametersManager.getParameters().getPictureSize().height);
        pictureSizeButton.SetValue(size1);
        videoSizeButton.SetValue(camMan.parametersManager.videoModes.Width + "x" + camMan.parametersManager.videoModes.Height);

        //ZeroShutterLag
        if (camMan.parametersManager.getSupportZSL())
        {
            if (button_zsl.getVisibility() == View.GONE)
                button_zsl.setVisibility(View.VISIBLE);
            button_zsl.SetValue(camMan.parametersManager.ZSLModes.getValue());
        }
        else
        if (button_zsl.getVisibility() == View.VISIBLE)
            button_zsl.setVisibility(View.GONE);

        //ImagePostProcessing
        if (camMan.parametersManager.getSupportIPP())
        {
            if (ippButton.getVisibility() == View.GONE)
                ippButton.setVisibility(View.VISIBLE);
            ippButton.SetValue(camMan.parametersManager.getParameters().get("ipp"));
        }
        else
            ippButton.setVisibility(View.GONE);
        if (camMan.parametersManager.is3DMode())
        {
            crop_box.setVisibility(View.VISIBLE);
            crop_box.setChecked(camMan.parametersManager.doCropping());
        }
        else
            crop_box.setVisibility(View.GONE);
        //FLASH
        if (!camMan.parametersManager.getSupportFlash())
            flashButton.setVisibility(View.GONE);
        else
        {
            if (flashButton.getVisibility() == View.GONE)
                flashButton.setVisibility(View.VISIBLE);
            flashButton.SetValue(camMan.parametersManager.getParameters().getFlashMode());
        }
        focusButton.SetValue(camMan.parametersManager.getParameters().getFocusMode());
    }
}
