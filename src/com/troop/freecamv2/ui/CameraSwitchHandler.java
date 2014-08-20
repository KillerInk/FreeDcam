package com.troop.freecamv2.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;

/**
 * Created by troop on 20.08.2014.
 */
public class CameraSwitchHandler implements View.OnClickListener
{
    CameraUiWrapper cameraUiWrapper;
    MainActivity_v2 activity;
    AppSettingsManager appSettingsManager;
    ImageView imageView;
    int currentCamera;
    Bitmap[] bitmaps;
    public CameraSwitchHandler(MainActivity_v2 activity, CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        imageView = (ImageView)activity.findViewById(R.id.imageView_cameraSwitch);
        imageView.setOnClickListener(this);
        currentCamera = appSettingsManager.GetCurrentCamera();
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
        switchImage();
    }


    private void switchImage()
    {
        int maxcams = cameraUiWrapper.cameraHolder.CameraCout();
        if (currentCamera++ >= maxcams - 1)
            currentCamera = 0;
        imageView.setImageBitmap(bitmaps[currentCamera]);
        appSettingsManager.SetCurrentCamera(currentCamera);
        cameraUiWrapper.StopPreviewAndCamera();
        cameraUiWrapper.StartPreviewAndCamera();

    }
}
