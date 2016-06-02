package com.freedcam.apis.camera1.camera.parameters.device.qcom;

import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.KEYS;
import com.freedcam.apis.camera1.camera.parameters.device.BaseQcomDevice;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseCCTManual;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManual_ExposureTime_FloatToSixty;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManual_ExposureTime_Micro;
import com.freedcam.utils.DeviceUtils;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class Xiaomi_Mi3W extends BaseQcomDevice {
    public Xiaomi_Mi3W(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        if(!DeviceUtils.isCyanogenMod()) {
            if (Build.VERSION.SDK_INT < 23) {
                return new BaseCCTManual(parameters, KEYS.WB_MANUAL, 7500, 2000, camParametersHandler, 100, KEYS.WB_MODE_MANUAL);
            } else
                return new BaseCCTManual(parameters, KEYS.WB_MANUAL, 8000, 2000, camParametersHandler, 100, KEYS.WB_MODE_MANUAL_CCT);
        }
        else
            return super.getCCTParameter();
    }

    public boolean IsDngSupported() {
        return true;
    }
    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize)
        {
            case 17522688:
                return new DngProfile(0, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16424960:
                return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 2969600:
                return new DngProfile(64,1976,1200,DngProfile.Mipi16,DngProfile.RGGB,0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 3170304://Xiaomi_mi3 front Qcom
                return new DngProfile(0, 1976, 1200, DngProfile.Qcom, DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}
