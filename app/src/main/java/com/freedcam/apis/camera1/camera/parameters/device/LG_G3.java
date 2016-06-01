package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualParameterLG;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G3 extends AbstractDevice
{

    public LG_G3(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler) {
        super(uihandler, parameters, cameraHolder, camParametersHandler);
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            return new BaseFocusManual(parameters,"manual-focus-position",0,1023,"manual",camParametersHandler,10,1);
        }
        else if (VERSION.SDK_INT < 21)
            return new FocusManualParameterLG(parameters,cameraHolder, camParametersHandler);
        else
            return null;
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }
}
