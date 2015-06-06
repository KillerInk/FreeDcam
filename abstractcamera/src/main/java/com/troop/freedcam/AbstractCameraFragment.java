package com.troop.freedcam;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected View view;
    protected AppSettingsManager appSettingsManager;
    protected I_error errorHandler;
    public AbstractCameraFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public abstract void setPreviewSizeEventListner(I_PreviewSizeEvent i_previewSizeEvent);
    public AbstractCameraUiWrapper GetCameraUiWrapper()
    {
        return cameraUiWrapper;
    }

    public void Init(AppSettingsManager appSettings,I_error errorHandler)
    {
        this.appSettingsManager = appSettings;
        this.errorHandler = errorHandler;
    }
    public abstract int getMargineLeft();
    public abstract int getMargineRight();
    public abstract int getMargineTop();
    public abstract int getPreviewWidth();
    public abstract int getPreviewHeight();
    public abstract void DestroyCameraUiWrapper();

}
