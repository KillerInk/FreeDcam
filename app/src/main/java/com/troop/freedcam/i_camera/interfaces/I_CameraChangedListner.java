package com.troop.freedcam.i_camera.interfaces;

import com.troop.freedcam.i_camera.modules.I_Module;

/**
 * Created by troop on 17.12.2014.
 */
public interface I_CameraChangedListner
{
    void onCameraOpen(String message);
    void onCameraError(String error);

    void onCameraStatusChanged(String status);
    void onModuleChanged(I_Module module);
}
