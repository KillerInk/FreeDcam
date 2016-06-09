/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.sonyremote;

import android.content.Context;
import android.location.Location;

import com.freedcam.apis.basecamera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.modules.CameraFocusEvent;
import com.freedcam.apis.basecamera.modules.I_Callbacks.AutoFocusCallback;
import com.freedcam.apis.sonyremote.modules.I_CameraStatusChanged;
import com.freedcam.apis.sonyremote.modules.I_PictureCallback;
import com.freedcam.apis.sonyremote.modules.ModuleHandlerSony;
import com.freedcam.apis.sonyremote.modules.PictureModuleSony;
import com.freedcam.apis.sonyremote.parameters.manual.ZoomManualSony;
import com.freedcam.apis.sonyremote.sonystuff.JsonUtils;
import com.freedcam.apis.sonyremote.sonystuff.ServerDevice;
import com.freedcam.apis.sonyremote.sonystuff.SimpleCameraEventObserver;
import com.freedcam.apis.sonyremote.sonystuff.SimpleCameraEventObserver.ChangeListener;
import com.freedcam.apis.sonyremote.sonystuff.SimpleCameraEventObserver.ChangeListenerTmpl;
import com.freedcam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import com.freedcam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView.StreamErrorListener;
import com.freedcam.apis.sonyremote.sonystuff.SonyUtils;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;

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
public class CameraHolder extends AbstractCameraHolder
{
    private static String TAG =CameraHolder.class.getSimpleName();

    Context context;

    ServerDevice serverDevice;
    public I_CameraStatusChanged CameraStatusListner;
    AutoFocusCallback autoFocusCallback;
    public FocusHandler focusHandler;

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


    private ChangeListener mEventListener = new ChangeListenerTmpl()
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
            cameraStatus = status;
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
            ParameterHandler.ManualIso.ThrowCurrentValueStringCHanged(iso);
        }

        @Override
        public void onIsoValuesChanged(String[] isovals) {
            ParameterHandler.ManualIso.ThrowBackgroundValuesChanged(isovals);
        }

        @Override
        public void onFnumberValuesChanged(String[] fnumbervals) {
            ParameterHandler.ManualFNumber.ThrowBackgroundValuesChanged(fnumbervals);
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
            ParameterHandler.ManualShutter.ThrowBackgroundValuesChanged(shuttervals);
        }

        @Override
        public void onFlashChanged(String flash)
        {
            Logger.d(TAG, "Fire ONFLashCHanged");
            ParameterHandler.FlashMode.BackgroundValueHasChanged(flash);
        }

        @Override
        public void onFocusLocked(boolean locked) {
            focusHandler.onFocusLock(locked);
        }

        @Override
        public void onWhiteBalanceValueChanged(String wb)
        {
            ParameterHandler.WhiteBalanceMode.BackgroundValueHasChanged(wb);
            if (ParameterHandler.WhiteBalanceMode.GetValue().equals("Color Temperature") && ParameterHandler.CCT != null)
                ParameterHandler.CCT.ThrowBackgroundIsSupportedChanged(true);
            else
                ParameterHandler.CCT.ThrowBackgroundIsSupportedChanged(false);
        }

        @Override
        public void onImagesRecieved(final String[] url)
        {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    for (String s : url)
                    {
                        if (moduleHandlerSony.GetCurrentModule() instanceof PictureModuleSony)
                        {
                            PictureModuleSony pictureModuleSony = (PictureModuleSony)moduleHandlerSony.GetCurrentModule();


                            try {
                                pictureModuleSony.onPictureTaken(new URL(s));
                            }catch (MalformedURLException e) {
                                Logger.exception(e);
                            }

                        }


                    }
                }});
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
        public void onZoomSettingValueCHanged(String value) {
            ParameterHandler.ZoomSetting.BackgroundValueHasChanged(value);
        }

        @Override
        public void onZoomSettingsValuesCHanged(String[] values) {
            ParameterHandler.ZoomSetting.BackgroundValuesHasChanged(values);
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

    private final Set<String> mAvailableCameraApiSet = new HashSet<>();

    private final Set<String> mSupportedApiSet = new HashSet<>();
    private SimpleStreamSurfaceView mLiveviewSurface;

    public com.freedcam.apis.sonyremote.parameters.ParameterHandler ParameterHandler;



    public CameraHolder(Context context, SimpleStreamSurfaceView simpleStreamSurfaceView, I_CameraChangedListner cameraChangedListner, AppSettingsManager appSettingsManager)
    {
        super(cameraChangedListner,appSettingsManager);
        this.context = context;
        mLiveviewSurface = simpleStreamSurfaceView;
    }


    public void OpenCamera(ServerDevice serverDevice)
    {
        this.serverDevice = serverDevice;
        mRemoteApi = new SimpleRemoteApi(serverDevice);
        ParameterHandler.SetRemoteApi(mRemoteApi);
        mEventObserver = new SimpleCameraEventObserver(context, mRemoteApi);
        mEventObserver.activate();


        StartPreview();
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
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = null;
                    replyJson = mRemoteApi.startLiveview();

                    if (!SimpleRemoteApi.isErrorReply(replyJson)) {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        if (1 <= resultsObj.length()) {
                            // Obtain liveview URL from the result.
                            String liveviewUrl = resultsObj.getString(0);
                            Logger.d(TAG,"startLiveview");
                            mLiveviewSurface.start(liveviewUrl, //
                                    new StreamErrorListener() {

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
        });
    }

    private void stopLiveview()
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    isPreviewRunning = false;
                    mRemoteApi.stopLiveview();

                } catch (IOException e) {
                    Logger.w(TAG, "stopLiveview IOException: " + e.getMessage());

                }
            }
        });
    }
// guide"results" -> "[["getServiceProtocols",[],["string","string*"],"1.0"],["getMethodTypes",["string"],["string","string*","string*","string"],"1.0"],["getVersions",[],["string*"],"1.0"]]"
    //{"results":[["setCurrentTime",["{\"dateTime\":\"string\", \"timeZoneOffsetMinute\":\"int\", \"dstOffsetMinute\":\"int\"}"],[],"1.0"],["getMethodTypes",["string"],["string","string*","string*","string"],"1.0"],["getVersions",[],["string*"],"1.0"]],"id":1}
    private void prepareOpenConnection() {
        Logger.d(TAG, "prepareToOpenConection() exec");
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get supported API list (Camera API)
                    Logger.d(TAG, "get event longpool false");
                    /*JSONObject replyJsonsystemMeth = mRemoteApi.getMethodTypes(SimpleRemoteApi.SYSTEM);
                    JSONObject replyJsonguideMeth = mRemoteApi.getMethodTypes(SimpleRemoteApi.GUIDE);*/
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
                            replyJson = mRemoteApi.setCameraFunction();
                        }
                    }
                } catch (IOException e) {
                    Logger.w(TAG, "prepareToStartContentsListMode: IOException: " + e.getMessage());

                } catch (JSONException e) {
                    Logger.w(TAG, "prepareToStartContentsListMode: JSONException: " + e.getMessage());

                }
            }
        });

    }

    private void openConnection() {

        mEventObserver.setEventChangeListener(mEventListener);
        FreeDPool.Execute(new Runnable() {
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
        });
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
        if (JsonUtils.isCameraApiAvailable("stopRecMode", mAvailableCameraApiSet))
        {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    Logger.d(TAG, "closeConnection(): stopRecMode()");
                    try {
                        mRemoteApi.stopRecMode();
                    } catch (IOException e) {
                        Logger.w(TAG, "closeConnection: IOException: " + e.getMessage());
                    }
                }
            });
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


    public void TakePicture(I_PictureCallback pictureCallback)
    {
        actTakePicture(pictureCallback);
    }

    public void startContShoot(I_PictureCallback pictureCallback)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.startContShoot();
                    JSONArray resultsObj = replyJson.getJSONArray("result");

                } catch (IOException e) {
                    Logger.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Logger.w(TAG, "JSONException while closing slicer");

                }
            }
        });
    }

    public void stopContShoot(I_PictureCallback pictureCallback)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.stopContShoot();
                    JSONArray resultsObj = replyJson.getJSONArray("result");

                } catch (IOException e) {
                    Logger.w(TAG, "IOException while closing slicer: " + e.getMessage());

                } catch (JSONException e) {
                    Logger.w(TAG, "JSONException while closing slicer");

                }
            }
        });
    }

    private void actTakePicture(final I_PictureCallback pictureCallback)
    {
        FreeDPool.Execute(new Runnable() {
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
        });
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
        FreeDPool.Execute(new Runnable() {
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
        });

    }

    public void StartRecording()
    {
        FreeDPool.Execute(new Runnable() {
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
        });
    }

    public void StopRecording()
    {
        FreeDPool.Execute(new Runnable() {
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
        });
    }

    @Override
    public void CancelFocus()
    {
        if (mAvailableCameraApiSet.contains("cancelTouchAFPosition"))
        {
            Logger.d(TAG, "Cancel Focus");
            FreeDPool.Execute(new Runnable() {
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
            });

        }
        else if (mAvailableCameraApiSet.contains("cancelTrackingFocus"))
        {
            Logger.d(TAG, "Cancel Focus");
            FreeDPool.Execute(new Runnable() {
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
            });
        }
    }

    @Override
    public void SetLocation(Location loc) {

    }

    @Override
    public void StartFocus() {
        Focus.StartFocus();
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
    public void StartFocus(AutoFocusCallback autoFocusCallback)
    {
        this.autoFocusCallback = autoFocusCallback;
    }

    public void SetTouchFocus(double x, double y)
    {
        if (mAvailableCameraApiSet.contains("setTouchAFPosition"))
            runSetTouch(x, y);
        else
            runActObjectTracking(x,y);
    }

    private void runActObjectTracking(final double x,final double y)
    {
        FreeDPool.Execute(new Runnable() {
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
        });
    }

    private void runSetTouch(final double x, final double y) {
        FreeDPool.Execute(new Runnable() {
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
        });
    }

    public void SetLiveViewFrameInfo(boolean val)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mRemoteApi.setLiveviewFrameInfo();
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
        });
    }

}
