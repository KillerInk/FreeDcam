package com.qcom;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.huawei.camera2ex.ReflectionHelper;

import java.lang.reflect.Type;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsQcom
{
    public static final CameraCharacteristics.Key<int[]> sharpness_range;
    public static final CameraCharacteristics.Key<int[]> saturation_range;

    static {
        sharpness_range = getKeyType("org.codeaurora.qcamera3.sharpness.range", int[].class);
        saturation_range = getKeyType("org.codeaurora.qcamera3.saturation.range", int[].class);
    }

    private static CameraCharacteristics.Key getKeyType(String string, Type type)
    {
        return (CameraCharacteristics.Key) ReflectionHelper.getKeyType(string,type,CameraCharacteristics.Key.class);
    }


    private static <T> CameraCharacteristics.Key getKeyClass(String string, Class<T> type)
    {

        return (CameraCharacteristics.Key) ReflectionHelper.getKeyClass(string,type,CameraCharacteristics.Key.class);
    }
}
