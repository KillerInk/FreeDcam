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

import com.troop.freecam.camera.BaseCamera;
import com.troop.freecam.camera.PictureCam;
import com.troop.freecam.camera.VideoCam;
import com.troop.freecam.manager.AutoFocusManager;
import com.troop.freecam.manager.HdrManager;
import com.troop.freecam.manager.ManualBrightnessManager;
import com.troop.freecam.manager.ManualContrastManager;
import com.troop.freecam.manager.ManualExposureManager;
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
    public static final String SwitchCamera = "switchcam";
    public static final String SwitchCamera_MODE_3D = "3D";
    public static final String SwitchCamera_MODE_2D = "2D";
    public static final String SwitchCamera_MODE_Front = "Front";
    public static final String Preferences_Flash3D = "3d_flash";
    public static final String Preferences_Flash2D = "2d_flash";
    public static final String Preferences_Focus2D = "2d_focus";
    public static final String Preferences_Focus3D = "3d_focus";
    public static final String Preferences_FocusFront = "front_focus";
    public static final String Preferences_WhiteBalanceFront = "front_whitebalance";
    public static final String Preferences_WhiteBalance3D = "3d_whitebalance";
    public static final String Preferences_WhiteBalance2D = "2d_whitebalance";
    public static final String Preferences_SceneFront = "front_scene";
    public static final String Preferences_Scene3D = "3d_scene";
    public static final String Preferences_Scene2D = "2d_scene";
    public static final String Preferences_Color2D = "2d_color";
    public static final String Preferences_Color3D = "3d_color";
    public static final String Preferences_ColorFront = "front_color";
    public static final String Preferences_IsoFront = "front_iso";
    public static final String Preferences_Iso3D = "3d_iso";
    public static final String Preferences_Iso2D = "2d_iso";
    public static final String Preferences_Exposure2D = "2d_exposure";
    public static final String Preferences_Exposure3D = "3d_exposure";
    public static final String Preferences_ExposureFront = "front_exposure";
    public static final String Preferences_PictureSize2D = "2d_picturesize";
    public static final String Preferences_PictureSize3D = "3d_picturesize";
    public static final String Preferences_PictureSizeFront = "front_picturesize";
    public static final String Preferences_PreviewSize2D = "2d_previewsize";
    public static final String Preferences_PreviewSize3D = "3d_previewsize";
    public static final String Preferences_PreviewSizeFront = "front_previewsize";
    public static final String Preferences_IPP2D = "2d_ipp";
    public static final String Preferences_IPP3D = "3d_ipp";
    public static final String Preferences_IPPFront = "front_ipp";

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
        HdrRender = new HdrManager(this);
        parametersManager = new ParametersManager(this);
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
        String tmp = preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);

        if(!tmp.equals(CameraManager.SwitchCamera_MODE_3D) && !tmp.equals(CameraManager.SwitchCamera_MODE_2D))
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
        String tmp = preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);

        if(!tmp.equals(CameraManager.SwitchCamera_MODE_3D) && !tmp.equals(CameraManager.SwitchCamera_MODE_2D))
        {
           // mCamera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }
        else
        {
            //mCamera.setDisplayOrientation(180);
            parameters.setRotation(180);
        }
    }

    //if startstop true cam preview will be stopped and restartet
    public  void Restart(boolean startstop)
    {
        if (startstop)
        {
            parameters = mCamera.getParameters();
            mCamera.stopPreview();
            parameters.set("jpeg-quality", 100);
            parameters.set("contrast", 100);
            //parameters.setExposureCompensation(0);
            parameters.set("preview-format", "yuv420p");
            if (preferences.getBoolean("upsidedown", false) == true)
                fixParametersOrientation();

            String tmp = preferences.getString(SwitchCamera, SwitchCamera_MODE_3D);
            activity.switch3dButton.setText(tmp);

            if (tmp.equals("3D"))
            {
                parameters.setFlashMode(preferences.getString(Preferences_Flash3D, "auto"));
                parameters.setFocusMode(preferences.getString(Preferences_Focus3D, "auto"));
                parameters.setWhiteBalance(preferences.getString(Preferences_WhiteBalance3D,"auto"));
                parameters.setSceneMode(preferences.getString(Preferences_Scene3D,"auto"));
                parameters.setColorEffect(preferences.getString(Preferences_Color3D,"none"));
                parameters.set("iso", preferences.getString(Preferences_Iso3D, "auto"));
                parameters.set("exposure", preferences.getString(Preferences_Exposure3D , "auto"));
                setPictureSize(preferences.getString(Preferences_PictureSize3D , "320x240"));
                //setPictureSize("2592x1458");
                setPreviewSize(preferences.getString(Preferences_PreviewSize3D, "320x240"));
                parameters.set("ipp",preferences.getString(Preferences_IPP3D, "ldc-nsf"));

            }

            if(tmp.equals("2D"))
            {
                parameters.setFlashMode(preferences.getString(Preferences_Flash2D, "auto"));
                parameters.setFocusMode(preferences.getString(Preferences_Focus2D, "auto"));
                parameters.setWhiteBalance(preferences.getString(Preferences_WhiteBalance2D,"auto"));
                parameters.setSceneMode(preferences.getString(Preferences_Scene2D,"auto"));
                parameters.setColorEffect(preferences.getString(Preferences_Color2D,"none"));
                parameters.set("iso", preferences.getString(Preferences_Iso2D, "auto"));
                parameters.set("exposure", preferences.getString(Preferences_Exposure2D , "auto"));
                setPictureSize(preferences.getString(Preferences_PictureSize2D , "320x240"));
                setPreviewSize(preferences.getString(Preferences_PreviewSize2D, "320x240"));
                parameters.set("ipp",preferences.getString(Preferences_IPP2D, "ldc-nsf"));
            }
            if (tmp.equals("Front"))
            {
                parameters.setFocusMode(preferences.getString(Preferences_FocusFront, "auto"));
                parameters.setWhiteBalance(preferences.getString(Preferences_WhiteBalanceFront,"auto"));
                parameters.setSceneMode(preferences.getString(Preferences_SceneFront,"auto"));
                parameters.setColorEffect(preferences.getString(Preferences_ColorFront,"none"));
                parameters.set("iso", preferences.getString(Preferences_IsoFront, "auto"));
                parameters.set("exposure", preferences.getString(Preferences_ExposureFront , "auto"));
                setPictureSize(preferences.getString(Preferences_PictureSizeFront , "320x240"));
                setPreviewSize(preferences.getString(Preferences_PreviewSizeFront, "320x240"));
                parameters.set("ipp",preferences.getString(Preferences_IPPFront, "ldc-nsf"));
            }

            if (parameters.getFocusMode().equals("auto"))
            {
                activity.drawSurface.drawingRectHelper.Enabled = true;
            }
            else
            {
                activity.drawSurface.drawingRectHelper.Enabled = false;
            }

        }
        //parameters.set("gbce","true");
        //String maxSharpnessString = parameters.get("max-sharpness");
        //String t = parameters.flatten();
        //parameters.set("mode-values", "exposure-bracketing");
        //int pis = parameters.getPictureFormat();
        //parameters.setPictureFormat(ImageFormat.RGB_565);
        //parameters.set("exposure", "manual");
        //parameters.set("manual-exposure-right-compensation", 150);
        //parameters.set("manual-exposure-compensation", 1500);
        //parameters.set("manual-exposure-left-compensation", 1500);
        //parameters.setAutoExposureLock(true);
        //parameters.setAutoWhiteBalanceLock(true);
        //parameters.setExposureCompensation(30);

        try
        {
            //set parameters
            mCamera.setParameters(parameters);
            //get parameters to see if changed
            //parameters = mCamera.getParameters();
        }
        catch (Exception ex)
        {
            Log.e("Parameters Set Fail: ", ex.getMessage());
            parameters = mCamera.getParameters();
        }
        activity.flashButton.setText(parameters.getFlashMode());
        activity.focusButton.setText(parameters.getFocusMode());
        activity.sceneButton.setText(parameters.getSceneMode());
        activity.whitebalanceButton.setText(parameters.getWhiteBalance());
        activity.colorButton.setText(parameters.getColorEffect());
        activity.isoButton.setText(parameters.get("iso"));
        activity.exposureButton.setText(parameters.get("exposure"));


        activity.sharpnessTextView.setText("Sharpness: " + parameters.getInt("sharpness"));
        //if (!parameters.get("exposure").equals("manual"))
            activity.exposureTextView.setText("Exposure: " + parameters.getExposureCompensation());
        //else
            //activity.exposureTextView.setText("Exposure: " + parameters.getInt("manual-exposure"));
        activity.contrastTextView.setText("Contrast: " + parameters.get("contrast"));
        activity.saturationTextView.setText("Saturation: " + parameters.get("saturation"));
        activity.brightnessTextView.setText("Brightness: " + parameters.get("brightness"));
        activity.previewSizeButton.setText(parameters.getPreviewSize().width + "x" + parameters.getPreviewSize().height);
        String size1 = String.valueOf(parameters.getPictureSize().width) + "x" + String.valueOf(parameters.getPictureSize().height);
        activity.pictureSizeButton.setText(size1);
        activity.ippButton.setText(parameters.get("ipp"));

        activity.saturationCheckBox.setText(parameters.get("saturation"));

        activity.brightnessCheckBox.setText(parameters.get("brightness"));
        activity.contrastRadioButton.setText(parameters.get("contrast"));
        activity.manualShaprness.setText(parameters.get("sharpness"));



        if (startstop)
        {

            //int max = 60; //parameters.getMaxExposureCompensation() - parameters.getMinExposureCompensation();
            //if (!parameters.get("exposure").equals("manual"))
            //{
                manualExposureManager.SetMinMax(parameters.getMinExposureCompensation(), parameters.getMaxExposureCompensation());
                manualExposureManager.ExternalSet = true;
                manualExposureManager.SetCurrentValue(parameters.getExposureCompensation());
            /*}
            else
            {
                manualExposureManager.SetMinMax(1, 125);
                manualExposureManager.ExternalSet = true;
                manualExposureManager.SetCurrentValue(parameters.getInt("manual-exposure"));
            }*/

            //activity.exposureSeekbar.setMax(max);

            //activity.exposureSeekbar.setProgress(parameters.getExposureCompensation() + parameters.getMaxExposureCompensation());
            activity.sharpnessSeekBar.setMax(180);
            activity.sharpnessSeekBar.setProgress(parameters.getInt("sharpness"));
            activity.contrastSeekBar.setMax(180);
            manualContrastManager.ExternalSet = true;
            activity.contrastSeekBar.setProgress(parameters.getInt("contrast"));
            activity.brightnessSeekBar.setMax(100);
            activity.brightnessSeekBar.setProgress(parameters.getInt("brightness"));
            activity.saturationSeekBar.setMax(180);
            activity.crop_box.setChecked(preferences.getBoolean("crop", false));
            crop = activity.crop_box.isChecked();
            mCamera.startPreview();
        }
    }

    private void setPictureSize(String s)
    {
        String[] widthHeight = s.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPictureSize(w,h);
    }
    private void setPreviewSize(String s)
    {
        String[] widthHeight = s.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPreviewSize(w, h);
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
        if (takePicture == false)
        {
            Log.d("StartTakingPicture", "takepicture:" + takePicture);
            Log.d("StartTakingPicture", "touchtofocus:" + touchtofocus);
            takePicture = true;
            if (parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO))
            {

                if (activity.drawSurface.drawingRectHelper.drawRectangle == true)
                {
                    SetTouchFocus(activity.drawSurface.drawingRectHelper.mainRect);
                    autoFocusManager.focusing = true;
                    if (autoFocusManager.hasFocus)
                        TakePicture(crop);
                    else
                        mCamera.autoFocus(autoFocusManager);
                }
                else if (touchtofocus == false)
                {
                    touchtofocus = true;
                    autoFocusManager.focusing = false;
                    mCamera.autoFocus(this.autoFocusManager);

                }
            }
            else
            {
                TakePicture(crop);
            }
        }
    }

    public  void SetTouchFocus(RectF rectangle)
    {
        if (touchtofocus == false)
        {
            touchtofocus = true;
        //Convert from View's width and height to +/- 1000

            final Rect targetFocusRect = new Rect(
                    (int)rectangle.left * 2000/context.getWidth() - 1000,
                    (int)rectangle.top * 2000/context.getHeight() - 1000,
                    (int)rectangle.right * 2000/context.getWidth() - 1000,
                    (int)rectangle.bottom * 2000/context.getHeight() - 1000);

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
            if (parameters.getMaxNumFocusAreas() > 0 && parameters.getMaxNumMeteringAreas() > 0)
            {
                parameters.setFocusAreas(meteringList);
                try
                {
                    mCamera.setParameters(parameters);
                    //mCamera.autoFocus(autoFocusManager);
                }
                catch (Exception ex)
                {
                    Log.d("TouchToFocus", "failed to set focusareas");
                }

                parameters.setMeteringAreas(meteringList);
                try
                {

                    mCamera.setParameters(parameters);
                    /*try
                    {
                        mCamera.autoFocus(autoFocusManager);
                    }
                    catch (Exception ex)
                    {
                        Log.d("TakingPicture Focus Faild", ex.getMessage());
                    }*/
                }
                catch (Exception ex)
                {
                    Log.d("TouchToFocus", "failed to set meteringareas");
                }


                //}
            }
        }
        else
        {
            mCamera.cancelAutoFocus();
            autoFocusManager.focusing = false;
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
