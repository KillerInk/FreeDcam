package com.troop.freedcam.camera.basecamera;

import android.location.Location;

import com.troop.freedcam.camera.basecamera.cameraholder.CameraHolderAbstract;
import com.troop.freedcam.camera.basecamera.cameraholder.CameraHolderInterface;
import com.troop.freedcam.camera.basecamera.modules.ModuleHandlerAbstract;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameterHandler;
import com.troop.freedcam.eventbus.models.TextureHolder;
import com.troop.freedcam.file.FileListController;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.processor.RenderScriptProcessorInterface;
import com.troop.freedcam.utils.PermissionManager;

public interface CameraObjects<P extends AbstractParameterHandler
        ,C extends CameraHolderAbstract
        , F extends AbstractFocusHandler
        ,M extends ModuleHandlerAbstract> {
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
    F getFocusHandler();
    RenderScriptProcessorInterface getFocusPeakProcessor();
    RenderScriptManager getRenderScriptManager();
    void setRenderScriptManager(RenderScriptManager renderScriptManager);
    void setPermissionManager(PermissionManager permissionManager);
    void setFileListController(FileListController fileListController);
    TextureHolder getTextureHolder();
    PermissionManager getPermissionManager();
    FileListController getFileListController();
    int getDeviceOrientation();
    Location getCurrentLocation();
}
