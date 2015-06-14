package com.troop.freedcam.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.CamcorderProfile;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 14.06.2015.
 */
public abstract class AbstractInfoOverlayHandler implements I_ModuleEvent
{
    protected AppSettingsManager appSettingsManager;
    Handler handler;
    protected AbstractCameraUiWrapper cameraUiWrapper;
    boolean started = false;
    Context context;

    protected String batteryLevel;
    BatteryBroadCastListner batteryBroadCastListner;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    protected String timeString;

    //this holds the format for video or picture
    protected String format;
    //this holds the size for video/picture
    protected String size;

    protected String storageSpace;

    public AbstractInfoOverlayHandler(Context context, AppSettingsManager appSettingsManager)
    {
        this.context = context;
        this.appSettingsManager = appSettingsManager;
        handler = new Handler();
        batteryBroadCastListner = new BatteryBroadCastListner();
    }

    public void setCameraUIWrapper(AbstractCameraUiWrapper cameraUIWrapper)
    {
        this.cameraUiWrapper = cameraUIWrapper;
        cameraUIWrapper.moduleHandler.moduleEventHandler.addListner(this);
    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    private void startLooperThread()
    {
        if (started)
            handler.postDelayed(runner, 1000);
    }

    public void StartUpdating()
    {
        started = true;
        context.registerReceiver(batteryBroadCastListner, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        startLooperThread();
    }

    public void StopUpdating()
    {
        started = false;
        handler.removeCallbacks(runner);
        try {
            context.unregisterReceiver(batteryBroadCastListner);
        }
        catch (IllegalArgumentException ex)
        {}

    }

    class BatteryBroadCastListner extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)+"%";
        }
    }

    Runnable runner = new Runnable() {
        @Override
        public void run()
        {
            timeString = dateFormat.format(new Date());
            getFormat();
            getStorageSpace();
            UpdateViews();


            startLooperThread();

        }
    };

    protected void UpdateViews()
    {

    }

    private void getFormat()
    {
        if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
        {
            format = "H264";
            size = appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        }
        else
        {
            if (cameraUiWrapper.camParametersHandler.PictureFormat != null)
                format = cameraUiWrapper.camParametersHandler.PictureFormat.GetValue();
            else
                format = "";
            if (cameraUiWrapper.camParametersHandler.PictureSize != null)
                size = cameraUiWrapper.camParametersHandler.PictureSize.GetValue();
            else
                size = "";
        }
    }

    public void getStorageSpace()
    {
        try {
            //Storage.setText(StringUtils.readableFileSize(Environment.getExternalStorageDirectory().getUsableSpace()));

            //defcomg was here 24/01/2015
            if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
                storageSpace = Avail4PIC();
            else
                storageSpace =StringUtils.readableFileSize(SDspace());
        }
        catch (Exception ex)
        {
            storageSpace = "";
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




}
