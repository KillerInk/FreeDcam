package freed.cam.apis.camera2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 16.03.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureSessionHandler
{
    private final String TAG = CaptureSessionHandler.class.getSimpleName();
    private final List<Surface> surfaces;
    public final Point displaySize;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest.Builder mImageCaptureRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private Camera2Fragment cameraUiWrapper;
    private CameraHolderApi2 cameraHolderApi2;
    private CameraCaptureSession.CaptureCallback cameraBackroundValuesChangedListner;
    private CaptureEvent waitForRdyCallback;
    private boolean isHighSpeedSession = false;


    private boolean captureSessionRdy = false;

    public interface CaptureEvent
    {
        void onRdy();
    }

    CameraCaptureSession.StateCallback previewStateCallBackRestart = new CameraCaptureSession.StateCallback()
    {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            captureSessionRdy = false;
            Log.d(TAG, "onConfigured()");
            // The camera is already closed
            if (null == cameraHolderApi2.mCameraDevice)
            {
                return;
            }
            // When the session is ready, we start displaying the previewSize.
            mCaptureSession = cameraCaptureSession;

            try {
                // Finally, we start displaying the camera preview.
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(),
                        cameraBackroundValuesChangedListner, null);
            } catch (CameraAccessException | IllegalStateException e) {
                mCaptureSession =null;
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "onConfigureFailed()");
            captureSessionRdy = false;
        }

        @Override
        public void onReady(@NonNull CameraCaptureSession session) {
            super.onReady(session);
            captureSessionRdy = true;
            Log.d(TAG, "onReady()");
            Log.d(TAG, "waitforCallBack:" + (waitForRdyCallback != null));
            if (waitForRdyCallback != null){
                waitForRdyCallback.onRdy();
                waitForRdyCallback = null;
            }
        }

        @Override
        public void onClosed(@NonNull CameraCaptureSession session) {
            captureSessionRdy = false;
            super.onClosed(session);
            Log.d(TAG, "onClosed()");
        }

        @Override
        public void onActive(@NonNull CameraCaptureSession session) {
            captureSessionRdy = false;
            super.onActive(session);
            Log.d(TAG, "onActive()");
        }

        @Override
        public void onSurfacePrepared(@NonNull CameraCaptureSession session, @NonNull Surface surface) {
            super.onSurfacePrepared(session, surface);
            Log.d(TAG,"onSurfacePrepared");
        }
    };


    public boolean IsCaptureSessionRDY()
    {
        return captureSessionRdy;
    }

    public CaptureSessionHandler(Camera2Fragment cameraUiWrapper,CameraCaptureSession.CaptureCallback cameraBackroundValuesChangedListner)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolderApi2 = (CameraHolderApi2) cameraUiWrapper.cameraHolder;
        this.cameraBackroundValuesChangedListner = cameraBackroundValuesChangedListner;
        surfaces = new ArrayList<>();
        Display display = ((WindowManager) cameraUiWrapper.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        displaySize = new Point();
        display.getRealSize(displaySize);
    }

    public Point getDisplaySize()
    {
        return displaySize;
    }

    public void SetCaptureSession(CameraCaptureSession cameraCaptureSession)
    {
        mCaptureSession = cameraCaptureSession;
    }



    public void CreatePreviewRequestBuilder()
    {
        if (cameraHolderApi2 == null || cameraHolderApi2.mCameraDevice == null)
            return;
        try {
            mPreviewRequestBuilder = cameraHolderApi2.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void createImageCaptureRequestBuilder()
    {
        if (cameraHolderApi2 == null || cameraHolderApi2.mCameraDevice == null)
            return;
        try {
            mImageCaptureRequestBuilder = cameraHolderApi2.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void setImageCaptureSurface(Surface surface)
    {
        mImageCaptureRequestBuilder.addTarget(surface);
    }

    public CameraCaptureSession GetActiveCameraCaptureSession()
    {
        return mCaptureSession;
    }

    public SurfaceTexture getSurfaceTexture()
    {
        return cameraHolderApi2.textureView.getSurfaceTexture();
    }

    public void AddSurface(Surface surface, boolean addtoPreviewRequestBuilder)
    {
        Log.d(TAG, "AddSurface");
        if (surfaces.contains(surface))
            return;
        surfaces.add(surface);
        if (addtoPreviewRequestBuilder)
        {
            mPreviewRequestBuilder.addTarget(surface);
        }
    }

    public void RemoveSurface(Surface surface)
    {
        Log.d(TAG, "RemoveSurface");
        if (surfaces.contains(surface))
            surfaces.remove(surface);
        mPreviewRequestBuilder.removeTarget(surface);

    }


    public void Clear()
    {
        Log.d(TAG, "Clear");
        try
        {
            if (null != mCaptureSession)
            {
                mCaptureSession.stopRepeating();
                mCaptureSession.abortCaptures();
                mCaptureSession.close();
                mCaptureSession = null;
            }
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }
        if (mPreviewRequestBuilder != null)
            for (Surface s: surfaces)
                mPreviewRequestBuilder.removeTarget(s);
        if (mImageCaptureRequestBuilder != null)
            for (Surface s: surfaces)
                mImageCaptureRequestBuilder.removeTarget(s);
        surfaces.clear();

    }

    public void CreateCaptureSession()
    {
        if (mCaptureSession != null)
            mCaptureSession.close();
        if(cameraHolderApi2.mCameraDevice == null)
            return;
        isHighSpeedSession = false;
        Log.d(TAG, "CreateCaptureSession:");
        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForFirstFrame();
        try {
            cameraHolderApi2.mCameraDevice.createCaptureSession(surfaces, previewStateCallBackRestart, null);
        } catch (CameraAccessException | SecurityException ex) {
            Log.WriteEx(ex);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void CreateHighSpeedCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
        if (mCaptureSession != null)
            mCaptureSession.close();
        if(cameraHolderApi2.mCameraDevice == null)
            return;
        isHighSpeedSession = true;
        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForFirstFrame();
        Log.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
        try {
            cameraHolderApi2.mCameraDevice.createConstrainedHighSpeedCaptureSession(surfaces, customCallback, null);
        } catch (CameraAccessException | SecurityException ex) {
            Log.WriteEx(ex);
        }
    }

    public void CreateCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
        if (mCaptureSession != null)
            mCaptureSession.close();
        isHighSpeedSession = false;
        Log.d(TAG, "CreateCaptureSessionWITHCustomCallback: Surfaces Count:" + surfaces.size());
        try {
            cameraHolderApi2.mCameraDevice.createCaptureSession(surfaces, customCallback, null);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void StopRepeatingCaptureSession()
    {
        Log.d(TAG, "StopRepeatingCaptureSession");
        if (mCaptureSession != null)
            try {
                mCaptureSession.stopRepeating();
            } catch (CameraAccessException | java.lang.SecurityException ex) {
                Log.WriteEx(ex);
                mCaptureSession = null;
            }
            catch (IllegalStateException ex)
            {
                Log.WriteEx(ex);
                mCaptureSession = null;
            }
    }

    public void StopRepeatingCaptureSession(CaptureEvent event)
    {
        this.waitForRdyCallback = event;
        StopRepeatingCaptureSession();
    }

    public void CancelRepeatingCaptureSession(CaptureEvent event)
    {
        this.waitForRdyCallback = event;
        Log.d(TAG, "CancelRepeatingCaptureSession waitforCallback:" + ( waitForRdyCallback != null));
        if (mCaptureSession != null)
            try {
                mCaptureSession.abortCaptures();
            } catch (CameraAccessException | java.lang.SecurityException ex) {
                Log.WriteEx(ex);
                mCaptureSession = null;
            }
            catch (IllegalStateException ex)
            {
                Log.WriteEx(ex);
                mCaptureSession = null;
            }
    }

    public void StartRepeatingCaptureSession()
    {
        Log.d(TAG, "StartRepeatingCaptureSession");
        if (mCaptureSession == null)
            return;
        try {
            if (waitForRdyCallback != null)
                Log.d(TAG, "waitforRdy not null");
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                    null);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
        catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public void StartRepeatingCaptureSession(CameraCaptureSession.CaptureCallback listener)
    {
        Log.d(TAG, "StartRepeatingCaptureSession with Custom CaptureCallback");
        if (mCaptureSession == null)
            return;
        try {
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), listener,
                    null);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void StartHighspeedCaptureSession()
    {
        if (mCaptureSession == null)
            return;
        try {
            List<CaptureRequest> capList =  ((CameraConstrainedHighSpeedCaptureSession)mCaptureSession).createHighSpeedRequestList(mPreviewRequestBuilder.build());

            mCaptureSession.setRepeatingBurst(capList, cameraBackroundValuesChangedListner, null);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    private CameraCaptureSession.CaptureCallback hfrcallback = new CameraCaptureSession.CaptureCallback()
    {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

        }
    };

    public void capture()
    {
        if(isHighSpeedSession)
            return;
        try {
            mCaptureSession.capture(mPreviewRequestBuilder.build(),null,null);
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void StartImageCapture(@Nullable CameraCaptureSession.CaptureCallback listener, Handler handler)
    {
        try {
            mCaptureSession.capture(mImageCaptureRequestBuilder.build(),listener,handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }


    public void cancelCapture()
    {
        try {
            mCaptureSession.abortCaptures();
        } catch (CameraAccessException e) {
            Log.WriteEx(e);
        }
    }

    public void CloseCaptureSession()
    {
        Log.d(TAG, "CloseCaptureSession");
        Clear();
        if (mCaptureSession != null)
        {
            mCaptureSession.close();

        }
        mCaptureSession = null;

    }

    public <T> void SetParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value, boolean setToCamera)
    {
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        if (mImageCaptureRequestBuilder != null)
            mImageCaptureRequestBuilder.set(key,value);
        if (setToCamera)
        {
            if (isHighSpeedSession)
                StartHighspeedCaptureSession();
            else
                StartRepeatingCaptureSession();
        }
    }

    public <T> void SetPreviewParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value, boolean apply)
    {
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        if (apply) {
            if (isHighSpeedSession)
                StartHighspeedCaptureSession();
            else
                StartRepeatingCaptureSession();
        }
    }

    public <T> void SetPreviewParameter(@NonNull CaptureRequest.Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
    }


    public <T> void SetParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value, CameraCaptureSession.CaptureCallback captureCallback)
    {
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        if (mImageCaptureRequestBuilder != null)
            mImageCaptureRequestBuilder.set(key,value);
        StartRepeatingCaptureSession(captureCallback);
    }

    public <T> void SetParameter(@NonNull CaptureRequest.Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null|| mCaptureSession == null)
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        if (isHighSpeedSession)
            StartHighspeedCaptureSession();
        else {
            if (mImageCaptureRequestBuilder != null)
                mImageCaptureRequestBuilder.set(key, value);
            try {
                mCaptureSession.capture(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                        null);
            } catch (CameraAccessException ex) {
                Log.WriteEx(ex);
            }
        }
    }

    public <T> void SetCaptureParameter(@NonNull CaptureRequest.Key<T> key, T value)
    {
        if (mImageCaptureRequestBuilder == null|| mCaptureSession == null)
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mImageCaptureRequestBuilder.set(key,value);
    }

    public <T> T getPreviewParameter(CaptureRequest.Key<T> key)
    {
        if (mPreviewRequestBuilder == null)
            return null;
        return mPreviewRequestBuilder.get(key);
    }

    public <T> T getImageCaptureParameter(CaptureRequest.Key<T> key)
    {
        if (mImageCaptureRequestBuilder == null)
            return null;
        return mImageCaptureRequestBuilder.get(key);
    }

    public void SetTextureViewSize(int w, int h, int orientation, int orientationWithHack,boolean video)
    {
        Matrix matrix = new Matrix();
        RectF inputRect = new RectF(0, 0, w, h);
        float xof = displaySize.x - inputRect.width();
        float yof = displaySize.y - inputRect.height();
        Log.d(TAG,"Video:"+video);
        Log.d(TAG, "PreviewSize:" + w +"x"+ h);
        Log.d(TAG,"DisplaySize:" + displaySize.x +"x"+ displaySize.y);
        Log.d(TAG, "margine x:" + xof +" margine y:" + yof);
        RectF viewRect = new RectF(0, 0, displaySize.x, displaySize.y);


        Log.d(TAG, "finalsize: " + viewRect.width()+"x"+viewRect.height());


        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (orientation == 90|| orientation == 270) {

            /**
             * input is like that when holding device in landscape
             *
             *   ________
             *   |      |                               _____________________
             *   |      |                               |                   |
             *   |      |  need to get transformed to:  |                   | viewrect
             *   |      |                               |___________________|
             *   ________
             */
            inputRect = new RectF(0, 0, w, h);
            //center input relative to viewrect
            inputRect.offset(centerX - inputRect.centerX(), centerY - inputRect.centerY());


            //set the rectangles for the matrix rotation and scale(inputRect is the startpostion in portrait, viewRect is the end postion landscape)
            matrix.setRectToRect(inputRect, viewRect, Matrix.ScaleToFit.FILL);
            cameraHolderApi2.textureView.setAspectRatio((int)inputRect.width(), (int)inputRect.height());


            //get scalefactor for the height from portrait to landscape
            float scY = inputRect.width() / viewRect.height();
            //get scalefactor for the width from portrait to landscape;
            float scX = inputRect.height() / viewRect.width();

            matrix.postScale(scX,scY,centerX,centerY);

            if (SettingsManager.get(Settings.orientationHack).getBoolean())
                matrix.postRotate(orientationWithHack, centerX, centerY);
            else
                matrix.postRotate(orientation, centerX,centerY);

        }
        else {
            if (SettingsManager.get(Settings.orientationHack).getBoolean()) {
                if (orientationWithHack >= 360)
                    orientationWithHack -= 180;
                matrix.postRotate(orientationWithHack, inputRect.centerX(), inputRect.centerY());
            }
            cameraHolderApi2.textureView.setAspectRatio((int) inputRect.width(), (int) inputRect.height());
        }

        cameraHolderApi2.textureView.setTransform(matrix);
    }

    public void StartAePrecapture(CameraCaptureSession.CaptureCallback listener)
    {
        SetParameterRepeating(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START,listener);
    }

    public <T> void SetFocusArea(@NonNull CaptureRequest.Key<T> key, T value)
    {
        SetParameter(key,null);
        /*captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);*/
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        SetParameter(key,value);
        SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
    }

}
