package com.troop.freecam.camera;

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
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.os.StatFs;


import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.AutoFocusManager;
import com.troop.freecam.manager.HdrManager;
import com.troop.freecam.manager.ManualBrightnessManager;
import com.troop.freecam.manager.ManualContrastManager;
import com.troop.freecam.manager.ManualExposureManager;
import com.troop.freecam.manager.ManualFocus;
import com.troop.freecam.manager.ManualSharpnessManager;
import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecam.manager.ParametersManager;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.manager.ZoomManager;
import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.utils.DeviceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 25.08.13.
 */
public class CameraManager extends VideoCam implements SurfaceHolder.Callback , SensorEventListener
{
    MainActivity mainActivity;


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
    //public ParametersManager parametersManager;
    public boolean takePicture = false;



    public CameraManager(CamPreview context, MainActivity activity, SettingsManager settingsManager)
    {
        super(context, settingsManager);
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
        parametersManager = new ParametersManager(this, settingsManager);

    }


    
    //Remaining Pictures Ca;cu;ation
    public int RemainingPics ()
    {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;

        int mb = (int) megAvailable / 10;

        return mb;


    }
//WIP
    public int VideoLength ()
    {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;

        int mb = (int) megAvailable / 10;

        return mb;

    }
    //Aspect ratio Calc
    public String Aspect()
    {
        int w = parametersManager.getParameters().getPictureSize().width;
        int h = parametersManager.getParameters().getPictureSize().height;

        String box = "4:3";
        String wide = "16:9";

        double ar = w / h;


        if (ar == 1.777777777777778)
            return wide;

        if(ar == 1.333333333333333)
            return box;


        return box;

    }
    
    //On Picture Size Detect Do Action
    public void DoAr()
    {
        if (Double.parseDouble(Aspect()) == 1.777777777777778)
            parametersManager.getParameters().setPreviewSize(1920,1080);


        if(DeviceUtils.isG2())
            if(Double.parseDouble(Aspect()) == 1.333333333333333)
                parametersManager.getParameters().setPreviewSize(1440,1080);

        if(Double.parseDouble(Aspect()) == 1.333333333333333)
            parametersManager.getParameters().setPreviewSize(1440,1080);

        //532 Layer


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
            options.inSampleSize = 16;
            Bitmap bitmaporg = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            //mediaScannerManager.startScan(s);

            int w = cameraManager.activity.thumbButton.getWidth();
            int h = cameraManager.activity.thumbButton.getHeight();
            bitmascale = Bitmap.createScaledBitmap(bitmaporg,w,h,true);
            cameraManager.activity.thumbButton.setImageBitmap(bitmascale);
            cameraManager.lastPicturePath =file.getAbsolutePath();
            scanManager.startScan(lastPicturePath);
            bitmaporg.recycle();
            
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
            mCamera.setPreviewDisplay(activity.mPreview.mHolder);
            mCamera.setZoomChangeListener(zoomManager);
            if(Settings.OrientationFix.GET() == true)
                fixCameraDisplayOrientation();
            zoomManager.ResetZoom();
        } catch (Exception exception) {
            CloseCamera();

            // TODO: add more exception handling logic here
        }
    }

    private void fixCameraDisplayOrientation()
    {
        String tmp = Settings.Cameras.GetCamera();

        if(!tmp.equals(SettingsManager.Preferences.MODE_3D) && !tmp.equals(SettingsManager.Preferences.MODE_2D))
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



    //if restarted true cam preview will be stopped and restartet
    public  void Restart(boolean restarted)
    {
        if (restarted)
        {
            parametersManager.SetCameraParameters(mCamera.getParameters());

            mCamera.stopPreview();
            parametersManager.SetJpegQuality(100);
            parametersManager.SetContrast(100);
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

    public void AutoFocusAssit()
    {
        String atr = "torch";
        parametersManager.getParameters().setFlashMode(atr);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

               // parametersManager.setFlashMode(preferences.getString(Preferences_Flash2D, "auto"));
            }
        }, 2000); //time in millis
    }

    public  void SetTouchFocus(RectF rectangle)
    {
        //Attempt at af Assit light
        /*if (mainActivity.AFS_enable == true)
        {
            AutoFocusAssit();

        }*/

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
