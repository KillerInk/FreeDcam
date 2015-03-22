package com.troop.freedcam.sonyapi;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.i_camera.modules.CameraFocusEvent;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.sonyapi.modules.I_CameraStatusChanged;
import com.troop.freedcam.sonyapi.modules.I_PictureCallback;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.parameters.manual.ZoomManualSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.ServerDevice;
import com.troop.freedcam.sonyapi.sonystuff.SimpleCameraEventObserver;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.sonyapi.sonystuff.SonyUtils;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraHolderSony extends AbstractCameraHolder
{
    private static String TAG =CameraHolderSony.class.getSimpleName();

    Context context;

    ServerDevice serverDevice;
    public I_CameraStatusChanged CameraStatusListner;
    I_Callbacks.AutoFocusCallback autoFocusCallback;
    public FocusHandlerSony focusHandlerSony;

    private SimpleCameraEventObserver mEventObserver;


    private SimpleCameraEventObserver.ChangeListener mEventListener = new SimpleCameraEventObserver.ChangeListenerTmpl()
    {

        @Override
        public void onShootModeChanged(String shootMode) {

        }

        @Override
        public void onCameraStatusChanged(String status)
        {
            if (CameraStatusListner != null)
                CameraStatusListner.onCameraStatusChanged(status);

        }

        @Override
        public void onTimout() {
            cameraChangedListner.onCameraError("Camera connection timed out");
        }

        @Override
        public void onApiListModified(List<String> apis) {

            synchronized (mAvailableCameraApiSet) {
                mAvailableCameraApiSet.clear();
                for (String api : apis) {
                    mAvailableCameraApiSet.add(api);
                }
                ParameterHandler.SetCameraApiSet(mAvailableCameraApiSet);
                if (!mEventObserver.getLiveviewStatus() //
                        && JsonUtils.isCameraApiAvailable("startLiveview", mAvailableCameraApiSet)) {
                    if (mLiveviewSurface != null && !mLiveviewSurface.isStarted()) {
                        startLiveview();
                    }
                }
                if (JsonUtils.isCameraApiAvailable("actZoom", mAvailableCameraApiSet)) {


                } else {

                }
            }
        }

        @Override
        public void onZoomPositionChanged(int zoomPosition)
        {
            ((ZoomManualSony)ParameterHandler.Zoom).setZoomsHasChanged(zoomPosition);
        }

        @Override
        public void onIsoChanged(String iso)
        {
            ParameterHandler.ISOManual.currentValueStringCHanged(iso);
        }

        @Override
        public void onIsoValuesChanged(String[] isovals) {
            ParameterHandler.ISOManual.BackgroundValuesChanged(isovals);
        }

        @Override
        public void onFnumberValuesChanged(String[] fnumbervals) {
            ParameterHandler.ManualFNumber.BackgroundValuesChanged(fnumbervals);
        }

        @Override
        public void onExposureCompensationMaxChanged(int epxosurecompmax) {
            ParameterHandler.ManualExposure.BackgroundMaxValueChanged(epxosurecompmax);
        }

        @Override
        public void onExposureCompensationMinChanged(int epxosurecompmin) {
            ParameterHandler.ManualExposure.BackgroundMinValueChanged(epxosurecompmin);
        }

        @Override
        public void onShutterSpeedChanged(String shutter) {
            ParameterHandler.ManualShutter.currentValueStringCHanged(shutter);
        }

        @Override
        public void onShutterSpeedValuesChanged(String[] shuttervals) {
            ParameterHandler.ManualShutter.BackgroundValuesChanged(shuttervals);
        }

        @Override
        public void onFlashChanged(String flash)
        {
            Log.d(TAG, "Fire ONFLashCHanged");
            ParameterHandler.FlashMode.BackgroundValueHasChanged(flash);
        }

        @Override
        public void onFocusLocked(boolean locked) {
            focusHandlerSony.onFocusLock(locked);
        }

        @Override
        public void onFnumberChanged(String fnumber) {
            ParameterHandler.ManualFNumber.currentValueStringCHanged(fnumber);
        }

        @Override
        public void onLiveviewStatusChanged(boolean status) {

        }

        @Override
        public void onStorageIdChanged(String storageId) {

        }




    };

    private SimpleRemoteApi mRemoteApi;

    private final Set<String> mAvailableCameraApiSet = new HashSet<String>();

    private final Set<String> mSupportedApiSet = new HashSet<String>();
    private SimpleStreamSurfaceView mLiveviewSurface;

    public ParameterHandlerSony ParameterHandler;



    public CameraHolderSony(Context context, SimpleStreamSurfaceView simpleStreamSurfaceView, I_CameraChangedListner cameraChangedListner,Handler UIHandler)
    {
        super(cameraChangedListner, UIHandler);
        this.context = context;
        this.mLiveviewSurface = simpleStreamSurfaceView;
    }


    public boolean OpenCamera(ServerDevice serverDevice)
    {
        this.serverDevice = serverDevice;
        mRemoteApi = new SimpleRemoteApi(serverDevice);
        ParameterHandler.SetRemoteApi(mRemoteApi);
        mEventObserver = new SimpleCameraEventObserver(context, mRemoteApi);
        mEventObserver.activate();


        StartPreview();
        return false;
    }

    @Override
    public void CloseCamera()
    {
        closeConnection();
    }

    @Override
    public int CameraCout() {
        return 0;
    }

    @Override
    public boolean IsRdy() {
        return false;
    }


    @Override
    public void StartPreview()
    {
        prepareOpenConnection();
    }

    @Override
    public void StopPreview()
    {
        stopLiveview();
    }



    private void startLiveview() {
        if (mLiveviewSurface == null) {
            Log.w(TAG, "startLiveview mLiveviewSurface is null.");
            return;
        }

        new Thread() {
            @Override
            public void run() {

                try {
                    JSONObject replyJson = null;
                    replyJson = mRemoteApi.startLiveview();

                    if (!SimpleRemoteApi.isErrorReply(replyJson)) {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        if (1 <= resultsObj.length()) {
                            // Obtain liveview URL from the result.
                            final String liveviewUrl = resultsObj.getString(0);
                            /*context.runOnUiThread(new Runnable()
                            {

                                @Override
                                public void run() {*/

                                    mLiveviewSurface.start(liveviewUrl, //
                                            new SimpleStreamSurfaceView.StreamErrorListener() {

                                                @Override
                                                public void onError(StreamErrorReason reason) {
                                                    stopLiveview();
                                                }
                                            });
                                /*}
                            });*/
                        }
                    }
                } catch (IOException e) {
                    Log.w(TAG, "startLiveview IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "startLiveview JSONException: " + e.getMessage());
                }
            }
        }.start();
    }

    private void stopLiveview() {
        new Thread() {
            @Override
            public void run() {
                try {
                    mRemoteApi.stopLiveview();
                } catch (IOException e) {
                    Log.w(TAG, "stopLiveview IOException: " + e.getMessage());

                }
            }
        }.start();
    }

    private void prepareOpenConnection() {
        Log.d(TAG, "prepareToOpenConection() exec");


        new Thread() {

            @Override
            public void run() {
                try {
                    // Get supported API list (Camera API)
                    JSONObject replyJsonCamera = mRemoteApi.getCameraMethodTypes();
                    JsonUtils.loadSupportedApiList(replyJsonCamera, mSupportedApiSet);
                    ParameterHandler.SetSupportedApiSet(mSupportedApiSet);

                    try {
                        // Get supported API list (AvContent API)
                        JSONObject replyJsonAvcontent = mRemoteApi.getAvcontentMethodTypes();
                        JsonUtils.loadSupportedApiList(replyJsonAvcontent, mSupportedApiSet);
                    } catch (IOException e) {
                        Log.d(TAG, "AvContent is not support.");
                    }



                    if (!JsonUtils.isApiSupported("setCameraFunction", mSupportedApiSet)) {

                        // this device does not support setCameraFunction.
                        // No need to check camera status.

                        openConnection();

                    } else {

                        // this device supports setCameraFunction.
                        // after confirmation of camera state, open connection.
                        Log.d(TAG, "this device support set camera function");

                        if (!JsonUtils.isApiSupported("getEvent", mSupportedApiSet)) {
                            Log.e(TAG, "this device is not support getEvent");
                            openConnection();
                            return;
                        }

                        // confirm current camera status
                        String cameraStatus = null;
                        JSONObject replyJson = mRemoteApi.getEvent(false, "1.0");
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        JSONObject cameraStatusObj = resultsObj.getJSONObject(1);
                        String type = cameraStatusObj.getString("type");
                        if ("cameraStatus".equals(type)) {
                            cameraStatus = cameraStatusObj.getString("cameraStatus");
                        } else {
                            throw new IOException();
                        }

                        if (SonyUtils.isShootingStatus(cameraStatus)) {
                            Log.d(TAG, "camera function is Remote Shooting.");
                            openConnection();
                        } else {
                            // set Listener
                            startOpenConnectionAfterChangeCameraState();

                            // set Camera function to Remote Shooting
                            replyJson = mRemoteApi.setCameraFunction("Remote Shooting");
                        }
                    }
                } catch (IOException e) {
                    Log.w(TAG, "prepareToStartContentsListMode: IOException: " + e.getMessage());

                } catch (JSONException e) {
                    Log.w(TAG, "prepareToStartContentsListMode: JSONException: " + e.getMessage());

                }
            }
        }.start();
    }

    private void openConnection() {

        mEventObserver.setEventChangeListener(mEventListener);
        new Thread() {

            @Override
            public void run() {
                Log.d(TAG, "openConnection(): exec.");

                try {
                    JSONObject replyJson = null;

                    // getAvailableApiList
                    replyJson = mRemoteApi.getAvailableApiList();
                    JsonUtils.loadAvailableCameraApiList(replyJson, mAvailableCameraApiSet);
                    ParameterHandler.SetCameraApiSet(mAvailableCameraApiSet);

                    // check version of the server device
                    if (JsonUtils.isCameraApiAvailable("getApplicationInfo", mAvailableCameraApiSet)) {
                        Log.d(TAG, "openConnection(): getApplicationInfo()");
                        replyJson = mRemoteApi.getApplicationInfo();

                    } else {
                        // never happens;
                        return;
                    }

                    // startRecMode if necessary.
                    if (JsonUtils.isCameraApiAvailable("startRecMode", mAvailableCameraApiSet)) {
                        Log.d(TAG, "openConnection(): startRecMode()");
                        replyJson = mRemoteApi.startRecMode();

                        // Call again.
                        replyJson = mRemoteApi.getAvailableApiList();
                        JsonUtils.loadAvailableCameraApiList(replyJson, mAvailableCameraApiSet);
                        ParameterHandler.SetCameraApiSet(mAvailableCameraApiSet);
                    }

                    // getEvent start
                    if (JsonUtils.isCameraApiAvailable("getEvent", mAvailableCameraApiSet)) {
                        Log.d(TAG, "openConnection(): EventObserver.start()");
                        mEventObserver.start();
                    }

                    // Liveview start
                    if (JsonUtils.isCameraApiAvailable("startLiveview", mAvailableCameraApiSet)) {
                        Log.d(TAG, "openConnection(): LiveviewSurface.start()");
                        startLiveview();
                    }

                    if(JsonUtils.isCameraApiAvailable("setLiveviewFrameInfo", mAvailableCameraApiSet))
                        SetLiveViewFrameInfo(true);

                    // prepare UIs
                    if (JsonUtils.isCameraApiAvailable("getAvailableShootMode", mAvailableCameraApiSet)) {
                        Log.d(TAG, "openConnection(): prepareShootModeSpinner()");

                        // Note: hide progress bar on title after this calling.
                    }

                    // prepare UIs
                    if (JsonUtils.isCameraApiAvailable("actZoom", mAvailableCameraApiSet)) {
                        Log.d(TAG, "openConnection(): prepareActZoomButtons()");

                    } else {

                    }

                    Log.d(TAG, "openConnection(): completed.");
                } catch (IOException e) {
                    Log.w(TAG, "openConnection : IOException: " + e.getMessage());

                }
            }
        }.start();

    }

    /**
     * Stop monitoring Camera events and close liveview connection.
     */
    private void closeConnection() {

        Log.d(TAG, "closeConnection(): exec.");
        if (mLiveviewSurface == null || mEventObserver == null || mAvailableCameraApiSet == null)
            return;
        // Liveview stop
        Log.d(TAG, "closeConnection(): LiveviewSurface.stop()");
        if (mLiveviewSurface != null) {
            mLiveviewSurface.stop();
            mLiveviewSurface = null;
            stopLiveview();
        }

        // getEvent stop
        Log.d(TAG, "closeConnection(): EventObserver.release()");
        mEventObserver.release();

        // stopRecMode if necessary.
        if (JsonUtils.isCameraApiAvailable("stopRecMode", mAvailableCameraApiSet)) {
            new Thread() {

                @Override
                public void run() {
                    Log.d(TAG, "closeConnection(): stopRecMode()");
                    try {
                        mRemoteApi.stopRecMode();
                    } catch (IOException e) {
                        Log.w(TAG, "closeConnection: IOException: " + e.getMessage());
                    }
                }
            }.start();
        }

        Log.d(TAG, "closeConnection(): completed.");
    }



    private void startOpenConnectionAfterChangeCameraState() {
        Log.d(TAG, "startOpenConectiontAfterChangeCameraState() exec");

        //context.runOnUiThread(new Runnable() {

          //  @Override
            //public void run() {
                mEventObserver.setEventChangeListener(mEventListener);
                mEventObserver.start();
        //}
        //});
    }


    public void TakePicture(final I_PictureCallback pictureCallback)
    {
            actTakePicture(pictureCallback);
    }

    public void startContShoot(final I_PictureCallback pictureCallback)
    {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.startContShoot();
                    JSONArray resultsObj = replyJson.getJSONArray("result");

                } catch (IOException e) {
                    Log.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Log.w(TAG, "JSONException while closing slicer");

                } finally {

                }
            }
        }.start();
    }

    public void stopContShoot(final I_PictureCallback pictureCallback)
    {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.stopContShoot();
                    JSONArray resultsObj = replyJson.getJSONArray("result");

                } catch (IOException e) {
                    Log.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Log.w(TAG, "JSONException while closing slicer");

                } finally {

                }
            }
        }.start();
    }

    private void actTakePicture(final I_PictureCallback pictureCallback) {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.actTakePicture();
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
                    String postImageUrl = null;
                    if (1 <= imageUrlsObj.length()) {
                        postImageUrl = imageUrlsObj.getString(0);
                    }
                    if (postImageUrl == null) {
                        Log.w(TAG, "takeAndFetchPicture: post image URL is null.");

                        return;
                    }
                    // Show progress indicator


                    URL url = new URL(postImageUrl);
                    pictureCallback.onPictureTaken(url);
                    //InputStream istream = new BufferedInputStream(url.openStream());


                } catch (IOException e) {
                    Log.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Log.w(TAG, "JSONException while closing slicer");

                } finally {

                }
            }
        }.start();
    }

    public void SetShootMode(final String mode)
    {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.setShootMode(mode);
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    int resultCode = resultsObj.getInt(0);
                    if (resultCode == 0) {
                        // Success, but no refresh UI at the point.
                        Log.v(TAG, "setShootMode: success.");
                    } else {
                        Log.w(TAG, "setShootMode: error: " + resultCode);

                    }
                } catch (IOException e) {
                    Log.w(TAG, "setShootMode: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "setShootMode: JSON format error.");
                }
            }
        }.start();
    }

    @Override
    public void CancelFocus()
    {

        if (mAvailableCameraApiSet.contains("cancelTouchAFPosition"))
        {
                Log.d(TAG, "Cancel Focus");
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                        JSONObject ob = mRemoteApi.setParameterToCamera("cancelTouchAFPosition", new JSONArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Cancel Focus failed");
                        }
                    }
                }).start();

        }
        else if (mAvailableCameraApiSet.contains("cancelTrackingFocus"))
        {
            Log.d(TAG, "Cancel Focus");
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject ob = mRemoteApi.setParameterToCamera("cancelTrackingFocus", new JSONArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Cancel Focus failed");
                    }
                }
            }).start();
        }
    }

    @Override
    public void SetLocation(Location loc) {

    }

    public boolean canCancelFocus()
    {
        if (mAvailableCameraApiSet.contains("cancelTouchAFPosition") || mAvailableCameraApiSet.contains("cancelTrackingFocus"))
        {
            Log.d(TAG, "Throw Focus LOCKED true");
            return true;
        }
        else
        {
            Log.d(TAG, "Throw Focus LOCKED false");
            return false;
        }
    }

    @Override
    public void StartFocus(I_Callbacks.AutoFocusCallback autoFocusCallback)
    {
        this.autoFocusCallback = autoFocusCallback;
    }

    public void SetTouchFocus(final double x, final double y)
    {
        if (mAvailableCameraApiSet.contains("setTouchAFPosition"))
            runSetTouch(x, y);
        else
            runActObjectTracking(x,y);
    }

    private void runActObjectTracking(final double x,final double y) {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.actObjectTracking(x, y);
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                } catch (IOException e) {
                    Log.w(TAG, "setShootMode: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "setShootMode: JSON format error.");
                }
            }
        }.start();
    }

    private void runSetTouch(final double x, final double y) {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.setTouchToFocus(x,y);
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    int resultCode = resultsObj.getInt(0);
                    if (resultCode == 0)
                    {
                        JSONObject ob = resultsObj.getJSONObject(1);
                        String success = ob.getString("AFResult");
                        boolean suc = false;
                        if (success.equals("true"))
                            suc = true;
                        if (autoFocusCallback != null)
                        {
                            CameraFocusEvent focusEvent = new CameraFocusEvent();
                            focusEvent.success = suc;
                            autoFocusCallback.onAutoFocus(focusEvent);
                        }

                    }
                } catch (IOException e) {
                    Log.w(TAG, "setShootMode: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "setShootMode: JSON format error.");
                }
            }
        }.start();
    }

    public void SetLiveViewFrameInfo(boolean val)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mRemoteApi.setLiveviewFrameInfo(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
