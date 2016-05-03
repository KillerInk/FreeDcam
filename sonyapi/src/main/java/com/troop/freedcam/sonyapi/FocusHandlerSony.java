package com.troop.freedcam.sonyapi;

import android.view.MotionEvent;

import com.troop.filelogger.Logger;
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
    private CameraUiWrapperSony cameraUiWrapper;
    private CameraHolderSony cameraHolder;
    private ParameterHandlerSony parametersHandler;
    private static String TAG = FocusHandlerSony.class.getSimpleName();

    private boolean isFocusing = false;
    private boolean isFocusLocked = false;

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
        if (parametersHandler == null && !cameraHolder.isPreviewRunning)
            return;
        if (isFocusing)
        {
            cameraHolder.CancelFocus();
            Logger.d(TAG, "Canceld Focus");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Logger.exception(e);
            }
        }

        double x = rect.left + (rect.right - rect.left)/2  ;
        double y = rect.top + (rect.bottom - rect.top )  /2;
        double xproz = (x / (double)width) * 100;
        double yproz = (y / (double)height) *100;
        Logger.d(TAG, "set focus to: x: " + xproz + " y: " +yproz);
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
    public boolean isAeMeteringSupported() {
        return false;
    }

    @Override
    public boolean isWbMeteringSupported() {
        return false;
    }

    @Override
    public void SetMotionEvent(MotionEvent event) {
        cameraUiWrapper.surfaceView.onTouchEvent(event);
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


