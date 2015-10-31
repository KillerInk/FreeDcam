package com.troop.freedcam.camera2.modules.IO;

/**
 * Created by GeorgeKiarie on 10/31/2015.
 */
import android.os.Handler;
import android.util.Log;

import com.troop.androiddng.RawToDng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LegacyDngSaver {
    final public String fileEnding = ".dng";
   // final RawToDng dngConverter;
    boolean exTSD;

    final String TAG = LegacyDngSaver.class.getSimpleName();
    public LegacyDngSaver(boolean externalSD)
    {
     exTSD = externalSD;
       // dngConverter = RawToDng.GetInstance();
    }

    public void processData(File file)
    {




        Log.d(TAG, "Is raw stream");
        int w = 0;
        int h = 0;


        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;


        String dng = file.getAbsolutePath().replace("raw","dng");

        final RawToDng dngConverter = RawToDng.GetInstance();

        try {
            dngConverter.SetBayerData(readFile(file), dng);




        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        float fnum, focal = 0;
        fnum = 2.0f;
        focal = 4.7f;

        dngConverter.setExifData(0, 0, 0, fnum, focal, "0", "0", 0);

        dngConverter.WriteDNG(null);
        dngConverter.RELEASE();
        file.delete();


    }

    private static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");

            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

}
