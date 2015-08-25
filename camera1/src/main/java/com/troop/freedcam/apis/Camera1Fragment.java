package com.troop.freedcam.apis;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.ExtendedSurfaceView;
import com.troop.freedcam.camera.R;
import com.troop.freedcam.ui.I_PreviewSizeEvent;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends AbstractCameraFragment
{

    ExtendedSurfaceView extendedSurfaceView;
    TextureView camera;
    TextureView preview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (Build.VERSION.SDK_INT < 18)
        {
            view = inflater.inflate(R.layout.cameraholder1, container, false);
            extendedSurfaceView = (ExtendedSurfaceView) view.findViewById(R.id.exSurface);
            extendedSurfaceView.appSettingsManager = appSettingsManager;

            this.cameraUiWrapper = new CameraUiWrapper(extendedSurfaceView, appSettingsManager);
        }
        else
        {
            view = inflater.inflate(R.layout.cameraholder_rs, container, false);
            camera = (TextureView) view.findViewById(R.id.textureView_camera);
            preview = (TextureView) view.findViewById(R.id.textureView_preview);
            this.cameraUiWrapper = new CameraUiWrapper(camera, preview, appSettingsManager);
        }
        //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(extendedSurfaceView);
        //cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(extendedSurfaceView);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
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
