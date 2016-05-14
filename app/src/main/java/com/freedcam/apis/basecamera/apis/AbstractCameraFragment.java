package com.freedcam.apis.basecamera.apis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class AbstractCameraFragment extends Fragment
{
    private final String TAG = AbstractCameraFragment.class.getSimpleName();

    //the cameraWrapper to hold
    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected View view;
    //the even listner when the camerauiwrapper is rdy to get attached to ui
    protected CamerUiWrapperRdy onrdy;
    //holds the appsettings
    protected AppSettingsManager appSettingsManager;

    public void SetAppSettingsManager(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
    }

    /**
     *
     * @return the current instance of the cameruiwrapper
     */
    public AbstractCameraUiWrapper GetCameraUiWrapper()
    {
        return cameraUiWrapper;
    }

    /**
     *
     * @param rdy the listner that gets thrown when the cameraUIwrapper
     *            has loaded all stuff and is rdy to get attached to ui.
     */
    public void Init(CamerUiWrapperRdy rdy)
    {
        this.onrdy = rdy;
    }


    /**
     * shutdown the current camera instance
     */
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

    /**
     * inteface for event listning when the camerauiwrapper is rdy
     */
    public interface CamerUiWrapperRdy
    {
        void onCameraUiWrapperRdy(AbstractCameraUiWrapper cameraUiWrapper);
    }

}
