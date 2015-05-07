package com.troop.freedcam.camera2;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera2.modules.PictureModuleApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractFocusHandler;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FocusHandlerApi2 extends AbstractFocusHandler
{

    private final BaseCameraHolderApi2 cameraHolder;
    private final CameraUiWrapperApi2 cameraUiWrapper;
    private final AbstractParameterHandler parametersHandler;
    int mState;
    FocusRect focusRect;

    final String TAG = FocusHandlerApi2.class.getSimpleName();

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
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height)
    {
        focusRect = rect;
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
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = PictureModuleApi2.STATE_WAITING_LOCK;
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), CaptureCallback,
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
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            // After this, the camera will go back to the normal state of preview.
            mState = PictureModuleApi2.STATE_PREVIEW;
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    CameraCaptureSession.CaptureCallback CaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {

                case PictureModuleApi2.STATE_WAITING_LOCK:
                {
                    Log.d(TAG, "STATE WAITING LOCK");
                    int afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState)
                    {
                        if (focusEvent != null)
                            focusEvent.FocusFinished(true);

                    }
                    else if (CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState)
                    {
                        if (focusEvent != null)
                            focusEvent.FocusFinished(false);
                    }
                    break;
                }

            }
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                        CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            process(result);


        }


    };

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when we
     * get a response in {@link #CaptureCallback} from {@link #lockFocus()}.
     */

    private void runPrecaptureSequence() {
        try {
            Log.d(TAG, "Run Precapture");
            // This is how to tell the camera to trigger.
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = PictureModuleApi2.STATE_WAITING_PRECAPTURE;
            cameraHolder.mCaptureSession.capture(cameraHolder.mPreviewRequestBuilder.build(), CaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
