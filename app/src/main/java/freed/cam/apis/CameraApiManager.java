package freed.cam.apis;


import android.graphics.SurfaceTexture;
import android.os.Build;

import com.troop.freedcam.R;

import javax.inject.Inject;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.featuredetector.CameraFeatureDetector;
import freed.cam.apis.sonyremote.SonyRemoteCamera;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.event.camera.CameraHolderEventHandler;
import freed.cam.event.capture.CaptureStateChangedEvent;
import freed.cam.event.capture.CaptureStateChangedEventHandler;
import freed.cam.previewpostprocessing.Preview;
import freed.cam.previewpostprocessing.PreviewController;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.BackgroundHandlerThread;
import freed.utils.Log;

public class CameraApiManager implements Preview.PreviewEvent {
    private final String TAG = CameraApiManager.class.getSimpleName();



    private BackgroundHandlerThread backgroundHandlerThread;
    private SettingsManager settingsManager;
    private CameraWrapperInterface camera;
    private boolean PreviewSurfaceRdy;
    boolean cameraIsOpen;
    private PreviewController previewController;
    private CaptureStateChangedEventHandler captureStateChangedEventHandler;
    private CameraHolderEventHandler cameraHolderEventHandler;

    @Inject
    public CameraApiManager(SettingsManager settingsManager, PreviewController previewController)
    {
        this.settingsManager = settingsManager;
        this.previewController = previewController;
        captureStateChangedEventHandler = new CaptureStateChangedEventHandler();
        cameraHolderEventHandler = new CameraHolderEventHandler();
    }

    public void init()
    {
        Log.d(TAG,"Create camera BackgroundHandler");
        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        backgroundHandlerThread.create();
        new CameraThreadHandler(backgroundHandlerThread.getThread().getLooper());

    }

    public void destroy()
    {
        Log.d(TAG,"Destroy camera BackgroundHandler");
        backgroundHandlerThread.destroy();
        CameraThreadHandler.close();
    }

    private void loadFeatureDetector() {
        Log.d(TAG, "Start FeatureDetector");
        settingsManager.setAreFeaturesDetected(false);
        new CameraFeatureDetector().detectFeatures();
        switchCamera();
    }


    public void onResume()
    {
        Log.d(TAG, "onResume");
        if (camera == null)
            switchCamera();
        if (previewController.isPreviewInit()) {
            Log.d(TAG, "Reuse CamaraFragment");
        }
        else {
            Log.d(TAG, "create new CameraFragment");
            changePreviewPostProcessing();
        }
        if (PreviewSurfaceRdy && !cameraIsOpen) {
            CameraThreadHandler.startCameraAsync();
        }
    }

    public void  onPause()
    {
        Log.d(TAG, "onPause");
        if(camera != null
                && camera.getModuleHandler() != null
                && camera.getModuleHandler().getCurrentModule() != null
                && camera.getModuleHandler().getCurrentModule().ModuleName() != null
                && camera.getModuleHandler().getCurrentModule().ModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_video))
                && camera.getModuleHandler().getCurrentModule().IsWorking())
            camera.getModuleHandler().getCurrentModule().DoWork();
        CameraThreadHandler.stopCameraAsync();
    }

    public void switchCamera()
    {
        Log.d(TAG, "BackgroundHandler is null: " + (backgroundHandlerThread.getThread() == null) +
                " features detected: " + settingsManager.getAreFeaturesDetected() + " app version changed: " + settingsManager.appVersionHasChanged());
        if ((!settingsManager.getAreFeaturesDetected() || settingsManager.appVersionHasChanged()))
        {
            Log.d(TAG, "load featuredetector");
            if (camera != null)
                unloadCamera();
            loadFeatureDetector();
        }
        else
        {
            if (/*cameraFragment == null*/true) {
                String api = settingsManager.getCamApi();
                switch (api) {
                    case SettingsManager.API_SONY:
                        Log.d(TAG, "load sony remote");
                        camera = new SonyRemoteCamera();
                        break;
                    case SettingsManager.API_2:
                        Log.d(TAG, "load camera2");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            camera = new Camera2();
                        }
                        break;
                    default:
                        Log.d(TAG, "load camera1");
                        camera = new Camera1();
                        break;
                }
                CameraThreadHandler.setCameraInterface(camera);
                cameraHolderEventHandler.setEventListner(camera);
                camera.setCameraHolderEventHandler(cameraHolderEventHandler);
                camera.setCaptureStateChangedEventHandler(captureStateChangedEventHandler);
                if (!cameraIsOpen && PreviewSurfaceRdy)
                    CameraThreadHandler.startCameraAsync();
            }
        }
    }

    public CameraWrapperInterface getCamera()
    {
        return camera;
    }

    public void unloadCamera()
    {
        Log.d(TAG, "unloadCameraFragment");
        if (camera != null) {
            //kill the cam befor the fragment gets removed to make sure when
            //new cameraFragment gets created and its texture view is created the cam get started
            //when its done in textureview/surfaceview destroy method its already to late and we get a security ex lack of privilege
            CameraThreadHandler.stopCameraAsync();
            CameraThreadHandler.setCameraInterface(null);
            camera.setCameraHolderEventHandler(null);
            cameraHolderEventHandler.removeEventListner(camera);
        }
    }

    public void runFeatureDetector() {
        unloadCamera();
        boolean legacy = settingsManager.get(SettingKeys.openCamera1Legacy).get();
        boolean showHelpOverlay = settingsManager.getShowHelpOverlay();
        settingsManager.RESET();
        settingsManager.get(SettingKeys.openCamera1Legacy).set(legacy);
        settingsManager.setshowHelpOverlay(showHelpOverlay);
        switchCamera();
    }

    public void changePreviewPostProcessing()
    {
        CameraThreadHandler.stopCameraAsync();
        previewController.setPreviewEventListner(null);
        previewController.changePreviewPostProcessing();
        previewController.setPreviewEventListner(this);
    }

    @Override
    public void onPreviewAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG,"onPreviewAvailable");
        PreviewSurfaceRdy = true;
        if (!cameraIsOpen)
            CameraThreadHandler.startCameraAsync();
        else
            CameraThreadHandler.initCameraAsync();
    }

    @Override
    public void onPreviewSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG,"onPreviewSizeChanged " +width + "/" + height);
    }

    @Override
    public boolean onPreviewDestroyed(SurfaceTexture surface) {
        Log.d(TAG,"onPreviewDestroyed");
        PreviewSurfaceRdy = false;
        return false;
    }

    @Override
    public void onPreviewUpdated(SurfaceTexture surface) {
        Log.d(TAG,"onPreviewUpdated");
    }

    public void addEventListner(CameraHolderEvent event)
    {
        Log.d(TAG,"addEventListner " + event.getClass().getSimpleName());
        cameraHolderEventHandler.setEventListner(event);
    }

    public void removeEventListner(CameraHolderEvent event)
    {
        Log.d(TAG,"removeEventListner " + event.getClass().getSimpleName());
        cameraHolderEventHandler.removeEventListner(event);
    }

    public void addCaptureStateChangedEventListner(CaptureStateChangedEvent listner)
    {
        captureStateChangedEventHandler.setEventListner(listner);
    }

    public void removeCaptureStateChangedListner(CaptureStateChangedEvent listner)
    {
        captureStateChangedEventHandler.removeEventListner(listner);
    }
}
