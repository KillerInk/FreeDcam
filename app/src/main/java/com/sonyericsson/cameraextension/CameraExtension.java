package com.sonyericsson.cameraextension;

import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.sonyericsson.cameracommon.device.CameraExtensionValues;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraExtension {
    private static final int BURST_FRAME_DROPPED_CALLBACK = 6291456;
    private static final int BURST_GROUP_STORE_COMPLETED_CALLBACK = 8388608;
    private static final int BURST_SHUTTER_DONE_CALLBACK = 5242880;
    private static final int BURST_STORE_COMPLETED_CALLBACK = 7340032;
    private static final int BURST_TASK_WAIT_INTERVAL = 100;
    private static final int CAMERAEXTENSION_ERROR_CALLBACK = 1;
    public static final int CAMERA_EX_ERROR_FILE_ACCESS = 1;
    public static final int CAMERA_EX_ERROR_NO_ERROR = 0;
    public static final int CAMERA_EX_ERROR_STORAGE_FULL = 2;
    private static final boolean DEBUGLOG = false;
    private static final int EACCESS = -13;
    private static final int EBUSY = -16;
    private static final int EINVAL = -22;
    private static final int ENODEV = -19;
    private static final int ENOSYS = -38;
    private static final int EOPNOTSUPP = -95;
    private static final int EUSERS = -87;
    private static final int FACEDETECT_CALLBACK = 1048576;
    private static final Pattern FOCUS_RECTS_PATTERN;
    private static final String KEY_EX_MULTI_FOCUS_RECTS = "sony-multi-focus-rects";
    private static final int NO_ERROR = 0;
    private static final int OBJECTTRACKING_CALLBACK = 4194304;
    private static final int SCENERECOGNITION_CALLBACK = 2097152;
    private static final int START_AUTOFOCUS_CALLBACK = 3145728;
    private static final String TAG = "CameraExtension";
    private static Map mBurstFilePathGeneratorMap;
    private static int mBurstId;
    private static Map mBurstMediaProviderUpdatorMap;
    private static BurstShotCallback mBurstShotCallback;
    private static StorageFullDetector mBurstStorageFullDetector;
    private static ExecutorService mBurstStoreAndMediaProviderUpdatorExecutor;
    private static boolean mIsBurstApplicationFinishing;
    private AutoFocusCallback mAutoFocusCallback;
    private final Object mAutoFocusCallbackLock;
    private CameraExtensionErrorCallback mErrorCallback;
    private EventHandler mEventHandler;
    private FaceDetectionCallback mFaceDetectionCallback;
    private boolean mIsReleased;
    private long mNativeContext;
    private ObjectTrackingCallback mObjectTrackingCallback;
    private SceneRecognitionCallback mSceneRecognitionCallback;

    public interface MediaProviderUpdator {
        void insert(String str);
    }

    public interface FaceDetectionCallback {
        void onFaceDetection(FaceDetectionResult faceDetectionResult);
    }

    public interface ObjectTrackingCallback {
        void onObjectTracked(ObjectTrackingResult objectTrackingResult);
    }

    public interface SceneRecognitionCallback {
        void onSceneModeChanged(SceneRecognitionResult sceneRecognitionResult);
    }

    public interface AutoFocusCallback {
        void onAutoFocus(AutoFocusResult autoFocusResult);
    }

    public interface StorageFullDetector {
        boolean isCurrentStorageFull();
    }

    public interface BurstShotCallback {
        void onBurstFrameDropped(BurstShotResult burstShotResult);

        void onBurstGroupStoreCompleted(BurstShotResult burstShotResult);

        void onBurstPictureStoreCompleted(BurstShotResult burstShotResult);

        void onBurstShutterDone(BurstShotResult burstShotResult);
    }

    /* renamed from: com.sonyericsson.cameraextension.CameraExtension.1 */
    static class C10971 implements Comparator<Size> {
        C10971() {
        }

        public int compare(Size object1, Size object2) {
            return (object1.width * object1.height) - (object2.width * object2.height);
        }
    }

    public static class AutoFocusResult {
        private static final int MOST_RIGHT_BIT_MASK = 1;
        private boolean mFocused;
        private int mMultiFocusState;

        public boolean isFocused() {
            return this.mFocused;
        }

        public boolean isFocused(int position) {
            if (((this.mMultiFocusState >> position) & MOST_RIGHT_BIT_MASK) == MOST_RIGHT_BIT_MASK) {
                return true;
            }
            return CameraExtension.DEBUGLOG;
        }
    }

    public static class BurstShotResult {
        public int mBurstId;
        public int mErrorCode;
        public int mPictureCount;
        public String mStoredFilePath;
    }

    public interface CameraExtensionErrorCallback {
        void onError(int i, CameraExtension cameraExtension);
    }

    public enum DeviceStabilityCondition {
        AUTO,
        MOTION,
        STABLE,
        WALK
    }

    private class EventHandler extends Handler {
        private final CameraExtension mCameraExtension;

        public EventHandler(CameraExtension c, Looper looper) {
            super(looper);
            this.mCameraExtension = c;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CameraExtension.CAMERA_EX_ERROR_FILE_ACCESS /*1*/:
                    if (CameraExtension.this.mErrorCallback != null) {
                        CameraExtension.this.mErrorCallback.onError(msg.arg1, this.mCameraExtension);
                    }
                case CameraExtension.FACEDETECT_CALLBACK /*1048576*/:
                    if (CameraExtension.this.mFaceDetectionCallback != null) {
                        CameraExtension.this.mFaceDetectionCallback.onFaceDetection((FaceDetectionResult) msg.obj);
                    }
                case CameraExtension.SCENERECOGNITION_CALLBACK /*2097152*/:
                    if (CameraExtension.this.mSceneRecognitionCallback != null) {
                        CameraExtension.this.mSceneRecognitionCallback.onSceneModeChanged((SceneRecognitionResult) msg.obj);
                    }
                case CameraExtension.START_AUTOFOCUS_CALLBACK /*3145728*/:
                    AutoFocusCallback cb;
                    synchronized (CameraExtension.this.mAutoFocusCallbackLock) {
                        cb = CameraExtension.this.mAutoFocusCallback;
                        break;
                    }
                case CameraExtension.OBJECTTRACKING_CALLBACK /*4194304*/:
                    if (CameraExtension.this.mObjectTrackingCallback != null) {
                        CameraExtension.this.mObjectTrackingCallback.onObjectTracked((ObjectTrackingResult) msg.obj);
                    }
                case CameraExtension.BURST_SHUTTER_DONE_CALLBACK /*5242880*/:
                    if (CameraExtension.mBurstShotCallback != null) {
                        CameraExtension.mBurstShotCallback.onBurstShutterDone((BurstShotResult) msg.obj);
                    }
                case CameraExtension.BURST_FRAME_DROPPED_CALLBACK /*6291456*/:
                    if (CameraExtension.mBurstShotCallback != null) {
                        CameraExtension.mBurstShotCallback.onBurstFrameDropped((BurstShotResult) msg.obj);
                    }
                case CameraExtension.BURST_STORE_COMPLETED_CALLBACK /*7340032*/:
                    if (CameraExtension.mBurstShotCallback != null) {
                        CameraExtension.mBurstShotCallback.onBurstPictureStoreCompleted((BurstShotResult) msg.obj);
                    }
                case CameraExtension.BURST_GROUP_STORE_COMPLETED_CALLBACK /*8388608*/:
                    if (CameraExtension.mBurstShotCallback != null) {
                        CameraExtension.mBurstShotCallback.onBurstGroupStoreCompleted((BurstShotResult) msg.obj);
                    }
                default:
                    Log.e(CameraExtension.TAG, "Unknown message type " + msg.what);
            }
        }
    }

    public static class ExtFace {
        public Face face;
        public int smileScore;
    }

    public static class FaceDetectionResult {
        public List<ExtFace> extFaceList;
        @Deprecated
        public FaceData[] faceData;
        @Deprecated
        public int faceNum;
        public int indexOfSelectedFace;
        private StringBuffer mToStringBuffer;

        @Deprecated
        public static class FaceData {
            public android.graphics.Rect position;
            public int smileScore;
            public int trackId;
        }

        public FaceDetectionResult() {
            this.mToStringBuffer = new StringBuffer();
            this.extFaceList = new ArrayList();
        }

        void setFrameResult(int indexOfSelectedFaceIn) {
            this.indexOfSelectedFace = indexOfSelectedFaceIn;
        }

        void addFaceResult(int id, int rectLeft, int rectTop, int rectRight, int rectBottom, int leftEyeX, int leftEyeY, int rightEyeX, int rightEyeY, int mouthX, int mouthY, int score, int smileScore) {
            Face face = new Face();
            face.id = id;
            face.rect = new android.graphics.Rect(rectLeft, rectTop, rectRight, rectBottom);
            face.leftEye = new Point(leftEyeX, leftEyeY);
            face.rightEye = new Point(rightEyeX, rightEyeY);
            face.mouth = new Point(mouthX, mouthY);
            face.score = score;
            ExtFace extFace = new ExtFace();
            extFace.smileScore = smileScore;
            extFace.face = face;
            this.extFaceList.add(extFace);
        }

        public String toString() {
            int i$;
            this.mToStringBuffer.setLength(CameraExtension.NO_ERROR);
            this.mToStringBuffer.append("\n########## OLD");
            this.mToStringBuffer.append("\n[faceNum = ").append(this.faceNum).append("]");
            this.mToStringBuffer.append("\n[indexOfSelectedFace = ").append(this.indexOfSelectedFace).append("]");
            if (this.faceData == null) {
                this.mToStringBuffer.append("\n  [FaceData == null]");
            } else {
                FaceData[] arr$ = this.faceData;
                int len$ = arr$.length;
                for (i$ = 0; i$ < len$; i$ += CameraExtension.CAMERA_EX_ERROR_FILE_ACCESS) {
                    FaceData face = arr$[i$];
                    this.mToStringBuffer.append("\n  [FaceData.trackId = ").append(face.trackId).append("]");
                    this.mToStringBuffer.append("\n  [FaceData.smileScore = ").append(face.smileScore).append("]");
                }
            }
            this.mToStringBuffer.append("\n########## NEW");
            this.mToStringBuffer.append("\n[indexOfSelectedFace = ").append(this.indexOfSelectedFace).append("]");
            for (ExtFace extFace : this.extFaceList) {
                this.mToStringBuffer.append("\n  [ExtFace.smileScore = ").append(extFace.smileScore).append("]");
                this.mToStringBuffer.append("\n  [ExtFace.face.id = ").append(extFace.face.id).append("]");
                this.mToStringBuffer.append("\n  [ExtFace.face.rect = ").append(extFace.face.rect.toString()).append("]");
                this.mToStringBuffer.append("\n  [ExtFace.face.leftEye = ").append(extFace.face.leftEye.toString()).append("]");
                this.mToStringBuffer.append("\n  [ExtFace.face.rightEye = ").append(extFace.face.rightEye.toString()).append("]");
                this.mToStringBuffer.append("\n  [ExtFace.face.mouth = ").append(extFace.face.mouth.toString()).append("]");
                this.mToStringBuffer.append("\n  [ExtFace.face.score = ").append(extFace.face.score).append("]");
            }
            return this.mToStringBuffer.toString();
        }
    }

    public interface FilePathGenerator {
        String getNextFilePathToStorePicture();
    }

    private static class NotifyBurstGroupStoreCompletedTask implements Runnable {
        private Handler mCallbackHandler;
        private BurstShotResult mResult;

        public NotifyBurstGroupStoreCompletedTask(BurstShotResult result, Handler handler) {
            this.mResult = null;
            this.mCallbackHandler = null;
            this.mResult = result;
            this.mCallbackHandler = handler;
        }

        public void run() {
            if (!(this.mResult == null || this.mCallbackHandler == null || CameraExtension.mIsBurstApplicationFinishing)) {
                this.mCallbackHandler.sendMessage(this.mCallbackHandler.obtainMessage(CameraExtension.BURST_GROUP_STORE_COMPLETED_CALLBACK, CameraExtension.NO_ERROR, CameraExtension.NO_ERROR, this.mResult));
            }
            if (this.mResult != null) {
                CameraExtension.clearCachedLogicObjectAccordingTo(this.mResult.mBurstId);
            }
        }
    }

    public static class ObjectTrackingResult {
        public boolean mIsLost;
        public android.graphics.Rect mRectOfTrackedObject;
    }

    @Deprecated
    public static class Rect {
        public int height;
        public int width;
        public int f166x;
        public int f167y;

        void setData(int inputX, int inputY, int inputWidth, int inputHeight) {
            this.f166x = inputX;
            this.f167y = inputY;
            this.width = inputWidth;
            this.height = inputHeight;
        }
    }

    public enum SceneMode {
        AUTO(CameraExtensionValues.EX_LENS_DC_MODE_AUTO),
        ACTION("action"),
        PORTRAIT("portrait"),
        LANDSCAPE(CameraExtensionValues.EX_LENS_DC_MODE_LANDSCAPE),
        NIGHT("night"),
        NIGHT_PORTRAIT("night-portrait"),
        THEATRE("theatre"),
        BEACH("beach"),
        SNOW("snow"),
        SUNSET("sunset"),
        STEADYPHOTO("steadyphoto"),
        FIREWORKS("fireworks"),
        SPORTS("sports"),
        PARTY("party"),
        CANDLELIGHT("candlelight"),
        DOCUMENT(CameraExtensionValues.EX_SCENE_MODE_DOCUMENT),
        BACKLIGHT(CameraExtensionValues.EX_SCENE_MODE_BACKLIGHT),
        BACKLIGHT_PORTRAIT(CameraExtensionValues.EX_SCENE_MODE_BACKLIGHT_PORTRAIT),
        DARK(CameraExtensionValues.EX_SCENE_MODE_DARK),
        BABY(CameraExtensionValues.EX_SCENE_MODE_BABY),
        SPOTLIGHT(CameraExtensionValues.EX_SCENE_MODE_SPOT_LIGHT),
        DISH(CameraExtensionValues.EX_SCENE_MODE_DISH);
        
        private final String mNativeValue;

        private SceneMode(String nativeValue) {
            this.mNativeValue = nativeValue;
        }

        public String toString() {
            return this.mNativeValue;
        }
    }

    public static class SceneRecognitionResult {
        public DeviceStabilityCondition deviceStabilityCondition;
        public boolean isMacroRange;
        public SceneMode sceneMode;
    }

    private static class StopBurstOnErrorTask implements Runnable {
        private CameraExtension mCameraExt;

        public StopBurstOnErrorTask(CameraExtension curCamExt) {
            this.mCameraExt = null;
            this.mCameraExt = curCamExt;
        }

        public void run() {
            this.mCameraExt.stopBurstShot();
        }
    }

    private static class UpdateMediaProviderTask implements Runnable {
        private Handler mCallbackHandler;
        private BurstShotResult mResult;
        private MediaProviderUpdator mUpdator;

        public UpdateMediaProviderTask(MediaProviderUpdator updator, BurstShotResult result, Handler handler) {
            this.mUpdator = null;
            this.mResult = null;
            this.mCallbackHandler = null;
            this.mUpdator = updator;
            this.mResult = result;
            this.mCallbackHandler = handler;
        }

        public void run() {
            if (this.mUpdator != null && this.mResult != null && this.mResult.mStoredFilePath != null && this.mCallbackHandler != null) {
                this.mUpdator.insert(this.mResult.mStoredFilePath);
                if (!CameraExtension.mIsBurstApplicationFinishing) {
                    this.mCallbackHandler.sendMessage(this.mCallbackHandler.obtainMessage(CameraExtension.BURST_STORE_COMPLETED_CALLBACK, CameraExtension.NO_ERROR, CameraExtension.NO_ERROR, this.mResult));
                }
            }
        }
    }

    private native void disableBurstShotCallback();

    private native void enableBurstShotCallback();

    private native boolean isBurstSavingTaskStackEmpty();

    private native void native_deselectObject();

    private native String native_getParameters();

    private native void native_release();

    private native void native_selectObject(int i, int i2);

    private native void native_setBurstShutterSoundFilePath(String str);

    private native void native_setSelectFacePos(int i, int i2);

    private native int native_setup(Object obj, int i, String str);

    private native void native_startAutoFocus(boolean z, boolean z2, boolean z3);

    private native void native_startObjectTracking();

    private native void native_startSceneRecognition();

    private native void native_startVideoMetadata();

    private native void native_stopBurstShot();

    private native void native_stopObjectTracking();

    private native void native_stopSceneRecognition();

    private native void native_stopVideoMetadata();

    private native void setObjectTrackingLowPassFilterPrameters(int i, int i2);

    private native void startBurstShot(int i);

    public final native void native_stopAutoFocus();

    static {
        mBurstFilePathGeneratorMap = new ConcurrentHashMap();
        mBurstMediaProviderUpdatorMap = new ConcurrentHashMap();
        mIsBurstApplicationFinishing = DEBUGLOG;
        System.loadLibrary("cameraextensionjni");
        FOCUS_RECTS_PATTERN = Pattern.compile("\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *, *([0-9]+) *\\)");
    }

    public static CameraExtension open(Camera camera, int cameraId) {
        return new CameraExtension(cameraId);
    }

    private CameraExtension(int cameraId) {
        this.mAutoFocusCallbackLock = new Object();
        int err = cameraInit(cameraId);
        if (checkInitErrors(err)) {
            switch (err) {
                case EOPNOTSUPP /*-95*/:
                    throw new RuntimeException("Camera initialization failed because the hal version is not supported by this device");
                case EUSERS /*-87*/:
                    throw new RuntimeException("Camera initialization failed because the max number of camera devices were already opened");
                case ENOSYS /*-38*/:
                    throw new RuntimeException("Camera initialization failed because some methods are not implemented");
                case EINVAL /*-22*/:
                    throw new RuntimeException("Camera initialization failed because the input arugments are invalid");
                case ENODEV /*-19*/:
                    throw new RuntimeException("Camera initialization failed");
                case EBUSY /*-16*/:
                    throw new RuntimeException("Camera initialization failed because the camera device was already opened");
                case EACCESS /*-13*/:
                    throw new RuntimeException("Fail to connect to camera extension service");
                default:
                    throw new RuntimeException("Unknown camera error");
            }
        }
    }

    private int cameraInit(int cameraId) {
        this.mErrorCallback = null;
        this.mAutoFocusCallback = null;
        this.mSceneRecognitionCallback = null;
        this.mFaceDetectionCallback = null;
        this.mObjectTrackingCallback = null;
        this.mIsReleased = DEBUGLOG;
        mBurstId = NO_ERROR;
        mBurstShotCallback = null;
        mBurstStorageFullDetector = null;
        mBurstFilePathGeneratorMap.clear();
        mBurstMediaProviderUpdatorMap.clear();
        mBurstStoreAndMediaProviderUpdatorExecutor = null;
        mIsBurstApplicationFinishing = DEBUGLOG;
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            looper = Looper.getMainLooper();
            if (looper != null) {
                this.mEventHandler = new EventHandler(this, looper);
            } else {
                this.mEventHandler = null;
            }
        }
        return native_setup(new WeakReference(this), cameraId, "com.sonyericsson.android.camera");
    }

    private static boolean checkInitErrors(int err) {
        return err != 0 ? true : DEBUGLOG;
    }

    protected void finalize() {
        native_release();
    }

    public final void release() {
        if (!this.mIsReleased) {
            this.mIsReleased = true;
            if (mBurstStoreAndMediaProviderUpdatorExecutor != null) {
                mBurstStoreAndMediaProviderUpdatorExecutor.shutdown();
                mBurstStoreAndMediaProviderUpdatorExecutor = null;
            }
            mBurstShotCallback = null;
            mBurstStorageFullDetector = null;
            mBurstFilePathGeneratorMap.clear();
            mBurstMediaProviderUpdatorMap.clear();
            native_release();
        }
    }

    public void fetchParameters(Parameters params) {
        if (!this.mIsReleased) {
            params.unflatten(native_getParameters());
        }
    }

    public final void startAutoFocus(AutoFocusCallback cb, boolean aeLock, boolean awbLock, boolean focusLock) {
        if (!this.mIsReleased) {
            synchronized (this.mAutoFocusCallbackLock) {
                this.mAutoFocusCallback = cb;
            }
            native_startAutoFocus(aeLock, awbLock, focusLock);
        }
    }

    public final void stopAutoFocus() {
        if (!this.mIsReleased) {
            synchronized (this.mAutoFocusCallbackLock) {
                this.mAutoFocusCallback = null;
            }
            native_stopAutoFocus();
            this.mEventHandler.removeMessages(START_AUTOFOCUS_CALLBACK);
        }
    }

    public final void startSceneRecognition(SceneRecognitionCallback cb) {
        if (!this.mIsReleased) {
            this.mSceneRecognitionCallback = cb;
            native_startSceneRecognition();
        }
    }

    public final void stopSceneRecognition() {
        if (!this.mIsReleased) {
            native_stopSceneRecognition();
        }
    }

    public final void setFaceDetectionCallback(FaceDetectionCallback cb) {
        if (!this.mIsReleased) {
            this.mFaceDetectionCallback = cb;
        }
    }

    public final void setSelectFacePos(int x, int y) {
        if (!this.mIsReleased) {
            native_setSelectFacePos(x, y);
        }
    }

    public final void setErrorCallback(CameraExtensionErrorCallback cb) {
        this.mErrorCallback = cb;
    }

    public static List<Rect> getFocusAreasOnPreview(Parameters parameters, Size previewSize) {
        List<Rect> result = new ArrayList();
        Size max = getMaxPictureSize(parameters);
        if (max != null) {
            Matcher matcher = FOCUS_RECTS_PATTERN.matcher(parameters.get(KEY_EX_MULTI_FOCUS_RECTS));
            while (matcher.find()) {
                Rect rect = new Rect();
                rect.setData(Integer.parseInt(matcher.group(CAMERA_EX_ERROR_FILE_ACCESS)), Integer.parseInt(matcher.group(CAMERA_EX_ERROR_STORAGE_FULL)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
                result.add(convertToRectOnPreview(rect, previewSize, max));
            }
        }
        return result;
    }

    private static Size getMaxPictureSize(Parameters parameters) {
        Comparator<Size> sizeComparator = new C10971();
        if (parameters != null) {
            List<Size> supported = parameters.getSupportedPictureSizes();
            if (supported != null) {
                return (Size) Collections.max(supported, sizeComparator);
            }
        }
        return null;
    }

    private static Rect convertToRectOnPreview(Rect rect, Size currentPreviewSize, Size maxPictureSize) {
        float destWidth = (float) currentPreviewSize.width;
        float destHeight = (float) currentPreviewSize.height;
        float srcToDestRatio;
        Rect focusInDest;
        if (((float) currentPreviewSize.width) / ((float) currentPreviewSize.height) > ((float) maxPictureSize.width) / ((float) maxPictureSize.height)) {
            srcToDestRatio = ((float) currentPreviewSize.width) / ((float) maxPictureSize.width);
            float srcHeight = ((float) maxPictureSize.height) * srcToDestRatio;
            focusInDest = new Rect();
            focusInDest.setData((int) (((((float) rect.f166x) / 100.0f) * ((float) maxPictureSize.width)) * srcToDestRatio), (int) ((((((float) rect.f167y) / 100.0f) * ((float) maxPictureSize.height)) * srcToDestRatio) - ((srcHeight - destHeight) / 2.0f)), (int) (((((float) rect.width) / 100.0f) * ((float) maxPictureSize.width)) * srcToDestRatio), (int) (((((float) rect.height) / 100.0f) * ((float) maxPictureSize.height)) * srcToDestRatio));
            return focusInDest;
        }
        srcToDestRatio = ((float) currentPreviewSize.height) / ((float) maxPictureSize.height);
        float srcWidth = ((float) maxPictureSize.width) * srcToDestRatio;
        focusInDest = new Rect();
        focusInDest.setData((int) ((((((float) rect.f166x) / 100.0f) * ((float) maxPictureSize.width)) * srcToDestRatio) - ((srcWidth - destWidth) / 2.0f)), (int) (((((float) rect.f167y) / 100.0f) * ((float) maxPictureSize.height)) * srcToDestRatio), (int) (((((float) rect.width) / 100.0f) * ((float) maxPictureSize.width)) * srcToDestRatio), (int) (((((float) rect.height) / 100.0f) * ((float) maxPictureSize.height)) * srcToDestRatio));
        return focusInDest;
    }

    private static void postEventFromNative(Object cameraref, int what, int arg1, int arg2, Object obj) {
        CameraExtension camera = (CameraExtension) ((WeakReference) cameraref).get();
        if (camera != null) {
            switch (what) {
                case START_AUTOFOCUS_CALLBACK /*3145728*/:
                    synchronized (camera.mAutoFocusCallbackLock) {
                        if (camera.mAutoFocusCallback != null) {
                            break;
                        } else {
                            return;
                        }
                    }
                case BURST_SHUTTER_DONE_CALLBACK /*5242880*/:
                    if (((BurstShotResult) obj).mErrorCode == CAMERA_EX_ERROR_STORAGE_FULL) {
                        camera.mEventHandler.post(new StopBurstOnErrorTask(camera));
                        break;
                    }
                    break;
                case BURST_FRAME_DROPPED_CALLBACK /*6291456*/:
                    if (((BurstShotResult) obj).mErrorCode == CAMERA_EX_ERROR_FILE_ACCESS) {
                        camera.mEventHandler.post(new StopBurstOnErrorTask(camera));
                        break;
                    }
                    break;
                case BURST_STORE_COMPLETED_CALLBACK /*7340032*/:
                    BurstShotResult storeResult = (BurstShotResult) obj;
                    mBurstStoreAndMediaProviderUpdatorExecutor.execute(new UpdateMediaProviderTask((MediaProviderUpdator) mBurstMediaProviderUpdatorMap.get(Integer.valueOf(storeResult.mBurstId)), storeResult, camera.mEventHandler));
                    return;
                case BURST_GROUP_STORE_COMPLETED_CALLBACK /*8388608*/:
                    mBurstStoreAndMediaProviderUpdatorExecutor.execute(new NotifyBurstGroupStoreCompletedTask((BurstShotResult) obj, camera.mEventHandler));
                    return;
            }
            if (camera.mEventHandler != null) {
                camera.mEventHandler.sendMessage(camera.mEventHandler.obtainMessage(what, arg1, arg2, obj));
            }
        }
    }

    public final void startObjectTracking() {
        if (!this.mIsReleased) {
            native_startObjectTracking();
        }
    }

    public final void stopObjectTracking() {
        if (!this.mIsReleased) {
            native_stopObjectTracking();
        }
    }

    public final void selectObject(int x, int y) {
        if (!this.mIsReleased) {
            native_selectObject(x, y);
        }
    }

    public final void deselectObject() {
        if (!this.mIsReleased) {
            native_deselectObject();
        }
    }

    public final void setObjectTrackingCallback(ObjectTrackingCallback cb, int lowPassFilterStrength, int minimumIntervalMilliSec) {
        if (!this.mIsReleased) {
            this.mObjectTrackingCallback = cb;
            if (Integer.MAX_VALUE < minimumIntervalMilliSec) {
                setObjectTrackingLowPassFilterPrameters(lowPassFilterStrength, minimumIntervalMilliSec);
            } else {
                setObjectTrackingLowPassFilterPrameters(lowPassFilterStrength, minimumIntervalMilliSec);
            }
        }
    }

    public final int startBurstShot(FilePathGenerator filePathGenerator, MediaProviderUpdator mediaProviderUpdator) {
        if (this.mIsReleased) {
            return NO_ERROR;
        }
        if (filePathGenerator == null || mediaProviderUpdator == null) {
            throw new NullPointerException("startBurstShot() : [argument is null]");
        }
        setBurstFilePathGenerator(mBurstId, filePathGenerator);
        setBurstMediaProviderUpdator(mBurstId, mediaProviderUpdator);
        startBurstShot(mBurstId);
        int curId = mBurstId;
        mBurstId += CAMERA_EX_ERROR_FILE_ACCESS;
        return curId;
    }

    public final void stopBurstShot() {
        if (!this.mIsReleased) {
            native_stopBurstShot();
        }
    }

    public final void setBurstShotCallback(BurstShotCallback callback) {
        if (!this.mIsReleased) {
            if (callback != null) {
                mBurstShotCallback = callback;
                enableBurstShotCallback();
                return;
            }
            mBurstShotCallback = null;
            disableBurstShotCallback();
        }
    }

    public final void setBurstStorageFullDetector(StorageFullDetector detector) {
        if (!this.mIsReleased) {
            mBurstStorageFullDetector = detector;
        }
    }

    private void setBurstFilePathGenerator(int burstId, FilePathGenerator generator) {
        if (!this.mIsReleased) {
            mBurstFilePathGeneratorMap.put(Integer.valueOf(burstId), generator);
        }
    }

    private void setBurstMediaProviderUpdator(int burstId, MediaProviderUpdator updator) {
        if (!this.mIsReleased) {
            mBurstMediaProviderUpdatorMap.put(Integer.valueOf(burstId), updator);
            if (mBurstStoreAndMediaProviderUpdatorExecutor == null) {
                mBurstStoreAndMediaProviderUpdatorExecutor = Executors.newSingleThreadExecutor();
            }
        }
    }

    public static boolean isCurrentStorageFull() {
        if (mBurstStorageFullDetector == null) {
            return true;
        }
        return mBurstStorageFullDetector.isCurrentStorageFull();
    }

    public static String getNextBurstFilePath(int burstId) {
        return ((FilePathGenerator) mBurstFilePathGeneratorMap.get(Integer.valueOf(burstId))).getNextFilePathToStorePicture();
    }

    public final boolean waitForCurrentSavingTaskStackCleared(long waitTimeoutMillisec) {
        if (this.mIsReleased) {
            return DEBUGLOG;
        }
        mIsBurstApplicationFinishing = true;
        long start = System.currentTimeMillis();
        while (!isBurstSavingTaskStackEmpty()) {
            try {
                Thread.sleep(100);
            } catch (Exception exception) {
                Log.e("TraceLog", "CameraExtension.waitForCurrentSavingTaskStackCleared():[Wait FAILED]" + exception);
            }
            if (waitTimeoutMillisec < System.currentTimeMillis() - start) {
                return DEBUGLOG;
            }
        }
        return true;
    }

    public final void setBurstShutterSoundFilePath(String filePath) {
        if (!this.mIsReleased) {
            native_setBurstShutterSoundFilePath(filePath);
        }
    }

    public void startVideoMetadata() {
        if (!this.mIsReleased) {
            native_startVideoMetadata();
        }
    }

    public void stopVideoMetadata() {
        if (!this.mIsReleased) {
            native_stopVideoMetadata();
        }
    }

    private static void clearCachedLogicObjectAccordingTo(int burstId) {
        mBurstFilePathGeneratorMap.remove(Integer.valueOf(burstId));
        mBurstMediaProviderUpdatorMap.remove(Integer.valueOf(burstId));
    }
}
