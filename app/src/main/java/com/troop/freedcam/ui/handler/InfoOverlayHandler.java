package com.troop.freedcam.ui.handler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Time;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 23.01.2015.
 */
public class InfoOverlayHandler extends BroadcastReceiver implements I_ModuleEvent {
    //troopii was here and cleaned up^^
    private final Activity context;
    private final AppSettingsManager appSettingsManager;
    TextView batteryLoad;
    TextView Storage;
    TextView pictureSize;
    TextView pictureFormat;
    TextView time;
    boolean started = false;
    AbstractCameraUiWrapper cameraUiWrapper;
    Handler handler = new Handler();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public InfoOverlayHandler(Activity context, AppSettingsManager appSettingsManager)
    {
        this.context = context;
        this.appSettingsManager = appSettingsManager;
        batteryLoad = (TextView)context.findViewById(R.id.txtViewBattLevel);
        context.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Storage = (TextView)context.findViewById(R.id.txtViewRemainingStorage);
        pictureSize = (TextView)context.findViewById(R.id.textViewRes);
        pictureFormat = (TextView)context.findViewById(R.id.textViewFormat);
        time = (TextView)context.findViewById(R.id.textViewTime);
        started = true;
        startLooperThread();
    }

    public void setCameraUIWrapper(AbstractCameraUiWrapper cameraUIWrapper)
    {
        this.cameraUiWrapper = cameraUIWrapper;
        cameraUIWrapper.moduleHandler.moduleEventHandler.addListner(this);
    }

    //i think a handler would be better with postdelayed
    //or more better would be to listen to the onvaluechanged event from parameters
    private void startLooperThread()
    {
        if (started)
            handler.postDelayed(runner, 1000);
    }

    public void StopUpdating()
    {
        started = false;
        handler.removeCallbacks(runner);
        context.unregisterReceiver(this);

    }

    Runnable runner = new Runnable() {
        @Override
        public void run()
        {
            time.setText(dateFormat.format(new Date()));
            if (cameraUiWrapper instanceof CameraUiWrapper)
            {
                pictureSize.setText(cameraUiWrapper.camParametersHandler.PictureSize.GetValue());
                trySet();
                if (appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("bayer")) {
                    if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals("true"))
                        pictureFormat.setText("DNG");
                    else
                        pictureFormat.setText("RAW");
                } else
                    pictureFormat.setText(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT));
            }
            startLooperThread();

        }
    };

    //defcomg was here this should go into some handler class that handles module change
    public void trySet()
    {
        try {
            //Storage.setText(StringUtils.readableFileSize(Environment.getExternalStorageDirectory().getUsableSpace()));

            //defcomg was here 24/01/2015
            Storage.setText(Avail4PIC());
        }
        catch (Exception ex)
        {
            Storage.setText("error");
        }
    }

    private  String Avail4PIC()
    {
        // double calc;
        long done;
        done = (long) Calc();
        long a = SDspace() / done;
        return  a + " left";
    }
    private double Calc()
    {
        double calc;
        String res [] = appSettingsManager.getString(AppSettingsManager.SETTING_PICTURESIZE).split("x");

        if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("bayer"))
        {
            if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals("true"))
                return calc = Integer.parseInt(res[0]) * 2 *Integer.parseInt(res[1]) *1.2;
            else
                return calc = Integer.parseInt(res[0]) *Integer.parseInt(res[1]) *1.26;
        }
        else
            return calc = Integer.parseInt(res[0]) *Integer.parseInt(res[1]) *1.2;
    }

    private static long SDspace()
    {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        stat.restat(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = Environment.getExternalStorageDirectory().getUsableSpace();
        return bytesAvailable;
    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        batteryLoad.setText(String.valueOf(level) + "%");
    }

    //End defcomg
}
