package com.troop.freecam.camera;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.jni.bitmap_operations.JniBitmapHolder;
import com.troop.freecam.manager.ExifManager;
import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecam.manager.interfaces.SavePictureCallback;
import com.troop.freecam.utils.BitmapUtils;

import java.io.File;
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
    SharedPreferences preferences;

    public SavePicture(MediaScannerManager mediaScannerManager, SharedPreferences preferences)
    {
        this.mediaScannerManager = mediaScannerManager;
        this.preferences = preferences;
    }

    public void SaveToSD(byte[] bytes, boolean crop, Camera.Size size, boolean is3d)
    {
        this.crop = crop;
        this.size = size;
        this.is3d = is3d;
        this.bytes = bytes;
        bytes = new byte[0];
        handler.post(runnable);

    }

    private void writePictureToSD(byte[] bytes, File file, boolean crop) throws IOException
    {
        FileOutputStream outStream = null;
        if (is3d)
        {
            if (crop)
            {
                Bitmap originalBmp = BitmapUtils.loadFromBytes(bytes);
                BitmapUtils.saveBytesToFile(file, bytes);
                ExifManager manager = new ExifManager();
                manager.LoadExifFrom(file.getAbsolutePath());
                bytes = new byte[0];

                JniBitmapHolder orgiHolder = new JniBitmapHolder(originalBmp);
                originalBmp.recycle();
                if (preferences.getBoolean("upsidedown", false) == true)
                {
                    orgiHolder.rotateBitmap180();
                }

                Integer newheigt = size.width /32 * 9;
                Integer tocrop = originalBmp.getHeight() - newheigt ;

                orgiHolder.cropBitmap(0, tocrop /2, originalBmp.getWidth(), newheigt);
                //final Bitmap croppedBmp = Bitmap.createBitmap(originalBmp, 0, tocrop /2, originalBmp.getWidth(), newheigt);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inDither = true;
                options.inPreferQualityOverSpeed = true;
                //originalBmp.recycle();
                System.gc();
                BitmapUtils.saveBitmapToFile(file, orgiHolder.getBitmapAndFree());
                orgiHolder = null;

                manager.SaveExifTo(file.getAbsolutePath());
            }
            else
            {
                Bitmap originalBmp = BitmapUtils.loadFromBytes(bytes);
                BitmapUtils.saveBytesToFile(file, bytes);
                ExifManager manager = new ExifManager();
                manager.LoadExifFrom(file.getAbsolutePath());
                bytes = new byte[0];

                if (preferences.getBoolean("upsidedown", false) == true)
                {
                    originalBmp = BitmapUtils.rotateBitmap(originalBmp);
                }
                BitmapUtils.saveBitmapToFile(file, originalBmp);
                manager.SaveExifTo(file.getAbsolutePath());
            }
        }
        else
        {
            if (preferences.getBoolean("upsidedown", false) == true)
            {
                Bitmap originalBmp = BitmapUtils.loadFromBytes(bytes);
                bytes = new byte[0];
                
                Bitmap rot = BitmapUtils.rotateBitmap(originalBmp);
                BitmapUtils.saveBitmapToFile(file, rot);

                rot.recycle();
            }
            else
            {
                BitmapUtils.saveBytesToFile(file, bytes);
            }
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
