package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.modes.BaseModeParameter;


/**
 * Created by troop on 26.04.2016.
 */
public class AE_Handler_QcomM
{
    private ShutterManual_ExposureTime_Micro exposureTime;
    private ISOManualParameter isoManual;

    public AE_Handler_QcomM(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler)
    {
        BaseModeParameter AE_Mode = new BaseModeParameter(uihandler, parameters, cameraHolder, "manual-exposure", "manual-exposure-modes");
        AE_Mode.addEventListner(aemodeChangedListner);
        camParametersHandler.AE_PriorityMode = AE_Mode;
        this.exposureTime = new ShutterManual_ExposureTime_FloatToSixty(parameters,camParametersHandler,null);
        camParametersHandler.ManualShutter = exposureTime;
        this.isoManual = new ISOManualParameter(parameters,camParametersHandler);
        camParametersHandler.ManualIso =isoManual;
    }

    AbstractModeParameter.I_ModeParameterEvent aemodeChangedListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            switch (val) {
                case "off":
                    exposureTime.BackgroundIsSetSupportedChanged(false);
                    isoManual.BackgroundIsSetSupportedChanged(false);
                    break;
                case "exp-time-priority":
                    exposureTime.BackgroundIsSetSupportedChanged(true);
                    isoManual.BackgroundIsSetSupportedChanged(false);
                    break;
                case "iso-priority":
                    exposureTime.BackgroundIsSetSupportedChanged(false);
                    isoManual.BackgroundIsSetSupportedChanged(true);
                    break;
                case "user-setting":
                    exposureTime.BackgroundIsSetSupportedChanged(true);
                    isoManual.BackgroundIsSetSupportedChanged(true);
                    break;
            }

        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

}
