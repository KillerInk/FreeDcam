package com.troop.freedcam.ui.handler;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.BurstModule;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.MainActivity_v2;

/**
 * Created by troop on 26.08.2014.
 */
public class ShutterHandler implements View.OnClickListener, I_ModuleEvent, View.OnTouchListener, View.OnLongClickListener
{

    private final MainActivity_v2 activity;
    private final AbstractCameraUiWrapper cameraUiWrapper;
    ImageView shutterButton;
    String currentModule;
    LinearLayout flashScreen;


    public ShutterHandler(MainActivity_v2 mainActivity, final AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.activity = mainActivity;
        this.cameraUiWrapper = cameraUiWrapper;
        shutterButton = (ImageView)activity.findViewById(R.id.shutter_imageview);
        shutterButton.setOnClickListener(this);
        shutterButton.setOnLongClickListener(this);
        //shutterButton.setOnTouchListener(this);

        flashScreen = (LinearLayout)activity.findViewById(R.id.screen_flash);
        flashScreen.setVisibility(View.GONE);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        this.currentModule = cameraUiWrapper.moduleHandler.GetCurrentModuleName();

    }

    @Override
    public void onClick(View v) {
        DoWork();
    }

    public void DoWork()
    {
        if (!currentModule.equals(ModuleHandler.MODULE_BURST))
        {
            cameraUiWrapper.DoWork();
            flashScreen.setVisibility(View.VISIBLE);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    flashScreen.setVisibility(View.GONE);
                }
            };
            flashScreen.postDelayed(runnable, 50);
        }
    }



    @Override
    public String ModuleChanged(String module)
    {
        currentModule = module;
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        boolean fireagain = false;
        if (currentModule.equals(ModuleHandler.MODULE_BURST))
        {
            fireagain = handelBurstClick(event, fireagain);
        }
        return fireagain;
    }

    public boolean handelBurstClick(MotionEvent event, boolean fireagain) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            BurstModule burstModule = (BurstModule)cameraUiWrapper.moduleHandler.GetCurrentModule();
            if (burstModule !=null) {
                burstModule.EnableBurst(true);
                fireagain = true;
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            BurstModule burstModule = (BurstModule)cameraUiWrapper.moduleHandler.GetCurrentModule();
            if (burstModule !=null) {
                burstModule.EnableBurst(false);
                fireagain = false;
            }
        }
        return fireagain;
    }

    @Override
    public boolean onLongClick(View v)
    {
        OnLongClick();
        return false;
    }

    public void OnLongClick()
    {
        cameraUiWrapper.camParametersHandler.LockExposureAndWhiteBalance(true);
        Toast.makeText(activity, "Exposure and WhiteBalance locked", Toast.LENGTH_LONG).show();
    }
}
