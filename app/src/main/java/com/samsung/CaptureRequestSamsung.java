package com.samsung;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.huawei.camera2ex.ReflectionHelper;

import java.lang.reflect.Type;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureRequestSamsung {

    public static final int CONTROL_TRANSIENT_ACTION_OFF = 0;
    public static final int CONTROL_TRANSIENT_ACTION_ZOOMING = 1;
    public static final int CONTROL_TRANSIENT_ACTION_ZOOMING_JUMP_TELE = 5;
    public static final int CONTROL_TRANSIENT_ACTION_ZOOMING_JUMP_UW = 3;
    public static final int CONTROL_TRANSIENT_ACTION_ZOOMING_JUMP_WIDE = 4;
    public static final CaptureRequest.Key<Integer> CONTROL_TRANSIENT_ACTION;

    static {
        CONTROL_TRANSIENT_ACTION = getKeyType("samsung.android.control.transientAction", Integer.TYPE);
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
