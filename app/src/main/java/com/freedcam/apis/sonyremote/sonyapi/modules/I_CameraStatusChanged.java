package com.freedcam.apis.sonyremote.sonyapi.modules;

/**
 * Created by troop on 31.01.2015.
 * This interface is used to listen on Camera status updates like idel/stillcapture etc
 */
public interface I_CameraStatusChanged
{
    void onCameraStatusChanged(String status);
}
