package com.sonyericsson.cameraextension;

import com.troop.freedcam.BuildConfig;

import java.lang.ref.WeakReference;

/**
 * Created by troop on 20.03.2017.
 */

public class CameraExtension
{

    public CameraExtension()
    {

    }

    private static void postEventFromNative(Object cameraref, int what, int arg1, int arg2, Object obj) {
    }

    private final native void disableBurstShotCallback();

    private final native void enableBurstShotCallback();

    private final native boolean isBurstSavingTaskStackEmpty();

    private final native void native_deselectObject();

    private final native String native_getParameters();

    private final native void native_release();

    private final native void native_selectObject(int i, int i2);

    private final native void native_setBurstShutterSoundFilePath(String str);

    private final native void native_setSelectFacePos(int i, int i2);

    private final native int native_setup(Object obj, int i, String str);

    private final native void native_startAutoFocus(boolean z, boolean z2, boolean z3);

    private final native void native_startObjectTracking();

    private final native void native_startSceneRecognition();

    private final native void native_startVideoMetadata();

    private final native void native_stopBurstShot();

    private final native void native_stopObjectTracking();

    private final native void native_stopSceneRecognition();

    private final native void native_stopVideoMetadata();

    private final native void setObjectTrackingLowPassFilterPrameters(int i, int i2);

    private final native void startBurstShot(int i);

    public final native void native_stopAutoFocus();

    static {
        System.loadLibrary("cameraextensionjni");
    }

    public int loadSonyExtension(int cameraId)
    {
        return native_setup(new WeakReference(this), cameraId, BuildConfig.APPLICATION_ID);
    }

    public String getNativeParameters()
    {
        return native_getParameters();
    }

    public void close()
    {
        native_release();
    }
}
