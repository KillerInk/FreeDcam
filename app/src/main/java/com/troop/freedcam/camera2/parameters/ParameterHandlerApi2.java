package com.troop.freedcam.camera2.parameters;

import com.troop.freedcam.camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.i_camera.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 12.12.2014.
 */
public class ParameterHandlerApi2 extends AbstractParameterHandler
{
    public ParameterHandlerApi2(I_CameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = cameraHolder;
        ParametersEventHandler = new CameraParametersEventHandler();
        this.appSettingsManager = appSettingsManager;
    }
}
