package com.troop.freecam.controls.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.manager.parameters.BaseParametersManager;

/**
 * Created by troop on 02.08.2014.
 */
public class ModeMenuControl extends LinearLayout
{

    CameraManager camMan;
    MainActivity activity;
    Button videoButton;
    Button picButton;
    Button hdrButton;

    boolean menuOpen = false;

    public ModeMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModeMenuControl(Context context) {
        super(context);
    }

    public void SetStuff(CameraManager cameraManager, MainActivity activity)
    {
        this.camMan = cameraManager;
        this.activity = activity;
        initButtons();
    }


    private void initButtons()
    {
        //infalte layout
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.select_mode_layout, this);
        //get controls
        videoButton = (Button)activity.findViewById(R.id.button_mode_Video);
        picButton = (Button)activity.findViewById(R.id.button_mode_Picture);
        hdrButton = (Button)activity.findViewById(R.id.button_mode_HDR);

        //check default mode and set this visible
        switchVisibility();

        videoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (!menuOpen)
                {
                    setAllVisible();
                    menuOpen = true;
                }
                else
                {
                    camMan.Settings.CameraMode.set(SettingsManager.Preferences.MODE_VIDEO);
                    camMan.parametersManager.UpdateGui(true, BaseParametersManager.enumParameters.All);
                    switchVisibility();
                    menuOpen= false;
                }
            }
        });

        picButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (!menuOpen)
                {
                    setAllVisible();
                    menuOpen = true;
                }
                else
                {
                    camMan.Settings.CameraMode.set(SettingsManager.Preferences.MODE_PIC);
                    camMan.parametersManager.UpdateGui(true, BaseParametersManager.enumParameters.All);
                    switchVisibility();
                    menuOpen= false;
                }
            }
        });

        hdrButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (!menuOpen)
                {
                    setAllVisible();
                    menuOpen = true;
                }
                else
                {
                    camMan.Settings.CameraMode.set(SettingsManager.Preferences.MODE_HDR);
                    camMan.parametersManager.UpdateGui(true, BaseParametersManager.enumParameters.All);
                    switchVisibility();
                    menuOpen= false;
                }
            }
        });

    }

    //change the visibility of the controls depending wich mode is set
    private void switchVisibility()
    {
        if (camMan.Settings.CameraMode.get() == SettingsManager.Preferences.MODE_VIDEO)
        {
            videoButton.setVisibility(VISIBLE);
            picButton.setVisibility(GONE);
            hdrButton.setVisibility(GONE);
        }
        else if (camMan.Settings.CameraMode.get() == SettingsManager.Preferences.MODE_PIC)
        {
            videoButton.setVisibility(GONE);
            picButton.setVisibility(VISIBLE);
            hdrButton.setVisibility(GONE);
        }
        else if (camMan.Settings.CameraMode.get() == SettingsManager.Preferences.MODE_HDR)
        {
            videoButton.setVisibility(GONE);
            picButton.setVisibility(GONE);
            hdrButton.setVisibility(VISIBLE);
        }
    }

    private void setAllVisible()
    {
        videoButton.setVisibility(VISIBLE);
        picButton.setVisibility(VISIBLE);
        hdrButton.setVisibility(VISIBLE);
    }
}
