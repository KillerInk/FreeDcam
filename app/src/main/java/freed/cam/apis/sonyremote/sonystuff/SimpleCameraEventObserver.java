/*
 * Copyright 2014 Sony Corporation
 */

package freed.cam.apis.sonyremote.sonystuff;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * A simple observer class for some status values in Camera. This class supports
 * only a few of values of getEvent result, so please add implementation for the
 * rest of values you want to handle.
 */
public class SimpleCameraEventObserver {

    private static final String TAG = SimpleCameraEventObserver.class.getSimpleName();

    private void sendLog(String msg)
    {
        boolean LOGGING = false;
        if (LOGGING)
            Log.d(SimpleCameraEventObserver.TAG, msg);
    }


    public interface CameraStatus{
        /**
         * Called when the value of "Camera Status" is changed.
         *
         * @param status camera status (ex."IDLE")
         */
        void onCameraStatusChanged(String status);
    }

    /**
     * A listener interface to receive these changes. These methods will be
     * called by UI thread.
     */
    public interface ChangeListener extends CameraStatus {

        /**
         * Called when the list of available APIs is modified.
         * 
         * @param apis a list of available APIs
         */
        void onApiListModified(List<String> apis);


        /**
         * Called when the value of "Liveview Status" is changed.
         * 
         * @param status liveview status (ex.true)
         */
        void onLiveviewStatusChanged(boolean status);

        /**
         * Called when the value of "Shoot Mode" is changed.
         * 
         * @param shootMode shoot mode (ex."still")
         */
        void onShootModeChanged(String shootMode);

        /**
         * Called when the value of "zoomPosition" is changed.
         * 
         * @param zoomPosition zoom position (ex.12)
         */
        void onZoomPositionChanged(int zoomPosition);

        /**
         * Called when the value of "storageId" is changed.
         * 
         * @param storageId storageId (ex. "Memory Card 1")
         */
        void onStorageIdChanged(String storageId);

        // :
        // : add methods for Event data as necessary.

        void onTimout();

        void onIsoChanged(String iso);

        void onIsoValuesChanged(String[] isovals);
        void onFnumberChanged(String fnumber);
        void onFnumberValuesChanged(String[]  fnumbervals);
        void onExposureCompensationChanged(int epxosurecomp);
        void onExposureCompensationMaxChanged(int epxosurecompmax);
        void onExposureCompensationMinChanged(int epxosurecompmin);
        void onShutterSpeedChanged(String shutter);
        void onShutterSpeedValuesChanged(String[]  shuttervals);
        void onFlashChanged(String flash);
        void onFocusLocked(boolean locked);
        void onWhiteBalanceValueChanged(String wb);

        void onImagesRecieved(String[] url);

        void onExposureModeChanged(String expomode);
        void onExposureModesChanged(String[] expomode);
        void onImageFormatChanged(String imagesize);
        void onImageFormatsChanged(String[] imagesize);
        void onImageSizeChanged(String imagesize);
        void onContshotModeChanged(String imagesize);
        void onContshotModesChanged(String[] imagesize);
        void onFocusModeChanged(String imagesize);
        void onFocusModesChanged(String[] imagesize);
        void onPostviewModeChanged(String imagesize);
        void onPostviewModesChanged(String[] imagesize);
        void onTrackingFocusModeChanged(String imagesize);
        void onTrackingFocusModesChanged(String[] imagesize);
        void onZoomSettingValueCHanged(String value);
        void onZoomSettingsValuesCHanged(String[] values);
    }


    private final Handler mUiHandler;

    private final SimpleRemoteApi mRemoteApi;

    private ChangeListener mListener;
    private CameraStatus mStateListener;

    private boolean mWhileEventMonitoring;

    private boolean mIsActive;

    // Current Liveview Status value.
    private boolean mLiveviewStatus;

    // Current Zoom Position value.
    private int mZoomPosition;

    // Current Storage Id value.
    private String mStorageId;

    private String iso;

    private String[] mIsovals;

    private String fnumber;
    private String[] mFnumbervals;

    private String[] mShuttervals;
    private String shutter;

    private String flash;

    private int mExposureCompMax;
    private int mExposureCompMin;
    private String version = "1.0";


    public String getVersion()
    {
        return version;
    }

    // :
    // : add attributes for Event data as necessary.

    /**
     * Constructor.
     *
     * @param context context to notify the changes by UI thread.
     * @param apiClient API client
     */
    public SimpleCameraEventObserver(Context context, SimpleRemoteApi apiClient) {
        if (context == null) {
            throw new IllegalArgumentException("context is null.");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("apiClient is null.");
        }
        this.mRemoteApi = apiClient;
        this.mUiHandler = new Handler(context.getMainLooper());
    }

    public void setEventVersion(String version)
    {
        this.version = version;
    }



    ///02-01 18:28:03.192  11377-11755/troop.freed.cam D/SimpleRemoteApi﹕ Response:
    // {"result":
    // [null,null,null,null,null,[],[],null,{"sceneRecognition":"None","type":"sceneRecognition","motionRecognition":"None","steadyRecognition":"Tripod"},
    // null,[],null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    // {"type":"isoSpeedRate","isoSpeedRateCandidates":[],"currentIsoSpeedRate":"AUTO"},null,null,
    // {"type":"shutterSpeed","shutterSpeedCandidates":[],"currentShutterSpeed":"1\/40"},null,null,
    // {"type":"focusStatus","focusStatus":"Not Focusing"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],
    // "id":73}
    // WHERE THE FUCK IS THIS DESCRIPED IN THE FUCKING API? "sceneRecognition":"None","type":"sceneRecognition","motionRecognition":"None","steadyRecognition":"Tripod"
    /**
     * Starts monitoring by continuously calling getEvent API.
     *
     * @return true if it successfully started, false if a monitoring is already
     *         started.
     */
    public void start() {
        if (!this.mIsActive) {
            this.sendLog("start() observer is not active.");
            return;
        }

        if (mWhileEventMonitoring) {
            sendLog("start() already starting.");
            return;
        }

        mWhileEventMonitoring = true;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                SimpleCameraEventObserver.this.sendLog("start() exec.");
                // Call getEvent API continuously.
                boolean firstCall = true;
                JSONObject replyJson;
                while (mWhileEventMonitoring)
                {

                    // At first, call as non-Long Polling.
                    boolean longPolling = !firstCall;

                    try {
                        // Call getEvent API.
                        replyJson = SimpleCameraEventObserver.this.mRemoteApi.getEvent(longPolling, SimpleCameraEventObserver.this.version);
                        if (firstCall)
                            Log.d(TAG,replyJson.toString());

                        // Check error code at first.
                        int errorCode = JsonUtils.findErrorCode(replyJson);
                        SimpleCameraEventObserver.this.sendLog("getEvent errorCode: " + errorCode);

                        SimpleCameraEventObserver.this.processEvents(replyJson);

                    } catch (IOException e) {
                        try {
                            replyJson = SimpleCameraEventObserver.this.mRemoteApi.getEvent(false, SimpleCameraEventObserver.this.version);
                            SimpleCameraEventObserver.this.processEvents(replyJson);
                        }
                        catch (IOException ex) {
                            // Occurs when the server is not available now.
                            SimpleCameraEventObserver.this.sendLog("getEvent timeout by client trigger.");
                            SimpleCameraEventObserver.this.fireTimeoutListener();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    } catch (JSONException e) {
                        SimpleCameraEventObserver.this.sendLog("getEvent: JSON format error. " + e.getMessage());
                    }

                    firstCall = false;
                } // MONITORLOOP end.

                SimpleCameraEventObserver.this.mWhileEventMonitoring = false;
            }
        });
    }

    public void processEvents(JSONObject replyJson) throws JSONException
    {
        //1
        List<String> availableApis = JsonUtils.findAvailableApiList(replyJson);
        if (!availableApis.isEmpty()) {
            this.fireApiListModifiedListener(availableApis);
        }

        //2 CameraStatus
        String cameraStatus = JsonUtils.findStringInformation(replyJson, 1, "cameraStatus", "cameraStatus");

        if (cameraStatus != null && !TextUtils.isEmpty(cameraStatus))
        {
            this.sendLog("getEvent cameraStatus: " + cameraStatus);
            this.fireCameraStatusChangeListener(cameraStatus);
        }

        //3 zoomPosition
        int zoomPosition = JsonUtils.findZoomInformation(replyJson);

        if (zoomPosition != -1) {
            this.mZoomPosition = zoomPosition;
            this.sendLog("getEvent zoomPosition: " + zoomPosition);
            this.fireZoomInformationChangeListener(0, zoomPosition, 0);
        }

        //3 LiveviewStatus
        Boolean liveviewStatus = JsonUtils.findLiveviewStatus(replyJson);

        if (liveviewStatus != null && !liveviewStatus.equals(this.mLiveviewStatus))
        {
            this.sendLog("getEvent liveviewStatus: " + liveviewStatus);
            this.mLiveviewStatus = liveviewStatus;
            this.fireLiveviewStatusChangeListener(liveviewStatus);
        }

        //4 liveview Orientation
        //TODO add orientation

        //5
        this.processActShotImage(replyJson);

        //6-9 emtpy

        //10 storage Information
        // storageId
        String storageId = JsonUtils.findStorageId(replyJson);

        if (storageId != null && !storageId.equals(this.mStorageId)) {
            this.mStorageId = storageId;
            this.sendLog("getEvent storageId:" + storageId);
            this.fireStorageIdChangeListener(storageId);
        }

        //11 beepmode
        //Todo beepmode

        //12 camera function

        //13 movie quality

        //14 still size
        String imagesize = JsonUtils.findStringInformation(replyJson, 14, "stillSize", "currentSize");
        if (imagesize != null || TextUtils.isEmpty(imagesize))
        {
            this.sendLog("getEvent imagesize: " +imagesize);
            this.fireImageSizeChangedListener(imagesize);
        }

        //15 cameraFunctionResult

        //16 Steady mode

        //17View angle

        //18 Exposure mode
        String expoMode = JsonUtils.findStringInformation(replyJson, 18, "exposureMode", "currentExposureMode");
        if (expoMode != null && !TextUtils.isEmpty(expoMode))
        {
            this.sendLog("getEvent expoMode: " + expoMode);
            this.fireExpoModeChangedListener(expoMode);
        }

        String[] expomodes = JsonUtils.findStringArrayInformation(replyJson, 18, "exposureMode", "exposureModeCandidates");
        if (expomodes != null && expomodes.length > 0)
        {
            this.sendLog("getEvent expoModes: " + expomodes.length);
            this.fireExpoModesChangedListener(expomodes);
        }

        //19 PostView Image Size
        String postview = JsonUtils.findStringInformation(replyJson, 19, "postviewImageSize", "currentPostviewImageSize");
        if (postview != null && !TextUtils.isEmpty(postview))
        {
            this.sendLog("getEvent postviewSize: " + postview);
            this.firePostviewChangedListener(postview);
        }

        String[] postviews = JsonUtils.findStringArrayInformation(replyJson, 19, "postviewImageSize", "postviewImageSizeCandidates");
        if (postviews != null && postviews.length > 0)
        {
            this.sendLog("getEvent postviewmodes: " + postviews.length);
            this.firePostViewModesChangedListener(postviews);
        }

        //20 selftimer

        //21 shootmode
        String shootMode = JsonUtils.findShootMode(replyJson);

        if (shootMode != null) {
            this.sendLog("getEvent shootMode: " + shootMode);
            this.fireShootModeChangeListener(shootMode);
        }

        //22-24 reserved/emtpy

        //25 exposure comepensation
        int minexpo = JsonUtils.findIntInformation(replyJson, "exposureCompensation", "minExposureCompensation");

        if (minexpo != -5000 && minexpo != this.mExposureCompMin)
        {
            this.sendLog("getEvent minExposure: " + minexpo);
            this.mExposureCompMin = minexpo;
            this.fireExposurCompMinChangeListener(minexpo);
        }
        int maxexpo = JsonUtils.findIntInformation(replyJson, "exposureCompensation", "maxExposureCompensation");

        if (maxexpo != -5000 && maxexpo != this.mExposureCompMax)
        {
            this.sendLog("getEvent maxExposure: " + maxexpo);
            this.mExposureCompMax = maxexpo;
            this.fireExposurCompMaxChangeListener(maxexpo);
        }

        int cexpo = JsonUtils.findIntInformation(replyJson, "exposureCompensation", "currentExposureCompensation");

        if (cexpo != -5000)
        {
            this.sendLog("getEvent currentExposure: " + cexpo);
            int mExposureComp = cexpo;
            this.fireExposurCompChangeListener(cexpo + minexpo * -1);
        }

        //26 flash
        String mflash = JsonUtils.findStringInformation(replyJson, 26, "flashMode", "currentFlashMode");
        if (mflash != null && !TextUtils.isEmpty(mflash) && !mflash.equals(this.flash))
        {
            this.flash = mflash;
            this.sendLog("getEvent flash:" + this.flash);
            this.fireFlashChangeListener(this.flash);
        }

        //27fnumber
        this.processFnumberStuff(replyJson);

        //28 focusmode
        String focus = JsonUtils.findStringInformation(replyJson, 28, "focusMode", "currentFocusMode");
        if (focus != null && !TextUtils.isEmpty(focus))
        {
            this.sendLog("getEvent focusmode: " +focus);
            this.fireFocusChangedListener(focus);
        }

        String[] focusmodes = JsonUtils.findStringArrayInformation(replyJson, 28, "focusMode", "focusModeCandidates");
        if (focusmodes != null && focusmodes.length > 0)
        {
            this.sendLog("getEvent focusmodes: " + Arrays.toString(focusmodes));
            this.fireFocusModesChangedListener(focusmodes);
        }
        //29 iso
        this.processIsoStuff(replyJson);


        //30 reserved/emtpy
        //31 program mode shifte

        //32 shutter
        this.processShutterSpeedStuff(replyJson);

        //33 whitebalance
        String wbval = JsonUtils.findStringInformation(replyJson,33, "whiteBalance", "currentWhiteBalanceMode");
        if (!TextUtils.isEmpty(wbval))
        {
            this.fireWbChangeListener(wbval);
            this.sendLog("WB mode: " + wbval);
        }

        //34touch af position
        String touchSuccess = JsonUtils.findStringInformation(replyJson, 34, "touchAFPosition", "currentSet");
        if (touchSuccess != null || !TextUtils.isEmpty(touchSuccess))
            this.sendLog("got focus sucess:" + touchSuccess);

        //35 focus status
        String focusStatus = JsonUtils.findStringInformation(replyJson, 35, "focusStatus", "focusStatus");
        if (!TextUtils.isEmpty(focusStatus))
        {
            this.sendLog("focusstate: " + focusStatus);
            if (focusStatus.equals("Not Focusing"))
                this.fireFocusLockedChangeListener(false);
            if (focusStatus.equals("Focused"))
                this.fireFocusLockedChangeListener(true);

        }

        //36 zoom settings
        String zoomSetting = JsonUtils.findStringInformation(replyJson, 36, "zoomSetting", "zoom");
        if (zoomSetting != null && !TextUtils.isEmpty(zoomSetting)) {
            this.sendLog("getEvent zoomSettings: " + zoomSetting);
            this.fireZoomSettingChangedListener(zoomSetting);
        }
        String[] zoomSettings = JsonUtils.findStringArrayInformation(replyJson, 36, "zoomSetting", "candidate");
        if (zoomSettings != null && zoomSettings.length > 0)
        {
            this.sendLog("getEvent zoomSettings: " + Arrays.toString(zoomSettings));
            this.fireZoomSettingsChangedListener(zoomSettings);
        }

        //37 still quality
        String imageFormat = JsonUtils.findStringInformation(replyJson, 37, "stillQuality", "stillQuality");
        if (imageFormat != null && !TextUtils.isEmpty(imageFormat)) {
            this.sendLog("getEvent imageformat: " + imageFormat);
            this.fireImageFormatChangedListener(imageFormat);
        }

        String[] imageformats = JsonUtils.findStringArrayInformation(replyJson, 37, "stillQuality", "candidate");
        if (imageformats != null && imageformats.length > 0)
        {
            this.sendLog("getEvent imageformats: " + Arrays.toString(imageformats));
            this.fireImageFormatsChangedListener(imageformats);
        }

        //38 cont shot
        String contshot = JsonUtils.findStringInformation(replyJson, 38, "contShootingMode", "contShootingMode");
        if (contshot != null && !TextUtils.isEmpty(contshot))
        {
            this.sendLog("getEvent contshot: " +contshot);
            this.fireContShotModeChangedListener(contshot);
        }

        String[] contshots = JsonUtils.findStringArrayInformation(replyJson, 38, "contShootingMode", "candidate");
        if (contshots != null && contshots.length > 0)
        {
            this.sendLog("getEvent contshots: " + contshot);
            this.fireContShotModesChangedListener(contshots);
        }

        //39 cont shot speed

        //40 cont shot urls
        this.processContShootImage(replyJson);

        //41 flipsettings

        //42 scene selection
        //43 interval time
        //44 color settings
        //45 Movie file format
        //46-51 reserved
        //52 IR remote control setting
        //53 TV color system
        //54 Tracking focus status
        String trackingFocusStatus = JsonUtils.findStringInformation(replyJson, 54, "trackingFocusStatus","trackingFocusStatus");
        if (!TextUtils.isEmpty(trackingFocusStatus))
        {
            this.sendLog("tracking focusstate: " + trackingFocusStatus);
            if (trackingFocusStatus.equals("Tracking"))
                this.fireFocusLockedChangeListener(true);
            if (trackingFocusStatus.equals("Not Tracking"))
                this.fireFocusLockedChangeListener(false);
        }
        //55Tracking focus setting
        String tf = JsonUtils.findStringInformation(replyJson, 55, "trackingFocus", "trackingFocus");
        if (tf != null && !TextUtils.isEmpty(tf))
        {
            this.sendLog("getEvent contshot: " +tf);
            this.fireTrackingFocusChangedListener(tf);
        }

        String[] tfs= JsonUtils.findStringArrayInformation(replyJson, 55, "trackingFocus", "candidate");
        if (tfs != null && contshots.length > 0)
        {
            this.sendLog("getEvent contshots: " + Arrays.toString(tfs));
            this.fireTrackingFocusModesChangedListener(tfs);
        }
        //56 BatteryStatus
        //57 Recording time

        //58Number of shots
        //59Auto power off time


        // :
        // : add implementation for Event data as necessary.
    }

    private void processActShotImage(JSONObject replyJson)
    {
        ArrayList<String> values = new ArrayList<>();

        JSONArray resultsObj = null;
        try {
            resultsObj = replyJson.getJSONArray("result");

            if (!resultsObj.isNull(5)) {
                JSONArray InformationObj = resultsObj.getJSONArray(5);
                if (!InformationObj.isNull(0)) {
                    JSONObject object = InformationObj.getJSONObject(0);
                    String type = object.getString("type");
                    if ("takePicture".equals(type)) {
                        JSONObject val = InformationObj.getJSONObject(0);
                        for (int i = 0; i < val.getJSONArray("takePictureUrl").length(); i++) {
                            values.add(val.getJSONArray("takePictureUrl").getString(i));
                        }
                        this.fireImageListener(values.toArray(new String[values.size()]));
                    }
                }
            }
        } catch (JSONException ex) {
            Log.WriteEx(ex);
        }
    }

    private void processContShootImage(JSONObject replyJson)
    {
        //String[] shuttervals = JsonUtils.findStringArrayInformation(replyJson, 40, "contShooting", "contShootingUrl");
        ArrayList<String> values = new ArrayList<>();

        JSONArray resultsObj = null;
        try {
            resultsObj = replyJson.getJSONArray("result");

        if (!resultsObj.isNull(40)) {
            JSONObject InformationObj = resultsObj.getJSONObject(40);
            String type = InformationObj.getString("type");
            if ("contShooting".equals(type))
            {
                JSONArray array = InformationObj.getJSONArray("contShootingUrl");
                for (int i = 0; i<array.length();i++)
                {
                    JSONObject ob = array.getJSONObject(i);
                    values.add(ob.getString("postviewUrl"));
                }
                this.fireImageListener(values.toArray(new String[values.size()]));
            }
        }
        } catch (JSONException ex) {
            Log.WriteEx(ex);
        }
    }

    private void processShutterSpeedStuff(JSONObject replyJson) throws JSONException
    {
        String[] shuttervals = JsonUtils.findStringArrayInformation(replyJson, 32, "shutterSpeed", "shutterSpeedCandidates");
        if (shuttervals != null && !Arrays.equals(shuttervals, this.mShuttervals) && shuttervals.length > 0)
        {
            this.mShuttervals = shuttervals;
            this.fireShutterValuesChangeListener(this.mShuttervals);
        }
        String shutterv = JsonUtils.findStringInformation(replyJson,32, "shutterSpeed", "currentShutterSpeed");
        if (shutterv != null && !TextUtils.isEmpty(shutterv) && !shutterv.equals(this.shutter))
        {
            this.shutter = shutterv;
            this.sendLog("getEvent shutter:" + this.shutter);
            this.fireShutterSpeedChangeListener(this.shutter);
        }
    }

    private void processFnumberStuff(JSONObject replyJson) throws JSONException {
        String[] fnumbervals = JsonUtils.findStringArrayInformation(replyJson, 27, "fNumber", "fNumberCandidates");

        if (fnumbervals != null && !Arrays.equals(fnumbervals, this.mFnumbervals) && fnumbervals.length > 0)
        {
            this.sendLog("getEvent fnumber vals: " + fnumbervals.length);
            this.mFnumbervals = fnumbervals;
            this.fireFnumberValuesChangeListener(this.mFnumbervals);
        }

        String fnumberv = JsonUtils.findStringInformation(replyJson,27, "fNumber", "currentFNumber");

        if (fnumberv != null && !TextUtils.isEmpty(fnumberv) && !fnumberv.equals(this.fnumber))
        {
            this.fnumber = fnumberv;
            this.sendLog("getEvent fnumber:" + this.fnumber);
            this.fireFNumberChangeListener(this.fnumber);
        }
    }

    private void processIsoStuff(JSONObject replyJson) throws JSONException {
        String[] isovals = JsonUtils.findStringArrayInformation(replyJson, 29, "isoSpeedRate", "isoSpeedRateCandidates");

        if (isovals != null && !Arrays.equals(isovals, this.mIsovals) && isovals.length > 0)
        {
            this.mIsovals = isovals;
            this.sendLog("getEvent isovalues: " + Arrays.toString(isovals));
            this.fireIsoValuesChangeListener(this.mIsovals);
        }
        String isoval = JsonUtils.findStringInformation(replyJson,29, "isoSpeedRate", "currentIsoSpeedRate");

        if (isoval != null && !TextUtils.isEmpty(isoval) && !isoval.equals(this.iso))
        {

            this.iso = isoval;
            this.sendLog( "getEvent isoVal:" + this.iso);
            this.fireIsoChangeListener(this.iso);
        }
    }

    /**
     * Requests to stop the monitoring.
     */
    public void stop() {
        this.mWhileEventMonitoring = false;
    }

    /**
     * Requests to release resource.
     */
    public void release() {
        this.mWhileEventMonitoring = false;
        this.mIsActive = false;
    }

    public void activate() {
        this.mIsActive = true;
    }

    public boolean isActive()
    {
        return mIsActive;
    }

    /**
     * Checks to see whether a monitoring is already started.
     *
     * @return true when monitoring is started.
     */
    public boolean isStarted() {
        return mWhileEventMonitoring;
    }

    /**
     * Sets a listener object.
     *
     * @param listener
     */
    public void setEventChangeListener(ChangeListener listener) {
        mListener = listener;
    }

    /**
     * Sets a listener object.
     *
     * @param listener
     */
    public void setCameraStateChangedListener(CameraStatus listener) {
        mStateListener = listener;
    }

    /**
     * Clears a listener object.
     */
    public void clearEventChangeListener() {
        mListener = null;
    }

    /**
     * Returns the current Camera Status value.
     * 
     * @return camera status
     */
    public boolean getLiveviewStatus() {
        return mLiveviewStatus;
    }

    /**
     * Returns the current Zoom Position value.
     * 
     * @return zoom position
     */
    public int getZoomPosition() {
        return mZoomPosition;
    }

    /**
     * Returns the current Storage Id value.
     * 
     * @return
     */
    public String getStorageId() {
        return mStorageId;
    }

    private void fireExposurCompMinChangeListener(final int ex) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onExposureCompensationMinChanged(ex);
                }
            }
        });
    }

    private void fireExposurCompMaxChangeListener(final int ex) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onExposureCompensationMaxChanged(ex);
                }
            }
        });
    }

    private void fireExposurCompChangeListener(final int ex) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onExposureCompensationChanged(ex);
                }
            }
        });
    }

    private void fireFNumberChangeListener(final String pfnum) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFnumberChanged(pfnum);
                }
            }
        });
    }

    private void fireImageListener(final String[] pfnum) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onImagesRecieved(pfnum);
                }
            }
        });
    }

    private void fireFnumberValuesChangeListener(final String[] pfnum) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFnumberValuesChanged(pfnum);
                }
            }
        });
    }

    private void fireShutterValuesChangeListener(final String[] pfnum) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onShutterSpeedValuesChanged(pfnum);
                }
            }
        });
    }

    private void fireShutterSpeedChangeListener(final String pfnum) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onShutterSpeedChanged(pfnum);
                }
            }
        });
    }

    private void fireFlashChangeListener(final String pfnum) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFlashChanged(pfnum);
                }
            }
        });
    }

    /**
     * Notify the change of available APIs
     * 
     * @param availableApis
     */
    private void fireApiListModifiedListener(final List<String> availableApis) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onApiListModified(availableApis);
                }
            }
        });
    }

    private void fireTimeoutListener() {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onTimout();
                }
            }
        });
    }

    /**
     * Notify the change of Camera Status.
     * 
     * @param status
     */
    private void fireCameraStatusChangeListener(final String status) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onCameraStatusChanged(status);
                }
                if (mStateListener != null) {
                    mStateListener.onCameraStatusChanged(status);
                }
            }
        });
    }

    /**
     * Notify the change of Liveview Status.
     * 
     * @param status
     */
    private void fireLiveviewStatusChangeListener(final boolean status) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onLiveviewStatusChanged(status);
                }
            }
        });
    }

    /**
     * Notify the change of Shoot Mode.
     * 
     * @param shootMode
     */
    private void fireShootModeChangeListener(final String shootMode) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onShootModeChanged(shootMode);
                }
                else Log.d(TAG, "onShootModeChanged listner NULL!");
            }
        });
    }

    /**
     * Notify the change of Zoom Information
     * @param zoomNumberBox
     * @param zoomPosition
     * @param zoomPositionCurrentBox
     */
    private void fireZoomInformationChangeListener(int zoomNumberBox,
                                                   final int zoomPosition, int zoomPositionCurrentBox) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onZoomPositionChanged(zoomPosition);
                }
            }
        });
    }

    /**
     * Notify the change of Storage Id.
     * 
     * @param storageId
     */
    private void fireStorageIdChangeListener(final String storageId) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onStorageIdChanged(storageId);
                }
            }
        });
    }

    private void fireIsoChangeListener(final String iso) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onIsoChanged(iso);
                }
            }
        });
    }

    private void fireIsoValuesChangeListener(final String[] iso) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onIsoValuesChanged(iso);
                }
            }
        });
    }

    private void fireFocusLockedChangeListener(final boolean locked) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFocusLocked(locked);
                }
            }
        });
    }

    private void fireWbChangeListener(final String wb) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onWhiteBalanceValueChanged(wb);
                }
            }
        });
    }

    private void fireExpoModeChangedListener(final String expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onExposureModeChanged(expo);
                }
            }
        });
    }

    private void fireExpoModesChangedListener(final String[] expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onExposureModesChanged(expo);
                }
            }
        });
    }

    private void fireZoomSettingsChangedListener(final String[] expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onZoomSettingsValuesCHanged(expo);
                }
            }
        });
    }

    private void fireImageFormatsChangedListener(final String[] expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onImageFormatsChanged(expo);
                }
            }
        });
    }

    private void fireZoomSettingChangedListener(final String expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onZoomSettingValueCHanged(expo);
                }
            }
        });
    }

    private void fireImageFormatChangedListener(final String expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onImageFormatChanged(expo);
                }
            }
        });
    }


    private void fireImageSizeChangedListener(final String expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onImageSizeChanged(expo);
                }
            }
        });
    }

    private void fireContShotModesChangedListener(final String[] expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onContshotModesChanged(expo);
                }
            }
        });
    }

    private void fireContShotModeChangedListener(final String expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onContshotModeChanged(expo);
                }
            }
        });
    }

    private void fireFocusModesChangedListener(final String[] expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFocusModesChanged(expo);
                }
            }
        });
    }

    private void fireFocusChangedListener(final String expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFocusModeChanged(expo);
                }
            }
        });
    }

    private void firePostViewModesChangedListener(final String[] expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onPostviewModesChanged(expo);
                }
            }
        });
    }

    private void firePostviewChangedListener(final String expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onPostviewModeChanged(expo);
                }
            }
        });
    }

    private void fireTrackingFocusModesChangedListener(final String[] expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onTrackingFocusModesChanged(expo);
                }
            }
        });
    }

    private void fireTrackingFocusChangedListener(final String expo) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onTrackingFocusModeChanged(expo);
                }
            }
        });
    }


}
