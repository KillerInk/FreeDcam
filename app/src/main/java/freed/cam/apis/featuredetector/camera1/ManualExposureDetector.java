package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Arrays;

import freed.FreedApplication;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class ManualExposureDetector extends BaseParameter1Detector{

    private final String TAG = ManualExposureDetector.class.getSimpleName();
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualExposureTime(cameraCharacteristics);
    }

    private String[] getSupportedShutterValues(long minMillisec, long maxMiliisec, boolean withautomode) {
        String[] allvalues = FreedApplication.getContext().getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        if (withautomode)
            tmp.add(FreedApplication.getStringFromRessources(R.string.auto_));
        for (int i = 0; i < allvalues.length; i++) {
            String s = allvalues[i];
            if (!s.equals(FreedApplication.getStringFromRessources(R.string.auto_))) {
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

    private void detectManualExposureTime(Camera.Parameters parameters)
    {
        Log.d(TAG, "ManualExposureTime is Presetted: "+ settingsManager.get(SettingKeys.M_EXPOSURE_TIME).isPresetted());
        if (settingsManager.get(SettingKeys.M_EXPOSURE_TIME).isPresetted())
            return;
        //mtk shutter
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "ManualExposureTime MTK");
            settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(true);
            settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.mtk_shutter));
            settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY("m-ss");
            settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(SettingsManager.SHUTTER_MTK);
        }
        else
        {
            //htc shutter
            if (parameters.get(FreedApplication.getStringFromRessources(R.string.shutter)) != null) {
                Log.d(TAG, "ManualExposureTime HTC");
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(true);
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.htc));
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.shutter));
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(SettingsManager.SHUTTER_HTC);
            }
            //lg shutter
            else if (parameters.get(FreedApplication.getStringFromRessources(R.string.lg_shutterspeed_values)) != null) {
                Log.d(TAG, "ManualExposureTime LG");
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(SettingsManager.SHUTTER_LG);
                ArrayList<String> l = new ArrayList(Arrays.asList(parameters.get(FreedApplication.getStringFromRessources(R.string.lg_shutterspeed_values)).replace(",0", "").split(",")));
                l.remove(0);
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(l.toArray(new String[l.size()]));
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.lg_shutterspeed));
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(true);
            }
            //meizu shutter
            else if (parameters.get("shutter-value") != null) {
                Log.d(TAG, "ManualExposureTime Meizu");
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(true);
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.shutter_values_meizu));
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY("shutter-value");
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(SettingsManager.SHUTTER_MEIZU);
            }
            //kirin shutter
            else if (parameters.get("hw-sensor-exposure-time-range") != null) {
                try {
                    Log.d(TAG, "ManualExposureTime huawei");
                    settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(true);
                    String[] split = parameters.get("hw-sensor-exposure-time-range").split(",");//=1/4000,30"
                    String[] split2 = split[0].split("/");
                    float a = (Float.parseFloat(split2[0]) / Float.parseFloat(split2[1])) * 1000000f;
                    long min = (long) a;
                    long max;
                    if (split2[1].contains("/")) {
                        String[] split3 = split2[1].split("/");
                        float tmp = (Float.parseFloat(split3[0]) / Float.parseFloat(split3[1])) * 1000000f;
                        max = (long) tmp;
                    } else
                        max = Long.parseLong(split[1]) * 1000000;

                    String[] values = getSupportedShutterValues(min, max, true);
                    settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(values);
                    settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY("hw-sensor-exposure-time");
                    settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(SettingsManager.SHUTTER_KRILLIN);
                }
                catch (NumberFormatException ex)
                {
                    Log.WriteEx(ex);
                    settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(false);
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    Log.WriteEx(ex);
                    settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(false);
                }
            }
            //sony shutter
            else if (parameters.get("sony-max-shutter-speed") != null) {
                Log.d(TAG, "ManualExposureTime Sony");
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(true);
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(getSupportedShutterValues(
                        Long.parseLong(parameters.get("sony-min-shutter-speed")),
                        Long.parseLong(parameters.get("sony-max-shutter-speed")),
                        true));
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY("sony-shutter-speed");
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(SettingsManager.SHUTTER_SONY);
            }
            //qcom shutter
            else if (parameters.get(camstring(R.string.max_exposure_time)) != null && parameters.get(camstring(R.string.min_exposure_time)) != null) {
                long min = 0, max = 0;
                try {
                    if (parameters.get(camstring(R.string.max_exposure_time)).contains(".")) {
                        Log.d(TAG, "ManualExposureTime Qcom Millisec");
                        min = (long) (Double.parseDouble(parameters.get(camstring(R.string.min_exposure_time))) * 1000);
                        max = (long) (Double.parseDouble(parameters.get(camstring(R.string.max_exposure_time))) * 1000);
                        settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(SettingsManager.SHUTTER_QCOM_MILLISEC);
                    } else {
                        Log.d(TAG, "ManualExposureTime Qcom MicroSec");
                        min = Integer.parseInt(parameters.get(camstring(R.string.min_exposure_time)));
                        max = Integer.parseInt(parameters.get(camstring(R.string.max_exposure_time)));
                        settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setType(SettingsManager.SHUTTER_QCOM_MICORSEC);
                    }
                    if (max > 0) {

                        settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(true);
                        settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setCamera1ParameterKEY(camstring(R.string.exposure_time));
                        settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(getSupportedShutterValues(min, max, true));
                    }
                }
                catch (NumberFormatException ex)
                {
                    Log.WriteEx(ex);
                    settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(false);
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    Log.WriteEx(ex);
                    settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(false);
                }
            }
        }
    }
}
