package com.troop.freecam.manager;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.troop.freecam.CameraManager;
import com.troop.freecam.HDR.HdrRenderActivity;
import com.troop.freecam.R;
import com.troop.freecam.manager.interfaces.PictureTakeFinish;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 15.10.13.
 */
public class HdrManager implements PictureTakeFinish
{

    private final String TAG = "HdrManager";

    public boolean IsActive = false;

    CameraManager cameraManager;

    private Uri[] uris;

    int count = 0;
    int interval = 500;
    boolean takepicture = false;
    boolean working = false;
    PictureTakeFinish pictureTakeFinish;




    private Handler handler = new Handler();
    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            doAction();
        }
    };

    private void doAction() {
        if (count < 3)
        {
            if (!takepicture)
            {

                starttakePicture();
            }
            else if(takepicture)
                handler.postDelayed(runnable, interval);
        }
        else
        {
            boolean is3d = false;
            String end;
            if (cameraManager.preferences.getString("switchcam", "3D").equals("3D"))
            {
                is3d = true;
            }
            if (is3d)
                end = "jps";
            else
                end = "jpg";


            File sdcardpath = Environment.getExternalStorageDirectory();
            //TODO add intent
            Intent hdractiv = new Intent(cameraManager.activity.getApplicationContext(), HdrRenderActivity.class);
            String[] ar = new String[3];
            ar[0] = uris[0].getPath();
            ar[1] = uris[1].getPath();
            ar[2] = uris[2].getPath();
            hdractiv.putExtra("uris", ar);
            cameraManager.activity.startActivityForResult(hdractiv, 1);
            cameraManager.parameters.set("video-stabilization", "false");
            cameraManager.parametersManager.SetExposureCompensation(0);
            cameraManager.parameters.setAutoExposureLock(false);
            cameraManager.parameters.setAutoWhiteBalanceLock(false);

            //cameraManager.parametersManager.SetBrightness(100);
            //cameraManager.parametersManager.SetContrast(50);
            cameraManager.mCamera.startPreview();
        }
    }

    public HdrManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
        uris  = new Uri[3];
        //HdrRender = new HdrSoftwareProcessor(cameraManager.activity.getBaseContext());
        pictureTakeFinish = this;
    }

    public void TakeHDRPictures(boolean reset)
    {
        /*if (reset)
        count = 0;
        cameraManager.parameters.set("video-stabilization", "true");
        setParameters();
        takepicture = true;
        cameraManager.mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);*/
        System.gc();
        cameraManager.parameters.set("video-stabilization", "true");
        cameraManager.mCamera.setParameters(cameraManager.parameters);
        count = 0;
        starttakePicture();
        //handler.postDelayed(runnable, interval);
    }

    private void starttakePicture()
    {

        setParameters();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        takepicture = true;
        cameraManager.mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    private void setParameters()
    {
        if (cameraManager.parameters.getAutoWhiteBalanceLock() == false)
            cameraManager.parameters.setAutoWhiteBalanceLock(true);
        if (cameraManager.parameters.getAutoExposureLock() == false)
            cameraManager.parameters.setAutoExposureLock(true);
        //disable, frame, center, fft and manual.
        cameraManager.parameters.set("auto-convergence-mode", "disable");

        if (count == 0)
        {
            /*int dif = 30;
            for (int i = 0; i < dif; i++ )
            {
                cameraManager.parametersManager.SetExposureCompensation(i);
            }*/
            cameraManager.parametersManager.SetExposureCompensation(15);
            cameraManager.parametersManager.SetBrightness(80);
            cameraManager.parametersManager.SetContrast(120);

            //cameraManager.parametersManager.SetBrightness(180);
            //cameraManager.parametersManager.SetContrast(100);

        }
        else if (count == 1)
        {
            /*int dif = -15;
            for (int i = 15; i >= dif; i-- )
            {
                cameraManager.parametersManager.SetExposureCompensation(i);
            }*/
            cameraManager.parametersManager.SetExposureCompensation(0);
            cameraManager.parametersManager.SetBrightness(50);
            cameraManager.parametersManager.SetContrast(100);

            //cameraManager.parametersManager.SetBrightness(100);
            //cameraManager.parametersManager.SetContrast(50);
        }
        else if (count == 2)
        {
            /*int dif = -30;
            for (int i = -15; i >= dif; i-- )
            {
                cameraManager.parametersManager.SetExposureCompensation(i);
            }*/
            cameraManager.parametersManager.SetExposureCompensation(-15);
            cameraManager.parametersManager.SetBrightness(20);
            cameraManager.parametersManager.SetContrast(80);

            //cameraManager.parametersManager.SetBrightness(0);
            //cameraManager.parametersManager.SetContrast(0);
        }

    }


    public Camera.PictureCallback jpegCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera)
        {

            boolean is3d = false;
            String end;


            if (cameraManager.preferences.getString("switchcam", "3D").equals("3D"))
            {
                is3d = true;
            }
            if (is3d)
                end = "jps";
            else
                end = "jpg";


            File sdcardpath = Environment.getExternalStorageDirectory();
            if (!sdcardpath.exists())
            {
                Log.e(TAG, "sdcard ist not connected");
                return;

            }else
            {
                savePic(data, end, sdcardpath);
            }
            count++;
            takepicture = false;
            cameraManager.mCamera.startPreview();
            pictureTakeFinish.PictureTakingFinish();
        }
    };

    private void savePic(byte[] data, String end, File sdcardpath) {
        File file = getFilePath(end, sdcardpath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        uris[count] = Uri.fromFile(file);
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

    private File getFilePath(String end, File sdcardpath) {
        File freeCamImageDirectory = new File(sdcardpath.getAbsolutePath() + "/DCIM/FreeCam/Tmp/");
        if (!freeCamImageDirectory.exists())
        {
            Log.d(TAG, "FreeCamFolder Tmp not exists try to create");
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
        File file = new File(String.format(freeCamImageDirectory + "/" + String.valueOf(count) + "." + end));
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        return file;
    }

    public Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {

            MediaPlayer mediaPlayer = MediaPlayer.create(cameraManager.activity.getApplicationContext(), R.raw.camerashutter);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            //mediaPlayer.setVolume(1,1);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
            Log.d("FreeCam", "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    public Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("FreeCam", "onPictureTaken - raw");
        }
    };

    @Override
    public void PictureTakingFinish()
    {
        doAction();
    }



}
