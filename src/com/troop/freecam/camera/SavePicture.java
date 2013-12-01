package com.troop.freecam.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.troop.freecam.manager.ExifManager;
import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecam.manager.interfaces.SavePictureCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 18.10.13.
 */
public class SavePicture
{
    MediaScannerManager mediaScannerManager;
    boolean crop = false;
    Camera.Size size;
    boolean is3d = false;
    byte[] bytes;
    public SavePictureCallback onSavePicture;
    public boolean IsWorking = false;

    public SavePicture(MediaScannerManager mediaScannerManager)
    {
        this.mediaScannerManager = mediaScannerManager;

    }

    public void SaveToSD(byte[] bytes, boolean crop, Camera.Size size, boolean is3d)
    {
        this.crop = crop;
        this.size = size;
        this.is3d = is3d;
        this.bytes = bytes;
        handler.post(runnable);

    }

    private void writePictureToSD(byte[] bytes, File file, boolean crop) throws IOException
    {
        FileOutputStream outStream = null;
        if (crop && is3d)
        {
            Bitmap originalBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Integer newheigt = size.width /32 * 9;
            Integer tocrop = originalBmp.getHeight() - newheigt ;
            outStream = new FileOutputStream(file);
            outStream.write(bytes, 0, bytes.length);
            outStream.flush();
            outStream.close();

            ExifManager manager = new ExifManager();
            manager.LoadExifFrom(file.getAbsolutePath());
            Bitmap croppedBmp = Bitmap.createBitmap(originalBmp, 0, tocrop /2, originalBmp.getWidth(), newheigt);
            outStream = new FileOutputStream(file);
            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            originalBmp.recycle();
            croppedBmp.recycle();
            manager.SaveExifTo(file.getAbsolutePath());
        }
        else
        {
            outStream = new FileOutputStream(file);
            outStream.write(bytes);
            outStream.flush();
            outStream.close();
        }

    }

    public static File getFilePath(String end, File sdcardpath) {
        File freeCamImageDirectory = new File(sdcardpath.getAbsolutePath() + "/DCIM/FreeCam/");
        if (!freeCamImageDirectory.exists())
        {
            Log.d("SavePictureTask", "FreeCamFolder not exists try to create");
            try
            {
                freeCamImageDirectory.mkdir();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }


        }
        File file = new File(String.format(freeCamImageDirectory + "/%d." + end, System.currentTimeMillis()));
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        return file;
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            String end;
            if (is3d)
                end = "jps";
            else
                end = "jpg";
            File sdcardpath = Environment.getExternalStorageDirectory();
            if (!sdcardpath.exists())
            {
                Log.e("SavePicture", "sdcard ist not connected");
                //return null;
            }
            else
            {
                File file = getFilePath(end, sdcardpath);
                try {
                    writePictureToSD(bytes, file, crop);
                    if (onSavePicture != null)
                        onSavePicture.onPictureSaved(file);
                    mediaScannerManager.startScan(Uri.fromFile(file).getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
