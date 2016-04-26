package com.troop.freedcam.camera.parameters.manual;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.BaseModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

import java.util.HashMap;


/**
 * Created by troop on 26.04.2016.
 */
public class AE_Handler_QcomM
{
    private BaseModeParameter AE_Mode;
    private ShutterManual_ExposureTime_Micro exposureTime;
    private ISOManualParameter isoManual;
    private Handler uihandler;
    private HashMap<String, String> parameters;
    private BaseCameraHolder cameraHolder;
    private CamParametersHandler camParametersHandler;

    public AE_Handler_QcomM(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, CamParametersHandler camParametersHandler)
    {
        this.AE_Mode =new BaseModeParameter(uihandler,parameters,cameraHolder,"manual-exposure","manual-exposure-modes");
        AE_Mode.addEventListner(aemodeChangedListner);
        this.exposureTime = new ShutterManual_ExposureTime_Micro(parameters,camParametersHandler,ShutterClassHandler.TEST.split(","),"cur-exposure-time", "max-exposure-time", "min-exposure-time");
        camParametersHandler.ManualShutter = exposureTime;
        this.isoManual = new ISOManualParameter(parameters,"cur-iso","max-iso", "min-iso",cameraHolder,camParametersHandler);
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
