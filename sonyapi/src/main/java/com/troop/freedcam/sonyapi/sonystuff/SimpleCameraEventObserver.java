/*
 * Copyright 2014 Sony Corporation
 */

package com.troop.freedcam.sonyapi.sonystuff;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple observer class for some status values in Camera. This class supports
 * only a few of values of getEvent result, so please add implementation for the
 * rest of values you want to handle.
 */
public class SimpleCameraEventObserver {

    private static final String TAG = SimpleCameraEventObserver.class.getSimpleName();

    boolean LOGGING = false;

    private void sendLog(String msg)
    {
        if (LOGGING)
            Log.d(TAG, msg);
    }

    /**
     * A listener interface to receive these changes. These methods will be
     * called by UI thread.
     */
    public interface ChangeListener {

        /**
         * Called when the list of available APIs is modified.
         * 
         * @param apis a list of available APIs
         */
        void onApiListModified(List<String> apis);

        /**
         * Called when the value of "Camera Status" is changed.
         * 
         * @param status camera status (ex."IDLE")
         */
        void onCameraStatusChanged(String status);

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
        public void onFnumberChanged(String fnumber);
        public void onFnumberValuesChanged(String[]  fnumbervals);
        void onExposureCompensationChanged(int epxosurecomp);
        void onExposureCompensationMaxChanged(int epxosurecompmax);
        void onExposureCompensationMinChanged(int epxosurecompmin);
        public void onShutterSpeedChanged(String shutter);
        public void onShutterSpeedValuesChanged(String[]  shuttervals);
        void onFlashChanged(String flash);
        void onFocusLocked(boolean locked);
        void onWhiteBalanceValueChanged(String wb);
        void onWbColorTemperatureChanged(int colortemp);
        void onPostViewImageRevieved(String url);
        void onImageRecieved(String url);
        void onImagesRecieved(String[] url);
        void onProgramShiftValueChanged(int shift);
        void onProgramShiftValuesChanged(String[] shift);

    }

    /**
     * Abstract class to receive these changes. please override methods that you
     * need.
     */
    public abstract static class ChangeListenerTmpl implements ChangeListener {

        @Override
        public void onApiListModified(List<String> apis) {
        }

        @Override
        public void onCameraStatusChanged(String status) {
        }

        @Override
        public void onLiveviewStatusChanged(boolean status) {
        }

        @Override
        public void onShootModeChanged(String shootMode) {
        }

        @Override
        public void onZoomPositionChanged(int zoomPosition) {
        }

        @Override
        public void onStorageIdChanged(String storageId) {
        }

        public void onTimout()
        {

        }
        public void onIsoChanged(String iso)
        {

        }

        public void onFnumberChanged(String fnumber)
        {

        }
        public void onExposureCompensationChanged(int epxosurecomp){};

    }

    protected final Handler mUiHandler;

    private SimpleRemoteApi mRemoteApi;

    protected ChangeListener mListener;

    private boolean mWhileEventMonitoring = false;

    private boolean mIsActive = false;

    // Current Camera Status value.
    private String mCameraStatus;

    // Current Liveview Status value.
    private boolean mLiveviewStatus;

    // Current Shoot Mode value.
    private String mShootMode;

    // Current Zoom Position value.
    private int mZoomPosition;

    // Current Storage Id value.
    private String mStorageId;

    private String iso;

    String[] mIsovals;

    private String fnumber;
    String[] mFnumbervals;

    String[] mShuttervals;
    private String shutter;

    private String flash;

    int mExposureComp;
    int mExposureCompMax;
    int mExposureCompMin;
    String version;

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
        mRemoteApi = apiClient;
        mUiHandler = new Handler(context.getMainLooper());
    }



    ///02-01 18:28:03.192  11377-11755/troop.com.freedcam D/SimpleRemoteApi﹕ Response:
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
    public boolean start() {
        if (!mIsActive) {
            sendLog("start() observer is not active.");
            return false;
        }

        if (mWhileEventMonitoring) {
            sendLog("start() already starting.");
            return false;
        }

        mWhileEventMonitoring = true;
        new Thread() {

            @Override
            public void run() {
                sendLog("start() exec.");
                // Call getEvent API continuously.
                boolean firstCall = true;
                MONITORLOOP: while (mWhileEventMonitoring) {

                    // At first, call as non-Long Polling.
                    boolean longPolling = !firstCall;

                    try {
                        // Call getEvent API.
                        JSONObject replyJson;
                        if(version == null || version == "")
                        {
                            replyJson = mRemoteApi.getVersions();
                            JSONArray array = replyJson.getJSONArray("result");
                            array = array.getJSONArray(0);
                            version = array.getString(array.length()-1);
                        }

                        replyJson = mRemoteApi.getEvent(longPolling, version);

                        // Check error code at first.
                        int errorCode = JsonUtils.findErrorCode(replyJson);
                        sendLog("getEvent errorCode: " + errorCode);
                        switch (errorCode) {
                            case 0: // no error
                                // Pass through.
                                break;
                            case 1: // "Any" error
                            case 12: // "No such method" error
                                break MONITORLOOP; // end monitoring.
                            case 2: // "Timeout" error
                                // Re-call immediately.
                                continue MONITORLOOP;
                            case 40402: // "Already polling" error
                                // Retry after 5 sec.
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    // do nothing.
                                }
                                continue MONITORLOOP;
                            default:
                                sendLog("SimpleCameraEventObserver: Unexpected error: "
                                        + errorCode);
                                break MONITORLOOP; // end monitoring.
                        }

                        processEvents(replyJson);

                    } catch (IOException e) {
                        // Occurs when the server is not available now.
                        sendLog("getEvent timeout by client trigger.");
                        fireTimeoutListener();
                        break MONITORLOOP;
                    } catch (JSONException e) {
                        sendLog("getEvent: JSON format error. " + e.getMessage());
                        break MONITORLOOP;
                    }

                    firstCall = false;
                } // MONITORLOOP end.

                mWhileEventMonitoring = false;
            }


        }.start();

        return true;
    }

    protected void processEvents(JSONObject replyJson) throws JSONException {
        List<String> availableApis = JsonUtils.findAvailableApiList(replyJson);
        if (!availableApis.isEmpty()) {
            fireApiListModifiedListener(availableApis);
        }

        // CameraStatus
        String cameraStatus = JsonUtils.findCameraStatus(replyJson);
        sendLog("getEvent cameraStatus: " + cameraStatus);
        if (cameraStatus != null && !cameraStatus.equals(mCameraStatus)) {
            mCameraStatus = cameraStatus;
            fireCameraStatusChangeListener(cameraStatus);
        }

        // LiveviewStatus
        Boolean liveviewStatus = JsonUtils.findLiveviewStatus(replyJson);
        sendLog("getEvent liveviewStatus: " + liveviewStatus);
        if (liveviewStatus != null && !liveviewStatus.equals(mLiveviewStatus)) {
            mLiveviewStatus = liveviewStatus;
            fireLiveviewStatusChangeListener(liveviewStatus);
        }

        // ShootMode
        String shootMode = JsonUtils.findShootMode(replyJson);

        if (shootMode != null && !shootMode.equals(mShootMode)) {
            mShootMode = shootMode;
            sendLog("getEvent shootMode: " + shootMode);
            fireShootModeChangeListener(shootMode);
        }

        // zoomPosition
        int zoomPosition = JsonUtils.findZoomInformation(replyJson);

        if (zoomPosition != -1) {
            mZoomPosition = zoomPosition;
            sendLog("getEvent zoomPosition: " + zoomPosition);
            fireZoomInformationChangeListener(0, 0, zoomPosition, 0);
        }

        int minexpo = JsonUtils.findIntInformation(replyJson, 25, "exposureCompensation", "minExposureCompensation");

        if (minexpo != -1 && minexpo != mExposureCompMin)
        {
            sendLog("getEvent minExposure: " + minexpo);
            mExposureCompMin = minexpo;
            fireExposurCompMinChangeListener(minexpo);
        }
        int maxexpo = JsonUtils.findIntInformation(replyJson, 25, "exposureCompensation", "maxExposureCompensation");

        if (maxexpo != -1 && maxexpo != mExposureCompMax)
        {
            sendLog("getEvent maxExposure: " + maxexpo);
            mExposureCompMax = maxexpo;
            fireExposurCompMaxChangeListener(maxexpo);
        }

        int cexpo = JsonUtils.findIntInformation(replyJson, 25, "exposureCompensation", "currentExposureCompensation");

        if (cexpo != -1)
        {
            sendLog("getEvent currentExposure: " + cexpo);
            mExposureComp = cexpo;
            fireExposurCompChangeListener(cexpo);
        }

        // storageId
        String storageId = JsonUtils.findStorageId(replyJson);

        if (storageId != null && !storageId.equals(mStorageId)) {
            mStorageId = storageId;
            sendLog("getEvent storageId:" + storageId);
            fireStorageIdChangeListener(storageId);
        }

        processIsoStuff(replyJson);

        processFnumberStuff(replyJson);

        processShutterSpeedStuff(replyJson);

        String mflash = JsonUtils.findStringInformation(replyJson, 26, "flashMode", "currentFlashMode");
        if (mflash != null && !mflash.equals("") && !mflash.equals(flash))
        {
            flash = mflash;
            sendLog("getEvent flash:" + flash);
            fireFlashChangeListener(flash);
        }

        String touchSuccess = JsonUtils.findStringInformation(replyJson, 34,"touchAFPosition", "currentSet");
        sendLog("got focus sucess:" + touchSuccess);
        /*String[] focusArea = JsonUtils.findStringArrayInformation(replyJson, 34, "touchAFPosition", "currentTouchCoordinates");
        Log.d(TAG, "got focus areas: " + focusArea.toString());*/

        String trackingFocusStatus = JsonUtils.findStringInformation(replyJson, 54, "trackingFocusStatus","trackingFocusStatus");
        sendLog("tracking focusstate: " + trackingFocusStatus);
        if (!trackingFocusStatus.equals(""))
        {
            if (trackingFocusStatus.equals("Tracking"))
                fireFocusLockedChangeListener(true);
            if (trackingFocusStatus.equals("Not Tracking"))
                fireFocusLockedChangeListener(false);
        }

        String focusStatus = JsonUtils.findStringInformation(replyJson, 35, "focusStatus", "focusStatus");
        if (!focusStatus.equals(""))
        {
            if (focusStatus.equals("Not Focusing"))
                fireFocusLockedChangeListener(false);
            if (focusStatus.equals("Focused"))
                fireFocusLockedChangeListener(true);

        }
        sendLog("focusstate: " + focusStatus);

        String wbval = JsonUtils.findStringInformation(replyJson,33, "whiteBalance", "currentWhiteBalanceMode");
        if (!wbval.equals(""))
        {
            fireWbChangeListener(wbval);
            sendLog("WB mode: " + wbval);
        }

        processContShootImage(replyJson);
        processActShotImage(replyJson);

        // :
        // : add implementation for Event data as necessary.
    }

    private void processActShotImage(JSONObject replyJson)
    {
        ArrayList<String> values = new ArrayList<String>();

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
                        fireImageListener(values.toArray(new String[values.size()]));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processContShootImage(JSONObject replyJson)
    {
        //String[] shuttervals = JsonUtils.findStringArrayInformation(replyJson, 40, "contShooting", "contShootingUrl");
        ArrayList<String> values = new ArrayList<String>();

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
                fireImageListener(values.toArray(new String[values.size()]));
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processShutterSpeedStuff(JSONObject replyJson) throws JSONException
    {
        String[] shuttervals = JsonUtils.findStringArrayInformation(replyJson, 32, "shutterSpeed", "shutterSpeedCandidates");
        if (shuttervals != null && !shuttervals.equals(mShuttervals) && shuttervals.length > 0)
        {
            mShuttervals = shuttervals;
            fireShutterValuesChangeListener(mShuttervals);
        }
        String shutterv = JsonUtils.findStringInformation(replyJson,32, "shutterSpeed", "currentShutterSpeed");
        if (shutterv != null && !shutterv.equals("") && !shutterv.equals(shutter))
        {
            shutter = shutterv;
            sendLog("getEvent shutter:" + shutter);
            fireShutterSpeedChangeListener(shutter);
        }
    }

    private void processFnumberStuff(JSONObject replyJson) throws JSONException {
        String[] fnumbervals = JsonUtils.findStringArrayInformation(replyJson, 27, "fNumber", "fNumberCandidates");

        if (fnumbervals != null && !fnumbervals.equals(mFnumbervals) && fnumbervals.length > 0)
        {
            sendLog("getEvent fnumber vals: " + fnumbervals.length);
            mFnumbervals = fnumbervals;
            fireFnumberValuesChangeListener(mFnumbervals);
        }

        String fnumberv = JsonUtils.findStringInformation(replyJson,27, "fNumber", "currentFNumber");

        if (fnumberv != null && !fnumberv.equals("") && !fnumberv.equals(fnumber))
        {
            fnumber = fnumberv;
            sendLog("getEvent fnumber:" + fnumber);
            fireFNumberChangeListener(fnumber);
        }
    }

    private void processIsoStuff(JSONObject replyJson) throws JSONException {
        String[] isovals = JsonUtils.findStringArrayInformation(replyJson, 29, "isoSpeedRate", "isoSpeedRateCandidates");

        if (isovals != null && !isovals.equals(mIsovals) && isovals.length > 0)
        {
            mIsovals = isovals;
            sendLog("getEvent isovalues: " + isovals);
            fireIsoValuesChangeListener(mIsovals);
        }
        String isoval = JsonUtils.findStringInformation(replyJson,29, "isoSpeedRate", "currentIsoSpeedRate");

        if (isoval != null && !isoval.equals("") && !isoval.equals(iso))
        {

            iso = isoval;
            sendLog( "getEvent isoVal:" + iso);
            fireIsoChangeListener(iso);
        }
    }

    /**
     * Requests to stop the monitoring.
     */
    public void stop() {
        mWhileEventMonitoring = false;
    }

    /**
     * Requests to release resource.
     */
    public void release() {
        mWhileEventMonitoring = false;
        mIsActive = false;
    }

    public void activate() {
        mIsActive = true;
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
    public String getCameraStatus() {
        return mCameraStatus;
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
     * Returns the current Shoot Mode value.
     * 
     * @return shoot mode
     */
    public String getShootMode() {
        return mShootMode;
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
            }
        });
    }

    /**
     * Notify the change of Zoom Information
     * 
     * @param zoomIndexCurrentBox
     * @param zoomNumberBox
     * @param zoomPosition
     * @param zoomPositionCurrentBox
     */
    private void fireZoomInformationChangeListener(final int zoomIndexCurrentBox,
            final int zoomNumberBox,
            final int zoomPosition, final int zoomPositionCurrentBox) {
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


}
