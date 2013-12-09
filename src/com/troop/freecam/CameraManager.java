package com.troop.freecam;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.SurfaceView;
import android.os.Build;


import com.troop.freecam.camera.BaseCamera;
import com.troop.freecam.camera.PictureCam;
import com.troop.freecam.camera.VideoCam;
import com.troop.freecam.manager.AutoFocusManager;
import com.troop.freecam.manager.HdrManager;
import com.troop.freecam.manager.ManualBrightnessManager;
import com.troop.freecam.manager.ManualContrastManager;
import com.troop.freecam.manager.ManualExposureManager;
import com.troop.freecam.manager.ManualFocus;
import com.troop.freecam.manager.ManualSharpnessManager;
import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecam.manager.ParametersManager;
import com.troop.freecam.manager.ZoomManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 25.08.13.
 */
public class CameraManager extends VideoCam implements SurfaceHolder.Callback , SensorEventListener
{


    CamPreview context;


    CameraManager cameraManager;
    public ZoomManager zoomManager;
    public boolean Running = false;
    public MediaScannerManager scanManager;
    public AutoFocusManager autoFocusManager;
    public static final String KEY_CAMERA_INDEX = "camera-index";
    public static final String KEY_S3D_SUPPORTED_STR = "s3d-supported";
    public boolean touchtofocus = false;
    public MainActivity activity;
    public ManualExposureManager manualExposureManager;
    public ManualSharpnessManager manualSharpnessManager;
    public ManualContrastManager manualContrastManager;
    public ManualBrightnessManager manualBrightnessManager;
    public ManualFocus manualFocus;
    public HdrManager HdrRender;
    public ParametersManager parametersManager;
    public boolean takePicture = false;

    public CameraManager(CamPreview context, MainActivity activity, SharedPreferences preferences)
    {

        super(context, preferences);
        scanManager = new MediaScannerManager(context.getContext());
        context.getHolder().addCallback(this);
        zoomManager = new ZoomManager(this);
        autoFocusManager = new AutoFocusManager(this);
        this.activity = activity;
        manualExposureManager = new ManualExposureManager(this);
        cameraManager = this;
        manualSharpnessManager = new ManualSharpnessManager(this);
        manualContrastManager = new ManualContrastManager(this);
        manualBrightnessManager = new ManualBrightnessManager(this);
        manualFocus = new ManualFocus(this);
        HdrRender = new HdrManager(this);
        parametersManager = new ParametersManager(this, preferences);
    }

    public static boolean isOmap()
    {
        String s = Build.MODEL;
        return s.equals("Galaxy Nexus") || s.equals("LG-P920") || s.equals("LG-P720") || s.equals("LG-P925") || s.equals("LG-P760") || s.equals("LG-P765") || s.equals("LG-P925") || s.equals("LG-SU760") || s.equals("LG-SU870") || s.equals("Motorola RAZR MAXX") || s.equals("DROID RAZR") || s.equals("DROID 4") || s.equals("GT-I9100G") || s.equals("U9200");
    }

    public static boolean isQualcomm()
    {
        String s = Build.MODEL;
        return s.equals("LG-D800") || s.equals("LG-D802") || s.equals("LG-D803") || s.equals("LG-D820") || s.equals("LG-D821") || s.equals("LG-D801") || s.equals("C6902") || s.equals("C6903") || s.equals("C833") || s.equals("LG803") || s.equals("C6602") || s.equals("C6603") || s.equals("Nexus 4") || s.equals("Nexus 5") || s.equals("SM-N9005") || s.equals("GT-I9505") || s.equals("GT-I9506") || s.equals("LG803") || s.equals("HTC One") || s.equals("LG-F320") || s.equals("LG-F320S") || s.equals("LG-F320K") || s.equals("LG-F320L") || s.equals("LG-VS980") || s.equals("LG-D805");
    }

    public static boolean isTegra()
    {
        String s = Build.MODEL;
        return s.equals("Nexus 7") || s.equals("LG-P880") || s.equals("ZTE-Mimosa X") || s.equals("HTC One X") || s.equals("HTC One X+") || s.equals("LG-P990") || s.equals("EPAD") || s.equals("GT-P7500") || s.equals("GT-P7300");
    }

    public static boolean isExynos()
    {
        String s = Build.MODEL;
        return s.equals("GT-I9000") || s.equals("GT-I9100") || s.equals("GT-I9300") || s.equals("GT-I9500") || s.equals("SM-905") || s.equals("GT-N7000") || s.equals("GT-N7100");
    }

    public static boolean is3d()
    {
        String s = Build.MODEL;
        return s.equals("LG-P920") || s.equals("LG-P720") || s.equals("LG-P925") || s.equals("LG-P925") || s.equals("LG-SU760") || s.equals("LG-SU870");
    }

    public static boolean isTablet()
    {
        String s = Build.MODEL;
        return s.equals("Nexus 7") || s.equals("Nexus 10");
    }

    public static boolean isG2()
    {
        String s = Build.MODEL;
        return s.equals("LG-D800") || s.equals("LG-D801") || s.equals("LG-D802") || s.equals("LG-D803") || s.equals("LG-D804") || s.equals("LG-D805") || s.equals("LG-D820") || s.equals("LG-F320") || s.equals("LG-F320S") || s.equals("LG-F320L") || s.equals("F320K") || s.equals("LG-VS980");
    }

    Bitmap bitmascale;
    @Override
    public void onPictureSaved(File file)
    {
        takePicture = false;
        if(bitmascale != null)
            bitmascale.recycle();
        try
        {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmaporg = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            //mediaScannerManager.startScan(s);

            int w = cameraManager.activity.thumbButton.getWidth();
            int h = cameraManager.activity.thumbButton.getHeight();
            bitmascale = Bitmap.createScaledBitmap(bitmaporg,w,h,true);
            cameraManager.activity.thumbButton.setImageBitmap(bitmascale);
            cameraManager.lastPicturePath =file.getAbsolutePath();
            scanManager.startScan(lastPicturePath);
            bitmaporg.recycle();
            System.gc();
            //bitmascale.recycle();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public  void Start()
    {
        OpenCamera();
    }

    @Override
    protected void OpenCamera() {
        super.OpenCamera();
        try {
            mCamera.setPreviewDisplay(activity.mPreview.getHolder());
            mCamera.setZoomChangeListener(zoomManager);
            if(preferences.getBoolean("upsidedown", false) == true)
                fixCameraDisplayOrientation();
            zoomManager.ResetZoom();
        } catch (Exception exception) {
            CloseCamera();

            // TODO: add more exception handling logic here
        }
    }

    private void fixCameraDisplayOrientation()
    {
        String tmp = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);

        if(!tmp.equals(ParametersManager.SwitchCamera_MODE_3D) && !tmp.equals(ParametersManager.SwitchCamera_MODE_2D))
        {
            mCamera.setDisplayOrientation(0);
            //mParameters.setRotation(0);
        }
        else
        {
            mCamera.setDisplayOrientation(180);
            //mParameters.setRotation(180);
        }
    }

    private void fixParametersOrientation()
    {
        String tmp = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);

        if(!tmp.equals(ParametersManager.SwitchCamera_MODE_3D) && !tmp.equals(ParametersManager.SwitchCamera_MODE_2D))
        {
           // mCamera.setDisplayOrientation(0);
            parametersManager.getParameters().setRotation(0);
        }
        else
        {
            //mCamera.setDisplayOrientation(180);
            parametersManager.getParameters().setRotation(180);
        }
    }

    //if restarted true cam preview will be stopped and restartet
    public  void Restart(boolean restarted)
    {
        if (restarted)
        {
            parametersManager.SetCameraParameters(mCamera.getParameters());
            mCamera.stopPreview();
            parametersManager.SetJpegQuality(100);
            parametersManager.SetContrast(100);
            //parameters.setExposureCompensation(0);

            if (preferences.getBoolean("upsidedown", false) == true)
                fixParametersOrientation();

            String tmp = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D);
            activity.switch3dButton.setText(tmp);
            if (parametersManager.getParameters().getFocusMode().equals("auto"))
            {
                activity.drawSurface.drawingRectHelper.Enabled = true;
            }
            else
            {
                activity.drawSurface.drawingRectHelper.Enabled = false;
            }
            mCamera.startPreview();

        }
        else
        {
            try
            {
                //set parameters
                mCamera.setParameters(parametersManager.getParameters());
                parametersManager.UpdateUI();
                //get parameters to see if changed
                //parameters = mCamera.getParameters();
            }
            catch (Exception ex)
            {
                Log.e("Parameters Set Fail: ", ex.getMessage());
                parametersManager.SetCameraParameters(mCamera.getParameters());
            }
        }
    }



    public  void Stop()
    {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.

        mCamera.stopPreview();
        CloseCamera();
    }



    public void StartTakePicture()
    {
        if (IsWorking == false)
        {
            Log.d("StartTakingPicture", "takepicture:" + takePicture);
            Log.d("StartTakingPicture", "touchtofocus:" + touchtofocus);
            takePicture = true;
            if (parametersManager.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO))
            {

                if (activity.drawSurface.drawingRectHelper.drawRectangle == true)
                {
                    SetTouchFocus(activity.drawSurface.drawingRectHelper.mainRect);
                    //autoFocusManager.focusing = true;
                    if (autoFocusManager.hasFocus)
                        TakePicture(crop);
                    else
                        autoFocusManager.StartFocus();
                }
                else if (touchtofocus == false)
                {
                    touchtofocus = false;
                    autoFocusManager.StartFocus();
                }
            }
            else
            {
                TakePicture(crop);
            }
        }
    }

    public void StartFocus()
    {
        SetTouchFocus(activity.drawSurface.drawingRectHelper.mainRect);
        mCamera.autoFocus(autoFocusManager);
    }

    public  void SetTouchFocus(RectF rectangle)
    {
        if (touchtofocus == false && !autoFocusManager.focusing)
        {
            touchtofocus = true;
        //Convert from View's width and height to +/- 1000

            final Rect targetFocusRect = new Rect(
                    (int)rectangle.left * 2000/activity.drawSurface.getWidth() - 1000,
                    (int)rectangle.top * 2000/activity.drawSurface.getHeight() - 1000,
                    (int)rectangle.right * 2000/activity.drawSurface.getWidth() - 1000,
                    (int)rectangle.bottom * 2000/activity.drawSurface.getHeight() - 1000);

            Rect top = new Rect(-999, -999, 999, targetFocusRect.top);
            Rect bottom = new Rect(-999, targetFocusRect.bottom, 999, 999);
            Rect left = new Rect(-999, targetFocusRect.top, targetFocusRect.left, targetFocusRect.bottom);
            Rect right = new Rect(targetFocusRect.right, targetFocusRect.top, 999, targetFocusRect.bottom);

            final List<Camera.Area> meteringList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
            Camera.Area topArea = new Camera.Area(top,1);
            Camera.Area bottomArea = new Camera.Area(bottom,1);
            Camera.Area leftArea = new Camera.Area(left,1);
            Camera.Area rightArea = new Camera.Area(right,1);
            meteringList.add(focusArea);
            meteringList.add(topArea);
            meteringList.add(bottomArea);
            meteringList.add(leftArea);
            meteringList.add(rightArea);
            if (parametersManager.getParameters().getMaxNumFocusAreas() > 0 && parametersManager.getParameters().getMaxNumMeteringAreas() > 0)
            {
                parametersManager.getParameters().setFocusAreas(meteringList);
                try
                {
                    mCamera.setParameters(parametersManager.getParameters());
                    //mCamera.autoFocus(autoFocusManager);
                }
                catch (Exception ex)
                {
                    Log.d("TouchToFocus", "failed to set focusareas");
                }

                parametersManager.getParameters().setMeteringAreas(meteringList);
                try
                {
                    mCamera.setParameters(parametersManager.getParameters());
                }
                catch (Exception ex)
                {
                    Log.d("TouchToFocus", "failed to set meteringareas");
                }
            }
        }
        else
        {
            autoFocusManager.CancelFocus();
            touchtofocus = false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
       //parameters = mCamera.getParameters();
       Restart(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.

        Start();
        Running = true;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Stop();
        Running = false;

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        /*if (parameters != null && parameters.getFocusMode().equals("auto"))
        {
            if (takePicture == false)
            {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float deltaX  = Math.abs(mLastX - x);
                float deltaY = Math.abs(mLastY - y);
                float deltaZ = Math.abs(mLastZ - z);

                if ((deltaX > 15 || deltaY > 15 || deltaZ > 15) && autoFocusManager.focusing){ //AUTOFOCUS (while it is not autofocusing)
                    autoFocusManager.focusing = false;
                    mCamera.autoFocus(autoFocusManager);
                    mLastX = x;
                    mLastY = y;
                    mLastZ = z;
                }
            }
        }*/




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
