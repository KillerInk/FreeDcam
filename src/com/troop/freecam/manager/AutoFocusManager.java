package com.troop.freecam.manager;

import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.hardware.Camera.AutoFocusCallback;

/**
 * Created by troop on 31.08.13.
 */
public class AutoFocusManager
{
    CameraManager cameraManager;

    public boolean focusing = false;
    public boolean hasFocus = false;
    public boolean takePicture = false;
    ImageView crosshair;
    MainActivity activity;
    final int crosshairshowTime = 3000;

    String supportedFocusModes = "auto, extended, manual, macro";

    final String TAG = "freecam.AutoFocusManager";

    public  AutoFocusManager(CameraManager cameraManager, MainActivity activity)
    {
        this.cameraManager = cameraManager;
        this.activity = activity;
        crosshair = (ImageView)activity.findViewById(R.id.imageView_crosshair);
        crosshair.setVisibility(View.GONE);
    }

    public boolean CanFocus()
    {
        if (supportedFocusModes.contains(cameraManager.parametersManager.getParameters().getFocusMode()))
            return true;
        else
            return false;
    }

    public void StartFocus()
    {

        crosshair.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, crosshairshowTime);
        if (!focusing && !cameraManager.IsWorking && !cameraManager.HdrRender.IsActive)
        {
            int half = crosshair.getWidth()/2;
            int halflength = activity.mPreview.getWidth()/2;
            int halfheight = activity.mPreview.getHeight()/2;

            RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) crosshair.getLayoutParams();
            mParams.leftMargin = halflength - half;
            //mParams.rightMargin = x +half;
            mParams.topMargin = halfheight - half;
            //mParams.bottomMargin = y +half;

            crosshair.setLayoutParams(mParams);
            crosshair.bringToFront();

            if (cameraManager.parametersManager.getParameters().getMaxNumFocusAreas() >= 1 )
                cameraManager.parametersManager.getParameters().setFocusAreas(null);
            if(cameraManager.parametersManager.getParameters().getMaxNumMeteringAreas() >= 1)
                cameraManager.parametersManager.getParameters().setMeteringAreas(null);

            doFocus();
        }
    }

    private void doFocus() {
        focusing = true;

        try {
            Log.d(TAG, "Starting Camera Focus");
            cameraManager.mCamera.autoFocus(autofocus);
            cameraManager.soundPlayer.PlayFocus();
        }
        catch (Exception ex)
        {
            focusing = false;
            Log.d(TAG, "Focus failed");
        }
    }

    public void StartTouchToFocus(int x, int y)
    {
        if (!focusing && !cameraManager.IsWorking && !cameraManager.HdrRender.IsActive)
        {
            //focusing = true;
            crosshair.setVisibility(View.VISIBLE);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, crosshairshowTime);

            int half = crosshair.getWidth()/2;
            Log.d(TAG, "start TouchToFocus");
            final Rect targetFocusRect = new Rect(
                    (x - half) * 2000/activity.mPreview.getWidth() - 1000,
                    (y - half) * 2000/activity.mPreview.getHeight() - 1000,
                    (x + half) * 2000/activity.mPreview.getWidth() - 1000,
                    (y + half) * 2000/activity.mPreview.getHeight() - 1000);
            Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
            final List<Camera.Area> meteringList = new ArrayList<Camera.Area>();
            if (cameraManager.parametersManager.getParameters().getMaxNumFocusAreas() >= 1 )
                cameraManager.parametersManager.getParameters().setFocusAreas(meteringList);
            if(cameraManager.parametersManager.getParameters().getMaxNumMeteringAreas() >= 1)
                cameraManager.parametersManager.getParameters().setMeteringAreas(meteringList);

            //crosshair.animate().x(x-half).y(y-half).setDuration(300);
            RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) crosshair.getLayoutParams();
            mParams.leftMargin = x - half;
            //mParams.rightMargin = x +half;
            mParams.topMargin = y -half;
            //mParams.bottomMargin = y +half;

            crosshair.setLayoutParams(mParams);
            crosshair.bringToFront();
            //crosshair.invalidate();
            cameraManager.mCamera.setParameters(cameraManager.parametersManager.getParameters());
            //crosshair.animate().x(mParams.leftMargin).y(mParams.topMargin).setDuration(500).;

            doFocus();
        }
    }

    public void CancelFocus()
    {
        cameraManager.mCamera.cancelAutoFocus();
        focusing = false;
    }

    //get called when a focus event has finished and
    AutoFocusCallback autofocus = new AutoFocusCallback()
    {
        @Override
        public void onAutoFocus(boolean success, Camera camera)
        {
            if (success)
                hasFocus = true;
            else
                hasFocus = false;
            focusing = false;
            if (success && takePicture)
            {
                cameraManager.TakePicture(cameraManager.parametersManager.doCropping());
                takePicture = false;
            }

        }
    };

    private Handler handler = new Handler();
    //this gets called when the touch to focus time has passed crosshairshowTime
    //hides the crosshair and set the focus to false
    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            crosshair.setVisibility(View.GONE);
            hasFocus = false;
        }
    };


}
