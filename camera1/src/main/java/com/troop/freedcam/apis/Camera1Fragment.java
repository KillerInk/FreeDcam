package com.troop.freedcam.apis;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholder, container, false);
        extendedSurfaceView = (ExtendedSurfaceView) view.findViewById(R.id.exSurface);
        extendedSurfaceView.SetOnPreviewSizeCHangedListner(i_previewSizeEvent);
        this.cameraUiWrapper = new CameraUiWrapper(extendedSurfaceView,appSettingsManager, errorHandler);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(extendedSurfaceView);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(extendedSurfaceView);
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



}
