package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class CorrelatedDoubleSamplingDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectMode(cameraCharacteristics, R.string.cds_mode,R.string.cds_mode_values, settingsManager.get(SettingKeys.CDS_Mode));
    }
}
