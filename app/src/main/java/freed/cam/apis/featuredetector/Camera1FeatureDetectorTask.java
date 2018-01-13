package freed.cam.apis.featuredetector;

import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;

import com.lge.hardware.LGCameraRef;
import com.troop.freedcam.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.camera1.cameraholder.CameraHolderLegacy;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.settings.FrameworkDetector;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.mode.SettingMode;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.VideoMediaProfile;


/**
 * Created by troop on 23.01.2017.
 */

public class Camera1FeatureDetectorTask extends AbstractFeatureDetectorTask
{
    private static final  String TAG = Camera1FeatureDetectorTask.class.getSimpleName();

    public Camera1FeatureDetectorTask(ProgressUpdate progressUpdate)
    {
        super(progressUpdate);
    }

    private String camstring(int id)
    {
        return SettingsManager.getInstance().getResString(id);
    }

    public void detect()
    {
        publishProgress("###################");
        publishProgress("#######Camera1#####");
        publishProgress("###################");
        SettingsManager.getInstance().setCamApi(SettingsManager.API_1);

        publishProgress("Device:"+ SettingsManager.getInstance().getDeviceString());
        //detect frameworks
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.Default)
            SettingsManager.getInstance().setFramework(FrameworkDetector.getFramework());
        publishProgress("FrameWork:"+ SettingsManager.getInstance().getFrameWork());


        int cameraCounts = Camera.getNumberOfCameras();
        SettingsManager.getInstance().setCamerasCount(cameraCounts);
        Log.d(TAG, "Cameras Found: " + cameraCounts);
        SettingsManager appS = SettingsManager.getInstance();
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

            appS.get(SettingKeys.selfTimer).setValues(appS.getResources().getStringArray(R.array.selftimervalues));
            appS.get(SettingKeys.selfTimer).set(appS.get(SettingKeys.selfTimer).getValues()[0]);

            appS.get(SettingKeys.GuideList).setValues(appS.getResources().getStringArray(R.array.guidelist));
            appS.get(SettingKeys.GuideList).set(appS.get(SettingKeys.GuideList).getValues()[0]);

            detectedPictureFormats(parameters);
            publishProgress("DngSupported:" + (appS.getDngProfilesMap() != null && appS.getDngProfilesMap().size() > 0) + " RawSupport:"+appS.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isSupported());
            publishProgress("PictureFormats:" + getStringFromArray(appS.get(SettingKeys.PictureFormat).getValues()));
            publishProgress("RawFormats:" + getStringFromArray(appS.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).getValues()));
            publishProgress(" RawFormat:" + appS.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).get());

            SettingsManager.get(SettingKeys.Module).set(appS.getResString(R.string.module_picture));

            detectPictureSizes(parameters);
            sendProgress(appS.get(SettingKeys.PictureSize),"PictureSize");

            detectFocusModes(parameters);
            sendProgress(appS.get(SettingKeys.FocusMode),"FocusMode");

            detectWhiteBalanceModes(parameters);
            sendProgress(appS.get(SettingKeys.WhiteBalanceMode),"WhiteBalance");

            detectExposureModes(parameters);
            sendProgress(appS.get(SettingKeys.ExposureMode),"ExposureMode");

            detectColorModes(parameters);
            sendProgress(appS.get(SettingKeys.ColorMode),"Color");

            detectFlashModes(parameters);
            sendProgress(appS.get(SettingKeys.FlashMode),"FLash");

            detectIsoModes(parameters);
            sendProgress(appS.get(SettingKeys.IsoMode),"Iso");

            detectAntiBandingModes(parameters);
            sendProgress(appS.get(SettingKeys.AntiBandingMode),"AntiBanding");

            detectImagePostProcessingModes(parameters);
            sendProgress(appS.get(SettingKeys.ImagePostProcessing),"ImagePostProcessing");

            detectPreviewSizeModes(parameters);
            sendProgress(appS.get(SettingKeys.PreviewSize),"PreviewSize");

            detectJpeqQualityModes(parameters);
            sendProgress(appS.get(SettingKeys.JpegQuality),"JpegQuality");

            detectAeBracketModes(parameters);
            sendProgress(appS.get(SettingKeys.AE_Bracket),"AeBracket");

            detectPreviewFPSModes(parameters);
            sendProgress(appS.get(SettingKeys.PreviewFPS),"PreviewFPS");

            detectPreviewFpsRanges(parameters);

            detectPreviewFormatModes(parameters);
            sendProgress(appS.get(SettingKeys.PreviewFormat),"PreviewFormat");

            detectSceneModes(parameters);
            sendProgress(appS.get(SettingKeys.SceneMode),"Scene");

            detectLensShadeModes(parameters);
            sendProgress(appS.get(SettingKeys.LensShade),"Lensshade");

            detectZeroShutterLagModes(parameters);
            sendProgress(appS.get(SettingKeys.ZSL),"ZeroShutterLag");

            detectSceneDetectModes(parameters);
            sendProgress(appS.get(SettingKeys.SceneDetect),"SceneDetect");

            detectMemoryColorEnhancementModes(parameters);
            sendProgress(appS.get(SettingKeys.MemoryColorEnhancement),"MemoryColorEnhancement");

            detectVideoSizeModes(parameters);
            sendProgress(appS.get(SettingKeys.VideoSize),"VideoSize");

            detectCorrelatedDoubleSamplingModes(parameters);
            sendProgress(appS.get(SettingKeys.CDS_Mode),"CorrelatedDoubleSampling");

            detectDisModes(parameters);
            sendProgress(appS.get(SettingKeys.DigitalImageStabilization), "DigitalImageStabilisation");

            detectDenoise(parameters);
            sendProgress(appS.get(SettingKeys.Denoise), "Denoise");

            detectTNR(parameters);
            sendProgress(appS.get(SettingKeys.TNR), "Temporoal_NR");
            sendProgress(appS.get(SettingKeys.TNR_V), "Temporoal_VIDEO_NR");

            detectPDAF(parameters);
            sendProgress(appS.get(SettingKeys.PDAF), "PDAF");

            detectSEEMoar(parameters);
            sendProgress(appS.get(SettingKeys.SeeMore), "StillMoreToneMap");

            detectTruePotrait(parameters);
            sendProgress(appS.get(SettingKeys.TruePotrait), "TruePotrait");

            detectRefocus(parameters);
            sendProgress(appS.get(SettingKeys.ReFocus), "ReFocus");

            detectOptizoom(parameters);
            sendProgress(appS.get(SettingKeys.OptiZoom), "OptiZoom");

            detectChromaFlash(parameters);
            sendProgress(appS.get(SettingKeys.ChromaFlash), "ChromaFlash");

            detectRDI(parameters);
            sendProgress(appS.get(SettingKeys.RDI), "RDI");


            detectNonZslmanual(parameters);
            sendProgress(appS.get(SettingKeys.NonZslManualMode), "NonZslManual");

            detectVideoHdr(parameters);
            sendProgress(appS.get(SettingKeys.VideoHDR), "VideoHDR");

            detectVideoHFR(parameters);
            sendProgress(appS.get(SettingKeys.VideoHighFramerate),"VideoHFR");

            detectVideoMediaProfiles(i);

            detectManualFocus(parameters);
            sendProgress(appS.get(SettingKeys.M_Focus),"ManualFocus");

            detectManualSaturation(parameters);
            sendProgress(appS.get(SettingKeys.M_Saturation),"ManualSaturation");

            detectManualSharpness(parameters);
            sendProgress(appS.get(SettingKeys.M_Sharpness),"ManualSharpness");

            detectManualBrightness(parameters);
            sendProgress(appS.get(SettingKeys.M_Brightness),"ManualBrightness");

            detectManualContrast(parameters);
            sendProgress(appS.get(SettingKeys.M_Contrast),"ManualContrast");

            detectManualExposureTime(parameters);
            sendProgress(appS.get(SettingKeys.M_ExposureTime),"ExposureTime");

            detectManualIso(parameters);
            sendProgress(appS.get(SettingKeys.M_ManualIso),"Manual ISo");

            detectManualWhiteBalance(parameters);
            sendProgress(appS.get(SettingKeys.M_Whitebalance),"Manual Wb");

            detectQcomFocus(parameters);

            detectAutoHdr(parameters);

            if (parameters.get("hw-dual-primary-supported") != null)
            {
                SettingsManager.get(SettingKeys.dualPrimaryCameraMode).setValues(parameters.get("hw-dual-primary-supported").split(","));
                SettingsManager.get(SettingKeys.dualPrimaryCameraMode).setKEY("hw-dual-primary-mode");
                SettingsManager.get(SettingKeys.dualPrimaryCameraMode).setIsSupported(true);
            }

            if (parameters.get("hw-supported-aperture-value") != null)
            {
                SettingsManager.get(SettingKeys.M_Aperture).setKEY("hw-set-aperture-value");
                SettingsManager.get(SettingKeys.M_Aperture).setValues(parameters.get("hw-supported-aperture-value").split(","));
                SettingsManager.get(SettingKeys.M_Aperture).setIsSupported(true);
            }
        }

        appS.SetCurrentCamera(0);
    }

    private void detectAutoHdr(Camera.Parameters parameters) {
        if (SettingsManager.get(SettingKeys.HDRMode).isPresetted())
            return;
        if (parameters.get(camstring(R.string.auto_hdr_supported))!=null){
            SettingsManager.get(SettingKeys.HDRMode).setIsSupported(false);
            return;
        }
        String autohdr = parameters.get(camstring(R.string.auto_hdr_supported));
        if (autohdr != null
                && !TextUtils.isEmpty(autohdr)
                && autohdr.equals(camstring(R.string.true_))
                && parameters.get(camstring(R.string.auto_hdr_enable)) != null) {

            List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(SettingsManager.getInstance().getResString(R.string.scene_mode_values)).split(",")));

            List<String> hdrVals =  new ArrayList<>();
            hdrVals.add(camstring(R.string.off_));

            if (Scenes.contains(camstring(R.string.scene_mode_hdr))) {
                hdrVals.add(camstring(R.string.on_));
            }
            if (Scenes.contains(camstring(R.string.scene_mode_asd))) {
                hdrVals.add(camstring(R.string.auto_));
            }
            SettingsManager.get(SettingKeys.HDRMode).setValues(hdrVals.toArray(new String[hdrVals.size()]));
            SettingsManager.get(SettingKeys.HDRMode).setIsSupported(true);
            SettingsManager.get(SettingKeys.HDRMode).setType(1);
        }
    }

    private void detectPreviewFpsRanges(Camera.Parameters parameters) {
        if (parameters.get(camstring(R.string.preview_fps_range_values))!= null)
        {
            SettingsManager.get(SettingKeys.PreviewFpsRange).setIsSupported(true);
            SettingsManager.get(SettingKeys.PreviewFpsRange).setValues(parameters.get(camstring(R.string.preview_fps_range_values)).split(","));
            SettingsManager.get(SettingKeys.PreviewFpsRange).setKEY(camstring(R.string.preview_fps_range));
            SettingsManager.get(SettingKeys.PreviewFpsRange).set(parameters.get(camstring(R.string.preview_fps_range)));
        }
    }

    private void detectQcomFocus(Camera.Parameters parameters)
    {
        SettingsManager.get(SettingKeys.useQcomFocus).set(parameters.get(camstring(R.string.touch_af_aec))!= null);
    }


    private void detectManualWhiteBalance(Camera.Parameters parameters) {
        if (SettingsManager.get(SettingKeys.M_Whitebalance).isPresetted())
            return;
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
            SettingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(false);
        else if (SettingsManager.get(SettingKeys.M_Whitebalance).isSupported()) // happens when its already set due supportedevices.xml
            return;
        else
        {
            // looks like wb-current-cct is loaded when the preview is up. this could be also for the other parameters
            String wbModeval ="", wbmax = "",wbmin = "";

            if (parameters.get(SettingsManager.getInstance().getResString(R.string.max_wb_cct)) != null) {
                wbmax = SettingsManager.getInstance().getResString(R.string.max_wb_cct);
            }
            else if (parameters.get(SettingsManager.getInstance().getResString(R.string.max_wb_ct))!= null)
                wbmax = SettingsManager.getInstance().getResString(R.string.max_wb_ct);

            if (parameters.get(SettingsManager.getInstance().getResString(R.string.min_wb_cct))!= null) {
                wbmin = SettingsManager.getInstance().getResString(R.string.min_wb_cct);
            } else if (parameters.get(SettingsManager.getInstance().getResString(R.string.min_wb_ct))!= null)
                wbmin = SettingsManager.getInstance().getResString(R.string.min_wb_ct);

            if (arrayContainsString(SettingsManager.get(SettingKeys.WhiteBalanceMode).getValues(), SettingsManager.getInstance().getResString(R.string.manual)))
                wbModeval = SettingsManager.getInstance().getResString(R.string.manual);
            else if (arrayContainsString(SettingsManager.get(SettingKeys.WhiteBalanceMode).getValues(), SettingsManager.getInstance().getResString(R.string.manual_cct)))
                wbModeval = SettingsManager.getInstance().getResString(R.string.manual_cct);

            if (!TextUtils.isEmpty(wbmax) && !TextUtils.isEmpty(wbmin) && !TextUtils.isEmpty(wbModeval)) {
                Log.d(TAG, "Found all wbct values:" +wbmax + " " + wbmin + " " +wbModeval);
                SettingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_Whitebalance).setMode(wbModeval);
                int min = Integer.parseInt(parameters.get(wbmin));
                int max = Integer.parseInt(parameters.get(wbmax));
                SettingsManager.get(SettingKeys.M_Whitebalance).setValues(createWBStringArray(min,max,100));
            }
            else {
                Log.d(TAG, "Failed to lookup wbct:" + " " +wbmax + " " + wbmin + " " +wbModeval);
                SettingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(false);
            }
        }
    }

    public static String[] createWBStringArray(int min, int max, float step)
    {
        Log.d(TAG,"Create Wbvalues");
        ArrayList<String> t = new ArrayList<>();
        t.add(SettingsManager.getInstance().getResString(R.string.auto_));
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

        Log.d(TAG, "Manual Iso Presetted:" + SettingsManager.get(SettingKeys.M_ManualIso).isPresetted());
        if (!SettingsManager.get(SettingKeys.M_ManualIso).isPresetted()) {

            if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK) {
                SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_ManualIso).setKEY("m-sr-g");
                SettingsManager.get(SettingKeys.M_ManualIso).setValues(createIsoValues(100, 1600, 100,false));
                SettingsManager.get(SettingKeys.M_ManualIso).setType(SettingsManager.ISOMANUAL_MTK);
            }
            else {
                if (parameters.get(SettingsManager.getInstance().getResString(R.string.min_iso)) != null && parameters.get(SettingsManager.getInstance().getResString(R.string.max_iso)) != null) {
                    SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(true);

                    int min = Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.min_iso)));
                    int max = Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.max_iso)));
                    if (SettingsManager.getInstance().getFrameWork() == Frameworks.Xiaomi)
                    {
                        SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(false);
                    }
                    else
                    {
                        SettingsManager.get(SettingKeys.M_ManualIso).setKEY(SettingsManager.getInstance().getResString(R.string.continuous_iso));
                        SettingsManager.get(SettingKeys.M_ManualIso).setType(SettingsManager.ISOMANUAL_QCOM);
                        SettingsManager.get(SettingKeys.M_ManualIso).setValues(createIsoValues(min, max, 50,false));
                    }
                }
                else if (parameters.get(SettingsManager.getInstance().getResString(R.string.hw_sensor_iso_range))!= null)
                {
                    SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(true);
                    String t[] = parameters.get(SettingsManager.getInstance().getResString(R.string.hw_sensor_iso_range)).split(",");
                    int min = Integer.parseInt(t[0]);
                    int max = Integer.parseInt(t[1]);
                    SettingsManager.get(SettingKeys.M_ManualIso).setValues(createIsoValues(min, max, 50,false));
                    SettingsManager.get(SettingKeys.M_ManualIso).setType(SettingsManager.ISOMANUAL_KRILLIN);
                    SettingsManager.get(SettingKeys.M_ManualIso).setKEY(SettingsManager.getInstance().getResString(R.string.hw_sensor_iso));
                }
                else if (parameters.get(SettingsManager.getInstance().getResString(R.string.lg_iso)) != null) {
                    SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(true);
                    SettingsManager.get(SettingKeys.M_ManualIso).setValues(createIsoValues(0, 2700, 50,false));
                    SettingsManager.get(SettingKeys.M_ManualIso).setType(SettingsManager.ISOMANUAL_LG);
                    SettingsManager.get(SettingKeys.M_ManualIso).setKEY(SettingsManager.getInstance().getResString(R.string.lg_iso));
                }
            }
        }
    }

    public static String[] createIsoValues(int miniso, int maxiso, int step, boolean xiaomi)
    {
        Log.d(TAG,"Create Isovalues");
        ArrayList<String> s = new ArrayList<>();
        s.add(SettingsManager.getInstance().getResString(R.string.auto_));
        for (int i =miniso; i <= maxiso; i +=step)
        {
            if (xiaomi)
                s.add("ISO"+i);
            else
                s.add(i + "");
        }
        String[] stringvalues = new String[s.size()];
        return s.toArray(stringvalues);
    }

    private void detectManualExposureTime(Camera.Parameters parameters)
    {
        Log.d(TAG, "ManualExposureTime is Presetted: "+ SettingsManager.get(SettingKeys.M_ExposureTime).isPresetted());
        if (SettingsManager.get(SettingKeys.M_ExposureTime).isPresetted())
            return;
        //mtk shutter
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "ManualExposureTime MTK");
            SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(true);
            SettingsManager.get(SettingKeys.M_ExposureTime).setValues(SettingsManager.getInstance().getResources().getStringArray(R.array.mtk_shutter));
            SettingsManager.get(SettingKeys.M_ExposureTime).setKEY("m-ss");
            SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_MTK);
        }
        else
        {
            //htc shutter
            if (parameters.get(SettingsManager.getInstance().getResString(R.string.shutter)) != null) {
                Log.d(TAG, "ManualExposureTime HTC");
                SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_ExposureTime).setValues(SettingsManager.getInstance().getResources().getStringArray(R.array.htc));
                SettingsManager.get(SettingKeys.M_ExposureTime).setKEY(SettingsManager.getInstance().getResString(R.string.shutter));
                SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_HTC);
            }
            //lg shutter
            else if (parameters.get(SettingsManager.getInstance().getResString(R.string.lg_shutterspeed_values)) != null) {
                Log.d(TAG, "ManualExposureTime LG");
                SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_LG);
                ArrayList<String> l = new ArrayList(Arrays.asList(parameters.get(SettingsManager.getInstance().getResString(R.string.lg_shutterspeed_values)).replace(",0", "").split(",")));
                l.remove(0);
                SettingsManager.get(SettingKeys.M_ExposureTime).setValues(l.toArray(new String[l.size()]));
                SettingsManager.get(SettingKeys.M_ExposureTime).setKEY(SettingsManager.getInstance().getResString(R.string.lg_shutterspeed));
                SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(true);
            }
            //meizu shutter
            else if (parameters.get("shutter-value") != null) {
                Log.d(TAG, "ManualExposureTime Meizu");
                SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_ExposureTime).setValues(SettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_meizu));
                SettingsManager.get(SettingKeys.M_ExposureTime).setKEY("shutter-value");
                SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_MEIZU);
            }
            //krillin shutter
            else if (parameters.get("hw-manual-exposure-value") != null) {
                Log.d(TAG, "ManualExposureTime Krilin");
                SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_ExposureTime).setValues(SettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_krillin));
                SettingsManager.get(SettingKeys.M_ExposureTime).setKEY("hw-manual-exposure-value");
                SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_KRILLIN);
            }
            else if (parameters.get("hw-max-exposure-time") != null) {
                Log.d(TAG, "ManualExposureTime huawei");
                SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_ExposureTime).setValues(SettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_krillin));
                SettingsManager.get(SettingKeys.M_ExposureTime).setKEY("hw-sensor-exposure-time");
                SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_KRILLIN);
            }
            //sony shutter
            else if (parameters.get("sony-max-shutter-speed") != null) {
                Log.d(TAG, "ManualExposureTime Sony");
                SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_ExposureTime).setValues(getSupportedShutterValues(
                        Long.parseLong(parameters.get("sony-min-shutter-speed")),
                        Long.parseLong(parameters.get("sony-max-shutter-speed")),
                        true));
                SettingsManager.get(SettingKeys.M_ExposureTime).setKEY("sony-shutter-speed");
                SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_SONY);
            }
            //qcom shutter
            else if (parameters.get(camstring(R.string.max_exposure_time)) != null && parameters.get(camstring(R.string.min_exposure_time)) != null) {
                long min = 0, max = 0;
                if (parameters.get(camstring(R.string.max_exposure_time)).contains(".")) {
                    Log.d(TAG, "ManualExposureTime Qcom Millisec");
                    min = (long) (Double.parseDouble(parameters.get(camstring(R.string.min_exposure_time))) * 1000);
                    max = (long) (Double.parseDouble(parameters.get(camstring(R.string.max_exposure_time))) * 1000);
                    SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_QCOM_MILLISEC);
                } else {
                    Log.d(TAG, "ManualExposureTime Qcom MicroSec");
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_exposure_time)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_exposure_time)));
                    SettingsManager.get(SettingKeys.M_ExposureTime).setType(SettingsManager.SHUTTER_QCOM_MICORSEC);
                }
                if (max > 0) {

                    SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(true);
                    SettingsManager.get(SettingKeys.M_ExposureTime).setKEY(camstring(R.string.exposure_time));
                    SettingsManager.get(SettingKeys.M_ExposureTime).setValues(getSupportedShutterValues(min, max, true));
                }
            }
        }
    }

    private String[] getSupportedShutterValues(long minMillisec, long maxMiliisec, boolean withautomode) {
        String[] allvalues = SettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        if (withautomode)
            tmp.add(SettingsManager.getInstance().getResString(R.string.auto_));
        for (int i = 0; i < allvalues.length; i++) {
            String s = allvalues[i];
            if (!s.equals(SettingsManager.getInstance().getResString(R.string.auto_))) {
                float a;
                if (s.contains("/")) {
                    String[] split = s.split("/");
                    a = (Float.parseFloat(split[0]) / Float.parseFloat(split[1])) * 1000000f;
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
        }
        return tmp.toArray(new String[tmp.size()]);
    }


    private void detectNonZslmanual(Camera.Parameters parameters) {
        if(parameters.get("non-zsl-manual-mode")!=null)
        {
            SettingsManager.get(SettingKeys.NonZslManualMode).setIsSupported(true);
            SettingsManager.get(SettingKeys.NonZslManualMode).setKEY("non-zsl-manual-mode");
            SettingsManager.get(SettingKeys.NonZslManualMode).setValues(new String[]{SettingsManager.getInstance().getResString(R.string.on_), SettingsManager.getInstance().getResString(R.string.off_)});
        }
    }

    private void detectTNR(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.TNR).setIsSupported(false);
            SettingsManager.get(SettingKeys.TNR_V).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.tnr,R.string.tnr_mode, SettingsManager.get(SettingKeys.TNR));
            detectMode(parameters,R.string.tnr_v,R.string.tnr_mode_v, SettingsManager.get(SettingKeys.TNR_V));
        }

    }

    private void detectPDAF(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.PDAF).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.pdaf,R.string.pdaf_mode, SettingsManager.get(SettingKeys.PDAF));
        }

    }

    private void detectSEEMoar(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.SeeMore).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.seemore,R.string.seemore_mode, SettingsManager.get(SettingKeys.SeeMore));
        }

    }

    private void detectRefocus(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.ReFocus).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.refocus,R.string.refocus_mode, SettingsManager.get(SettingKeys.ReFocus));
        }

    }

    private void detectRDI(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.RDI).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.rdi,R.string.rdi_mode, SettingsManager.get(SettingKeys.RDI));
        }

    }

    private void detectOptizoom(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.OptiZoom).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.optizoom,R.string.optizoom_mode, SettingsManager.get(SettingKeys.OptiZoom));
        }

    }

    private void detectChromaFlash(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.ChromaFlash).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.chroma,R.string.chroma_mode, SettingsManager.get(SettingKeys.ChromaFlash));
        }

    }

    private void detectTruePotrait(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.TruePotrait).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.truepotrait,R.string.truepotrait_mode, SettingsManager.get(SettingKeys.TruePotrait));
        }

    }

    private void detectDenoise(Camera.Parameters parameters)
    {
        Log.d(TAG, "Denoise is Presetted: "+ SettingsManager.get(SettingKeys.Denoise).isPresetted());
        if (SettingsManager.get(SettingKeys.Denoise).isPresetted())
            return;
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            if(parameters.get(camstring(R.string.mtk_3dnr_mode))!=null) {
                if (parameters.get(camstring(R.string.mtk_3dnr_mode_values)).equals("on,off")) {
                    SettingsManager.get(SettingKeys.Denoise).setIsSupported(true);
                    SettingsManager.get(SettingKeys.Denoise).setKEY(camstring(R.string.mtk_3dnr_mode));
                    SettingsManager.get(SettingKeys.Denoise).setValues(parameters.get(camstring(R.string.mtk_3dnr_mode_values)).split(","));
                }
            }
        }
        else
        {
            detectMode(parameters,R.string.denoise,R.string.denoise_values, SettingsManager.get(SettingKeys.Denoise));
        }
    }

    private void detectDisModes(Camera.Parameters parameters) {
        if (SettingsManager.get(SettingKeys.DigitalImageStabilization).isPresetted())
            return;
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK) {
            SettingsManager.get(SettingKeys.DigitalImageStabilization).setIsSupported(false);
        } else{
            detectMode(parameters,R.string.dis,R.string.dis_values, SettingsManager.get(SettingKeys.DigitalImageStabilization));
        }
    }

    private void detectManualSaturation(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "Saturation: MTK");
            if (parameters.get(camstring(R.string.saturation))!= null && parameters.get(camstring(R.string.saturation_values))!= null) {
                SettingsManager.get(SettingKeys.M_Saturation).setValues(parameters.get(camstring(R.string.saturation_values)).split(","));
                SettingsManager.get(SettingKeys.M_Saturation).setKEY(camstring(R.string.saturation));
                SettingsManager.get(SettingKeys.M_Saturation).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_Saturation).set(parameters.get(camstring(R.string.saturation)));
            }
        }
        else {
            try {
                int min = 0, max = 0;
                if (parameters.get(SettingsManager.getInstance().getResString(R.string.lg_color_adjust_max)) != null
                        && parameters.get(SettingsManager.getInstance().getResString(R.string.lg_color_adjust_min)) != null) {
                    Log.d(TAG, "Saturation: LG");
                    min = Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.lg_color_adjust_min)));
                    max = Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.lg_color_adjust_max)));
                    SettingsManager.get(SettingKeys.M_Saturation).setKEY(SettingsManager.getInstance().getResString(R.string.lg_color_adjust));
                    SettingsManager.get(SettingKeys.M_Saturation).set(parameters.get(camstring(R.string.lg_color_adjust)));
                } else if (parameters.get(camstring(R.string.saturation_max)) != null) {
                    Log.d(TAG, "Saturation: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.saturation_min)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.saturation_max)));
                    SettingsManager.get(SettingKeys.M_Saturation).setKEY(camstring(R.string.saturation));
                    SettingsManager.get(SettingKeys.M_Saturation).set(parameters.get(camstring(R.string.saturation)));
                } else if (parameters.get(camstring(R.string.max_saturation)) != null && parameters.get(camstring(R.string.min_saturation)) != null) {
                    Log.d(TAG, "Saturation: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_saturation)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_saturation)));
                    SettingsManager.get(SettingKeys.M_Saturation).setKEY(camstring(R.string.saturation));
                    SettingsManager.get(SettingKeys.M_Saturation).set(parameters.get(camstring(R.string.saturation)));
                }
                Log.d(TAG, "Saturation Max:" + max);
                if (max > 0) {
                    SettingsManager.get(SettingKeys.M_Saturation).setValues(createStringArray(min, max, 1));
                    SettingsManager.get(SettingKeys.M_Saturation).setIsSupported(true);
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.M_Saturation).setIsSupported(false);
            }
        }
    }

    private void detectManualSharpness(Camera.Parameters parameters) {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "Sharpness: MTK");
            if (parameters.get(camstring(R.string.edge))!= null && parameters.get(camstring(R.string.edge_values))!= null) {
                SettingsManager.get(SettingKeys.M_Sharpness).setValues(parameters.get(camstring(R.string.edge_values)).split(","));
                SettingsManager.get(SettingKeys.M_Sharpness).setKEY(camstring(R.string.edge));
                SettingsManager.get(SettingKeys.M_Sharpness).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_Sharpness).set(parameters.get(camstring(R.string.edge)));
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(camstring(R.string.sharpness_max)) != null) {
                Log.d(TAG, "Sharpness: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.sharpness_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.sharpness_max)));
                SettingsManager.get(SettingKeys.M_Sharpness).setKEY(camstring(R.string.sharpness));
                SettingsManager.get(SettingKeys.M_Sharpness).set(parameters.get(camstring(R.string.sharpness)));
            } else if (parameters.get(camstring(R.string.max_sharpness)) != null) {
                Log.d(TAG, "Sharpness: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.min_sharpness)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_sharpness)));
                SettingsManager.get(SettingKeys.M_Sharpness).setKEY(camstring(R.string.sharpness));
                SettingsManager.get(SettingKeys.M_Sharpness).set(parameters.get(camstring(R.string.sharpness)));
            }
            Log.d(TAG, "Sharpness Max:" +max);
            if (max > 0) {
                SettingsManager.get(SettingKeys.M_Sharpness).setValues(createStringArray(min, max, 1));
                SettingsManager.get(SettingKeys.M_Sharpness).setIsSupported(true);
            }
        }
    }

    private void detectManualBrightness(Camera.Parameters parameters) {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "Brightness: MTK");
            if (parameters.get(camstring(R.string.brightness))!= null && parameters.get(camstring(R.string.brightness_values))!= null) {
                SettingsManager.get(SettingKeys.M_Brightness).setValues(parameters.get(camstring(R.string.brightness_values)).split(","));
                SettingsManager.get(SettingKeys.M_Brightness).setKEY(camstring(R.string.brightness));
                SettingsManager.get(SettingKeys.M_Brightness).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_Brightness).set(parameters.get(camstring(R.string.brightness)));
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(camstring(R.string.brightness_max)) != null && parameters.get(camstring(R.string.brightness_min)) != null) {
                Log.d(TAG, "Brightness: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.brightness_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.brightness_max)));
            } else if (parameters.get(camstring(R.string.max_brightness)) != null && parameters.get(camstring(R.string.min_brightness)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.min_brightness)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_brightness)));
                Log.d(TAG, "Brightness: Default");
            }
            Log.d(TAG, "Brightness Max:" +max);
            if (max > 0) {
                if (parameters.get(camstring(R.string.brightness))!= null)
                    SettingsManager.get(SettingKeys.M_Brightness).setKEY(camstring(R.string.brightness));
                else if (parameters.get(camstring(R.string.luma_adaptation))!= null)
                    SettingsManager.get(SettingKeys.M_Brightness).setKEY(camstring(R.string.luma_adaptation));
                SettingsManager.get(SettingKeys.M_Brightness).setValues(createStringArray(min, max, 1));
                SettingsManager.get(SettingKeys.M_Brightness).set(parameters.get(SettingsManager.get(SettingKeys.M_Brightness).getKEY()));
                SettingsManager.get(SettingKeys.M_Brightness).setIsSupported(true);
            }
        }
    }

    private void detectManualContrast(Camera.Parameters parameters) {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            if (parameters.get(camstring(R.string.contrast))!= null && parameters.get(camstring(R.string.contrast_values))!= null) {
                SettingsManager.get(SettingKeys.M_Contrast).setValues(parameters.get(camstring(R.string.contrast_values)).split(","));
                SettingsManager.get(SettingKeys.M_Contrast).setKEY(camstring(R.string.contrast));
                SettingsManager.get(SettingKeys.M_Contrast).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_Contrast).set(parameters.get(camstring(R.string.contrast)));
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
            Log.d(TAG, "Contrast Max:" +max);
            if (max > 0) {
                SettingsManager.get(SettingKeys.M_Contrast).setKEY(camstring(R.string.contrast));
                SettingsManager.get(SettingKeys.M_Contrast).setValues(createStringArray(min, max, 1));
                SettingsManager.get(SettingKeys.M_Contrast).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_Contrast).set(parameters.get(camstring(R.string.contrast)));
            }
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
        Log.d(TAG, "mf is preseted:" + SettingsManager.get(SettingKeys.M_Focus).isPresetted());
        if (SettingsManager.get(SettingKeys.M_Focus).isPresetted())
            return;

        int min =0, max =0, step = 0;
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.M_Focus).setMode(camstring(R.string.manual));
            SettingsManager.get(SettingKeys.M_Focus).setType(-1);
            SettingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
            min = 0;
            max = 1023;
            step = 10;
            SettingsManager.get(SettingKeys.M_Focus).setKEY(SettingsManager.getInstance().getResString(R.string.afeng_pos));
            Log.d(TAG, "MF MTK");
        }
        else {
            //lookup old qcom

            if (parameters.get(camstring(R.string.manual_focus_modes)) == null) {

                if (parameters.get(camstring(R.string.max_focus_pos_index)) != null
                        && parameters.get(camstring(R.string.min_focus_pos_index))!= null
                        && SettingsManager.get(SettingKeys.FocusMode).contains(camstring(R.string.manual))) {

                    SettingsManager.get(SettingKeys.M_Focus).setMode(camstring(R.string.manual));
                    SettingsManager.get(SettingKeys.M_Focus).setType(1);
                    SettingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_focus_pos_index)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_focus_pos_index)));
                    step = 10;
                    SettingsManager.get(SettingKeys.M_Focus).setKEY(camstring(R.string.manual_focus_position));
                    Log.d(TAG, "MF old qcom");
                }
            }
            else
            {
                //lookup new qcom
                if (parameters.get(camstring(R.string.max_focus_pos_ratio)) != null
                        && parameters.get(camstring(R.string.min_focus_pos_ratio)) != null
                        && SettingsManager.get(SettingKeys.FocusMode).contains(camstring(R.string.manual))) {

                    SettingsManager.get(SettingKeys.M_Focus).setMode(camstring(R.string.manual));
                    SettingsManager.get(SettingKeys.M_Focus).setType(2);
                    SettingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_focus_pos_ratio)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_focus_pos_ratio)));
                    step = 1;
                    SettingsManager.get(SettingKeys.M_Focus).setKEY(camstring(R.string.manual_focus_position));
                    Log.d(TAG, "MF new qcom");
                }
            }
            //htc mf
            if (parameters.get(camstring(R.string.min_focus)) != null && parameters.get(camstring(R.string.max_focus)) != null)
            {
                SettingsManager.get(SettingKeys.M_Focus).setMode("");
                SettingsManager.get(SettingKeys.M_Focus).setType(-1);
                SettingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
                min = Integer.parseInt(parameters.get(camstring(R.string.min_focus)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_focus)));
                step = 1;
                SettingsManager.get(SettingKeys.M_Focus).setKEY(camstring(R.string.focus));
                Log.d(TAG, "MF HTC");
            }

            //huawai mf
            if(parameters.get(SettingsManager.getInstance().getResString(R.string.hw_vcm_end_value)) != null && parameters.get(SettingsManager.getInstance().getResString(R.string.hw_vcm_start_value)) != null)
            {
                Log.d(TAG,"Huawei MF");
                SettingsManager.get(SettingKeys.M_Focus).setMode(camstring(R.string.manual));
                SettingsManager.get(SettingKeys.M_Focus).setType(-1);
                SettingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
                max = Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.hw_vcm_end_value)));
                min = Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.hw_vcm_start_value)));
                Log.d(TAG,"min/max mf:" + min+"/"+max);
                step = 10;
                SettingsManager.get(SettingKeys.M_Focus).setKEY(SettingsManager.getInstance().getResString(R.string.hw_manual_focus_step_value));
            }
        }
        //create mf values
        if (SettingsManager.get(SettingKeys.M_Focus).isSupported())
            SettingsManager.get(SettingKeys.M_Focus).setValues(createManualFocusValues(min, max,step));
    }

    public static String[] createManualFocusValues(int min, int max, int step)
    {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(SettingsManager.getInstance().getResString(R.string.auto_));

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
            SettingsManager.getInstance().setIsFrontCamera(false);
        else
            SettingsManager.getInstance().setIsFrontCamera(true);
    }


    private boolean canOpenLegacy()
    {
        try {
            Class[] arrclass = {Integer.TYPE, Integer.TYPE};
            Method method = Class.forName("android.hardware.Camera").getDeclaredMethod("openLegacy", arrclass);
            return method != null;
        }
        catch
                (NoSuchMethodException e) {
            Log.WriteEx(e);
            return false;
        } catch (ClassNotFoundException e) {
            Log.WriteEx(e);
            return false;
        }
    }

    private Camera.Parameters getParameters(int currentcamera)
    {
        Camera camera = null;
        switch (SettingsManager.getInstance().getFrameWork())
        {
            case LG:
            {
                Log.d(TAG,"Open LG Camera");
                LGCameraRef lgCamera;
                if (SettingsManager.get(SettingKeys.openCamera1Legacy).get())
                    lgCamera = new LGCameraRef(currentcamera, 256);
                else
                    lgCamera = new LGCameraRef(currentcamera);
                return lgCamera.getParameters();
            }
            case Moto_Ext:
            {
                Log.d(TAG,"Open MOTO Camera");
                camera  = Camera.open(currentcamera);
                Camera.Parameters parameters = camera.getParameters();
                parameters.set("mot-app", "true");
                camera.setParameters(parameters);
                parameters = camera.getParameters();
                camera.release();
                return parameters;
            }
            case MTK:
            {
                Log.d(TAG,"Open MTK Camera");
                CameraHolderMTK.setMtkAppMode();
                camera = Camera.open(currentcamera);
                Camera.Parameters parameters = camera.getParameters();
                camera.release();
                return parameters;
            }
            default:
            {
               // if (AppSettingsManager.get(Settings.openCamera1Legacy).getApiBoolean())
               // {
                    Log.d(TAG,"Open Try legacy Camera");
                    try {
                        camera = CameraHolderLegacy.openWrapper(currentcamera);
                        Camera.Parameters parameters = camera.getParameters();
                        camera.release();
                        return parameters;
                    }
                    catch (NullPointerException ex)
                    {
                        if (camera != null)
                            camera.release();
                        Log.d(TAG,"Failes to open Legacy");
                        camera = Camera.open(currentcamera);
                        Camera.Parameters parameters = camera.getParameters();
                        camera.release();
                        return parameters;
                    }
                    catch(RuntimeException ex2)
                {
                    if (camera != null)
                        camera.release();
                    Log.d(TAG,"Failes to open Legacy");
                    camera = Camera.open(currentcamera);
                    Camera.Parameters parameters = camera.getParameters();
                    camera.release();
                    return parameters;
                }

            }

        }
    }

    private void detectMode(Camera.Parameters parameters, int key, int keyvalues, SettingMode mode)
    {
        if (parameters.get(camstring(keyvalues)) == null || parameters.get(camstring(key)) == null)
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
        if (false)
        {
            SettingsManager.get(SettingKeys.PictureFormat).setIsSupported(false);
            SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(false);
        }
        else {
            if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK) {
                SettingsManager.get(SettingKeys.PictureFormat).setIsSupported(true);
                SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
            } else {

                String formats = parameters.get(camstring(R.string.picture_format_values));

                if (!SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isPresetted()) {
                    Log.d(TAG, "rawpictureformat is not preseted try to find it");
                    if (formats.contains("bayer-mipi") || formats.contains("raw")) {
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
                        String[] forms = formats.split(",");
                        for (String s : forms) {
                            if (s.contains("bayer-mipi") || s.contains("raw")) {
                                Log.d(TAG, "rawpictureformat set to:" +s);
                                SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).set(s);
                                SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    if (!formats.contains(SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).get()))
                    {
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).set(SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).get());
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
                    }


                }
                if (formats.contains(SettingsManager.getInstance().getResString(R.string.bayer_)))
                {
                    Log.d(TAG, "create rawformats");
                    ArrayList<String> tmp = new ArrayList<>();
                    String[] forms = formats.split(",");
                    for (String s : forms) {
                        if (s.contains(SettingsManager.getInstance().getResString(R.string.bayer_)))
                        {
                            tmp.add(s);
                        }
                    }
                    String[] rawFormats = new String[tmp.size()];
                    tmp.toArray(rawFormats);
                    SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setValues(rawFormats);
                    if (rawFormats.length == 0)
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(false);
                    else
                        SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
                }
            }
            SettingsManager.get(SettingKeys.PictureFormat).setIsSupported(true);

            if (SettingsManager.getInstance().getDngProfilesMap() != null && SettingsManager.getInstance().getDngProfilesMap().size() > 0)
            {
                Log.d(TAG, "Dng, bayer, jpeg supported");
                SettingsManager.get(SettingKeys.PictureFormat).setValues(new String[]
                        {
                                SettingsManager.getInstance().getResString(R.string.jpeg_),
                                SettingsManager.getInstance().getResString(R.string.dng_),
                                SettingsManager.getInstance().getResString(R.string.bayer_)
                        });
            }
            else if (SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isSupported()) {
                Log.d(TAG, "bayer, jpeg supported");
                SettingsManager.get(SettingKeys.PictureFormat).setValues(new String[]{
                        SettingsManager.getInstance().getResString(R.string.jpeg_),
                        SettingsManager.getInstance().getResString(R.string.bayer_)
                });
            }
            else
            {
                Log.d(TAG, "jpeg supported");
                SettingsManager.get(SettingKeys.PictureFormat).setValues(new String[]{
                        SettingsManager.getInstance().getResString(R.string.jpeg_)
                });
            }

        }
    }

    private void detectPictureSizes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.picture_size,R.string.picture_size_values, SettingsManager.get(SettingKeys.PictureSize));
    }

    private void detectFocusModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.focus_mode,R.string.focus_mode_values, SettingsManager.get(SettingKeys.FocusMode));
    }

    private void detectWhiteBalanceModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.whitebalance,R.string.whitebalance_values, SettingsManager.get(SettingKeys.WhiteBalanceMode));
    }

    private void detectExposureModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.exposure))!= null) {
            detectMode(parameters,R.string.exposure,R.string.exposure_mode_values, SettingsManager.get(SettingKeys.ExposureMode));
        }
        else if (parameters.get(camstring(R.string.auto_exposure_values))!= null) {
            detectMode(parameters,R.string.auto_exposure,R.string.auto_exposure_values, SettingsManager.get(SettingKeys.ExposureMode));
        }
        else if(parameters.get(camstring(R.string.sony_metering_mode))!= null) {
            detectMode(parameters,R.string.sony_metering_mode,R.string.sony_metering_mode_values, SettingsManager.get(SettingKeys.ExposureMode));
        }
        else if(parameters.get(camstring(R.string.exposure_meter))!= null) {
            detectMode(parameters,R.string.exposure_meter,R.string.exposure_meter_values, SettingsManager.get(SettingKeys.ExposureMode));
        }
        else if (parameters.get(camstring(R.string.hw_exposure_mode_values)) != null)
            detectMode(parameters, R.string.hw_exposure_mode,R.string.hw_exposure_mode_values, SettingsManager.get(SettingKeys.ExposureMode));
        if (!TextUtils.isEmpty(SettingsManager.get(SettingKeys.ExposureMode).getKEY()))
            SettingsManager.get(SettingKeys.ExposureMode).setIsSupported(true);
        else
            SettingsManager.get(SettingKeys.ExposureMode).setIsSupported(false);
    }

    private void detectColorModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.effect,R.string.effect_values, SettingsManager.get(SettingKeys.ColorMode));
    }

    private void detectFlashModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.flash_mode,R.string.flash_mode_values, SettingsManager.get(SettingKeys.FlashMode));
    }

    private void detectIsoModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.iso_mode_values))!= null){
            detectMode(parameters,R.string.iso,R.string.iso_mode_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        else if (parameters.get(camstring(R.string.iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.iso_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        else if (parameters.get(camstring(R.string.iso_speed_values))!= null) {
            detectMode(parameters,R.string.iso_speed,R.string.iso_speed_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        else if (parameters.get(camstring(R.string.sony_iso_values))!= null) {
            detectMode(parameters,R.string.sony_iso,R.string.sony_iso_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        else if (parameters.get(camstring(R.string.lg_iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.lg_iso_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        if (SettingsManager.get(SettingKeys.IsoMode).getValues().length >1)
            SettingsManager.get(SettingKeys.IsoMode).setIsSupported(true);
        else
            SettingsManager.get(SettingKeys.IsoMode).setIsSupported(false);
    }

    private void detectAntiBandingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.antibanding,R.string.antibanding_values, SettingsManager.get(SettingKeys.AntiBandingMode));
    }

    private void detectImagePostProcessingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.ipp,R.string.ipp_values, SettingsManager.get(SettingKeys.ImagePostProcessing));
    }

    private void detectPreviewSizeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_size,R.string.preview_size_values, SettingsManager.get(SettingKeys.PreviewSize));
    }

    private void detectJpeqQualityModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.jpeg_quality)) == null)
        {
            SettingsManager.get(SettingKeys.JpegQuality).setIsSupported(false);
            return;
        }
        String[] valuetoreturn = new String[20];
        for (int i = 1; i < 21; i++)
        {
            valuetoreturn[i-1] = "" + i*5;
        }
        SettingsManager.get(SettingKeys.JpegQuality).setValues(valuetoreturn);
        SettingsManager.get(SettingKeys.JpegQuality).set(parameters.get(camstring(R.string.jpeg_quality)));
        SettingsManager.get(SettingKeys.JpegQuality).setKEY(camstring(R.string.jpeg_quality));
        if (valuetoreturn.length >0)
            SettingsManager.get(SettingKeys.JpegQuality).setIsSupported(true);
    }



    private void detectAeBracketModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.ae_bracket_hdr,R.string.ae_bracket_hdr_values, SettingsManager.get(SettingKeys.AE_Bracket));
    }

    private void detectPreviewFPSModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_frame_rate,R.string.preview_frame_rate_values, SettingsManager.get(SettingKeys.PreviewFPS));
    }

    private void detectPreviewFormatModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_format,R.string.preview_format_values, SettingsManager.get(SettingKeys.PreviewFormat));
    }

    private void detectSceneModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.scene_mode,R.string.scene_mode_values, SettingsManager.get(SettingKeys.SceneMode));
    }

    private void detectLensShadeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.lensshade,R.string.lensshade_values, SettingsManager.get(SettingKeys.LensShade));
    }

    private void detectZeroShutterLagModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.zsl_values)) != null)
        {
            detectMode(parameters,R.string.zsl,R.string.zsl_values, SettingsManager.get(SettingKeys.ZSL));

        }
        else if (parameters.get(camstring(R.string.mode_values)) != null)
        {
            detectMode(parameters,R.string.mode,R.string.mode_values, SettingsManager.get(SettingKeys.ZSL));
        }
        else if (parameters.get(camstring(R.string.zsd_mode)) != null) {
            detectMode(parameters, R.string.zsd_mode, R.string.zsd_mode_values, SettingsManager.get(SettingKeys.ZSL));
        }

        if (SettingsManager.get(SettingKeys.ZSL).getValues().length == 0)
            SettingsManager.get(SettingKeys.ZSL).setIsSupported(false);
    }

    private void detectSceneDetectModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.scene_mode,R.string.scene_mode_values, SettingsManager.get(SettingKeys.SceneMode));
    }

    private void detectMemoryColorEnhancementModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.mce,R.string.mce_values, SettingsManager.get(SettingKeys.MemoryColorEnhancement));
    }

    private void detectVideoSizeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.video_size,R.string.video_size_values, SettingsManager.get(SettingKeys.VideoSize));
    }

    private void detectCorrelatedDoubleSamplingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.cds_mode,R.string.cds_mode_values, SettingsManager.get(SettingKeys.CDS_Mode));
    }

    private void detectVideoHdr(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.video_hdr_values)) != null)
        {
            detectMode(parameters,R.string.video_hdr,R.string.video_hdr_values, SettingsManager.get(SettingKeys.VideoHDR));
        }
        else if (parameters.get(camstring(R.string.sony_video_hdr_values))!= null) {
            detectMode(parameters,R.string.sony_video_hdr,R.string.sony_video_hdr_values, SettingsManager.get(SettingKeys.VideoHDR));
        }
        else
            SettingsManager.get(SettingKeys.VideoHDR).setIsSupported(false);
    }

    private void detectVideoHFR(Camera.Parameters parameters)
    {
        if (parameters.get("video-hfr") != null)
        {
            String hfrvals = parameters.get("video-hfr-values");
            if (!hfrvals.equals("off"))
            {
                if (TextUtils.isEmpty(hfrvals)) {
                    SettingsManager.get(SettingKeys.VideoHighFramerate).setValues("off,60,120".split(","));
                    SettingsManager.get(SettingKeys.VideoHighFramerate).setKEY("video-hfr");
                    SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(true);
                    SettingsManager.get(SettingKeys.VideoHighFramerate).set(parameters.get("video-hfr"));
                }
                else
                    SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(false);
            }
        }
        else if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            if (parameters.get("hsvr-prv-fps-values") != null)
            {
                SettingsManager.get(SettingKeys.VideoHighFramerate).setValues(parameters.get("hsvr-prv-fps-values").split(","));
                SettingsManager.get(SettingKeys.VideoHighFramerate).setKEY("hsvr-prv-fps");
                SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(true);
                SettingsManager.get(SettingKeys.VideoHighFramerate).set(parameters.get("hsvr-prv-fps"));
            }
            else
                SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(false);
        }
        else
        {
            /*switch (AppSettingsManager.getInstance().getDevice())
            {
                case Htc_M8:
                case Htc_M9:
                case HTC_OneA9:
                case HTC_OneE8:
                    AppSettingsManager.get(Settings.VideoHighFramerate).setValues("off,60,120".split(","));
                    AppSettingsManager.get(Settings.VideoHighFramerate).setKEY("video-mode");
                    AppSettingsManager.get(Settings.VideoHighFramerate).setIsSupported(true);
                    AppSettingsManager.get(Settings.VideoHighFramerate).set(parameters.get("video-mode"));
                    break;
                default:
                    AppSettingsManager.get(Settings.VideoHighFramerate).setIsSupported(false);
                    break;
            }*/

        }
    }

    private void detectVideoMediaProfiles(int cameraid)
    {
        final String _720phfr = "720HFR";
        final String _2160p = "2160p";
        final String _2160pDCI = "2160pDCI";
        HashMap<String,VideoMediaProfile> supportedProfiles;
        if(SettingsManager.getInstance().getFrameWork() == Frameworks.LG)
            supportedProfiles =  getLGVideoMediaProfiles(cameraid);
        else
            supportedProfiles= getDefaultVideoMediaProfiles(cameraid);

        if (supportedProfiles.get(_720phfr) == null && SettingsManager.get(SettingKeys.VideoHighFramerate).isSupported() && SettingsManager.get(SettingKeys.VideoHighFramerate).contains("120"))
        {
            try {
                Log.d(TAG, "no 720phfr profile found, but hfr supported, try to add custom 720phfr");
                VideoMediaProfile t = supportedProfiles.get("720p").clone();
                t.videoFrameRate = 120;
                t.Mode = VideoMediaProfile.VideoMode.Highspeed;
                t.ProfileName = "720pHFR";
                supportedProfiles.put("720pHFR",t);
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }

        }
        if (SettingsManager.get(SettingKeys.VideoSize).isSupported() && SettingsManager.get(SettingKeys.VideoSize).contains("3840x2160")
                && SettingsManager.get(SettingKeys.VideoHighFramerate).isSupported()&& SettingsManager.get(SettingKeys.VideoHighFramerate).contains("60")) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
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
        if (supportedProfiles.get(_2160p) == null && SettingsManager.get(SettingKeys.VideoSize).isSupported()&& SettingsManager.get(SettingKeys.VideoSize).contains("3840x2160"))
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

        if (SettingsManager.get(SettingKeys.VideoSize).isSupported() && SettingsManager.get(SettingKeys.VideoSize).contains("1920x1080")
                && SettingsManager.get(SettingKeys.VideoHighFramerate).isSupported()&& SettingsManager.get(SettingKeys.VideoHighFramerate).contains("60")) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
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
        SettingsManager.getInstance().saveMediaProfiles(supportedProfiles);
        SettingsManager.get(SettingKeys.VideoProfiles).set("720p");

        publishProgress("VideoMediaProfiles:" + getStringFromArray(supportedProfiles.keySet().toArray(new String[supportedProfiles.size()])));
    }
}
