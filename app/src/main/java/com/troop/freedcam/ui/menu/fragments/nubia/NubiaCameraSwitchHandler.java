package com.troop.freedcam.ui.menu.fragments.nubia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.troop.freedcam.R;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.switches.CameraSwitchHandler;

/**
 * Created by troop on 12.03.2015.
 */
public class NubiaCameraSwitchHandler extends CameraSwitchHandler
{

    public NubiaCameraSwitchHandler(View activity, AppSettingsManager appSettingsManager)
    {
        super(activity,appSettingsManager);

    }

    @Override
    protected void initBitmaps() {
        Bitmap back = BitmapFactory.decodeResource(activity.getResources(), R.drawable.nubia_ui_cam_back);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(activity.getResources(), R.drawable.nubia_ui_cam_front);
        bitmaps[1] = front;
        Bitmap back3d = BitmapFactory.decodeResource(activity.getResources(), R.drawable.nubia_ui_cam_3d);
        bitmaps[2] = back3d;
    }
}
