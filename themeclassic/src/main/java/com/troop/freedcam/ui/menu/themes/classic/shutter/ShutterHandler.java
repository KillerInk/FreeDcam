package com.troop.freedcam.ui.menu.themes.classic.shutter;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.BurstModule;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;

/**
 * Created by troop on 26.08.2014.
 */
public class ShutterHandler implements View.OnClickListener, I_ModuleEvent, View.OnTouchListener, View.OnLongClickListener
{

    private final View activity;
    private AbstractCameraUiWrapper cameraUiWrapper;
    ImageView shutterButton;
    String currentModule;
    LinearLayout flashScreen;
    Fragment fragment;


    public ShutterHandler(View mainActivity, Fragment fragment)
    {
        this.activity = mainActivity;
        this.fragment = fragment;

        shutterButton = (ImageView)activity.findViewById(R.id.shutter_imageview);
        shutterButton.setOnClickListener(this);
        //shutterButton.setOnLongClickListener(this);
        shutterButton.setOnTouchListener(this);

        flashScreen = (LinearLayout)fragment.getActivity().findViewById(R.id.screen_flash);
        flashScreen.setVisibility(View.GONE);


    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        this.currentModule = cameraUiWrapper.moduleHandler.GetCurrentModuleName();
    }

    @Override
    public void onClick(View v)
    {
        if (cameraUiWrapper instanceof CameraUiWrapperSony) {
            DoWork();
        }
        else if (!currentModule.equals(ModuleHandler.MODULE_PICTURE))
        {
            DoWork();
        }
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
            flashScreen.postDelayed(runnable, 20);
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
        if (!(cameraUiWrapper instanceof CameraUiWrapperSony)) {
            if (currentModule.equals(ModuleHandler.MODULE_PICTURE)) {
                if (event.getButtonState() == MotionEvent.ACTION_DOWN && !cameraUiWrapper.moduleHandler.GetCurrentModule().IsWorking()) {
                    DoWork();
                    fireagain = true;
                } else if (event.getButtonState() == MotionEvent.ACTION_DOWN && cameraUiWrapper.moduleHandler.GetCurrentModule().IsWorking())
                    fireagain = true;
                if (event.getButtonState() == MotionEvent.ACTION_UP)
                    fireagain = false;
            }

        /*if (currentModule.equals(ModuleHandler.MODULE_BURST))
        {
            fireagain = handelBurstClick(event, fireagain);
        }*/
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
        //cameraUiWrapper.camParametersHandler.LockExposureAndWhiteBalance(true);
        //Toast.makeText(activity, "Exposure and WhiteBalance locked", Toast.LENGTH_LONG).show();
    }
}
