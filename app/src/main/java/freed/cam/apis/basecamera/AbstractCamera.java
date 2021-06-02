package freed.cam.apis.basecamera;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.apis.basecamera.modules.ModuleHandlerInterface;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.previewpostprocessing.PreviewControllerInterface;
import freed.settings.SettingsManager;
import freed.utils.Log;

public abstract class AbstractCamera<P extends ParameterHandler,C extends CameraHolderInterface, M extends ModuleHandlerInterface> implements CameraWrapperInterface
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
    public AbstractFocusHandler focusHandler;


    private ActivityInterface activityInterface;
    private PreviewControllerInterface preview;
    protected SettingsManager settingsManager;

    public AbstractCamera()
    {
        settingsManager = FreedApplication.settingsManager();
    }

    public void setPreview(PreviewControllerInterface preview) {
        this.preview = preview;
    }

    @Override
    public PreviewControllerInterface getPreview() {
        return preview;
    }

    public void init(ActivityInterface activityInterface)
    {
        Log.d(TAG, "init handler");
        this.activityInterface = activityInterface;
    }

    @Override
    public boolean isAeMeteringSupported() {
        return focusHandler.isAeMeteringSupported();
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
    public M getModuleHandler() {
        return moduleHandler;
    }

    @Override
    public int getMargineLeft() {
        return getPreview().getPreviewView().getLeft();
    }

    @Override
    public int getMargineRight() {
        return getPreview().getPreviewView().getRight();
    }

    @Override
    public int getMargineTop() {
        return getPreview().getPreviewView().getTop();
    }

    @Override
    public int getPreviewWidth() {
        return getPreview().getPreviewView().getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return getPreview().getPreviewView().getHeight();
    }
}
