package com.troop.freedcam.camera;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;

import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.lge.hardware.LGCamera;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.Size;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.CameraFocusEvent;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by troop on 15.08.2014.
 */
public class BaseCameraHolder extends AbstractCameraHolder
{
    Camera mCamera;
    Camera.Parameters MTKparameters;

    private Camera.Parameters mCameraParam;
    LGCamera lgCamera;
    LGCamera.LGParameters lgParameters;
    private static String TAG = BaseCameraHolder.class.getSimpleName();
    public I_error errorHandler;
    I_Callbacks.PictureCallback pictureCallback;
    I_Callbacks.PictureCallback rawCallback;
    I_Callbacks.ShutterCallback shutterCallback;
    I_Callbacks.PreviewCallback previewCallback;
    Surface surfaceHolder;

    public boolean hasLGFrameWork = false;
    public Location gpsLocation;
    public int Orientation;


    TextureView textureView;


    public int CurrentCamera;

    public BaseCameraHolder(I_CameraChangedListner cameraChangedListner, Handler UIHandler)
    {
        super(cameraChangedListner, UIHandler);
        //hasSamsungFramework();
        hasLGFramework();
    }

    private void hasLGFramework()
    {
        try {
            Class c = Class.forName("com.lge.hardware.LGCamera");
            Log.d(TAG, "Has Lg Framework");
            hasLGFrameWork = true;

        } catch (ExceptionInInitializerError e) {

            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }
        catch (UnsatisfiedLinkError er)
        {
            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }
        catch (ClassNotFoundException e)
        {
            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }
        catch (Exception e) {

            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }
        try {
            Class c = Class.forName("com.lge.media.CamcorderProfileEx");
            Log.d(TAG, "Has Lg Framework");
            hasLGFrameWork = true;

        } catch (ExceptionInInitializerError e) {

            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }
        catch (UnsatisfiedLinkError er)
        {
            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }
        catch (ClassNotFoundException e)
        {
            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }
        catch (Exception e) {

            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }

    }

    /**
     * Opens the Camera
     * @param camera the camera to open
     * @return false if camera open fails, return true when open
     */
    @Override
    public boolean OpenCamera(final int camera)
    {
        try
        {
            if (hasLGFrameWork /*&& Build.VERSION.SDK_INT < 21*/)
            {
                try {
                    if (DeviceUtils.isG4())
                        lgCamera = new LGCamera(camera, 256);
                    else
                        lgCamera = new LGCamera(camera);
                    mCamera = lgCamera.getCamera();
                    lgParameters = lgCamera.getLGParameters();
                }
                catch (RuntimeException ex)
                {
                    cameraChangedListner.onCameraError("Fail to connect to camera service");
                }
            } else {

                //if(DeviceUtils.isSonyM5_MTK())
                    //android.hardware.Camera.setProperty("client.appmode", "MtkEng");
                if(DeviceUtils.isMediaTekDevice())
                    setMtkAppMode();
                mCamera = Camera.open(camera);
            }

            isRdy = true;
            cameraChangedListner.onCameraOpen("");

        } catch (Exception ex) {
            isRdy = false;
            ex.printStackTrace();
        }

        return isRdy;
    }

    @Override
    public void CloseCamera()
    {
        Log.d(TAG, "Try to close Camera");
        if (mCamera != null)
        {
            try
            {
                mCamera.release();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e(TAG, "Error on Camera close");
            }
            finally {
                mCamera = null;
                isRdy = false;
                Log.d(TAG, "Camera closed");
            }
        }
        isRdy = false;
        cameraChangedListner.onCameraClose("");
    }



    @Override
    public int CameraCout() {
        return Camera.getNumberOfCameras();
    }

    @Override
    public boolean IsRdy() {
        return isRdy;
    }

    @Override
    public boolean SetCameraParameters(final HashMap<String, String> parameters)
    {
        try {
            String ret = "";
            for (Map.Entry s : parameters.entrySet()) {
                ret += s.getKey() + "=" + s.getValue() + ";";
            }
            if (hasLGFrameWork /*&& Build.VERSION.SDK_INT < 21*/) {
                Log.d(TAG, "Set lg Parameters");
                Camera.Parameters p = lgParameters.getParameters();
                p.unflatten(ret);
                lgParameters.setParameters(p);
            } else {
                Log.d(TAG, "Set Parameters");
                Camera.Parameters p = mCamera.getParameters();
                p.unflatten(ret);
                mCamera.setParameters(p);
            }
           // Thread.sleep(300);
        }
        catch (Exception ex) {
           // Log.d("Freedcam", ex.getMessage());
        }




        return true;
    }

    @Override
    public boolean SetSurface(SurfaceHolder surfaceHolder)
    {
        this.surfaceHolder = surfaceHolder.getSurface();
        try
        {
            if (isRdy && mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean SetTextureView(TextureView textureView)
    {
        try
        {
            mCamera.setPreviewTexture(textureView.getSurfaceTexture());
            this.textureView = textureView;
            this.surfaceHolder = new Surface(textureView.getSurfaceTexture());
            return  true;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return false;
    }

    public Surface getSurfaceHolder()
    {
        return  surfaceHolder;
    }

    @Override
    public void StartPreview()
    {
        if (mCamera == null)
            return;

            try
            {
                if (DeviceUtils.isMediaTekDevice())
                    initMTKSHit();

                mCamera.startPreview();
                isPreviewRunning = true;
                Log.d(TAG, "PreviewStarted");
                cameraChangedListner.onPreviewOpen("");

            } catch (Exception ex) {
                Log.d("Freedcam", ex.getMessage());
            }


    }

    private void initMTKSHit()    {

        MTKparameters = mCamera.getParameters();
        MTKparameters.set("afeng_raw_dump_flag", 1);
        MTKparameters.set("isp-mode", 1);
        MTKparameters.set("rawsave-mode", "2");
        MTKparameters.set("rawfname", "/mnt/sdcard/DCIM/FreeDCam/mtk_.raw");
        MTKparameters.set("zsd-mode", "on");
        mCamera.setParameters(MTKparameters);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void StopPreview()
    {
        if (mCamera == null)
            return;
        try {


                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

            isPreviewRunning = false;
            Log.d(TAG, "Preview Stopped");
            cameraChangedListner.onPreviewClose("");

        } catch (Exception ex)
        {
            cameraChangedListner.onPreviewClose("");
            isPreviewRunning = false;
            Log.d(TAG, "Camera was released");
            ex.printStackTrace();
        }
    }

    public HashMap<String, String> GetCameraParameters()
    {
        String[] split = null;
        if (hasLGFrameWork)
            split = lgCamera.getLGParameters().getParameters().flatten().split(";");
        else
            split = mCamera.getParameters().flatten().split(";");
        HashMap<String, String> map = new HashMap<>();
        for (String s: split)
        {
            String[] valSplit = s.split("=");
            boolean sucess = false;
            try
            {
                map.put(valSplit[0], valSplit[1]);
            }
            catch (Exception ex)
            {
                map.put(valSplit[0], "");
            }

        }

        return map;
    }

    public void TakePicture(final I_Callbacks.ShutterCallback shutter, final I_Callbacks.PictureCallback raw, final I_Callbacks.PictureCallback picture)
    {
        this.pictureCallback = picture;
        this.shutterCallback = shutter;
        this.rawCallback = raw;


            takePicture();

    }

    private void takePicture()
    {
        Camera.ShutterCallback sh = null;
        if (shutterCallback != null)
        {
            sh = new Camera.ShutterCallback() {
                @Override
                public void onShutter() {
                    shutterCallback.onShutter();
                }
            };
        }
        Camera.PictureCallback r = null;
        if (rawCallback != null)
        {
            r = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera secCamera)
                {
                    if (rawCallback != null)
                        rawCallback.onPictureTaken(bytes);
                }
            };
        }
        if (pictureCallback == null)
            return;
        Camera.PictureCallback pic = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera secCamera) {
                pictureCallback.onPictureTaken(bytes);

            }
        };
        try {
            this.mCamera.takePicture(sh, r, pic);
        }
        catch (RuntimeException ex)
        {
            errorHandler.OnError("Picture Taking failed, What a Terrible Failure!!");
        }

    }

    private void setMtkAppMode()
    {
        try {
            Class camera = Class.forName("android.hardware.Camera");
            Method[] meths = camera.getMethods();
            Method app = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setProperty"))
                    app = m;
            }
            if (app == null)
                throw new  NoSuchMethodException();
            app.invoke(null,"client.appmode", "MtkEng");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void runObjectTrackingReflection()
    {
        try {
            Class camera = Class.forName("com.lge.hardware.LGCamera");
            Method[] meths = camera.getMethods();
            Method obtrack = null;
            for (Method m : meths)
            {
                if (m.getName().equals("runObjectTracking"))
                    obtrack = m;
            }
            if (obtrack == null)
                throw new  NoSuchMethodException();
            obtrack.invoke(lgCamera, null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private void setHistogramReflection()
    {
        try
        {
            Class camera = Class.forName("android.hardware.Camera");
            Class[] intefaces = camera.getClasses();
            Class datacallback = null;
            for (Class i : intefaces)
            {
                if (i.getSimpleName().equals("CameraDataCallback"))
                    datacallback = i;
            }
            if (datacallback == null)
                throw new NoClassDefFoundError();

            Object dcb = (Object) Proxy.newProxyInstance(datacallback.getClassLoader(), new Class[]{datacallback}, new InvocationHandler()
            {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    //Handle the invocations
                    if(method.getName().equals("onCameraData")){
                        return 1;
                    }
                    else return -1;
                }
            });
            Method[] meths = camera.getMethods();
            Method setHistogramMode = null;
            Method sendHistogramData = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setHistogramMode"))
                    setHistogramMode = m;
                if (m.getName().equals("sendHistogramData"))
                    sendHistogramData = m;
            }
            if (sendHistogramData == null || setHistogramMode == null)
                throw new  NoSuchMethodException();
            setHistogramMode.invoke(mCamera, dcb);


            sendHistogramData.invoke(mCamera, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void SetMetaDataCallbackReflection()
    {
        try {
            Class camera = Class.forName("android.hardware.Camera");
            Class[] intefaces = camera.getClasses();

            Class metadatacallback = null;
            for (Class i : intefaces)
            {
                if (i.getSimpleName().equals("CameraMetaDataCallback"))
                    metadatacallback = i;
            }
            if (metadatacallback == null)
                throw new NoClassDefFoundError();

            Object dcb = (Object) Proxy.newProxyInstance(metadatacallback.getClassLoader(), new Class[]{metadatacallback}, new InvocationHandler()
            {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    //Handle the invocations
                    if(method.getName().equals("onCameraMetaData")){
                        return 1;
                    }
                    else return -1;
                }
            });

            Method[] meths = camera.getMethods();
            Method setMetaDataCB = null;
            Method sendMetaData = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setMetadataCb"))
                    setMetaDataCB = m;
                if (m.getName().equals("sendMetaData"))
                    sendMetaData = m;
            }
            if (sendMetaData == null || setMetaDataCB == null)
                throw new  NoSuchMethodException();
            setMetaDataCB.invoke(mCamera, dcb);


            sendMetaData.invoke(mCamera, null);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void SetPreviewCallback(final I_Callbacks.PreviewCallback previewCallback)
    {
        this.previewCallback = previewCallback;
        if (!isPreviewRunning && !isRdy)
            return;

            if (previewCallback == null)
                mCamera.setPreviewCallback(null);
            else
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        previewCallback.onPreviewFrame(data, I_Callbacks.YUV);
                    }
                });

    }

    @Override
    public void SetPreviewCallback(final Camera.PreviewCallback previewCallback)
    {
        try {
            if (!isPreviewRunning && !isRdy)
                return;
            Size s = new Size(ParameterHandler.PreviewSize.GetValue());
            mCamera.addCallbackBuffer(new byte[s.height * s.width *
                    ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8]);
            mCamera.addCallbackBuffer(new byte[s.height * s.width *
                    ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8]);
            mCamera.addCallbackBuffer(new byte[s.height * s.width *
                    ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8]);
            mCamera.setPreviewCallbackWithBuffer(previewCallback);
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void ResetPreviewCallback()
    {
        try {
            mCamera.setPreviewCallback(null);
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }

    public void SetErrorCallback(final I_Callbacks.ErrorCallback errorCallback)
    {

            if (mCamera == null)
                return;
            mCamera.setErrorCallback(new Camera.ErrorCallback() {
                @Override
                public void onError(int error, Camera camera)
                {
                    isRdy = false;
                    errorCallback.onError(error);
                }
            });

    }

    public void StartFocus(final I_Callbacks.AutoFocusCallback autoFocusCallback)
    {
        if (!isRdy)
            return;
        try {

                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera)
                    {
                        CameraFocusEvent focusEvent = new CameraFocusEvent();
                        focusEvent.camera = camera;
                        focusEvent.success = success;

                        autoFocusCallback.onAutoFocus(focusEvent);
                    }
                });

        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            CameraFocusEvent focusEvent = new CameraFocusEvent();

            focusEvent.success = false;
            autoFocusCallback.onAutoFocus(focusEvent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            CameraFocusEvent focusEvent = new CameraFocusEvent();

            focusEvent.success = false;
            autoFocusCallback.onAutoFocus(focusEvent);
        }
    }

    public void CancelFocus()
    {
        if (!isRdy)
            return;

            mCamera.cancelAutoFocus();

    }

    public void SetMeteringAreas(FocusRect meteringRect)
    {
        try {

                List<Camera.Area> meteringList = new ArrayList<>();
                if (meteringRect != null)
                    meteringList.add(new Camera.Area(new Rect(meteringRect.left, meteringRect.top, meteringRect.right, meteringRect.bottom), 100));
                Camera.Parameters p = mCamera.getParameters();
                if(p.getMaxNumMeteringAreas() > 0)
                    p.setMeteringAreas(meteringList);

                try {
                    Log.d(TAG, "try Set Metering");
                    mCamera.setParameters(p);
                    Log.d(TAG, "Setted Metering");
                } catch (Exception ex) {
                    Log.d(TAG, "Set Metering FAILED!");
                }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void SetLocation(Location loc)
    {
        this.gpsLocation = loc;
        if(!isRdy)
            return;

            if (mCamera != null) {
                Camera.Parameters paras = mCamera.getParameters();
                paras.setGpsAltitude(loc.getAltitude());
                paras.setGpsLatitude(loc.getLatitude());
                paras.setGpsLongitude(loc.getLongitude());
                paras.setGpsProcessingMethod(loc.getProvider());
                paras.setGpsTimestamp(loc.getTime());
                try {
                    mCamera.setParameters(paras);
                }
                catch (RuntimeException ex)
                {
                    errorHandler.OnError("Set Location failed");
                }

            }

    }

    public void SetOrientation(int or)
    {
        if (!isRdy || or == Orientation)
            return;
        this.Orientation = or;

            if (mCamera != null) {
                Camera.Parameters paras = mCamera.getParameters();
                paras.setRotation(or);
                mCamera.setParameters(paras);
            }

    }

    public void SetPreviewSize(String size)
    {
        String split[] = size.split("x");
        int width = Integer.parseInt(split[0]);
        int height = Integer.parseInt(split[1]);

            if (mCamera != null) {
                Camera.Parameters paras = mCamera.getParameters();
                paras.setPreviewSize(width,height);
                mCamera.setParameters(paras);
            }

    }

    public void SetCameraRotation(int rotation)
    {
        if (!isRdy)
            return;
            mCamera.setDisplayOrientation(rotation);
    }

    public Camera GetCamera() {
        return mCamera;
    }

    public String getLgParameters()
    {
        return lgParameters.getParameters().flatten();
    }


}
