package freed.cam.featuredetector;

import android.hardware.Camera;

import com.lge.hardware.LGCamera;
import com.troop.freedcam.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.camera1.cameraholder.CameraHolderLegacy;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.utils.AppSettingsManager;
import freed.utils.Log;
import freed.utils.VideoMediaProfile;


/**
 * Created by troop on 23.01.2017.
 */

public class Camera1FeatureDetectorTask extends AbstractFeatureDetectorTask
{
    private static final  String TAG = Camera1FeatureDetectorTask.class.getSimpleName();

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

        publishProgress("Device:"+appSettingsManager.getDeviceString());
        //detect frameworks
        appSettingsManager.setFramework(getFramework());
        publishProgress("FrameWork:"+appSettingsManager.getFrameWork());


        int cameraCounts = Camera.getNumberOfCameras();
        Log.d(TAG, "Cameras Found: " + cameraCounts);
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
            publishProgress("DngSupported:" + (appS.getDngProfilesMap() != null && appS.getDngProfilesMap().size() > 0) + " RawSupport:"+appS.rawPictureFormat.isSupported());
            publishProgress("PictureFormats:" + getStringFromArray(appS.pictureFormat.getValues()));
            publishProgress("RawFormats:" + getStringFromArray(appS.rawPictureFormat.getValues()));
            publishProgress(" RawFormat:" + appS.rawPictureFormat.get());

            appSettingsManager.modules.set(appS.getResString(R.string.module_picture));

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
                appSettingsManager.dualPrimaryCameraMode.setValues(parameters.get("hw-dual-primary-supported").split(","));
                appSettingsManager.dualPrimaryCameraMode.setKEY("hw-dual-primary-mode");
                appSettingsManager.dualPrimaryCameraMode.setIsSupported(true);
            }

            if (parameters.get("hw-supported-aperture-value") != null)
            {
                appSettingsManager.manualAperture.setKEY("hw-set-aperture-value");
                appSettingsManager.manualAperture.setValues(parameters.get("hw-supported-aperture-value").split(","));
                appSettingsManager.manualAperture.setIsSupported(true);
            }
        }

        appS.SetCurrentCamera(0);

        return null;
    }

    private void detectAutoHdr(Camera.Parameters parameters) {
        if (appSettingsManager.hdrMode.isPresetted())
            return;
        if (parameters.get(camstring(R.string.auto_hdr_supported))!=null){
            appSettingsManager.hdrMode.setIsSupported(false);
            return;
        }
        String autohdr = parameters.get(camstring(R.string.auto_hdr_supported));
        if (autohdr != null && !autohdr.equals("") && autohdr.equals(camstring(R.string.true_)) && parameters.get(camstring(R.string.auto_hdr_enable)) != null) {

            List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(appSettingsManager.getResString(R.string.scene_mode_values)).split(",")));

            List<String> hdrVals =  new ArrayList<>();
            hdrVals.add(camstring(R.string.off_));

            if (Scenes.contains(camstring(R.string.scene_mode_hdr))) {
                hdrVals.add(camstring(R.string.on_));
            }
            if (Scenes.contains(camstring(R.string.scene_mode_asd))) {
                hdrVals.add(camstring(R.string.auto_));
            }
            appSettingsManager.hdrMode.setValues(hdrVals.toArray(new String[hdrVals.size()]));
            appSettingsManager.hdrMode.setIsSupported(true);
            appSettingsManager.hdrMode.setType(1);
        }
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


    private void detectManualWhiteBalance(Camera.Parameters parameters) {
        if (appSettingsManager.manualWhiteBalance.isPresetted())
            return;
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
            appSettingsManager.manualWhiteBalance.setIsSupported(false);
        else if (appSettingsManager.manualWhiteBalance.isSupported()) // happens when its already set due supportedevices.xml
            return;
        else
        {
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
                appSettingsManager.manualWhiteBalance.setValues(createWBStringArray(min,max,100, appSettingsManager));
            }
            else {
                Log.d(TAG, "Failed to lookup wbct:" + " " +wbmax + " " + wbmin + " " +wbModeval);
                appSettingsManager.manualWhiteBalance.setIsSupported(false);
            }
        }
    }

    public static String[] createWBStringArray(int min, int max, float step, AppSettingsManager appSettingsManager)
    {
        Log.d(TAG,"Create Wbvalues");
        ArrayList<String> t = new ArrayList<>();
        t.add(appSettingsManager.getResString(R.string.auto_));
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

        Log.d(TAG, "Manual Iso Presetted:" + appSettingsManager.manualIso.isPresetted());
        if (!appSettingsManager.manualIso.isPresetted()) {

            if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
                appSettingsManager.manualIso.setIsSupported(true);
                appSettingsManager.manualIso.setKEY("m-sr-g");
                appSettingsManager.manualIso.setValues(createIsoValues(100, 1600, 100, appSettingsManager));
                appSettingsManager.manualIso.setType(AppSettingsManager.ISOMANUAL_MTK);
            } else {
                if (parameters.get(appSettingsManager.getResString(R.string.min_iso)) != null && parameters.get(appSettingsManager.getResString(R.string.max_iso)) != null) {
                    appSettingsManager.manualIso.setIsSupported(true);
                    appSettingsManager.manualIso.setKEY(appSettingsManager.getResString(R.string.continuous_iso));
                    int min = Integer.parseInt(parameters.get(appSettingsManager.getResString(R.string.min_iso)));
                    int max = Integer.parseInt(parameters.get(appSettingsManager.getResString(R.string.max_iso)));
                    appSettingsManager.manualIso.setValues(createIsoValues(min, max, 50, appSettingsManager));
                    appSettingsManager.manualIso.setType(AppSettingsManager.ISOMANUAL_QCOM);
                }
                else if (parameters.get(appSettingsManager.getResString(R.string.hw_sensor_iso_range))!= null)
                {
                    appSettingsManager.manualIso.setIsSupported(true);
                    String t[] = parameters.get(appSettingsManager.getResString(R.string.hw_sensor_iso_range)).split(",");
                    int min = Integer.parseInt(t[0]);
                    int max = Integer.parseInt(t[1]);
                    appSettingsManager.manualIso.setValues(createIsoValues(min, max, 50, appSettingsManager));
                    appSettingsManager.manualIso.setType(AppSettingsManager.ISOMANUAL_KRILLIN);
                    appSettingsManager.manualIso.setKEY(appSettingsManager.getResString(R.string.hw_sensor_iso));

                }
            }
        }
    }

    public static String[] createIsoValues(int miniso, int maxiso, int step,AppSettingsManager appSettingsManager)
    {
        Log.d(TAG,"Create Isovalues");
        ArrayList<String> s = new ArrayList<>();
        s.add(appSettingsManager.getResString(R.string.auto_));
        for (int i =miniso; i <= maxiso; i +=step)
        {
            s.add(i + "");
        }
        String[] stringvalues = new String[s.size()];
        return s.toArray(stringvalues);
    }

    private void detectManualExposureTime(Camera.Parameters parameters)
    {
        Log.d(TAG, "ManualExposureTime is Presetted: "+appSettingsManager.manualExposureTime.isPresetted());
        if (appSettingsManager.manualExposureTime.isPresetted())
            return;
        //mtk shutter
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            Log.d(TAG, "ManualExposureTime MTK");
            appSettingsManager.manualExposureTime.setIsSupported(true);
            appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.mtk_shutter));
            appSettingsManager.manualExposureTime.setKEY("m-ss");
            appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_MTK);
        }
        else
        {
            //htc shutter
            if (parameters.get(appSettingsManager.getResString(R.string.shutter)) != null) {
                Log.d(TAG, "ManualExposureTime HTC");
                appSettingsManager.manualExposureTime.setIsSupported(true);
                appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.htc));
                appSettingsManager.manualExposureTime.setKEY(appSettingsManager.getResString(R.string.shutter));
                appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_HTC);
            }
            //lg shutter
            else if (parameters.get(appSettingsManager.getResString(R.string.lg_shutterspeed_values)) != null) {
                Log.d(TAG, "ManualExposureTime LG");
                appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_LG);
                ArrayList<String> l = new ArrayList(Arrays.asList(parameters.get(appSettingsManager.getResString(R.string.lg_shutterspeed_values)).replace(",0", "").split(",")));
                l.remove(0);
                appSettingsManager.manualExposureTime.setValues(l.toArray(new String[l.size()]));
                appSettingsManager.manualExposureTime.setKEY(appSettingsManager.getResString(R.string.lg_shutterspeed));
                appSettingsManager.manualExposureTime.setIsSupported(true);
            }
            //meizu shutter
            else if (parameters.get("shutter-value") != null) {
                Log.d(TAG, "ManualExposureTime Meizu");
                appSettingsManager.manualExposureTime.setIsSupported(true);
                appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_meizu));
                appSettingsManager.manualExposureTime.setKEY("shutter-value");
                appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_MEIZU);
            }
            //krillin shutter
            else if (parameters.get("hw-manual-exposure-value") != null) {
                Log.d(TAG, "ManualExposureTime Krilin");
                appSettingsManager.manualExposureTime.setIsSupported(true);
                appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_krillin));
                appSettingsManager.manualExposureTime.setKEY("hw-manual-exposure-value");
                appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_KRILLIN);
            }
            else if (parameters.get("hw-max-exposure-time") != null) {
                Log.d(TAG, "ManualExposureTime huawei");
                appSettingsManager.manualExposureTime.setIsSupported(true);
                appSettingsManager.manualExposureTime.setValues(appSettingsManager.getResources().getStringArray(R.array.shutter_values_krillin));
                appSettingsManager.manualExposureTime.setKEY("hw-sensor-exposure-time");
                appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_KRILLIN);
            }
            //sony shutter
            else if (parameters.get("sony-max-shutter-speed") != null) {
                Log.d(TAG, "ManualExposureTime Sony");
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
                if (parameters.get(camstring(R.string.max_exposure_time)).contains(".")) {
                    Log.d(TAG, "ManualExposureTime Qcom Microsec");
                    min = (long) (Double.parseDouble(parameters.get(camstring(R.string.min_exposure_time))) * 1000);
                    max = (long) (Double.parseDouble(parameters.get(camstring(R.string.max_exposure_time))) * 1000);
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MICORSEC);
                } else {
                    Log.d(TAG, "ManualExposureTime Qcom Millisec");
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_exposure_time)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_exposure_time)));
                    appSettingsManager.manualExposureTime.setType(AppSettingsManager.SHUTTER_QCOM_MILLISEC);
                }
                if (max > 0) {

                    appSettingsManager.manualExposureTime.setIsSupported(true);
                    appSettingsManager.manualExposureTime.setKEY(camstring(R.string.exposure_time));
                    appSettingsManager.manualExposureTime.setValues(getSupportedShutterValues(min, max, true));
                }
            }
        }
    }

    private String[] getSupportedShutterValues(long minMillisec, long maxMiliisec, boolean withautomode) {
        String[] allvalues = appSettingsManager.getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        if (withautomode)
            tmp.add(appSettingsManager.getResString(R.string.auto_));
        for (int i = 0; i < allvalues.length; i++) {
            String s = allvalues[i];
            if (!s.equals(appSettingsManager.getResString(R.string.auto_))) {
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
            appSettingsManager.nonZslManualMode.setIsSupported(true);
            appSettingsManager.nonZslManualMode.setKEY("non-zsl-manual-mode");
            appSettingsManager.nonZslManualMode.setValues(new String[]{appSettingsManager.getResString(R.string.on_),appSettingsManager.getResString(R.string.off_)});
        }
    }

    private void detectTNR(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.temporal_nr.setIsSupported(false);
            appSettingsManager.temporal_video_nr.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.tnr,R.string.tnr_mode,appSettingsManager.temporal_nr);
            detectMode(parameters,R.string.tnr_v,R.string.tnr_mode_v,appSettingsManager.temporal_video_nr);
        }

    }

    private void detectPDAF(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.pdafcontrol.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.pdaf,R.string.pdaf_mode,appSettingsManager.pdafcontrol);
        }

    }

    private void detectSEEMoar(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.seemore_tonemap.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.seemore,R.string.seemore_mode,appSettingsManager.seemore_tonemap);
        }

    }

    private void detectRefocus(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.refocus.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.refocus,R.string.refocus_mode,appSettingsManager.refocus);
        }

    }

    private void detectRDI(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.rawdumpinterface.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.rdi,R.string.rdi_mode,appSettingsManager.rawdumpinterface);
        }

    }

    private void detectOptizoom(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.optizoom.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.optizoom,R.string.optizoom_mode,appSettingsManager.optizoom);
        }

    }

    private void detectChromaFlash(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.chromaflash.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.chroma,R.string.chroma_mode,appSettingsManager.chromaflash);
        }

    }

    private void detectTruePotrait(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.truepotrait.setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters,R.string.truepotrait,R.string.truepotrait_mode,appSettingsManager.truepotrait);
        }

    }

    private void detectDenoise(Camera.Parameters parameters)
    {
        Log.d(TAG, "Denoise is Presetted: "+appSettingsManager.denoiseMode.isPresetted());
        if (appSettingsManager.denoiseMode.isPresetted())
            return;
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
            detectMode(parameters,R.string.denoise,R.string.denoise_values,appSettingsManager.denoiseMode);
        }
    }

    private void detectDisModes(Camera.Parameters parameters) {
        if (appSettingsManager.digitalImageStabilisationMode.isPresetted())
            return;
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
            appSettingsManager.digitalImageStabilisationMode.setIsSupported(false);
        } else{
            detectMode(parameters,R.string.dis,R.string.dis_values, appSettingsManager.digitalImageStabilisationMode);
        }
    }

    private void detectManualSaturation(Camera.Parameters parameters)
    {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            Log.d(TAG, "Saturation: MTK");
            if (parameters.get(camstring(R.string.saturation))!= null && parameters.get(camstring(R.string.saturation_values))!= null) {
                appSettingsManager.manualSaturation.setValues(parameters.get(camstring(R.string.saturation_values)).split(","));
                appSettingsManager.manualSaturation.setKEY(camstring(R.string.saturation));
                appSettingsManager.manualSaturation.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(appSettingsManager.getResString(R.string.lg_color_adjust_max)) != null
                    && parameters.get(appSettingsManager.getResString(R.string.lg_color_adjust_min)) != null) {
                Log.d(TAG, "Saturation: LG");
                min = Integer.parseInt(parameters.get(appSettingsManager.getResString(R.string.lg_color_adjust_min)));
                max = Integer.parseInt(parameters.get(appSettingsManager.getResString(R.string.lg_color_adjust_max)));
                appSettingsManager.manualSaturation.setKEY(appSettingsManager.getResString(R.string.lg_color_adjust));
            }
            else if (parameters.get(camstring(R.string.saturation_max)) != null) {
                Log.d(TAG, "Saturation: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.saturation_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.saturation_max)));
                appSettingsManager.manualSaturation.setKEY(camstring(R.string.saturation));
            } else if (parameters.get(camstring(R.string.max_saturation)) != null) {
                Log.d(TAG, "Saturation: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.min_saturation)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_saturation)));
                appSettingsManager.manualSaturation.setKEY(camstring(R.string.saturation));
            }
            Log.d(TAG, "Saturation Max:" +max);
            if (max > 0) {
                appSettingsManager.manualSaturation.setValues(createStringArray(min, max, 1));
                appSettingsManager.manualSaturation.setIsSupported(true);
            }
        }
    }

    private void detectManualSharpness(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            Log.d(TAG, "Sharpness: MTK");
            if (parameters.get(camstring(R.string.edge))!= null && parameters.get(camstring(R.string.edge_values))!= null) {
                appSettingsManager.manualSharpness.setValues(parameters.get(camstring(R.string.edge_values)).split(","));
                appSettingsManager.manualSharpness.setKEY(camstring(R.string.edge));
                appSettingsManager.manualSharpness.setIsSupported(true);
            }
        }
        else {
            int min = 0, max = 0;
            if (parameters.get(camstring(R.string.sharpness_max)) != null) {
                Log.d(TAG, "Sharpness: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.sharpness_min)));
                max = Integer.parseInt(parameters.get(camstring(R.string.sharpness_max)));
                appSettingsManager.manualSharpness.setKEY(camstring(R.string.sharpness));
            } else if (parameters.get(camstring(R.string.max_sharpness)) != null) {
                Log.d(TAG, "Sharpness: Default");
                min = Integer.parseInt(parameters.get(camstring(R.string.min_sharpness)));
                max = Integer.parseInt(parameters.get(camstring(R.string.max_sharpness)));
                appSettingsManager.manualSharpness.setKEY(camstring(R.string.sharpness));
            }
            Log.d(TAG, "Sharpness Max:" +max);
            if (max > 0) {
                appSettingsManager.manualSharpness.setValues(createStringArray(min, max, 1));
                appSettingsManager.manualSharpness.setIsSupported(true);
            }
        }
    }

    private void detectManualBrightness(Camera.Parameters parameters) {
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            Log.d(TAG, "Brightness: MTK");
            if (parameters.get(camstring(R.string.brightness))!= null && parameters.get(camstring(R.string.brightness_values))!= null) {
                appSettingsManager.manualBrightness.setValues(parameters.get(camstring(R.string.brightness_values)).split(","));
                appSettingsManager.manualBrightness.setKEY(camstring(R.string.brightness));
                appSettingsManager.manualBrightness.setIsSupported(true);
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
            Log.d(TAG, "Contrast Max:" +max);
            if (max > 0) {
                appSettingsManager.manualContrast.setKEY(camstring(R.string.contrast));
                appSettingsManager.manualContrast.setValues(createStringArray(min, max, 1));
                appSettingsManager.manualContrast.setIsSupported(true);
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
        Log.d(TAG, "mf is preseted:" +appSettingsManager.manualFocus.isPresetted());
        if (appSettingsManager.manualFocus.isPresetted())
            return;

        int min =0, max =0, step = 0;
        if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
        {
            appSettingsManager.manualFocus.setMode(camstring(R.string.manual));
            appSettingsManager.manualFocus.setType(-1);
            appSettingsManager.manualFocus.setIsSupported(true);
            min = 0;
            max = 1023;
            step = 10;
            appSettingsManager.manualFocus.setKEY(appSettingsManager.getResString(R.string.afeng_pos));
            Log.d(TAG, "MF MTK");
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
                    Log.d(TAG, "MF old qcom");
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
                    Log.d(TAG, "MF new qcom");
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
                Log.d(TAG, "MF HTC");
            }

            //huawai mf
            if(parameters.get(appSettingsManager.getResString(R.string.hw_vcm_end_value)) != null && parameters.get(appSettingsManager.getResString(R.string.hw_vcm_start_value)) != null)
            {
                Log.d(TAG,"Huawei MF");
                appSettingsManager.manualFocus.setMode(camstring(R.string.manual));
                appSettingsManager.manualFocus.setType(-1);
                appSettingsManager.manualFocus.setIsSupported(true);
                max = Integer.parseInt(parameters.get(appSettingsManager.getResString(R.string.hw_vcm_end_value)));
                min = Integer.parseInt(parameters.get(appSettingsManager.getResString(R.string.hw_vcm_start_value)));
                Log.d(TAG,"min/max mf:" + min+"/"+max);
                step = 10;
                appSettingsManager.manualFocus.setKEY(appSettingsManager.getResString(R.string.hw_manual_focus_step_value));
            }
        }
        //create mf values
        if (appSettingsManager.manualFocus.isSupported())
            appSettingsManager.manualFocus.setValues(createManualFocusValues(min, max,step,appSettingsManager));
    }

    public static String[] createManualFocusValues(int min, int max, int step,AppSettingsManager appSettingsManager)
    {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(appSettingsManager.getResString(R.string.auto_));

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

    private static boolean hasLGFramework()
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
        switch (appSettingsManager.getFrameWork())
        {
            case AppSettingsManager.FRAMEWORK_LG:
            {
                Log.d(TAG,"Open LG Camera");
                LGCamera lgCamera;
                if (appSettingsManager.opencamera1Legacy.getBoolean())
                    lgCamera = new LGCamera(currentcamera, 256);
                else
                    lgCamera = new LGCamera(currentcamera);
                return lgCamera.getLGParameters().getParameters();
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
               // if (appSettingsManager.opencamera1Legacy.getBoolean())
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

                String formats = parameters.get(camstring(R.string.picture_format_values));

                if (!appSettingsManager.rawPictureFormat.isPresetted()) {
                    Log.d(TAG, "rawpictureformat is not preseted try to find it");
                    if (formats.contains("bayer-mipi") || formats.contains("raw")) {
                        appSettingsManager.rawPictureFormat.setIsSupported(true);
                        String[] forms = formats.split(",");
                        for (String s : forms) {
                            if (s.contains("bayer-mipi") || s.contains("raw")) {
                                Log.d(TAG, "rawpictureformat set to:" +s);
                                appSettingsManager.rawPictureFormat.set(s);
                                appSettingsManager.rawPictureFormat.setIsSupported(true);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    if (!formats.contains(appSettingsManager.rawPictureFormat.get()))
                    {
                        appSettingsManager.rawPictureFormat.set(appSettingsManager.rawPictureFormat.get());
                        appSettingsManager.rawPictureFormat.setIsSupported(true);
                    }


                }
                if (formats.contains(appSettingsManager.getResString(R.string.bayer_)))
                {
                    Log.d(TAG, "create rawformats");
                    ArrayList<String> tmp = new ArrayList<>();
                    String[] forms = formats.split(",");
                    for (String s : forms) {
                        if (s.contains(appSettingsManager.getResString(R.string.bayer_)))
                        {
                            tmp.add(s);
                        }
                    }
                    String[] rawFormats = new String[tmp.size()];
                    tmp.toArray(rawFormats);
                    appSettingsManager.rawPictureFormat.setValues(rawFormats);
                }
            }
            appSettingsManager.pictureFormat.setIsSupported(true);

            if (appSettingsManager.getDngProfilesMap() != null && appSettingsManager.getDngProfilesMap().size() > 0)
            {
                Log.d(TAG, "Dng, bayer, jpeg supported");
                appSettingsManager.pictureFormat.setValues(new String[]
                        {
                                appSettingsManager.getResString(R.string.jpeg_),
                                appSettingsManager.getResString(R.string.dng_),
                                appSettingsManager.getResString(R.string.bayer_)
                        });
            }
            else if (appSettingsManager.rawPictureFormat.isSupported()) {
                Log.d(TAG, "bayer, jpeg supported");
                appSettingsManager.pictureFormat.setValues(new String[]{
                        appSettingsManager.getResString(R.string.jpeg_),
                        appSettingsManager.getResString(R.string.bayer_)
                });
            }
            else
            {
                Log.d(TAG, "jpeg supported");
                appSettingsManager.pictureFormat.setValues(new String[]{
                        appSettingsManager.getResString(R.string.jpeg_)
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
        else if (parameters.get(camstring(R.string.hw_exposure_mode_values)) != null)
            detectMode(parameters, R.string.hw_exposure_mode,R.string.hw_exposure_mode_values, appSettingsManager.exposureMode);
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
            /*switch (appSettingsManager.getDevice())
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
            }*/

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
