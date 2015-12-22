package com.troop.freedcam.apis;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.marshmallowpermission.MPermissions;

/**
 * Created by troop on 06.06.2015.
 */
public abstract class AbstractCameraFragment extends Fragment
{
    final static String TAG = AbstractCameraFragment.class.getSimpleName();

    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected View view;
    protected AppSettingsManager appSettingsManager;
    protected CamerUiWrapperRdy onrdy;
    public AbstractCameraFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (onrdy != null)
            onrdy.onCameraUiWrapperRdy(cameraUiWrapper);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public AbstractCameraUiWrapper GetCameraUiWrapper()
    {
        return cameraUiWrapper;
    }

    public void Init(AppSettingsManager appSettings, CamerUiWrapperRdy rdy)
    {
        this.appSettingsManager = appSettings;

        this.onrdy = rdy;
    }

    public abstract int getMargineLeft();
    public abstract int getMargineRight();
    public abstract int getMargineTop();
    public abstract int getPreviewWidth();
    public abstract int getPreviewHeight();
    public abstract SurfaceView getSurfaceView();



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

    public interface CamerUiWrapperRdy
    {
        void onCameraUiWrapperRdy(AbstractCameraUiWrapper cameraUiWrapper);
    }

    protected void checkMarshmallowPermissions()
    {
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            MPermissions.requestCameraPermission(this);
        }
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            MPermissions.requestSDPermission(this);
        }
        if (getActivity().checkSelfPermission(Manifest.permission.CAPTURE_AUDIO_OUTPUT)
                != PackageManager.PERMISSION_GRANTED)
        {
            MPermissions.requestAudioSDPermission(this);
        }
        if (getActivity().checkSelfPermission(Manifest.permission.CAPTURE_VIDEO_OUTPUT)
                != PackageManager.PERMISSION_GRANTED)
        {
            MPermissions.requestAudioVideoPermission(this);
        }

    }

}
