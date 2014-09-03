package com.troop.freecamv2.camera;

import android.graphics.Rect;
import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.CamParametersHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusHandler implements Camera.AutoFocusCallback
{

    private final BaseCameraHolder cameraHolder;
    private final CameraUiWrapper cameraUiWrapper;
    private final CamParametersHandler parametersHandler;
    public  I_Focus focusEvent;

    public FocusHandler(CameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = cameraUiWrapper.cameraHolder;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {
        if (focusEvent != null)
            focusEvent.FocusFinished(success);
    }

    public void StartFocus()
    {
        if (focusEvent != null)
        {
            focusEvent.FocusStarted(null);
        }

        cameraHolder.StartFocus(this);
    }

    public void StartTouchToFocus(Rect rect, int width, int height)
    {
        final Rect targetFocusRect = new Rect(
                rect.left * 2000/width - 1000,
                rect.top * 2000/height - 1000,
                rect.right * 2000/width - 1000,
                rect.bottom * 2000/height - 1000);
        Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
        final List<Camera.Area> meteringList = new ArrayList<Camera.Area>();
        meteringList.add(focusArea);
        parametersHandler.SetFocusAREA(meteringList);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cameraHolder.StartFocus(this);
        if (focusEvent != null)
            focusEvent.FocusStarted(rect);

    }
}
