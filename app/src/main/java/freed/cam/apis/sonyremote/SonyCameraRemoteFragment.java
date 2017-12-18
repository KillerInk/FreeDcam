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

package freed.cam.apis.sonyremote;

import android.os.Bundle;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.cam.apis.sonyremote.sonystuff.ServerDevice;
import freed.cam.apis.sonyremote.sonystuff.SimpleCameraEventObserver;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import freed.cam.apis.sonyremote.sonystuff.SonyUtils;
import freed.cam.apis.sonyremote.sonystuff.WifiHandler;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 06.06.2015.
 */
public class SonyCameraRemoteFragment extends CameraFragmentAbstract implements SurfaceHolder.Callback, WifiHandler.WifiEvents, CameraHolderSony.CameraRemoteEvents
{
    private final String TAG = SonyCameraRemoteFragment.class.getSimpleName();
    private SimpleStreamSurfaceView surfaceView;

    private ServerDevice serverDevice;

    private TextView textView_wifi;
    private final int STATE_IDEL = 0;
    private final int STATE_DEVICE_CONNECTED = 3;
    private int STATE = STATE_IDEL;
    WifiHandler wifiHandler;
    private SimpleRemoteApi mRemoteApi;
    private SimpleCameraEventObserver mEventObserver;
    private final Set<String> mAvailableCameraApiSet = new HashSet<>();

    public static SonyCameraRemoteFragment getInstance(HandlerThread mBackgroundThread, Object cameraLock)
    {
        SonyCameraRemoteFragment fragment = new SonyCameraRemoteFragment();
        fragment.init(mBackgroundThread, cameraLock);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(layout.cameraholdersony, container, false);
        surfaceView = (SimpleStreamSurfaceView) view.findViewById(id.view);
        surfaceView.SetRenderScriptHandlerAndInterface(renderScriptManager, (ActivityInterface) getActivity());

        textView_wifi =(TextView) view.findViewById(id.textView_wificonnect);

        wifiHandler = new WifiHandler(getActivityInterface());
        parametersHandler = new ParameterHandler(this, surfaceView, getContext());

        moduleHandler = new ModuleHandlerSony(this);
        Focus = new FocusHandler(this);
        cameraHolder = new CameraHolderSony(getContext(), surfaceView, this);
        moduleHandler.initModules();

        //this.onCameraOpenFinish("");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiHandler.onResume();
        startCamera();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.stopCamera");
        wifiHandler.onPause();
        stopCamera();

    }


    private void setTextFromWifi(final String txt)
    {
        textView_wifi.post(new Runnable() {
            @Override
            public void run() {
                textView_wifi.setText(txt);
            }
        });
    }

    private void hideTextViewWifi(final boolean hide)
    {
        textView_wifi.post(new Runnable() {
            @Override
            public void run() {
                if (hide)
                    textView_wifi.setVisibility(View.GONE);
                else
                    textView_wifi.setVisibility(View.VISIBLE);
            }
        });
    }

    public Set<String> getAvailableApiSet(){return mAvailableCameraApiSet;}


    @Override
    public String CameraApiName() {

        return SettingsManager.API_SONY;
    }

    @Override
    public void startCamera()
    {
        if (serverDevice == null)
        {
            wifiHandler.setEventsListner(this);
            wifiHandler.StartLookUp();
            return;
        }
        Log.d(TAG,"startCamera");

        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock)
                {
                    startSonyCamera();
                    Log.d(TAG, "onCameraOpen State:" + STATE);
                    STATE = STATE_DEVICE_CONNECTED;
                }
            }
        });
    }

    private void startSonyCamera()
    {
        Log.d(TAG, "########################### start Camera ##########################");
        if (mRemoteApi == null)
        {
            mRemoteApi = new SimpleRemoteApi(serverDevice);
            ((ParameterHandler)parametersHandler).SetRemoteApi(mRemoteApi);

        }
        mEventObserver = new SimpleCameraEventObserver(getActivityInterface().getContext(), mRemoteApi);


        ((CameraHolderSony)cameraHolder).setRemoteApi(mRemoteApi);
        ((CameraHolderSony)cameraHolder).cameraRemoteEventsListner =this;

        try {
            JSONObject replyJson;
            // Get supported API list (Camera API)
            Log.d(TAG, "get event longpool false");
            replyJson =mRemoteApi.getAvailableApiList();
            JsonUtils.loadAvailableCameraApiList(replyJson, mAvailableCameraApiSet);
           /* replyJson = mRemoteApi.getEvent(false, "1.0");
            JSONArray resultsObj = replyJson.getJSONArray("result");
            JsonUtils.loadSupportedApiListFromEvent(resultsObj.getJSONObject(0), mAvailableCameraApiSet);*/
            ((ParameterHandler) parametersHandler).SetCameraApiSet(mAvailableCameraApiSet);


            if (JsonUtils.isApiSupported("startContShooting",mAvailableCameraApiSet))
                moduleHandler.changeCaptureState(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_working);
            else if (JsonUtils.isApiSupported("stopContShooting",mAvailableCameraApiSet))
                moduleHandler.changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_start);
            else if (JsonUtils.isApiSupported("actTakePicture",mAvailableCameraApiSet))
                moduleHandler.changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_stop);
            else if (JsonUtils.isApiSupported("awaitTakePicture",mAvailableCameraApiSet))
                moduleHandler.changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_start);

            if (!JsonUtils.isApiSupported("setCameraFunction", mAvailableCameraApiSet) &&
                    !(JsonUtils.isApiSupported("startContShooting",mAvailableCameraApiSet) && JsonUtils.isApiSupported("stopContShooting",mAvailableCameraApiSet))) {
                // this device does not support setCameraFunction.
                // No need to check camera status.
                Log.d(TAG, "prepareOpenConnection->openconnection, no setCameraFunciton");
                openConnection();
            }
            else
            {
                // this device supports setCameraFunction.
                // after confirmation of camera state, open connection.
                Log.d(TAG, "this device support set camera function");

                if (!JsonUtils.isApiSupported("getEvent", mAvailableCameraApiSet)) {
                    Log.e(TAG, "this device is not support getEvent");
                    openConnection();
                    return;
                }

                // confirm current camera status
                String cameraStatus = null;
                replyJson = mRemoteApi.getEvent(false,"1.0");
                JSONArray resultsObj = replyJson.getJSONArray("result");
                JSONObject cameraStatusObj = resultsObj.getJSONObject(1);
                String type = cameraStatusObj.getString("type");
                if ("cameraStatus".equals(type)) {
                    cameraStatus = cameraStatusObj.getString("cameraStatus");

                    Log.d(TAG,"prepareOpenConnection camerastatusChanged" + cameraStatus );
                    onCameraStatusChanged(cameraStatus);
                } else {
                    throw new IOException();
                }




                if (SonyUtils.isShootingStatus(cameraStatus)) {
                    Log.d(TAG, "camera function is Remote Shooting.");

                    openConnection();

                } else {
                    // set Listener
                    startOpenConnectionAfterChangeCameraState();
                    Log.d(TAG,"Change function to remote shooting");
                    // set Camera function to Remote Shooting
                    replyJson = mRemoteApi.setCameraFunction();
                    openConnection();
                }
                onCameraOpenFinish("");
            }
        } catch (IOException e) {
            Log.w(TAG, "prepareToStartContentsListMode: IOException: " + e.getMessage());

        } catch (JSONException e) {
            Log.w(TAG, "prepareToStartContentsListMode: JSONException: " + e.getMessage());

        }

        cameraHolder.OpenCamera(0);
    }

    private void startOpenConnectionAfterChangeCameraState() {
        Log.d(TAG, "startOpenConectiontAfterChangeCameraState() exec");

        mEventObserver.setCameraStateChangedListener(new SimpleCameraEventObserver.CameraStatus() {

                    @Override
                    public void onCameraStatusChanged(String status) {
                        Log.d(TAG, "onCameraStatusChanged:" + status);
                        if ("IDLE".equals(status)) {
                            openConnection();
                        }
                    }

                });

        mEventObserver.start();
    }

    private void openConnection()
    {
        Log.d(TAG, "########################### openConnection ##########################");
        Log.d(TAG, "openConnection(): exec.");



        try {
            JSONObject replyJson = null;

            //find api version for requests
            replyJson = mRemoteApi.getVersions();
            JSONArray array = replyJson.getJSONArray("result");
            array = array.getJSONArray(0);
            String eventid = array.getString(array.length()-1);
            mEventObserver.setEventVersion(eventid);

            replyJson = mRemoteApi.getEvent(false, eventid);
            JSONArray resultsObj = replyJson.getJSONArray("result");
            mEventObserver.setEventChangeListener((ParameterHandler)parametersHandler);
            JsonUtils.loadSupportedApiListFromEvent(resultsObj.getJSONObject(0), mAvailableCameraApiSet);
            ((ParameterHandler) parametersHandler).SetCameraApiSet(mAvailableCameraApiSet);

            if (!mEventObserver.isActive())
                mEventObserver.activate();
            mEventObserver.processEvents(replyJson);

            // startRecMode if necessary.
            Log.d(TAG, "openConnection(): startRecMode");
            if (JsonUtils.isCameraApiAvailable("startRecMode", mAvailableCameraApiSet)) {
                Log.d(TAG, "openConnection(): startRecMode()");
                replyJson = mRemoteApi.startRecMode();

                // Call again.
                replyJson = mRemoteApi.getAvailableApiList();
                JsonUtils.loadAvailableCameraApiList(replyJson, mAvailableCameraApiSet);
                ((ParameterHandler) parametersHandler).SetCameraApiSet(mAvailableCameraApiSet);
            }


            Log.d(TAG, "openConnection(): setLiveViewFrameInfo");
            if(serverDevice != null &&(serverDevice.getFriendlyName().contains("ILCE-QX1") || serverDevice.getFriendlyName().contains("ILCE-QX30"))
                    && JsonUtils.isApiSupported("setLiveviewFrameInfo", (mAvailableCameraApiSet)) && parametersHandler.get(Settings.FocusMode) != null)
            {
                if (!parametersHandler.get(Settings.FocusMode).GetStringValue().equals("MF"))
                    ((CameraHolderSony) getCameraHolder()).SetLiveViewFrameInfo(true);
                else
                    ((CameraHolderSony) getCameraHolder()).SetLiveViewFrameInfo(false);
            }

            // Liveview start
            Log.d(TAG, "openConnection(): startLiveView");
            if (JsonUtils.isCameraApiAvailable("startLiveview", mAvailableCameraApiSet)) {
                Log.d(TAG, "openConnection(): LiveviewSurface.start()");
                cameraHolder.StartPreview();
            }

            // getEvent start
            Log.d(TAG, "openConnection(): getEvent");
            if (JsonUtils.isCameraApiAvailable("getEvent", mAvailableCameraApiSet)) {
                Log.d(TAG, "openConnection(): EventObserver.start()");
                if (!mEventObserver.isStarted())
                    mEventObserver.start();

            }

            Log.d(TAG, "openConnection(): completed.");
        } catch (IOException e) {
            Log.w(TAG, "openConnection : IOException: " + e.getMessage());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        onCameraOpen("");
    }

    @Override
    public void stopCamera()
    {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock)
                {
                    if (mEventObserver != null)
                        mEventObserver.stop();
                    cameraHolder.CloseCamera();
                    STATE = STATE_IDEL;
                }
            }
        });
    }

    @Override
    public void restartCamera() {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock){
                    if (mEventObserver != null)
                        mEventObserver.stop();
                    cameraHolder.CloseCamera();
                    STATE = STATE_IDEL;

                    if (serverDevice == null)
                    {
                        wifiHandler.setEventsListner(SonyCameraRemoteFragment.this);
                        wifiHandler.StartLookUp();
                        return;
                    }
                    Log.d(TAG,"startCamera");

                    startCamera();
                    Log.d(TAG, "onCameraOpen State:" + STATE);
                    STATE = STATE_DEVICE_CONNECTED;
                }
            }
        });

    }

    public void stopEventObserver()
    {
        mEventObserver.stop();
        mEventObserver.clearEventChangeListener();
    }

    //CameraWrapperInterface api specific overrides
    @Override
    public int getMargineLeft() {
        return surfaceView.getLeft();
    }

    @Override
    public int getMargineRight() {
        return surfaceView.getRight();
    }

    @Override
    public int getMargineTop() {
        return surfaceView.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return surfaceView.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return surfaceView.getHeight();
    }

    @Override
    public String getResString(int id) {
        return SettingsManager.getInstance().getResString(id);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }


    //SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        cameraHolder.StopPreview();
        stopCamera();
    }

    //WifiHandler.WifiEvents
    @Override
    public void onDeviceFound(ServerDevice serverDevice) {
        this.serverDevice = serverDevice;
        wifiHandler.setEventsListner(null);
        hideTextViewWifi(true);
        startCamera();
    }

    @Override
    public void onMessage(String msg) {
        setTextFromWifi(msg);
    }


    @Override
    public void onCameraOpen(String message)
    {

        //this.onCameraOpenFinish("");
    }

    @Override
    public void onCameraClose(String message) {
        if(mEventObserver != null)
            mEventObserver.release();
    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void onCameraError(String error)
    {
        Log.d(TAG, "###################### onCamerError:"+ error + " ################################");
        hideTextViewWifi(false);
        setTextFromWifi(error);
        serverDevice = null;
        STATE = STATE_IDEL;
        mEventObserver.stop();
        surfaceView.stop();
        //setCameraStateChangedListner(SonyCameraRemoteFragment.this);
        mBackgroundHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startCamera();
            }
        },5000);

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onApiSetChanged(Set<String> mAvailableCameraApiSet) {
        ((ParameterHandler)parametersHandler).SetCameraApiSet(mAvailableCameraApiSet);
    }
}
