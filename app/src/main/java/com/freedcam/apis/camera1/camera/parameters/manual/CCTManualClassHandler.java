package com.freedcam.apis.camera1.camera.parameters.manual;

import android.os.Build;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManualClassHandler
{
    final static String WBCURRENT = "wb-current-cct";
    final static String WB_CCT = "wb-cct";
    final static String WB_CT = "wb-ct";
    final static String WB_MANUAL = "wb-manual-cct";
    final static String MANUAL_WB_VALUE = "manual-wb-value";
    final static String MAX_WB_CCT = "max-wb-cct";
    final static String MIN_WB_CCT = "min-wb-cct";
    final static String MAX_WB_CT = "max-wb-ct";
    final static String MIN_WB_CT = "min-wb-ct";
    final static String LG_Min = "lg-wb-supported-min";
    final static String LG_Max = "lg-wb-supported-max";
    final static String LG_WB = "lg-wb";
    final static String WB_MODE_MANUAL = "manual";
    final static String WB_MODE_MANUAL_CCT = "manual-cct";

    public static BaseManualParameter GetCCT_Class(HashMap<String, String> parameters, CamParametersHandler parametersHandler, I_CameraHolder cameraHolder)
    {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) && !DeviceUtils.isCyanogenMod())
        {
            if (Build.VERSION.SDK_INT < 23)
            {
                return new BaseCCTManual(parameters,WB_MANUAL,7500,2000,parametersHandler,100, WB_MODE_MANUAL);
            }
            else
                return new BaseCCTManual(parameters,WB_MANUAL,8000,2000,parametersHandler,100, WB_MODE_MANUAL_CCT);
        }
        else if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
            return new BaseCCTManual(parameters,WB_MANUAL,8000,2000,parametersHandler,100, WB_MODE_MANUAL_CCT);
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.QC_Manual_New))
            return new CCTManual_SonyM4(parameters, 8000,2000,parametersHandler,100, WB_MODE_MANUAL_CCT);
        else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
            return new CCTManualG4(parameters, LG_Max, LG_Min,parametersHandler,100,"");
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            return new CCTManualHtc(parameters, MAX_WB_CT, MIN_WB_CT, parametersHandler,100,"");
        else
        {
            String wbModeval ="", wbcur ="", wbmax = "",wbmin = "";
            if (parameters.containsKey(WBCURRENT))
                wbcur = WBCURRENT;
            else if (parameters.containsKey(WB_CCT))
                wbcur = WB_CCT;
            else if (parameters.containsKey(WB_CT))
                wbcur = WB_CT;
            else if (parameters.containsKey(WB_MANUAL))
                wbcur = WB_MANUAL;
            else if (parameters.containsKey(MANUAL_WB_VALUE))
                wbcur = MANUAL_WB_VALUE;

            if (parameters.containsKey(MAX_WB_CCT)) {
                wbmax = MAX_WB_CCT;
            }
            else if (parameters.containsKey(MAX_WB_CT))
                wbmax =MAX_WB_CT;

            if (parameters.containsKey(MIN_WB_CCT)) {
                wbmin =MIN_WB_CCT;
            } else if (parameters.containsKey(MIN_WB_CT))
                wbmin =MIN_WB_CT;

            if (arrayContainsString(parametersHandler.WhiteBalanceMode.GetValues(), WB_MODE_MANUAL))
                wbModeval = WB_MODE_MANUAL;
            else if (arrayContainsString(parametersHandler.WhiteBalanceMode.GetValues(), WB_MODE_MANUAL_CCT))
                wbModeval = WB_MODE_MANUAL_CCT;

            if (!wbcur.equals("") && !wbmax.equals("") && !wbmin.equals("") && wbModeval.equals(""))
                return new BaseCCTManual(parameters,wbcur,wbmax,wbmin,parametersHandler,100,wbModeval);
            else
                return null;
        }


    }

    private static boolean arrayContainsString(String[] ar,String dif)
    {
        boolean ret = false;
        for (String s: ar)
            if (s.equals(dif))
                ret = true;
        return ret;
    }
}
