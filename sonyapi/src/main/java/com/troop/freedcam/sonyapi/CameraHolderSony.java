package com.troop.freedcam.sonyapi;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.modules.CameraFocusEvent;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.sonyapi.modules.I_CameraStatusChanged;
import com.troop.freedcam.sonyapi.modules.I_PictureCallback;
import com.troop.freedcam.sonyapi.modules.ModuleHandlerSony;
import com.troop.freedcam.sonyapi.modules.PictureModuleSony;
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
import java.net.MalformedURLException;
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
    public ModuleHandlerSony moduleHandlerSony;

    private String cameraStatus = "IDLE";

    public I_CameraShotMode cameraShotMode;
    JSONObject FullUiSetup;

    public interface I_CameraShotMode
    {
        void onShootModeChanged(String mode);
        void onShootModeValuesChanged(String[] modes);
    }

    public String GetCameraStatus()
    { return cameraStatus;}


    private SimpleCameraEventObserver.ChangeListener mEventListener = new SimpleCameraEventObserver.ChangeListenerTmpl()
    {

        @Override
        public void onShootModeChanged(String shootMode) {
            if(cameraShotMode != null )
                cameraShotMode.onShootModeChanged(shootMode);
        }

        @Override
        public void onCameraStatusChanged(String status)
        {
            //if (cameraStatus.equals(status))
            //    return;
            CameraHolderSony.this.cameraStatus = status;
            Logger.d(TAG, "Camerastatus:" + cameraStatus);
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
            ParameterHandler.ISOManual.ThrowCurrentValueStringCHanged(iso);
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
            //ParameterHandler.ManualExposure.BackgroundMaxValueChanged(epxosurecompmax);
        }

        @Override
        public void onExposureCompensationMinChanged(int epxosurecompmin) {
            //ParameterHandler.ManualExposure.BackgroundMinValueChanged(epxosurecompmin);
        }

        @Override
        public void onExposureCompensationChanged(int epxosurecomp) {
            ParameterHandler.ManualExposure.ThrowCurrentValueChanged(epxosurecomp);
        }

        @Override
        public void onShutterSpeedChanged(String shutter) {
            ParameterHandler.ManualShutter.ThrowCurrentValueStringCHanged(shutter);
        }

        @Override
        public void onShutterSpeedValuesChanged(String[] shuttervals) {
            ParameterHandler.ManualShutter.BackgroundValuesChanged(shuttervals);
        }

        @Override
        public void onFlashChanged(String flash)
        {
            Logger.d(TAG, "Fire ONFLashCHanged");
            ParameterHandler.FlashMode.BackgroundValueHasChanged(flash);
        }

        @Override
        public void onFocusLocked(boolean locked) {
            focusHandlerSony.onFocusLock(locked);
        }

        @Override
        public void onWhiteBalanceValueChanged(String wb)
        {
            ParameterHandler.WhiteBalanceMode.BackgroundValueHasChanged(wb);
            if (ParameterHandler.WhiteBalanceMode.GetValue().equals("Color Temperature") && ParameterHandler.CCT != null)
                ParameterHandler.CCT.BackgroundIsSupportedChanged(true);
            else
                ParameterHandler.CCT.BackgroundIsSupportedChanged(false);
        }

        @Override
        public void onWbColorTemperatureChanged(int colortemp) {

        }

        @Override
        public void onPostViewImageRevieved(String url) {

        }

        @Override
        public void onImageRecieved(String url) {

        }

        @Override
        public void onImagesRecieved(final String[] url)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (final String s : url)
                    {
                        if (moduleHandlerSony.GetCurrentModule() instanceof PictureModuleSony)
                        {
                            final PictureModuleSony pictureModuleSony = (PictureModuleSony)moduleHandlerSony.GetCurrentModule();


                            try {
                                pictureModuleSony.onPictureTaken(new URL(s));
                            }catch (MalformedURLException e) {
                                Logger.exception(e);
                            }

                        }


                    }
                }}).start();
        }

        @Override
        public void onProgramShiftValueChanged(int shift) {

        }

        @Override
        public void onProgramShiftValuesChanged(String[] shift) {

        }

        @Override
        public void onFnumberChanged(String fnumber) {
            ParameterHandler.ManualFNumber.ThrowCurrentValueStringCHanged(fnumber);
        }

        @Override
        public void onLiveviewStatusChanged(boolean status) {

        }

        @Override
        public void onStorageIdChanged(String storageId) {

        }

        @Override
        public void onExposureModesChanged(String[] expomode)
        {
            ParameterHandler.ExposureMode.BackgroundValuesHasChanged(expomode);
        }

        @Override
        public void onImageFormatChanged(String imagesize) {
            ParameterHandler.PictureFormat.BackgroundValueHasChanged(imagesize);
        }

        @Override
        public void onImageFormatsChanged(String[] imagesize) {
            ParameterHandler.PictureFormat.BackgroundValuesHasChanged(imagesize);
        }

        @Override
        public void onImageSizeChanged(String imagesize) {
            ParameterHandler.PictureSize.BackgroundValueHasChanged(imagesize);
        }

        @Override
        public void onContshotModeChanged(String imagesize) {
            ParameterHandler.ContShootMode.BackgroundValueHasChanged(imagesize);
        }

        @Override
        public void onContshotModesChanged(String[] imagesize) {
            ParameterHandler.ContShootMode.BackgroundValuesHasChanged(imagesize);
        }

        @Override
        public void onFocusModeChanged(String imagesize) {
            ParameterHandler.FocusMode.BackgroundValueHasChanged(imagesize);
        }

        @Override
        public void onFocusModesChanged(String[] imagesize) {
            ParameterHandler.FocusMode.BackgroundValuesHasChanged(imagesize);
        }

        @Override
        public void onPostviewModeChanged(String imagesize) {
            ParameterHandler.PostViewSize.BackgroundValueHasChanged(imagesize);
        }

        @Override
        public void onPostviewModesChanged(String[] imagesize) {
            ParameterHandler.PostViewSize.BackgroundValuesHasChanged(imagesize);
        }

        @Override
        public void onTrackingFocusModeChanged(String imagesize) {
            ParameterHandler.ObjectTracking.BackgroundValueHasChanged(imagesize);
        }

        @Override
        public void onTrackingFocusModesChanged(String[] imagesize) {
            ParameterHandler.ObjectTracking.BackgroundValuesHasChanged(imagesize);
        }

        @Override
        public void onExposureModeChanged(String expomode) {
            if (!ParameterHandler.ExposureMode.GetValue().equals(expomode))
                ParameterHandler.ExposureMode.BackgroundValueHasChanged(expomode);
            if (expomode.equals("Intelligent Auto")|| expomode.equals("Superior Auto"))
                ParameterHandler.WhiteBalanceMode.BackgroundIsSupportedChanged(false);
            else
                ParameterHandler.WhiteBalanceMode.BackgroundIsSupportedChanged(true);
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
            Logger.d(TAG, "startLiveview mLiveviewSurface is null.");
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
                            Logger.d(TAG,"startLiveview");
                            mLiveviewSurface.start(liveviewUrl, //
                                    new SimpleStreamSurfaceView.StreamErrorListener() {

                                        @Override
                                        public void onError(StreamErrorReason reason)
                                        {
                                            Logger.e(TAG, "Error StartingLiveView");
                                            stopLiveview();
                                        }
                                    });
                            isPreviewRunning = true;
                        }
                    }
                } catch (IOException e) {
                    Logger.w(TAG, "startLiveview IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Logger.w(TAG, "startLiveview JSONException: " + e.getMessage());
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
                    isPreviewRunning = false;
                } catch (IOException e) {
                    Logger.w(TAG, "stopLiveview IOException: " + e.getMessage());

                }
            }
        }.start();
    }

    private void prepareOpenConnection() {
        Logger.d(TAG, "prepareToOpenConection() exec");


        new Thread() {

            @Override
            public void run() {
                try {
                    // Get supported API list (Camera API)
                    Logger.d(TAG, "get event longpool false");
                    JSONObject replyJson = mRemoteApi.getEvent(false, "1.0");
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    JsonUtils.loadSupportedApiListFromEvent(resultsObj.getJSONObject(0), mAvailableCameraApiSet);
                    ParameterHandler.SetCameraApiSet(mAvailableCameraApiSet);

                    if (!JsonUtils.isApiSupported("setCameraFunction", mAvailableCameraApiSet)) {

                        // this device does not support setCameraFunction.
                        // No need to check camera status.
                        Logger.d(TAG, "prepareOpenConnection->openconnection, no setCameraFunciton");
                        openConnection();

                    } else {

                        // this device supports setCameraFunction.
                        // after confirmation of camera state, open connection.
                        Logger.d(TAG, "this device support set camera function");

                        if (!JsonUtils.isApiSupported("getEvent", mAvailableCameraApiSet)) {
                            Logger.e(TAG, "this device is not support getEvent");
                            openConnection();
                            return;
                        }

                        // confirm current camera status
                        String cameraStatus = null;

                        FullUiSetup = replyJson;
                        JSONObject cameraStatusObj = resultsObj.getJSONObject(1);
                        String type = cameraStatusObj.getString("type");
                        if ("cameraStatus".equals(type)) {
                            cameraStatus = cameraStatusObj.getString("cameraStatus");
                            if (cameraChangedListner != null) {
                                Logger.d(TAG,"prepareOpenConnection camerastatusChanged" + cameraStatus );
                                cameraChangedListner.onCameraStatusChanged(cameraStatus);
                            }
                        } else {
                            throw new IOException();
                        }

                        if (SonyUtils.isShootingStatus(cameraStatus)) {
                            Logger.d(TAG, "camera function is Remote Shooting.");
                            openConnection();
                        } else {
                            // set Listener
                            Logger.d(TAG,"Change function to remote shooting");
                            startOpenConnectionAfterChangeCameraState();

                            // set Camera function to Remote Shooting
                            replyJson = mRemoteApi.setCameraFunction("Remote Shooting");
                        }
                    }
                } catch (IOException e) {
                    Logger.w(TAG, "prepareToStartContentsListMode: IOException: " + e.getMessage());

                } catch (JSONException e) {
                    Logger.w(TAG, "prepareToStartContentsListMode: JSONException: " + e.getMessage());

                }
            }
        }.start();
    }

    private void openConnection() {

        mEventObserver.setEventChangeListener(mEventListener);
        new Thread() {

            @Override
            public void run() {
                Logger.d(TAG, "openConnection(): exec.");

                try {
                    JSONObject replyJson = null;
                    // startRecMode if necessary.
                    Logger.d(TAG, "openConnection(): startRecMode");
                    if (JsonUtils.isCameraApiAvailable("startRecMode", mAvailableCameraApiSet)) {
                        Logger.d(TAG, "openConnection(): startRecMode()");
                        replyJson = mRemoteApi.startRecMode();

                        // Call again.
                        replyJson = mRemoteApi.getAvailableApiList();
                        JsonUtils.loadAvailableCameraApiList(replyJson, mAvailableCameraApiSet);
                    }

                    // getEvent start
                    Logger.d(TAG, "openConnection(): getEvent");
                    if (JsonUtils.isCameraApiAvailable("getEvent", mAvailableCameraApiSet)) {
                        Logger.d(TAG, "openConnection(): EventObserver.start()");
                        mEventObserver.start();

                    }

                    // Liveview start
                    Logger.d(TAG, "openConnection(): startLiveView");
                    if (JsonUtils.isCameraApiAvailable("startLiveview", mAvailableCameraApiSet) && cameraStatus.equals("IDLE")) {
                        Logger.d(TAG, "openConnection(): LiveviewSurface.start()");
                        startLiveview();
                    }
                    Logger.d(TAG, "openConnection(): setLiveViewFrameInfo");
                    if((serverDevice.getFriendlyName().contains("ILCE-QX1")
                            || serverDevice.getFriendlyName().contains("ILCE-QX30"))
                            && JsonUtils.isApiSupported("setLiveviewFrameInfo", mAvailableCameraApiSet)
                            && cameraStatus.equals("IDLE"))
                    {
                        SetLiveViewFrameInfo(true);
                    }

                    Logger.d(TAG, "openConnection(): completed.");
                } catch (IOException e) {
                    Logger.w(TAG, "openConnection : IOException: " + e.getMessage());

                }
            }
        }.start();

    }

    /**
     * Stop monitoring Camera events and close liveview connection.
     */
    private void closeConnection() {


        Logger.d(TAG, "closeConnection(): exec.");
        if (mLiveviewSurface == null || mEventObserver == null || mAvailableCameraApiSet == null)
            return;
        // Liveview stop
        Logger.d(TAG, "closeConnection(): LiveviewSurface.stop()");
        if (mLiveviewSurface != null)
        {
            if((serverDevice.getFriendlyName().contains("ILCE-QX1") || serverDevice.getFriendlyName().contains("ILCE-QX30")) && JsonUtils.isApiSupported("setLiveviewFrameInfo", mAvailableCameraApiSet))
            {
                SetLiveViewFrameInfo(false);
            }
            mLiveviewSurface.stop();
            mLiveviewSurface = null;
            stopLiveview();
        }

        // getEvent stop
        Logger.d(TAG, "closeConnection(): EventObserver.release()");
        mEventObserver.release();

        // stopRecMode if necessary.
        if (JsonUtils.isCameraApiAvailable("stopRecMode", mAvailableCameraApiSet)) {
            new Thread() {

                @Override
                public void run() {
                    Logger.d(TAG, "closeConnection(): stopRecMode()");
                    try {
                        mRemoteApi.stopRecMode();
                    } catch (IOException e) {
                        Logger.w(TAG, "closeConnection: IOException: " + e.getMessage());
                    }
                }
            }.start();
        }

        Logger.d(TAG, "closeConnection(): completed.");
    }



    private void startOpenConnectionAfterChangeCameraState() {
        Logger.d(TAG, "startOpenConectiontAfterChangeCameraState() exec");

        //context.runOnUiThread(new Runnable() {

        //  @Override
        //public void run() {
        mEventObserver.setEventChangeListener(mEventListener);
        mEventObserver.start();
        try {
            mEventObserver.processEvents(FullUiSetup);
        } catch (JSONException e) {
            Logger.exception(e);
        }
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
                    Logger.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Logger.w(TAG, "JSONException while closing slicer");

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
                    Logger.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Logger.w(TAG, "JSONException while closing slicer");

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
                    Logger.d(TAG, "####################### ACT TAKE PICTURE");
                    JSONObject replyJson = mRemoteApi.actTakePicture();
                    Logger.d(TAG, "####################### ACT TAKE PICTURE REPLY RECIEVED");
                    Logger.d(TAG, replyJson.toString());
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    Logger.d(TAG, "####################### ACT TAKE PICTURE PARSED RESULT");
                    JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
                    String postImageUrl = null;
                    if (1 <= imageUrlsObj.length()) {
                        postImageUrl = imageUrlsObj.getString(0);
                    }
                    if (postImageUrl == null) {
                        Logger.w(TAG, "takeAndFetchPicture: post image URL is null.");

                        return;
                    }
                    // Show progress indicator


                    URL url = new URL(postImageUrl);
                    pictureCallback.onPictureTaken(url);
                    //InputStream istream = new BufferedInputStream(url.openStream());


                } catch (IOException e)
                {
                    Logger.exception(e);
                    Logger.w(TAG, "IOException while closing slicer: " + e.getMessage());
                    awaitTakePicture(pictureCallback);
                } catch (JSONException e) {
                    Logger.w(TAG, "JSONException while closing slicer");
                    //awaitTakePicture(pictureCallback);
                }
            }
        }.start();
    }


    private void awaitTakePicture(I_PictureCallback pictureCallback)
    {
        Logger.d(TAG, "Camerastatus:" + cameraStatus);
        if (cameraStatus.equals("StillCapturing")) {
            try {
                Logger.d(TAG, "####################### AWAIT TAKE");
                JSONObject replyJson = mRemoteApi.awaitTakePicture();
                Logger.d(TAG, "####################### AWAIT TAKE PICTURE RECIEVED RESULT");
                JSONArray resultsObj = replyJson.getJSONArray("result");
                Logger.d(TAG, "####################### AWAIT TAKE PICTURE PARSED RESULT");
                if (!resultsObj.isNull(0))
                {
                    Logger.d(TAG, resultsObj.toString());
                    JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
                    URL url = new URL(imageUrlsObj.getString(0));
                    pictureCallback.onPictureTaken(url);
                }
            } catch (IOException e1)
            {
                awaitTakePicture(pictureCallback);
                e1.printStackTrace();
            } catch (JSONException e1) {
                //awaitTakePicture(pictureCallback);
                e1.printStackTrace();
            }
        }
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
                        Logger.v(TAG, "setShootMode: success.");
                    } else {
                        Logger.w(TAG, "setShootMode: error: " + resultCode);

                    }
                } catch (IOException e) {
                    Logger.w(TAG, "setShootMode: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Logger.w(TAG, "setShootMode: JSON format error.");
                }
                catch (NullPointerException e) {
                    Logger.w(TAG, "remote api null");
                }
            }
        }.start();
    }

    public void StartRecording()
    {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.startMovieRec();
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    int resultCode = resultsObj.getInt(0);
                    if (resultCode == 0) {
                        // Success, but no refresh UI at the point.
                        Logger.v(TAG, "startRecording: success.");
                    } else {
                        Logger.w(TAG, "startRecording: error: " + resultCode);

                    }
                } catch (IOException e) {
                    Logger.w(TAG, "startRecording: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Logger.w(TAG, "startRecording: JSON format error.");
                }
            }
        }.start();
    }

    public void StopRecording()
    {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.stopMovieRec();
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    int resultCode = resultsObj.getInt(0);
                    if (resultCode == 0) {
                        // Success, but no refresh UI at the point.
                        Logger.v(TAG, "StopRecording: success.");
                    } else {
                        Logger.w(TAG, "StopRecording: error: " + resultCode);

                    }
                } catch (IOException e) {
                    Logger.w(TAG, "StopRecording: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Logger.w(TAG, "StopRecording: JSON format error.");
                }
            }
        }.start();
    }

    @Override
    public void CancelFocus()
    {

        if (mAvailableCameraApiSet.contains("cancelTouchAFPosition"))
        {
            Logger.d(TAG, "Cancel Focus");
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject ob = mRemoteApi.setParameterToCamera("cancelTouchAFPosition", new JSONArray());
                    } catch (IOException e) {
                        Logger.exception(e);
                        Logger.d(TAG, "Cancel Focus failed");
                    }
                }
            }).start();

        }
        else if (mAvailableCameraApiSet.contains("cancelTrackingFocus"))
        {
            Logger.d(TAG, "Cancel Focus");
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject ob = mRemoteApi.setParameterToCamera("cancelTrackingFocus", new JSONArray());
                    } catch (IOException e) {
                        Logger.exception(e);
                        Logger.d(TAG, "Cancel Focus failed");
                    }
                }
            }).start();
        }
    }

    @Override
    public void SetLocation(Location loc) {

    }

    @Override
    public void SetPreviewCallback(I_Callbacks.PreviewCallback previewCallback)
    {
        if (mLiveviewSurface != null)
            mLiveviewSurface.SetOnPreviewFrame(previewCallback);
    }

    public boolean canCancelFocus()
    {
        if (mAvailableCameraApiSet.contains("cancelTouchAFPosition") || mAvailableCameraApiSet.contains("cancelTrackingFocus"))
        {
            Logger.d(TAG, "Throw Focus LOCKED true");
            return true;
        }
        else
        {
            Logger.d(TAG, "Throw Focus LOCKED false");
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
                    Logger.w(TAG, "setShootMode: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Logger.w(TAG, "setShootMode: JSON format error.");
                }
                catch (NullPointerException e) {
                    Logger.w(TAG, "remote api is null");
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
                    Logger.w(TAG, "setShootMode: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Logger.w(TAG, "setShootMode: JSON format error.");
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
                    Logger.exception(e);
                }
            }
        }).start();
    }

}
