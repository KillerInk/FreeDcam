package com.troop.freedcam.camera;

import android.graphics.Rect;
import android.hardware.Camera;

import com.troop.freedcam.camera.modules.CameraFocusEvent;
import com.troop.freedcam.camera.modules.I_Callbacks;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractFocusHandler;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusHandler extends AbstractFocusHandler implements I_Callbacks.AutoFocusCallback
{

    private final BaseCameraHolder cameraHolder;
    private final CameraUiWrapper cameraUiWrapper;
    private final AbstractParameterHandler parametersHandler;

    int count;
    List<Camera.Area> areas;
    boolean isFocusing = false;

    public FocusHandler(CameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = cameraUiWrapper.cameraHolder;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
    }

    @Override
    public void onAutoFocus(CameraFocusEvent event)
    {
        //camera.cancelAutoFocus();
        isFocusing = false;
        if (focusEvent != null)
            focusEvent.FocusFinished(event.success);
    }

    @Override
    public void onFocusLock(boolean locked) {

    }

    public void StartFocus()
    {
        if (isFocusing) {
            cameraHolder.StartFocus(this);
            isFocusing =false;
        }

        if (focusEvent != null)
        {
            focusEvent.FocusStarted(null);
        }
        BaseCameraHolder baseCameraHolder = (BaseCameraHolder)cameraHolder;
        baseCameraHolder.StartFocus(this);
        isFocusing = true;
    }

    public void StartTouchToFocus(FocusRect rect, int width, int height)
    {

        String focusmode = parametersHandler.FocusMode.GetValue();
        if (focusmode.equals("auto") || focusmode.equals("macro"))
        {
            if (cameraUiWrapper.camParametersHandler.ExposureLock != null && cameraUiWrapper.camParametersHandler.ExposureLock.IsSupported()) {
                if (cameraUiWrapper.camParametersHandler.ExposureLock.GetValue().equals("true")) {
                    cameraUiWrapper.camParametersHandler.ExposureLock.SetValue("false", true);
                    cameraUiWrapper.camParametersHandler.ExposureLock.BackgroundValueHasChanged("false");
                }
            }
            final FocusRect targetFocusRect = new FocusRect(
                    rect.left * 2000 / width - 1000,
                    rect.right * 2000 / width - 1000,
                    rect.top * 2000 / height - 1000,
                    rect.bottom * 2000 / height - 1000);
            //check if stuff is to big or to small and set it to min max value
            if (targetFocusRect.left < -1000)
            {
                int dif = targetFocusRect.left + 1000;
                targetFocusRect.left = -1000;
                targetFocusRect.right += dif;
            }
            if (targetFocusRect.right > 1000)
            {
                int dif = targetFocusRect.right - 1000;
                targetFocusRect.right = 1000;
                targetFocusRect.left -= dif;
            }
            if (targetFocusRect.top < -1000)
            {
                int dif = targetFocusRect.top + 1000;
                targetFocusRect.top = -1000;
                targetFocusRect.bottom += dif;
            }
            if (targetFocusRect.bottom > 1000)
            {
                int dif = targetFocusRect.bottom -1000;
                targetFocusRect.bottom = 1000;
                targetFocusRect.top -=dif;
            }


            if (targetFocusRect.left >= -1000
                    && targetFocusRect.top >= -1000
                    && targetFocusRect.bottom <= 1000
                    && targetFocusRect.right <= 1000)
            {

                parametersHandler.SetFocusAREA(targetFocusRect, 300);


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (cameraHolder != null)
                    cameraHolder.StartFocus(this);
                isFocusing = true;
                if (focusEvent != null)
                    focusEvent.FocusStarted(rect);
            }
        }

        /*count = 0;
        Camera.Parameters para = cameraHolder.GetCameraParameters();
        para.set("zsl", "on");
        para.set("af-bracket", "af-bracket-on");
        areas = para.getFocusAreas();
        para.setFocusAreas(null);
        cameraHolder.GetCamera().setParameters(para);
        para = cameraHolder.GetCameraParameters();
        cameraHolder.GetCamera().cancelAutoFocus();
        cameraHolder.GetCamera().takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                int i = data.length;
            }
        });*/

    }
}
