package com.troop.freedcam.cameraui.fragment;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.troop.freedcam.camera.basecamera.AbstractCameraController;
import com.troop.freedcam.camera.basecamera.handler.CameraToMainHandler;
import com.troop.freedcam.camera.basecamera.handler.MainToCameraHandler;

import com.troop.freedcam.camera.camera1.Camera1Controller;
import com.troop.freedcam.camera.camera2.Camera2Controller;
import com.troop.freedcam.camera.sonyremote.CameraControllerSonyRemote;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.EventBusLifeCycle;
import com.troop.freedcam.eventbus.events.SwichCameraFragmentEvent;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.BackgroundHandlerThread;
import com.troop.freedcam.utils.Log;
import com.troop.freedcam.utils.PermissionManager;

import org.greenrobot.eventbus.Subscribe;


public class CameraFragmentManager implements EventBusLifeCycle {
    private final String TAG = CameraFragmentManager.class.getSimpleName();

    @Subscribe
    public void onChangeApi(SwichCameraFragmentEvent changeapi)
    {
        switchCameraFragment();
    }


    private final int fragmentHolderId;
    private final FragmentManager fragmentManager;
    private AbstractCameraController cameraController;
    private BasicCameraFragment cameraFragment;
    private RenderScriptManager renderScriptManager;
    private final PermissionManager permissionManager;

    private final BackgroundHandlerThread backgroundHandlerThread;
    private final MainToCameraHandler mainToCameraHandler;
    private final CameraToMainHandler cameraToMainHandler;

    public CameraFragmentManager(FragmentManager fragmentManager, int fragmentHolderId, Context context, PermissionManager permissionManager)
    {
        this.fragmentManager = fragmentManager;
        this.fragmentHolderId = fragmentHolderId;
        this.permissionManager = permissionManager;
        if (RenderScriptManager.isSupported())
            renderScriptManager = new RenderScriptManager(context);
        Log.d(TAG,"Create camera BackgroundHandler");
        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        backgroundHandlerThread.create();
        cameraToMainHandler = new CameraToMainHandler();
        this.mainToCameraHandler = new MainToCameraHandler(backgroundHandlerThread.getThread().getLooper());
    }

    public void destroy()
    {
        Log.d(TAG,"Destroy camera BackgroundHandler");
        backgroundHandlerThread.destroy();
    }


    private void replaceCameraFragment(Fragment fragment, String id)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
       //transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
        transaction.replace(fragmentHolderId, fragment, id);
        transaction.commit();
    }

    public void onResume()
    {
        startListning();
        Log.d(TAG, "onResume");
        if (cameraController != null) {
            Log.d(TAG, "Reuse CamaraFragment");
            mainToCameraHandler.setCameraInterface(cameraController);
            cameraController.init(mainToCameraHandler, cameraToMainHandler);
            cameraController.setRenderScriptManager(renderScriptManager);
            cameraController.setPermissionManager(permissionManager);
        }
        else {
            Log.d(TAG, "create new CameraFragment");
            switchCameraFragment();
        }
    }

    public void  onPause()
    {
        stopListning();
        Log.d(TAG, "onPause");
        if (cameraController != null) {
            //unloadCameraFragment();
            /*cameraFragment.stopCameraAsync();
            mainToCameraHandler.setCameraInterface(null);*/
        }
    }

    public void switchCameraFragment()
    {
        if (cameraController != null && cameraFragment != null)
            unloadCameraFragment();
        else {
            String api = SettingsManager.getInstance().getCamApi();
            switch (api) {
                case SettingsManager.API_SONY:
                    Log.d(TAG, "load sony remote");
                    cameraController = new CameraControllerSonyRemote();
                    break;
                case SettingsManager.API_2:
                    Log.d(TAG, "load camera2");
                    cameraController = new Camera2Controller();
                    break;
                default:
                    Log.d(TAG, "load camera1");
                    cameraController = new Camera1Controller();
                    break;
            }

            mainToCameraHandler.setCameraInterface(cameraController);
            cameraController.init(mainToCameraHandler,cameraToMainHandler);
            cameraController.setRenderScriptManager(renderScriptManager);
            cameraController.setPermissionManager(permissionManager);
            cameraController.createCamera();
            cameraFragment = new BasicCameraFragment();
            cameraFragment.setCameraController(cameraController);
            replaceCameraFragment(cameraFragment, cameraController.getClass().getSimpleName());
        }
    }

    public void unloadCameraFragment()
    {
        Log.d(TAG, "unloadCameraFragment");
        if (cameraController != null) {
            //kill the cam befor the fragment gets removed to make sure when
            //new cameraFragment gets created and its texture view is created the cam get started
            //when its done in textureview/surfaceview destroy method its already to late and we get a security ex lack of privilege
            cameraController.stopCamera();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //transaction.setCustomAnimations(com.troop.freedcam.camera.R.anim.right_to_left_enter, com.troop.freedcam.camera.R.anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commit();
            cameraController = null;
            cameraFragment = null;
            mainToCameraHandler.setCameraInterface(null);
        }
    }


    @Override
    public void startListning() {
        EventBusHelper.register(this);
    }

    @Override
    public void stopListning() {
        EventBusHelper.unregister(this);
    }
}
