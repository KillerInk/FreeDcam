package com.troop.freedcam.ui.handler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 23.01.2015.
 */
public class InfoOverlayHandler
{
    //troopii was here and cleaned up^^
    private final Activity context;
    private final AppSettingsManager appSettingsManager;
    TextView BattL;
    TextView Storage;
    TextView Restext;
    TextView FormatTextL;
    private BroadcastReceiver rec;
    Thread t;

    public InfoOverlayHandler(Activity context, AppSettingsManager appSettingsManager)
    {
        this.context = context;
        this.appSettingsManager = appSettingsManager;
        BattL = (TextView)context.findViewById(R.id.txtViewBattLevel);
        registerBatteryEventReciever();
        Storage = (TextView)context.findViewById(R.id.txtViewRemainingStorage);
        Restext = (TextView)context.findViewById(R.id.textViewRes);
        FormatTextL = (TextView)context.findViewById(R.id.textViewFormat);
        startLooperThread();
    }

    private void registerBatteryEventReciever()
    {
        rec = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
                int level = -1;
                if(currentLevel >= 0 && scale > 0)
                {
                    level = (currentLevel * 100) / scale;
                }
                BattL.setText(level+"%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(rec, batteryLevelFilter);
    }

    //i think a handler would be better with postdelayed
    //or more better would be to listen to the onvaluechanged event from parameters
    private void startLooperThread()
    {
        this.t = new Thread()
        {
            @Override
            public  void run() {
                try {
                    while (!isInterrupted()) {


                        Thread.sleep(1000);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trySet();
                                Restext.setText(appSettingsManager.getString(AppSettingsManager.SETTING_PICTURESIZE));
                                if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("bayer"))
                                {
                                    if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals("true"))
                                        FormatTextL.setText("DNG");
                                    else
                                        FormatTextL.setText("RAW");
                                }
                                else
                                    FormatTextL.setText(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT));

                            }
                        });
                    }
                }
                catch (InterruptedException e)
                {

                }
            }
        };
        t.start();
    }

    public void StopUpdating()
    {
        if (t != null)
        {
            try {
                t.interrupt();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        t = null;
    }

    //defcomg was here this should go into some handler class that handles module change
    public void trySet()
    {
        try {
            Storage.setText(StringUtils.readableFileSize(Environment.getExternalStorageDirectory().getUsableSpace()));
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
        return StringUtils.readableFileSize(a);

    }
    private double Calc()
    {
        double calc;
        String res [] = appSettingsManager.getString(AppSettingsManager.SETTING_PICTURESIZE).split("x");

        if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("jpeg"))
            return calc = Integer.parseInt(res[0]) *Integer.parseInt(res[1]) *1.2;
        if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("raw"))
            return calc = Integer.parseInt(res[0]) *Integer.parseInt(res[1]) *1.26;
        if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("dng"))
            return calc = Integer.parseInt(res[0]) * 2 *Integer.parseInt(res[1]) *1.2;

        return 1;
    }

    private static long SDspace()
    {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        stat.restat(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        return bytesAvailable;
    }

    //End defcomg
}
