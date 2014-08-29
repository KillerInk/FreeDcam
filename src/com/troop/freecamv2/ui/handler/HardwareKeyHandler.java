package com.troop.freecamv2.ui.handler;

import android.view.KeyEvent;
import android.view.View;

import com.troop.freecam.utils.DeviceUtils;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.ui.MainActivity_v2;

/**
 * Created by troop on 29.08.2014.
 */
public class HardwareKeyHandler
{
    private final MainActivity_v2 activity;
    private final CameraUiWrapper cameraUiWrapper;

    public HardwareKeyHandler(MainActivity_v2 activity, CameraUiWrapper cameraUiWrapper)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;

    }

    public boolean OnKeyEvent(int keyCode, KeyEvent event)
    {
        boolean set = false;
        if(keyCode == KeyEvent.KEYCODE_3D_MODE ||keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
        {
            set = true;
            cameraUiWrapper.DoWork();

        }
        if(DeviceUtils.isEvo3d() || DeviceUtils.isZTEADV())
        {
            //shutterbutton full pressed
            if (keyCode == KeyEvent.KEYCODE_CAMERA)
            {
                set = true;
                cameraUiWrapper.DoWork();
            }
            // shutterbutton half pressed
            //if (keyCode == KeyEvent.KEYCODE_FOCUS)

        }
        return set;
    }
}
