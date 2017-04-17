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

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.utils.AppSettingsManager;
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
    private CameraCaptureSession mCaptureSession;
    private CameraConstrainedHighSpeedCaptureSession mHighSpeedCaptureSession;
    private Camera2Fragment cameraUiWrapper;
    private CameraHolderApi2 cameraHolderApi2;
    private CameraCaptureSession.CaptureCallback cameraBackroundValuesChangedListner;
    private CaptureEvent waitForRdyCallback;

    public interface CaptureEvent
    {
        void onRdy();
    }

    public List<CaptureRequest.Key<?>> getKeys()
    {
        return mPreviewRequestBuilder.build().getKeys();
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
                        cameraBackroundValuesChangedListner, null);
            } catch (CameraAccessException | IllegalStateException e) {
                mCaptureSession =null;
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "onConfigureFailed()");
        }

        @Override
        public void onReady(@NonNull CameraCaptureSession session) {
            super.onReady(session);
            Log.d(TAG, "onReady()");
            Log.d(TAG, "waitforCallBack:" + (waitForRdyCallback != null));
            if (waitForRdyCallback != null){
                waitForRdyCallback.onRdy();
                waitForRdyCallback = null;
            }
        }

        @Override
        public void onClosed(@NonNull CameraCaptureSession session) {
            super.onClosed(session);
            Log.d(TAG, "onClosed()");
        }

        @Override
        public void onActive(@NonNull CameraCaptureSession session) {
            super.onActive(session);
            Log.d(TAG, "onActive()");
        }

        @Override
        public void onSurfacePrepared(@NonNull CameraCaptureSession session, @NonNull Surface surface) {
            super.onSurfacePrepared(session, surface);
            Log.d(TAG,"onSurfacePrepared");
        }
    };




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

    public void SetCaptureSession(CameraCaptureSession cameraCaptureSession)
    {
        mCaptureSession = cameraCaptureSession;
    }

    public void SetHighSpeedCaptureSession(CameraCaptureSession cameraCaptureSession)
    {
        mHighSpeedCaptureSession = (CameraConstrainedHighSpeedCaptureSession)cameraCaptureSession;
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
        surfaces.clear();

    }

    public void CreateCaptureSession()
    {
        if(cameraHolderApi2.mCameraDevice == null)
            return;
        Log.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
        try {
            cameraHolderApi2.mCameraDevice.createCaptureSession(surfaces, previewStateCallBackRestart, null);
        } catch (CameraAccessException | SecurityException ex) {
            Log.WriteEx(ex);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void CreateHighSpeedCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
        if(cameraHolderApi2.mCameraDevice == null)
            return;
        Log.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
        try {
            cameraHolderApi2.mCameraDevice.createConstrainedHighSpeedCaptureSession(surfaces, customCallback, null);
        } catch (CameraAccessException | SecurityException ex) {
            Log.WriteEx(ex);
        }
    }

    public void CreateCaptureSession(CameraCaptureSession.StateCallback customCallback)
    {
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

    public void StopHighspeedCaptureSession()
    {
        if (mHighSpeedCaptureSession != null)
            try {
                mHighSpeedCaptureSession.stopRepeating();
            } catch (CameraAccessException ex) {
                Log.WriteEx(ex);
            }
            catch (IllegalStateException ex)
            {
                Log.WriteEx(ex);
                mHighSpeedCaptureSession = null;
            }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void StartHighspeedCaptureSession()
    {
        if (mHighSpeedCaptureSession == null)
            return;
        try {
            List<CaptureRequest> capList = mHighSpeedCaptureSession.createHighSpeedRequestList(mPreviewRequestBuilder.build());

            mHighSpeedCaptureSession.setRepeatingBurst(capList, new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    Log.d("Completed", "fps:" + result.getFrameNumber());
                }
            }, null);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void StartImageCapture(@NonNull CaptureRequest.Builder request,
                                  @Nullable CameraCaptureSession.CaptureCallback listener, Handler handler)
    {
        //StopRepeatingCaptureSession();
        //CancelRepeatingCaptureSession();
        try {
            CaptureRequest request1 = request.build();
            mCaptureSession.capture(request1,listener,handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void StartImageCapture(@NonNull CaptureRequest request,
                                  @Nullable CameraCaptureSession.CaptureCallback listener, Handler handler)
    {
        //StopRepeatingCaptureSession();
        //CancelRepeatingCaptureSession();
        try {;

            mCaptureSession.capture(request,listener,handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void prepareSurface(Surface surface)
    {
        try {
            mCaptureSession.prepare(surface);
        } catch (CameraAccessException e) {
            e.printStackTrace();
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

    public void StartCaptureBurst(@NonNull List<CaptureRequest>  request,
                                  @Nullable CameraCaptureSession.CaptureCallback listener, Handler handler)
    {
        try {
            mCaptureSession.captureBurst(request,listener,handler);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public void CloseCaptureSession()
    {
        Log.d(TAG, "CloseCaptureSession");
        CancelRepeatingCaptureSession(null);
        Clear();
        if (mCaptureSession != null)
        {
            mCaptureSession.close();

        }
        mCaptureSession = null;

    }



    public <T> void SetParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        StartRepeatingCaptureSession();
    }


    public <T> void SetParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value, CameraCaptureSession.CaptureCallback captureCallback)
    {
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        StartRepeatingCaptureSession(captureCallback);
    }

    public <T> void SetParameter(@NonNull CaptureRequest.Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null|| mCaptureSession == null)
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        try {
            mCaptureSession.capture(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                    null);
        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
        }
    }

    public <T> T get(CaptureRequest.Key<T> key)
    {
        if (mPreviewRequestBuilder == null)
            return null;
        return mPreviewRequestBuilder.get(key);
    }

    public void SetTextureViewSize(int w, int h, int orientation, int orientationWithHack,boolean video)
    {
        Matrix matrix = new Matrix();
        RectF bufferRect = new RectF(0, 0, w, h);
        float xof = displaySize.x - bufferRect.width();
        float yof = displaySize.y - bufferRect.height();
        Log.d(TAG,"Video:"+video);
        Log.d(TAG, "PreviewSize:" + w +"x"+ h);
        Log.d(TAG,"DisplaySize:" + displaySize.x +"x"+ displaySize.y);
        Log.d(TAG, "margine x:" + xof +" margine y:" + yof);
        RectF viewRect = new RectF(0, 0, displaySize.x - xof, displaySize.y - yof);
        Log.d(TAG, "finalsize: " + viewRect.width()+"x"+viewRect.height());


        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (orientation == 90|| orientation == 270) {
            bufferRect = new RectF(0, 0, h, w);
            //center buffer to screen
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());

            float scale = Math.max(
                    (float) displaySize.x / h,
                    (float) displaySize.y / w);
            matrix.postScale(scale,scale);

            cameraHolderApi2.textureView.setAspectRatio((int)viewRect.width(), (int)viewRect.height());

            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);

            if (cameraUiWrapper.GetAppSettingsManager().getApiString(AppSettingsManager.SETTING_OrientationHack).equals(cameraUiWrapper.getResString(R.string.on_)))
                matrix.postRotate(orientationWithHack, centerX, centerY);
            else
                matrix.postRotate(orientation, centerX,centerY);
        }
        else
            cameraHolderApi2.textureView.setAspectRatio((int)bufferRect.width(), (int)bufferRect.height());
        cameraHolderApi2.textureView.setTransform(matrix);
    }

}
