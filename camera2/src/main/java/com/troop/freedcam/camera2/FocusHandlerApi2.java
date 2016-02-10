package com.troop.freedcam.camera2;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera2.modules.PictureModuleApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractFocusHandler;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FocusHandlerApi2 extends AbstractFocusHandler implements I_ParametersLoaded
{

    private final BaseCameraHolderApi2 cameraHolder;
    private final CameraUiWrapperApi2 cameraUiWrapper;
    private final ParameterHandlerApi2 parametersHandler;
    private int mState;
    private FocusRect focusRect;
    private boolean focusenabled = false;



    private final String TAG = FocusHandlerApi2.class.getSimpleName();

    public FocusHandlerApi2(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = (CameraUiWrapperApi2) cameraUiWrapper;
        this.cameraHolder = (BaseCameraHolderApi2) cameraUiWrapper.cameraHolder;
        this.parametersHandler = (ParameterHandlerApi2) cameraUiWrapper.camParametersHandler;
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
        public void onVisibilityChanged() {

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
        Log.d(TAG, "Width:"+width +"Height"+height);
        if (!focusenabled)
            return;
        focusRect = rect;
        Rect m = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        logRect(m);
        final FocusRect targetFocusRect = new FocusRect(
                rect.left * m.right /width,
                rect.right * m.right /width,
                rect.top * m.bottom /height,
                rect.bottom * m.bottom / height);
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
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, mre);
        lockFocus();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try
        {
            // This is how to tell the camera to lock focus.
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            //mState = PictureModuleApi2.STATE_WAITING_LOCK;
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
            if (focusEvent != null)
                focusEvent.FocusStarted(focusRect);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is finished.
     */
    private void unlockFocus() {
        try {
            Log.d(TAG, "CaptureDone Unlock Focus");
            // Reset the autofucos trigger
            //cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            // After this, the camera will go back to the normal state of preview.
            mState = PictureModuleApi2.STATE_PREVIEW;
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

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
        public void onVisibilityChanged() {

        }
    };

    private void lockAE() {
        try {
            Log.d(TAG, "Run Precapture");
            // This is how to tell the camera to trigger.
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            //mState = PictureModuleApi2.STATE_WAITING_PRECAPTURE;
            cameraHolder.mCaptureSession.capture(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

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
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, mre);
        lockAE();
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
        public void onVisibilityChanged() {

        }
    };

    @Override
    public void SetAwbAreas(FocusRect rect)
    {
        /*cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_LOCK, false);
        try {
            cameraHolder.mCaptureSession.capture(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
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
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_REGIONS, mre);
        lockAE();

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
