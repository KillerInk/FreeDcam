package freed.cam.apis.basecamera;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerInterface;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.previewpostprocessing.PreviewControllerInterface;

public interface CameraObjects<C extends CameraHolderInterface, P extends ParameterHandler, M extends ModuleHandlerInterface> {
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
    M getModuleHandler();
    AbstractFocusHandler getFocusHandler();
}
