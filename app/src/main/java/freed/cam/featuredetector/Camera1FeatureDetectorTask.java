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

import static freed.cam.apis.KEYS.AE_BRACKET_HDR_VALUES;
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

    private void detectQcomFocus(Camera.Parameters parameters)
    {
        if (parameters.get("touch-af-aec")!= null)
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
                    appSettingsManager.manualWhiteBalance.setMode(KEYS.WB_MODE_MANUAL);
                    appSettingsManager.manualWhiteBalance.setIsSupported(true);
                    break;
                case XiaomiMI3W:
                case XiaomiMI4W:
                case XiaomiMI4C:
                {
                    if(!DeviceUtils.isCyanogenMod()) {
                        if (Build.VERSION.SDK_INT < 23) {
                            appSettingsManager.manualWhiteBalance.setValues(createWBStringArray(2000,7500,100));
                            appSettingsManager.manualWhiteBalance.setMode(KEYS.WB_MODE_MANUAL);
                        } else {
                            appSettingsManager.manualWhiteBalance.setValues(createWBStringArray(2000,8000,100));
                            appSettingsManager.manualWhiteBalance.setMode(KEYS.WB_MODE_MANUAL_CCT);
                        }
                        appSettingsManager.manualWhiteBalance.setIsSupported(true);
                        break;
                    }
                }
                default:
                    // looks like wb-current-cct is loaded when the preview is up. this could be also for the other parameters
                    String wbModeval ="", wbmax = "",wbmin = "";

                    if (parameters.get(KEYS.MAX_WB_CCT) != null) {
                        wbmax = KEYS.MAX_WB_CCT;
                    }
                    else if (parameters.get(KEYS.MAX_WB_CT)!= null)
                        wbmax =KEYS.MAX_WB_CT;

                    if (parameters.get(KEYS.MIN_WB_CCT)!= null) {
                        wbmin =KEYS.MIN_WB_CCT;
                    } else if (parameters.get(KEYS.MIN_WB_CT)!= null)
                        wbmin =KEYS.MIN_WB_CT;

                    if (arrayContainsString(appSettingsManager.whiteBalanceMode.getValues(), KEYS.WB_MODE_MANUAL))
                        wbModeval = KEYS.WB_MODE_MANUAL;
                    else if (arrayContainsString(appSettingsManager.whiteBalanceMode.getValues(), KEYS.WB_MODE_MANUAL_CCT))
                        wbModeval = KEYS.WB_MODE_MANUAL_CCT;

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
                    appSettingsManager.manualExposureTime.setKEY(KEYS.EXPOSURE_TIME);
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MICORSEC);
                    break;
                case LG_G2pro:
                    appSettingsManager.manualExposureTime.setIsSupported(true);
                    appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_lg_g2pro));
                    appSettingsManager.manualExposureTime.setKEY(KEYS.EXPOSURE_TIME);
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_G2PRO);
                    break;
                case ZTE_ADV:
                    appSettingsManager.manualExposureTime.setIsSupported(true);
                    appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_zte_z5s));
                    appSettingsManager.manualExposureTime.setKEY(KEYS.EXPOSURE_TIME);
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_ZTE);
                    break;
                case  ZTEADVIMX214:
                case ZTEADV234:
                case ZTE_Z11:
                    appSettingsManager.manualExposureTime.setIsSupported(true);
                    appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_zte_z7));
                    appSettingsManager.manualExposureTime.setKEY(KEYS.EXPOSURE_TIME);
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
                    else if (parameters.get(KEYS.MAX_EXPOSURE_TIME) != null && parameters.get(KEYS.MIN_EXPOSURE_TIME) != null) {
                        long min = 0, max = 0;
                        switch (appSettingsManager.getDevice()) {
                            case OnePlusX:
                                min = Integer.parseInt(parameters.get(KEYS.MIN_EXPOSURE_TIME)) * 1000;
                                max = Integer.parseInt(parameters.get(KEYS.MAX_EXPOSURE_TIME)) * 1000;
                                appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MICORSEC);
                                break;
                            default:
                                if (parameters.get(KEYS.MAX_EXPOSURE_TIME).contains(".")) {
                                    min = (long) Double.parseDouble(parameters.get(KEYS.MIN_EXPOSURE_TIME)) * 1000;
                                    max = (long) Double.parseDouble(parameters.get(KEYS.MAX_EXPOSURE_TIME)) * 1000;
                                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MICORSEC);
                                } else {
                                    min = Integer.parseInt(parameters.get(KEYS.MIN_EXPOSURE_TIME));
                                    max = Integer.parseInt(parameters.get(KEYS.MAX_EXPOSURE_TIME));
                                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MILLISEC);
                                }
                                break;
                        }
                        if (max > 0) {
                            appSettingsManager.manualExposureTime.setIsSupported(true);
                            appSettingsManager.manualExposureTime.setKEY(KEYS.EXPOSURE_TIME);
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
            if(parameters.get(KEYS.MTK_NOISE_REDUCTION_MODE)!=null) {
                if (parameters.get(KEYS.MTK_NOISE_REDUCTION_MODE_VALUES).equals("on,off")) {
                    appSettingsManager.denoiseMode.setIsSupported(true);
                    appSettingsManager.denoiseMode.setKEY(KEYS.MTK_NOISE_REDUCTION_MODE);
                    appSettingsManager.denoiseMode.setValues(parameters.get(KEYS.MTK_NOISE_REDUCTION_MODE_VALUES).split(","));
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
                    if (parameters.get(KEYS.DENOISE) != null && parameters.get(KEYS.DENOISE_VALUES) != null)
                    {
                        appSettingsManager.denoiseMode.setIsSupported(true);
                        appSettingsManager.denoiseMode.setValues( parameters.get(KEYS.DENOISE_VALUES).split(","));
                        appSettingsManager.denoiseMode.setKEY(KEYS.DENOISE);
                    }
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
                    if (parameters.get(KEYS.DIGITALIMAGESTABILIZATION) != null && parameters.get(KEYS.DIGITALIMAGESTABILIZATION_VALUES) != null)
                    {
                        appSettingsManager.digitalImageStabilisationMode.setIsSupported(true);
                        appSettingsManager.digitalImageStabilisationMode.setKEY(KEYS.DIGITALIMAGESTABILIZATION);
                        appSettingsManager.digitalImageStabilisationMode.setValues(parameters.get(KEYS.DIGITALIMAGESTABILIZATION_VALUES).split(","));
                    }
                    break;
            }

        }
    }

    private void detectManualSaturation(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get(KEYS.SATURATION)!= null && parameters.get(KEYS.SATURATION_VALUES)!= null) {
                appSettingsManager.manualSaturation.setValues(parameters.get(KEYS.SATURATION_VALUES).split(","));
                appSettingsManager.manualSaturation.setKEY(KEYS.SATURATION);
                appSettingsManager.manualSaturation.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(KEYS.LG_COLOR_ADJUST_MAX) != null && parameters.get(KEYS.LG_COLOR_ADJUST_MIN) != null) {
                min = Integer.parseInt(KEYS.LG_COLOR_ADJUST_MIN);
                max = Integer.parseInt(KEYS.LG_COLOR_ADJUST_MAX);
                appSettingsManager.manualSaturation.setKEY(KEYS.LG_COLOR_ADJUST);
            }
            else if (parameters.get(KEYS.SATURATION_MAX) != null) {
                min = Integer.parseInt(KEYS.SATURATION_MIN);
                max = Integer.parseInt(KEYS.SATURATION_MAX);
                appSettingsManager.manualSaturation.setKEY(KEYS.SATURATION);
            } else if (parameters.get(KEYS.MAX_SATURATION) != null) {
                min = Integer.parseInt(KEYS.MIN_SATURATION);
                max = Integer.parseInt(KEYS.MAX_SATURATION);
                appSettingsManager.manualSaturation.setKEY(KEYS.SATURATION);
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
            if (parameters.get(KEYS.EDGE)!= null && parameters.get(KEYS.EDGE_VALUES)!= null) {
                appSettingsManager.manualSharpness.setValues(parameters.get(KEYS.EDGE_VALUES).split(","));
                appSettingsManager.manualSharpness.setKEY(KEYS.EDGE);
                appSettingsManager.manualSharpness.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(KEYS.SHARPNESS_MAX) != null) {
                min = Integer.parseInt(KEYS.SHARPNESS_MIN);
                max = Integer.parseInt(KEYS.SHARPNESS_MAX);
                appSettingsManager.manualSharpness.setKEY(KEYS.SHARPNESS);
            } else if (parameters.get(KEYS.MAX_SHARPNESS) != null) {
                min = Integer.parseInt(KEYS.MIN_SHARPNESS);
                max = Integer.parseInt(KEYS.MAX_SHARPNESS);
                appSettingsManager.manualSharpness.setKEY(KEYS.SHARPNESS);
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
            if (parameters.get(KEYS.BRIGHTNESS)!= null && parameters.get(KEYS.BRIGHTNESS_VALUES)!= null) {
                appSettingsManager.manualBrightness.setValues(parameters.get(KEYS.BRIGHTNESS_VALUES).split(","));
                appSettingsManager.manualBrightness.setKEY(KEYS.BRIGHTNESS);
                appSettingsManager.manualBrightness.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(KEYS.BRIGHTNESS_MAX) != null) {
                min = Integer.parseInt(KEYS.BRIGHTNESS_MIN);
                max = Integer.parseInt(KEYS.BRIGHTNESS_MAX);
            } else if (parameters.get(KEYS.MAX_BRIGHTNESS) != null) {
                min = Integer.parseInt(KEYS.MIN_BRIGHTNESS);
                max = Integer.parseInt(KEYS.MAX_BRIGHTNESS);

            }
            if (max > 0) {
                if (parameters.get(KEYS.BRIGHTNESS)!= null)
                    appSettingsManager.manualBrightness.setKEY(KEYS.BRIGHTNESS);
                else if (parameters.get(KEYS.LUMA_ADAPTATION)!= null)
                    appSettingsManager.manualBrightness.setKEY(KEYS.LUMA_ADAPTATION);
                appSettingsManager.manualBrightness.setValues(createStringArray(min, max, 1));
                appSettingsManager.manualBrightness.setIsSupported(true);
            }
        }
    }

    private void detectManualContrast(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get(KEYS.CONTRAST)!= null && parameters.get(KEYS.CONTRAST_VALUES)!= null) {
                appSettingsManager.manualContrast.setValues(parameters.get(KEYS.CONTRAST_VALUES).split(","));
                appSettingsManager.manualContrast.setKEY(KEYS.CONTRAST);
                appSettingsManager.manualContrast.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(KEYS.CONTRAST_MAX) != null) {
                min = Integer.parseInt(KEYS.CONTRAST_MIN);
                max = Integer.parseInt(KEYS.CONTRAST_MAX);
            } else if (parameters.get(KEYS.MAX_CONTRAST) != null) {
                min = Integer.parseInt(KEYS.MIN_CONTRAST);
                max = Integer.parseInt(KEYS.MAX_CONTRAST);

            }
            if (max > 0) {
                appSettingsManager.manualContrast.setKEY(KEYS.CONTRAST);
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
            appSettingsManager.manualFocus.setMode(KEYS.KEY_FOCUS_MODE_MANUAL);
            appSettingsManager.manualFocus.setType(-1);
            appSettingsManager.manualFocus.setIsSupported(true);
            min = 0;
            max = 1023;
            step = 10;
            appSettingsManager.manualFocus.setKEY(KEYS.AFENG_POS);
        }
        else {
            //lookup old qcom

            if (parameters.get(KEYS.KEY_MANUAL_FOCUS_MODE_VALUE) == null) {

                if (parameters.get(KEYS.MAX_FOCUS_POS_INDEX) != null
                        && KEYS.MIN_FOCUS_POS_INDEX != null
                        && appSettingsManager.focusMode.contains(KEYS.KEY_FOCUS_MODE_MANUAL)) {

                    appSettingsManager.manualFocus.setMode(KEYS.KEY_FOCUS_MODE_MANUAL);
                    appSettingsManager.manualFocus.setType(1);
                    appSettingsManager.manualFocus.setIsSupported(true);
                    min = Integer.parseInt(parameters.get(KEYS.MIN_FOCUS_POS_INDEX));
                    max = Integer.parseInt(parameters.get(KEYS.MAX_FOCUS_POS_INDEX));
                    step = 10;
                    appSettingsManager.manualFocus.setKEY(KEYS.KEY_MANUAL_FOCUS_POSITION);
                }
            }
            else
            {
                //lookup new qcom
                if (parameters.get(KEYS.MAX_FOCUS_POS_RATIO) != null
                        && KEYS.MIN_FOCUS_POS_RATIO != null
                        && appSettingsManager.focusMode.contains(KEYS.KEY_FOCUS_MODE_MANUAL)) {

                    appSettingsManager.manualFocus.setMode(KEYS.KEY_FOCUS_MODE_MANUAL);
                    appSettingsManager.manualFocus.setType(2);
                    appSettingsManager.manualFocus.setIsSupported(true);
                    min = Integer.parseInt(parameters.get(KEYS.MIN_FOCUS_POS_RATIO));
                    max = Integer.parseInt(parameters.get(KEYS.MAX_FOCUS_POS_RATIO));
                    step = 1;
                    appSettingsManager.manualFocus.setKEY(KEYS.KEY_MANUAL_FOCUS_POSITION);
                }
            }
            //htc mf
            if (parameters.get(KEYS.MIN_FOCUS) != null && parameters.get(KEYS.MAX_FOCUS) != null)
            {
                appSettingsManager.manualFocus.setMode("");
                appSettingsManager.manualFocus.setType(-1);
                appSettingsManager.manualFocus.setIsSupported(true);
                min = Integer.parseInt(parameters.get(KEYS.MIN_FOCUS));
                max = Integer.parseInt(parameters.get(KEYS.MAX_FOCUS));
                step = 1;
                appSettingsManager.manualFocus.setKEY(KEYS.FOCUS);
            }

            //huawai mf
            if(parameters.get(KEYS.HW_VCM_END_VALUE) != null && parameters.get(KEYS.HW_VCM_START_VALUE) != null)
            {
                appSettingsManager.manualFocus.setMode(KEYS.KEY_FOCUS_MODE_MANUAL);
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
                    appSettingsManager.manualFocus.setMode(KEYS.KEY_FOCUS_MODE_MANUAL);
                    appSettingsManager.manualFocus.setType(2);
                    appSettingsManager.manualFocus.setIsSupported(true);
                    appSettingsManager.manualFocus.setKEY(KEYS.KEY_MANUAL_FOCUS_POSITION);
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
                appSettingsManager.manualFocus.setMode(KEYS.KEY_FOCUS_MODE_MANUAL);
                appSettingsManager.manualFocus.setType(1);
                appSettingsManager.manualFocus.setIsSupported(true);
                min = 0;
                max = 79;
                step = 1;
                appSettingsManager.manualFocus.setKEY(KEYS.KEY_MANUAL_FOCUS_POSITION);
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
                    String formats = parameters.get(KEYS.PICTURE_FORMAT_VALUES);

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
        String[] sizes = parameters.get(KEYS.PICTURE_SIZE_VALUES).split(",");
        appSettingsManager.pictureSize.setValues(sizes);
        appSettingsManager.pictureSize.set(parameters.get(KEYS.PICTURE_SIZE));
        if (sizes.length > 0)
            appSettingsManager.pictureSize.setIsSupported(true);
        else
            appSettingsManager.pictureSize.setIsSupported(false);
    }

    private void detectFocusModes(Camera.Parameters parameters)
    {
        appSettingsManager.focusMode.setValues(parameters.get(KEYS.FOCUS_MODE_VALUES).split(","));
        appSettingsManager.focusMode.set(parameters.get(KEYS.FOCUS_MODE));
        if (appSettingsManager.focusMode.getValues().length >0)
            appSettingsManager.focusMode.setIsSupported(true);
        else
            appSettingsManager.focusMode.setIsSupported(false);
    }

    private void detectWhiteBalanceModes(Camera.Parameters parameters)
    {
        appSettingsManager.whiteBalanceMode.setValues(parameters.get(KEYS.WHITEBALANCE_VALUES).split(","));
        appSettingsManager.whiteBalanceMode.set(parameters.get(KEYS.WHITEBALANCE));
        if (appSettingsManager.whiteBalanceMode.getValues().length >0)
            appSettingsManager.whiteBalanceMode.setIsSupported(true);
        else
            appSettingsManager.whiteBalanceMode.setIsSupported(false);
    }

    private void detectExposureModes(Camera.Parameters parameters)
    {
        if (parameters.get("exposure-mode-values")!= null) {
            appSettingsManager.exposureMode.setKEY("exposure");
            appSettingsManager.exposureMode.set(parameters.get("exposure"));
            appSettingsManager.exposureMode.setValues(parameters.get("exposure-mode-values").split(","));
        }
        else if (parameters.get("auto-exposure-values")!= null) {
            appSettingsManager.exposureMode.setKEY("auto-exposure");
            appSettingsManager.exposureMode.set(parameters.get("auto-exposure"));
            appSettingsManager.exposureMode.setValues(parameters.get("auto-exposure-values").split(","));
        }
        else if(parameters.get("sony-metering-mode-values")!= null) {
            appSettingsManager.exposureMode.setKEY("sony-metering-mode");
            appSettingsManager.exposureMode.set(parameters.get("sony-metering-mode"));
            appSettingsManager.exposureMode.setValues(parameters.get("sony-metering-mode-values").split(","));
        }
        else if(parameters.get("exposure-meter-values")!= null) {
            appSettingsManager.exposureMode.setKEY("exposure-meter");
            appSettingsManager.exposureMode.set(parameters.get("exposure-meter"));
            appSettingsManager.exposureMode.setValues(parameters.get("exposure-meter-values").split(","));
        }
        if (!appSettingsManager.exposureMode.getKEY().equals(""))
            appSettingsManager.exposureMode.setIsSupported(true);
        else
            appSettingsManager.exposureMode.setIsSupported(false);
    }

    private void detectColorModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.COLOR_EFFECT_VALUES) == null)
        {
            appSettingsManager.colorMode.setIsSupported(false);
            return;
        }
        appSettingsManager.colorMode.setValues(parameters.get(KEYS.COLOR_EFFECT_VALUES).split(","));
        appSettingsManager.colorMode.set(parameters.get(KEYS.COLOR_EFFECT));
        if (appSettingsManager.colorMode.getValues().length >0)
            appSettingsManager.colorMode.setIsSupported(true);
    }

    private void detectFlashModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.FLASH_MODE_VALUES) == null)
        {
            appSettingsManager.flashMode.setIsSupported(false);
            return;
        }
        appSettingsManager.flashMode.setValues(parameters.get(KEYS.FLASH_MODE_VALUES).split(","));
        appSettingsManager.flashMode.set(parameters.get(KEYS.FLASH_MODE));
        if (appSettingsManager.flashMode.getValues().length >0)
            appSettingsManager.flashMode.setIsSupported(true);
    }

    private void detectIsoModes(Camera.Parameters parameters)
    {
        if (parameters.get("iso-mode-values")!= null){
            appSettingsManager.isoMode.setKEY("iso");
            appSettingsManager.isoMode.setValues(parameters.get("iso-mode-values").split(","));
            appSettingsManager.isoMode.set(parameters.get("iso"));
        }
        else if (parameters.get("iso-values")!= null) {
            appSettingsManager.isoMode.setKEY("iso");
            appSettingsManager.isoMode.setValues(parameters.get("iso-values").split(","));
            appSettingsManager.isoMode.set(parameters.get("iso"));
        }
        else if (parameters.get("iso-speed-values")!= null) {
            appSettingsManager.isoMode.setKEY("iso-speed");
            appSettingsManager.isoMode.setValues(parameters.get("iso-speed-values").split(","));
            appSettingsManager.isoMode.set(parameters.get("iso-speed"));
        }
        else if (parameters.get("sony-iso-values")!= null) {
            appSettingsManager.isoMode.setKEY("sony-iso");
            appSettingsManager.isoMode.setValues(parameters.get("sony-iso-values").split(","));
            appSettingsManager.isoMode.set(parameters.get("sony-iso"));
        }
        else if (parameters.get("lg-iso-values")!= null) {
            appSettingsManager.isoMode.setKEY("iso");
            appSettingsManager.isoMode.setValues(parameters.get("lg-iso-values").split(","));
            appSettingsManager.isoMode.set(parameters.get("iso"));
        }
        if (appSettingsManager.isoMode.getValues().length >1)
            appSettingsManager.isoMode.setIsSupported(true);
        else
            appSettingsManager.isoMode.setIsSupported(false);
    }

    private void detectAntiBandingModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.ANTIBANDING_VALUES) == null)
        {
            appSettingsManager.antiBandingMode.setIsSupported(false);
            return;
        }
        appSettingsManager.antiBandingMode.setValues(parameters.get(KEYS.ANTIBANDING_VALUES).split(","));
        appSettingsManager.antiBandingMode.set(parameters.get(KEYS.ANTIBANDING));
        if (appSettingsManager.antiBandingMode.getValues().length >0)
            appSettingsManager.antiBandingMode.setIsSupported(true);
    }

    private void detectImagePostProcessingModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.IMAGEPOSTPROCESSING_VALUES) == null)
        {
            appSettingsManager.imagePostProcessing.setIsSupported(false);
            return;
        }
        appSettingsManager.imagePostProcessing.setValues(parameters.get(KEYS.IMAGEPOSTPROCESSING_VALUES).split(","));
        appSettingsManager.imagePostProcessing.set(parameters.get(KEYS.IMAGEPOSTPROCESSING));
        if (appSettingsManager.imagePostProcessing.getValues().length >0)
            appSettingsManager.imagePostProcessing.setIsSupported(true);
    }

    private void detectPreviewSizeModes(Camera.Parameters parameters)
    {
        if (parameters.get("preview-size-values") == null)
        {
            appSettingsManager.previewSize.setIsSupported(false);
            return;
        }
        appSettingsManager.previewSize.setValues(parameters.get("preview-size-values").split(","));
        appSettingsManager.previewSize.set(parameters.get("preview-size"));
        if (appSettingsManager.previewSize.getValues().length >0)
            appSettingsManager.previewSize.setIsSupported(true);
    }

    private void detectJpeqQualityModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.JPEG_QUALITY) == null)
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
        appSettingsManager.jpegQuality.set(parameters.get(KEYS.JPEG_QUALITY));
        if (valuetoreturn.length >0)
            appSettingsManager.jpegQuality.setIsSupported(true);
    }

    private void detectAeBracketModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.AE_BRACKET_HDR_VALUES) == null)
        {
            appSettingsManager.aeBracket.setIsSupported(false);
            return;
        }
        appSettingsManager.aeBracket.setValues(parameters.get(AE_BRACKET_HDR_VALUES).split(","));
        appSettingsManager.aeBracket.set(parameters.get(KEYS.AE_BRACKET_HDR));
        if (appSettingsManager.aeBracket.getValues().length >0)
            appSettingsManager.aeBracket.setIsSupported(true);
    }

    private void detectPreviewFPSModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.PREVIEW_FRAME_RATE_VALUES) == null)
        {
            appSettingsManager.previewFps.setIsSupported(false);
            return;
        }
        appSettingsManager.previewFps.setValues(parameters.get(KEYS.PREVIEW_FRAME_RATE_VALUES).split(","));
        appSettingsManager.previewFps.set(parameters.get(KEYS.PREVIEW_FRAME_RATE));
        if (appSettingsManager.previewFps.getValues().length >0)
            appSettingsManager.previewFps.setIsSupported(true);
    }

    private void detectPreviewFormatModes(Camera.Parameters parameters)
    {
        if (parameters.get("preview-format-values") == null)
        {
            appSettingsManager.previewFormat.setIsSupported(false);
            return;
        }
        appSettingsManager.previewFormat.setValues(parameters.get("preview-format-values").split(","));
        appSettingsManager.previewFormat.set(parameters.get("preview-format"));
        if (appSettingsManager.previewFormat.getValues().length >0)
            appSettingsManager.previewFormat.setIsSupported(true);
    }

    private void detectSceneModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.SCENE_MODE_VALUES) == null)
        {
            appSettingsManager.sceneMode.setIsSupported(false);
            return;
        }
        appSettingsManager.sceneMode.setValues(parameters.get(KEYS.SCENE_MODE_VALUES).split(","));
        appSettingsManager.sceneMode.set(parameters.get(KEYS.SCENE_MODE));
        if (appSettingsManager.sceneMode.getValues().length >0)
            appSettingsManager.sceneMode.setIsSupported(true);
    }

    private void detectLensShadeModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.LENSSHADE) == null)
        {
            appSettingsManager.lenshade.setIsSupported(false);
            return;
        }
        appSettingsManager.lenshade.setValues(parameters.get(KEYS.LENSSHADE_VALUES).split(","));
        appSettingsManager.lenshade.set(parameters.get(KEYS.LENSSHADE));
        if (appSettingsManager.lenshade.getValues().length >0)
            appSettingsManager.lenshade.setIsSupported(true);
    }

    private void detectZeroShutterLagModes(Camera.Parameters parameters)
    {
        if (parameters.get("zsl") != null)
        {
            appSettingsManager.zeroshutterlag.setValues(parameters.get("zsl-values").split(","));
            appSettingsManager.zeroshutterlag.set(parameters.get("zsl"));
            appSettingsManager.zeroshutterlag.setKEY("zsl");
            appSettingsManager.zeroshutterlag.setIsSupported(true);
        }
        else if (parameters.get("mode") != null)
        {
            appSettingsManager.zeroshutterlag.setValues(parameters.get("mode-values").split(","));
            appSettingsManager.zeroshutterlag.set(parameters.get("mode"));
            appSettingsManager.zeroshutterlag.setKEY("mode");
            appSettingsManager.zeroshutterlag.setIsSupported(true);
        }
        else if (parameters.get("zsd-mode") != null)
        {
            appSettingsManager.zeroshutterlag.setValues(parameters.get("zsd-mode-values").split(","));
            appSettingsManager.zeroshutterlag.set(parameters.get("zsd-mode"));
            appSettingsManager.zeroshutterlag.setKEY("zsd-mode");
            appSettingsManager.zeroshutterlag.setIsSupported(true);
        }

        if (appSettingsManager.lenshade.getValues().length == 0)
            appSettingsManager.lenshade.setIsSupported(false);
    }

    private void detectSceneDetectModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.SCENE_DETECT) == null)
        {
            appSettingsManager.sceneDetectMode.setIsSupported(false);
            return;
        }
        appSettingsManager.sceneDetectMode.setValues(parameters.get(KEYS.SCENE_MODE_VALUES).split(","));
        appSettingsManager.sceneDetectMode.set(parameters.get(KEYS.SCENE_DETECT));
        if (appSettingsManager.sceneDetectMode.getValues().length >0)
            appSettingsManager.sceneDetectMode.setIsSupported(true);
    }

    private void detectMemoryColorEnhancementModes(Camera.Parameters parameters)
    {
        if (parameters.get(KEYS.MEMORYCOLORENHANCEMENT) == null)
        {
            appSettingsManager.memoryColorEnhancement.setIsSupported(false);
            return;
        }
        String mce = parameters.get(KEYS.MEMORYCOLORENHANCEMENT_VALUES);
        appSettingsManager.memoryColorEnhancement.setValues(mce.split(","));
        appSettingsManager.memoryColorEnhancement.set(parameters.get(KEYS.MEMORYCOLORENHANCEMENT));
        if (appSettingsManager.memoryColorEnhancement.getValues().length >0)
            appSettingsManager.memoryColorEnhancement.setIsSupported(true);
    }

    private void detectVideoSizeModes(Camera.Parameters parameters)
    {
        if (parameters.get("video-size") == null)
        {
            appSettingsManager.videoSize.setIsSupported(false);
            return;
        }
        appSettingsManager.videoSize.setValues(parameters.get("video-size-values").split(","));
        appSettingsManager.videoSize.set(parameters.get("video-size"));
        if (appSettingsManager.videoSize.getValues().length >0)
            appSettingsManager.videoSize.setIsSupported(true);
    }

    private void detectCorrelatedDoubleSamplingModes(Camera.Parameters parameters)
    {
        if (parameters.get("cds-mode") == null)
        {
            appSettingsManager.correlatedDoubleSampling.setIsSupported(false);
            return;
        }
        appSettingsManager.correlatedDoubleSampling.setValues(parameters.get("cds-mode-values").split(","));
        appSettingsManager.correlatedDoubleSampling.set(parameters.get("cds-mode"));
        if (appSettingsManager.correlatedDoubleSampling.getValues().length >0)
            appSettingsManager.correlatedDoubleSampling.setIsSupported(true);
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
        if (parameters.get("video-hdr-values") != null)
        {
            appSettingsManager.videoHDR.setIsSupported(true);
            appSettingsManager.videoHDR.setKEY("video-hdr");
            appSettingsManager.videoHDR.setValues(parameters.get("video-hdr-values").split(","));
        }
        else if (parameters.get("sony-video-hdr")!= null) {
            appSettingsManager.videoHDR.setIsSupported(true);
            appSettingsManager.videoHDR.setKEY("sony-video-hdr");
            appSettingsManager.videoHDR.setValues(parameters.get("sony-video-hdr-values").split(","));
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
