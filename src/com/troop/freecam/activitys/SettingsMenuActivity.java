package com.troop.freecam.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

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
import com.troop.menu.PreviewSizeMenu;
import com.troop.menu.VideoSizesMenu;
import com.troop.menu.ZslMenu;
import com.troop.menu.switchcameramenu;

/**
 * Created by troop on 01.01.14.
 */
public class SettingsMenuActivity extends ButtonsActivity
{
    public ExtendedButton switch3dButton;
    public ExtendedButton flashButton;
    public ExtendedButton focusButton;
    public ExtendedButton buttonPictureFormat;
    public ExtendedButton pictureSizeButton;
    public ExtendedButton previewSizeButton;
    protected ExtendedButton videoSizeButton;
    public ExtendedButton ippButton;
    public ExtendedButton button_denoise;
    //ExtendedButton button_stab;
    public ExtendedButton button_zsl;

    protected CheckBox upsidedown;
    protected CheckBox crop_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSettingsMenuButtons();
    }

    private void initSettingsMenuButtons() {
        switch3dButton = (ExtendedButton) findViewById(R.id.button_switch3d);
        switch3dButton.setOnClickListener(new switchcameramenu(camMan, (MainActivity)this));

        flashButton = (ExtendedButton) findViewById(R.id.button_flash);
        flashButton.setOnClickListener(new FlashMenu(camMan, (MainActivity)this));

        focusButton = (ExtendedButton) findViewById(R.id.button_focus);
        focusButton.setOnClickListener(new FocusMenu(camMan, (MainActivity)this));

        buttonPictureFormat = (ExtendedButton)findViewById(R.id.button_pictureFormat);
        buttonPictureFormat.setOnClickListener(new PictureFormatMenu(camMan,(MainActivity)this));
        //is hidden because the values are not from the cameraParameters
        buttonPictureFormat.setVisibility(View.GONE);

        pictureSizeButton = (ExtendedButton) findViewById(R.id.button_pictureSize);
        pictureSizeButton.setOnClickListener(new PictureSizeMenu(camMan, (MainActivity)this));

        previewSizeButton = (ExtendedButton)findViewById(R.id.button_previewsize);
        previewSizeButton.setOnClickListener(new PreviewSizeMenu(camMan,(MainActivity)this));

        videoSizeButton = (ExtendedButton)findViewById(R.id.button_videoSize);
        videoSizeButton.setOnClickListener(new VideoSizesMenu(camMan,(MainActivity)this));

        ippButton = (ExtendedButton)findViewById(R.id.button_ipp);
        ippButton.setOnClickListener(new IppMenu(camMan, (MainActivity)this));

        button_denoise = (ExtendedButton)findViewById(R.id.button_denoise);
        button_denoise.setOnClickListener(new DenoiseMenu(camMan, (MainActivity)this));

        //07-12-13
        // button_stab = (Button)findViewById(R.id.button_ipp);
        // button_stab.setOnClickListener(new IppMenu(camMan, this));

        button_zsl = (ExtendedButton)findViewById(R.id.buttonZsl);
        button_zsl.setOnClickListener(new ZslMenu(camMan, (MainActivity)this));

        upsidedown = (CheckBox) findViewById(R.id.button_fixupsidedown);

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
        crop_box = (CheckBox)findViewById(R.id.checkBox_crop);
        crop_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (crop_box.isChecked())
                {
                    camMan.crop = true;
                    preferences.edit().putBoolean("crop", true).commit();
                }
                else
                {
                    camMan.crop = false;
                    preferences.edit().putBoolean("crop", false).commit();
                }
            }
        });
        if(!preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D).equals(ParametersManager.SwitchCamera_MODE_3D))
        {
            crop_box.setVisibility(View.GONE);
        }
        else
        {
            crop_box.setChecked(true);
        }
    }
}
