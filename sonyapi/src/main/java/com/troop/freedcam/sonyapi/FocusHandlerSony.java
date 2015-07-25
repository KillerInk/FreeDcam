package com.troop.freedcam.sonyapi;

import android.util.Log;

import com.troop.freedcam.i_camera.AbstractFocusHandler;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.modules.CameraFocusEvent;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;

/**
 * Created by troop on 31.01.2015.
 */
public class FocusHandlerSony extends AbstractFocusHandler implements I_Callbacks.AutoFocusCallback
{
    CameraUiWrapperSony cameraUiWrapper;
    CameraHolderSony cameraHolder;
    ParameterHandlerSony parametersHandler;
    private static String TAG = FocusHandlerSony.class.getSimpleName();

    boolean isFocusing = false;
    boolean isFocusLocked = false;

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
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height)
    {
        if (parametersHandler == null)
            return;
        if (parametersHandler.ObjectTracking.GetValue().equals("On"))
        {

        }
        if (isFocusing)
        {
            cameraHolder.CancelFocus();
            Log.d(TAG,"Canceld Focus");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        double x = rect.left + (rect.right - rect.left)/2  ;
        double y = rect.top + (rect.bottom - rect.top )  /2;
        double xproz = (x / (double)width) * 100;
        double yproz = (y / (double)height) *100;
        Log.d(TAG, "set focus to: x: " + xproz + " y: " +yproz);
        cameraHolder.StartFocus(this);
        cameraHolder.SetTouchFocus(xproz, yproz);
        isFocusing = true;
        if (focusEvent != null)
            focusEvent.FocusStarted(rect);
        //super.StartTouchToFocus(rect, width, height);
    }

    @Override
    public void SetAwbAreas(FocusRect awbRect, int width, int height) {

    }


    @Override
    public void onAutoFocus(CameraFocusEvent event)
    {
        //camera.cancelAutoFocus();
        isFocusing = false;
        if (focusEvent != null) {
            focusEvent.FocusFinished(event.success);
            focusEvent.FocusLocked(cameraHolder.canCancelFocus());
        }

    }

    @Override
    public void onFocusLock(boolean locked) {
        isFocusLocked = locked;
        if (focusEvent != null) {
            focusEvent.FocusLocked(locked);
        }
    }


}


