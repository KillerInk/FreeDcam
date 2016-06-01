package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraHolderApi1.Frameworks;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_MTK;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_QcomM;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualMTK;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManual_QcomM;

/**
 * Created by troop on 31.05.2016.
 */
public class RedmiNote3 extends AbstractDevice
{
    private Frameworks frameworks;
    private AE_Handler_MTK ae_handler_mtk;
    private AE_Handler_QcomM ae_handler_qcomM;
    public RedmiNote3(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler)
    {
        super(uihandler,parameters,cameraHolder,camParametersHandler);
        this.frameworks = cameraHolder.DeviceFrameWork;
        if (frameworks == Frameworks.MTK)
            ae_handler_mtk = new AE_Handler_MTK(parameters,cameraHolder,camParametersHandler);
        else
            ae_handler_qcomM = new AE_Handler_QcomM(uihandler,parameters,cameraHolder,camParametersHandler);
    }
    //gets set due ae handler
    @Override
    public AbstractManualParameter getExposureTimeParameter()
    {
       return null;
    }
    //gets set due ae handler
    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter()
    {
        if (frameworks == Frameworks.MTK)
            return new FocusManualMTK(parameters,camParametersHandler);
        else
            return new FocusManual_QcomM(parameters, camParametersHandler,1);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }
}
