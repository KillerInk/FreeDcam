package com.troop.freedcam.camera2;

import android.graphics.Rect;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractFocusHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 12.12.2014.
 */
public class FocusHandlerApi2 extends AbstractFocusHandler
{

    private final BaseCameraHolderApi2 cameraHolder;
    private final CameraUiWrapperApi2 cameraUiWrapper;
    private final AbstractParameterHandler parametersHandler;

    public FocusHandlerApi2(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = (CameraUiWrapperApi2) cameraUiWrapper;
        this.cameraHolder = (BaseCameraHolderApi2) cameraUiWrapper.cameraHolder;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
    }
    @Override
    public void StartFocus() {
        super.StartFocus();
    }

    @Override
    public void StartTouchToFocus(Rect rect, int width, int height) {
        super.StartTouchToFocus(rect, width, height);
    }
}
