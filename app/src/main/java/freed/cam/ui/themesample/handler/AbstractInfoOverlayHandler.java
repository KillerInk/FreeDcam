/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;

import com.troop.freedcam.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.settings.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 14.06.2015.
 */
public abstract class AbstractInfoOverlayHandler implements ModuleChangedEvent
{
    private final Handler handler;
    protected CameraWrapperInterface cameraUiWrapper;
    boolean started;
    private final Context context;

    protected String batteryLevel;
    private final BatteryBroadCastListner batteryBroadCastListner;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    protected String timeString;

    //this holds the format for video or picture
    protected String format;
    //this holds the size for video/picture
    protected String size;

    protected String storageSpace;
    private DecimalFormat decimalFormat;

    private final String[] units = { "B", "KB", "MB", "GB", "TB" };

    public AbstractInfoOverlayHandler(Context context)
    {
        this.context = context;
        handler = new Handler();
        batteryBroadCastListner = new BatteryBroadCastListner();
        decimalFormat = new DecimalFormat("#,##0.#");
    }

    public void setCameraUIWrapper(CameraWrapperInterface cameraUIWrapper)
    {
        cameraUiWrapper = cameraUIWrapper;
        if (cameraUIWrapper != null && cameraUIWrapper.getModuleHandler() != null)
            cameraUIWrapper.getModuleHandler().addListner(this);
    }

    @Override
    public void onModuleChanged(String module) {
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
        catch (IllegalArgumentException ex) {
            Log.WriteEx(ex);
        }
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
            if (cameraUiWrapper != null){
                getFormat();
                getStorageSpace();
            }
            else
            {
                format = "";
            }
            UpdateViews();
            startLooperThread();

        }
    };



    protected void UpdateViews()
    {

    }

    private void getFormat()
    {
        if (cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(cameraUiWrapper.getResString(R.string.module_video)))
        {
            if (cameraUiWrapper.getParameterHandler().VideoProfiles != null)
                size = cameraUiWrapper.getParameterHandler().VideoProfiles.GetStringValue();
            else
                size = "";
        }
        else
        {
            if (cameraUiWrapper.getParameterHandler().PictureFormat != null)
                format = cameraUiWrapper.getParameterHandler().PictureFormat.GetStringValue();
            else
                format = "";

            if (cameraUiWrapper.getParameterHandler().PictureSize != null)
                size = cameraUiWrapper.getParameterHandler().PictureSize.GetStringValue();
            else
                size = "";
        }
    }

    public void getStorageSpace()
    {
        try
        {
            //defcomg was here 24/01/2015
            if(!cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(cameraUiWrapper.getResString(R.string.module_video)))
                storageSpace = Avail4PIC();
            else
                storageSpace = readableFileSize(SDspace());
        }
        catch (Exception ex)
        {
            storageSpace = "";
        }


    }

    private String readableFileSize(long size) {
        if(size <= 0) return "0";

        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return decimalFormat.format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
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
        String[] res = AppSettingsManager.getInstance().pictureSize.get().split("x");

        if(AppSettingsManager.getInstance().pictureFormat.get().contains(AppSettingsManager.getInstance().getResString(R.string.bayer_)))
        {
            if (Build.MANUFACTURER.contains("HTC"))
                return Integer.parseInt(res[0]) * 2 *Integer.parseInt(res[1]) * 16 / 8;
            else
                return Integer.parseInt(res[0]) *Integer.parseInt(res[1]) * 10 / 8;
        }
        else
            return Integer.parseInt(res[0]) *Integer.parseInt(res[1]) * 8 / 8;
    }

    private long SDspace()
    {
        long bytesAvailable = 0;
        if (!AppSettingsManager.getInstance().GetWriteExternal()) {
            bytesAvailable = Environment.getExternalStorageDirectory().getUsableSpace();
        }
        else
        {
            StatFs stat = new StatFs(System.getenv("SECONDARY_STORAGE"));
            if(VERSION.SDK_INT > 17)
                bytesAvailable = stat.getFreeBytes();
            else
            {
                bytesAvailable = stat.getAvailableBlocks() * stat.getBlockSize();
            }

        }
        return bytesAvailable;
    }




}
