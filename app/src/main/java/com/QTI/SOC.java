package com.QTI;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.huawei.camera2ex.ReflectionHelper;

import java.lang.reflect.Type;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SOC {

    public static final CaptureRequest.Key EIS_MODE;


    static {
        EIS_MODE = getKeyType("org.quic.camera.eis3enable.EISV3Enable", byte.class);
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
