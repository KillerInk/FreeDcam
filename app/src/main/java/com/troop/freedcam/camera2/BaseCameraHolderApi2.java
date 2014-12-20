package com.troop.freedcam.camera2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
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
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.ui.TextureView.AutoFitTextureView;

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

    public CameraManager manager;
    public CameraDevice mCameraDevice;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    AutoFitTextureView textureView;

    //this is needed for the preview...
    public CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    public CameraCaptureSession mCaptureSession;
    public StreamConfigurationMap map;

    private ImageReader mImageReader;
    private CaptureRequest mPreviewRequest;

    public int CurrentCamera;
    Size preview;
    public CameraCharacteristics characteristics;
    public Surface surface;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseCameraHolderApi2(Context context,I_CameraChangedListner cameraChangedListner,HandlerThread backGroundThread, Handler backGroundHandler, Handler UIHandler)
    {
        super(cameraChangedListner,backGroundThread, backGroundHandler, UIHandler);
        this.context = context;
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean OpenCamera(int camera)
    {
        //startBackgroundThread();
        CurrentCamera = camera;
        String cam = camera +"";
        try
        {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cam, mStateCallback, backGroundHandler);
            characteristics = manager.getCameraCharacteristics(CurrentCamera+"");
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
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
        //stopBackgroundThread();
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

    public boolean SetSurface(TextureView surfaceHolder)
    {
        this.textureView = (AutoFitTextureView) surfaceHolder;
        return true;
    }

    @Override
    public void StartPreview()
    {

        try {
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());

            preview = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    textureView.getWidth(), textureView.getHeight(), largest);
            textureView.setAspectRatio(preview.getWidth(), preview.getHeight());
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(preview.getWidth(),preview.getHeight());
            configureTransform(textureView.getWidth(), textureView.getHeight());
            surface = new Surface(texture);



        // We set up a CaptureRequest.Builder with the output Surface.

            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            if (mImageReader == null)
                mCameraDevice.createCaptureSession(Arrays.asList(surface),previewStateCallBack, null);
            else
                mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),previewStateCallBack, null);


        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
            return;
        }

    }

    CameraCaptureSession.StateCallback previewStateCallBack = new CameraCaptureSession.StateCallback()
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
                        mCaptureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {

        }
    };

    public void setIntKeyToCam(CaptureRequest.Key<Integer> key, int value)
    {
        if (mCaptureSession != null)
        {
            //StopPreview();
            try {
                mPreviewRequestBuilder.set(key, value);
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback,
                        null);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        else
        {
            mPreviewRequestBuilder.set(key, value);
        }
    }


    private void configureTransform(int viewWidth, int viewHeight) {

        DisplayManager windowManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
        int rotation = windowManager.getDisplay(0).getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, preview.getHeight(), preview.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / preview.getHeight(),
                    (float) viewWidth / preview.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    public CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result)
        {

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


    CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;


            UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen("");
                }
            });

            ((ParameterHandlerApi2)ParameterHandler).Init();
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

    public void SetImageReader(ImageReader imageReader)
    {
        this.mImageReader = imageReader;
        StopPreview();
        StartPreview();
    }
}
