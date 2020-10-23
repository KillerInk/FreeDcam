package com.troop.freedcam.camera.basecamera;

import android.location.Location;

import com.troop.freedcam.camera.basecamera.cameraholder.CameraHolderInterface;
import com.troop.freedcam.camera.basecamera.modules.ModuleHandlerAbstract;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameterHandler;
import com.troop.freedcam.eventbus.models.TextureHolder;
import com.troop.freedcam.file.FileListController;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.processor.RenderScriptProcessorInterface;
import com.troop.freedcam.utils.PermissionManager;

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
    TextureHolder getTextureHolder();
    PermissionManager getPermissionManager();
    FileListController getFileListController();
    int getDeviceOrientation();
    Location getCurrentLocation();
}
