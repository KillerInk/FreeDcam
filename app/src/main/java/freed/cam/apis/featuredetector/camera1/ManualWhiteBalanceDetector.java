package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;
import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera1FeatureDetectorTask;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class ManualWhiteBalanceDetector extends BaseParameter1Detector{

    private final String TAG = ManualWhiteBalanceDetector.class.getSimpleName();
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualWhiteBalance(cameraCharacteristics);
    }

    private boolean arrayContainsString(String[] ar,String dif)
    {
        if (ar == null)
            return false;
        boolean ret = false;
        for (String s: ar)
            if (s.equals(dif))
                ret = true;
        return ret;
    }

    private void detectManualWhiteBalance(Camera.Parameters parameters) {
        if (settingsManager.get(SettingKeys.M_Whitebalance).isPresetted())
            return;
        if (settingsManager.getFrameWork() == Frameworks.MTK)
            settingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(false);
        else if (settingsManager.get(SettingKeys.M_Whitebalance).isSupported()) // happens when its already set due supportedevices.xml
            return;
        else
        {
            // looks like wb-current-cct is loaded when the preview is up. this could be also for the other parameters
            String wbModeval ="", wbmax = "",wbmin = "";

            if (parameters.get(FreedApplication.getStringFromRessources(R.string.max_wb_cct)) != null) {
                wbmax = FreedApplication.getStringFromRessources(R.string.max_wb_cct);
            }
            else if (parameters.get(FreedApplication.getStringFromRessources(R.string.max_wb_ct))!= null)
                wbmax = FreedApplication.getStringFromRessources(R.string.max_wb_ct);

            if (parameters.get(FreedApplication.getStringFromRessources(R.string.min_wb_cct))!= null) {
                wbmin = FreedApplication.getStringFromRessources(R.string.min_wb_cct);
            } else if (parameters.get(FreedApplication.getStringFromRessources(R.string.min_wb_ct))!= null)
                wbmin = FreedApplication.getStringFromRessources(R.string.min_wb_ct);

            if (arrayContainsString(settingsManager.get(SettingKeys.WhiteBalanceMode).getValues(), FreedApplication.getStringFromRessources(R.string.manual)))
                wbModeval = FreedApplication.getStringFromRessources(R.string.manual);
            else if (arrayContainsString(settingsManager.get(SettingKeys.WhiteBalanceMode).getValues(), FreedApplication.getStringFromRessources(R.string.manual_cct)))
                wbModeval = FreedApplication.getStringFromRessources(R.string.manual_cct);

            try {
                if (!TextUtils.isEmpty(wbmax) && !TextUtils.isEmpty(wbmin) && !TextUtils.isEmpty(wbModeval)) {
                    Log.d(TAG, "Found all wbct values:" +wbmax + " " + wbmin + " " +wbModeval);
                    settingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(true);
                    settingsManager.get(SettingKeys.M_Whitebalance).setMode(wbModeval);
                    int min = Integer.parseInt(parameters.get(wbmin));
                    int max = Integer.parseInt(parameters.get(wbmax));
                    settingsManager.get(SettingKeys.M_Whitebalance).setValues(Camera1FeatureDetectorTask.createWBStringArray(min,max,100));
                }
                else {
                    Log.d(TAG, "Failed to lookup wbct:" + " " +wbmax + " " + wbmin + " " +wbModeval);
                    settingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(false);
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(false);
            }

        }
    }
}
