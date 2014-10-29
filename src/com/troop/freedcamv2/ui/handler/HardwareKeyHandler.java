package com.troop.freedcamv2.ui.handler;

import android.view.KeyEvent;

import com.troop.freedcamv2.utils.DeviceUtils;
import com.troop.freedcamv2.camera.CameraUiWrapper;
import com.troop.freedcamv2.ui.MainActivity_v2;

/**
 * Created by troop on 29.08.2014.
 */
public class HardwareKeyHandler
{
    private final MainActivity_v2 activity;
    private final CameraUiWrapper cameraUiWrapper;
    boolean longKeyPress = false;

    public HardwareKeyHandler(MainActivity_v2 activity, CameraUiWrapper cameraUiWrapper)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;

    }

    public boolean OnKeyUp(int keyCode, KeyEvent event)
    {
        boolean set = false;
        longKeyPress = false;
        if(keyCode == KeyEvent.KEYCODE_3D_MODE ||keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
        {
            set = true;
            activity.shutterHandler.DoWork();

        }
        if(DeviceUtils.isEvo3d() || DeviceUtils.isZTEADV())
        {
            //shutterbutton full pressed
            if (keyCode == KeyEvent.KEYCODE_CAMERA)
            {
                set = true;
                activity.shutterHandler.DoWork();
            }
            // shutterbutton half pressed
            //if (keyCode == KeyEvent.KEYCODE_FOCUS)

        }
        return true;
    }

    public boolean OnKeyLongPress(int keyCode, KeyEvent event)
    {
        boolean set = false;
        if(keyCode == KeyEvent.KEYCODE_3D_MODE ||keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
        {
            set = true;
            activity.shutterHandler.OnLongClick();

        }

        return set;
    }

    public boolean OnKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK && event.isLongPress() && !longKeyPress)
        {
            longKeyPress = true;
            activity.shutterHandler.OnLongClick();

        }
        return true;
    }
}
