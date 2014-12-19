package com.troop.freedcam.sonyapi.parameters;

import android.os.Handler;

import com.troop.freedcam.camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.sonyapi.parameters.manual.ExposureTimeSony;
import com.troop.freedcam.sonyapi.parameters.manual.ZoomManualSony;
import com.troop.freedcam.sonyapi.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.sonyapi.parameters.modes.ExposureModeSony;
import com.troop.freedcam.sonyapi.parameters.modes.I_SonyApi;
import com.troop.freedcam.sonyapi.parameters.modes.PictureSizeSony;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by troop on 13.12.2014.
 */
public class ParameterHandlerSony extends AbstractParameterHandler
{
    CameraHolderSony cameraHolder;
    public SimpleRemoteApi mRemoteApi;
    public Set<String> mAvailableCameraApiSet;
    private Set<String> mSupportedApiSet;
    List<I_SonyApi> parametersChangedList;

    public ParameterHandlerSony(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager, Handler backGroundHandler, Handler uiHandler)
    {
        super(cameraHolder, appSettingsManager, backGroundHandler, uiHandler);
        this.cameraHolder = (CameraHolderSony)cameraHolder;
        ParametersEventHandler = new CameraParametersEventHandler();
        parametersChangedList  = new ArrayList<I_SonyApi>();

    }

    public void SetCameraApiSet(final Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        /*appSettingsManager.context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {*/
                for (int i = 0; i < parametersChangedList.size(); i++)
                {
                    if (parametersChangedList.get(i) == null)
                    {
                        parametersChangedList.remove(i);
                        i--;
                    }
                    else
                        parametersChangedList.get(i).SonyApiChanged(mAvailableCameraApiSet);

                }
           /* }
        })

        appSettingsManager.context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ParametersEventHandler.ParametersHasLoaded();
            }
        });;*/
        ParametersEventHandler.ParametersHasLoaded();

    }

    private void createParameters()
    {
        PictureSize = new PictureSizeSony("getStillSize", "setStillSize", "getAvailableStillSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureSize);
        ExposureMode = new ExposureModeSony("getExposureMode", "setExposureMode", "getAvailableExposureMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ExposureMode);

        Zoom = new ZoomManualSony("","","", this);
        ManualShutter = new ExposureTimeSony("", "","", this);
    }

    public void SetRemoteApi(SimpleRemoteApi api)
    {
        this.mRemoteApi = api;
        createParameters();
    }

    public void SetSupportedApiSet(Set<String> mSupportedApiSet)
    {
        this.mSupportedApiSet = mSupportedApiSet;
    }

    @Override
    public void SetParametersToCamera() {
        super.SetParametersToCamera();
    }

    @Override
    public void LockExposureAndWhiteBalance(boolean lock) {
        super.LockExposureAndWhiteBalance(lock);
    }
}
