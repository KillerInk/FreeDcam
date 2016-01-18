package com.troop.freedcam.apis;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.ExtendedSurfaceView;
import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.TextureViewRatio;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends AbstractCameraFragment
{

    ExtendedSurfaceView extendedSurfaceView;
    TextureViewRatio preview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.cameraholder1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        extendedSurfaceView = (ExtendedSurfaceView) view.findViewById(R.id.exSurface);
        extendedSurfaceView.appSettingsManager = appSettingsManager;
        preview = (TextureViewRatio) view.findViewById(R.id.textureView_preview);
        this.cameraUiWrapper = new CameraUiWrapper(extendedSurfaceView, preview, appSettingsManager);
        //call super at end because its throws on camerardy event
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getMargineLeft() {
        return extendedSurfaceView.getLeft();
    }

    @Override
    public int getMargineRight() {
        return extendedSurfaceView.getRight();
    }

    @Override
    public int getMargineTop() {
        return extendedSurfaceView.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return extendedSurfaceView.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return extendedSurfaceView.getHeight();
    }

    @Override
    public SurfaceView getSurfaceView() {
        return extendedSurfaceView;
    }



}
