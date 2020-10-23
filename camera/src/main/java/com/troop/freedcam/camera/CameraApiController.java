package com.troop.freedcam.camera;

import com.troop.freedcam.camera.basecamera.handler.CameraToMainHandler;
import com.troop.freedcam.camera.basecamera.handler.MainToCameraHandler;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.utils.BackgroundHandlerThread;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

public class CameraApiController {

    private final String TAG = CameraApiController.class.getSimpleName();
    private BackgroundHandlerThread backgroundHandlerThread;
    private MainToCameraHandler mainToCameraHandler;
    private CameraToMainHandler cameraToMainHandler;
    private RenderScriptManager renderScriptManager;

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
}
