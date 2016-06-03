package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.KEYS;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseCCTManual;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManual_ExposureTime_FloatToSixty;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManual_ExposureTime_Micro;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomDevice extends AbstractDevice {
    public BaseQcomDevice(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return false;
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter()
    {
        if (parameters.get(KEYS.MAX_EXPOSURE_TIME) != null && parameters.get(KEYS.EXPOSURE_TIME) != null && parameters.get(KEYS.MIN_EXPOSURE_TIME )!= null) {
            if (!parameters.get(KEYS.MAX_EXPOSURE_TIME).contains("."))
                return new ShutterManual_ExposureTime_FloatToSixty(parameters, camParametersHandler, null);
            else
                return new ShutterManual_ExposureTime_Micro(parameters, camParametersHandler, null, KEYS.EXPOSURE_TIME, KEYS.MAX_EXPOSURE_TIME, KEYS.MIN_EXPOSURE_TIME);
        }
        return null;
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter()
    {
        if (parameters.get(KEYS.KEY_MANUAL_FOCUS_POSITION) != null && arrayContainsString(camParametersHandler.FocusMode.GetValues(), KEYS.KEY_FOCUS_MODE_MANUAL))
            return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0,1000,KEYS.KEY_FOCUS_MODE_MANUAL,camParametersHandler,10,1);
        return null;
    }

    @Override
    public AbstractManualParameter getCCTParameter()
    {
        String wbModeval ="", wbcur ="", wbmax = "",wbmin = "";
        if (parameters.get(KEYS.WBCURRENT)!=null)
            wbcur = KEYS.WBCURRENT;
        else if (parameters.get(KEYS.WB_CCT) != null)
            wbcur = KEYS.WB_CCT;
        else if (parameters.get(KEYS.WB_CT) != null)
            wbcur = KEYS.WB_CT;
        else if (parameters.get(KEYS.WB_MANUAL) != null)
            wbcur = KEYS.WB_MANUAL;
        else if (parameters.get(KEYS.MANUAL_WB_VALUE) != null)
            wbcur = KEYS.MANUAL_WB_VALUE;

        if (parameters.get(KEYS.MAX_WB_CCT) != null) {
            wbmax = KEYS.MAX_WB_CCT;
        }
        else if (parameters.get(KEYS.MAX_WB_CT)!= null)
            wbmax =KEYS.MAX_WB_CT;

        if (parameters.get(KEYS.MIN_WB_CCT)!= null) {
            wbmin =KEYS.MIN_WB_CCT;
        } else if (parameters.get(KEYS.MIN_WB_CT)!= null)
            wbmin =KEYS.MIN_WB_CT;

        if (arrayContainsString(camParametersHandler.WhiteBalanceMode.GetValues(), KEYS.WB_MODE_MANUAL))
            wbModeval = KEYS.WB_MODE_MANUAL;
        else if (arrayContainsString(camParametersHandler.WhiteBalanceMode.GetValues(), KEYS.WB_MODE_MANUAL_CCT))
            wbModeval = KEYS.WB_MODE_MANUAL_CCT;

        if (!wbcur.equals("") && !wbmax.equals("") && !wbmin.equals("") && wbModeval.equals(""))
            return new BaseCCTManual(parameters,wbcur,wbmax,wbmin,camParametersHandler,100,wbModeval);
        else
            return null;
    }

    private boolean arrayContainsString(String[] ar,String dif)
    {
        boolean ret = false;
        for (String s: ar)
            if (s.equals(dif))
                ret = true;
        return ret;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return null;
    }
}
