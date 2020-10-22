package com.troop.freedcam.camera.basecamera;

import com.troop.freedcam.camera.basecamera.cameraholder.CameraHolderAbstract;
import com.troop.freedcam.camera.basecamera.handler.CameraToMainHandler;
import com.troop.freedcam.camera.basecamera.handler.MainToCameraHandler;
import com.troop.freedcam.camera.basecamera.modules.ModuleHandlerAbstract;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameterHandler;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.EventBusLifeCycle;
import com.troop.freedcam.eventbus.models.TextureHolder;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.processor.RenderScriptProcessorInterface;
import com.troop.freedcam.utils.Log;

import org.greenrobot.eventbus.Subscribe;

public abstract class AbstractCameraController<P extends AbstractParameterHandler,C extends CameraHolderAbstract, F extends AbstractFocusHandler> implements CameraInterface, CameraControllerInterface, EventBusLifeCycle
{
    private final String TAG = AbstractCameraController.class.getSimpleName();


    @Subscribe
    public void onTextureHolder(TextureHolder textureHolder)
    {
        this.textureHolder = textureHolder;
    }

    protected RenderScriptManager renderScriptManager;
    public ModuleHandlerAbstract moduleHandler;
    /**
     * parameters for avail for the cameraHolder
     */
    public P parametersHandler;
    /**
     * holds the current camera
     */
    public C cameraHolder;
    /**
     * handels focus releated stuff for the current camera
     */
    public F focusHandler;

    protected boolean PreviewSurfaceRdy;
    protected TextureHolder textureHolder;

    /**
     * holds handler to invoke stuff in ui or camera thread
     */
    protected MainToCameraHandler mainToCameraHandler;
    protected CameraToMainHandler cameraToMainHandler;

    public void init(MainToCameraHandler mainToCameraHandler, CameraToMainHandler cameraToMainHandler)
    {
        Log.d(TAG, "init handler");
        this.mainToCameraHandler = mainToCameraHandler;
        this.cameraToMainHandler = cameraToMainHandler;
    }

    @Override
    public void startCameraAsync() {
        Log.d(TAG, "startCameraAsync");
        if (mainToCameraHandler != null)
            mainToCameraHandler.startCamera();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void stopCameraAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.stopCamera();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void restartCameraAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.restartCamera();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void startPreviewAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.startPreview();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void stopPreviewAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.stopPreview();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void restartPreviewAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.restartPreview();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public RenderScriptManager getRenderScriptManager() {
        return renderScriptManager;
    }

    @Override
    public RenderScriptProcessorInterface getFocusPeakProcessor() {
        return null;
    }

    @Override
    public AbstractFocusHandler getFocusHandler() {
        return focusHandler;
    }

    @Override
    public C getCameraHolder() {
        return cameraHolder;
    }

    @Override
    public P getParameterHandler() {
        return parametersHandler;
    }

    @Override
    public ModuleHandlerAbstract getModuleHandler() {
        return moduleHandler;
    }

    @Override
    public TextureHolder getTextureHolder() {
        return textureHolder;
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
