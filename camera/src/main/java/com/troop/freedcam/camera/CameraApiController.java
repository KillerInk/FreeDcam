package com.troop.freedcam.camera;

import com.troop.freedcam.camera.basecamera.AbstractCameraController;
import com.troop.freedcam.camera.basecamera.handler.CameraToMainHandler;
import com.troop.freedcam.camera.basecamera.handler.MainToCameraHandler;
import com.troop.freedcam.camera.camera1.Camera1Controller;
import com.troop.freedcam.camera.camera2.Camera2Controller;
import com.troop.freedcam.camera.sonyremote.CameraControllerSonyRemote;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.BackgroundHandlerThread;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

public class CameraApiController {

    private final String TAG = CameraApiController.class.getSimpleName();
    private BackgroundHandlerThread backgroundHandlerThread;
    private MainToCameraHandler mainToCameraHandler;
    private CameraToMainHandler cameraToMainHandler;
    private RenderScriptManager renderScriptManager;
    private AbstractCameraController cameraController;

    public CameraApiController()
    {
        if (RenderScriptManager.isSupported())
            renderScriptManager = new RenderScriptManager(ContextApplication.getContext());
        Log.d(TAG,"Create camera BackgroundHandler");
        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        backgroundHandlerThread.create();
        cameraToMainHandler = new CameraToMainHandler();
        this.mainToCameraHandler = new MainToCameraHandler(backgroundHandlerThread.getThread().getLooper());
    }

    public AbstractCameraController getCameraController()
    {
        return cameraController;
    }

    public void changeApi()
    {
        if (SettingsManager.getInstance().getCamApi().equals(SettingsManager.API_2))
            cameraController = new Camera2Controller();
        else if (SettingsManager.getInstance().getCamApi().equals(SettingsManager.API_SONY))
            cameraController = new CameraControllerSonyRemote();
        else
            cameraController = new Camera1Controller();
        cameraController.init(mainToCameraHandler,cameraToMainHandler);
    }

    public void onResume()
    {
        cameraController.init(mainToCameraHandler,cameraToMainHandler);
        cameraController.setRenderScriptManager(renderScriptManager);
    }
}
