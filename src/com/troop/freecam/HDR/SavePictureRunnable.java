package com.troop.freecam.HDR;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.troop.freecam.utils.BitmapUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 18.11.13.
 */
public class SavePictureRunnable implements  Runnable
{
    byte[] data;
    String path;
    int count;
    boolean rotate;

    public boolean Running = false;


    public SavePictureRunnable(byte[] data, String path, int count, boolean rotate)
    {
        this.data = data;
        this.path = path;
        this.count = count;
        this.rotate = rotate;
    }

    @Override
    public void run()
    {
        Running = true;
        savePic(data, path ,count);
    }

    private void savePic(byte[] data, String path, int count)
    {
        String TAG = "SavePictureHDR";
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //uris[count] = Uri.fromFile(file);
        if(!rotate)
        {
            Log.d(TAG, "save HdrPicture NR" + String.valueOf(count));
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                outStream.write(data);
                outStream.flush();
                outStream.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "save HdrPicture NR" + String.valueOf(count));
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.gc();
            Bitmap orgi = BitmapUtils.loadFromBytes(data);
            orgi = BitmapUtils.rotateBitmap(orgi);
            BitmapUtils.saveBitmapToFile(file, orgi);
            orgi.recycle();
        }
        data = null;
        Running = false;
    }


}
