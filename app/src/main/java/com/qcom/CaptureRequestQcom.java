package com.qcom;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.huawei.camera2ex.ReflectionHelper;

import java.lang.reflect.Type;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureRequestQcom {
    public static final CaptureRequest.Key<Byte> eis_mode;
    public static final CaptureRequest.Key<Byte> eis_realtime_mode;
    public static final CaptureRequest.Key<Integer> sharpness;
    public static final CaptureRequest.Key<Integer> saturation;


    static {
        eis_mode = getKeyType("org.quic.camera.eis3enable.EISV3Enable", Byte.TYPE);
        eis_realtime_mode = getKeyType("org.quic.camera.eisrealtime.Enabled", Byte.TYPE);
        sharpness = getKeyType("org.codeaurora.qcamera3.sharpness.strength", Integer.TYPE);
        saturation = getKeyType("org.codeaurora.qcamera3.saturation.use_saturation", Integer.TYPE);
    }


    private static CaptureRequest.Key getKeyType(String string, Type type)
    {
        return (CaptureRequest.Key) ReflectionHelper.getKeyType(string,type, CaptureRequest.Key.class);
    }


    private static <T> CaptureRequest.Key getKeyClass(String string, Class<T> type)
    {
        return (CaptureRequest.Key) ReflectionHelper.getKeyClass(string,type, CaptureRequest.Key.class);
    }
}
