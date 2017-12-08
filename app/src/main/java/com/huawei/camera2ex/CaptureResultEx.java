package com.huawei.camera2ex;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureResult;
import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Created by troop on 29.03.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CaptureResultEx {
    
    public static final CaptureResult.Key<Integer> HUAWEI_DUAL_SENSOR_ABORMAL;
    
    public static final CaptureResult.Key<Byte> HUAWEI_EXPOSURE_MODE_PREVIEW_STATE;
    
    public static final CaptureResult.Key<Integer> HUAWEI_EXPOSURE_STATE_HINT;
    
    public static final CaptureResult.Key<int[]> HUAWEI_FACE_INFOS;
    
    public static final CaptureResult.Key<Integer> HUAWEI_FOCUS_VCM_VALUE;
    
    public static final CaptureResult.Key<Integer> HUAWEI_FRAME_LUMINANCE;
    
    public static final CaptureResult.Key<Integer> HUAWEI_FRAME_LUMINATION;
    
    public static final CaptureResult.Key<Integer> HUAWEI_HINT_USER;
    
    public static final CaptureResult.Key<Integer> HUAWEI_HINT_USER_VALUE;
    
    public static final CaptureResult.Key<Integer> HUAWEI_ISO_STATE;
    
    public static final CaptureResult.Key<int[]> HUAWEI_LASER_DATA;
    
    public static final CaptureResult.Key<Integer> HUAWEI_LIGHT_PAINTING_EXPOSURE_TIME;
    
    public static final CaptureResult.Key<Byte> HUAWEI_NEED_LCD_COMPENSATE;
    
    public static final CaptureResult.Key<Integer> HUAWEI_THERMAL_DUAL_TO_SINGLE;
    
    public static final CaptureResult.Key<int[]> HUAWEI_VIDEO_BOKEH_AF_REGION;

    static
    {
        HUAWEI_FOCUS_VCM_VALUE = getKeyType("com.huawei.capture.metadata.focusVcmValue", Integer.TYPE);
        HUAWEI_HINT_USER = getKeyType("com.huawei.capture.metadata.hintUser", Integer.TYPE);
        HUAWEI_DUAL_SENSOR_ABORMAL = getKeyType("com.huawei.capture.metadata.dualSensorAbnormal", Integer.TYPE);
        HUAWEI_LIGHT_PAINTING_EXPOSURE_TIME = getKeyType("com.huawei.capture.metadata.lightPaintingExposureTime", Integer.TYPE);
        HUAWEI_ISO_STATE = getKeyType("com.huawei.capture.metadata.isoState", Integer.TYPE);
        HUAWEI_EXPOSURE_STATE_HINT = getKeyType("com.huawei.capture.metadata.exposureStateHint", Integer.TYPE);
        HUAWEI_FRAME_LUMINANCE = getKeyType("com.huawei.capture.metadata.frameLuminance", Integer.TYPE);
        HUAWEI_FACE_INFOS = getKeyClass("com.huawei.capture.metadata.hwFaceInfos", int[].class);
        HUAWEI_HINT_USER_VALUE = getKeyType("com.huawei.capture.metadata.hintUserValue", Integer.TYPE);
        HUAWEI_FRAME_LUMINATION = getKeyType("com.huawei.capture.metadata.hw_algo_mean_y", Integer.TYPE);
        HUAWEI_EXPOSURE_MODE_PREVIEW_STATE = getKeyType("com.huawei.capture.metadata.hw-exposure-mode-preview-state", Byte.TYPE);
        HUAWEI_THERMAL_DUAL_TO_SINGLE = getKeyType("com.huawei.capture.metadata.hwThermalDual2single", Integer.TYPE);
        HUAWEI_VIDEO_BOKEH_AF_REGION = getKeyClass("com.huawei.capture.metadata.hwVideoBokehAfRegion", int[].class);
        HUAWEI_LASER_DATA = getKeyClass("com.huawei.capture.metadata.cameraLaserData", int[].class);
        HUAWEI_NEED_LCD_COMPENSATE = getKeyType("com.huawei.capture.metadata.needLcdCompensate", Byte.TYPE);
    }


    private static CaptureResult.Key getKeyType(String string, Type type)
    {
        return (CaptureResult.Key) ReflectionHelper.getKeyType(string,type,CaptureResult.Key.class);
    }

    //public android.hardware.camera2.CameraCharacteristics$Key(java.lang.String,java.lang.Class)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static <T> CaptureResult.Key getKeyClass(String string, Class<T> type)
    {
        return (CaptureResult.Key) ReflectionHelper.getKeyClass(string,type,CaptureResult.Key.class);
    }
}
