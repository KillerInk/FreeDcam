package com.troop.freecam.camera;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPurgeable = true; // Tell to gc that whether it needs free
                // memory, the Bitmap can be cleared
                opts.inInputShareable = true; // Which kind of reference will be used to
                // recover the Bitmap data after being
                // clear, when it will be used in the
                // future
                Bitmap originalBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                saveBytesToFile(file, bytes);
                ExifManager manager = new ExifManager();
                manager.LoadExifFrom(file.getAbsolutePath());
                bytes = new byte[0];
                System.gc();

                if (preferences.getBoolean("upsidedown", false) == true)
                {
                    originalBmp = rotateBitmap(originalBmp);
                }

                Integer newheigt = size.width /32 * 9;
                Integer tocrop = originalBmp.getHeight() - newheigt ;

                final Bitmap croppedBmp = Bitmap.createBitmap(originalBmp, 0, tocrop /2, originalBmp.getWidth(), newheigt);
                originalBmp.recycle();
                saveBitmapToFile(file, croppedBmp);

                manager.SaveExifTo(file.getAbsolutePath());
            }
            else
            {
                Bitmap originalBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                saveBytesToFile(file, bytes);
                ExifManager manager = new ExifManager();
                manager.LoadExifFrom(file.getAbsolutePath());
                bytes = new byte[0];
                System.gc();

                if (preferences.getBoolean("upsidedown", false) == true)
                {
                    originalBmp = rotateBitmap(originalBmp);
                }
                saveBitmapToFile(file, originalBmp);
                manager.SaveExifTo(file.getAbsolutePath());
            }
        }
        else
        {
            if (preferences.getBoolean("upsidedown", false) == true)
            {
                Bitmap originalBmp = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                bytes = new byte[0];
                System.gc();
                Bitmap rot = rotateBitmap(originalBmp);
                saveBitmapToFile(file, rot);

                rot.recycle();
            }
            else
            {
                saveBytesToFile(file, bytes);
            }
        }
    }

    private Bitmap rotateBitmap(Bitmap originalBmp)
    {
        Matrix m = new Matrix();
        m.postRotate(180);
        Bitmap rot = Bitmap.createBitmap(originalBmp, 0, 0, originalBmp.getWidth(), originalBmp.getHeight(), m, false);
        originalBmp.recycle();
        System.gc();
        Runtime.getRuntime().gc();
        System.gc();
        return rot;
    }

    private void saveBitmapToFile(File file, Bitmap bitmap)
    {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveBytesToFile(File file, byte[] bytes)
    {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            outStream.write(bytes);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
