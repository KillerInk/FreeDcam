package freed.cam.apis.sonyremote;

import android.view.TextureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.AbstractCamera;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.parameters.modes.I_SonyApi;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.cam.apis.sonyremote.sonystuff.ServerDevice;
import freed.cam.apis.sonyremote.sonystuff.SimpleCameraEventObserver;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.cam.apis.sonyremote.sonystuff.SonyUtils;
import freed.cam.apis.sonyremote.sonystuff.WifiHandler;
import freed.cam.events.CaptureStateChangedEvent;
import freed.cam.events.EventBusHelper;
import freed.cam.previewpostprocessing.RenderScriptPreview;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class SonyRemoteCamera extends AbstractCamera<ParameterHandler,CameraHolderSony,ModuleHandlerSony, FocusHandler> implements WifiHandler.WifiEvents, CameraHolderSony.CameraRemoteEvents {

    private static final String TAG = SonyRemoteCamera.class.getSimpleName();
    private ServerDevice serverDevice;

    private final int STATE_IDEL = 0;
    private final int STATE_DEVICE_CONNECTED = 3;
    private int STATE = STATE_IDEL;
    WifiHandler wifiHandler;
    private SimpleRemoteApi mRemoteApi;
    private SimpleCameraEventObserver mEventObserver;
    private final Set<String> mAvailableCameraApiSet = new HashSet<>();
    PreviewStreamDrawer previewStreamDrawer;
    private UserMessageHandler userMessageHandler;

    public SonyRemoteCamera()
    {
        userMessageHandler = ActivityFreeDcamMain.userMessageHandler();
        RenderScriptPreview rsPrev = (RenderScriptPreview)preview;
        previewStreamDrawer = new PreviewStreamDrawer((TextureView) rsPrev.getPreviewView(),rsPrev.getRenderScriptManager());
        parametersHandler = new ParameterHandler(this, previewStreamDrawer);

        moduleHandler = new ModuleHandlerSony(this);
        focusHandler = new FocusHandler(this);
        parametersHandler.addApiChangedListner((I_SonyApi) focusHandler);
        cameraHolder = new CameraHolderSony(FreedApplication.getContext(), previewStreamDrawer, this);
        cameraHolder.addEventListner(this);
        moduleHandler.initModules();
    }

    public void onResume()
    {
        wifiHandler.onResume();
    }

    public void onPause()
    {
        wifiHandler.onPause();
    }


    @Override
    public void initCamera() {

    }

    @Override
    public void startCamera() {
        if (serverDevice == null)
        {
            wifiHandler.setEventsListner(this);
            wifiHandler.StartLookUp();
            return;
        }
        Log.d(TAG,"startCamera");
        startSonyCamera();
        Log.d(TAG, "onCameraOpen State:" + STATE);
        STATE = STATE_DEVICE_CONNECTED;
    }

    @Override
    public void stopCamera() {
        if (mEventObserver != null)
            mEventObserver.stop();
        if (cameraHolder !=  null)
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
            wifiHandler.setEventsListner(this);
            wifiHandler.StartLookUp();
            return;
        }
        Log.d(TAG,"startCamera");

        CameraThreadHandler.startCameraAsync();
        Log.d(TAG, "onCameraOpen State:" + STATE);
        STATE = STATE_DEVICE_CONNECTED;
    }

    @Override
    public void startPreview() {

    }

    @Override
    public void stopPreview() {
        cameraHolder.StopPreview();
    }

    private void setTextFromWifi(final String txt)
    {
        userMessageHandler.sendMSG(txt,false);
    }

    public Set<String> getAvailableApiSet(){return mAvailableCameraApiSet;}



    private void startSonyCamera()
    {
        Log.d(TAG, "########################### start Camera ##########################");
        if (mRemoteApi == null)
        {
            mRemoteApi = new SimpleRemoteApi(serverDevice);
            parametersHandler.SetRemoteApi(mRemoteApi);

        }
        mEventObserver = new SimpleCameraEventObserver(FreedApplication.getContext(), mRemoteApi);


        cameraHolder.setRemoteApi(mRemoteApi);
        cameraHolder.cameraRemoteEventsListner =this;

       /* try {
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
        }*/

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
            parametersHandler.SetCameraApiSet(mAvailableCameraApiSet);

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
                parametersHandler.SetCameraApiSet(mAvailableCameraApiSet);
            }


            Log.d(TAG, "openConnection(): setLiveViewFrameInfo");
            if(serverDevice != null &&(serverDevice.getFriendlyName().contains("ILCE-QX1") || serverDevice.getFriendlyName().contains("ILCE-QX30"))
                    && JsonUtils.isApiSupported("setLiveviewFrameInfo", (mAvailableCameraApiSet)) && parametersHandler.get(SettingKeys.FocusMode) != null)
            {
                if (!parametersHandler.get(SettingKeys.FocusMode).getStringValue().equals("MF"))
                    getCameraHolder().SetLiveViewFrameInfo(true);
                else
                    getCameraHolder().SetLiveViewFrameInfo(false);
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
        cameraHolder.fireCameraOpenFinished();
    }

    public void stopEventObserver()
    {
        mEventObserver.stop();
        mEventObserver.clearEventChangeListener();
    }

    @Override
    public void onApiSetChanged(Set<String> mAvailableCameraApiSet) {
        parametersHandler.SetCameraApiSet(mAvailableCameraApiSet);
    }

    //WifiHandler.WifiEvents
    @Override
    public void onDeviceFound(ServerDevice serverDevice) {
        this.serverDevice = serverDevice;
        wifiHandler.setEventsListner(null);
        CameraThreadHandler.startCameraAsync();
    }

    @Override
    public void onMessage(String msg) {
        setTextFromWifi(msg);
    }

    public void stop()
    {
        serverDevice = null;
        mEventObserver.stop();
    }

    public void release()
    {
        if(mEventObserver != null)
            mEventObserver.release();
    }


   /* @Override
    public void onPreviewClose() {
        stopPreview();
        CameraThreadHandler.stopCameraAsync();
    }*/

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinished() {

    }

    @Override
    public void onCameraClose() {
        release();
    }

    @Override
    public void onCameraError(String error) {
        Log.d(TAG, "###################### onCamerError:"+ error + " ################################");
        setTextFromWifi(error);
        STATE = STATE_IDEL;
        stop();
        previewStreamDrawer.stop();
        //setCameraEventListner(SonyCameraRemoteFragment.this);
        CameraThreadHandler.startCameraAsync(5000);
    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }
}
