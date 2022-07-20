package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.HashMap;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.utils.StringUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SceneModeDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectSceneModes(cameraCharacteristics);
    }

    private void detectSceneModes(CameraCharacteristics characteristics){
        String[] lookupar = FreedApplication.getContext().getResources().getStringArray(R.array.sceneModes);
        int[]  scenes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
        settingsManager.get(SettingKeys.SCENE_MODE).setIsSupported(scenes.length > 1);

        HashMap<String,Integer> map = new HashMap<>();
        for (int i = 0; i< scenes.length; i++)
        {
            switch (scenes[i])
            {
                case CameraCharacteristics.CONTROL_SCENE_MODE_DISABLED:
                    map.put(lookupar[0], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_FACE_PRIORITY:
                    map.put(lookupar[1], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_ACTION:
                    map.put(lookupar[2], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_PORTRAIT:
                    map.put(lookupar[3], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_LANDSCAPE:
                    map.put(lookupar[4], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT:
                    map.put(lookupar[5], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT_PORTRAIT:
                    map.put(lookupar[6], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_THEATRE:
                    map.put(lookupar[7], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_BEACH:
                    map.put(lookupar[8], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_SNOW:
                    map.put(lookupar[9], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_SUNSET:
                    map.put(lookupar[10], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_STEADYPHOTO:
                    map.put(lookupar[11], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_FIREWORKS:
                    map.put(lookupar[12], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_SPORTS:
                    map.put(lookupar[13], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_PARTY:
                    map.put(lookupar[14], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_CANDLELIGHT:
                    map.put(lookupar[15], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_BARCODE:
                    map.put(lookupar[16], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO:
                    map.put(lookupar[17], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_HDR:
                    map.put(lookupar[18], scenes[i]);
                    break;
            }
        }
        lookupar = StringUtils.IntHashmapToStringArray(map);
        settingsManager.get(SettingKeys.SCENE_MODE).setValues(lookupar);
    }
}
