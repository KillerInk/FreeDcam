package com.troop.freedcam.ui.switches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 20.08.2014.
 */
public class CameraSwitchHandler implements View.OnClickListener, I_ParametersLoaded
{
    AbstractCameraUiWrapper cameraUiWrapper;
    protected View activity;
    AppSettingsManager appSettingsManager;
    ImageView imageView;
    int currentCamera;
    protected Bitmap[] bitmaps;
    SurfaceView surfaceView;
    private static String TAG = StringUtils.TAG + CameraSwitchHandler.class.getSimpleName();

    public CameraSwitchHandler(View activity, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;
        imageView = (ImageView)activity.findViewById(R.id.imageView_cameraSwitch);
        imageView.setOnClickListener(this);
        currentCamera = appSettingsManager.GetCurrentCamera();
        initBitmaps();


    }

    protected void initBitmaps()
    {
        bitmaps = new Bitmap[3];
        Bitmap back = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_freed_mode_rear);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_freed_mode_front);
        bitmaps[1] = front;
        Bitmap back3d = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_freed_mode_3d);
        bitmaps[2] = back3d;
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper, SurfaceView view)
    {
        if (cameraUiWrapper instanceof CameraUiWrapperSony)
        {
            imageView.setVisibility(View.GONE);
        }
        else
        {
            imageView.setVisibility(View.VISIBLE);
            this.cameraUiWrapper = cameraUiWrapper;
            cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
            this.surfaceView = view;
        }
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
        Log.d(TAG, "Stop Preview and Camera");
        //cameraUiWrapper.StopPreview();
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
