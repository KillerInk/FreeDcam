package freed.cam.apis;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraStateEvents;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.featuredetector.CameraFeatureDetectorFragment;
import freed.cam.apis.sonyremote.SonyCameraRemoteFragment;
import freed.settings.AppSettingsManager;
import freed.utils.Log;
import freed.utils.RenderScriptManager;

public class CameraFragmentManager implements CameraFeatureDetectorFragment.FeatureDetectorEvents {
    private final String TAG = CameraFragmentManager.class.getSimpleName();

    private int fragmentHolderId;
    private FragmentManager fragmentManager;
    private CameraFragmentAbstract cameraFragment;
    private RenderScriptManager renderScriptManager;

    private Object cameraLock = new Object();
    private HandlerThread mBackgroundThread;
    private CameraStateEvents cameraStateEventListner;
    private CameraFeatureDetectorFragment fd;

    public CameraFragmentManager(FragmentManager fragmentManager, int fragmentHolderId, Context context, CameraStateEvents cameraStateEventListner)
    {
        this.fragmentManager = fragmentManager;
        this.fragmentHolderId = fragmentHolderId;
        this.cameraStateEventListner = cameraStateEventListner;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            renderScriptManager = new RenderScriptManager(context);
        startBackgroundThread();
    }

    public void destroy()
    {
        stopBackgroundThread();
    }

    public CameraFragmentAbstract getCameraFragment()
    {
        return cameraFragment;
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        synchronized (cameraLock) {
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
        }
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread()
    {
        synchronized (cameraLock) {
            Log.d(TAG, "stopBackgroundThread");
            if (mBackgroundThread == null)
                return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundThread.quitSafely();
            } else
                mBackgroundThread.quit();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
            } catch (InterruptedException e) {
                Log.WriteEx(e);
            }
        }
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
        AppSettingsManager.getInstance().RESET();
        fd = new CameraFeatureDetectorFragment();
        fd.setFeatureDetectorDoneListner(this);
        replaceCameraFragment(fd, "FeatureDetector");
    }

    @Override
    public void featuredetectorDone() {
        Log.d(TAG,"FD done, load cameraFragment");
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
        transaction.remove(fd);
        transaction.commit();
        fd = null;
        switchCameraFragment();
    }

    public void switchCameraFragment()
    {
        if ((!AppSettingsManager.getInstance().areFeaturesDetected() || AppSettingsManager.getInstance().appVersionHasChanged()) && fd == null)
        {
            if (cameraFragment != null)
                unloadCameraFragment();
            loadFeatureDetector();
        }
        else if (fd == null)
        {
            if (cameraFragment == null) {
                String api = AppSettingsManager.getInstance().getCamApi();
                if (api.equals(AppSettingsManager.API_SONY)) {
                    cameraFragment = SonyCameraRemoteFragment.getInstance(mBackgroundThread, cameraLock);
                } else if (api.equals(AppSettingsManager.API_2)) {
                    cameraFragment = Camera2Fragment.getInstance(mBackgroundThread, cameraLock);
                } else {
                    cameraFragment = Camera1Fragment.getInstance(mBackgroundThread, cameraLock);
                }
                cameraFragment.SetRenderScriptHandler(renderScriptManager);
                cameraFragment.setCameraStateChangedListner(cameraStateEventListner);
                replaceCameraFragment(cameraFragment, cameraFragment.getClass().getSimpleName());
            } else cameraFragment.startCamera();
        }
    }

    public void unloadCameraFragment()
    {
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
        }
    }
}
