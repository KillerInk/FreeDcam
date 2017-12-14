package com.lge.hardware;

import android.hardware.Camera;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by KillerInk on 08.12.2017.
 */

public class LGCameraRef
{


    private Object lgCamera;
    private Class CLASS_LGCAMERA;
    private Class CLASS_LGPARAMETERS;
    private Method METHOD_getCamera;
    private Method METHOD_getLGParameters;
    private Method METHOD_LGParameters_getParameters;

    /*

    package com.lge.hardware;

    private static final int CAMERA_META_DATA_FLASH_INDICATOR = 8;
    private static final int CAMERA_META_DATA_HDR_INDICATOR = 4;
    private static final int CAMERA_META_DATA_LG_MANUAL_MODE_INDICATOR = 18;
    private static final int CAMERA_MSG_META_DATA = 8192;
    private static final int CAMERA_MSG_OBT_DATA = 20480;
    private static final int CAMERA_MSG_PROXY_DATA = 32768;
    private static final int CAMERA_MSG_STATS_DATA = 4096;
    private static final java.lang.String TAG = "LGCamera";
    private static java.lang.Object sSplitAreaMethod;
    private android.hardware.Camera mCamera;
    private com.lge.hardware.LGCamera.CameraDataCallback mCameraDataCallback;
    private int mCameraId;
    private int mEnabledMetaData;
    private com.lge.hardware.LGCamera.EventHandler mEventHandler;
    private com.lge.hardware.LGCamera.CameraMetaDataCallback mFlashMetaDataCallback;
    private com.lge.hardware.LGCamera.CameraMetaDataCallback mHdrMetaDataCallback;
    private com.lge.hardware.LGCamera.CameraMetaDataCallback mLGManualModeMetaDataCallback;
    private java.lang.Object mMetaDataCallbackLock;
    private com.lge.hardware.LGCamera.ProxyDataListener mProxyDataListener;
    private boolean mProxyDataRunning;

    public LGCamera(int i) {  } */

    public LGCameraRef(int cameraid)
    {
        try {
            init_classes();
            Constructor<?>[] ctors = CLASS_LGCAMERA.getDeclaredConstructors();
            Constructor<?> constructor = (Constructor<?>) ctors[0];
            lgCamera = constructor.newInstance(cameraid);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /*public LGCamera(int i, int i1) {  } */

    public LGCameraRef(int cameraid, int hwlvl)
    {
        try {
            init_classes();
            Constructor<?>[] ctors = CLASS_LGCAMERA.getDeclaredConstructors();
            Constructor<?> constructor = (Constructor<?>) ctors[1];
            lgCamera = constructor.newInstance(cameraid, hwlvl);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void init_classes() throws ClassNotFoundException, NoSuchMethodException {
        CLASS_LGCAMERA =  Class.forName("com.lge.hardware.LGCamera");
        CLASS_LGPARAMETERS = Class.forName("com.lge.hardware.LGCamera$LGParameters");
        METHOD_getCamera = CLASS_LGCAMERA.getMethod("getCamera");
        METHOD_getLGParameters = CLASS_LGCAMERA.getMethod("getLGParameters");
        METHOD_LGParameters_getParameters = CLASS_LGPARAMETERS.getMethod("getParameters");
    }

    //public android.hardware.Camera getCamera() {  }

    public Camera getCamera()
    {
        try {
            return (Camera) METHOD_getCamera.invoke(lgCamera);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    //public com.lge.hardware.LGCamera.LGParameters getLGParameters() {  }

    public Camera.Parameters getParameters()
    {
        try {
            Object lgparams = METHOD_getLGParameters.invoke(lgCamera);
            return (Camera.Parameters) METHOD_LGParameters_getParameters.invoke(lgparams);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*private final native void _enableProxyDataListen(android.hardware.Camera camera, boolean b);

    private static int byteToInt(byte[] bytes, int i) {  }

    private void cameraInit(int i, android.hardware.Camera camera) {  }

    private final native void native_cancelPicture(android.hardware.Camera camera);

    private final native void native_change_listener(java.lang.Object o, android.hardware.Camera camera);

    private final native void native_sendObjectTrackingCmd(android.hardware.Camera camera);

    private final native void native_setISPDataCallbackMode(android.hardware.Camera camera, boolean b);

    private final native void native_setMetadataCb(android.hardware.Camera camera, boolean b);

    private final native void native_setOBTDataCallbackMode(android.hardware.Camera camera, boolean b);

    private static void postEventFromNative(java.lang.Object o, int i, int i1, int i2, java.lang.Object o1) {  }

    public final void cancelPicture() {  }

    protected void finalize() {  }

    public android.hardware.Camera getCamera() {  }

    public com.lge.hardware.LGCamera.LGParameters getLGParameters() {  }

    public final void runObjectTracking() {  }

    public final void setFlashdataCb(com.lge.hardware.LGCamera.CameraMetaDataCallback cameraMetaDataCallback) {  }

    public final void setISPDataCallbackMode(com.lge.hardware.LGCamera.CameraDataCallback cameraDataCallback) {  }

    public final void setLGManualModedataCb(com.lge.hardware.LGCamera.CameraMetaDataCallback cameraMetaDataCallback) {  }

    public final void setMetadataCb(com.lge.hardware.LGCamera.CameraMetaDataCallback cameraMetaDataCallback) {  }

    public final void setOBTDataCallbackMode(com.lge.hardware.LGCamera.CameraDataCallback cameraDataCallback) {  }

    public final void setProxyDataListener(com.lge.hardware.LGCamera.ProxyDataListener proxyDataListener) {  }

public static interface CameraDataCallback {
    void onCameraData(int[] ints, android.hardware.Camera camera);
}

public static interface CameraMetaDataCallback {
    void onCameraMetaData(byte[] bytes, android.hardware.Camera camera);
}

private class EventHandler extends android.os.Handler {
    private com.lge.hardware.LGCamera mLGCamera;

    public EventHandler(com.lge.hardware.LGCamera lgCamera, android.os.Looper looper) {  }

    public void handleMessage(android.os.Message message) {  }
}

public class LGParameters {
    private static final java.lang.String KEY_BACKLIGHT_CONDITION = "backlight-condition";
    private static final java.lang.String KEY_BEAUTY = "beautyshot";
    private static final java.lang.String KEY_FLASH_MODE = "flash-mode";
    private static final java.lang.String KEY_FLASH_STATUS = "flash-status";
    private static final java.lang.String KEY_FOCUS_MODE_OBJECT_TRACKING = "object-tracking";
    private static final java.lang.String KEY_HDR_MODE = "hdr-mode";
    private static final java.lang.String KEY_LG_MULTI_WINDOW_FOCUS_AREA = "multi-window-focus-area";
    private static final java.lang.String KEY_LUMINANCE_CONDITION = "luminance-condition";
    private static final java.lang.String KEY_PANORAMA = "panorama-shot";
    private static final java.lang.String KEY_QC_SCENE_DETECT = "scene-detect";
    private static final java.lang.String KEY_SUPERZOOM = "superzoom";
    private static final java.lang.String KEY_ZOOM = "zoom";
    public static final java.lang.String SCENE_MODE_AUTO = "auto";
    public static final java.lang.String SCENE_MODE_NIGHT = "night";
    java.lang.String backlightCondition;
    java.lang.String luminanceCondition;
    java.lang.String mCurrentFlash;
    java.lang.String mFlashStatus;
    java.lang.String mHDRstatus;
    java.lang.String mIsBeauty;
    boolean mIsCurrentFlash;
    boolean mIsFlashAuto;
    boolean mIsFlashOff;
    boolean mIsFlashOn;
    boolean mIsHDRAuto;
    boolean mIsHDROff;
    boolean mIsHDROn;
    boolean mIsHighBackLight;
    boolean mIsLuminanceEis;
    boolean mIsLuminanceHigh;
    boolean mIsSuperZoomEnabled;
    private android.hardware.Camera.Parameters mParameters;
    int mSuperZoomStatus;
    java.lang.String mshotMode;

    public LGParameters() {  }

    private void checkBacklightStatus() {  }

    private void checkFlashStatus() {  }

    private void checkHDRStatus() {  }

    private void checkLuminanceStatus() {  }

    private void checkSceneStatus() {  }

    private void checkSuperZoomStatus() {  }

    private void setDefaultParam() {  }

    private void setHDROnParam() {  }

    private void setLGParameters() {  }

    public java.util.List<android.hardware.Camera.Area> getMultiWindowFocusAreas() throws java.lang.UnsupportedOperationException {  }

    public boolean getParamStatus(java.lang.String s, java.lang.String s1) { }

    public android.hardware.Camera.Parameters getParameters() { }

    public android.hardware.Camera.Parameters setNightandHDRorAuto(android.hardware.Camera.Parameters parameters, java.lang.String s, boolean b) {  }

    public void setObjectTracking(java.lang.String s) {  }

    public void setParameters(android.hardware.Camera.Parameters parameters) {  }

    public void setSceneDetectMode(java.lang.String s) {  }

    public android.hardware.Camera.Parameters setSuperZoom(android.hardware.Camera.Parameters parameters) {  }
}

public static class ProxyData {
    public int amb;
    public int conv;
    public int raw;
    public int sig;
    public int val;

    public ProxyData() {  }
}

public static interface ProxyDataListener {
    void onDataListen(com.lge.hardware.LGCamera.ProxyData proxyData, android.hardware.Camera camera);
}
     */
}
