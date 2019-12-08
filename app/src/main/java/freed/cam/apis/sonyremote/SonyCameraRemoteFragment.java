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
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.parameters.modes.I_SonyApi;
import freed.cam.apis.sonyremote.sonystuff.Auth;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.cam.apis.sonyremote.sonystuff.ServerDevice;
import freed.cam.apis.sonyremote.sonystuff.SimpleCameraEventObserver;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import freed.cam.apis.sonyremote.sonystuff.SonyUtils;
import freed.cam.apis.sonyremote.sonystuff.WifiHandler;
import freed.cam.events.CameraStateEvents;
import freed.cam.events.CaptureStateChangedEvent;
import freed.cam.events.EventBusHelper;
import freed.cam.events.EventBusLifeCycle;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 06.06.2015.
 */
public class SonyCameraRemoteFragment extends CameraFragmentAbstract implements SurfaceHolder.Callback, WifiHandler.WifiEvents, CameraHolderSony.CameraRemoteEvents, EventBusLifeCycle
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

    public static SonyCameraRemoteFragment getInstance()
    {
        SonyCameraRemoteFragment fragment = new SonyCameraRemoteFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(layout.camerafragment_sonyapi, container, false);
        surfaceView = view.findViewById(id.view);
        surfaceView.SetRenderScriptHandlerAndInterface(renderScriptManager, (ActivityInterface) getActivity());

        textView_wifi = view.findViewById(id.textView_wificonnect);

        wifiHandler = new WifiHandler(getActivityInterface());
        parametersHandler = new ParameterHandler(this, surfaceView, getContext());

        moduleHandler = new ModuleHandlerSony(this);
        Focus = new FocusHandler(this);
        ((ParameterHandler)parametersHandler).addApiChangedListner((I_SonyApi) Focus);
        cameraHolder = new CameraHolderSony(getContext(), surfaceView, this);
        moduleHandler.initModules();

        //this.onCameraOpenFinish("");
        return view;
    }

    @Override
    public void startListning() {
        EventBusHelper.register(this);
    }

    @Override
    public void stopListning() {
        EventBusHelper.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startListning();
        wifiHandler.onResume();
        startCameraAsync();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.stopCamera");
        wifiHandler.onPause();
        stopCameraAsync();
        stopListning();
    }


    private void setTextFromWifi(final String txt)
    {
        textView_wifi.post(() -> textView_wifi.setText(txt));
    }

    private void hideTextViewWifi(final boolean hide)
    {
        textView_wifi.post(() -> {
            if (hide)
                textView_wifi.setVisibility(View.GONE);
            else
                textView_wifi.setVisibility(View.VISIBLE);
        });
    }

    public Set<String> getAvailableApiSet(){return mAvailableCameraApiSet;}


    @Override
    public String CameraApiName() {

        return SettingsManager.API_SONY;
    }

    @Override
    public void startCameraAsync()
    {
        if (serverDevice == null)
        {
            wifiHandler.setEventsListner(this);
            wifiHandler.StartLookUp();
            return;
        }
        Log.d(TAG,"startCamera");

        mainToCameraHandler.startCamera();
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
            replyJson = mRemoteApi.getAccessMethodTypes();
            replyJson = mRemoteApi.getAccessVersions();
            replyJson = mRemoteApi.actEnableMethods("","","","");
            //result = {"result":[{"dg":"4b263abeeb922f3070f452553fb6bd9b04605d25928932922d2957ab092a4a99"}],"id":3}
            String dg =replyJson.getJSONArray("result").getJSONObject(0).getString("dg");
            String sg  = new Auth().SHA256(dg);
            replyJson = mRemoteApi.actEnableMethods(Auth.METHODS_TO_ENABLE,"Sony Corporation","7DED695E-75AC-4ea9-8A85-E5F8CA0AF2F3",sg);
            Log.d(TAG,replyJson.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

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
                EventBusHelper.post(new CaptureStateChangedEvent(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_working));
            else if (JsonUtils.isApiSupported("stopContShooting",mAvailableCameraApiSet))
                EventBusHelper.post(new CaptureStateChangedEvent(ModuleHandlerAbstract.CaptureStates.continouse_capture_start));
            else if (JsonUtils.isApiSupported("actTakePicture",mAvailableCameraApiSet))
                EventBusHelper.post(new CaptureStateChangedEvent(ModuleHandlerAbstract.CaptureStates.image_capture_stop));
            else if (JsonUtils.isApiSupported("awaitTakePicture",mAvailableCameraApiSet))
                EventBusHelper.post(new CaptureStateChangedEvent(ModuleHandlerAbstract.CaptureStates.image_capture_start));

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
            }
        } catch (IOException e) {
            Log.w(TAG, "prepareToStartContentsListMode: IOException: " + e.getMessage());

        } catch (JSONException e) {
            Log.w(TAG, "prepareToStartContentsListMode: JSONException: " + e.getMessage());

        }
    }

    private void startOpenConnectionAfterChangeCameraState() {
        Log.d(TAG, "startOpenConectiontAfterChangeCameraState() exec");

        mEventObserver.setCameraStateChangedListener(status -> {
            Log.d(TAG, "onCameraStatusChanged:" + status);
            if ("IDLE".equals(status)) {
                openConnection();
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

            replyJson = mRemoteApi.getCameraMethodTypes();
            Log.d(TAG,replyJson.toString());
            //find api version for requests
            replyJson = mRemoteApi.getVersions();
            JSONArray array = replyJson.getJSONArray("result");
            Log.d(TAG,array.toString());
            array = array.getJSONArray(0);
            String eventid = array.getString(array.length()-1);
            Log.d(TAG,"SetEventVersion:" +eventid);
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
                    && JsonUtils.isApiSupported("setLiveviewFrameInfo", (mAvailableCameraApiSet)) && parametersHandler.get(SettingKeys.FocusMode) != null)
            {
                if (!parametersHandler.get(SettingKeys.FocusMode).GetStringValue().equals("MF"))
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
        fireCameraOpenFinished();
    }

    @Override
    public void stopCameraAsync()
    {
        mainToCameraHandler.stopCamera();
    }

    @Override
    public void restartCameraAsync() {
        mainToCameraHandler.restartCamera();
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
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    @Override
    public RenderScriptProcessorInterface getFocusPeakProcessor() {
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
        stopCameraAsync();
    }

    //WifiHandler.WifiEvents
    @Override
    public void onDeviceFound(ServerDevice serverDevice) {
        this.serverDevice = serverDevice;
        wifiHandler.setEventsListner(null);
        hideTextViewWifi(true);
        startCameraAsync();
    }

    @Override
    public void onMessage(String msg) {
        setTextFromWifi(msg);
    }




    @Subscribe
    public void onCameraClose(CameraStateEvents.CameraCloseEvent message) {
        if(mEventObserver != null)
            mEventObserver.release();
    }

    @Subscribe
    public void onCameraError(CameraStateEvents.CameraErrorEvent error)
    {
        Log.d(TAG, "###################### onCamerError:"+ error + " ################################");
        hideTextViewWifi(false);
        setTextFromWifi(error.msg);
        serverDevice = null;
        STATE = STATE_IDEL;
        mEventObserver.stop();
        surfaceView.stop();
        //setCameraEventListner(SonyCameraRemoteFragment.this);
        mainToCameraHandler.postDelayed(() -> startCameraAsync(),5000);

    }

    @Override
    public void onApiSetChanged(Set<String> mAvailableCameraApiSet) {
        ((ParameterHandler)parametersHandler).SetCameraApiSet(mAvailableCameraApiSet);
    }


    @Override
    public void createCamera() {

    }

    @Override
    public void initCamera() {

    }

    @Override
    public void startCamera() {
        startSonyCamera();
        Log.d(TAG, "onCameraOpen State:" + STATE);
        STATE = STATE_DEVICE_CONNECTED;
    }

    @Override
    public void stopCamera() {
        if (mEventObserver != null)
            mEventObserver.stop();
        cameraHolder.CloseCamera();
        STATE = STATE_IDEL;
    }

    @Override
    public void restartCamera() {
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

        startCameraAsync();
        Log.d(TAG, "onCameraOpen State:" + STATE);
        STATE = STATE_DEVICE_CONNECTED;
    }

    @Override
    public void startPreview() {

    }

    @Override
    public void stopPreview() {

    }
}
