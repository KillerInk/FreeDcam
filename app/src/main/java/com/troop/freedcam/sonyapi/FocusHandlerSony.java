package com.troop.freedcam.sonyapi;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.CameraFocusEvent;
import com.troop.freedcam.camera.modules.I_Callbacks;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.AbstractFocusHandler;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;

/**
 * Created by troop on 31.01.2015.
 */
public class FocusHandlerSony extends AbstractFocusHandler implements I_Callbacks.AutoFocusCallback
{
    CameraUiWrapperSony cameraUiWrapper;
    CameraHolderSony cameraHolder;
    ParameterHandlerSony parametersHandler;

    boolean isFocusing = false;

    public FocusHandlerSony(CameraUiWrapperSony cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = cameraUiWrapper.cameraHolder;
        this.parametersHandler = (ParameterHandlerSony)cameraUiWrapper.camParametersHandler;
    }

    @Override
    public void StartFocus() {
        super.StartFocus();
    }

    @Override
    public void StartTouchToFocus(FocusRect rect, int width, int height)
    {
        int x = (rect.left + (rect.right - rect.left))  /2;
        int y = rect.top + (rect.bottom + rect.top )  /2;
        double xproz = x / (width/100);
        double yproz = y / (height/100);
        cameraHolder.StartFocus(this);
        cameraHolder.SetTouchFocus(xproz, yproz);
        //super.StartTouchToFocus(rect, width, height);
    }


    @Override
    public void onAutoFocus(CameraFocusEvent event)
    {
        //camera.cancelAutoFocus();
        isFocusing = false;
        if (focusEvent != null)
            focusEvent.FocusFinished(event.success);
    }
}


