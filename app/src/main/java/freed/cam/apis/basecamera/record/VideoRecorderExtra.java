package freed.cam.apis.basecamera.record;

import android.media.MediaRecorder;

import java.lang.reflect.Method;

/*package com.miui.internal.os;

import com.miui.internal.JniNames;
import miui.reflect.Constructor;
import miui.reflect.Field;
import miui.reflect.Method;
import miui.reflect.ReflectUtils;*/


public class VideoRecorderExtra {

    private static final float defaultInternval = 0.033f;

/*

    //public static final String LIB_NATIVE = "miuinative";
    static {
        System.loadLibrary(JniNames.LIB_NATIVE);
    }


    public  void setIframeInterval(MediaRecorder recorder, float interval)
    {
        setParameterExtra(recorder,"video-param-i-frames-interval="+interval);
    }

    private void setParameterExtra(MediaRecorder mediaRecorder, String str) {
        Class<?>[] clsArr = {MediaRecorder.class};
        Method method = getMethod(clsArr, "setParameter", "(Ljava/lang/String;)V");
        if (method != null) {
            method.invoke(clsArr[0], mediaRecorder, str);
        }
    }

    public static Method getMethod(Class<?>[] clsArr, String str, String str2) {
        Method method = null;
        if (clsArr != null) {
            try {
                if (clsArr.length == 1) {
                    method = Method.of(clsArr[0], str, str2);
                }
            } catch (NoSuchMethodException unused) {
                if (clsArr[0].getSuperclass() != null) {
                    clsArr[0] = clsArr[0].getSuperclass();
                    method = getMethod(clsArr, str, str2);
                }
            }
        }
        if (method == null) {
            Log.e(TAG, "getMethod fail, " + str + "[" + str2 + "]");
        }
        return method;
    }

    public static Method of(Class<?> cls, String str, String str2) throws NoSuchMethodException {
        return Native.getMethod(cls, str, str2);
    }

    public static native Method getMethod(Class<?> cls, String str, String str2);

*/


}
