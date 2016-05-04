package com.freedcam.apis.i_camera.parameters;

import com.freedcam.apis.i_camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.i_camera.interfaces.I_Module;

/**
 * Created by GeorgeKiarie on 10/31/2015.
 */
public class AbstractCameraChanged implements I_CameraChangedListner {

    @Override
    public void onCameraOpen(String message)
    {

    }

    @Override
    public void onCameraOpenFinish(String message) {

    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message)
    {

    }

    @Override
    public void onPreviewClose(String message)
    {

    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }


    @Override
    public void onModuleChanged(I_Module module)
    {}


}
