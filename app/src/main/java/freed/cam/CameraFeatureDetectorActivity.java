package freed.cam;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.lge.hardware.LGCamera;
import com.troop.freedcam.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import freed.ActivityAbstract;
import freed.cam.apis.KEYS;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.cam.apis.camera1.parameters.DeviceSelector;
import freed.cam.apis.camera1.parameters.device.I_Device;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils;
import freed.utils.LocationHandler;
import freed.utils.StringFloatArray;
import freed.utils.StringUtils;
import freed.viewer.holder.FileHolder;

import static freed.cam.apis.KEYS.AE_BRACKET_HDR_VALUES;
import static freed.cam.apis.KEYS.BAYER;
import static freed.cam.apis.KEYS.JPEG;

/**
 * Created by troop on 27.12.2016.
 */

public class CameraFeatureDetectorActivity extends ActivityAbstract
{
    private final String TAG = CameraFeatureDetectorActivity.class.getSimpleName();
    private TextView loggerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerafeaturedetector);
        loggerview = (TextView)findViewById(R.id.textview_log);
        loggerview.setMovementMethod(new ScrollingMovementMethod());
        if (hasCameraPermission()) {
            new Camera1AsyncTask().execute("");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public LocationHandler getLocationHandler() {
        return null;
    }

    @Override
    public void WorkHasFinished(FileHolder fileHolder) {

    }

    @Override
    public void WorkHasFinished(FileHolder[] fileHolder) {

    }

    @Override
    protected void cameraPermsissionGranted(boolean granted) {
        Log.d(TAG, "cameraPermission Granted:" + granted);
        if (granted) {
            new Camera1AsyncTask().execute("");
        }
        else {
            finish();
        }
    }

    private void sendLog(String log)
    {
        String tmp = log + " \n"+ loggerview.getText().toString();
        loggerview.setText(tmp);
    }

    private String getStringFromArray(String[] arr)
    {
        String t = "";
        for (int i =0; i<arr.length;i++)
            t+=arr[i]+",";
        return t;
    }

    private void startFreedcam()
    {
        getAppSettings().setAreFeaturesDetected(true);
        Intent intent = new Intent(this, ActivityFreeDcamMain.class);
        startActivity(intent);
        this.finish();
    }

    private class Camera1AsyncTask extends AsyncTask<String,String, String>
    {

        @Override
        protected String doInBackground(String... params)
        {
            publishProgress("###################");
            publishProgress("#######Camera1#####");
            publishProgress("###################");
            getAppSettings().setCamApi(AppSettingsManager.API_1);
            //detect Device
            if (getAppSettings().getDevice() == null)
                getAppSettings().SetDevice(new DeviceUtils().getDevice(getResources()));
            publishProgress("Device:"+getAppSettings().getDevice().name());
            //detect frameworks
            getAppSettings().setFramework(getFramework());
            publishProgress("FrameWork:"+getAppSettings().getFrameWork());
            //can open legcay
            getAppSettings().setCanOpenLegacy(canOpenLegacy());
            publishProgress("CanOpenLegacy:"+getAppSettings().getCanOpenLegacy());

                Camera camera = null;
                int cameraCounts = Camera.getNumberOfCameras();
                AppSettingsManager appS = getAppSettings();
                for (int i = 0; i < cameraCounts; i++)
                {
                    publishProgress("###################");
                    publishProgress("#####CameraID:"+i+"####");
                    publishProgress("###################");
                    appS.SetCurrentCamera(i);
                    detectFrontCamera(i);
                    publishProgress("isFrontCamera:"+appS.getIsFrontCamera() + " CameraID:"+ i);

                    Camera.Parameters parameters = getParameters(i);
                    publishProgress("Detecting Features");
                    I_Device device = new DeviceSelector().getDevice(null,parameters, appS);

                    detectedPictureFormats(parameters,device);
                    publishProgress("DngSupported:" + device.IsDngSupported() + " RawSupport:"+appS.rawPictureFormat.isSupported());
                    publishProgress("PictureFormats:" + getStringFromArray(appS.pictureFormat.getValues()));
                    publishProgress("RawFormats:" + getStringFromArray(appS.rawPictureFormat.getValues()));
                    publishProgress(" RawFormat:" + appS.rawPictureFormat.get());

                    detectPictureSizes(parameters);
                    sendProgress(appS.pictureSize,"PictureSize");

                    detectFocusModes(parameters);
                    sendProgress(appS.focusMode,"FocusMode");

                    detectWhiteBalanceModes(parameters);
                    sendProgress(appS.whiteBalanceMode,"WhiteBalance");

                    detectExposureModes(parameters);
                    sendProgress(appS.exposureMode,"ExposureMode");

                    detectColorModes(parameters);
                    sendProgress(appS.colorMode,"Color");

                    detectFlashModes(parameters);
                    sendProgress(appS.flashMode,"FLash");

                    detectIsoModes(parameters);
                    sendProgress(appS.isoMode,"Iso");

                    detectAntiBandingModes(parameters);
                    sendProgress(appS.antiBandingMode,"AntiBanding");

                    detectImagePostProcessingModes(parameters);
                    sendProgress(appS.imagePostProcessing,"ImagePostProcessing");

                    detectPreviewSizeModes(parameters);
                    sendProgress(appS.previewSize,"PreviewSize");

                    detectJpeqQualityModes(parameters);
                    sendProgress(appS.jpegQuality,"JpegQuality");

                    detectAeBracketModes(parameters);
                    sendProgress(appS.aeBracket,"AeBracket");

                    detectPreviewFPSModes(parameters);
                    sendProgress(appS.previewFps,"PreviewFPS");

                    detectPreviewFormatModes(parameters);
                    sendProgress(appS.previewFormat,"PreviewFormat");

                    detectSceneModes(parameters);
                    sendProgress(appS.sceneMode,"Scene");

                    detectLensShadeModes(parameters);
                    sendProgress(appS.lenshade,"Lensshade");

                    detectZeroShutterLagModes(parameters);
                    sendProgress(appS.zeroshutterlag,"ZeroShutterLag");

                    detectSceneDetectModes(parameters);
                    sendProgress(appS.sceneDetectMode,"SceneDetect");

                    detectMemoryColorEnhancementModes(parameters);
                    sendProgress(appS.memoryColorEnhancement,"MemoryColorEnhancement");

                    detectVideoSizeModes(parameters);
                    sendProgress(appS.videoSize,"VideoSize");


                    detectCorrelatedDoubleSamplingModes(parameters);
                    sendProgress(appS.correlatedDoubleSampling,"CorrelatedDoubleSampling");

                    detectOisModes(parameters);
                    sendProgress(appS.opticalImageStabilisation, "OpticalImageStabilisation");

                    detectVideoHdr(parameters);
                    sendProgress(appS.videoHDR, "VideoHDR");

                    detectVideoHFR(parameters);
                    sendProgress(appS.videoHFR,"VideoHFR");
                }

            return null;
        }

        private void sendProgress(AppSettingsManager.SettingMode settingMode, String name)
        {
            if (settingMode.isSupported()) {
                String[]ar = settingMode.getValues();
                String t = getStringFromArray(ar);
                publishProgress(name+" Values:" +t);
                publishProgress(name+":" + settingMode.get());
            }
            else
                publishProgress(name+" not supported");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            sendLog(values[0]);
            Log.d(TAG, values[0]);
        }

        private void detectFrontCamera(int i) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i,info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                getAppSettings().setIsFrontCamera(false);
            else
                getAppSettings().setIsFrontCamera(true);
        }

        private boolean hasLGFramework()
        {
            try {
                Class c = Class.forName("com.lge.hardware.LGCamera");
                Log.d(TAG, "Has Lg Framework");
                c = Class.forName("com.lge.media.CamcorderProfileEx");
                Log.d(TAG, "Has Lg Framework");
                return true;

            } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {

                Log.d(TAG, "No LG Framework");
                return false;
            }
        }

        private boolean isMotoExt()
        {
            try {
                Class c = Class.forName("com.motorola.android.camera.CameraMotExt");
                Log.d(TAG, "Has Moto Framework");
                c = Class.forName("com.motorola.android.media.MediaRecorderExt");
                Log.d(TAG, "Has Moto Framework");
                return true;

            } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {
                Log.d(TAG, "No Moto Framework");
                return false;
            }
        }

        private boolean isMTKDevice()
        {
            try
            {
                Class camera = Class.forName("android.hardware.Camera");
                Method[] meths = camera.getMethods();
                Method app = null;
                for (Method m : meths)
                {
                    if (m.getName().equals("setProperty"))
                        app = m;
                }
                if (app != null) {
                    Log.d(TAG,"MTK Framework found");
                    return true;
                }
                Log.d(TAG, "MTK Framework not found");
                return false;
            }
            catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e)
            {
                e.printStackTrace();
                Log.d(TAG, "MTK Framework not found");
                return false;
            }
        }

        private int getFramework()
        {
            if (hasLGFramework())
                return AppSettingsManager.FRAMEWORK_LG;
            else if (isMTKDevice())
                return AppSettingsManager.FRAMEWORK_MTK;
            else if (isMotoExt())
                return AppSettingsManager.FRAMEWORK_MOTO_EXT;
            else
                return AppSettingsManager.FRAMEWORK_NORMAL;
        }

        private boolean canOpenLegacy()
        {
            try {
                Class[] arrclass = {Integer.TYPE, Integer.TYPE};
                Method method = Class.forName("android.hardware.Camera").getDeclaredMethod("openLegacy", arrclass);
                if (method != null)
                    return true;
                else
                    return false;
            }
            catch
                    (NoSuchMethodException e) {
                e.printStackTrace();
                return false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        private Camera.Parameters getParameters(int currentcamera)
        {
            Camera camera;
            switch (getAppSettings().getFrameWork())
            {
                case AppSettingsManager.FRAMEWORK_LG:
                {
                    LGCamera lgCamera;
                    if (getAppSettings().getDevice() == DeviceUtils.Devices.LG_G4 || getAppSettings().getDevice() == DeviceUtils.Devices.LG_V20)
                        lgCamera = new LGCamera(currentcamera, 256);
                    else
                        lgCamera = new LGCamera(currentcamera);
                    return lgCamera.getLGParameters().getParameters();
                }
                case AppSettingsManager.FRAMEWORK_MOTO_EXT:
                {
                    camera  = Camera.open(currentcamera);
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.set("mot-app", "true");
                    camera.setParameters(parameters);
                    parameters = camera.getParameters();
                    camera.release();
                    return parameters;
                }
                case AppSettingsManager.FRAMEWORK_MTK:
                {
                    CameraHolderMTK.setMtkAppMode();
                }
                default:
                {
                    camera  = Camera.open(currentcamera);
                    Camera.Parameters parameters = camera.getParameters();
                    camera.release();
                    return parameters;
                }

            }
        }

        private void detectedPictureFormats(Camera.Parameters parameters, I_Device device)
        {
            //drop raw for front camera
            if (getAppSettings().getIsFrontCamera())
            {
                getAppSettings().pictureFormat.setIsSupported(false);
                getAppSettings().rawPictureFormat.setIsSupported(false);
            }
            else {

                if (getAppSettings().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
                    getAppSettings().pictureFormat.setIsSupported(true);
                    getAppSettings().rawPictureFormat.setIsSupported(true);
                } else {
                    if (getAppSettings().getDevice() == DeviceUtils.Devices.LG_G2)
                    {
                        getAppSettings().pictureFormat.setIsSupported(true);
                        getAppSettings().rawPictureFormat.setIsSupported(true);
                        getAppSettings().rawPictureFormat.set(KEYS.BAYER_MIPI_10BGGR);
                    }
                    else if (getAppSettings().getDevice() == DeviceUtils.Devices.HTC_OneA9 )
                    {
                        getAppSettings().pictureFormat.setIsSupported(true);
                        getAppSettings().rawPictureFormat.setIsSupported(true);
                        getAppSettings().rawPictureFormat.set(KEYS.BAYER_MIPI_10RGGB);
                    }else if(getAppSettings().getDevice() == DeviceUtils.Devices.MotoG3 ||getAppSettings().getDevice() == DeviceUtils.Devices.MotoG_Turbo)
                    {
                        getAppSettings().pictureFormat.setIsSupported(true);
                        getAppSettings().rawPictureFormat.setIsSupported(true);
                        getAppSettings().rawPictureFormat.set(KEYS.BAYER_QCOM_10RGGB);
                    }

                    else if(getAppSettings().getDevice() == DeviceUtils.Devices.Htc_M8 && Build.VERSION.SDK_INT >= 21)
                    {
                        getAppSettings().pictureFormat.setIsSupported(true);
                        getAppSettings().rawPictureFormat.setIsSupported(true);
                        getAppSettings().rawPictureFormat.set(KEYS.BAYER_QCOM_10GRBG);
                    }
                    else
                    {
                        String formats = parameters.get(KEYS.PICTURE_FORMAT_VALUES);

                        if (formats.contains("bayer-mipi") || formats.contains("raw"))
                        {
                            getAppSettings().rawPictureFormat.setIsSupported(true);
                            String[] forms = formats.split(",");
                            for (String s : forms) {
                                if (s.contains("bayer-mipi") || s.contains("raw"))
                                {
                                    getAppSettings().rawPictureFormat.set(s);
                                    break;
                                }
                            }
                        }
                        if (formats.contains(BAYER))
                        {
                            ArrayList<String> tmp = new ArrayList<>();
                            String[] forms = formats.split(",");
                            for (String s : forms) {
                                if (s.contains(BAYER))
                                {
                                    tmp.add(s);
                                }
                            }
                            String[] rawFormats = new String[tmp.size()];
                            tmp.toArray(rawFormats);
                            getAppSettings().rawPictureFormat.setValues(rawFormats);
                        }
                    }
                }

                if (device.IsDngSupported())
                {
                    getAppSettings().pictureFormat.setValues(new String[]{
                            AppSettingsManager.CaptureMode[AppSettingsManager.JPEG], AppSettingsManager.CaptureMode[AppSettingsManager.DNG], AppSettingsManager.CaptureMode[AppSettingsManager.RAW]
                    });
                }
                else if (getAppSettings().rawPictureFormat.isSupported()) {
                        getAppSettings().pictureFormat.setValues(new String[]{
                                AppSettingsManager.CaptureMode[AppSettingsManager.JPEG], AppSettingsManager.CaptureMode[AppSettingsManager.RAW]
                        });
                }
                else
                {
                    getAppSettings().pictureFormat.setValues(new String[]{
                            AppSettingsManager.CaptureMode[AppSettingsManager.JPEG]
                    });
                }

            }
        }

        private void detectPictureSizes(Camera.Parameters parameters)
        {
            String[] sizes = parameters.get(KEYS.PICTURE_SIZE_VALUES).split(",");
            getAppSettings().pictureSize.setValues(sizes);
            getAppSettings().pictureSize.set(parameters.get(KEYS.PICTURE_SIZE));
            if (sizes.length > 0)
                getAppSettings().pictureSize.setIsSupported(true);
            else
                getAppSettings().pictureSize.setIsSupported(false);
        }

        private void detectFocusModes(Camera.Parameters parameters)
        {
            getAppSettings().focusMode.setValues(parameters.get(KEYS.FOCUS_MODE_VALUES).split(","));
            getAppSettings().focusMode.set(parameters.get(KEYS.FOCUS_MODE));
            if (getAppSettings().focusMode.getValues().length >0)
                getAppSettings().focusMode.setIsSupported(true);
            else
                getAppSettings().focusMode.setIsSupported(false);
        }

        private void detectWhiteBalanceModes(Camera.Parameters parameters)
        {
            getAppSettings().whiteBalanceMode.setValues(parameters.get(KEYS.WHITEBALANCE_VALUES).split(","));
            getAppSettings().whiteBalanceMode.set(parameters.get(KEYS.WHITEBALANCE));
            if (getAppSettings().whiteBalanceMode.getValues().length >0)
                getAppSettings().whiteBalanceMode.setIsSupported(true);
            else
                getAppSettings().whiteBalanceMode.setIsSupported(false);
        }

        private void detectExposureModes(Camera.Parameters parameters)
        {
            if (parameters.get("exposure-mode-values")!= null) {
                getAppSettings().exposureMode.setKEY("exposure");
                getAppSettings().exposureMode.set(parameters.get("exposure"));
                getAppSettings().exposureMode.setValues(parameters.get("exposure-mode-values").split(","));
            }
            else if (parameters.get("auto-exposure-values")!= null) {
                getAppSettings().exposureMode.setKEY("auto-exposure");
                getAppSettings().exposureMode.set(parameters.get("auto-exposure"));
                getAppSettings().exposureMode.setValues(parameters.get("auto-exposure-values").split(","));
            }
            else if(parameters.get("sony-metering-mode-values")!= null) {
                getAppSettings().exposureMode.setKEY("sony-metering-mode");
                getAppSettings().exposureMode.set(parameters.get("sony-metering-mode"));
                getAppSettings().exposureMode.setValues(parameters.get("sony-metering-mode-values").split(","));
            }
            else if(parameters.get("exposure-meter-values")!= null) {
                getAppSettings().exposureMode.setKEY("exposure-meter");
                getAppSettings().exposureMode.set(parameters.get("exposure-meter"));
                getAppSettings().exposureMode.setValues(parameters.get("exposure-meter-values").split(","));
            }
            if (!getAppSettings().exposureMode.getKEY().equals(""))
                getAppSettings().exposureMode.setIsSupported(true);
            else
                getAppSettings().exposureMode.setIsSupported(false);
        }

        private void detectColorModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.COLOR_EFFECT_VALUES) == null)
            {
                getAppSettings().colorMode.setIsSupported(false);
                return;
            }
            getAppSettings().colorMode.setValues(parameters.get(KEYS.COLOR_EFFECT_VALUES).split(","));
            getAppSettings().colorMode.set(parameters.get(KEYS.COLOR_EFFECT));
            if (getAppSettings().colorMode.getValues().length >0)
                getAppSettings().colorMode.setIsSupported(true);
        }

        private void detectFlashModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.FLASH_MODE_VALUES) == null)
            {
                getAppSettings().flashMode.setIsSupported(false);
                return;
            }
            getAppSettings().flashMode.setValues(parameters.get(KEYS.FLASH_MODE_VALUES).split(","));
            getAppSettings().flashMode.set(parameters.get(KEYS.FLASH_MODE));
            if (getAppSettings().flashMode.getValues().length >0)
                getAppSettings().flashMode.setIsSupported(true);
        }

        private void detectIsoModes(Camera.Parameters parameters)
        {
            if (parameters.get("iso-mode-values")!= null){
                getAppSettings().isoMode.setKEY("iso");
                getAppSettings().isoMode.setValues(parameters.get("iso-mode-values").split(","));
                getAppSettings().isoMode.set(parameters.get("iso"));
            }
            else if (parameters.get("iso-values")!= null) {
                getAppSettings().isoMode.setKEY("iso");
                getAppSettings().isoMode.setValues(parameters.get("iso-values").split(","));
                getAppSettings().isoMode.set(parameters.get("iso"));
            }
            else if (parameters.get("iso-speed-values")!= null) {
                getAppSettings().isoMode.setKEY("iso-speed");
                getAppSettings().isoMode.setValues(parameters.get("iso-speed-values").split(","));
                getAppSettings().isoMode.set(parameters.get("iso-speed"));
            }
            else if (parameters.get("sony-iso-values")!= null) {
                getAppSettings().isoMode.setKEY("sony-iso");
                getAppSettings().isoMode.setValues(parameters.get("sony-iso-values").split(","));
                getAppSettings().isoMode.set(parameters.get("sony-iso"));
            }
            else if (parameters.get("lg-iso-values")!= null) {
                getAppSettings().isoMode.setKEY("iso");
                getAppSettings().isoMode.setValues(parameters.get("lg-iso-values").split(","));
                getAppSettings().isoMode.set(parameters.get("iso"));
            }
            if (getAppSettings().isoMode.getValues().length >1)
                getAppSettings().isoMode.setIsSupported(true);
            else
                getAppSettings().isoMode.setIsSupported(false);
        }

        private void detectAntiBandingModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.ANTIBANDING_VALUES) == null)
            {
                getAppSettings().antiBandingMode.setIsSupported(false);
                return;
            }
            getAppSettings().antiBandingMode.setValues(parameters.get(KEYS.ANTIBANDING_VALUES).split(","));
            getAppSettings().antiBandingMode.set(parameters.get(KEYS.ANTIBANDING));
            if (getAppSettings().antiBandingMode.getValues().length >0)
                getAppSettings().antiBandingMode.setIsSupported(true);
        }

        private void detectImagePostProcessingModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.IMAGEPOSTPROCESSING_VALUES) == null)
            {
                getAppSettings().imagePostProcessing.setIsSupported(false);
                return;
            }
            getAppSettings().imagePostProcessing.setValues(parameters.get(KEYS.IMAGEPOSTPROCESSING_VALUES).split(","));
            getAppSettings().imagePostProcessing.set(parameters.get(KEYS.IMAGEPOSTPROCESSING));
            if (getAppSettings().imagePostProcessing.getValues().length >0)
                getAppSettings().imagePostProcessing.setIsSupported(true);
        }

        private void detectPreviewSizeModes(Camera.Parameters parameters)
        {
            if (parameters.get("preview-size-values") == null)
            {
                getAppSettings().previewSize.setIsSupported(false);
                return;
            }
            getAppSettings().previewSize.setValues(parameters.get("preview-size-values").split(","));
            getAppSettings().previewSize.set(parameters.get("preview-size"));
            if (getAppSettings().previewSize.getValues().length >0)
                getAppSettings().previewSize.setIsSupported(true);
        }

        private void detectJpeqQualityModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.JPEG_QUALITY) == null)
            {
                getAppSettings().jpegQuality.setIsSupported(false);
                return;
            }
            String[] valuetoreturn = new String[20];
            for (int i = 1; i < 21; i++)
            {
                valuetoreturn[i-1] = "" + i*5;
            }
            getAppSettings().jpegQuality.setValues(valuetoreturn);
            getAppSettings().jpegQuality.set(parameters.get(KEYS.JPEG_QUALITY));
            if (valuetoreturn.length >0)
                getAppSettings().jpegQuality.setIsSupported(true);
        }

        private void detectAeBracketModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.AE_BRACKET_HDR_VALUES) == null)
            {
                getAppSettings().aeBracket.setIsSupported(false);
                return;
            }
            getAppSettings().aeBracket.setValues(parameters.get(AE_BRACKET_HDR_VALUES).split(","));
            getAppSettings().aeBracket.set(parameters.get(KEYS.AE_BRACKET_HDR));
            if (getAppSettings().aeBracket.getValues().length >0)
                getAppSettings().aeBracket.setIsSupported(true);
        }

        private void detectPreviewFPSModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.PREVIEW_FRAME_RATE_VALUES) == null)
            {
                getAppSettings().previewFps.setIsSupported(false);
                return;
            }
            getAppSettings().previewFps.setValues(parameters.get(KEYS.PREVIEW_FRAME_RATE_VALUES).split(","));
            getAppSettings().previewFps.set(parameters.get(KEYS.PREVIEW_FRAME_RATE));
            if (getAppSettings().previewFps.getValues().length >0)
                getAppSettings().previewFps.setIsSupported(true);
        }

        private void detectPreviewFormatModes(Camera.Parameters parameters)
        {
            if (parameters.get("preview-format-values") == null)
            {
                getAppSettings().previewFormat.setIsSupported(false);
                return;
            }
            getAppSettings().previewFormat.setValues(parameters.get("preview-format-values").split(","));
            getAppSettings().previewFormat.set(parameters.get("preview-format"));
            if (getAppSettings().previewFormat.getValues().length >0)
                getAppSettings().previewFormat.setIsSupported(true);
        }

        private void detectSceneModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.SCENE_MODE_VALUES) == null)
            {
                getAppSettings().sceneMode.setIsSupported(false);
                return;
            }
            getAppSettings().sceneMode.setValues(parameters.get(KEYS.SCENE_MODE_VALUES).split(","));
            getAppSettings().sceneMode.set(parameters.get(KEYS.SCENE_MODE));
            if (getAppSettings().sceneMode.getValues().length >0)
                getAppSettings().sceneMode.setIsSupported(true);
        }

        private void detectLensShadeModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.LENSSHADE) == null)
            {
                getAppSettings().lenshade.setIsSupported(false);
                return;
            }
            getAppSettings().lenshade.setValues(parameters.get(KEYS.LENSSHADE_VALUES).split(","));
            getAppSettings().lenshade.set(parameters.get(KEYS.LENSSHADE));
            if (getAppSettings().lenshade.getValues().length >0)
                getAppSettings().lenshade.setIsSupported(true);
        }

        private void detectZeroShutterLagModes(Camera.Parameters parameters)
        {
            if (parameters.get("zsl") != null)
            {
                getAppSettings().zeroshutterlag.setValues(parameters.get("zsl-values").split(","));
                getAppSettings().zeroshutterlag.set(parameters.get("zsl"));
                getAppSettings().zeroshutterlag.setKEY("zsl");
                getAppSettings().zeroshutterlag.setIsSupported(true);
            }
            else if (parameters.get("mode") != null)
            {
                getAppSettings().zeroshutterlag.setValues(parameters.get("mode-values").split(","));
                getAppSettings().zeroshutterlag.set(parameters.get("mode"));
                getAppSettings().zeroshutterlag.setKEY("mode");
                getAppSettings().zeroshutterlag.setIsSupported(true);
            }
            else if (parameters.get("zsd-mode") != null)
            {
                getAppSettings().zeroshutterlag.setValues(parameters.get("zsd-mode-values").split(","));
                getAppSettings().zeroshutterlag.set(parameters.get("zsd-mode"));
                getAppSettings().zeroshutterlag.setKEY("zsd-mode");
                getAppSettings().zeroshutterlag.setIsSupported(true);
            }

            if (getAppSettings().lenshade.getValues().length == 0)
                getAppSettings().lenshade.setIsSupported(false);
        }

        private void detectSceneDetectModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.SCENE_DETECT) == null)
            {
                getAppSettings().sceneDetectMode.setIsSupported(false);
                return;
            }
            getAppSettings().sceneDetectMode.setValues(parameters.get(KEYS.SCENE_MODE_VALUES).split(","));
            getAppSettings().sceneDetectMode.set(parameters.get(KEYS.SCENE_DETECT));
            if (getAppSettings().sceneDetectMode.getValues().length >0)
                getAppSettings().sceneDetectMode.setIsSupported(true);
        }

        private void detectMemoryColorEnhancementModes(Camera.Parameters parameters)
        {
            if (parameters.get(KEYS.MEMORYCOLORENHANCEMENT) == null)
            {
                getAppSettings().memoryColorEnhancement.setIsSupported(false);
                return;
            }
            String mce = parameters.get(KEYS.MEMORYCOLORENHANCEMENT_VALUES);
            getAppSettings().memoryColorEnhancement.setValues(mce.split(","));
            getAppSettings().memoryColorEnhancement.set(parameters.get(KEYS.MEMORYCOLORENHANCEMENT));
            if (getAppSettings().memoryColorEnhancement.getValues().length >0)
                getAppSettings().memoryColorEnhancement.setIsSupported(true);
        }

        private void detectVideoSizeModes(Camera.Parameters parameters)
        {
            if (parameters.get("video-size") == null)
            {
                getAppSettings().videoSize.setIsSupported(false);
                return;
            }
            getAppSettings().videoSize.setValues(parameters.get("video-size-values").split(","));
            getAppSettings().videoSize.set(parameters.get("video-size"));
            if (getAppSettings().videoSize.getValues().length >0)
                getAppSettings().videoSize.setIsSupported(true);
        }

        private void detectCorrelatedDoubleSamplingModes(Camera.Parameters parameters)
        {
            if (parameters.get("cds-mode") == null)
            {
                getAppSettings().correlatedDoubleSampling.setIsSupported(false);
                return;
            }
            getAppSettings().correlatedDoubleSampling.setValues(parameters.get("cds-mode-values").split(","));
            getAppSettings().correlatedDoubleSampling.set(parameters.get("cds-mode"));
            if (getAppSettings().correlatedDoubleSampling.getValues().length >0)
                getAppSettings().correlatedDoubleSampling.setIsSupported(true);
        }

        private void detectOisModes(Camera.Parameters parameters)
        {
            switch (getAppSettings().getDevice())
            {
                case LG_G2:
                case LG_G2pro:
                case LG_G3:
                    getAppSettings().opticalImageStabilisation.setIsSupported(true);
                    getAppSettings().opticalImageStabilisation.setKEY(KEYS.LG_OIS);
                    getAppSettings().opticalImageStabilisation.setValues(new String[] {
                            KEYS.LG_OIS_PREVIEW_CAPTURE,KEYS.LG_OIS_CAPTURE,KEYS.LG_OIS_VIDEO,KEYS.LG_OIS_CENTERING_ONLY, KEYS.LG_OIS_CENTERING_OFF});
                    getAppSettings().opticalImageStabilisation.set(KEYS.LG_OIS_CENTERING_OFF);
                    break;
                case XiaomiMI5:
                    getAppSettings().opticalImageStabilisation.setIsSupported(true);
                    getAppSettings().opticalImageStabilisation.setKEY("ois");
                    getAppSettings().opticalImageStabilisation.setValues(new String[] {
                            KEYS.ENABLE,KEYS.DISABLE});
                    getAppSettings().opticalImageStabilisation.set(KEYS.ENABLE);
                    break;
                case p8lite:
                    getAppSettings().opticalImageStabilisation.setIsSupported(true);
                    getAppSettings().opticalImageStabilisation.setKEY("hw_ois_enable");
                    getAppSettings().opticalImageStabilisation.setValues(new String[] {
                            KEYS.ON,KEYS.OFF});
                    getAppSettings().opticalImageStabilisation.set(KEYS.ON);
                    break;
                default:
                    getAppSettings().opticalImageStabilisation.setIsSupported(false);
            }
        }

        private void detectVideoHdr(Camera.Parameters parameters)
        {
            if (parameters.get("video-hdr-values") != null)
            {
                getAppSettings().videoHDR.setIsSupported(true);
                getAppSettings().videoHDR.setKEY("video-hdr");
                getAppSettings().videoHDR.setValues(parameters.get("video-hdr-values").split(","));
            }
            else if (parameters.get("sony-video-hdr")!= null) {
                getAppSettings().videoHDR.setIsSupported(true);
                getAppSettings().videoHDR.setKEY("sony-video-hdr");
                getAppSettings().videoHDR.setValues(parameters.get("sony-video-hdr-values").split(","));
            }
            else
                getAppSettings().videoHDR.setIsSupported(false);
        }

        private void detectVideoHFR(Camera.Parameters parameters)
        {
            if (parameters.get("video-hfr") != null)
            {
                String hfrvals = parameters.get("video-hfr-values");
                if (!hfrvals.equals("off"))
                {
                    if (hfrvals.equals("")) {
                        getAppSettings().videoHFR.setValues("off,60,120".split(","));
                        getAppSettings().videoHFR.setKEY("video-hfr");
                        getAppSettings().videoHFR.setIsSupported(true);
                        getAppSettings().videoHFR.set(parameters.get("video-hfr"));
                    }
                    else
                        getAppSettings().videoHFR.setIsSupported(false);
                }
            }
            else if (getAppSettings().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
            {
                if (parameters.get("hsvr-prv-fps-values") != null)
                {
                    getAppSettings().videoHFR.setValues(parameters.get("hsvr-prv-fps-values").split(","));
                    getAppSettings().videoHFR.setKEY("hsvr-prv-fps");
                    getAppSettings().videoHFR.setIsSupported(true);
                    getAppSettings().videoHFR.set(parameters.get("hsvr-prv-fps"));
                }
                else
                    getAppSettings().videoHFR.setIsSupported(false);
            }
            else
            {
                switch (getAppSettings().getDevice())
                {
                    case Htc_M8:
                    case Htc_M9:
                    case HTC_OneA9:
                    case HTC_OneE8:
                        getAppSettings().videoHFR.setValues("off,60,120".split(","));
                        getAppSettings().videoHFR.setKEY("video-mode");
                        getAppSettings().videoHFR.setIsSupported(true);
                        getAppSettings().videoHFR.set(parameters.get("video-mode"));
                        break;
                    default:
                        getAppSettings().videoHFR.setIsSupported(false);
                        break;
                }

            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Build.VERSION.SDK_INT >= 21) {
                new Camera2AsyncTask().execute("");
            }
            else {
                sendLog("No camera2");
                startFreedcam();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class Camera2AsyncTask extends AsyncTask<String,String,String>
    {


        @Override
        protected String doInBackground(String... params)
        {
            publishProgress("###################");
            publishProgress("#######Camera2#####");
            publishProgress("###################");
            getAppSettings().setCamApi(AppSettingsManager.API_2);
            try {
                publishProgress("Check Camera2");
                CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
                String cameras[] = manager.getCameraIdList();

                for (String s : cameras)
                {

                    getAppSettings().SetCurrentCamera(Integer.parseInt(s));
                    publishProgress("###################");
                    publishProgress("#####CameraID:"+s+"####");
                    publishProgress("###################");
                    publishProgress("Check camera features:" + s);
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(s);
                    boolean front = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
                    getAppSettings().setIsFrontCamera(front);
                    boolean legacy = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
                    getAppSettings().SetCamera2FullSupported(String.valueOf(!legacy));
                    publishProgress("IsCamera2 Full Device:" + getAppSettings().IsCamera2FullSupported() + " isFront:" +getAppSettings().getIsFrontCamera());

                    if (!legacy) {

                        detectFlash(characteristics);
                        sendProgress(getAppSettings().flashMode, "Flash");

                        detectMode(characteristics, CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES,getAppSettings().sceneMode,R.array.sceneModes);
                        sendProgress(getAppSettings().sceneMode, "Scene");

                        detectMode(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES,getAppSettings().antiBandingMode,R.array.antibandingmodes);
                        sendProgress(getAppSettings().antiBandingMode, "Antibanding");

                        detectMode(characteristics,CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS, getAppSettings().colorMode,R.array.colormodes);
                        sendProgress(getAppSettings().colorMode, "Color");

                        detectControlMode(characteristics);
                        sendProgress(getAppSettings().controlMode, "ControlMode");

                        detectMode(characteristics,CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES, getAppSettings().edgeMode,R.array.edgeModes);
                        sendProgress(getAppSettings().edgeMode, "EdgeMode");

                        detectMode(characteristics,CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, getAppSettings().opticalImageStabilisation,R.array.digitalImageStabModes);
                        sendProgress(getAppSettings().opticalImageStabilisation, "OpticalImageStabilisationMode");

                        detectMode(characteristics,CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES,getAppSettings().focusMode,R.array.focusModes);
                        sendProgress(getAppSettings().focusMode, "FocusMode");

                        detectMode(characteristics,CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES,getAppSettings().hotpixelMode,R.array.hotpixelmodes);
                        sendProgress(getAppSettings().hotpixelMode, "HotPixelMode");

                        detectPictureFormats(characteristics);
                        sendProgress(getAppSettings().pictureFormat,"PictureFormat");

                        detectManualFocus(characteristics);
                        sendProgress(getAppSettings().manualFocus,"Manual Focus");
                    }
                }
            }
            catch (Throwable ex) {
                ex.printStackTrace();
                getAppSettings().SetCamera2FullSupported("false");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            startFreedcam();
        }

        private void detectFlash(CameraCharacteristics characteristics) {
            if (getAppSettings().IsCamera2FullSupported().equals("true")) {
                //flash mode
                boolean flashavail = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                getAppSettings().flashMode.setIsSupported(flashavail);
                if (getAppSettings().flashMode.isSupported()) {
                    String[] lookupar = getResources().getStringArray(R.array.flashModes);
                    HashMap<String,Integer> map = new HashMap<>();
                    for (int i = 0; i< 3; i++)
                    {
                        map.put(lookupar[i], i);
                    }
                    lookupar = StringUtils.IntHashmapToStringArray(map);
                    getAppSettings().flashMode.setValues(lookupar);
                }
            }
        }

        private void detectControlMode(CameraCharacteristics characteristics) {
            if (getAppSettings().IsCamera2FullSupported().equals("true")) {
                //flash mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    detectMode(characteristics,CameraCharacteristics.CONTROL_AVAILABLE_MODES,getAppSettings().controlMode,R.array.controlModes);
                    return;
                }
                else {
                    int device = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    String[] lookupar = getResources().getStringArray(R.array.controlModes);
                    int[] full = null;
                    if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL) {
                        full = new int[] {0,1,2,3};
                    }
                    else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED)
                    {
                        full = new int[] {0,1,2,};
                    }
                    else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                        full = new int[] {1,2,};
                    getAppSettings().controlMode.setIsSupported(true);
                    if (getAppSettings().controlMode.isSupported()) {
                        HashMap<String, Integer> map = new HashMap<>();
                        for (int i = 0; i < full.length; i++) {
                            map.put(lookupar[i], full[i]);
                        }
                        lookupar = StringUtils.IntHashmapToStringArray(map);
                        getAppSettings().controlMode.setValues(lookupar);
                    }
                }
            }
        }

        private void detectPictureFormats(CameraCharacteristics characteristics)
        {
            StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            HashMap<String, Integer> hmap = new HashMap<>();
            if (smap.isOutputSupportedFor(ImageFormat.RAW10))
                hmap.put("10bitDNG", ImageFormat.RAW10);
            if (smap.isOutputSupportedFor(ImageFormat.RAW_SENSOR))
                hmap.put("16bitDNG", ImageFormat.RAW_SENSOR);
            if (smap.isOutputSupportedFor(ImageFormat.RAW12))
                hmap.put("12bitDNG", ImageFormat.RAW12);
            if (smap.isOutputSupportedFor(ImageFormat.JPEG))
                hmap.put(JPEG, ImageFormat.JPEG);
            getAppSettings().pictureFormat.setIsSupported(true);
            getAppSettings().pictureFormat.set(JPEG);
            getAppSettings().pictureFormat.setValues(StringUtils.IntHashmapToStringArray(hmap));
        }

        private void detectMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<int[]> requestKey, AppSettingsManager.SettingMode settingMode, int ressourceArray)
        {
            if (getAppSettings().IsCamera2FullSupported().equals("true")) {

                int[]  scenes = characteristics.get(requestKey);
                if (scenes.length >0)
                    settingMode.setIsSupported(true);
                else
                    return;
                String[] lookupar = getResources().getStringArray(ressourceArray);
                HashMap<String,Integer> map = new HashMap<>();
                for (int i = 0; i< scenes.length; i++)
                {
                    map.put(lookupar[i], i);
                }
                lookupar = StringUtils.IntHashmapToStringArray(map);
                settingMode.setValues(lookupar);
            }
        }


        private void detectManualFocus(CameraCharacteristics cameraCharacteristics)
        {
            AppSettingsManager.SettingMode mf = getAppSettings().manualFocus;
            float maxfocusrange = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
            if (maxfocusrange == 0)
            {
                mf.setIsSupported(false);
                return;
            }
            float step = 0.2f;
            int count = (int)(maxfocusrange/step)+1;
            StringFloatArray focusranges = new StringFloatArray(count);
            focusranges.add(0,getString(R.string.auto),0f);
            int t = 1;
            for (float i = step; i < maxfocusrange; i += step)
            {
                focusranges.add(t++,StringUtils.getMeterString(1/i),i);
            }
            mf.setIsSupported(true);

            mf.setValues(focusranges.getStringArray());
        }

        @Override
        protected void onProgressUpdate(String... values) {
            sendLog(values[0]);
            Log.d(TAG, values[0]);
        }

        private void sendProgress(AppSettingsManager.SettingMode settingMode, String name)
        {
            if (settingMode.isSupported()) {
                publishProgress(name+" Values:" + getStringFromArray(settingMode.getValues()));
                publishProgress(name+":" + settingMode.get());
            }
            else
                publishProgress(name+" not supported");
        }
    }


}
