package com.troop.freedcam.camera2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.I_error;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.I_CameraHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BaseCameraHolderApi2 extends AbstractCameraHolder
{
    final static String TAG = "freedcam.BaseCameraHolderApi2";

    Context context;
    public I_error errorHandler;
    private Handler mBackgroundHandler;
    CameraManager manager;
    private CameraDevice mCameraDevice;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    //this is needed for the preview...
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    private ImageReader mImageReader;
    private CaptureRequest mPreviewRequest;

    public int CurrentCamera;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseCameraHolderApi2(Context context)
    {
        this.context = context;
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean OpenCamera(int camera)
    {
        CurrentCamera = camera;
        String cam = camera +"";
        try
        {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cam, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return  false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void CloseCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            /*if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }*/
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    @Override
    public Camera GetCamera() {
        return null;
    }

    @Override
    public int CameraCout() {
        return CameraCountId().length;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String[] CameraCountId()
    {
        try {
            return manager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean IsRdy() {
        return super.IsRdy();
    }

    @Override
    public boolean SetCameraParameters(Camera.Parameters parameters) {
        return  false;
    }

    @Override
    public boolean SetSurface(SurfaceHolder surfaceHolder)
    {
        this.surfaceHolder = surfaceHolder;
        return true;
    }

    @Override
    public void StartPreview()
    {

        try {
            CameraCharacteristics characteristics
                    = manager.getCameraCharacteristics(CurrentCamera+"");

            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());

            Size preview = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    960, 720, largest);
            surfaceHolder.setFixedSize(preview.getWidth(),preview.getHeight());
            //surfaceHolder.setFormat();


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        // We set up a CaptureRequest.Builder with the output Surface.
        try
        {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surfaceHolder.getSurface());

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface()),
                    new CameraCaptureSession.StateCallback()
                    {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession)
                        {
                            // The camera is already closed
                            if (null == mCameraDevice)
                            {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
                        {
                        }
                    }, null
            );
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
            return;
        }
    }

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {

        }
    };

    @Override
    public void StopPreview()
    {
        if (mCaptureSession != null)
            mCaptureSession.close();
    }

    public Camera.Parameters GetCameraParameters() {
        return null;
    }


    public void TakePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback picture) {
        //super.TakePicture(shutter, raw, picture);
    }

    public void SetPreviewCallback(Camera.PreviewCallback previewCallback) {
        //super.SetPreviewCallback(previewCallback);
    }

    public void StartFocus(Camera.AutoFocusCallback autoFocusCallback) {
        //super.StartFocus(autoFocusCallback);
    }



    CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            StartPreview();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;

        }
    };

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}
