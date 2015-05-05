package com.troop.freedcam.camera2;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
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
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height) {
        super.StartTouchToFocus(rect, null, width, height);
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
                case PictureModuleApi2.STATE_PREVIEW:
                {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case PictureModuleApi2.STATE_WAITING_LOCK:
                {
                    Log.d(TAG, "STATE WAITING LOCK");
                    int afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED || aeState == CaptureResult.CONTROL_AE_STATE_LOCKED)
                        {
                            mState = PictureModuleApi2.STATE_PICTURE_TAKEN;
                            //captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case PictureModuleApi2.STATE_WAITING_PRECAPTURE:
                {
                    Log.d(TAG, "STATE WAITING PRECAPTURE");
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = PictureModuleApi2.STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case PictureModuleApi2.STATE_WAITING_NON_PRECAPTURE:
                {
                    Log.d(TAG, "STATE WAITING NON PRECAPTURE");
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = PictureModuleApi2.STATE_PICTURE_TAKEN;
                        //captureStillPicture();
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
