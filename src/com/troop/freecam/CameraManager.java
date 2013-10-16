package com.troop.freecam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.SurfaceView;

import com.troop.freecam.cm.HdrSoftwareProcessor;
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
public class CameraManager implements SurfaceHolder.Callback , SensorEventListener
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
    public Camera mCamera;
    //MediaScannerManager scanManager;
    CameraManager cameraManager;
    public  Camera.Parameters parameters;
    public ZoomManager zoomManager;
    public boolean Running = false;
    public MediaScannerManager scanManager;
    public AutoFocusManager autoFocusManager;
    public static final String KEY_CAMERA_INDEX = "camera-index";
    public static final String KEY_S3D_SUPPORTED_STR = "s3d-supported";
    //public boolean picturetaking = false;
    public boolean touchtofocus = false;
    public MainActivity activity;
    public SharedPreferences preferences;
    public ManualExposureManager manualExposureManager;
    public String lastPicturePath;
    public ManualSharpnessManager manualSharpnessManager;
    public ManualContrastManager manualContrastManager;
    public ManualBrightnessManager manualBrightnessManager;
    public HdrManager HdrRender;
    public ParametersManager parametersManager;

    float mLastX;
    float mLastZ;
    float mLastY;
    MediaRecorder recorder;
    String mediaSavePath;


    public boolean takePicture = false;

    public CameraManager(CamPreview context, MainActivity activity)
    {
        this.context = context;
        scanManager = new MediaScannerManager(context.getContext());
        context.mHolder.addCallback(this);
        zoomManager = new ZoomManager(this);
        autoFocusManager = new AutoFocusManager(this);
        this.activity = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        manualExposureManager = new ManualExposureManager(this);
        cameraManager = this;
        manualSharpnessManager = new ManualSharpnessManager(this);
        manualContrastManager = new ManualContrastManager(this);
        manualBrightnessManager = new ManualBrightnessManager(this);
        HdrRender = new HdrManager(this);
        parametersManager = new ParametersManager(this);

    }

    public  void Start()
    {
        String tmp = preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);
        //mCamera.unlock();
        if (tmp.equals(CameraManager.SwitchCamera_MODE_3D))
            mCamera = Camera.open(2);
        if(tmp.equals(CameraManager.SwitchCamera_MODE_2D))
            mCamera = Camera.open(0);
            //mCamera.setDisplayOrientation(90);
        if (tmp.equals(CameraManager.SwitchCamera_MODE_Front))
            mCamera = Camera.open(1);

        try {
            mCamera.setPreviewDisplay(context.mHolder);
            mCamera.setZoomChangeListener(zoomManager);
            if(preferences.getBoolean("upsidedown", false) == true)
                fixCameraDisplayOrientation();
            zoomManager.ResetZoom();
        } catch (Exception exception) {
            mCamera.release();
            mCamera = null;

            // TODO: add more exception handling logic here
        }

        recorder = new MediaRecorder();

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
        if (IsRecording)
            StopRecording();
        recorder.reset();
        recorder.release();
        recorder = null;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

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
                        TakePicture();
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
                TakePicture();
            }
        }
    }

    public void TakePicture()
    {

            mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);

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

    public boolean IsRecording = false;
    public void StartRecording()
    {
        mCamera.unlock();
        File sdcardpath = Environment.getExternalStorageDirectory();

        recorder.setCamera(mCamera);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (parameters.getPreviewSize().height == 1080)
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
        if (parameters.getPreviewSize().height == 720)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
            if (parameters.getPreviewSize().width == 960)
                recorder.setVideoSize(960, 720);
            else
                recorder.setVideoSize(1280,720);
        }
        if (parameters.getPreviewSize().height == 480)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
            if (parameters.getPreviewSize().height == 800)
                recorder.setVideoSize(800, 480);
            if (parameters.getPreviewSize().height == 640)
                recorder.setVideoSize(640,480);

        }
        /*if (parameters.getPreviewSize().height == 576)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
            recorder.setVideoSize(720,576);
        }*/
        if (parameters.getPreviewSize().height == 240)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
            recorder.setVideoSize(320,240);
        }
        if (parameters.getPreviewSize().height == 288)
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
        if (parameters.getPreviewSize().height == 160)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
            recorder.setVideoSize(240,160);
        }
        if (parameters.getPreviewSize().height == 144)
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_QCIF));

        if (preferences.getBoolean("upsidedown", false) == true)
        {
            String rota = parameters.get("rotation");

            if (rota != null && rota.equals("180"))
                recorder.setOrientationHint(180);
            if (rota == null)
                recorder.setOrientationHint(0);
        }
        //recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaSavePath = SavePictureTask.getFilePath("mp4", sdcardpath).getAbsolutePath();
        recorder.setOutputFile(mediaSavePath);
        recorder.setPreviewDisplay(context.getHolder().getSurface());
        try {
            recorder.prepare();
            recorder.start();
            IsRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  void StopRecording()
    {
        IsRecording = false;
        recorder.stop();
        scanManager.startScan(mediaSavePath);
        lastPicturePath = mediaSavePath;
        recorder.reset();
        mCamera.lock();
    }

    public Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {

            MediaPlayer mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), R.raw.camerashutter);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            //mediaPlayer.setVolume(1,1);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
            Log.d("FreeCam", "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    public Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("FreeCam", "onPictureTaken - raw");
        }
    };
    SavePictureTask task;
    /** Handles data for jpeg picture */
    public Camera.PictureCallback jpegCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera)
        {
            Log.d("PictureCallback", "DATAsize:" + data.length);
            boolean is3d = false;
            if (preferences.getString("switchcam", "3D").equals("3D"))
            {
                is3d = true;
            }

            task = new SavePictureTask(scanManager, is3d, cameraManager);



            task.execute(data);


            //activity.thumbButton.invalidate();


            mCamera.startPreview();
            takePicture = false;

        }
    };

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
