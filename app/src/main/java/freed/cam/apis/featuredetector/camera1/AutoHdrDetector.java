package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;
import android.text.TextUtils;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class AutoHdrDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectAutoHdr(cameraCharacteristics);
    }

    private void detectAutoHdr(Camera.Parameters parameters) {
        if (settingsManager.get(SettingKeys.HDR_MODE).isPresetted())
            return;
        if (parameters.get(camstring(R.string.auto_hdr_supported))!=null){
            settingsManager.get(SettingKeys.HDR_MODE).setIsSupported(false);
            return;
        }
        try {
            String autohdr = parameters.get(camstring(R.string.auto_hdr_supported));
            if (autohdr != null
                    && !TextUtils.isEmpty(autohdr)
                    && autohdr.equals(camstring(R.string.true_))
                    && parameters.get(camstring(R.string.auto_hdr_enable)) != null) {

                List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(FreedApplication.getStringFromRessources(R.string.scene_mode_values)).split(",")));

                List<String> hdrVals = new ArrayList<>();
                hdrVals.add(camstring(R.string.off_));

                if (Scenes.contains(camstring(R.string.scene_mode_hdr))) {
                    hdrVals.add(camstring(R.string.on_));
                }
                if (Scenes.contains(camstring(R.string.scene_mode_asd))) {
                    hdrVals.add(camstring(R.string.auto_));
                }
                settingsManager.get(SettingKeys.HDR_MODE).setValues(hdrVals.toArray(new String[hdrVals.size()]));
                settingsManager.get(SettingKeys.HDR_MODE).setIsSupported(true);
                settingsManager.get(SettingKeys.HDR_MODE).setType(1);
            }
        }
        catch (NumberFormatException ex)
        {
            Log.WriteEx(ex);
            settingsManager.get(SettingKeys.HDR_MODE).setIsSupported(false);
        }
        catch(ArrayIndexOutOfBoundsException ex)
        {
            Log.WriteEx(ex);
            settingsManager.get(SettingKeys.HDR_MODE).setIsSupported(false);
        }
    }
}
