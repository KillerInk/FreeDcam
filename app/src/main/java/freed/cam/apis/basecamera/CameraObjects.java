package freed.cam.apis.basecamera;

import android.view.SurfaceView;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.renderscript.RenderScriptManager;
import freed.renderscript.RenderScriptProcessorInterface;

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
    RenderScriptProcessorInterface getFocusPeakProcessor();
    RenderScriptManager getRenderScriptManager();
    ActivityInterface getActivityInterface();
}
