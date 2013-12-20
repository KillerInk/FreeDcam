package com.troop.freecam.camera;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.Metadata;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.jni.bitmap_operations.JniBitmapHolder;
import com.troop.freecam.manager.ExifManager;
import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecam.manager.interfaces.SavePictureCallback;
import com.troop.freecam.utils.BitmapUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        if (is3d)
        {
            BitmapUtils.saveBytesToFile(file, bytes);
            //bytes = null;
            if (preferences.getBoolean("upsidedown", false) == true || crop)
            {
                ExifManager manager = new ExifManager();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inDither = false;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                manager.LoadExifFrom(file.getAbsolutePath());
                JniBitmapHolder orgiHolder = new JniBitmapHolder(bitmap);
                bitmap.recycle();
                if (preferences.getBoolean("upsidedown", false) == true)
                {
                    orgiHolder.rotateBitmap180();
                }
                if (crop)
                {
                    Integer newheigt = size.width /32 * 9;
                    Integer tocrop = orgiHolder.getHeight() - newheigt ;
                    orgiHolder.cropBitmap(0, tocrop /2, orgiHolder.getWidth(), newheigt + (tocrop /2));
                }

                //BitmapUtils.saveBitmapToFile(file, orgiHolder.getBitmapAndFree());
                BitmapUtils.saveBitmapNativeToFile(file, orgiHolder);
                manager.SaveExifTo(file.getAbsolutePath());
            }
        }
        else
        {
            if (preferences.getBoolean("upsidedown", false) == true)
            {
                JniBitmapHolder h = new JniBitmapHolder(BitmapUtils.loadFromBytes(bytes));
                h.rotateBitmap180();
                BitmapUtils.saveBitmapNativeToFile(file, h);
                //BitmapUtils.saveBitmapToFile(file, h.getBitmapAndFree());
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
