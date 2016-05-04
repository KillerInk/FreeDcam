package com.freedcam.apis.apis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 06.06.2015.
 */
public abstract class AbstractCameraFragment extends Fragment
{
    private final String TAG = AbstractCameraFragment.class.getSimpleName();

    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected View view;
    protected CamerUiWrapperRdy onrdy;
    protected AppSettingsManager appSettingsManager;

    public void SetAppSettingsManager(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Logger.d(TAG, "onCreateView");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        super.onViewCreated(view, savedInstanceState);
    }

    public AbstractCameraUiWrapper GetCameraUiWrapper()
    {
        return cameraUiWrapper;
    }

    public void Init(CamerUiWrapperRdy rdy)
    {
        this.onrdy = rdy;
    }



    public void DestroyCameraUiWrapper()
    {
        if (cameraUiWrapper != null)
        {
            Logger.d(TAG, "Destroying Wrapper");
            cameraUiWrapper.camParametersHandler.CLEAR();
            cameraUiWrapper.moduleHandler.moduleEventHandler.CLEAR();
            cameraUiWrapper.moduleHandler.CLEARWORKERLISTNER();
            cameraUiWrapper.StopPreview();
            cameraUiWrapper.StopCamera();
            cameraUiWrapper = null;
            Logger.d(TAG, "destroyed cameraWrapper");
        }
    }

    public interface CamerUiWrapperRdy
    {
        void onCameraUiWrapperRdy(AbstractCameraUiWrapper cameraUiWrapper);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
