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
import com.troop.freecam.HDR.SavePictureRunnable;
import com.troop.freecam.R;
import com.troop.freecam.manager.interfaces.PictureTakeFinish;

import java.io.File;
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
    SavePictureRunnable saveFirstPic;
    SavePictureRunnable saveSecondPic;
    SavePictureRunnable saveThirdPic;




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
            while (saveFirstPic.Running || saveSecondPic.Running || saveThirdPic.Running )
            {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //File sdcardpath = Environment.getExternalStorageDirectory();

            Intent hdractiv = new Intent(cameraManager.activity.getApplicationContext(), HdrRenderActivity.class);
            String[] ar = new String[3];
            ar[0] = uris[0].getPath();
            ar[1] = uris[1].getPath();
            ar[2] = uris[2].getPath();
            hdractiv.putExtra("uris", ar);
            cameraManager.activity.startActivityForResult(hdractiv, 1);
            //cameraManager.parameters.set("video-stabilization", "false");
            //cameraManager.parametersManager.SetExposureCompensation(0);
            //cameraManager.parameters.setAutoExposureLock(false);
            //cameraManager.parameters.setAutoWhiteBalanceLock(false);

            //cameraManager.parametersManager.SetBrightness(100);
            //cameraManager.parametersManager.SetContrast(50);
            //cameraManager.mCamera.startPreview();
        }
    }

    public HdrManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
        uris  = new Uri[3];
        pictureTakeFinish = this;
    }

    public void TakeHDRPictures(boolean reset)
    {
        
        cameraManager.parametersManager.getParameters().set("video-stabilization", "true");
        cameraManager.mCamera.setParameters(cameraManager.parametersManager.getParameters());
        count = 0;
        starttakePicture();
    }

    private void starttakePicture()
    {

        setParameters();
        /*try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        takepicture = true;
        cameraManager.mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    private void setParameters()
    {
        if (cameraManager.parametersManager.getParameters().getAutoWhiteBalanceLock() == false)
            cameraManager.parametersManager.getParameters().setAutoWhiteBalanceLock(true);
        if (cameraManager.parametersManager.getParameters().getAutoExposureLock() == false)
            cameraManager.parametersManager.getParameters().setAutoExposureLock(true);
        //disable, frame, center, fft and manual.
        cameraManager.parametersManager.getParameters().set("auto-convergence-mode", "disable");


        int conv  = cameraManager.parametersManager.getParameters().getExposureCompensation();

        if (count == 0)
        {
            cameraManager.parametersManager.SetExposureCompensation(30);
            //cameraManager.parametersManager.SetBrightness(60);
            //cameraManager.parametersManager.SetContrast(120);
        }
        else if (count == 1)
        {
            cameraManager.parametersManager.SetExposureCompensation(0);
            //cameraManager.parametersManager.SetBrightness(50);
            //cameraManager.parametersManager.SetContrast(100);
        }
        else if (count == 2)
        {
            cameraManager.parametersManager.SetExposureCompensation(conv - 30);
            //cameraManager.parametersManager.SetBrightness(40);
            //cameraManager.parametersManager.SetContrast(80);
        }

    }


    public Camera.PictureCallback jpegCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera)
        {

            boolean is3d = false;
            String end;


            if (cameraManager.preferences.getString("switchcam", "2D").equals("3D"))
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
                //TODO move saving into new thread for faster picture taking
                File file = getFilePath(end,sdcardpath);
                uris[count] = Uri.fromFile(file);
                boolean upsidedownfix = cameraManager.preferences.getBoolean("upsidedown", false);

                if (count == 0)
                {
                    saveFirstPic = new SavePictureRunnable(data, file.getAbsolutePath(), count, upsidedownfix);
                    handler.post(saveFirstPic);
                }
                else if (count == 1)
                {
                    saveSecondPic = new SavePictureRunnable(data, file.getAbsolutePath(), count, upsidedownfix);
                    handler.post(saveSecondPic);
                }
                else if (count == 2)
                {
                    saveThirdPic = new SavePictureRunnable(data, file.getAbsolutePath(), count, upsidedownfix);
                    handler.post(saveThirdPic);
                }
                //savePic(data, end, sdcardpath);
            }
            count++;
            takepicture = false;
            cameraManager.mCamera.startPreview();
            pictureTakeFinish.PictureTakingFinish();
        }
    };



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
