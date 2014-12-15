package com.troop.freedcam.sonyapi.parameters;

import com.troop.freedcam.camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.i_camera.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.sonyapi.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.sonyapi.parameters.modes.I_SonyApi;
import com.troop.freedcam.sonyapi.parameters.modes.PictureSizeSony;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by troop on 13.12.2014.
 */
public class ParameterHandlerSony extends AbstractParameterHandler
{
    CameraHolderSony cameraHolder;
    private SimpleRemoteApi mRemoteApi;
    private Set<String> mAvailableCameraApiSet;
    private Set<String> mSupportedApiSet;
    List<I_SonyApi> parametersChangedList;

    public ParameterHandlerSony(I_CameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = (CameraHolderSony)cameraHolder;
        this.appSettingsManager = appSettingsManager;
        ParametersEventHandler = new CameraParametersEventHandler();
        parametersChangedList  = new ArrayList<I_SonyApi>();

    }

    public void SetCameraApiSet(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
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

        ParametersEventHandler.ParametersHasLoaded();

    }

    private void createParameters()
    {
        PictureSize = new PictureSizeSony("getStillSize", "setStillSize", "getAvailableStillSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureSize);
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
