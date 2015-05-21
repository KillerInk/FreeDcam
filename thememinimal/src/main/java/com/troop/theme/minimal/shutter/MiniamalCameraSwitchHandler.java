package com.troop.theme.minimal.shutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.View;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.classic.shutter.CameraSwitchHandler;
import com.troop.theme.minimal.R;

/**
 * Created by George on 3/17/2015.
 */
public class MiniamalCameraSwitchHandler extends CameraSwitchHandler {


    public MiniamalCameraSwitchHandler(I_Activity activity, AppSettingsManager appSettingsManager, View fragment) {
        super(activity, appSettingsManager, fragment);
    }

    @Override
    protected void initBitmaps()
    {
        bitmaps = new Bitmap[3];
        Bitmap back = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.minimal_ui_camrear);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.minimal_ui_camfront);
        bitmaps[1] = front;
        Bitmap back3d = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.minimal_ui_btn);
        bitmaps[2] = back3d;
    }
}
