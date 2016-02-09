package com.troop.freedcam.manager;

import android.os.Build;
import android.text.format.DateFormat;

import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by troop on 09.09.2015.
 */
public class FileLogger
{
    private  boolean LOGTOFILE = false;
    private  boolean isrunning = false;
    private  Process process;

    public void StartLogging()
    {
        if (isrunning)
            return;
        LOGTOFILE = true;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                File file = new File(StringUtils.GetInternalSDCARD()+ StringUtils.freedcamFolder + "DEBUG/"+ Build.MODEL + "_" + DateFormat.format("yyyy-MM-dd_hh.mm.ss", new Date().getTime()) + ".txt");
                try {
                    process = Runtime.getRuntime().exec("logcat -f"+file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //processlog();
            }
        }).start();
    }

    public void StopLogging()
    {
        LOGTOFILE = false;
        process.destroy();
        process = null;
    }
}
