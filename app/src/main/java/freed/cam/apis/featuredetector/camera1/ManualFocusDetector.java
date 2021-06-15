package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera1FeatureDetectorTask;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class ManualFocusDetector extends BaseParameter1Detector{

    private final String TAG = ManualFocusDetector.class.getSimpleName();
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualFocus(cameraCharacteristics);
    }

    private void detectManualFocus(Camera.Parameters parameters) {
        Log.d(TAG, "mf is preseted:" + settingsManager.get(SettingKeys.M_Focus).isPresetted());
        if (settingsManager.get(SettingKeys.M_Focus).isPresetted())
            return;

        int min =0, max =0, step = 0;
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            settingsManager.get(SettingKeys.M_Focus).setMode(camstring(R.string.manual));
            settingsManager.get(SettingKeys.M_Focus).setType(-1);
            settingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
            min = 0;
            max = 1023;
            step = 10;
            settingsManager.get(SettingKeys.M_Focus).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.afeng_pos));
            Log.d(TAG, "MF MTK");
        }
        else {
            //lookup old qcom

            try {
                if (parameters.get(camstring(R.string.manual_focus_modes)) == null) {

                    if (parameters.get(camstring(R.string.max_focus_pos_index)) != null
                            && parameters.get(camstring(R.string.min_focus_pos_index))!= null
                            && settingsManager.get(SettingKeys.FocusMode).contains(camstring(R.string.manual))) {

                        settingsManager.get(SettingKeys.M_Focus).setMode(camstring(R.string.manual));
                        settingsManager.get(SettingKeys.M_Focus).setType(1);
                        settingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
                        min = Integer.parseInt(parameters.get(camstring(R.string.min_focus_pos_index)));
                        max = Integer.parseInt(parameters.get(camstring(R.string.max_focus_pos_index)));
                        step = 10;
                        settingsManager.get(SettingKeys.M_Focus).setCamera1ParameterKEY(camstring(R.string.manual_focus_position));
                        Log.d(TAG, "MF old qcom");
                    }
                }
                else
                {
                    //lookup new qcom
                    if (parameters.get(camstring(R.string.max_focus_pos_ratio)) != null
                            && parameters.get(camstring(R.string.min_focus_pos_ratio)) != null
                            && settingsManager.get(SettingKeys.FocusMode).contains(camstring(R.string.manual))) {

                        settingsManager.get(SettingKeys.M_Focus).setMode(camstring(R.string.manual));
                        settingsManager.get(SettingKeys.M_Focus).setType(2);
                        settingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
                        min = Integer.parseInt(parameters.get(camstring(R.string.min_focus_pos_ratio)));
                        max = Integer.parseInt(parameters.get(camstring(R.string.max_focus_pos_ratio)));
                        step = 1;
                        settingsManager.get(SettingKeys.M_Focus).setCamera1ParameterKEY(camstring(R.string.manual_focus_position));
                        Log.d(TAG, "MF new qcom");
                    }
                }
                //htc mf
                if (parameters.get(camstring(R.string.min_focus)) != null && parameters.get(camstring(R.string.max_focus)) != null)
                {
                    settingsManager.get(SettingKeys.M_Focus).setMode("");
                    settingsManager.get(SettingKeys.M_Focus).setType(-1);
                    settingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_focus)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_focus)));
                    step = 1;
                    settingsManager.get(SettingKeys.M_Focus).setCamera1ParameterKEY(camstring(R.string.focus));
                    Log.d(TAG, "MF HTC");
                }

                //huawai mf
                if(parameters.get(FreedApplication.getStringFromRessources(R.string.hw_vcm_end_value)) != null && parameters.get(FreedApplication.getStringFromRessources(R.string.hw_vcm_start_value)) != null)
                {
                    Log.d(TAG,"Huawei MF");
                    settingsManager.get(SettingKeys.M_Focus).setMode(camstring(R.string.manual));
                    settingsManager.get(SettingKeys.M_Focus).setType(-1);
                    settingsManager.get(SettingKeys.M_Focus).setIsSupported(true);
                    max = Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.hw_vcm_end_value)));
                    min = Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.hw_vcm_start_value)));
                    Log.d(TAG,"min/max mf:" + min+"/"+max);
                    step = 10;
                    settingsManager.get(SettingKeys.M_Focus).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.hw_manual_focus_step_value));
                }


            } catch(NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_Focus).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_Focus).setIsSupported(false);
            }

        }
        //create mf values
        if (settingsManager.get(SettingKeys.M_Focus).isSupported())
            settingsManager.get(SettingKeys.M_Focus).setValues(Camera1FeatureDetectorTask.createManualFocusValues(min, max,step));

    }
}
