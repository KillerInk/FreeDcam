package freed.cam.apis.basecamera;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.modules.ModuleHandlerInterface;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.event.camera.CameraHolderEventHandler;
import freed.cam.event.capture.CaptureStateChangedEventHandler;
import freed.cam.event.module.ModuleChangedEventHandler;
import freed.cam.previewpostprocessing.PreviewControllerInterface;
import freed.settings.SettingsManager;

public abstract class AbstractCamera<
        P extends ParameterHandler,
        C extends CameraHolderInterface,
        M extends ModuleHandlerInterface,
        F extends AbstractFocusHandler>
        implements CameraWrapperInterface
{
    private static final String TAG = AbstractCamera.class.getSimpleName();
    public M moduleHandler;
    /**
     * parameters for avail for the cameraHolder
     */
    protected P parametersHandler;
    /**
     * holds the current camera
     */
    protected C cameraHolder;
    /**
     * handels focus releated stuff for the current camera
     */
    public F focusHandler;

    protected PreviewControllerInterface preview;
    protected SettingsManager settingsManager;

    public AbstractCamera()
    {
        settingsManager = FreedApplication.settingsManager();
        preview = ActivityFreeDcamMain.previewController();
    }

    @Override
    public void setCaptureStateChangedEventHandler(CaptureStateChangedEventHandler eventHandler)
    {
        moduleHandler.setCaptureStateChangedEventHandler(eventHandler);
    }

    @Override
    public void setCameraHolderEventHandler(CameraHolderEventHandler cameraHolderEventHandler) {

        cameraHolder.addEventListner(cameraHolderEventHandler);
    }

    @Override
    public void setModuleChangedEventHandler(ModuleChangedEventHandler moduleChangedEventHandler) {
        moduleHandler.setModuleChangedEventHandler(moduleChangedEventHandler);
    }

    @Override
    public boolean isAeMeteringSupported() {
        return focusHandler.isAeMeteringSupported();
    }

    @Override
    public F getFocusHandler() {
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
    public M getModuleHandler() {
        return moduleHandler;
    }
}
