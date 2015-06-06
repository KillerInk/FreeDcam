package com.troop.freedcam.camera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.AbstractCameraFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_PreviewSizeEvent;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends AbstractCameraFragment
{
    final static String TAG = Camera1Fragment.class.getSimpleName();
    ExtendedSurfaceView extendedSurfaceView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholder, container, false);
        extendedSurfaceView = (ExtendedSurfaceView) view.findViewById(R.id.exSurface);
        this.cameraUiWrapper = new CameraUiWrapper(extendedSurfaceView,appSettingsManager, errorHandler);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(extendedSurfaceView);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(extendedSurfaceView);
        return view;
    }

    @Override
    public void setPreviewSizeEventListner(I_PreviewSizeEvent i_previewSizeEvent)
    {
        extendedSurfaceView.SetOnPreviewSizeCHangedListner(i_previewSizeEvent);
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
    public void DestroyCameraUiWrapper()
    {
        if (cameraUiWrapper != null)
        {
            Log.d(TAG, "Destroying Wrapper");
            cameraUiWrapper.camParametersHandler.ParametersEventHandler.CLEAR();
            cameraUiWrapper.camParametersHandler.ParametersEventHandler = null;
            cameraUiWrapper.moduleHandler.moduleEventHandler.CLEAR();
            cameraUiWrapper.moduleHandler.moduleEventHandler = null;
            cameraUiWrapper.moduleHandler.SetWorkListner(null);
            cameraUiWrapper.StopPreview();
            cameraUiWrapper.StopCamera();
            cameraUiWrapper = null;
            Log.d(TAG, "destroyed cameraWrapper");
        }
    }
}
