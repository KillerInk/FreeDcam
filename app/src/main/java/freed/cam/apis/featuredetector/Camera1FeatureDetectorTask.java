package freed.cam.apis.featuredetector;

import android.hardware.Camera;
import android.text.TextUtils;

import com.lge.hardware.LGCameraRef;
import com.troop.freedcam.R;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.cam.apis.featuredetector.camera1.AeBracketDetector;
import freed.cam.apis.featuredetector.camera1.AntibandingDetector;
import freed.cam.apis.featuredetector.camera1.ApertureDetector;
import freed.cam.apis.featuredetector.camera1.AutoHdrDetector;
import freed.cam.apis.featuredetector.camera1.BaseParameter1Detector;
import freed.cam.apis.featuredetector.camera1.ChromaFlashDetector;
import freed.cam.apis.featuredetector.camera1.ColorModeDetector;
import freed.cam.apis.featuredetector.camera1.CorrelatedDoubleSamplingDetector;
import freed.cam.apis.featuredetector.camera1.DenoiseDetector;
import freed.cam.apis.featuredetector.camera1.DigitalImageStabDetector;
import freed.cam.apis.featuredetector.camera1.DualPrimaryCameraDetector;
import freed.cam.apis.featuredetector.camera1.ExposureModeDetector;
import freed.cam.apis.featuredetector.camera1.FlashModeDetector;
import freed.cam.apis.featuredetector.camera1.FocusModeDetector;
import freed.cam.apis.featuredetector.camera1.ImagePostProcessingDetector;
import freed.cam.apis.featuredetector.camera1.IsoModesDetector;
import freed.cam.apis.featuredetector.camera1.JpegQualityMode;
import freed.cam.apis.featuredetector.camera1.LensShadeModeDetector;
import freed.cam.apis.featuredetector.camera1.ManualBrightnessDetector;
import freed.cam.apis.featuredetector.camera1.ManualContrastDetector;
import freed.cam.apis.featuredetector.camera1.ManualExposureDetector;
import freed.cam.apis.featuredetector.camera1.ManualFocusDetector;
import freed.cam.apis.featuredetector.camera1.ManualIsoDetector;
import freed.cam.apis.featuredetector.camera1.ManualSaturationDetector;
import freed.cam.apis.featuredetector.camera1.ManualSharpnessDetector;
import freed.cam.apis.featuredetector.camera1.ManualWhiteBalanceDetector;
import freed.cam.apis.featuredetector.camera1.MemColorEnhancDetector;
import freed.cam.apis.featuredetector.camera1.NonZslManualDetector;
import freed.cam.apis.featuredetector.camera1.OptizoomDetector;
import freed.cam.apis.featuredetector.camera1.PdafDetector;
import freed.cam.apis.featuredetector.camera1.PictureFormatDetector;
import freed.cam.apis.featuredetector.camera1.PictureSizeDetector;
import freed.cam.apis.featuredetector.camera1.PreviewFormatDetector;
import freed.cam.apis.featuredetector.camera1.PreviewFpsDetector;
import freed.cam.apis.featuredetector.camera1.PreviewFpsRangeDetector;
import freed.cam.apis.featuredetector.camera1.PreviewSizeDetector;
import freed.cam.apis.featuredetector.camera1.RdiDetector;
import freed.cam.apis.featuredetector.camera1.ReFocusDetector;
import freed.cam.apis.featuredetector.camera1.SceneModeDetector;
import freed.cam.apis.featuredetector.camera1.SeeMoarDetector;
import freed.cam.apis.featuredetector.camera1.TemporalNoiseReductionDetector;
import freed.cam.apis.featuredetector.camera1.TruePortraitDetector;
import freed.cam.apis.featuredetector.camera1.VideoHdrDetector;
import freed.cam.apis.featuredetector.camera1.VideoHfrDetector;
import freed.cam.apis.featuredetector.camera1.VideoSizeModeDetector;
import freed.cam.apis.featuredetector.camera1.VideoStabDetector;
import freed.cam.apis.featuredetector.camera1.WhiteBalanceModeDetector;
import freed.cam.apis.featuredetector.camera1.ZeroShutterLagDetector;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.cam.apis.featuredetector.camera2.VendorKeyDetector;
import freed.renderscript.RenderScriptManager;
import freed.settings.FrameworkDetector;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;
import freed.utils.Log;
import freed.utils.VideoMediaProfile;


/**
 * Created by troop on 23.01.2017.
 */

public class Camera1FeatureDetectorTask extends AbstractFeatureDetectorTask
{
    private static final  String TAG = Camera1FeatureDetectorTask.class.getSimpleName();

    public Camera1FeatureDetectorTask()
    {
        super();
    }

    @Override
    public List<Class> createParametersToCheckList() {
        List<Class> parameter1Detectors = new ArrayList<>();
        parameter1Detectors.add(PictureFormatDetector.class);
        parameter1Detectors.add(PictureSizeDetector.class);
        parameter1Detectors.add(FocusModeDetector.class);
        parameter1Detectors.add(WhiteBalanceModeDetector.class);
        parameter1Detectors.add(ExposureModeDetector.class);
        parameter1Detectors.add(ColorModeDetector.class);
        parameter1Detectors.add(FlashModeDetector.class);
        parameter1Detectors.add(IsoModesDetector.class);
        parameter1Detectors.add(AntibandingDetector.class);
        parameter1Detectors.add(ImagePostProcessingDetector.class);
        parameter1Detectors.add(PreviewSizeDetector.class);
        parameter1Detectors.add(JpegQualityMode.class);

        parameter1Detectors.add(AeBracketDetector.class);
        parameter1Detectors.add(PreviewFpsDetector.class);
        parameter1Detectors.add(PreviewFormatDetector.class);
        parameter1Detectors.add(SceneModeDetector.class);
        parameter1Detectors.add(LensShadeModeDetector.class);
        parameter1Detectors.add(ZeroShutterLagDetector.class);
        parameter1Detectors.add(MemColorEnhancDetector.class);
        parameter1Detectors.add(VideoSizeModeDetector.class);
        parameter1Detectors.add(CorrelatedDoubleSamplingDetector.class);
        parameter1Detectors.add(VideoHdrDetector.class);
        parameter1Detectors.add(VideoHfrDetector.class);
        parameter1Detectors.add(DigitalImageStabDetector.class);
        parameter1Detectors.add(DenoiseDetector.class);
        parameter1Detectors.add(TemporalNoiseReductionDetector.class);
        parameter1Detectors.add(PdafDetector.class);
        parameter1Detectors.add(SeeMoarDetector.class);
        parameter1Detectors.add(TruePortraitDetector.class);
        parameter1Detectors.add(ReFocusDetector.class);
        parameter1Detectors.add(OptizoomDetector.class);
        parameter1Detectors.add(ChromaFlashDetector.class);
        parameter1Detectors.add(RdiDetector.class);
        parameter1Detectors.add(VideoStabDetector.class);
        parameter1Detectors.add(NonZslManualDetector.class);
        parameter1Detectors.add(PreviewFpsRangeDetector.class);
        parameter1Detectors.add(AutoHdrDetector.class);

        parameter1Detectors.add(ManualSaturationDetector.class);
        parameter1Detectors.add(ManualFocusDetector.class);
        parameter1Detectors.add(ManualSharpnessDetector.class);
        parameter1Detectors.add(ManualBrightnessDetector.class);
        parameter1Detectors.add(ManualContrastDetector.class);
        parameter1Detectors.add(ManualExposureDetector.class);
        parameter1Detectors.add(ManualIsoDetector.class);
        parameter1Detectors.add(ManualWhiteBalanceDetector.class);
        parameter1Detectors.add(DualPrimaryCameraDetector.class);
        parameter1Detectors.add(ApertureDetector.class);
        return parameter1Detectors;
    }

    private String camstring(int id)
    {
        return FreedApplication.getStringFromRessources(id);
    }

    @Override
    public void preDetect() {
        SettingsManager.getInstance().setCamApi(SettingsManager.API_1);
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.Default)
            SettingsManager.getInstance().setFramework(FrameworkDetector.getFramework());
    }

    @Override
    public List<String> findCameraIDs() {
        List<String> cam_ids = new ArrayList<>();
        for (int i = 0; i < 200; i++)
        {
            Camera.Parameters parameters = null;
            try {
                parameters = getParameters(i);
                if (parameters != null)
                    cam_ids.add(String.valueOf(i));
            }
            catch(RuntimeException ex)
            {
                Log.d(TAG, "Failed to get Parameters from Camera:" + i);
            }
        }
        return cam_ids;
    }

    @Override
    public void checkCameraID(int id, List<String> cameraids, List<Class> parametersToDetect) {
        super.checkCameraID(id,cameraids,parametersToDetect);

        Camera.Parameters parameters = null;
        try {
            detectFrontCamera(Integer.parseInt(cameraids.get(id)));
            parameters = getParameters(Integer.parseInt(cameraids.get(id)));
        }
        catch(RuntimeException ex)
        {
            Log.d(TAG, "Failed to get Parameters from Camera:" + id);
        }
        if(parameters == null)
            return;

        for (int i = 0; i < parametersToDetect.size(); i++) {
            try {
                BaseParameterDetector parameter2Detector = getInstance(parametersToDetect.get(i));
                parameter2Detector.checkIfSupported(parameters);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        detectVideoMediaProfiles(id);
        detectQcomFocus(parameters);
    }



    @Override
    public void postDetect() {
        SettingsManager.getInstance().SetCurrentCamera(0);
    }


    private void detectQcomFocus(Camera.Parameters parameters)
    {
        SettingsManager.get(SettingKeys.useQcomFocus).set(parameters.get(camstring(R.string.touch_af_aec))!= null);
    }

    public static String[] createWBStringArray(int min, int max, float step)
    {
        Log.d(TAG,"Create Wbvalues");
        ArrayList<String> t = new ArrayList<>();
        t.add(FreedApplication.getStringFromRessources(R.string.auto_));
        for (int i = min; i<=max;i+=step)
        {
            t.add(i+"");
        }
        return  t.toArray(new String[t.size()]);
    }

    public static String[] createIsoValues(int miniso, int maxiso, int step, boolean xiaomi)
    {
        Log.d(TAG,"Create Isovalues");
        ArrayList<String> s = new ArrayList<>();
        s.add(FreedApplication.getStringFromRessources(R.string.auto_));
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

    public static String[] createManualFocusValues(int min, int max, int step)
    {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(FreedApplication.getStringFromRessources(R.string.auto_));

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


    private LGCameraRef lgCamera = null;
    private Camera.Parameters getParameters(int currentcamera)
    {
        Camera camera = null;
        switch (SettingsManager.getInstance().getFrameWork())
        {
            case LG:
            {
                Log.d(TAG,"Open LG Camera");
                if (SettingsManager.get(SettingKeys.openCamera1Legacy).get())
                    lgCamera = new LGCameraRef(currentcamera, 256);
                if (lgCamera == null || lgCamera.getCamera() == null)
                    lgCamera = new LGCameraRef(currentcamera);
                Camera.Parameters parameters = lgCamera.getCamera().getParameters();
                lgCamera.getCamera().release();
                lgCamera.release();
                lgCamera = null;
                return parameters;
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
                try {
                    if (camera != null)
                        camera.release();
                    camera = Camera.open(currentcamera);
                    Camera.Parameters parameters = camera.getParameters();
                    camera.release();
                    return parameters;
                }
                catch (RuntimeException ex)
                {
                    Log.d(TAG, "unsupported id: " + currentcamera);
                    return null;
                }
            }

        }
    }

    private void detectVideoMediaProfiles(int cameraid)
    {
        final String _720phfr = "720HFR";
        final String _2160p = "2160p";
        final String _2160pDCI = "2160pDCI";
        HashMap<String,VideoMediaProfile> supportedProfiles;
        SupportedVideoProfilesDetector videoProfilesDetector = new SupportedVideoProfilesDetector();
        if(SettingsManager.getInstance().getFrameWork() == Frameworks.LG)
            supportedProfiles =  videoProfilesDetector.getLGVideoMediaProfiles(cameraid);
        else
            supportedProfiles= videoProfilesDetector.getDefaultVideoMediaProfiles(cameraid);

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
    }
}
