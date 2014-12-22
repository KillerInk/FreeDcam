package com.troop.freedcam.ui.switches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;


import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;

/**
 * Created by troop on 20.08.2014.
 */
public class CameraSwitchHandler implements View.OnClickListener, I_ParametersLoaded
{
    AbstractCameraUiWrapper cameraUiWrapper;
    MainActivity_v2 activity;
    AppSettingsManager appSettingsManager;
    ImageView imageView;
    int currentCamera;
    Bitmap[] bitmaps;
    SurfaceView surfaceView;
    public CameraSwitchHandler(MainActivity_v2 activity, AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager, SurfaceView surfaceView)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        this.surfaceView = surfaceView;
        imageView = (ImageView)activity.findViewById(R.id.imageView_cameraSwitch);
        imageView.setOnClickListener(this);
        currentCamera = appSettingsManager.GetCurrentCamera();
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        bitmaps = new Bitmap[3];
        Bitmap back = BitmapFactory.decodeResource(activity.getResources(), R.drawable.camera_back);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(activity.getResources(), R.drawable.camera_front);
        bitmaps[1] = front;
        Bitmap back3d = BitmapFactory.decodeResource(activity.getResources(), R.drawable.camera_back3d);
        bitmaps[2] = back3d;

    }

    @Override
    public void onClick(View v)
    {
        switchImageAndCamera();
    }

    private void switchImageAndCamera()
    {
        int maxcams = cameraUiWrapper.cameraHolder.CameraCout();
        if (currentCamera++ >= maxcams - 1)
            currentCamera = 0;
        imageView.setImageBitmap(bitmaps[currentCamera]);
        appSettingsManager.SetCurrentCamera(currentCamera);
        cameraUiWrapper.StopPreview();
        cameraUiWrapper.StopCamera();
        if (surfaceView instanceof ExtendedSurfaceView)
        {
            ((ExtendedSurfaceView)surfaceView).SwitchViewMode();
        }
        cameraUiWrapper.StartCamera();
        //cameraUiWrapper.StartPreview();

    }

    @Override
    public void ParametersLoaded()
    {
        imageView.post(new Runnable() {
            @Override
            public void run()
            {
                imageView.setImageBitmap(bitmaps[currentCamera]);
            }
        });

    }
}
