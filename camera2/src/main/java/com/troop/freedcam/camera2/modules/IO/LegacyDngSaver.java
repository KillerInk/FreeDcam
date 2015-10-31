package com.troop.freedcam.camera2.modules.IO;

/**
 * Created by GeorgeKiarie on 10/31/2015.
 */
import android.os.Handler;
import android.util.Log;

import com.troop.androiddng.RawToDng;

import java.io.File;

public class LegacyDngSaver {
    final public String fileEnding = ".dng";
    final RawToDng dngConverter;
    boolean exTSD;

    final String TAG = LegacyDngSaver.class.getSimpleName();
    public LegacyDngSaver(boolean externalSD)
    {
     exTSD = externalSD;
        dngConverter = RawToDng.GetInstance();
    }

    public void processData(byte[] data, File file)
    {




        Log.d(TAG, "Is raw stream");
        int w = 0;
        int h = 0;


        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;


        dngConverter.SetBayerData(data, file.getAbsolutePath());
        float fnum, focal = 0;
        fnum = 2.0f;
        focal = 4.7f;

        dngConverter.setExifData(0, 0, 0, fnum, focal, "0", "0", 0);

        dngConverter.WriteDNG(null);
        dngConverter.RELEASE();


    }

}
