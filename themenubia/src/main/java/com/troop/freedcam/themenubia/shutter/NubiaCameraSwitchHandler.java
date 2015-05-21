package com.troop.freedcam.themenubia.shutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.View;

import com.troop.freedcam.themenubia.R;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.classic.shutter.CameraSwitchHandler;

/**
 * Created by troop on 12.03.2015.
 */
public class NubiaCameraSwitchHandler extends CameraSwitchHandler
{


    public NubiaCameraSwitchHandler(I_Activity activity, AppSettingsManager appSettingsManager, View fragment) {
        super(activity, appSettingsManager, fragment);
    }

    @Override
    protected void initBitmaps()
    {
        bitmaps = new Bitmap[3];
        Bitmap back = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.nubia_ui_cam_back);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.nubia_ui_cam_front);
        bitmaps[1] = front;
        Bitmap back3d = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.nubia_ui_cam_3d);
        bitmaps[2] = back3d;
    }
}
