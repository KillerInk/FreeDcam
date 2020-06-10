package com.samsung;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.huawei.camera2ex.ReflectionHelper;

import java.lang.reflect.Type;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsSamsung {
    public static final CameraCharacteristics.Key<String> LOGICAL_MULTI_CAMERA_MASTER_PHYSICAL_ID;
    public static final CameraCharacteristics.Key<byte[]> LOGICAL_MULTI_CAMERA_DUAL_CALIBRATION;


    private static CameraCharacteristics.Key getKeyType(String string, Type type)
    {
        return (CameraCharacteristics.Key) ReflectionHelper.getKeyType(string,type,CameraCharacteristics.Key.class);
    }

    static {
        LOGICAL_MULTI_CAMERA_MASTER_PHYSICAL_ID = getKeyType("samsung.android.logicalMultiCamera.masterPhysicalId",String.class);
        LOGICAL_MULTI_CAMERA_DUAL_CALIBRATION = getKeyType("samsung.android.logicalMultiCamera.dualCalibration",byte[].class);
    }


    private static <T> CameraCharacteristics.Key getKeyClass(String string, Class<T> type)
    {

        return (CameraCharacteristics.Key) ReflectionHelper.getKeyClass(string,type,CameraCharacteristics.Key.class);
    }
}
