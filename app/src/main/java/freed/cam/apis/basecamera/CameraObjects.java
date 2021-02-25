package freed.cam.apis.basecamera;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.previewpostprocessing.PreviewControllerInterface;

public interface CameraObjects<C extends CameraHolderInterface, P extends ParameterHandler> {
    /**
     * Get the current active CameraHolder
     * @return
     */
    C getCameraHolder();

    /**
     * get the active parameterhandler
     * @return
     */
    P getParameterHandler();
    ModuleHandlerAbstract getModuleHandler();
    AbstractFocusHandler getFocusHandler();
    PreviewControllerInterface getPreview();
    ActivityInterface getActivityInterface();
}
