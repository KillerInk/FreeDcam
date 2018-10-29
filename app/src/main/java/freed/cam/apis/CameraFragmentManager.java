package freed.cam.apis;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraStateEvents;
import freed.cam.apis.basecamera.CameraToMainHandler;
import freed.cam.apis.basecamera.MainToCameraHandler;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.featuredetector.CameraFeatureDetectorFragment;
import freed.cam.apis.sonyremote.SonyCameraRemoteFragment;
import freed.renderscript.RenderScriptManager;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.BackgroundHandlerThread;
import freed.utils.Log;

public class CameraFragmentManager implements CameraFeatureDetectorFragment.FeatureDetectorEvents {
    private final String TAG = CameraFragmentManager.class.getSimpleName();

    private int fragmentHolderId;
    private FragmentManager fragmentManager;
    private CameraFragmentAbstract cameraFragment;
    private RenderScriptManager renderScriptManager;
    private CameraStateEvents cameraStateEventListner;
    private CameraFeatureDetectorFragment fd;
    private BackgroundHandlerThread backgroundHandlerThread;
    private MainToCameraHandler mainToCameraHandler;
    private CameraToMainHandler cameraToMainHandler;

    public CameraFragmentManager(FragmentManager fragmentManager, int fragmentHolderId, Context context, CameraStateEvents cameraStateEventListner)
    {
        this.fragmentManager = fragmentManager;
        this.fragmentHolderId = fragmentHolderId;
        this.cameraStateEventListner = cameraStateEventListner;
        if (RenderScriptManager.isSupported())
            renderScriptManager = new RenderScriptManager(context);
        Log.d(TAG,"Create camera BackgroundHandler");
        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        backgroundHandlerThread.create();
        cameraToMainHandler = new CameraToMainHandler();
        this.mainToCameraHandler = new MainToCameraHandler(backgroundHandlerThread.getThread().getLooper());
        /*startBackgroundThread();*/
    }

    public void destroy()
    {
        Log.d(TAG,"Destroy camera BackgroundHandler");
        backgroundHandlerThread.destroy();
    }

    public CameraFragmentAbstract getCameraFragment()
    {
        return cameraFragment;
    }


    private void replaceCameraFragment(Fragment fragment, String id)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
        transaction.replace(fragmentHolderId, fragment, id);
        transaction.commit();
    }

    private void loadFeatureDetector() {
        Log.d(TAG, "Start FeatureDetector");
        SettingsManager.getInstance().RESET();
        fd = new CameraFeatureDetectorFragment();
        fd.setFeatureDetectorDoneListner(this);
        replaceCameraFragment(fd, "FeatureDetector");
    }

    @Override
    public void featuredetectorDone() {
        Log.d(TAG,"FD done, load cameraFragment");
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(fd);
            transaction.commit();
            fd = null;
            switchCameraFragment();
        }
        catch (IllegalStateException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public void switchCameraFragment()
    {
        Log.d(TAG, "BackgroundHandler is null: " + (backgroundHandlerThread.getThread() == null));
        if ((!SettingsManager.get(SettingKeys.areFeaturesDetected).get() || SettingsManager.getInstance().appVersionHasChanged()) && fd == null)
        {
            if (cameraFragment != null)
                unloadCameraFragment();
            loadFeatureDetector();
        }
        else if (fd == null)
        {
            if (cameraFragment == null) {
                String api = SettingsManager.getInstance().getCamApi();
                switch (api) {
                    case SettingsManager.API_SONY:
                        cameraFragment = SonyCameraRemoteFragment.getInstance();
                        break;
                    case SettingsManager.API_2:
                        cameraFragment = Camera2Fragment.getInstance();
                        break;
                    default:
                        cameraFragment = Camera1Fragment.getInstance();
                        break;
                }
                cameraFragment.init(mainToCameraHandler,cameraToMainHandler);
                mainToCameraHandler.setCameraInterface(cameraFragment);
                cameraToMainHandler.setMainMessageEventWeakReference(cameraFragment);
                cameraFragment.setRenderScriptManager(renderScriptManager);
                cameraFragment.setCameraEventListner(cameraStateEventListner);
                replaceCameraFragment(cameraFragment, cameraFragment.getClass().getSimpleName());
            } else {
                mainToCameraHandler.setCameraInterface(cameraFragment);
                cameraToMainHandler.setMainMessageEventWeakReference(cameraFragment);
                cameraFragment.startCameraAsync();
            }
        }
    }

    public void unloadCameraFragment()
    {
        if (cameraFragment != null) {
            //kill the cam befor the fragment gets removed to make sure when
            //new cameraFragment gets created and its texture view is created the cam get started
            //when its done in textureview/surfaceview destroy method its already to late and we get a security ex lack of privilege
            cameraFragment.stopCameraAsync();
            mainToCameraHandler.setCameraInterface(null);
            cameraToMainHandler.setMainMessageEventWeakReference(null);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commit();
            cameraFragment = null;
        }
    }
}
