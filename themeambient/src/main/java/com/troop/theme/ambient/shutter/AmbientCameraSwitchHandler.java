package com.troop.theme.ambient.shutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.shutter.CameraSwitchHandler;
import com.troop.theme.ambient.R;

/**
 * Created by George on 3/17/2015.
 */
public class AmbientCameraSwitchHandler extends CameraSwitchHandler {
    public AmbientCameraSwitchHandler(View activity, AppSettingsManager appSettingsManager)
    {
        super(activity,appSettingsManager);

    }

    @Override
    protected void initBitmaps()
    {
        bitmaps = new Bitmap[3];
        Bitmap back = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_back);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_front);
        bitmaps[1] = front;
        Bitmap back3d = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_3d);
        bitmaps[2] = back3d;
    }
}
