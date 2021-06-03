package freed.cam.apis.basecamera;

import javax.inject.Inject;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.apis.basecamera.modules.ModuleHandlerInterface;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.previewpostprocessing.PreviewControllerInterface;
import freed.settings.SettingsManager;
import freed.utils.Log;

public abstract class AbstractCamera<P extends ParameterHandler,C extends CameraHolderInterface, M extends ModuleHandlerInterface,F extends AbstractFocusHandler> implements CameraWrapperInterface
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
        preview = CameraFragmentAbstract.getPreviewController();
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

    @Override
    public int getMargineLeft() {
        return preview.getPreviewView().getLeft();
    }

    @Override
    public int getMargineRight() {
        return preview.getPreviewView().getRight();
    }

    @Override
    public int getMargineTop() {
        return preview.getPreviewView().getTop();
    }

    @Override
    public int getPreviewWidth() {
        return preview.getPreviewView().getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return preview.getPreviewView().getHeight();
    }
}
