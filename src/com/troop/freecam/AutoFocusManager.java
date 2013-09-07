package com.troop.freecam;

import android.hardware.Camera;
import android.media.MediaPlayer;

/**
 * Created by troop on 31.08.13.
 */
public class AutoFocusManager implements Camera.AutoFocusCallback
{
    CameraManager cameraManager;

    public  AutoFocusManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
    }

    public  void  SetFocus(String focusmode)
    {
        if (Camera.Parameters.FOCUS_MODE_AUTO.equals(focusmode))
        {
            cameraManager.parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            cameraManager.mCamera.cancelAutoFocus();
            cameraManager.mCamera.autoFocus(this);
        }

        if ("infinity".equals(focusmode))
        {
            cameraManager.parameters.setFocusMode("infinity");
            cameraManager.mCamera.cancelAutoFocus();
            cameraManager.mCamera.autoFocus(null);
        }
        if (focusmode.equals("off"))
        {
            cameraManager.parameters.setFocusMode("off");
            cameraManager.mCamera.cancelAutoFocus();
            cameraManager.mCamera.autoFocus(null);
        }
        if (focusmode.equals("portrait"))
        {
            cameraManager.parameters.setFocusMode("portrait");
            cameraManager.mCamera.cancelAutoFocus();
            cameraManager.mCamera.autoFocus(this);
        }
        if (focusmode.equals("extended"))
        {
            cameraManager.parameters.setFocusMode("extended");
            cameraManager.mCamera.cancelAutoFocus();
            cameraManager.mCamera.autoFocus(this);
        }
        if (focusmode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            cameraManager.parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            cameraManager.mCamera.cancelAutoFocus();
            cameraManager.mCamera.autoFocus(null);
        }
        if (focusmode.equals(Camera.Parameters.FOCUS_MODE_MACRO))
        {
            cameraManager.parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            cameraManager.mCamera.autoFocus(this);
        }

    }

    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {
        MediaPlayer mediaPlayer = MediaPlayer.create(cameraManager.activity.getApplicationContext(), R.raw.camerafocus);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            public void onCompletion(MediaPlayer mp)
            {
                mp.release();
                mp = null;
            }
        });
        //mediaPlayer.setVolume(1,1);
        mediaPlayer.start();

        if(success && cameraManager.takePicture)
        {
            cameraManager.TakePicture();
            cameraManager.takePicture = false;
        }
        if (success && cameraManager.touchtofocus)
        {
            cameraManager.TakePicture();
            cameraManager.touchtofocus = false;
        }
    }

}
