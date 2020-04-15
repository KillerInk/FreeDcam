package com.QTI;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.huawei.camera2ex.ReflectionHelper;

import java.lang.reflect.Type;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SOC {

    public static final CaptureRequest.Key EIS_MODE;
    public static final CaptureRequest.Key MFNR;
    public static final CaptureRequest.Key AWB_CCT;
    public static final CaptureRequest.Key AWB_Range;

    public static final CaptureRequest.Key METERING_MODE;




    static {

        //QC EIS
        EIS_MODE = getKeyType("org.quic.camera.eis3enable.EISV3Enable", byte.class);
        //Multiframe Temporal FIltering Set Noise Reduction to High Qiuality to Enable
        MFNR = getKeyType("org.quic.camera.CustomNoiseReduction.CustomNoiseReduction", byte.class);
        //CCT TEMP
        AWB_CCT = getKeyType("org.codeaurora.qcamera3.manualWB.color_temperature", Integer.class);
        AWB_Range = getKeyType("org.codeaurora.qcamera3.manualWB.color_temperature_range", Integer.class);

        METERING_MODE = getKeyType("org.codeaurora.qcamera3.exposure_metering.exposure_metering_mode", Integer.class);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static CaptureRequest.Key getKeyType(String string, Type type)
    {
        return (CaptureRequest.Key) ReflectionHelper.getKeyType(string,type, CaptureRequest.Key.class);
    }

    private static <T> CaptureRequest.Key getKeyClass(String string, Class<T> type)
    {
        return (CaptureRequest.Key) ReflectionHelper.getKeyClass(string,type, CaptureRequest.Key.class);
    }
}
