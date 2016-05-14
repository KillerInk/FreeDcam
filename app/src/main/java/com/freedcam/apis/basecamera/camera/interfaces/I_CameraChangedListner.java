package com.freedcam.apis.basecamera.camera.interfaces;

/**
 * Created by troop on 17.12.2014.
 */
public interface I_CameraChangedListner
{
    /**
     * gets thrown when camera starts open
     * @param message
     */
    void onCameraOpen(String message);
    /**
     * gets thrown when camera open has finish
     * @param message
     */
    void onCameraOpenFinish(String message);
    /**
     * gets thrown when camera is closed
     * @param message
     */
    void onCameraClose(String message);
    /**
     * gets thrown when preview is running
     * @param message
     */
    void onPreviewOpen(String message);
    /**
     * gets thrown when preview gets closed
     * @param message
     */
    void onPreviewClose(String message);
    /**
     * gets thrown when camera has a problem
     * @param error to send
     */
    void onCameraError(String error);
    /**
     * gets thrown when camera status changed
     * @param status that has changed
     */
    void onCameraStatusChanged(String status);
    /**
     * gets thrown when current module has changed
     * @param module
     */
    void onModuleChanged(I_Module module);
}
