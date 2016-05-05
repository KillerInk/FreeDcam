package com.freedcam.apis.camera1.camera.parameters.manual;

import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.modes.BaseModeParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;


import java.util.HashMap;


/**
 * Created by troop on 26.04.2016.
 */
public class AE_Handler_QcomM
{
    private ShutterManual_ExposureTime_Micro exposureTime;
    private ISOManualParameter isoManual;

    public AE_Handler_QcomM(Handler uihandler, HashMap<String, String> parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler)
    {
        BaseModeParameter AE_Mode = new BaseModeParameter(uihandler, parameters, cameraHolder, "manual-exposure", "manual-exposure-modes");
        AE_Mode.addEventListner(aemodeChangedListner);
        camParametersHandler.AE_PriorityMode = AE_Mode;
        this.exposureTime = new ShutterManual_ExposureTime_Micro(parameters,camParametersHandler,ShutterClassHandler.TEST.split(","),"exposure-time", "max-exposure-time", "min-exposure-time");
        camParametersHandler.ManualShutter = exposureTime;
        this.isoManual = new ISOManualParameter(parameters,"iso","max-iso", "min-iso",cameraHolder,camParametersHandler);
        camParametersHandler.ISOManual =isoManual;
    }

    AbstractModeParameter.I_ModeParameterEvent aemodeChangedListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            if (val.equals("off"))
            {
                exposureTime.BackgroundIsSetSupportedChanged(false);
                isoManual.BackgroundIsSetSupportedChanged(false);
            }
            else if (val.equals("exp-time-priority"))
            {
                exposureTime.BackgroundIsSetSupportedChanged(true);
                isoManual.BackgroundIsSetSupportedChanged(false);
            }
            else if(val.equals("iso-priority"))
            {
                exposureTime.BackgroundIsSetSupportedChanged(false);
                isoManual.BackgroundIsSetSupportedChanged(true);
            }
            else if (val.equals("user-setting"))
            {
                exposureTime.BackgroundIsSetSupportedChanged(true);
                isoManual.BackgroundIsSetSupportedChanged(true);
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
