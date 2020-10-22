package com.troop.freedcam.camera.camera2;

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
import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.camera.camera2.camera2_hidden_keys.huawei.CaptureRequestHuawei;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.CameraStateEvents;
import com.troop.freedcam.eventbus.events.SwichCameraFragmentEvent;
import com.troop.freedcam.eventbus.events.TransformMatrixChangedEvent;
import com.troop.freedcam.eventbus.events.UserMessageEvent;
import com.troop.freedcam.settings.Frameworks;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.BackgroundHandlerThread;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    private Camera2Controller cameraUiWrapper;
    private CameraHolderApi2 cameraHolderApi2;
    private CameraValuesChangedCaptureCallback cameraBackroundValuesChangedListner;
    private boolean isHighSpeedSession = false;
    private BackgroundHandlerThread backgroundHandlerThread;
    private Handler handler;

    private int OPMODE = 0;

    private boolean captureSessionOpen = false;


    public void setOPMODE(int opmode){
        OPMODE = opmode;
    }

    CameraCaptureSession.StateCallback previewStateCallBackRestart = new CameraCaptureSession.StateCallback()
    {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
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
                        cameraBackroundValuesChangedListner, handler);
            } catch (CameraAccessException | IllegalStateException e) {
                Log.WriteEx(e);
            }

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "onConfigureFailed()");
            mCaptureSession = null;
            EventBusHelper.post(new SwichCameraFragmentEvent());
        }

        @Override
        public void onReady( CameraCaptureSession session) {
            super.onReady(session);
            Log.d(TAG, "onReady()");
        }

        @Override
        public void onClosed( CameraCaptureSession session) {
            super.onClosed(session);
            Log.d(TAG, "onClosed()");
        }

        @Override
        public void onActive( CameraCaptureSession session) {
            super.onActive(session);
            Log.d(TAG, "onActive()");
        }

        @Override
        public void onSurfacePrepared( CameraCaptureSession session,  Surface surface) {
            super.onSurfacePrepared(session, surface);
            Log.d(TAG,"onSurfacePrepared");
        }
    };

    public CaptureSessionHandler(Camera2Controller cameraUiWrapper, CameraValuesChangedCaptureCallback cameraBackroundValuesChangedListner)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolderApi2 = cameraUiWrapper.getCameraHolder();
        this.cameraBackroundValuesChangedListner = cameraBackroundValuesChangedListner;
        surfaces = new ArrayList<>();
        Display display = ((WindowManager) ContextApplication.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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
            try {
                if (SettingsManager.getInstance().getFrameWork() == Frameworks.HuaweiCamera2Ex)
                    mPreviewRequestBuilder.set(CaptureRequestHuawei.HUAWEI_CAMERA_FLAG, (byte) 1);
            }
            catch (IllegalArgumentException ex)
            {
                Log.WriteEx(ex);
            }
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
                mImageCaptureRequestBuilder.set(CaptureRequestHuawei.HUAWEI_CAMERA_FLAG,(byte)1);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void setImageCaptureSurface(Surface surface)
    {
        mImageCaptureRequestBuilder.addTarget(surface);
    }

    public void AddSurface(Surface surface, boolean addtoPreviewRequestBuilder)
    {
        Log.d(TAG, "AddSurface");
        if (surfaces.contains(surface))
            return;
        surfaces.add(surface);
        if (addtoPreviewRequestBuilder)
        {
            if (mPreviewRequestBuilder == null)
                CreatePreviewRequestBuilder();
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
        if (mPreviewRequestBuilder != null)
            for (Surface s: surfaces)
                mPreviewRequestBuilder.removeTarget(s);
            mPreviewRequestBuilder = null;
        if (mImageCaptureRequestBuilder != null)
            for (Surface s: surfaces)
                mImageCaptureRequestBuilder.removeTarget(s);
            mImageCaptureRequestBuilder = null;
        surfaces.clear();

    }

    public void CreateCaptureSession()
    {
        Log.d(TAG, "CreateCaptureSession:");
        if(cameraHolderApi2.mCameraDevice == null)
            return;
        isHighSpeedSession = false;

        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForFirstFrame();
        try {
            cameraHolderApi2.mCameraDevice.createCaptureSession(surfaces, previewStateCallBackRestart, handler);
        } catch (Exception  ex) {
            Log.WriteEx(ex);
            EventBusHelper.post(new SwichCameraFragmentEvent());
        }
        captureSessionOpen = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void CreateCustomCaptureSession()
    {
        Log.d(TAG, "CreateCustomCaptureSession:");
        if(cameraHolderApi2.mCameraDevice == null)
            return;
        isHighSpeedSession = false;

        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForFirstFrame();
        try {
            List<OutputConfiguration> outputConfigurations = new ArrayList<>(surfaces.size());
            for (Surface surface : surfaces)
            {
                outputConfigurations.add(new OutputConfiguration(surface));
            }
            createCustomCaptureSession(cameraHolderApi2.mCameraDevice,null,outputConfigurations, OPMODE,previewStateCallBackRestart,handler);


        } catch (Exception  ex) {
            Log.WriteEx(ex);
        }
        captureSessionOpen = true;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createCustomCaptureSession(CameraDevice cdv,InputConfiguration var1, List<OutputConfiguration> var2, int var3, CameraCaptureSession.StateCallback var4, Handler var5)
    {
        try {
            Method ccreateCustomCaptureSession = CameraDevice.class.getMethod("createCustomCaptureSession",InputConfiguration.class,
                    List.class,Integer.TYPE,CameraCaptureSession.StateCallback.class,Handler.class);
            ccreateCustomCaptureSession.invoke(cdv,var1,var2,var3,var4,var5);

        }catch (NoSuchMethodException e)
        {e.printStackTrace();} catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void CreateHighSpeedCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
        Log.d(TAG,"CreateHighspeedCaptureSession");
        if(cameraHolderApi2.mCameraDevice == null)
            return;
        isHighSpeedSession = true;
        //cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForFirstFrame();
        Log.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
        try {
            cameraHolderApi2.mCameraDevice.createConstrainedHighSpeedCaptureSession(surfaces, customCallback, handler);
        } catch (CameraAccessException | SecurityException ex) {
            Log.WriteEx(ex);
        }
        captureSessionOpen = true;
    }


    public void CreateCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
        Log.d(TAG, "CreateCaptureSessionWITHCustomCallback: Surfaces Count:" + surfaces.size());
        isHighSpeedSession = false;

        try {
            cameraHolderApi2.mCameraDevice.createCaptureSession(surfaces, customCallback, handler);
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        captureSessionOpen = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void CreateCustomCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
        Log.d(TAG, "CreateCaptureSessionWITHCustomCallback: Surfaces Count:" + surfaces.size());
        isHighSpeedSession = false;

        try {
            List<OutputConfiguration> outputConfigurations = new ArrayList<>(surfaces.size());
            for (Surface surface : surfaces)
            {
                outputConfigurations.add(new OutputConfiguration(surface));
            }
            createCustomCaptureSession(cameraHolderApi2.mCameraDevice,null,outputConfigurations, OPMODE,customCallback,handler);

        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        captureSessionOpen = true;
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
        catch (IllegalStateException ex)
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
        }catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }
        catch (IllegalStateException ex)
        {
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
            EventBusHelper.post(new UserMessageEvent(ex.getLocalizedMessage(),false));
        }catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }
        catch (IllegalStateException ex)
        {
            Log.WriteEx(ex);
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
        }catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }
        catch (IllegalStateException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public void StartImageCapture(CameraCaptureSession.CaptureCallback listener, Handler handler)
    {
        Log.d(TAG,"StartImageCapture");
        try {

            mCaptureSession.capture(mImageCaptureRequestBuilder.build(),listener,handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }
        catch (IllegalStateException ex)
        {
            Log.WriteEx(ex);
        }
        catch (NullPointerException ex)
        {
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
        }catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }
        catch (IllegalStateException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public void CloseCaptureSession()
    {
        Log.d(TAG, "CloseCaptureSession");
        if (!captureSessionOpen)
            return;
        captureSessionOpen = false;
        Clear();
        if (mCaptureSession == null)
        {
            Log.d(TAG,"CaptureSession is null");
            return;
        }
        try
        {
            if (!captureSessionOpen)
                return;
            mCaptureSession.close();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
        mCaptureSession = null;
    }

    public <T> void SetParameterRepeating(CaptureRequest.Key<T> key, T value, boolean setToCamera)
    {
        if (key != null && value != null)
            Log.d(TAG," SetParameterRepeating(" + key.getName() + " " + value+")");
        if (mPreviewRequestBuilder == null )
            return;
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

    public <T> void SetPreviewParameterRepeating(CaptureRequest.Key<T> key, T value, boolean apply)
    {
        Log.d(TAG,"SetPreviewParameterRepeating( CaptureRequest.Key<T> key, T value, boolean apply)");
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

    public <T> void SetPreviewParameter(CaptureRequest.Key<T> key, T value ,boolean setToCamera)
    {
        if (mPreviewRequestBuilder == null || mCaptureSession == null)
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        if (setToCamera)
            try {
                mCaptureSession.capture(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                        handler);
            } catch (CameraAccessException ex) {
                Log.WriteEx(ex);
            }
            catch (IllegalArgumentException ex)
            {
                Log.WriteEx(ex);
            }
            catch (IllegalStateException ex)
            {
                Log.WriteEx(ex);
            }
    }


    public <T> void SetParameterRepeating(CaptureRequest.Key<T> key, T value, CameraCaptureSession.CaptureCallback captureCallback)
    {
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        if (mImageCaptureRequestBuilder != null)
            mImageCaptureRequestBuilder.set(key,value);
        StartRepeatingCaptureSession(captureCallback);
    }

    public <T> void SetParameter( CaptureRequest.Key<T> key, T value)
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
            if (!captureSessionOpen) {
                Log.d(TAG, "capturesession is not open");
                return;
            }
            try {
                mCaptureSession.capture(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                        handler);
            } catch (CameraAccessException ex) {
                Log.WriteEx(ex);
            }
            catch (IllegalArgumentException ex)
            {
                Log.WriteEx(ex);
            }
            catch (IllegalStateException ex)
            {
                Log.WriteEx(ex);
            }
        }
    }

    public <T> void SetCaptureParameter( CaptureRequest.Key<T> key, T value)
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

    private final String MATRIXTAG = TAG + ".SetTextureViewSize";

    public void SetTextureViewSize(int w, int h, int rotation,boolean renderscript)
    {
        Matrix matrix = new Matrix();
        matrix.reset();
        RectF inputRect = new RectF(0, 0, w, h);
        Log.d(MATRIXTAG, "PreviewSize:" + w +"x"+ h);
        Log.d(MATRIXTAG,"DisplaySize:" + displaySize.x +"x"+ displaySize.y);

        float dispWidth = 0;
        float dispHeight = 0;
        if (renderscript)
        {
            dispWidth = cameraUiWrapper.getTextureHolder().getWidth();
            dispHeight = cameraUiWrapper.getTextureHolder().getHeight();
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

        Log.d(MATRIXTAG,"previewratio : " + viewRatio + " inputratio :" + inputRatio);

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

        float scaleX;
        float scaleY;
        if (renderscript)
        {
            //renderscript has already set the width and height due the Allocation
            //we have to use the real width and height from the Allocation
            if (rotation == 90 || rotation == 270) {
                Log.d(MATRIXTAG, "orientation 90/270");
                scaleY = w / viewRect.height();
                scaleX = h / viewRect.width();
            } else {
                Log.d(MATRIXTAG, "orientation 0/180");
                scaleY = h / viewRect.height();
                scaleX = w / viewRect.width();
            }
        }
        else {
            if (rotation == 90 || rotation == 270) {
                Log.d(MATRIXTAG, "orientation 90/270");
                scaleY= inputRect.width() / viewRect.height();
                scaleX = inputRect.height() / viewRect.width();
            } else {
                Log.d(MATRIXTAG, "orientation 0/180");
                scaleY = inputRect.height() / viewRect.height();
                scaleX = inputRect.width() / viewRect.width();
            }
        }
        Log.d(MATRIXTAG,"scaleX:" +scaleX + " scaleY:" +scaleY + " centerX:"+centerX +" centerY:" +centerY + " rotation:" + rotation);

        inputRect.offset(centerX - inputRect.centerX(), centerY - inputRect.centerY());
        matrix.setRectToRect(inputRect,viewRect, Matrix.ScaleToFit.CENTER);
        matrix.postScale(scaleX, scaleY, inputRect.centerX(), inputRect.centerY());
        matrix.postRotate(rotation, inputRect.centerX(), inputRect.centerY());

        EventBusHelper.post(new TransformMatrixChangedEvent(matrix));
    }

    public void StartAePrecapture()
    {
        SetPreviewParameter(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START,false);
    }

    public <T> void SetFocusArea( CaptureRequest.Key<T> key, T value)
    {
        if (value != null)
            Log.d(TAG, "Set :" + key.getName() + " to " + value.toString());
        if (isHighSpeedSession && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            mPreviewRequestBuilder.set(key,value);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            try {
                CameraConstrainedHighSpeedCaptureSession session = null;
                session = (CameraConstrainedHighSpeedCaptureSession)mCaptureSession;
                List<CaptureRequest> capList =  session.createHighSpeedRequestList(mPreviewRequestBuilder.build());

                mCaptureSession.captureBurst(capList, cameraBackroundValuesChangedListner, handler);
            } catch (CameraAccessException ex) {
                Log.WriteEx(ex);
                EventBusHelper.post(new UserMessageEvent(ex.getLocalizedMessage(),false));
            }catch (IllegalArgumentException ex)
            {
                Log.WriteEx(ex);
            }
            catch (IllegalStateException ex)
            {
                Log.WriteEx(ex);
            }
        }
        else {
            cameraBackroundValuesChangedListner.setWaitForFocusLock(true);
            mPreviewRequestBuilder.set(key,value);
            SetPreviewParameter(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START,true);
            SetPreviewParameter(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE,true);

        }
    }

}
