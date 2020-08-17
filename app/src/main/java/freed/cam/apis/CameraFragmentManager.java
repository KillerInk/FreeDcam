package freed.cam.apis;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.troop.freedcam.R;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraToMainHandler;
import freed.cam.apis.basecamera.MainToCameraHandler;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.featuredetector.CameraFeatureDetectorFragment;
import freed.cam.apis.sonyremote.SonyCameraRemoteFragment;
import freed.renderscript.RenderScriptManager;
import freed.settings.SettingsManager;
import freed.utils.BackgroundHandlerThread;
import freed.utils.Log;

public class CameraFragmentManager implements CameraFeatureDetectorFragment.FeatureDetectorEvents {
    private final String TAG = CameraFragmentManager.class.getSimpleName();

    private int fragmentHolderId;
    private FragmentManager fragmentManager;
    private CameraFragmentAbstract cameraFragment;
    private RenderScriptManager renderScriptManager;

    private CameraFeatureDetectorFragment fd;
    private BackgroundHandlerThread backgroundHandlerThread;
    private MainToCameraHandler mainToCameraHandler;
    private CameraToMainHandler cameraToMainHandler;
    private ActivityInterface activityInterface;

    public CameraFragmentManager(FragmentManager fragmentManager, int fragmentHolderId, Context context, ActivityInterface activityInterface)
    {
        this.fragmentManager = fragmentManager;
        this.fragmentHolderId = fragmentHolderId;
        if (RenderScriptManager.isSupported())
            renderScriptManager = new RenderScriptManager(context);
        Log.d(TAG,"Create camera BackgroundHandler");
        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        backgroundHandlerThread.create();
        cameraToMainHandler = new CameraToMainHandler();
        this.mainToCameraHandler = new MainToCameraHandler(backgroundHandlerThread.getThread().getLooper());
        this.activityInterface = activityInterface;
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
        SettingsManager.getInstance().setAreFeaturesDetected(false);
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
            if (fd != null) {
                transaction.remove(fd);
                transaction.commit();
                fd = null;
            }
            switchCameraFragment();
        }
        catch (IllegalStateException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public void onResume()
    {
        Log.d(TAG, "onResume");
        if (cameraFragment != null) {
            Log.d(TAG, "Reuse CamaraFragment");
            mainToCameraHandler.setCameraInterface(cameraFragment);
            cameraFragment.init(mainToCameraHandler, cameraToMainHandler,activityInterface);
            cameraFragment.setRenderScriptManager(renderScriptManager);
        }
        else {
            Log.d(TAG, "create new CameraFragment");
            switchCameraFragment();
        }
    }

    public void  onPause()
    {
        Log.d(TAG, "onPause");
        if (cameraFragment != null) {
            //unloadCameraFragment();
            /*cameraFragment.stopCameraAsync();
            mainToCameraHandler.setCameraInterface(null);*/
        }
    }

    public void switchCameraFragment()
    {
        Log.d(TAG, "BackgroundHandler is null: " + (backgroundHandlerThread.getThread() == null) +
                " features detected: " + SettingsManager.getInstance().getAreFeaturesDetected() + " app version changed: " + SettingsManager.getInstance().appVersionHasChanged());
        if ((!SettingsManager.getInstance().getAreFeaturesDetected() || SettingsManager.getInstance().appVersionHasChanged()) && fd == null)
        {
            Log.d(TAG, "load featuredetector");
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
                        Log.d(TAG, "load sony remote");
                        cameraFragment = SonyCameraRemoteFragment.getInstance();
                        break;
                    case SettingsManager.API_2:
                        Log.d(TAG, "load camera2");
                        cameraFragment = Camera2Fragment.getInstance();
                        break;
                    default:
                        Log.d(TAG, "load camera1");
                        cameraFragment = Camera1Fragment.getInstance();
                        break;
                }

                mainToCameraHandler.setCameraInterface(cameraFragment);
                cameraFragment.init(mainToCameraHandler,cameraToMainHandler,activityInterface);
                cameraFragment.setRenderScriptManager(renderScriptManager);
                replaceCameraFragment(cameraFragment, cameraFragment.getClass().getSimpleName());
            }
        }
    }

    public void unloadCameraFragment()
    {
        Log.d(TAG, "unloadCameraFragment");
        if (cameraFragment != null) {
            //kill the cam befor the fragment gets removed to make sure when
            //new cameraFragment gets created and its texture view is created the cam get started
            //when its done in textureview/surfaceview destroy method its already to late and we get a security ex lack of privilege
            cameraFragment.stopCamera();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commit();
            cameraFragment = null;
            mainToCameraHandler.setCameraInterface(null);
        }
    }


}
