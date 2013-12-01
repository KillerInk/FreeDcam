package com.troop.freecam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;


import com.troop.freecam.manager.ExifManager;
import com.troop.freecam.manager.MediaScannerManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Created by troop on 29.08.13.
 */



public class SavePictureTask extends AsyncTask<byte[], Void, String>
{
    MediaScannerManager mediaScannerManager;
    boolean is3d = false;
    CameraManager cameraManager;
    SharedPreferences preferences;

    final String TAG = "FreeDCam.PictureTask";

    public  SavePictureTask (MediaScannerManager mediaScannerManager, boolean is3d, CameraManager cameraManager)
    {
        this.mediaScannerManager = mediaScannerManager;
        this.is3d = is3d;
        this.cameraManager = cameraManager;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(cameraManager.activity);
    }

    @Override
    protected String doInBackground(byte[]... params)
    {
        Log.d(TAG, "Starting Saving Data");
        FileOutputStream outStream = null;
        String end;

        if (is3d)
            end = "jps";
        else
            end = "jpg";
        File sdcardpath = Environment.getExternalStorageDirectory();
        if (!sdcardpath.exists())
        {
            Log.e(TAG, "sdcard ist not connected");
            return null;
        }
        else
        {
            File file = getFilePath(end, sdcardpath);
            if (file == null) return null;
            //long time = System.currentTimeMillis();
            //URI newuri = MediaStore.Images.Media.INTERNAL_CONTENT_URI.buildUpon().appendPath("DCIM").appendPath("FreeCam").appendPath( time +end ).build();
            //file = new File(newuri);
            Log.d( TAG + " FilePath: ", file.getAbsolutePath());
            Integer bytesize = params[0].length;
            Log.d(TAG+" ByteArraySize: ",bytesize.toString());
            try {
                // write to local sandbox file system
                // Or write to sdcard
                if (preferences.getBoolean("crop", false) == true && is3d)
                {
                    Bitmap originalBmp = BitmapFactory.decodeByteArray(params[0], 0 , params[0].length);
                    outStream = new FileOutputStream(file);
                    outStream.write(params[0], 0 , params[0].length);
                    outStream.flush();
                    outStream.close();
                    ExifManager manager = new ExifManager();
                    manager.LoadExifFrom(file.getAbsolutePath());
                    android.hardware.Camera.Size size = cameraManager.parametersManager.getParameters().getPictureSize();
                    Integer newheigt = size.width /32 * 9;
                    Integer tocrop = originalBmp.getHeight() - newheigt ;
                    //ByteArrayInputStream reader = new ByteArrayInputStream(params[0]);
                    //BufferedInputStream stream = new BufferedInputStream(reader);

                    //Metadata metadataorginal = ImageMetadataReader.readMetadata(stream, false);
                    Bitmap croppedBmp = Bitmap.createBitmap(originalBmp, 0, tocrop /2, originalBmp.getWidth(), newheigt);
                    outStream = new FileOutputStream(file);
                    croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    originalBmp.recycle();
                    croppedBmp.recycle();

                    //ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                    //exifInterface



                }
                else
                {
                    outStream = new FileOutputStream(file);
                    outStream.write(params[0]);
                    outStream.flush();
                    outStream.close();
                }
                //Log.d("SavePictureTask", "onPictureTaken - wrote bytes: " + data.length);
                //new MediaScannerManager().startScan(file.getAbsolutePath());
                //scanMedia(file);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
                //} catch (ImageProcessingException e) {
                // e.printStackTrace();
            }
            finally
            {
            }
            Log.d(TAG, "finished saving");
            return file.getPath();
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

    Bitmap bitmascale;
    @Override
    protected void onPostExecute(String s)
    {
        if (s != null)
        {
            if(bitmascale != null)
                bitmascale.recycle();
            try
            {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap bitmaporg = BitmapFactory.decodeFile(s, options);
                mediaScannerManager.startScan(s);

                int w = cameraManager.activity.thumbButton.getWidth();
                int h = cameraManager.activity.thumbButton.getHeight();
                bitmascale = Bitmap.createScaledBitmap(bitmaporg,w,h,true);
                cameraManager.activity.thumbButton.setImageBitmap(bitmascale);
                cameraManager.lastPicturePath = s;
                bitmaporg.recycle();
                System.gc();
                //bitmascale.recycle();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        //cameraManager.takePicture =false;

        //super.onPostExecute(s);
    }
}
