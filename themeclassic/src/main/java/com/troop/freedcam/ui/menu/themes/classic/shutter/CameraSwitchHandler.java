package com.troop.freedcam.ui.menu.themes.classic.shutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.troop.freedcam.camera.ExtendedSurfaceView;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 20.08.2014.
 */
public class CameraSwitchHandler implements View.OnClickListener, I_ParametersLoaded
{
    AbstractCameraUiWrapper cameraUiWrapper;
    protected I_Activity activity;
    AppSettingsManager appSettingsManager;
    ImageView imageView;
    int currentCamera;
    public Bitmap[] bitmaps;
    SurfaceView surfaceView;
    protected View fragment;
    private static String TAG = StringUtils.TAG + CameraSwitchHandler.class.getSimpleName();

    public CameraSwitchHandler(I_Activity activity, AppSettingsManager appSettingsManager, View fragment)
    {
        this.activity = activity;
        this.fragment = fragment;
        this.appSettingsManager = appSettingsManager;
        imageView = (ImageView)fragment.findViewById(R.id.imageView_cameraSwitch);
        imageView.setOnClickListener(this);
        currentCamera = appSettingsManager.GetCurrentCamera();
        initBitmaps();


    }

    protected void initBitmaps()
    {
        bitmaps = new Bitmap[3];
        Bitmap back = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.ic_freed_mode_rear);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.ic_freed_mode_front);
        bitmaps[1] = front;
        Bitmap back3d = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.ic_freed_mode_3d);
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

        if (surfaceView instanceof ExtendedSurfaceView)
        {
            ((ExtendedSurfaceView)surfaceView).SwitchViewMode();
        }
        activity.SwitchCameraAPI(appSettingsManager.getCamApi());
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
