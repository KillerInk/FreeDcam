package com.troop.freedcam.ui.menu.themes.classic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.CamcorderProfile;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;


import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.ui.AbstractInfoOverlayHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 23.01.2015.
 */
public class InfoOverlayHandler extends AbstractInfoOverlayHandler implements I_ModuleEvent {
    //troopii was here and cleaned up^^
    private final View view;
    TextView batteryLoad;
    TextView Storage;
    TextView pictureSize;
    TextView pictureFormat;
    TextView time;
    Typeface font;

    public InfoOverlayHandler(View view, AppSettingsManager appSettingsManager)
    {
        super(view.getContext(), appSettingsManager);
        this.view = view;
        batteryLoad = (TextView)view.findViewById(R.id.txtViewBattLevel);

        Storage = (TextView)view.findViewById(R.id.txtViewRemainingStorage);
        pictureSize = (TextView)view.findViewById(R.id.textViewRes);
        pictureFormat = (TextView)view.findViewById(R.id.textViewFormat);
        time = (TextView)view.findViewById(R.id.textViewTime);

        switch (appSettingsManager.GetTheme())
        {
            case "Ambient": case "Nubia":
            font = Typeface.createFromAsset(view.getContext().getAssets(),"fonts/arial.ttf");
            Storage.setTypeface(font);
            pictureSize.setTypeface(font);
            pictureFormat.setTypeface(font);
            time.setTypeface(font);
            batteryLoad.setTypeface(font);
            break;
            case "Minimal":
                font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/BRADHITC.TTF");

                Storage.setTypeface(font);
                pictureSize.setTypeface(font);
                pictureFormat.setTypeface(font);
                time.setTypeface(font);
                batteryLoad.setTypeface(font);
                break;
        }
    }

    @Override
    protected void UpdateViews() {
        batteryLoad.setText(batteryLevel);
        pictureFormat.setText(format);
        pictureSize.setText(size);
        time.setText(timeString);
        Storage.setText(storageSpace);
    }

    private void setThemeFonts() {
        switch (appSettingsManager.GetTheme())
        {
            case "Ambient": case "Nubia":
            font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/arial.ttf");
            Storage.setTypeface(font);
            pictureSize.setTypeface(font);
            pictureFormat.setTypeface(font);
            time.setTypeface(font);
            batteryLoad.setTypeface(font);
            break;
            case "Minimal":
                font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/BRADHITC.TTF");

                Storage.setTypeface(font);
                pictureSize.setTypeface(font);
                pictureFormat.setTypeface(font);
                time.setTypeface(font);
                batteryLoad.setTypeface(font);
                break;

            case "Material":
                font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/BOOKOS.TTF");

                Storage.setTypeface(font);
                pictureSize.setTypeface(font);
                pictureFormat.setTypeface(font);
                time.setTypeface(font);
                batteryLoad.setTypeface(font);
                break;


        }
    }





    @Override
    public String ModuleChanged(String module) {


        return null;


    }

    //End defcomg
}
