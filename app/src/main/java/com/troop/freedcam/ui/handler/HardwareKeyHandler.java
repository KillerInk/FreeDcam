package com.troop.freedcam.ui.handler;

import android.util.Log;
import android.view.KeyEvent;

import com.troop.androiddng.DeviceUtils;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.ExpandableChildExternalShutter;


/**
 * Created by troop on 29.08.2014.
 */
public class HardwareKeyHandler
{
    private final MainActivity_v2 activity;
    private AbstractCameraUiWrapper cameraUiWrapper;
    boolean longKeyPress = false;
    private static String TAG = "freedcam.HardwareKeyHandler";
    AppSettingsManager appSettingsManager;
    ShutterHandler shutterHandler;

    public HardwareKeyHandler(MainActivity_v2 activity, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, ShutterHandler shutterHandler)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.shutterHandler = shutterHandler;
    }

    public boolean OnKeyUp(int keyCode, KeyEvent event)
    {
        boolean set = false;
        longKeyPress = false;
        int appSettingsKeyShutter = 0;

        if (appSettingsManager.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(ExpandableChildExternalShutter.VoLP))
            appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_UP;
        if (appSettingsManager.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(ExpandableChildExternalShutter.VoLM))
            appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_DOWN;
        if (appSettingsManager.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(ExpandableChildExternalShutter.Hook) || appSettingsManager.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(""))
            appSettingsKeyShutter = KeyEvent.KEYCODE_HEADSETHOOK;

        if(keyCode == KeyEvent.KEYCODE_3D_MODE ||keyCode == KeyEvent.KEYCODE_POWER || keyCode == appSettingsKeyShutter || keyCode == KeyEvent.KEYCODE_UNKNOWN)
        {
            set = true;
            Log.d(TAG, "KeyUp");
            activity.shutterItemsFragment.shutterHandler.DoWork();

        }
        if(DeviceUtils.isEvo3d() || DeviceUtils.isZTEADV())
        {
            //shutterbutton full pressed
            if (keyCode == KeyEvent.KEYCODE_CAMERA)
            {
                set = true;
                activity.shutterItemsFragment.shutterHandler.DoWork();
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
            shutterHandler.OnLongClick();

        }

        return set;
    }

    public boolean OnKeyDown(int keyCode, KeyEvent event)
    {
        /*if (event.isLongPress() && !longKeyPress) {
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
        }*/

        return true;
    }
}
