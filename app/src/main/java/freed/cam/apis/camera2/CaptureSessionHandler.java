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
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.huawei.camera2ex.CaptureRequestEx;

import java.util.ArrayList;
import java.util.List;

import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.BackgroundHandlerThread;
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
    private boolean isHighSpeedSession = false;
    private BackgroundHandlerThread backgroundHandlerThread;
    private Handler handler;

    private final Object waitLock = new Object();


    private volatile boolean captureSessionRdy = false;


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

            synchronized (waitLock)
            {
                // When the session is ready, we start displaying the previewSize.
                mCaptureSession = cameraCaptureSession;

                try {
                    // Finally, we start displaying the camera preview.
                    mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(),
                            cameraBackroundValuesChangedListner, null);
                } catch (CameraAccessException | IllegalStateException e) {
                    Log.WriteEx(e);
                }
                waitLock.notify();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "onConfigureFailed()");
            captureSessionRdy = false;
            synchronized (waitLock)
            {
                mCaptureSession = null;
                waitLock.notify();
            }
        }

        @Override
        public void onReady(@NonNull CameraCaptureSession session) {
            super.onReady(session);
            captureSessionRdy = true;
            Log.d(TAG, "onReady()");
            synchronized (waitLock) {
                waitLock.notify();
            }

        }

        @Override
        public void onClosed(@NonNull CameraCaptureSession session) {
            captureSessionRdy = false;
            super.onClosed(session);
            Log.d(TAG, "onClosed()");
            synchronized (waitLock)
            {
                //mCaptureSession = null;
                waitLock.notify();
            }
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
        backgroundHandlerThread = new BackgroundHandlerThread(TAG);
        backgroundHandlerThread.create();
        handler = new Handler(backgroundHandlerThread.getThread().getLooper());
    }

    @Override
    protected void finalize() throws Throwable {
        backgroundHandlerThread.destroy();
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
            mPreviewRequestBuilder = cameraHolderApi2.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            if (SettingsManager.getInstance().getFrameWork() == Frameworks.HuaweiCamera2Ex)
                mPreviewRequestBuilder.set(CaptureRequestEx.HUAWEI_CAMERA_FLAG,(byte)1);
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
            if (SettingsManager.getInstance().getFrameWork() == Frameworks.HuaweiCamera2Ex)
                mImageCaptureRequestBuilder.set(CaptureRequestEx.HUAWEI_CAMERA_FLAG,(byte)1);
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
        /*try
        {
            if (null != mCaptureSession)
            {
                mCaptureSession.stopRepeating();
                mCaptureSession.abortCaptures();

            }
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }*/
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
        Log.d(TAG, "CreateCaptureSession:");
        /*if (mCaptureSession != null) {
            Log.d(TAG,"CaptureSession is not closed, close it");
            CloseCaptureSession();
        }*/
        if(cameraHolderApi2.mCameraDevice == null)
            return;


        try {
            synchronized (waitLock) {
                isHighSpeedSession = false;

                cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForFirstFrame();
                handler.post(() -> {
                    try {
                        cameraHolderApi2.mCameraDevice.createCaptureSession(surfaces, previewStateCallBackRestart, handler);
                    } catch (CameraAccessException | SecurityException ex) {
                        Log.WriteEx(ex);
                    }
                });
                waitLock.wait();
            }
        } catch (InterruptedException e) {
            Log.WriteEx(e);
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void CreateHighSpeedCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
        Log.d(TAG,"CreateHighspeedCaptureSession");
        if (mCaptureSession != null) {
            Log.d(TAG,"CaptureSession is not close, close it");
           CloseCaptureSession();
        }
        if(cameraHolderApi2.mCameraDevice == null)
            return;
        isHighSpeedSession = true;
        //cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForFirstFrame();
        Log.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
        handler.post(() -> {
            try {
                cameraHolderApi2.mCameraDevice.createConstrainedHighSpeedCaptureSession(surfaces, customCallback, handler);
            } catch (CameraAccessException | SecurityException ex) {
                Log.WriteEx(ex);
            }
        });

        try {
            synchronized (waitLock) {
                waitLock.wait();
            }
        } catch (InterruptedException e) {
            Log.WriteEx(e);
        }
    }


    public void CreateCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
        Log.d(TAG, "CreateCaptureSessionWITHCustomCallback: Surfaces Count:" + surfaces.size());
       /* if (mCaptureSession != null) {
            CloseCaptureSession();
        }*/
        isHighSpeedSession = false;

        try {
            cameraHolderApi2.mCameraDevice.createCaptureSession(surfaces, customCallback, handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void StopRepeatingCaptureSession()
    {
        Log.d(TAG, "StopRepeatingCaptureSession");
        if (mCaptureSession != null) {
            try {
                mCaptureSession.stopRepeating();
            } catch (CameraAccessException | java.lang.SecurityException ex) {
                Log.WriteEx(ex);
                mCaptureSession = null;
            } catch (IllegalStateException ex) {
                Log.WriteEx(ex);
                mCaptureSession = null;
            }
        }
    }


    public void CancelRepeatingCaptureSession()
    {
        Log.d(TAG,"CancelRepeatingCaptureSession");
        synchronized (waitLock)
        {
            handler.post(() -> {
                if (mCaptureSession != null)
                    try {
                        mCaptureSession.abortCaptures();
                    } catch (CameraAccessException | SecurityException ex) {
                        Log.WriteEx(ex);
                        mCaptureSession = null;
                    }
                    catch (IllegalStateException ex)
                    {
                        Log.WriteEx(ex);
                        mCaptureSession = null;
                    }
            });
            try {
                waitLock.wait();
            } catch (InterruptedException e) {
                Log.WriteEx(e);
            }
        }
    }

    public void StartRepeatingCaptureSession()
    {
        Log.d(TAG, "StartRepeatingCaptureSession Surface:" +surfaces.size());
        if (mCaptureSession == null || surfaces.size() == 0)
            return;
        try {
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
                    handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void StartHighspeedCaptureSession()
    {
        Log.d(TAG, "StartHighspeedSession");
        if (mCaptureSession == null || !isHighSpeedSession)
            return;
        try {
            CameraConstrainedHighSpeedCaptureSession session = (CameraConstrainedHighSpeedCaptureSession)mCaptureSession;
            List<CaptureRequest> capList =  session.createHighSpeedRequestList(mPreviewRequestBuilder.build());

            mCaptureSession.setRepeatingBurst(capList, cameraBackroundValuesChangedListner, handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
            UserMessageHandler.sendMSG(ex.getLocalizedMessage(),false);
        }
    }

    public void capture()
    {
        Log.d(TAG,"capture");
        if(isHighSpeedSession)
            return;
        try {
            mCaptureSession.capture(mPreviewRequestBuilder.build(),cameraBackroundValuesChangedListner,handler);
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void StartImageCapture(@Nullable CameraCaptureSession.CaptureCallback listener, Handler handler)
    {
        Log.d(TAG,"StartImageCapture");
        try {
            mCaptureSession.capture(mImageCaptureRequestBuilder.build(),listener,handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }


    public void cancelCapture()
    {
        Log.d(TAG,"cancelCapture");
        try {
            mCaptureSession.abortCaptures();
        } catch (CameraAccessException | NullPointerException e) {
            Log.WriteEx(e);
        }
    }

    public void CloseCaptureSession()
    {
        Log.d(TAG, "CloseCaptureSession");

        synchronized (waitLock) {
            Clear();
            if (mCaptureSession == null)
            {
                Log.d(TAG,"CaptureSession is null");
                return;
            }
            handler.post(() -> {
                try
                {
                    mCaptureSession.close();
                }
                catch (NullPointerException ex)
                {
                    Log.WriteEx(ex);
                }
            });



           /* try {
                Log.d(TAG,"CloseCaptureSession Enter Wait State");
                waitLock.wait();
                Log.d(TAG,"CloseCaptureSession Wait done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            mCaptureSession = null;
        }


    }

    public <T> void SetParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value, boolean setToCamera)
    {
        Log.d(TAG," SetParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value, boolean setToCamera)");
        if (mPreviewRequestBuilder == null )
            return;
        //Log.d(TAG, "Set :" + key.getName() + " to " + value);
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
        Log.d(TAG,"SetPreviewParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value, boolean apply)");
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
                        handler);
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

    public void SetTextureViewSize(int w, int h, int orientation, int orientationWithHack,boolean renderscript)
    {
        Matrix matrix = new Matrix();
        matrix.reset();
        RectF inputRect = new RectF(0, 0, w, h);
        Log.d(TAG, "PreviewSize:" + w +"x"+ h);
        Log.d(TAG,"DisplaySize:" + displaySize.x +"x"+ displaySize.y);

        float dispWidth = 0;
        float dispHeight = 0;
        if (renderscript)
        {
            dispWidth = cameraHolderApi2.textureView.getWidth();
            dispHeight = cameraHolderApi2.textureView.getHeight();
        }
        else if (displaySize.x > displaySize.y) {
            dispWidth = displaySize.x;
            dispHeight = displaySize.y;
        }
        else
        {
            dispWidth = displaySize.y;
            dispHeight = displaySize.x;
        }

        float viewRatio = dispWidth / dispHeight;
        float inputRatio = inputRect.width() /inputRect.height();

        RectF viewRect = new RectF(0, 0, dispWidth, dispHeight);

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        /*
          input is like that when holding device in landscape

            ________
            |      |                               _____________________
            |      |                               |                   |
            |      |  need to get transformed to:  |                   | viewrect
            |      |                               |___________________|
            ________
         */

        inputRect.offset(centerX - inputRect.centerX(), centerY - inputRect.centerY());
        if (!renderscript)
            matrix.setRectToRect(inputRect,viewRect, Matrix.ScaleToFit.CENTER);
        else
            matrix.setRectToRect(inputRect,viewRect, Matrix.ScaleToFit.CENTER);


        float scaleX;
        float scaleY;
        if (renderscript)
        {
            //renderscript has already set the width and height due the Allocation
            //we have to use the real width and height from the Allocation
            if (orientation == 90 || orientation == 270) {
                Log.d(TAG, "viewRect > inputRect");
                scaleY = w / viewRect.height();
                scaleX = h / viewRect.width();
            } else {
                Log.d(TAG, "viewRect <= inputRect");
                scaleY = h / viewRect.height();
                scaleX = w / viewRect.width();
            }
        }
        else {
            if (orientation == 90 || orientation == 270) {
                Log.d(TAG, "viewRect > inputRect");
                scaleY = inputRect.width() / viewRect.height();
                scaleX = inputRect.height() / viewRect.width();
            } else {
                Log.d(TAG, "viewRect <= inputRect");
                scaleY = inputRect.height() / viewRect.height();
                scaleX = inputRect.width() / viewRect.width();
            }
        }
        Log.d(TAG,"scaleX:" +scaleX + " scaleY:" +scaleY);

        matrix.postScale(scaleX, scaleY, centerX, centerY);

        matrix.postRotate(orientation, centerX, centerY);
        cameraHolderApi2.textureView.setTransform(matrix);
    }

    public void StartAePrecapture(CameraCaptureSession.CaptureCallback listener)
    {
        SetParameter(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
    }

    public <T> void SetFocusArea(@NonNull CaptureRequest.Key<T> key, T value)
    {
        //SetParameter(key,null);
        /*captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);*/
        if (value != null)
            Log.d(TAG, "Set :" + key.getName() + " to " + value.toString());
        SetParameter(key,value);
        if (value != null)
            SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
    }

}
