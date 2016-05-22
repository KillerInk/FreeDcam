package com.freedcam.apis.camera2.camera;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;
import android.view.MotionEvent;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.AbstractFocusHandler;
import com.freedcam.apis.basecamera.camera.FocusRect;
import com.freedcam.apis.basecamera.camera.parameters.I_ParametersLoaded;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FocusHandlerApi2 extends AbstractFocusHandler implements I_ParametersLoaded
{

    private final CameraHolderApi2 cameraHolder;
    private int mState;
    private FocusRect focusRect;
    private boolean focusenabled = false;



    private final String TAG = FocusHandlerApi2.class.getSimpleName();

    public FocusHandlerApi2(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraHolder = (CameraHolderApi2) cameraUiWrapper.cameraHolder;
    }

    public AbstractModeParameter.I_ModeParameterEvent focusModeListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            if (val.contains("continous")|| val.equals("off"))
            {
                focusenabled = false;
                if (focusEvent != null)
                    focusEvent.TouchToFocusSupported(false);
            }
            else
            {
                focusenabled = true;
                if (focusEvent != null)
                    focusEvent.TouchToFocusSupported(true);
            }
        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

    @Override
    public void StartFocus() {
        super.StartFocus();
    }

    @Override
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height)
    {
        logFocusRect(rect);
        Logger.d(TAG, "Width:" + width + "Height" + height);
        if (!focusenabled)
            return;
        focusRect = rect;
        Rect m = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        logRect(m);
        final FocusRect targetFocusRect = new FocusRect(
                rect.left * m.right /width,
                rect.right * m.right /width,
                rect.top * m.bottom /height,
                rect.bottom * m.bottom / height,rect.x,rect.y);
        logFocusRect(targetFocusRect);
        if (targetFocusRect.left < m.left)
            targetFocusRect.left = m.left;
        if (targetFocusRect.right > m.right)
            targetFocusRect.right = m.right;
        if (targetFocusRect.top < m.top)
            targetFocusRect.top = m.top;
        if (targetFocusRect.bottom > m.bottom)
            targetFocusRect.bottom = m.bottom;
        logFocusRect(targetFocusRect);
        MeteringRectangle rectangle = new MeteringRectangle(targetFocusRect.left,targetFocusRect.top,targetFocusRect.right,targetFocusRect.bottom, 1000);
        MeteringRectangle[] mre = { rectangle};
        cameraHolder.SetFocusArea(CaptureRequest.CONTROL_AF_REGIONS, mre);
        if (focusEvent != null)
            focusEvent.FocusStarted(focusRect);
    }

    public AbstractModeParameter.I_ModeParameterEvent aeModeListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            if (val.equals("off"))
            {
                if (focusEvent != null)
                    focusEvent.AEMeteringSupported(false);
            }
            else {
                if (focusEvent != null)
                    focusEvent.AEMeteringSupported(true);
            }

        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

    @Override
    public void SetMeteringAreas(FocusRect rect, int width, int height)
    {
        Rect m = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        if (rect.left < m.left)
            rect.left = m.left;
        if (rect.right > m.right)
            rect.right = m.right;
        if (rect.top < m.top)
            rect.top = m.top;
        if (rect.bottom > m.bottom)
            rect.bottom = m.bottom;
        MeteringRectangle rectangle = new MeteringRectangle(rect.left,rect.top,rect.right,rect.bottom, 1000);
        MeteringRectangle[] mre = { rectangle};
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_REGIONS, mre);
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
    }

    public AbstractModeParameter.I_ModeParameterEvent awbModeListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val) {
            if (val.equals("OFF"))
            {
                if (focusEvent != null)
                    focusEvent.AWBMeteringSupported(false);
            }
            else {
                if (focusEvent != null)
                    focusEvent.AWBMeteringSupported(true);
            }
        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

    @Override
    public void SetAwbAreas(FocusRect rect, int width, int height)
    {
        /*cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_LOCK, false);
        try {
            cameraHolder.mCaptureSession.capture(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.cameraBackroundValuesChangedListner,
                    null);
        } catch (CameraAccessException e) {
            Logger.exception(e);
        }*/
        Rect m = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        if (rect.left < m.left)
            rect.left = m.left;
        if (rect.right > m.right)
            rect.right = m.right;
        if (rect.top < m.top)
            rect.top = m.top;
        if (rect.bottom > m.bottom)
            rect.bottom = m.bottom;
        MeteringRectangle rectangle = new MeteringRectangle(rect.left,rect.top,rect.right,rect.bottom, 1000);
        MeteringRectangle[] mre = { rectangle};
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AWB_REGIONS, mre);
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);

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

    }

    @Override
    public void ParametersLoaded()
    {
        if (focusEvent == null
                || cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE) == null
                || cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB) == null)
            return;
        if (cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB)> 0)
            focusEvent.AWBMeteringSupported(true);
        else
            focusEvent.AWBMeteringSupported(false);
        if (cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)>0)
            focusEvent.AEMeteringSupported(true);
        else
            focusEvent.AEMeteringSupported(false);
    }
}
