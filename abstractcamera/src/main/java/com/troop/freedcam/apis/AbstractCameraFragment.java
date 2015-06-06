package com.troop.freedcam.apis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_PreviewSizeEvent;

/**
 * Created by troop on 06.06.2015.
 */
public abstract class AbstractCameraFragment extends Fragment
{
    final static String TAG = AbstractCameraFragment.class.getSimpleName();

    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected View view;
    protected AppSettingsManager appSettingsManager;
    protected I_error errorHandler;
    protected I_PreviewSizeEvent i_previewSizeEvent;
    public AbstractCameraFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public AbstractCameraUiWrapper GetCameraUiWrapper()
    {
        return cameraUiWrapper;
    }

    public void Init(AppSettingsManager appSettings,I_error errorHandler,I_PreviewSizeEvent i_previewSizeEvent)
    {
        this.appSettingsManager = appSettings;
        this.errorHandler = errorHandler;
        this.i_previewSizeEvent =i_previewSizeEvent;
    }
    public abstract int getMargineLeft();
    public abstract int getMargineRight();
    public abstract int getMargineTop();
    public abstract int getPreviewWidth();
    public abstract int getPreviewHeight();

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
