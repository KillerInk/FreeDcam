package com.troop.freedcam.ui.handler;

import android.util.Log;
import android.view.KeyEvent;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.ui.MainActivity_v2;

/**
 * Created by troop on 29.08.2014.
 */
public class HardwareKeyHandler
{
    private final MainActivity_v2 activity;
    private final AbstractCameraUiWrapper cameraUiWrapper;
    boolean longKeyPress = false;
    String TAG = "freedcam.HardwareKeyHandler";

    public HardwareKeyHandler(MainActivity_v2 activity, AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;

    }

    public boolean OnKeyUp(int keyCode, KeyEvent event)
    {
        boolean set = false;
        longKeyPress = false;

        if(keyCode == KeyEvent.KEYCODE_3D_MODE ||keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_UNKNOWN)
        {
            set = true;
            Log.d(TAG, "KeyUp");
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
        if (keyCode == KeyEvent.KEYCODE_BACK)
            activity.finish();
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
        if (event.isLongPress() && !longKeyPress) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                Log.d(TAG, "LongKeyPress for Headsethook");
                longKeyPress = true;
                activity.shutterHandler.OnLongClick();

            }

        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                activity.manualMenuHandler.Decrase();
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                activity.manualMenuHandler.Incrase();
        }

        return true;
    }
}
