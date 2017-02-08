package freed.cam.featuredetector;

import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.lge.hardware.LGCamera;
import com.troop.freedcam.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils;

import static freed.cam.apis.KEYS.BAYER;
import static freed.cam.apis.KEYS.MODULE_PICTURE;

/**
 * Created by troop on 23.01.2017.
 */

public class Camera1FeatureDetectorTask extends AbstractFeatureDetectorTask
{
    private final  String TAG = Camera1FeatureDetectorTask.class.getSimpleName();

    public Camera1FeatureDetectorTask(ProgressUpdate progressUpdate, AppSettingsManager appSettingsManager)
    {
        super(progressUpdate,appSettingsManager);
    }

    private String camstring(int id)
    {
        return appSettingsManager.getResString(id);
    }

    @Override
    protected String doInBackground(String... params)
    {
        publishProgress("###################");
        publishProgress("#######Camera1#####");
        publishProgress("###################");
        appSettingsManager.setCamApi(AppSettingsManager.API_1);
        //detect Device
        if (appSettingsManager.getDevice() == null)
            appSettingsManager.SetDevice(new DeviceUtils().getDevice(appSettingsManager.getResources()));
        publishProgress("Device:"+appSettingsManager.getDevice().name());
        //detect frameworks
        appSettingsManager.setFramework(getFramework());
        publishProgress("FrameWork:"+appSettingsManager.getFrameWork());
        //can open legcay
        appSettingsManager.setCanOpenLegacy(canOpenLegacy());
        publishProgress("CanOpenLegacy:"+appSettingsManager.getCanOpenLegacy());

        int cameraCounts = Camera.getNumberOfCameras();
        AppSettingsManager appS = appSettingsManager;
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

            appSettingsManager.guide.setValues(appS.getResources().getStringArray(R.array.guidelist));
            appSettingsManager.guide.set(appS.guide.getValues()[0]);

            detectedPictureFormats(parameters);
            publishProgress("DngSupported:" + (appS.getDngProfilesMap().size() > 0) + " RawSupport:"+appS.rawPictureFormat.isSupported());
            publishProgress("PictureFormats:" + getStringFromArray(appS.pictureFormat.getValues()));
            publishProgress("RawFormats:" + getStringFromArray(appS.rawPictureFormat.getValues()));
            publishProgress(" RawFormat:" + appS.rawPictureFormat.get());

            appSettingsManager.modules.set(MODULE_PICTURE);

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

            detectPreviewFpsRanges(parameters);

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

            detectDisModes(parameters);
            sendProgress(appS.digitalImageStabilisationMode, "DigitalImageStabilisation");

            detectDenoise(parameters);
            sendProgress(appS.denoiseMode, "Denoise");

            detectNonZslmanual(parameters);
            sendProgress(appS.nonZslManualMode, "NonZslManual");

            detectVirtualLensFilter(parameters);
            sendProgress(appS.virtualLensfilter, "NonZslManual");

            detectVideoHdr(parameters);
            sendProgress(appS.videoHDR, "VideoHDR");

            detectVideoHFR(parameters);
            sendProgress(appS.videoHFR,"VideoHFR");

            detectVideoMediaProfiles(i);

            detectManualFocus(parameters);
            sendProgress(appS.manualFocus,"ManualFocus");

            detectManualSaturation(parameters);
            sendProgress(appS.manualSaturation,"ManualSaturation");

            detectManualSharpness(parameters);
            sendProgress(appS.manualSharpness,"ManualSharpness");

            detectManualBrightness(parameters);
            sendProgress(appS.manualBrightness,"ManualBrightness");

            detectManualContrast(parameters);
            sendProgress(appS.manualContrast,"ManualContrast");

            detectManualExposureTime(parameters);
            sendProgress(appS.manualExposureTime,"ExposureTime");

            detectManualIso(parameters);
            sendProgress(appS.manualIso,"Manual ISo");

            detectManualWhiteBalance(parameters);
            sendProgress(appS.manualIso,"Manual Wb");

            detectNightMode(parameters);

            detectQcomFocus(parameters);
        }

        appS.SetCurrentCamera(0);

        return null;
    }

    private void detectPreviewFpsRanges(Camera.Parameters parameters) {
        if (parameters.get(camstring(R.string.preview_fps_range_values))!= null)
        {
            appSettingsManager.previewFpsRange.setIsSupported(true);
            appSettingsManager.previewFpsRange.setValues(parameters.get(camstring(R.string.preview_fps_range_values)).split(","));
            appSettingsManager.previewFpsRange.setKEY(camstring(R.string.preview_fps_range));
            appSettingsManager.previewFpsRange.set(parameters.get(camstring(R.string.preview_fps_range)));
        }
    }

    private void detectQcomFocus(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.touch_af_aec))!= null)
            appSettingsManager.setUseQcomFocus(true);
        else
            appSettingsManager.setUseQcomFocus(false);
    }

    private void detectNightMode(Camera.Parameters parameters) {

        switch (appSettingsManager.getDevice())
        {
            case XiaomiMI3W:
            case XiaomiMI4C:
            case XiaomiMI4W:
            case XiaomiMI_Note_Pro:
            case Xiaomi_RedmiNote:
            case ZTE_ADV:
            case ZTEADVIMX214:
            case ZTEADV234:
            case ZTE_Z5SMINI:
            case ZTE_Z11:
                appSettingsManager.nightMode.setIsSupported(true);
                break;

        }
    }

    private void detectManualWhiteBalance(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
            appSettingsManager.manualWhiteBalance.setIsSupported(false);
        else
        {
            switch (appSettingsManager.getDevice())
            {
                case ZTEADV234:
                case ZTEADVIMX214:
                case ZTE_Z11:
                case ZTE_ADV:
                    appSettingsManager.manualWhiteBalance.setValues(createWBStringArray(2000,8000,100));
                    appSettingsManager.manualWhiteBalance.setMode(appSettingsManager.getResString(R.string.manual));
                    appSettingsManager.manualWhiteBalance.setIsSupported(true);
                    break;
                case XiaomiMI3W:
                case XiaomiMI4W:
                case XiaomiMI4C:
                {
                    if(!DeviceUtils.isCyanogenMod()) {
                        if (Build.VERSION.SDK_INT < 23) {
                            appSettingsManager.manualWhiteBalance.setValues(createWBStringArray(2000,7500,100));
                            appSettingsManager.manualWhiteBalance.setMode(appSettingsManager.getResString(R.string.manual));
                        } else {
                            appSettingsManager.manualWhiteBalance.setValues(createWBStringArray(2000,8000,100));
                            appSettingsManager.manualWhiteBalance.setMode(appSettingsManager.getResString(R.string.manual_cct));
                        }
                        appSettingsManager.manualWhiteBalance.setIsSupported(true);
                        break;
                    }
                }
                default:
                    // looks like wb-current-cct is loaded when the preview is up. this could be also for the other parameters
                    String wbModeval ="", wbmax = "",wbmin = "";

                    if (parameters.get(appSettingsManager.getResString(R.string.max_wb_cct)) != null) {
                        wbmax = appSettingsManager.getResString(R.string.max_wb_cct);
                    }
                    else if (parameters.get(appSettingsManager.getResString(R.string.max_wb_ct))!= null)
                        wbmax =appSettingsManager.getResString(R.string.max_wb_ct);

                    if (parameters.get(appSettingsManager.getResString(R.string.min_wb_cct))!= null) {
                        wbmin =appSettingsManager.getResString(R.string.min_wb_cct);
                    } else if (parameters.get(appSettingsManager.getResString(R.string.min_wb_ct))!= null)
                        wbmin =appSettingsManager.getResString(R.string.min_wb_ct);

                    if (arrayContainsString(appSettingsManager.whiteBalanceMode.getValues(), appSettingsManager.getResString(R.string.manual)))
                        wbModeval = appSettingsManager.getResString(R.string.manual);
                    else if (arrayContainsString(appSettingsManager.whiteBalanceMode.getValues(),appSettingsManager.getResString(R.string.manual_cct)))
                        wbModeval = appSettingsManager.getResString(R.string.manual_cct);

                    if (!wbmax.equals("") && !wbmin.equals("") && !wbModeval.equals("")) {
                        Log.d(TAG, "Found all wbct values:" +wbmax + " " + wbmin + " " +wbModeval);
                        appSettingsManager.manualWhiteBalance.setIsSupported(true);
                        appSettingsManager.manualWhiteBalance.setMode(wbModeval);
                        int min = Integer.parseInt(parameters.get(wbmin));
                        int max = Integer.parseInt(parameters.get(wbmax));
                        appSettingsManager.manualWhiteBalance.setValues(createWBStringArray(min,max,100));
                    }
                    else {
                        Log.d(TAG, "Failed to lookup wbct:" + " " +wbmax + " " + wbmin + " " +wbModeval);
                        appSettingsManager.manualWhiteBalance.setIsSupported(false);
                    }
                    break;
            }
        }
    }

    protected String[] createWBStringArray(int min, int max, float step)
    {
        ArrayList<String> t = new ArrayList<>();
        t.add(KEYS.AUTO);
        for (int i = min; i<=max;i+=step)
        {
            t.add(i+"");
        }
        return  t.toArray(new String[t.size()]);
    }

    private boolean arrayContainsString(String[] ar,String dif)
    {
        boolean ret = false;
        for (String s: ar)
            if (s.equals(dif))
                ret = true;
        return ret;
    }

    private void detectManualIso(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
            appSettingsManager.manualIso.setIsSupported(true);
            appSettingsManager.manualIso.setKEY("m-sr-g");
            switch (appSettingsManager.getDevice())
            {
                case Xiaomi_Redmi_Note3:
                    appSettingsManager.manualIso.setValues(createIsoValues(100,2700,100));
                    break;
                default:
                    appSettingsManager.manualIso.setValues(createIsoValues(100,1600,100));
                    break;
            }
        }
        else
        {
            switch (appSettingsManager.getDevice()) {
                case Aquaris_E5:
                    appSettingsManager.manualIso.setIsSupported(true);
                    appSettingsManager.manualIso.setKEY(KEYS.CONTINUOUS_ISO);
                    appSettingsManager.manualIso.setValues(createIsoValues(100, 1600, 50));
                    break;
                case Xiaomi_Redmi3:
                case LG_G3:
                    appSettingsManager.manualIso.setIsSupported(false);
                    break;
                default:
                    if (parameters.get(KEYS.MIN_ISO) != null && parameters.get(KEYS.MAX_ISO) != null) {
                        appSettingsManager.manualIso.setIsSupported(true);
                        appSettingsManager.manualIso.setKEY(KEYS.CONTINUOUS_ISO);
                        int min = Integer.parseInt(parameters.get(KEYS.MIN_ISO));
                        int max = Integer.parseInt(parameters.get(KEYS.MAX_ISO));
                        appSettingsManager.manualIso.setValues(createIsoValues(min, max, 50));
                    }
                break;
            }
        }
    }

    private String[] createIsoValues(int miniso, int maxiso, int step)
    {
        ArrayList<String> s = new ArrayList<>();
        s.add(KEYS.AUTO);
        for (int i =miniso; i <= maxiso; i +=step)
        {
            s.add(i + "");
        }
        String[] stringvalues = new String[s.size()];
        return s.toArray(stringvalues);
    }

    private void detectManualExposureTime(Camera.Parameters parameters)
    {
        //mtk shutter
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.manualExposureTime.setIsSupported(true);
            appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.mtk_shutter));
            appSettingsManager.manualExposureTime.setKEY("m-ss");
            appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_MTK);
        }
        else
        {
            switch(appSettingsManager.getDevice()) {
                case Aquaris_E5:
                    appSettingsManager.manualExposureTime.setIsSupported(true);
                    appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.aquaris_e5_shuttervalues));
                    appSettingsManager.manualExposureTime.setKEY(camstring(R.string.exposure_time));
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MICORSEC);
                    break;
                case LG_G2pro:
                    appSettingsManager.manualExposureTime.setIsSupported(true);
                    appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_lg_g2pro));
                    appSettingsManager.manualExposureTime.setKEY(camstring(R.string.exposure_time));
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_G2PRO);
                    break;
                case ZTE_ADV:
                    appSettingsManager.manualExposureTime.setIsSupported(true);
                    appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_zte_z5s));
                    appSettingsManager.manualExposureTime.setKEY(camstring(R.string.exposure_time));
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_ZTE);
                    break;
                case  ZTEADVIMX214:
                case ZTEADV234:
                case ZTE_Z11:
                    appSettingsManager.manualExposureTime.setIsSupported(true);
                    appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_zte_z7));
                    appSettingsManager.manualExposureTime.setKEY(camstring(R.string.exposure_time));
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_ZTE);
                    break;
                case LG_G3:
                    appSettingsManager.manualExposureTime.setIsSupported(false);
                    break;
                default:
                    //htc shutter
                    if (parameters.get("shutter") != null) {
                        appSettingsManager.manualExposureTime.setIsSupported(true);
                        appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.htc));
                        appSettingsManager.manualExposureTime.setKEY("shutter");
                        appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_HTC);
                    }
                    //lg shutter
                    else if (parameters.get(KEYS.LG_SHUTTER_SPEED_VALUES) != null) {
                        appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_LG);
                        ArrayList<String> l = new ArrayList(Arrays.asList(parameters.get(KEYS.LG_SHUTTER_SPEED_VALUES).replace(",0", "").split(",")));
                        l.remove(0);
                        appSettingsManager.manualExposureTime.setValues(l.toArray(new String[l.size()]));
                        appSettingsManager.manualExposureTime.setKEY(KEYS.LG_SHUTTER_SPEED);
                        appSettingsManager.manualExposureTime.setIsSupported(true);
                    }
                    //meizu shutter
                    else if (parameters.get("shutter-value") != null) {
                        appSettingsManager.manualExposureTime.setIsSupported(true);
                        appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_meizu));
                        appSettingsManager.manualExposureTime.setKEY("shutter-value");
                        appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_MEIZU);
                    }
                    //krillin shutter
                    else if (parameters.get("hw-manual-exposure-value") != null) {
                        appSettingsManager.manualExposureTime.setIsSupported(true);
                        appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_krillin));
                        appSettingsManager.manualExposureTime.setKEY("hw-manual-exposure-value");
                        appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_KRILLIN);
                    }
                    //sony shutter
                    else if (parameters.get("sony-max-shutter-speed") != null) {
                        appSettingsManager.manualExposureTime.setIsSupported(true);
                        appSettingsManager.manualExposureTime.setValues(getSupportedShutterValues(
                                Long.parseLong(parameters.get("sony-min-shutter-speed")),
                                Long.parseLong(parameters.get("sony-max-shutter-speed")),
                                true));
                        appSettingsManager.manualExposureTime.setKEY("sony-shutter-speed");
                        appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_SONY);
                    }
                    //qcom shutter
                    else if (parameters.get(camstring(R.string.max_exposure_time)) != null && parameters.get(camstring(R.string.min_exposure_time)) != null) {
                        long min = 0, max = 0;
                        switch (appSettingsManager.getDevice()) {
                            case OnePlusX:
                                min = Integer.parseInt(parameters.get(camstring(R.string.min_exposure_time))) * 1000;
                                max = Integer.parseInt(parameters.get(camstring(R.string.max_exposure_time))) * 1000;
                                appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MICORSEC);
                                break;
                            default:
                                if (parameters.get(camstring(R.string.max_exposure_time)).contains(".")) {
                                    min = (long) Double.parseDouble(parameters.get(camstring(R.string.min_exposure_time))) * 1000;
                                    max = (long) Double.parseDouble(parameters.get(camstring(R.string.max_exposure_time))) * 1000;
                                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MICORSEC);
                                } else {
                                    min = Integer.parseInt(parameters.get(camstring(R.string.min_exposure_time)));
                                    max = Integer.parseInt(parameters.get(camstring(R.string.max_exposure_time)));
                                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MILLISEC);
                                }
                                break;
                        }
                        if (max > 0) {
                            appSettingsManager.manualExposureTime.setIsSupported(true);
                            appSettingsManager.manualExposureTime.setKEY(camstring(R.string.exposure_time));
                            appSettingsManager.manualExposureTime.setValues(getSupportedShutterValues(min, max, true));
                        }
                    }
                    break;
            }
        }
    }

    private String[] getSupportedShutterValues(long minMillisec, long maxMiliisec, boolean withautomode) {
        String[] allvalues = appSettingsManager.getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        if (withautomode)
            tmp.add(KEYS.AUTO);
        for (int i = 1; i < allvalues.length; i++) {
            String s = allvalues[i];

            float a;
            if (s.contains("/")) {
                String[] split = s.split("/");
                a = Float.parseFloat(split[0]) / Float.parseFloat(split[1]) * 1000000f;
            } else
                a = Float.parseFloat(s) * 1000000f;

            if (a >= minMillisec && a <= maxMiliisec)
                tmp.add(s);
            if (a >= minMillisec && !foundmin) {
                foundmin = true;
            }
            if (a > maxMiliisec && !foundmax) {
                foundmax = true;
            }
            if (foundmax && foundmin)
                break;

        }
        return tmp.toArray(new String[tmp.size()]);
    }

    private void detectVirtualLensFilter(Camera.Parameters parameters)
    {
        switch (appSettingsManager.getDevice())
        {
            case ZTE_ADV:
            case ZTE_Z5SMINI:
            case ZTE_Z11:
                appSettingsManager.virtualLensfilter.setIsSupported(true);
                break;
            default:
                appSettingsManager.virtualLensfilter.setIsSupported(false);
                break;
        }
    }

    private void detectNonZslmanual(Camera.Parameters parameters) {
        if(parameters.get("non-zsl-manual-mode")!=null)
        {
            appSettingsManager.nonZslManualMode.setIsSupported(true);
            appSettingsManager.nonZslManualMode.setKEY("non-zsl-manual-mode");
            appSettingsManager.nonZslManualMode.setValues(new String[]{KEYS.ON,KEYS.OFF});
        }
    }

    private void detectDenoise(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if(parameters.get(camstring(R.string.mtk_3dnr_mode))!=null) {
                if (parameters.get(camstring(R.string.mtk_3dnr_mode_values)).equals("on,off")) {
                    appSettingsManager.denoiseMode.setIsSupported(true);
                    appSettingsManager.denoiseMode.setKEY(camstring(R.string.mtk_3dnr_mode));
                    appSettingsManager.denoiseMode.setValues(parameters.get(camstring(R.string.mtk_3dnr_mode_values)).split(","));
                }
            }
        }
        else
        {
            switch (appSettingsManager.getDevice())
            {
                case p8:
                case p8lite:
                    appSettingsManager.denoiseMode.setIsSupported(false);
                    break;
                default:
                    detectMode(parameters,R.string.denoise,R.string.denoise_values,appSettingsManager.denoiseMode);
                    break;
            }
        }
    }

    private void detectDisModes(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
            appSettingsManager.digitalImageStabilisationMode.setIsSupported(false);
        } else{
            switch (appSettingsManager.getDevice())
            {
                case XiaomiMI5:
                case XiaomiMI5s:
                case Lenovo_Vibe_X3:
                    appSettingsManager.digitalImageStabilisationMode.setIsSupported(false);
                    break;
                default:
                    detectMode(parameters,R.string.dis,R.string.dis_values, appSettingsManager.digitalImageStabilisationMode);
                    break;
            }

        }
    }

    private void detectManualSaturation(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get(camstring(R.string.saturation))!= null && parameters.get(camstring(R.string.saturation_values))!= null) {
                appSettingsManager.manualSaturation.setValues(parameters.get(camstring(R.string.saturation_values)).split(","));
                appSettingsManager.manualSaturation.setKEY(camstring(R.string.saturation));
                appSettingsManager.manualSaturation.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(KEYS.LG_COLOR_ADJUST_MAX) != null && parameters.get(KEYS.LG_COLOR_ADJUST_MIN) != null) {
                min = Integer.parseInt(parameters.get(KEYS.LG_COLOR_ADJUST_MIN));
                max = Integer.parseInt(parameters.get(KEYS.LG_COLOR_ADJUST_MAX));
                appSettingsManager.manualSaturation.setKEY(KEYS.LG_COLOR_ADJUST);
            }
            else if (parameters.get(camstring(R.string.saturation_max)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.saturation_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.saturation_max)));
                appSettingsManager.manualSaturation.setKEY(camstring(R.string.saturation));
            } else if (parameters.get(camstring(R.string.max_saturation)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.min_saturation)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_saturation)));
                appSettingsManager.manualSaturation.setKEY(camstring(R.string.saturation));
            }
            if (max > 0) {
                appSettingsManager.manualSaturation.setValues(createStringArray(min, max, 1));
                appSettingsManager.manualSaturation.setIsSupported(true);
            }
        }
    }

    private void detectManualSharpness(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get(camstring(R.string.edge))!= null && parameters.get(camstring(R.string.edge_values))!= null) {
                appSettingsManager.manualSharpness.setValues(parameters.get(camstring(R.string.edge_values)).split(","));
                appSettingsManager.manualSharpness.setKEY(camstring(R.string.edge));
                appSettingsManager.manualSharpness.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(camstring(R.string.sharpness_max)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.sharpness_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.sharpness_max)));
                appSettingsManager.manualSharpness.setKEY(camstring(R.string.sharpness));
            } else if (parameters.get(camstring(R.string.max_sharpness)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.min_sharpness)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_sharpness)));
                appSettingsManager.manualSharpness.setKEY(camstring(R.string.sharpness));
            }
            if (max > 0) {
                appSettingsManager.manualSharpness.setValues(createStringArray(min, max, 1));
                appSettingsManager.manualSharpness.setIsSupported(true);
            }
        }
    }

    private void detectManualBrightness(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get(camstring(R.string.brightness))!= null && parameters.get(camstring(R.string.brightness_values))!= null) {
                appSettingsManager.manualBrightness.setValues(parameters.get(camstring(R.string.brightness_values)).split(","));
                appSettingsManager.manualBrightness.setKEY(camstring(R.string.brightness));
                appSettingsManager.manualBrightness.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(camstring(R.string.brightness_max)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.brightness_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.brightness_max)));
            } else if (parameters.get(camstring(R.string.max_brightness)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.min_brightness)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_brightness)));

            }
            if (max > 0) {
                if (parameters.get(camstring(R.string.brightness))!= null)
                    appSettingsManager.manualBrightness.setKEY(camstring(R.string.brightness));
                else if (parameters.get(camstring(R.string.luma_adaptation))!= null)
                    appSettingsManager.manualBrightness.setKEY(camstring(R.string.luma_adaptation));
                appSettingsManager.manualBrightness.setValues(createStringArray(min, max, 1));
                appSettingsManager.manualBrightness.setIsSupported(true);
            }
        }
    }

    private void detectManualContrast(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get(camstring(R.string.contrast))!= null && parameters.get(camstring(R.string.contrast_values))!= null) {
                appSettingsManager.manualContrast.setValues(parameters.get(camstring(R.string.contrast_values)).split(","));
                appSettingsManager.manualContrast.setKEY(camstring(R.string.contrast));
                appSettingsManager.manualContrast.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(camstring(R.string.contrast_max)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.contrast_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.contrast_max)));
            } else if (parameters.get(camstring(R.string.max_contrast)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.min_contrast)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_contrast)));

            }
            if (max > 0) {
                appSettingsManager.manualContrast.setKEY(camstring(R.string.contrast));
                appSettingsManager.manualContrast.setValues(createStringArray(min, max, 1));
                appSettingsManager.manualContrast.setIsSupported(true);
            }
        }
    }


    private void detectManual(Camera.Parameters parameters, String key_min, String key_max, String key_value, AppSettingsManager.SettingMode settingsmode)
    {
        int min =0,max=0;
        if (parameters.get(key_max)!= null)
        {
            min = Integer.parseInt(key_min);
            max = Integer.parseInt(key_max);
        }
        if (max > 0) {
            settingsmode.setValues(createStringArray(min, max, 1));
            settingsmode.setKEY(key_value);
            settingsmode.isSupported();
        }
    }

    private String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        if (step == 0)
            step = 1;
        for (int i = min; i <= max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    private void detectManualFocus(Camera.Parameters parameters) {
        int min =0, max =0, step = 0;
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.manualFocus.setMode(camstring(R.string.manual));
            appSettingsManager.manualFocus.setType(-1);
            appSettingsManager.manualFocus.setIsSupported(true);
            min = 0;
            max = 1023;
            step = 10;
            appSettingsManager.manualFocus.setKEY(KEYS.AFENG_POS);
        }
        else {
            //lookup old qcom

            if (parameters.get(camstring(R.string.manual_focus_modes)) == null) {

                if (parameters.get(camstring(R.string.max_focus_pos_index)) != null
                        && parameters.get(camstring(R.string.min_focus_pos_index))!= null
                        && appSettingsManager.focusMode.contains(camstring(R.string.manual))) {

                    appSettingsManager.manualFocus.setMode(camstring(R.string.manual));
                    appSettingsManager.manualFocus.setType(1);
                    appSettingsManager.manualFocus.setIsSupported(true);
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_focus_pos_index)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_focus_pos_index)));
                    step = 10;
                    appSettingsManager.manualFocus.setKEY(camstring(R.string.manual_focus_position));
                }
            }
            else
            {
                //lookup new qcom
                if (parameters.get(camstring(R.string.max_focus_pos_ratio)) != null
                        && parameters.get(camstring(R.string.min_focus_pos_ratio)) != null
                        && appSettingsManager.focusMode.contains(camstring(R.string.manual))) {

                    appSettingsManager.manualFocus.setMode(camstring(R.string.manual));
                    appSettingsManager.manualFocus.setType(2);
                    appSettingsManager.manualFocus.setIsSupported(true);
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_focus_pos_ratio)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_focus_pos_ratio)));
                    step = 1;
                    appSettingsManager.manualFocus.setKEY(camstring(R.string.manual_focus_position));
                }
            }
            //htc mf
            if (parameters.get(camstring(R.string.min_focus)) != null && parameters.get(camstring(R.string.max_focus)) != null)
            {
                appSettingsManager.manualFocus.setMode("");
                appSettingsManager.manualFocus.setType(-1);
                appSettingsManager.manualFocus.setIsSupported(true);
                min = Integer.parseInt(parameters.get(camstring(R.string.min_focus)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_focus)));
                step = 1;
                appSettingsManager.manualFocus.setKEY(camstring(R.string.focus));
            }

            //huawai mf
            if(parameters.get(KEYS.HW_VCM_END_VALUE) != null && parameters.get(KEYS.HW_VCM_START_VALUE) != null)
            {
                appSettingsManager.manualFocus.setMode(camstring(R.string.manual));
                appSettingsManager.manualFocus.setType(-1);
                appSettingsManager.manualFocus.setIsSupported(true);
                min = Integer.parseInt(parameters.get(KEYS.HW_VCM_END_VALUE));
                max = Integer.parseInt(parameters.get(KEYS.HW_VCM_START_VALUE));
                step = 10;
                appSettingsManager.manualFocus.setKEY(KEYS.HW_MANUAL_FOCUS_STEP_VALUE);
            }
        }
        //override device specific
        switch (appSettingsManager.getDevice())
        {
            case LG_G3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    min = 0;
                    max = 1023;
                    step = 10;
                    appSettingsManager.manualFocus.setMode(camstring(R.string.manual));
                    appSettingsManager.manualFocus.setType(2);
                    appSettingsManager.manualFocus.setIsSupported(true);
                    appSettingsManager.manualFocus.setKEY(camstring(R.string.manual_focus_position));
                }
                else if (Build.VERSION.SDK_INT < 21)
                {
                    min = 0;
                    max = 79;
                    step = 1;
                    appSettingsManager.manualFocus.setMode(KEYS.FOCUS_MODE_NORMAL);
                    appSettingsManager.manualFocus.setType(-1);
                    appSettingsManager.manualFocus.setIsSupported(true);
                    appSettingsManager.manualFocus.setKEY(KEYS.MANUALFOCUS_STEP);
                }
                break;
            case LG_G4:
            case LG_V20:
                min = 0;
                max = 60;
                step = 1;
                appSettingsManager.manualFocus.setMode(KEYS.FOCUS_MODE_NORMAL);
                appSettingsManager.manualFocus.setType(-1);
                appSettingsManager.manualFocus.setIsSupported(true);
                appSettingsManager.manualFocus.setKEY(KEYS.MANUALFOCUS_STEP);
                break;
            case LG_G2:
            case LG_G2pro:
                min = 0;
                max = 79;
                step = 1;
                appSettingsManager.manualFocus.setMode(KEYS.FOCUS_MODE_NORMAL);
                appSettingsManager.manualFocus.setType(-1);
                appSettingsManager.manualFocus.setIsSupported(true);
                appSettingsManager.manualFocus.setKEY(KEYS.MANUALFOCUS_STEP);
                break;
            case ZTE_Z11:
            case ZTEADV234:
            case ZTEADVIMX214:
            case ZTE_ADV:
                appSettingsManager.manualFocus.setMode(camstring(R.string.manual));
                appSettingsManager.manualFocus.setType(1);
                appSettingsManager.manualFocus.setIsSupported(true);
                min = 0;
                max = 79;
                step = 1;
                appSettingsManager.manualFocus.setKEY(camstring(R.string.manual_focus_position));
                break;
            case Vivo_V3:
            case Moto_X2k14:
                appSettingsManager.manualFocus.setIsSupported(false);
                break;

        }
        //create mf values
        if (appSettingsManager.manualFocus.isSupported())
            appSettingsManager.manualFocus.setValues(createManualFocusValues(min, max,step));
    }

    private String[] createManualFocusValues(int min, int max, int step)
    {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(KEYS.AUTO);

        for (int i = min; i < max; i+= step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    private void detectFrontCamera(int i) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(i,info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            appSettingsManager.setIsFrontCamera(false);
        else
            appSettingsManager.setIsFrontCamera(true);
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
        switch (appSettingsManager.getFrameWork())
        {
            case AppSettingsManager.FRAMEWORK_LG:
            {
                LGCamera lgCamera;
                if (appSettingsManager.getDevice() == DeviceUtils.Devices.LG_G4 || appSettingsManager.getDevice() == DeviceUtils.Devices.LG_V20)
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

    private void detectMode(Camera.Parameters parameters, int key, int keyvalues, AppSettingsManager.SettingMode mode)
    {
        if (parameters.get(camstring(keyvalues)) == null)
        {
            mode.setIsSupported(false);
            return;
        }
        mode.setValues(parameters.get(camstring(keyvalues)).split(","));
        mode.setKEY(camstring(key));
        mode.set(parameters.get(mode.getKEY()));

        if (mode.getValues().length >0)
            mode.setIsSupported(true);
    }

    private void detectedPictureFormats(Camera.Parameters parameters)
    {
        //drop raw for front camera
        if (appSettingsManager.getIsFrontCamera())
        {
            appSettingsManager.pictureFormat.setIsSupported(false);
            appSettingsManager.rawPictureFormat.setIsSupported(false);
        }
        else {

            if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
                appSettingsManager.pictureFormat.setIsSupported(true);
                appSettingsManager.rawPictureFormat.setIsSupported(true);
            } else {
                if (appSettingsManager.getDevice() == DeviceUtils.Devices.LG_G2)
                {
                    appSettingsManager.pictureFormat.setIsSupported(true);
                    appSettingsManager.rawPictureFormat.setIsSupported(true);
                    appSettingsManager.rawPictureFormat.set(KEYS.BAYER_MIPI_10BGGR);
                }
                else if (appSettingsManager.getDevice() == DeviceUtils.Devices.HTC_OneA9 )
                {
                    appSettingsManager.pictureFormat.setIsSupported(true);
                    appSettingsManager.rawPictureFormat.setIsSupported(true);
                    appSettingsManager.rawPictureFormat.set(KEYS.BAYER_MIPI_10RGGB);
                }else if(appSettingsManager.getDevice() == DeviceUtils.Devices.MotoG3 ||appSettingsManager.getDevice() == DeviceUtils.Devices.MotoG_Turbo)
                {
                    appSettingsManager.pictureFormat.setIsSupported(true);
                    appSettingsManager.rawPictureFormat.setIsSupported(true);
                    appSettingsManager.rawPictureFormat.set(KEYS.BAYER_QCOM_10RGGB);
                }

                else if(appSettingsManager.getDevice() == DeviceUtils.Devices.Htc_M8 && Build.VERSION.SDK_INT >= 21)
                {
                    appSettingsManager.pictureFormat.setIsSupported(true);
                    appSettingsManager.rawPictureFormat.setIsSupported(true);
                    appSettingsManager.rawPictureFormat.set(KEYS.BAYER_QCOM_10GRBG);
                }
                else
                {
                    String formats = parameters.get(camstring(R.string.picture_format_values));

                    if (formats.contains("bayer-mipi") || formats.contains("raw"))
                    {
                        appSettingsManager.rawPictureFormat.setIsSupported(true);
                        String[] forms = formats.split(",");
                        for (String s : forms) {
                            if (s.contains("bayer-mipi") || s.contains("raw"))
                            {
                                appSettingsManager.rawPictureFormat.set(s);
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
                        appSettingsManager.rawPictureFormat.setValues(rawFormats);
                    }
                }
            }
            appSettingsManager.pictureFormat.setIsSupported(true);
            if (appSettingsManager.getDngProfilesMap().size() > 0)
            {
                appSettingsManager.pictureFormat.setValues(new String[]{
                        AppSettingsManager.CaptureMode[AppSettingsManager.JPEG], AppSettingsManager.CaptureMode[AppSettingsManager.DNG], AppSettingsManager.CaptureMode[AppSettingsManager.RAW]
                });
            }
            else if (appSettingsManager.rawPictureFormat.isSupported()) {
                appSettingsManager.pictureFormat.setValues(new String[]{
                        AppSettingsManager.CaptureMode[AppSettingsManager.JPEG], AppSettingsManager.CaptureMode[AppSettingsManager.RAW]
                });
            }
            else
            {
                appSettingsManager.pictureFormat.setValues(new String[]{
                        AppSettingsManager.CaptureMode[AppSettingsManager.JPEG]
                });
            }

        }
    }

    private void detectPictureSizes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.picture_size,R.string.picture_size_values,appSettingsManager.pictureSize);
    }

    private void detectFocusModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.focus_mode,R.string.focus_mode_values,appSettingsManager.focusMode);
    }

    private void detectWhiteBalanceModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.whitebalance,R.string.whitebalance_values,appSettingsManager.whiteBalanceMode);
    }

    private void detectExposureModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.exposure))!= null) {
            detectMode(parameters,R.string.exposure,R.string.exposure_mode_values,appSettingsManager.exposureMode);
        }
        else if (parameters.get(camstring(R.string.auto_exposure_values))!= null) {
            detectMode(parameters,R.string.auto_exposure,R.string.auto_exposure_values,appSettingsManager.exposureMode);
        }
        else if(parameters.get(camstring(R.string.sony_metering_mode))!= null) {
            detectMode(parameters,R.string.sony_metering_mode,R.string.sony_metering_mode_values,appSettingsManager.exposureMode);
        }
        else if(parameters.get(camstring(R.string.exposure_meter))!= null) {
            detectMode(parameters,R.string.exposure_meter,R.string.exposure_meter_values,appSettingsManager.exposureMode);
        }
        if (!appSettingsManager.exposureMode.getKEY().equals(""))
            appSettingsManager.exposureMode.setIsSupported(true);
        else
            appSettingsManager.exposureMode.setIsSupported(false);
    }

    private void detectColorModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.effect,R.string.effect_values,appSettingsManager.colorMode);
    }

    private void detectFlashModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.flash_mode,R.string.flash_mode_values,appSettingsManager.flashMode);
    }

    private void detectIsoModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.iso_mode_values))!= null){
            detectMode(parameters,R.string.iso,R.string.iso_mode_values,appSettingsManager.isoMode);
        }
        else if (parameters.get(camstring(R.string.iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.iso_values,appSettingsManager.isoMode);
        }
        else if (parameters.get(camstring(R.string.iso_speed_values))!= null) {
            detectMode(parameters,R.string.iso_speed,R.string.iso_speed_values,appSettingsManager.isoMode);
        }
        else if (parameters.get(camstring(R.string.sony_iso_values))!= null) {
            detectMode(parameters,R.string.sony_iso,R.string.sony_iso_values,appSettingsManager.isoMode);
        }
        else if (parameters.get(camstring(R.string.lg_iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.lg_iso_values,appSettingsManager.isoMode);
        }
        if (appSettingsManager.isoMode.getValues().length >1)
            appSettingsManager.isoMode.setIsSupported(true);
        else
            appSettingsManager.isoMode.setIsSupported(false);
    }

    private void detectAntiBandingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.antibanding,R.string.antibanding_values,appSettingsManager.antiBandingMode);
    }

    private void detectImagePostProcessingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.ipp,R.string.ipp_values,appSettingsManager.imagePostProcessing);
    }

    private void detectPreviewSizeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_size,R.string.preview_size_values,appSettingsManager.previewSize);
    }

    private void detectJpeqQualityModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.jpeg_quality)) == null)
        {
            appSettingsManager.jpegQuality.setIsSupported(false);
            return;
        }
        String[] valuetoreturn = new String[20];
        for (int i = 1; i < 21; i++)
        {
            valuetoreturn[i-1] = "" + i*5;
        }
        appSettingsManager.jpegQuality.setValues(valuetoreturn);
        appSettingsManager.jpegQuality.set(parameters.get(camstring(R.string.jpeg_quality)));
        appSettingsManager.jpegQuality.setKEY(camstring(R.string.jpeg_quality));
        if (valuetoreturn.length >0)
            appSettingsManager.jpegQuality.setIsSupported(true);
    }



    private void detectAeBracketModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.ae_bracket_hdr,R.string.ae_bracket_hdr_values,appSettingsManager.aeBracket);
    }

    private void detectPreviewFPSModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_frame_rate,R.string.preview_frame_rate_values,appSettingsManager.previewFps);
    }

    private void detectPreviewFormatModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_format,R.string.preview_format_values,appSettingsManager.previewFormat);
    }

    private void detectSceneModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.scene_mode,R.string.scene_mode_values,appSettingsManager.sceneMode);
    }

    private void detectLensShadeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.lensshade,R.string.lensshade_values,appSettingsManager.lenshade);
    }

    private void detectZeroShutterLagModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.zsl_values)) != null)
        {
            detectMode(parameters,R.string.zsl,R.string.zsl_values,appSettingsManager.zeroshutterlag);

        }
        else if (parameters.get(camstring(R.string.mode_values)) != null)
        {
            detectMode(parameters,R.string.mode,R.string.mode_values,appSettingsManager.zeroshutterlag);
        }
        else if (parameters.get(camstring(R.string.zsd_mode)) != null) {
            detectMode(parameters, R.string.zsd_mode, R.string.zsd_mode_values, appSettingsManager.zeroshutterlag);
        }

        if (appSettingsManager.lenshade.getValues().length == 0)
            appSettingsManager.lenshade.setIsSupported(false);
    }

    private void detectSceneDetectModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.scene_mode,R.string.scene_mode_values,appSettingsManager.sceneMode);
    }

    private void detectMemoryColorEnhancementModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.mce,R.string.mce_values,appSettingsManager.memoryColorEnhancement);
    }

    private void detectVideoSizeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.video_size,R.string.video_size_values,appSettingsManager.videoSize);
    }

    private void detectCorrelatedDoubleSamplingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.cds_mode,R.string.cds_mode_values,appSettingsManager.correlatedDoubleSampling);
    }

    private void detectOisModes(Camera.Parameters parameters)
    {
        switch (appSettingsManager.getDevice())
        {
            case LG_G2:
            case LG_G2pro:
            case LG_G3:
                appSettingsManager.opticalImageStabilisation.setIsSupported(true);
                appSettingsManager.opticalImageStabilisation.setKEY(KEYS.LG_OIS);
                appSettingsManager.opticalImageStabilisation.setValues(new String[] {
                        KEYS.LG_OIS_PREVIEW_CAPTURE,KEYS.LG_OIS_CAPTURE,KEYS.LG_OIS_VIDEO,KEYS.LG_OIS_CENTERING_ONLY, KEYS.LG_OIS_CENTERING_OFF});
                appSettingsManager.opticalImageStabilisation.set(KEYS.LG_OIS_CENTERING_OFF);
                break;
            case XiaomiMI5:
                appSettingsManager.opticalImageStabilisation.setIsSupported(true);
                appSettingsManager.opticalImageStabilisation.setKEY("ois");
                appSettingsManager.opticalImageStabilisation.setValues(new String[] {
                        KEYS.ENABLE,KEYS.DISABLE});
                appSettingsManager.opticalImageStabilisation.set(KEYS.ENABLE);
                break;
            case p8lite:
                appSettingsManager.opticalImageStabilisation.setIsSupported(true);
                appSettingsManager.opticalImageStabilisation.setKEY("hw_ois_enable");
                appSettingsManager.opticalImageStabilisation.setValues(new String[] {
                        KEYS.ON,KEYS.OFF});
                appSettingsManager.opticalImageStabilisation.set(KEYS.ON);
                break;
            default:
                appSettingsManager.opticalImageStabilisation.setIsSupported(false);
        }
    }

    private void detectVideoHdr(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.video_hdr_values)) != null)
        {
            detectMode(parameters,R.string.video_hdr,R.string.video_hdr_values,appSettingsManager.videoHDR);
        }
        else if (parameters.get(camstring(R.string.sony_video_hdr_values))!= null) {
            detectMode(parameters,R.string.sony_video_hdr,R.string.sony_video_hdr_values,appSettingsManager.videoHDR);
        }
        else
            appSettingsManager.videoHDR.setIsSupported(false);
    }

    private void detectVideoHFR(Camera.Parameters parameters)
    {
        if (parameters.get("video-hfr") != null)
        {
            String hfrvals = parameters.get("video-hfr-values");
            if (!hfrvals.equals("off"))
            {
                if (hfrvals.equals("")) {
                    appSettingsManager.videoHFR.setValues("off,60,120".split(","));
                    appSettingsManager.videoHFR.setKEY("video-hfr");
                    appSettingsManager.videoHFR.setIsSupported(true);
                    appSettingsManager.videoHFR.set(parameters.get("video-hfr"));
                }
                else
                    appSettingsManager.videoHFR.setIsSupported(false);
            }
        }
        else if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get("hsvr-prv-fps-values") != null)
            {
                appSettingsManager.videoHFR.setValues(parameters.get("hsvr-prv-fps-values").split(","));
                appSettingsManager.videoHFR.setKEY("hsvr-prv-fps");
                appSettingsManager.videoHFR.setIsSupported(true);
                appSettingsManager.videoHFR.set(parameters.get("hsvr-prv-fps"));
            }
            else
                appSettingsManager.videoHFR.setIsSupported(false);
        }
        else
        {
            switch (appSettingsManager.getDevice())
            {
                case Htc_M8:
                case Htc_M9:
                case HTC_OneA9:
                case HTC_OneE8:
                    appSettingsManager.videoHFR.setValues("off,60,120".split(","));
                    appSettingsManager.videoHFR.setKEY("video-mode");
                    appSettingsManager.videoHFR.setIsSupported(true);
                    appSettingsManager.videoHFR.set(parameters.get("video-mode"));
                    break;
                default:
                    appSettingsManager.videoHFR.setIsSupported(false);
                    break;
            }

        }
    }

    private void detectVideoMediaProfiles(int cameraid)
    {
        final String _720phfr = "720HFR";
        final String _2160p = "2160p";
        final String _2160pDCI = "2160pDCI";
        HashMap<String,VideoMediaProfile> supportedProfiles;
        if(appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_LG)
            supportedProfiles =  getLGVideoMediaProfiles(cameraid);
        else
            supportedProfiles= getDefaultVideoMediaProfiles(cameraid);

        if (supportedProfiles.get(_720phfr) == null && appSettingsManager.videoHFR.isSupported() && appSettingsManager.videoHFR.contains("120"))
        {
            Log.d(TAG, "no 720phfr profile found, but hfr supported, try to add custom 720phfr");
            VideoMediaProfile t = supportedProfiles.get("720p").clone();
            t.videoFrameRate = 120;
            t.Mode = VideoMediaProfile.VideoMode.Highspeed;
            t.ProfileName = "720pHFR";
            supportedProfiles.put("720pHFR",t);
        }
        if (appSettingsManager.videoSize.isSupported() && appSettingsManager.videoSize.contains("3840x2160")
                && appSettingsManager.videoHFR.isSupported()&& appSettingsManager.videoHFR.contains("60")) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
        {
            if (supportedProfiles.containsKey("1080p"))
            {
                VideoMediaProfile uhdHFR = supportedProfiles.get("1080p").clone();
                uhdHFR.videoFrameWidth = 3840;
                uhdHFR.videoFrameHeight = 2160;
                uhdHFR.videoBitRate = 30000000;
                uhdHFR.Mode = VideoMediaProfile.VideoMode.Highspeed;
                uhdHFR.ProfileName = "UHD_2160p_60FPS";
                supportedProfiles.put("UHD_2160p_60FPS", uhdHFR);
                Log.d(TAG, "added custom 2160pHFR");
            }
        }
        if (supportedProfiles.get(_2160p) == null && appSettingsManager.videoSize.isSupported()&& appSettingsManager.videoSize.contains("3840x2160"))
        {
            if (supportedProfiles.containsKey("1080p"))
            {
                VideoMediaProfile uhd = supportedProfiles.get("1080p").clone();
                uhd.videoFrameWidth = 3840;
                uhd.videoFrameHeight = 2160;
                uhd.videoBitRate = 30000000;
                uhd.Mode = VideoMediaProfile.VideoMode.Normal;
                uhd.ProfileName = _2160p;
                supportedProfiles.put(_2160p, uhd);
                Log.d(TAG, "added custom 2160p");
            }
        }

        if (appSettingsManager.videoSize.isSupported() && appSettingsManager.videoSize.contains("1920x1080")
                && appSettingsManager.videoHFR.isSupported()&& appSettingsManager.videoHFR.contains("60")) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
        {
            if (supportedProfiles.containsKey("1080p")) {
                VideoMediaProfile t = supportedProfiles.get("1080p").clone();
                t.videoFrameRate = 60;
                t.Mode = VideoMediaProfile.VideoMode.Highspeed;
                t.ProfileName = "1080pHFR";
                supportedProfiles.put("1080pHFR", t);
                Log.d(TAG, "added custom 1080pHFR");
            }

        }
        appSettingsManager.saveMediaProfiles(supportedProfiles);
        appSettingsManager.setApiString(AppSettingsManager.VIDEOPROFILE, "720p");

        publishProgress("VideoMediaProfiles:" + getStringFromArray(supportedProfiles.keySet().toArray(new String[supportedProfiles.size()])));
    }
}
