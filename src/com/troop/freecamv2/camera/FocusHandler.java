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
    int count;
    List<Camera.Area> areas;

    public FocusHandler(CameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = cameraUiWrapper.cameraHolder;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {

        //camera.cancelAutoFocus();
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
        if (parametersHandler.FocusMode.GetValue().equals("auto")
                || parametersHandler.FocusMode.GetValue().equals("macro")
                || parametersHandler.FocusMode.GetValue().equals("normal"))
        {
            if (parametersHandler.FocusMode.GetValue().equals("normal"))
            {
                parametersHandler.FocusMode.SetValue("auto");
            }
            final Rect targetFocusRect = new Rect(
                    rect.left * 2000 / width - 1000,
                    rect.top * 2000 / height - 1000,
                    rect.right * 2000 / width - 1000,
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
                    && targetFocusRect.right <= 1000) {
                Camera.Area focusArea = new Camera.Area(targetFocusRect, 900);
                final List<Camera.Area> meteringList = new ArrayList<Camera.Area>();
                meteringList.add(focusArea);
                parametersHandler.SetFocusAREA(meteringList);

                cameraHolder.StartFocus(this);
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
