package com.troop.freedcam.ui.handler;

import android.app.Activity;
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
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.text.DecimalFormat;
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
    Typeface font;

    public InfoOverlayHandler(Activity context, AppSettingsManager appSettingsManager)
    {
        this.context = context;
        this.appSettingsManager = appSettingsManager;
        batteryLoad = (TextView)context.findViewById(R.id.txtViewBattLevel);

        Storage = (TextView)context.findViewById(R.id.txtViewRemainingStorage);
        pictureSize = (TextView)context.findViewById(R.id.textViewRes);
        pictureFormat = (TextView)context.findViewById(R.id.textViewFormat);
        time = (TextView)context.findViewById(R.id.textViewTime);

        switch (appSettingsManager.GetTheme())
        {
            case "Ambient": case "Nubia":
            font = Typeface.createFromAsset(context.getAssets(),"fonts/arial.ttf");
            Storage.setTypeface(font);
            pictureSize.setTypeface(font);
            pictureFormat.setTypeface(font);
            time.setTypeface(font);
            batteryLoad.setTypeface(font);
            break;
            case "Minimal":
                font = Typeface.createFromAsset(context.getAssets(), "fonts/BRADHITC.TTF");

                Storage.setTypeface(font);
                pictureSize.setTypeface(font);
                pictureFormat.setTypeface(font);
                time.setTypeface(font);
                batteryLoad.setTypeface(font);
                break;
        }
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

    public void StartUpdating()
    {
        setThemeFonts();
        started = true;
        context.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        startLooperThread();
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
            if (true)
            {
                if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_VIDEO) || cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_LONGEXPO)) {

                    if (!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_LONGEXPO)) {
                        pictureFormat.setText("H264");
                        pictureSize.setText(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
                    }
                }
                else
                {
                    setPictureSize();

                    setPictureFormat();
                }
                trySet();
            }
            startLooperThread();

        }
    };

    private void setPictureSize()
    {
        if (cameraUiWrapper instanceof CameraUiWrapper) {
            if (cameraUiWrapper.camParametersHandler.PictureSize != null)
            {
                String RESRAY[] = cameraUiWrapper.camParametersHandler.PictureSize.GetValue().split("x");
                if (RESRAY.length < 2)
                    return;
                double mp = (Integer.parseInt(RESRAY[0]) * Integer.parseInt(RESRAY[1])) / 1000000;
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                pictureSize.setText(String.valueOf(decimalFormat.format(mp)) + "MP");
            }
        }
        else
            pictureSize.setText(cameraUiWrapper.camParametersHandler.PictureSize.GetValue());
    }

    private void setPictureFormat()
    {
        if (cameraUiWrapper instanceof CameraUiWrapper) {
            if (appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("bayer") || appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("raw")) {
                if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals("true"))
                    pictureFormat.setText("DNG");
                else
                    pictureFormat.setText("RAW");
            } else
                pictureFormat.setText(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT));
        }
        else
            pictureFormat.setText(cameraUiWrapper.camParametersHandler.PictureFormat.GetValue());
    }

    //defcomg was here this should go into some handler class that handles module change
    public void trySet()
    {

        try {
            //Storage.setText(StringUtils.readableFileSize(Environment.getExternalStorageDirectory().getUsableSpace()));

            //defcomg was here 24/01/2015
            if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_VIDEO))
                Storage.setText(Avail4PIC());
            else
                Storage.setText(StringUtils.readableFileSize(SDspace()));
        }
        catch (Exception ex)
        {
            Storage.setText("error");
        }


    }

    private void setThemeFonts() {
        switch (appSettingsManager.GetTheme())
        {
            case "Ambient": case "Nubia":
            font = Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf");
            Storage.setTypeface(font);
            pictureSize.setTypeface(font);
            pictureFormat.setTypeface(font);
            time.setTypeface(font);
            batteryLoad.setTypeface(font);
            break;
            case "Minimal":
                font = Typeface.createFromAsset(context.getAssets(), "fonts/BRADHITC.TTF");

                Storage.setTypeface(font);
                pictureSize.setTypeface(font);
                pictureFormat.setTypeface(font);
                time.setTypeface(font);
                batteryLoad.setTypeface(font);
                break;

            case "Material":
                font = Typeface.createFromAsset(context.getAssets(), "fonts/BOOKOS.TTF");

                Storage.setTypeface(font);
                pictureSize.setTypeface(font);
                pictureFormat.setTypeface(font);
                time.setTypeface(font);
                batteryLoad.setTypeface(font);
                break;


        }
    }

    private String Avail4Video()
    {
        int ByteDiv = 1048576;
        int BitDiv = 8;

        System.out.println("Video "+SDspace());

        long MBytes = SDspace() / ByteDiv;

        System.out.println("Video "+MBytes);

        String profile = appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        VideoProfilesParameter videoProfilesParameter = (VideoProfilesParameter)cameraUiWrapper.camParametersHandler.VideoProfiles;


        CamcorderProfile prof = videoProfilesParameter.GetCameraProfile(profile);
        System.out.println("Video "+prof.videoBitRate);

        long Bitt = (prof.videoBitRate /1000)/8;

        System.out.println("Video "+Bitt);

        return String.valueOf(MBytes/Bitt);
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
