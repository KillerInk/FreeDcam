package com.freedcam.apis.camera1.camera.parameters.device.qcom;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseCCTManual;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManualZTE;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class ZTE_ADV_IMX214 extends ZTE_ADV {
    public ZTE_ADV_IMX214(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return new ShutterManualZTE(parameters, cameraHolder, camParametersHandler);
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0,79,KEYS.KEY_FOCUS_MODE_MANUAL,camParametersHandler,1,1);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return new BaseCCTManual(parameters,KEYS.WB_MANUAL_CCT,8000,2000,camParametersHandler,100, KEYS.WB_MODE_MANUAL_CCT);
    }

    public boolean IsDngSupported() {
        return true;
    }
    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 20041728: // IMX234 FUll no crop
                return new DngProfile(64, 5344,3000,DngProfile.Mipi16, DngProfile.RGGB,0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.G4));
        }
        return null;
    }
}
