package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_MTK;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_QcomM;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesParameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 31.05.2016.
 */
public abstract class AbstractDevice
{
    protected Handler uihandler;
    protected Camera.Parameters parameters;
    protected CameraHolderApi1 cameraHolder;
    protected CameraUiWrapper cameraUiWrapper;
    protected CamParametersHandler camParametersHandler;
    protected MatrixChooserParameter matrixChooserParameter;

    public AbstractDevice(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper)
    {
        this.uihandler = uihandler;
        this.parameters = parameters;
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = cameraUiWrapper.cameraHolder;
        this.camParametersHandler = (CamParametersHandler) cameraUiWrapper.camParametersHandler;
        if (IsDngSupported())
        {
            this.matrixChooserParameter = new MatrixChooserParameter(uihandler);
            camParametersHandler.matrixChooser = matrixChooserParameter;
        }
    }

    public abstract boolean IsDngSupported();
    public abstract AbstractManualParameter getExposureTimeParameter();
    public abstract AbstractManualParameter getIsoParameter();
    public abstract AbstractManualParameter getManualFocusParameter();
    public abstract AbstractManualParameter getCCTParameter();
    public abstract DngProfile getDngProfile(int filesize);
    public AbstractModeParameter getVideoProfileMode()
    {
        return new VideoProfilesParameter(uihandler,parameters,cameraHolder, "", cameraUiWrapper);
    }
}
