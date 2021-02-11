package freed.cam.apis.basecamera;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.previewpostprocessing.PreviewControllerInterface;

public interface CameraObjects {
    /**
     * Get the current active CameraHolder
     * @return
     */
    CameraHolderInterface getCameraHolder();

    /**
     * get the active parameterhandler
     * @return
     */
    AbstractParameterHandler getParameterHandler();
    ModuleHandlerAbstract getModuleHandler();
    AbstractFocusHandler getFocusHandler();
    PreviewControllerInterface getPreview();
    ActivityInterface getActivityInterface();
}
