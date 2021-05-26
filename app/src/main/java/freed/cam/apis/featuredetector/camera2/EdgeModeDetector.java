package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EdgeModeDetector extends BaseParameter2Detector {

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        Camera2Util.detectIntMode(cameraCharacteristics, CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES, settingsManager.get(SettingKeys.EDGE_MODE), FreedApplication.getStringArrayFromRessource(R.array.edgeModes),settingsManager);
        String vals[] = settingsManager.get(SettingKeys.EDGE_MODE).getValues();
        String zsldnoise= FreedApplication.getStringFromRessources(R.string.zeroshutterlag);
        boolean containszslstring = false;
        for (int i = 0; i< vals.length; i++)
        {
            if (vals[i].contains(zsldnoise))
                containszslstring = true;
        }

        if (containszslstring) {
            String newvals[] = new String[vals.length - 1];
            int t = 0;
            for (int i = 0; i< vals.length; i++)
            {
                if (!vals[i].contains(zsldnoise))
                    newvals[t++] = vals[i];
            }
            settingsManager.get(SettingKeys.EDGE_MODE).setValues(newvals);
        }

    }
}
