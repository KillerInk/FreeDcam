package freed.cam.apis;


import android.graphics.SurfaceTexture;
import android.os.Build;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraHolderEvent;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.featuredetector.CameraFeatureDetector;
import freed.cam.apis.sonyremote.SonyRemoteCamera;
import freed.cam.previewpostprocessing.Preview;
import freed.cam.previewpostprocessing.PreviewController;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.BackgroundHandlerThread;
import freed.utils.Log;

public class CameraFragmentManager implements Preview.PreviewEvent, CameraHolderEvent {
    private final String TAG = CameraFragmentManager.class.getSimpleName();

    private int fragmentHolderId;
    private FragmentManager fragmentManager;
    //private CameraFragmentAbstract cameraFragment;
    private PreviewFragment previewFragment;

    private BackgroundHandlerThread backgroundHandlerThread;
    private SettingsManager settingsManager;
    private CameraWrapperInterface camera;
    private boolean PreviewSurfaceRdy;
    boolean cameraIsOpen;
    private PreviewController previewController;
    private List<CameraHolderEvent> eventList;

    @Inject
    public CameraFragmentManager(SettingsManager settingsManager, PreviewController previewController)
    {
        this.settingsManager = settingsManager;
        this.previewController = previewController;
        eventList = new ArrayList<>();
    }

    public void init(FragmentManager fragmentManager, int fragmentHolderId)
    {
        this.fragmentManager = fragmentManager;
        this.fragmentHolderId = fragmentHolderId;
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
        if (previewFragment != null) {
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
                camera.getCameraHolder().addEventListner(this);
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
            camera.getCameraHolder().removeEventListner(this);
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
        if (previewFragment != null) {
            Log.d(TAG, "unload old Preview");
            //kill the cam befor the fragment gets removed to make sure when
            //new cameraFragment gets created and its texture view is created the cam get started
            //when its done in textureview/surfaceview destroy method its already to late and we get a security ex lack of privilege
            CameraThreadHandler.stopCameraAsync();
            FragmentTransaction transaction  = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(previewFragment);
            transaction.commit();
            previewFragment = null;
            previewController.setPreviewEventListner(null);
        }
        Log.d(TAG, "load new Preview");
        previewFragment = new PreviewFragment();
        previewController.setPreviewEventListner(this);
        FragmentTransaction transaction  = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
        transaction.replace(fragmentHolderId, previewFragment, previewFragment.getClass().getSimpleName());
        transaction.commit();
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
    public void onCameraOpen() {
        Log.d(TAG,"onCameraOpen");
        for (CameraHolderEvent event : eventList)
            event.onCameraOpen();
    }

    @Override
    public void onCameraOpenFinished() {
        Log.d(TAG,"onCameraOpenFinished");
        for (CameraHolderEvent event : eventList)
            event.onCameraOpenFinished();
    }

    @Override
    public void onCameraClose() {
        Log.d(TAG,"onCameraClose");
        for (CameraHolderEvent event : eventList)
            event.onCameraClose();
    }

    @Override
    public void onCameraError(String error) {
        Log.d(TAG,"onCameraError " +error);
        for (CameraHolderEvent event : eventList)
            event.onCameraError(error);
    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {
        Log.d(TAG,"onCameraChangedAspectRatioEvent " +size.toString());
        for (CameraHolderEvent event : eventList)
            event.onCameraChangedAspectRatioEvent(size);
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
        eventList.add(event);
    }

    public void removeEventListner(CameraHolderEvent event)
    {
        Log.d(TAG,"removeEventListner " + event.getClass().getSimpleName());
        eventList.remove(event);
    }
}
