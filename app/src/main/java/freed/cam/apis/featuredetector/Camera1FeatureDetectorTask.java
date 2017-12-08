package freed.cam.apis.featuredetector;

import android.hardware.Camera;
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
import freed.settings.AppSettingsManager;
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
        return AppSettingsManager.getInstance().getResString(id);
    }

    public void detect()
    {
        publishProgress("###################");
        publishProgress("#######Camera1#####");
        publishProgress("###################");
        AppSettingsManager.getInstance().setCamApi(AppSettingsManager.API_1);

        publishProgress("Device:"+AppSettingsManager.getInstance().getDeviceString());
        //detect frameworks
        AppSettingsManager.getInstance().setFramework(getFramework());
        publishProgress("FrameWork:"+AppSettingsManager.getInstance().getFrameWork());


        int cameraCounts = Camera.getNumberOfCameras();
        Log.d(TAG, "Cameras Found: " + cameraCounts);
        AppSettingsManager appS = AppSettingsManager.getInstance();
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

            AppSettingsManager.getInstance().selfTimer.setValues(appS.getResources().getStringArray(R.array.selftimervalues));
            AppSettingsManager.getInstance().selfTimer.set(appS.selfTimer.getValues()[0]);

            AppSettingsManager.getInstance().guide.setValues(appS.getResources().getStringArray(R.array.guidelist));
            AppSettingsManager.getInstance().guide.set(appS.guide.getValues()[0]);

            detectedPictureFormats(parameters);
            publishProgress("DngSupported:" + (appS.getDngProfilesMap() != null && appS.getDngProfilesMap().size() > 0) + " RawSupport:"+appS.rawPictureFormat.isSupported());
            publishProgress("PictureFormats:" + getStringFromArray(appS.pictureFormat.getValues()));
            publishProgress("RawFormats:" + getStringFromArray(appS.rawPictureFormat.getValues()));
            publishProgress(" RawFormat:" + appS.rawPictureFormat.get());

            AppSettingsManager.getInstance().modules.set(appS.getResString(R.string.module_picture));

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

            detectDisModes(parameters);
            sendProgress(appS.digitalImageStabilisationMode, "DigitalImageStabilisation");

            detectDenoise(parameters);
            sendProgress(appS.denoiseMode, "Denoise");

            detectTNR(parameters);
            sendProgress(appS.temporal_nr, "Temporoal_NR");
            sendProgress(appS.temporal_video_nr, "Temporoal_VIDEO_NR");

            detectPDAF(parameters);
            sendProgress(appS.pdafcontrol, "PDAF");

            detectSEEMoar(parameters);
            sendProgress(appS.seemore_tonemap, "StillMoreToneMap");

            detectTruePotrait(parameters);
            sendProgress(appS.truepotrait, "TruePotrait");

            detectRefocus(parameters);
            sendProgress(appS.refocus, "ReFocus");

            detectOptizoom(parameters);
            sendProgress(appS.optizoom, "OptiZoom");

            detectChromaFlash(parameters);
            sendProgress(appS.chromaflash, "ChromaFlash");

            detectRDI(parameters);
            sendProgress(appS.rawdumpinterface, "RDI");


            detectNonZslmanual(parameters);
            sendProgress(appS.nonZslManualMode, "NonZslManual");

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
            sendProgress(appS.manualWhiteBalance,"Manual Wb");

            detectQcomFocus(parameters);

            detectAutoHdr(parameters);

            if (parameters.get("hw-dual-primary-supported") != null)
            {
                AppSettingsManager.getInstance().dualPrimaryCameraMode.setValues(parameters.get("hw-dual-primary-supported").split(","));
                AppSettingsManager.getInstance().dualPrimaryCameraMode.setKEY("hw-dual-primary-mode");
                AppSettingsManager.getInstance().dualPrimaryCameraMode.setIsSupported(true);
            }

            if (parameters.get("hw-supported-aperture-value") != null)
            {
                AppSettingsManager.getInstance().manualAperture.setKEY("hw-set-aperture-value");
                AppSettingsManager.getInstance().manualAperture.setValues(parameters.get("hw-supported-aperture-value").split(","));
                AppSettingsManager.getInstance().manualAperture.setIsSupported(true);
            }
        }

        appS.SetCurrentCamera(0);
    }

    private void detectAutoHdr(Camera.Parameters parameters) {
        if (AppSettingsManager.getInstance().hdrMode.isPresetted())
            return;
        if (parameters.get(camstring(R.string.auto_hdr_supported))!=null){
            AppSettingsManager.getInstance().hdrMode.setIsSupported(false);
            return;
        }
        String autohdr = parameters.get(camstring(R.string.auto_hdr_supported));
        if (autohdr != null
                && !TextUtils.isEmpty(autohdr)
                && autohdr.equals(camstring(R.string.true_))
                && parameters.get(camstring(R.string.auto_hdr_enable)) != null) {

            List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(AppSettingsManager.getInstance().getResString(R.string.scene_mode_values)).split(",")));

            List<String> hdrVals =  new ArrayList<>();
            hdrVals.add(camstring(R.string.off_));

            if (Scenes.contains(camstring(R.string.scene_mode_hdr))) {
                hdrVals.add(camstring(R.string.on_));
            }
            if (Scenes.contains(camstring(R.string.scene_mode_asd))) {
                hdrVals.add(camstring(R.string.auto_));
            }
            AppSettingsManager.getInstance().hdrMode.setValues(hdrVals.toArray(new String[hdrVals.size()]));
            AppSettingsManager.getInstance().hdrMode.setIsSupported(true);
            AppSettingsManager.getInstance().hdrMode.setType(1);
        }
    }

    private void detectPreviewFpsRanges(Camera.Parameters parameters) {
        if (parameters.get(camstring(R.string.preview_fps_range_values))!= null)
        {
            AppSettingsManager.getInstance().previewFpsRange.setIsSupported(true);
            AppSettingsManager.getInstance().previewFpsRange.setValues(parameters.get(camstring(R.string.preview_fps_range_values)).split(","));
            AppSettingsManager.getInstance().previewFpsRange.setKEY(camstring(R.string.preview_fps_range));
            AppSettingsManager.getInstance().previewFpsRange.set(parameters.get(camstring(R.string.preview_fps_range)));
        }
    }

    private void detectQcomFocus(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.touch_af_aec))!= null)
            AppSettingsManager.getInstance().qcomAFocus.setBoolean(true);
        else
            AppSettingsManager.getInstance().qcomAFocus.setBoolean(false);
    }


    private void detectManualWhiteBalance(Camera.Parameters parameters) {
        if (AppSettingsManager.getInstance().manualWhiteBalance.isPresetted())
            return;
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
            AppSettingsManager.getInstance().manualWhiteBalance.setIsSupported(false);
        else if (AppSettingsManager.getInstance().manualWhiteBalance.isSupported()) // happens when its already set due supportedevices.xml
            return;
        else
        {
            // looks like wb-current-cct is loaded when the preview is up. this could be also for the other parameters
            String wbModeval ="", wbmax = "",wbmin = "";

            if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.max_wb_cct)) != null) {
                wbmax = AppSettingsManager.getInstance().getResString(R.string.max_wb_cct);
            }
            else if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.max_wb_ct))!= null)
                wbmax =AppSettingsManager.getInstance().getResString(R.string.max_wb_ct);

            if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.min_wb_cct))!= null) {
                wbmin =AppSettingsManager.getInstance().getResString(R.string.min_wb_cct);
            } else if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.min_wb_ct))!= null)
                wbmin =AppSettingsManager.getInstance().getResString(R.string.min_wb_ct);

            if (arrayContainsString(AppSettingsManager.getInstance().whiteBalanceMode.getValues(), AppSettingsManager.getInstance().getResString(R.string.manual)))
                wbModeval = AppSettingsManager.getInstance().getResString(R.string.manual);
            else if (arrayContainsString(AppSettingsManager.getInstance().whiteBalanceMode.getValues(),AppSettingsManager.getInstance().getResString(R.string.manual_cct)))
                wbModeval = AppSettingsManager.getInstance().getResString(R.string.manual_cct);

            if (!TextUtils.isEmpty(wbmax) && !TextUtils.isEmpty(wbmin) && !TextUtils.isEmpty(wbModeval)) {
                Log.d(TAG, "Found all wbct values:" +wbmax + " " + wbmin + " " +wbModeval);
                AppSettingsManager.getInstance().manualWhiteBalance.setIsSupported(true);
                AppSettingsManager.getInstance().manualWhiteBalance.setMode(wbModeval);
                int min = Integer.parseInt(parameters.get(wbmin));
                int max = Integer.parseInt(parameters.get(wbmax));
                AppSettingsManager.getInstance().manualWhiteBalance.setValues(createWBStringArray(min,max,100));
            }
            else {
                Log.d(TAG, "Failed to lookup wbct:" + " " +wbmax + " " + wbmin + " " +wbModeval);
                AppSettingsManager.getInstance().manualWhiteBalance.setIsSupported(false);
            }
        }
    }

    public static String[] createWBStringArray(int min, int max, float step)
    {
        Log.d(TAG,"Create Wbvalues");
        ArrayList<String> t = new ArrayList<>();
        t.add(AppSettingsManager.getInstance().getResString(R.string.auto_));
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

        Log.d(TAG, "Manual Iso Presetted:" + AppSettingsManager.getInstance().manualIso.isPresetted());
        if (!AppSettingsManager.getInstance().manualIso.isPresetted()) {

            if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
                AppSettingsManager.getInstance().manualIso.setIsSupported(true);
                AppSettingsManager.getInstance().manualIso.setKEY("m-sr-g");
                AppSettingsManager.getInstance().manualIso.setValues(createIsoValues(100, 1600, 100));
                AppSettingsManager.getInstance().manualIso.setType(AppSettingsManager.ISOMANUAL_MTK);
            } else {
                if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.min_iso)) != null && parameters.get(AppSettingsManager.getInstance().getResString(R.string.max_iso)) != null) {
                    AppSettingsManager.getInstance().manualIso.setIsSupported(true);
                    AppSettingsManager.getInstance().manualIso.setKEY(AppSettingsManager.getInstance().getResString(R.string.continuous_iso));
                    int min = Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.min_iso)));
                    int max = Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.max_iso)));
                    AppSettingsManager.getInstance().manualIso.setValues(createIsoValues(min, max, 50));
                    AppSettingsManager.getInstance().manualIso.setType(AppSettingsManager.ISOMANUAL_QCOM);
                }
                else if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.hw_sensor_iso_range))!= null)
                {
                    AppSettingsManager.getInstance().manualIso.setIsSupported(true);
                    String t[] = parameters.get(AppSettingsManager.getInstance().getResString(R.string.hw_sensor_iso_range)).split(",");
                    int min = Integer.parseInt(t[0]);
                    int max = Integer.parseInt(t[1]);
                    AppSettingsManager.getInstance().manualIso.setValues(createIsoValues(min, max, 50));
                    AppSettingsManager.getInstance().manualIso.setType(AppSettingsManager.ISOMANUAL_KRILLIN);
                    AppSettingsManager.getInstance().manualIso.setKEY(AppSettingsManager.getInstance().getResString(R.string.hw_sensor_iso));

                }
            }
        }
    }

    public static String[] createIsoValues(int miniso, int maxiso, int step)
    {
        Log.d(TAG,"Create Isovalues");
        ArrayList<String> s = new ArrayList<>();
        s.add(AppSettingsManager.getInstance().getResString(R.string.auto_));
        for (int i =miniso; i <= maxiso; i +=step)
        {
            s.add(i + "");
        }
        String[] stringvalues = new String[s.size()];
        return s.toArray(stringvalues);
    }

    private void detectManualExposureTime(Camera.Parameters parameters)
    {
        Log.d(TAG, "ManualExposureTime is Presetted: "+AppSettingsManager.getInstance().manualExposureTime.isPresetted());
        if (AppSettingsManager.getInstance().manualExposureTime.isPresetted())
            return;
        //mtk shutter
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            Log.d(TAG, "ManualExposureTime MTK");
            AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
            AppSettingsManager.getInstance().manualExposureTime.setValues(AppSettingsManager.getInstance().getResources().getStringArray(R.array.mtk_shutter));
            AppSettingsManager.getInstance().manualExposureTime.setKEY("m-ss");
            AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_MTK);
        }
        else
        {
            //htc shutter
            if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.shutter)) != null) {
                Log.d(TAG, "ManualExposureTime HTC");
                AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
                AppSettingsManager.getInstance().manualExposureTime.setValues(AppSettingsManager.getInstance().getResources().getStringArray(R.array.htc));
                AppSettingsManager.getInstance().manualExposureTime.setKEY(AppSettingsManager.getInstance().getResString(R.string.shutter));
                AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_HTC);
            }
            //lg shutter
            else if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.lg_shutterspeed_values)) != null) {
                Log.d(TAG, "ManualExposureTime LG");
                AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_LG);
                ArrayList<String> l = new ArrayList(Arrays.asList(parameters.get(AppSettingsManager.getInstance().getResString(R.string.lg_shutterspeed_values)).replace(",0", "").split(",")));
                l.remove(0);
                AppSettingsManager.getInstance().manualExposureTime.setValues(l.toArray(new String[l.size()]));
                AppSettingsManager.getInstance().manualExposureTime.setKEY(AppSettingsManager.getInstance().getResString(R.string.lg_shutterspeed));
                AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
            }
            //meizu shutter
            else if (parameters.get("shutter-value") != null) {
                Log.d(TAG, "ManualExposureTime Meizu");
                AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
                AppSettingsManager.getInstance().manualExposureTime.setValues(AppSettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_meizu));
                AppSettingsManager.getInstance().manualExposureTime.setKEY("shutter-value");
                AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_MEIZU);
            }
            //krillin shutter
            else if (parameters.get("hw-manual-exposure-value") != null) {
                Log.d(TAG, "ManualExposureTime Krilin");
                AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
                AppSettingsManager.getInstance().manualExposureTime.setValues(AppSettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_krillin));
                AppSettingsManager.getInstance().manualExposureTime.setKEY("hw-manual-exposure-value");
                AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_KRILLIN);
            }
            else if (parameters.get("hw-max-exposure-time") != null) {
                Log.d(TAG, "ManualExposureTime huawei");
                AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
                AppSettingsManager.getInstance().manualExposureTime.setValues(AppSettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_krillin));
                AppSettingsManager.getInstance().manualExposureTime.setKEY("hw-sensor-exposure-time");
                AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_KRILLIN);
            }
            //sony shutter
            else if (parameters.get("sony-max-shutter-speed") != null) {
                Log.d(TAG, "ManualExposureTime Sony");
                AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
                AppSettingsManager.getInstance().manualExposureTime.setValues(getSupportedShutterValues(
                        Long.parseLong(parameters.get("sony-min-shutter-speed")),
                        Long.parseLong(parameters.get("sony-max-shutter-speed")),
                        true));
                AppSettingsManager.getInstance().manualExposureTime.setKEY("sony-shutter-speed");
                AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_SONY);
            }
            //qcom shutter
            else if (parameters.get(camstring(R.string.max_exposure_time)) != null && parameters.get(camstring(R.string.min_exposure_time)) != null) {
                long min = 0, max = 0;
                if (parameters.get(camstring(R.string.max_exposure_time)).contains(".")) {
                    Log.d(TAG, "ManualExposureTime Qcom Microsec");
                    min = (long) (Double.parseDouble(parameters.get(camstring(R.string.min_exposure_time))) * 1000);
                    max = (long) (Double.parseDouble(parameters.get(camstring(R.string.max_exposure_time))) * 1000);
                    AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MICORSEC);
                } else {
                    Log.d(TAG, "ManualExposureTime Qcom Millisec");
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_exposure_time)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_exposure_time)));
                    AppSettingsManager.getInstance().manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MILLISEC);
                }
                if (max > 0) {

                    AppSettingsManager.getInstance().manualExposureTime.setIsSupported(true);
                    AppSettingsManager.getInstance().manualExposureTime.setKEY(camstring(R.string.exposure_time));
                    AppSettingsManager.getInstance().manualExposureTime.setValues(getSupportedShutterValues(min, max, true));
                }
            }
        }
    }

    private String[] getSupportedShutterValues(long minMillisec, long maxMiliisec, boolean withautomode) {
        String[] allvalues = AppSettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        if (withautomode)
            tmp.add(AppSettingsManager.getInstance().getResString(R.string.auto_));
        for (int i = 0; i < allvalues.length; i++) {
            String s = allvalues[i];
            if (!s.equals(AppSettingsManager.getInstance().getResString(R.string.auto_))) {
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
            AppSettingsManager.getInstance().nonZslManualMode.setIsSupported(true);
            AppSettingsManager.getInstance().nonZslManualMode.setKEY("non-zsl-manual-mode");
            AppSettingsManager.getInstance().nonZslManualMode.setValues(new String[]{AppSettingsManager.getInstance().getResString(R.string.on_),AppSettingsManager.getInstance().getResString(R.string.off_)});
        }
    }

    private void detectTNR(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().temporal_nr.setIsSupported(false);
            AppSettingsManager.getInstance().temporal_video_nr.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.tnr,R.string.tnr_mode,AppSettingsManager.getInstance().temporal_nr);
            detectMode(parameters,R.string.tnr_v,R.string.tnr_mode_v,AppSettingsManager.getInstance().temporal_video_nr);
        }

    }

    private void detectPDAF(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().pdafcontrol.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.pdaf,R.string.pdaf_mode,AppSettingsManager.getInstance().pdafcontrol);
        }

    }

    private void detectSEEMoar(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().seemore_tonemap.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.seemore,R.string.seemore_mode,AppSettingsManager.getInstance().seemore_tonemap);
        }

    }

    private void detectRefocus(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().refocus.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.refocus,R.string.refocus_mode,AppSettingsManager.getInstance().refocus);
        }

    }

    private void detectRDI(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().rawdumpinterface.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.rdi,R.string.rdi_mode,AppSettingsManager.getInstance().rawdumpinterface);
        }

    }

    private void detectOptizoom(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().optizoom.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.optizoom,R.string.optizoom_mode,AppSettingsManager.getInstance().optizoom);
        }

    }

    private void detectChromaFlash(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().chromaflash.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.chroma,R.string.chroma_mode,AppSettingsManager.getInstance().chromaflash);
        }

    }

    private void detectTruePotrait(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().truepotrait.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.truepotrait,R.string.truepotrait_mode,AppSettingsManager.getInstance().truepotrait);
        }

    }

    private void detectDenoise(Camera.Parameters parameters)
    {
        Log.d(TAG, "Denoise is Presetted: "+AppSettingsManager.getInstance().denoiseMode.isPresetted());
        if (AppSettingsManager.getInstance().denoiseMode.isPresetted())
            return;
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if(parameters.get(camstring(R.string.mtk_3dnr_mode))!=null) {
                if (parameters.get(camstring(R.string.mtk_3dnr_mode_values)).equals("on,off")) {
                    AppSettingsManager.getInstance().denoiseMode.setIsSupported(true);
                    AppSettingsManager.getInstance().denoiseMode.setKEY(camstring(R.string.mtk_3dnr_mode));
                    AppSettingsManager.getInstance().denoiseMode.setValues(parameters.get(camstring(R.string.mtk_3dnr_mode_values)).split(","));
                }
            }
        }
        else
        {
            detectMode(parameters,R.string.denoise,R.string.denoise_values,AppSettingsManager.getInstance().denoiseMode);
        }
    }

    private void detectDisModes(Camera.Parameters parameters) {
        if (AppSettingsManager.getInstance().digitalImageStabilisationMode.isPresetted())
            return;
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
            AppSettingsManager.getInstance().digitalImageStabilisationMode.setIsSupported(false);
        } else{
            detectMode(parameters,R.string.dis,R.string.dis_values, AppSettingsManager.getInstance().digitalImageStabilisationMode);
        }
    }

    private void detectManualSaturation(Camera.Parameters parameters)
    {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            Log.d(TAG, "Saturation: MTK");
            if (parameters.get(camstring(R.string.saturation))!= null && parameters.get(camstring(R.string.saturation_values))!= null) {
                AppSettingsManager.getInstance().manualSaturation.setValues(parameters.get(camstring(R.string.saturation_values)).split(","));
                AppSettingsManager.getInstance().manualSaturation.setKEY(camstring(R.string.saturation));
                AppSettingsManager.getInstance().manualSaturation.setIsSupported(true);
                AppSettingsManager.getInstance().manualSaturation.set(parameters.get(camstring(R.string.saturation)));
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.lg_color_adjust_max)) != null
                    && parameters.get(AppSettingsManager.getInstance().getResString(R.string.lg_color_adjust_min)) != null) {
                Log.d(TAG, "Saturation: LG");
                min = Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.lg_color_adjust_min)));
                max = Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.lg_color_adjust_max)));
                AppSettingsManager.getInstance().manualSaturation.setKEY(AppSettingsManager.getInstance().getResString(R.string.lg_color_adjust));
                AppSettingsManager.getInstance().manualSaturation.set(parameters.get(camstring(R.string.lg_color_adjust)));
            }
            else if (parameters.get(camstring(R.string.saturation_max)) != null) {
                Log.d(TAG, "Saturation: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.saturation_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.saturation_max)));
                AppSettingsManager.getInstance().manualSaturation.setKEY(camstring(R.string.saturation));
                AppSettingsManager.getInstance().manualSaturation.set(parameters.get(camstring(R.string.saturation)));
            } else if (parameters.get(camstring(R.string.max_saturation)) != null) {
                Log.d(TAG, "Saturation: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.min_saturation)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_saturation)));
                AppSettingsManager.getInstance().manualSaturation.setKEY(camstring(R.string.saturation));
                AppSettingsManager.getInstance().manualSaturation.set(parameters.get(camstring(R.string.saturation)));
            }
            Log.d(TAG, "Saturation Max:" +max);
            if (max > 0) {
                AppSettingsManager.getInstance().manualSaturation.setValues(createStringArray(min, max, 1));
                AppSettingsManager.getInstance().manualSaturation.setIsSupported(true);
            }
        }
    }

    private void detectManualSharpness(Camera.Parameters parameters) {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            Log.d(TAG, "Sharpness: MTK");
            if (parameters.get(camstring(R.string.edge))!= null && parameters.get(camstring(R.string.edge_values))!= null) {
                AppSettingsManager.getInstance().manualSharpness.setValues(parameters.get(camstring(R.string.edge_values)).split(","));
                AppSettingsManager.getInstance().manualSharpness.setKEY(camstring(R.string.edge));
                AppSettingsManager.getInstance().manualSharpness.setIsSupported(true);
                AppSettingsManager.getInstance().manualSharpness.set(parameters.get(camstring(R.string.edge)));
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(camstring(R.string.sharpness_max)) != null) {
                Log.d(TAG, "Sharpness: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.sharpness_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.sharpness_max)));
                AppSettingsManager.getInstance().manualSharpness.setKEY(camstring(R.string.sharpness));
                AppSettingsManager.getInstance().manualSharpness.set(parameters.get(camstring(R.string.sharpness)));
            } else if (parameters.get(camstring(R.string.max_sharpness)) != null) {
                Log.d(TAG, "Sharpness: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.min_sharpness)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_sharpness)));
                AppSettingsManager.getInstance().manualSharpness.setKEY(camstring(R.string.sharpness));
                AppSettingsManager.getInstance().manualSharpness.set(parameters.get(camstring(R.string.sharpness)));
            }
            Log.d(TAG, "Sharpness Max:" +max);
            if (max > 0) {
                AppSettingsManager.getInstance().manualSharpness.setValues(createStringArray(min, max, 1));
                AppSettingsManager.getInstance().manualSharpness.setIsSupported(true);
            }
        }
    }

    private void detectManualBrightness(Camera.Parameters parameters) {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            Log.d(TAG, "Brightness: MTK");
            if (parameters.get(camstring(R.string.brightness))!= null && parameters.get(camstring(R.string.brightness_values))!= null) {
                AppSettingsManager.getInstance().manualBrightness.setValues(parameters.get(camstring(R.string.brightness_values)).split(","));
                AppSettingsManager.getInstance().manualBrightness.setKEY(camstring(R.string.brightness));
                AppSettingsManager.getInstance().manualBrightness.setIsSupported(true);
                AppSettingsManager.getInstance().manualBrightness.set(parameters.get(camstring(R.string.brightness)));
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(camstring(R.string.brightness_max)) != null) {
                Log.d(TAG, "Brightness: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.brightness_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.brightness_max)));
            } else if (parameters.get(camstring(R.string.max_brightness)) != null) {
                min = Integer.parseInt(parameters.get(camstring(R.string.min_brightness)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_brightness)));
                Log.d(TAG, "Brightness: Default");
            }
            Log.d(TAG, "Brightness Max:" +max);
            if (max > 0) {
                if (parameters.get(camstring(R.string.brightness))!= null)
                    AppSettingsManager.getInstance().manualBrightness.setKEY(camstring(R.string.brightness));
                else if (parameters.get(camstring(R.string.luma_adaptation))!= null)
                    AppSettingsManager.getInstance().manualBrightness.setKEY(camstring(R.string.luma_adaptation));
                AppSettingsManager.getInstance().manualBrightness.setValues(createStringArray(min, max, 1));
                AppSettingsManager.getInstance().manualBrightness.set(parameters.get(AppSettingsManager.getInstance().manualBrightness.getKEY()));
                AppSettingsManager.getInstance().manualBrightness.setIsSupported(true);
            }
        }
    }

    private void detectManualContrast(Camera.Parameters parameters) {
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get(camstring(R.string.contrast))!= null && parameters.get(camstring(R.string.contrast_values))!= null) {
                AppSettingsManager.getInstance().manualContrast.setValues(parameters.get(camstring(R.string.contrast_values)).split(","));
                AppSettingsManager.getInstance().manualContrast.setKEY(camstring(R.string.contrast));
                AppSettingsManager.getInstance().manualContrast.setIsSupported(true);
                AppSettingsManager.getInstance().manualContrast.set(parameters.get(camstring(R.string.contrast)));
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
                AppSettingsManager.getInstance().manualContrast.setKEY(camstring(R.string.contrast));
                AppSettingsManager.getInstance().manualContrast.setValues(createStringArray(min, max, 1));
                AppSettingsManager.getInstance().manualContrast.setIsSupported(true);
                AppSettingsManager.getInstance().manualContrast.set(parameters.get(camstring(R.string.contrast)));
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
        Log.d(TAG, "mf is preseted:" +AppSettingsManager.getInstance().manualFocus.isPresetted());
        if (AppSettingsManager.getInstance().manualFocus.isPresetted())
            return;

        int min =0, max =0, step = 0;
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            AppSettingsManager.getInstance().manualFocus.setMode(camstring(R.string.manual));
            AppSettingsManager.getInstance().manualFocus.setType(-1);
            AppSettingsManager.getInstance().manualFocus.setIsSupported(true);
            min = 0;
            max = 1023;
            step = 10;
            AppSettingsManager.getInstance().manualFocus.setKEY(AppSettingsManager.getInstance().getResString(R.string.afeng_pos));
            Log.d(TAG, "MF MTK");
        }
        else {
            //lookup old qcom

            if (parameters.get(camstring(R.string.manual_focus_modes)) == null) {

                if (parameters.get(camstring(R.string.max_focus_pos_index)) != null
                        && parameters.get(camstring(R.string.min_focus_pos_index))!= null
                        && AppSettingsManager.getInstance().focusMode.contains(camstring(R.string.manual))) {

                    AppSettingsManager.getInstance().manualFocus.setMode(camstring(R.string.manual));
                    AppSettingsManager.getInstance().manualFocus.setType(1);
                    AppSettingsManager.getInstance().manualFocus.setIsSupported(true);
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_focus_pos_index)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_focus_pos_index)));
                    step = 10;
                    AppSettingsManager.getInstance().manualFocus.setKEY(camstring(R.string.manual_focus_position));
                    Log.d(TAG, "MF old qcom");
                }
            }
            else
            {
                //lookup new qcom
                if (parameters.get(camstring(R.string.max_focus_pos_ratio)) != null
                        && parameters.get(camstring(R.string.min_focus_pos_ratio)) != null
                        && AppSettingsManager.getInstance().focusMode.contains(camstring(R.string.manual))) {

                    AppSettingsManager.getInstance().manualFocus.setMode(camstring(R.string.manual));
                    AppSettingsManager.getInstance().manualFocus.setType(2);
                    AppSettingsManager.getInstance().manualFocus.setIsSupported(true);
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_focus_pos_ratio)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_focus_pos_ratio)));
                    step = 1;
                    AppSettingsManager.getInstance().manualFocus.setKEY(camstring(R.string.manual_focus_position));
                    Log.d(TAG, "MF new qcom");
                }
            }
            //htc mf
            if (parameters.get(camstring(R.string.min_focus)) != null && parameters.get(camstring(R.string.max_focus)) != null)
            {
                AppSettingsManager.getInstance().manualFocus.setMode("");
                AppSettingsManager.getInstance().manualFocus.setType(-1);
                AppSettingsManager.getInstance().manualFocus.setIsSupported(true);
                min = Integer.parseInt(parameters.get(camstring(R.string.min_focus)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_focus)));
                step = 1;
                AppSettingsManager.getInstance().manualFocus.setKEY(camstring(R.string.focus));
                Log.d(TAG, "MF HTC");
            }

            //huawai mf
            if(parameters.get(AppSettingsManager.getInstance().getResString(R.string.hw_vcm_end_value)) != null && parameters.get(AppSettingsManager.getInstance().getResString(R.string.hw_vcm_start_value)) != null)
            {
                Log.d(TAG,"Huawei MF");
                AppSettingsManager.getInstance().manualFocus.setMode(camstring(R.string.manual));
                AppSettingsManager.getInstance().manualFocus.setType(-1);
                AppSettingsManager.getInstance().manualFocus.setIsSupported(true);
                max = Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.hw_vcm_end_value)));
                min = Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.hw_vcm_start_value)));
                Log.d(TAG,"min/max mf:" + min+"/"+max);
                step = 10;
                AppSettingsManager.getInstance().manualFocus.setKEY(AppSettingsManager.getInstance().getResString(R.string.hw_manual_focus_step_value));
            }
        }
        //create mf values
        if (AppSettingsManager.getInstance().manualFocus.isSupported())
            AppSettingsManager.getInstance().manualFocus.setValues(createManualFocusValues(min, max,step));
    }

    public static String[] createManualFocusValues(int min, int max, int step)
    {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(AppSettingsManager.getInstance().getResString(R.string.auto_));

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
            AppSettingsManager.getInstance().setIsFrontCamera(false);
        else
            AppSettingsManager.getInstance().setIsFrontCamera(true);
    }

    private static boolean hasLGFramework()
    {
        try {
            Class c = Class.forName("com.lge.hardware.LGCameraRef");
            Log.d(TAG, "Has Lg Framework");
            c = Class.forName("com.lge.media.CamcorderProfileEx");
            Log.d(TAG, "Has Lg Framework");
            return true;

        } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {

            Log.d(TAG, "No LG Framework");
            return false;
        }
    }

    private static boolean isMotoExt()
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

    private static boolean isMTKDevice()
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
            Log.WriteEx(e);
            Log.d(TAG, "MTK Framework not found");
            return false;
        }
    }

    public static int getFramework()
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
        switch (AppSettingsManager.getInstance().getFrameWork())
        {
            case AppSettingsManager.FRAMEWORK_LG:
            {
                Log.d(TAG,"Open LG Camera");
                LGCameraRef lgCamera;
                if (AppSettingsManager.getInstance().opencamera1Legacy.getBoolean())
                    lgCamera = new LGCameraRef(currentcamera, 256);
                else
                    lgCamera = new LGCameraRef(currentcamera);
                return lgCamera.getParameters();
            }
            case AppSettingsManager.FRAMEWORK_MOTO_EXT:
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
            case AppSettingsManager.FRAMEWORK_MTK:
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
               // if (AppSettingsManager.getInstance().opencamera1Legacy.getBoolean())
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

    private void detectMode(Camera.Parameters parameters, int key, int keyvalues, AppSettingsManager.SettingMode mode)
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
        if (AppSettingsManager.getInstance().getIsFrontCamera())
        {
            AppSettingsManager.getInstance().pictureFormat.setIsSupported(false);
            AppSettingsManager.getInstance().rawPictureFormat.setIsSupported(false);
        }
        else {
            if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
                AppSettingsManager.getInstance().pictureFormat.setIsSupported(true);
                AppSettingsManager.getInstance().rawPictureFormat.setIsSupported(true);
            } else {

                String formats = parameters.get(camstring(R.string.picture_format_values));

                if (!AppSettingsManager.getInstance().rawPictureFormat.isPresetted()) {
                    Log.d(TAG, "rawpictureformat is not preseted try to find it");
                    if (formats.contains("bayer-mipi") || formats.contains("raw")) {
                        AppSettingsManager.getInstance().rawPictureFormat.setIsSupported(true);
                        String[] forms = formats.split(",");
                        for (String s : forms) {
                            if (s.contains("bayer-mipi") || s.contains("raw")) {
                                Log.d(TAG, "rawpictureformat set to:" +s);
                                AppSettingsManager.getInstance().rawPictureFormat.set(s);
                                AppSettingsManager.getInstance().rawPictureFormat.setIsSupported(true);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    if (!formats.contains(AppSettingsManager.getInstance().rawPictureFormat.get()))
                    {
                        AppSettingsManager.getInstance().rawPictureFormat.set(AppSettingsManager.getInstance().rawPictureFormat.get());
                        AppSettingsManager.getInstance().rawPictureFormat.setIsSupported(true);
                    }


                }
                if (formats.contains(AppSettingsManager.getInstance().getResString(R.string.bayer_)))
                {
                    Log.d(TAG, "create rawformats");
                    ArrayList<String> tmp = new ArrayList<>();
                    String[] forms = formats.split(",");
                    for (String s : forms) {
                        if (s.contains(AppSettingsManager.getInstance().getResString(R.string.bayer_)))
                        {
                            tmp.add(s);
                        }
                    }
                    String[] rawFormats = new String[tmp.size()];
                    tmp.toArray(rawFormats);
                    AppSettingsManager.getInstance().rawPictureFormat.setValues(rawFormats);
                    if (rawFormats.length == 0)
                        AppSettingsManager.getInstance().rawPictureFormat.setIsSupported(false);
                    else
                        AppSettingsManager.getInstance().rawPictureFormat.setIsSupported(true);
                }
            }
            AppSettingsManager.getInstance().pictureFormat.setIsSupported(true);

            if (AppSettingsManager.getInstance().getDngProfilesMap() != null && AppSettingsManager.getInstance().getDngProfilesMap().size() > 0)
            {
                Log.d(TAG, "Dng, bayer, jpeg supported");
                AppSettingsManager.getInstance().pictureFormat.setValues(new String[]
                        {
                                AppSettingsManager.getInstance().getResString(R.string.jpeg_),
                                AppSettingsManager.getInstance().getResString(R.string.dng_),
                                AppSettingsManager.getInstance().getResString(R.string.bayer_)
                        });
            }
            else if (AppSettingsManager.getInstance().rawPictureFormat.isSupported()) {
                Log.d(TAG, "bayer, jpeg supported");
                AppSettingsManager.getInstance().pictureFormat.setValues(new String[]{
                        AppSettingsManager.getInstance().getResString(R.string.jpeg_),
                        AppSettingsManager.getInstance().getResString(R.string.bayer_)
                });
            }
            else
            {
                Log.d(TAG, "jpeg supported");
                AppSettingsManager.getInstance().pictureFormat.setValues(new String[]{
                        AppSettingsManager.getInstance().getResString(R.string.jpeg_)
                });
            }

        }
    }

    private void detectPictureSizes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.picture_size,R.string.picture_size_values,AppSettingsManager.getInstance().pictureSize);
    }

    private void detectFocusModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.focus_mode,R.string.focus_mode_values,AppSettingsManager.getInstance().focusMode);
    }

    private void detectWhiteBalanceModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.whitebalance,R.string.whitebalance_values,AppSettingsManager.getInstance().whiteBalanceMode);
    }

    private void detectExposureModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.exposure))!= null) {
            detectMode(parameters,R.string.exposure,R.string.exposure_mode_values,AppSettingsManager.getInstance().exposureMode);
        }
        else if (parameters.get(camstring(R.string.auto_exposure_values))!= null) {
            detectMode(parameters,R.string.auto_exposure,R.string.auto_exposure_values,AppSettingsManager.getInstance().exposureMode);
        }
        else if(parameters.get(camstring(R.string.sony_metering_mode))!= null) {
            detectMode(parameters,R.string.sony_metering_mode,R.string.sony_metering_mode_values,AppSettingsManager.getInstance().exposureMode);
        }
        else if(parameters.get(camstring(R.string.exposure_meter))!= null) {
            detectMode(parameters,R.string.exposure_meter,R.string.exposure_meter_values,AppSettingsManager.getInstance().exposureMode);
        }
        else if (parameters.get(camstring(R.string.hw_exposure_mode_values)) != null)
            detectMode(parameters, R.string.hw_exposure_mode,R.string.hw_exposure_mode_values, AppSettingsManager.getInstance().exposureMode);
        if (!TextUtils.isEmpty(AppSettingsManager.getInstance().exposureMode.getKEY()))
            AppSettingsManager.getInstance().exposureMode.setIsSupported(true);
        else
            AppSettingsManager.getInstance().exposureMode.setIsSupported(false);
    }

    private void detectColorModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.effect,R.string.effect_values,AppSettingsManager.getInstance().colorMode);
    }

    private void detectFlashModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.flash_mode,R.string.flash_mode_values,AppSettingsManager.getInstance().flashMode);
    }

    private void detectIsoModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.iso_mode_values))!= null){
            detectMode(parameters,R.string.iso,R.string.iso_mode_values,AppSettingsManager.getInstance().isoMode);
        }
        else if (parameters.get(camstring(R.string.iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.iso_values,AppSettingsManager.getInstance().isoMode);
        }
        else if (parameters.get(camstring(R.string.iso_speed_values))!= null) {
            detectMode(parameters,R.string.iso_speed,R.string.iso_speed_values,AppSettingsManager.getInstance().isoMode);
        }
        else if (parameters.get(camstring(R.string.sony_iso_values))!= null) {
            detectMode(parameters,R.string.sony_iso,R.string.sony_iso_values,AppSettingsManager.getInstance().isoMode);
        }
        else if (parameters.get(camstring(R.string.lg_iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.lg_iso_values,AppSettingsManager.getInstance().isoMode);
        }
        if (AppSettingsManager.getInstance().isoMode.getValues().length >1)
            AppSettingsManager.getInstance().isoMode.setIsSupported(true);
        else
            AppSettingsManager.getInstance().isoMode.setIsSupported(false);
    }

    private void detectAntiBandingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.antibanding,R.string.antibanding_values,AppSettingsManager.getInstance().antiBandingMode);
    }

    private void detectImagePostProcessingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.ipp,R.string.ipp_values,AppSettingsManager.getInstance().imagePostProcessing);
    }

    private void detectPreviewSizeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_size,R.string.preview_size_values,AppSettingsManager.getInstance().previewSize);
    }

    private void detectJpeqQualityModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.jpeg_quality)) == null)
        {
            AppSettingsManager.getInstance().jpegQuality.setIsSupported(false);
            return;
        }
        String[] valuetoreturn = new String[20];
        for (int i = 1; i < 21; i++)
        {
            valuetoreturn[i-1] = "" + i*5;
        }
        AppSettingsManager.getInstance().jpegQuality.setValues(valuetoreturn);
        AppSettingsManager.getInstance().jpegQuality.set(parameters.get(camstring(R.string.jpeg_quality)));
        AppSettingsManager.getInstance().jpegQuality.setKEY(camstring(R.string.jpeg_quality));
        if (valuetoreturn.length >0)
            AppSettingsManager.getInstance().jpegQuality.setIsSupported(true);
    }



    private void detectAeBracketModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.ae_bracket_hdr,R.string.ae_bracket_hdr_values,AppSettingsManager.getInstance().aeBracket);
    }

    private void detectPreviewFPSModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_frame_rate,R.string.preview_frame_rate_values,AppSettingsManager.getInstance().previewFps);
    }

    private void detectPreviewFormatModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.preview_format,R.string.preview_format_values,AppSettingsManager.getInstance().previewFormat);
    }

    private void detectSceneModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.scene_mode,R.string.scene_mode_values,AppSettingsManager.getInstance().sceneMode);
    }

    private void detectLensShadeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.lensshade,R.string.lensshade_values,AppSettingsManager.getInstance().lenshade);
    }

    private void detectZeroShutterLagModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.zsl_values)) != null)
        {
            detectMode(parameters,R.string.zsl,R.string.zsl_values,AppSettingsManager.getInstance().zeroshutterlag);

        }
        else if (parameters.get(camstring(R.string.mode_values)) != null)
        {
            detectMode(parameters,R.string.mode,R.string.mode_values,AppSettingsManager.getInstance().zeroshutterlag);
        }
        else if (parameters.get(camstring(R.string.zsd_mode)) != null) {
            detectMode(parameters, R.string.zsd_mode, R.string.zsd_mode_values, AppSettingsManager.getInstance().zeroshutterlag);
        }

        if (AppSettingsManager.getInstance().lenshade.getValues().length == 0)
            AppSettingsManager.getInstance().lenshade.setIsSupported(false);
    }

    private void detectSceneDetectModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.scene_mode,R.string.scene_mode_values,AppSettingsManager.getInstance().sceneMode);
    }

    private void detectMemoryColorEnhancementModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.mce,R.string.mce_values,AppSettingsManager.getInstance().memoryColorEnhancement);
    }

    private void detectVideoSizeModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.video_size,R.string.video_size_values,AppSettingsManager.getInstance().videoSize);
    }

    private void detectCorrelatedDoubleSamplingModes(Camera.Parameters parameters)
    {
        detectMode(parameters,R.string.cds_mode,R.string.cds_mode_values,AppSettingsManager.getInstance().correlatedDoubleSampling);
    }

    private void detectVideoHdr(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.video_hdr_values)) != null)
        {
            detectMode(parameters,R.string.video_hdr,R.string.video_hdr_values,AppSettingsManager.getInstance().videoHDR);
        }
        else if (parameters.get(camstring(R.string.sony_video_hdr_values))!= null) {
            detectMode(parameters,R.string.sony_video_hdr,R.string.sony_video_hdr_values,AppSettingsManager.getInstance().videoHDR);
        }
        else
            AppSettingsManager.getInstance().videoHDR.setIsSupported(false);
    }

    private void detectVideoHFR(Camera.Parameters parameters)
    {
        if (parameters.get("video-hfr") != null)
        {
            String hfrvals = parameters.get("video-hfr-values");
            if (!hfrvals.equals("off"))
            {
                if (TextUtils.isEmpty(hfrvals)) {
                    AppSettingsManager.getInstance().videoHFR.setValues("off,60,120".split(","));
                    AppSettingsManager.getInstance().videoHFR.setKEY("video-hfr");
                    AppSettingsManager.getInstance().videoHFR.setIsSupported(true);
                    AppSettingsManager.getInstance().videoHFR.set(parameters.get("video-hfr"));
                }
                else
                    AppSettingsManager.getInstance().videoHFR.setIsSupported(false);
            }
        }
        else if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            if (parameters.get("hsvr-prv-fps-values") != null)
            {
                AppSettingsManager.getInstance().videoHFR.setValues(parameters.get("hsvr-prv-fps-values").split(","));
                AppSettingsManager.getInstance().videoHFR.setKEY("hsvr-prv-fps");
                AppSettingsManager.getInstance().videoHFR.setIsSupported(true);
                AppSettingsManager.getInstance().videoHFR.set(parameters.get("hsvr-prv-fps"));
            }
            else
                AppSettingsManager.getInstance().videoHFR.setIsSupported(false);
        }
        else
        {
            /*switch (AppSettingsManager.getInstance().getDevice())
            {
                case Htc_M8:
                case Htc_M9:
                case HTC_OneA9:
                case HTC_OneE8:
                    AppSettingsManager.getInstance().videoHFR.setValues("off,60,120".split(","));
                    AppSettingsManager.getInstance().videoHFR.setKEY("video-mode");
                    AppSettingsManager.getInstance().videoHFR.setIsSupported(true);
                    AppSettingsManager.getInstance().videoHFR.set(parameters.get("video-mode"));
                    break;
                default:
                    AppSettingsManager.getInstance().videoHFR.setIsSupported(false);
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
        if(AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_LG)
            supportedProfiles =  getLGVideoMediaProfiles(cameraid);
        else
            supportedProfiles= getDefaultVideoMediaProfiles(cameraid);

        if (supportedProfiles.get(_720phfr) == null && AppSettingsManager.getInstance().videoHFR.isSupported() && AppSettingsManager.getInstance().videoHFR.contains("120"))
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
        if (AppSettingsManager.getInstance().videoSize.isSupported() && AppSettingsManager.getInstance().videoSize.contains("3840x2160")
                && AppSettingsManager.getInstance().videoHFR.isSupported()&& AppSettingsManager.getInstance().videoHFR.contains("60")) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
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
        if (supportedProfiles.get(_2160p) == null && AppSettingsManager.getInstance().videoSize.isSupported()&& AppSettingsManager.getInstance().videoSize.contains("3840x2160"))
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

        if (AppSettingsManager.getInstance().videoSize.isSupported() && AppSettingsManager.getInstance().videoSize.contains("1920x1080")
                && AppSettingsManager.getInstance().videoHFR.isSupported()&& AppSettingsManager.getInstance().videoHFR.contains("60")) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
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
        AppSettingsManager.getInstance().saveMediaProfiles(supportedProfiles);
        AppSettingsManager.getInstance().setApiString(AppSettingsManager.VIDEOPROFILE, "720p");

        publishProgress("VideoMediaProfiles:" + getStringFromArray(supportedProfiles.keySet().toArray(new String[supportedProfiles.size()])));
    }
}
