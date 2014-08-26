package com.troop.freecamv2.ui.handler;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.BurstModule;
import com.troop.freecamv2.camera.modules.I_ModuleEvent;
import com.troop.freecamv2.camera.modules.ModuleHandler;
import com.troop.freecamv2.ui.MainActivity_v2;

/**
 * Created by troop on 26.08.2014.
 */
public class ShutterHandler implements View.OnClickListener, I_ModuleEvent, View.OnTouchListener
{

    private final MainActivity_v2 activity;
    private final CameraUiWrapper cameraUiWrapper;
    ImageView shutterButton;
    String currentModule;


    public ShutterHandler(MainActivity_v2 mainActivity, final CameraUiWrapper cameraUiWrapper)
    {
        this.activity = mainActivity;
        this.cameraUiWrapper = cameraUiWrapper;
        shutterButton = (ImageView)activity.findViewById(R.id.shutter_imageview);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (!currentModule.equals(ModuleHandler.MODULE_BURST))
                    cameraUiWrapper.DoWork();
            }
        });
        shutterButton.setOnTouchListener(this);

        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        this.currentModule = cameraUiWrapper.moduleHandler.GetCurrentModuleName();

    }

    @Override
    public void onClick(View v) {

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
        }
        return fireagain;
    }
}
