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
public class EdgeModeDetector extends BaseParameterDetector{

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        Camera2Util.detectIntMode(cameraCharacteristics, CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES, SettingsManager.get(SettingKeys.EDGE_MODE), FreedApplication.getStringArrayFromRessource(R.array.edgeModes));
        String vals[] = SettingsManager.get(SettingKeys.EDGE_MODE).getValues();
        String newvals[] = new String[vals.length-1];
        String zsldnoise= FreedApplication.getStringFromRessources(R.string.zeroshutterlag);
        int t = 0;
        for (int i = 0; i< vals.length; i++)
        {
            if (!vals[i].contains(zsldnoise))
                newvals[t++] = vals[i];
        }
        SettingsManager.get(SettingKeys.EDGE_MODE).setValues(newvals);
    }
}
