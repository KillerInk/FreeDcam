package com.troop.freecam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 29.08.13.
 */
public class SavePictureTask extends AsyncTask<byte[], Void, String>
{
    MediaScannerManager mediaScannerManager;
    boolean is3d = false;
    CameraManager cameraManager;
    public  SavePictureTask (MediaScannerManager mediaScannerManager, boolean is3d, CameraManager cameraManager)
    {
        this.mediaScannerManager = mediaScannerManager;
        this.is3d = is3d;
        this.cameraManager = cameraManager;
    }

    @Override
    protected String doInBackground(byte[]... params) {
        Log.d("SavePictureTask", "Starting Saving Data");
        FileOutputStream outStream = null;
        String end;
        if (is3d)
            end = "jps";
        else
            end = "jpg";
        File file = new File(String.format("/mnt/sdcard/DCIM/FreeCam/%d." + end, System.currentTimeMillis()));
        try {
            // write to local sandbox file system
            // Or write to sdcard

            outStream = new FileOutputStream(file);
            outStream.write(params[0]);
            outStream.close();
            //Log.d("SavePictureTask", "onPictureTaken - wrote bytes: " + data.length);
            //new MediaScannerManager().startScan(file.getAbsolutePath());
            //scanMedia(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        Log.d("SavePictureTask", "finished saving");
        return file.getPath();
    }

    @Override
    protected void onPostExecute(String s)
    {
        mediaScannerManager.startScan(s);
        Bitmap bitmaporg = BitmapFactory.decodeFile(s);
        int w = cameraManager.activity.thumbButton.getWidth();
        int h = cameraManager.activity.thumbButton.getHeight();
        bitmaporg = Bitmap.createScaledBitmap(bitmaporg,w,h,true);
        cameraManager.activity.thumbButton.setImageBitmap(bitmaporg);
        cameraManager.lastPicturePath = s;

        //super.onPostExecute(s);
    }
}
