package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraHolder.Frameworks;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_MTK;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_QcomM;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualMTK;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManual_QcomM;
import com.troop.androiddng.DngProfile;

import static com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter.NEXUS6;

/**
 * Created by troop on 31.05.2016.
 */
public class Xiaomi_Redmi_Note3_QC_MTK extends AbstractDevice
{
    private Frameworks frameworks;
    private AE_Handler_MTK ae_handler_mtk;
    private AE_Handler_QcomM ae_handler_qcomM;
    public Xiaomi_Redmi_Note3_QC_MTK(Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        this.frameworks = cameraHolder.DeviceFrameWork;
        if (frameworks == Frameworks.MTK)
            ae_handler_mtk = new AE_Handler_MTK(parameters,cameraHolder, parametersHandler,2700);
        else
            ae_handler_qcomM = new AE_Handler_QcomM(parameters,cameraHolder, parametersHandler);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
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
            return new FocusManualMTK(parameters, parametersHandler);
        else
            return new FocusManual_QcomM(parameters, parametersHandler,1);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 26023936://xiaomi redmi note3 mtk
                return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.GBRG, 0, matrixChooserParameter.GetCustomMatrix(NEXUS6));
            case 20389888: //xiaomi redmi note3 / pro
                return new DngProfile(64, 4632, 3480, DngProfile.Mipi16, DngProfile.GRBG, 0,matrixChooserParameter.GetCustomMatrix(NEXUS6));
        }
        return null;
    }
}
