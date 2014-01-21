package com.troop.freecam.manager;

import android.hardware.Camera;
import android.media.MediaPlayer;
import android.util.Log;

import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 31.08.13.
 */
public class AutoFocusManager implements Camera.AutoFocusCallback
{
    CameraManager cameraManager;

    public boolean focusing = false;
    public boolean hasFocus = false;

    public  AutoFocusManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
    }

    public  void  SetFocus(String focusmode)
    {
        if (Camera.Parameters.FOCUS_MODE_AUTO.equals(focusmode))
        {
            cameraManager.parametersManager.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        }
        if ("infinity".equals(focusmode))
        {
            cameraManager.parametersManager.getParameters().setFocusMode("infinity");
            //cameraManager.mCamera.cancelAutoFocus();
            //cameraManager.mCamera.autoFocus(null);
        }
        if (focusmode.equals("off"))
        {
            cameraManager.parametersManager.getParameters().setFocusMode("off");
            //cameraManager.mCamera.cancelAutoFocus();
            //cameraManager.mCamera.autoFocus(null);
        }
        if (focusmode.equals("portrait"))
        {
            cameraManager.parametersManager.getParameters().setFocusMode("portrait");
            //cameraManager.mCamera.cancelAutoFocus();
            //cameraManager.mCamera.autoFocus(this);
        }
        if (focusmode.equals("extended"))
        {
            cameraManager.parametersManager.getParameters().setFocusMode("extended");
            //cameraManager.mCamera.cancelAutoFocus();
            //cameraManager.mCamera.autoFocus(null);
        }
        if (focusmode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            cameraManager.parametersManager.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            //cameraManager.mCamera.cancelAutoFocus();
            //cameraManager.mCamera.autoFocus(null);
        }
        if (focusmode.equals(Camera.Parameters.FOCUS_MODE_MACRO))
        {
            cameraManager.parametersManager.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        }
        if (focusmode.equals(Camera.Parameters.FOCUS_MODE_FIXED))
        {
            cameraManager.parametersManager.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        }

    }

    public void StartFocus()
    {
        if (!focusing && !cameraManager.IsWorking && !cameraManager.HdrRender.IsActive)
        {
            focusing = true;
            try {
                cameraManager.mCamera.autoFocus(this);
                cameraManager.soundPlayer.PlayFocus();
            }
            catch (Exception ex)
            {
                focusing = false;
            }

            /*MediaPlayer mediaPlayer = MediaPlayer.create(cameraManager.activity.getApplicationContext(), R.raw.camerafocus);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                    mp = null;
                }
            });
            //mediaPlayer.setVolume(1,1);
            mediaPlayer.start();*/
        }
    }

    public void CancelFocus()
    {
        cameraManager.mCamera.cancelAutoFocus();
        focusing = false;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {



        /*if(success && cameraManager.takePicture)
        {
            cameraManager.TakePicture();
            cameraManager.takePicture = false;
        }*/
        Log.d("onAutoFocus", "takepicture:" + cameraManager.takePicture);
        Log.d("onAutoFocus", "touchtofocus:" + cameraManager.touchtofocus);
        if (success)
            hasFocus = true;
        else
            hasFocus = false;

        if (success && cameraManager.touchtofocus)
        {
            //cameraManager.TakePicture(cameraManager.preferences.getBoolean("crop", false));
            cameraManager.touchtofocus = false;
        }
        else if (success && !cameraManager.touchtofocus)
        {
            cameraManager.TakePicture(cameraManager.parametersManager.doCropping());
        }
        else
        {
            //cameraManager.mCamera.cancelAutoFocus();
            cameraManager.touchtofocus = false;
            cameraManager.takePicture = false;
        }
        focusing = false;
    }

}
