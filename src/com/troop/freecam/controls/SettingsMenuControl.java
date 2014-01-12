package com.troop.freecam.controls;


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
import com.troop.freecam.manager.SettingsManager;
import com.troop.menu.DenoiseMenu;
import com.troop.menu.FlashMenu;
import com.troop.menu.FocusMenu;
import com.troop.menu.IppMenu;
import com.troop.menu.PictureSizeMenu;
import com.troop.menu.PreviewFormatMenu;
import com.troop.menu.PreviewSizeMenu;
import com.troop.menu.VideoSizesMenu;
import com.troop.menu.ZslMenu;
import com.troop.menu.switchcameramenu;

/**
 * Created by troop on 01.01.14.
 */
public class SettingsMenuControl extends LinearLayout
{
    public Switch upsidedown;
    public Switch crop_box;
    InfoScreenControl infoScreenFragment;
    public Switch checkBoxOnScreen;
    public MenuItemControl switchCamera;
    MenuItemControl switchFlash;
    MenuItemControl switchFocus;
    //MenuItemFragment switchPictureFormat;
    MenuItemControl switchPictureSize;
    MenuItemControl switchPreviewSize;
    MenuItemControl switchPreviewFormat;
    MenuItemControl switchVideoSize;
    MenuItemControl switchIPP;
    MenuItemControl switchDenoise;
    MenuItemControl switchZSL;
    CameraManager camMan;
    MainActivity activity;

    public SettingsMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SettingsMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingsMenuControl(Context context) {
        super(context);
    }

    public void SetStuff(CameraManager cameraManager, MainActivity activity, InfoScreenControl infoScreenFragment)
    {
        this.camMan = cameraManager;
        this.activity = activity;
        this.infoScreenFragment = infoScreenFragment;
        initSettingsMenuButtons();
    }

    /*public SettingsMenuControl(CameraManager camMan, MainActivity activity, InfoScreenFragment infoScreenFragment)
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
    }*/

    private void initSettingsMenuButtons()
    {

        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.settingsmenufragment, this);


        upsidedown = (Switch)findViewById(R.id.button_fixupsidedown);

        if (camMan.Settings.OrientationFix.GET())
            upsidedown.setChecked(true);
        upsidedown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (upsidedown.isChecked())
                {
                    camMan.Settings.OrientationFix.Set(true);
                    camMan.Stop();
                    camMan.Start();

                    camMan.Restart(true);
                }
                else
                {
                    camMan.Settings.OrientationFix.Set(false);
                    camMan.Stop();
                    camMan.Start();
                    camMan.Restart(true);
                }

            }
        });
        crop_box = (Switch)findViewById(R.id.checkBox_crop);
        crop_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (crop_box.isChecked())
                {
                    camMan.crop = true;
                    camMan.Settings.CropImage.Set(true);
                }
                else
                {
                    camMan.crop = false;
                    camMan.Settings.CropImage.Set(false);
                }
            }
        });
        if(!camMan.Settings.Cameras.GetCamera().equals(SettingsManager.Preferences.MODE_3D))
        {
            crop_box.setVisibility(View.GONE);
        }
        else
        {
            crop_box.setChecked(true);
        }
        checkBoxOnScreen = (Switch)findViewById(R.id.checkBoxOnscreen);
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

        //switchCamera = new MenuItemFragment(camMan, activity, "Select Camera", "Front", new switchcameramenu(camMan, activity));
        switchCamera = (MenuItemControl)findViewById(R.id.switch_camera_control);
        switchCamera.SetOnClickListner(new switchcameramenu(camMan, activity));


        //switchFlash = new MenuItemFragment(camMan, activity, "Flash Mode", "", new FlashMenu(camMan, activity));
        switchFlash = (MenuItemControl)findViewById(R.id.switch_flash_control);
        switchFlash.SetOnClickListner(new FlashMenu(camMan, activity));
        //switchFocus = new MenuItemFragment(camMan,activity, "Focus Mode", "", new FocusMenu(camMan, activity));
        switchFocus = (MenuItemControl)findViewById(R.id.switch_focus_control);
        switchFocus.SetOnClickListner(new FocusMenu(camMan, activity));
        //switchPictureFormat = new MenuItemFragment(camMan,activity, "Picture Format", "", new PictureFormatMenu(camMan,activity));
        //switchPictureSize = new MenuItemFragment(camMan,activity,"Picture Size","", new PictureSizeMenu(camMan, activity));
        switchPictureSize = (MenuItemControl)findViewById(R.id.switch_picturesize_control);
        switchPictureSize.SetOnClickListner(new PictureSizeMenu(camMan, activity));

        //switchPreviewSize = new MenuItemFragment(camMan, activity,"Preview Size", "", new PreviewSizeMenu(camMan, activity));
        switchPreviewSize = (MenuItemControl)findViewById(R.id.switch_previewsize_control);
        switchPreviewSize.SetOnClickListner(new PreviewSizeMenu(camMan, activity));

        //switchPreviewFormat = new MenuItemFragment(camMan, activity, "Preview Format", "", new PreviewFormatMenu(camMan, activity));
        switchPreviewFormat = (MenuItemControl)findViewById(R.id.switch_previewformat_control);
        switchPreviewFormat.SetOnClickListner(new PreviewFormatMenu(camMan, activity));

        //switchVideoSize = new MenuItemFragment(camMan, activity, "Video Size", "", new VideoSizesMenu(camMan,activity));
        switchVideoSize = (MenuItemControl)findViewById(R.id.switch_videosize_control);
        switchVideoSize.SetOnClickListner(new VideoSizesMenu(camMan, activity));

        //switchIPP =new MenuItemFragment(camMan, activity, "ImagePostProcessing", "", new IppMenu(camMan,activity));
        switchIPP = (MenuItemControl)findViewById(R.id.switch_ipp_control);
        switchIPP.SetOnClickListner(new IppMenu(camMan,activity));

        //switchDenoise = new MenuItemFragment(camMan, activity, "Denoise", "", new DenoiseMenu(camMan,activity));
        switchDenoise = (MenuItemControl)findViewById(R.id.switch_denoise_control);
        switchDenoise.SetOnClickListner(new DenoiseMenu(camMan,activity));

        switchZSL = (MenuItemControl)findViewById(R.id.switch_zsl_control);
        switchZSL.SetOnClickListner(new ZslMenu(camMan,activity));
        //switchZSL = new MenuItemFragment(camMan, activity, "ZeroShutterLag", "", new ZslMenu(camMan,activity));

        /*FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
        transaction.add(R.id.popupLayoutSettings, switchCamera);
        transaction.add(R.id.popupLayoutSettings, switchFlash);
        transaction.add(R.id.popupLayoutSettings, switchFocus);
        transaction.add(R.id.popupLayoutSettings, switchPictureSize);
        transaction.add(R.id.popupLayoutSettings, switchPreviewSize);
        transaction.add(R.id.popupLayoutSettings, switchPreviewFormat);
        transaction.add(R.id.popupLayoutSettings, switchVideoSize);
        transaction.add(R.id.popupLayoutSettings, switchIPP);
        transaction.add(R.id.popupLayoutSettings, switchDenoise);
        transaction.add(R.id.popupLayoutSettings, switchZSL);
        //transaction.add(R.id.popupLayoutSettings, switchPictureFormat);
        transaction.commit();
        //transaction.show(switchCamera);*/

    }
    public void Hide()
    {
        setVisibility(View.GONE);
    }

    public void Show()
    {
        setVisibility(View.VISIBLE);
    }

    public void UpdateUI(boolean parametersReseted)
    {
        switchPreviewFormat.SetButtonText(camMan.parametersManager.PreviewFormat.Get());

        switchPreviewSize.SetButtonText(camMan.parametersManager.getParameters().getPreviewSize().width + "x" + camMan.parametersManager.getParameters().getPreviewSize().height);
        switchDenoise.SetButtonText(camMan.parametersManager.Denoise.getDenoiseValue());
        String size1 = String.valueOf(camMan.parametersManager.getParameters().getPictureSize().width) + "x" + String.valueOf(camMan.parametersManager.getParameters().getPictureSize().height);
        switchPictureSize.SetButtonText(size1);
        switchVideoSize.SetButtonText(camMan.parametersManager.videoModes.Width + "x" + camMan.parametersManager.videoModes.Height);
        String tmp = camMan.Settings.Cameras.GetCamera();
        //switch3dButton.SetValue(tmp);
        switchCamera.SetButtonText(tmp);

        //ZeroShutterLag
        if (camMan.parametersManager.getSupportZSL())
        {
            if (switchZSL.getVisibility() == View.GONE)
                switchZSL.setVisibility(View.VISIBLE);
            switchZSL.SetButtonText(camMan.parametersManager.ZSLModes.getValue());
        }
        else
        if (switchZSL.getVisibility() == View.VISIBLE)
            switchZSL.setVisibility(View.GONE);

        //ImagePostProcessing
        if (camMan.parametersManager.getSupportIPP())
        {
            if (switchIPP.getVisibility() == View.GONE)
                switchIPP.setVisibility(View.VISIBLE);
            switchIPP.SetButtonText(camMan.parametersManager.getParameters().get("ipp"));
        }
        else
            switchIPP.setVisibility(View.GONE);
        if (camMan.Settings.Cameras.is3DMode())
        {
            crop_box.setVisibility(View.VISIBLE);
            crop_box.setChecked(camMan.parametersManager.doCropping());
        }
        else
            crop_box.setVisibility(View.GONE);
        //FLASH
        if (!camMan.parametersManager.getSupportFlash())
            switchFlash.setVisibility(View.GONE);
        else
        {
            if (switchFlash.getVisibility() == View.GONE)
                switchFlash.setVisibility(View.VISIBLE);
            switchFlash.SetButtonText(camMan.parametersManager.getParameters().getFlashMode());
        }
        switchFocus.SetButtonText(camMan.parametersManager.getParameters().getFocusMode());
    }
}
