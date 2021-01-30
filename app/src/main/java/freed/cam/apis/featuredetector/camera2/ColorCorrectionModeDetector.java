package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.Arrays;
import java.util.HashMap;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ColorCorrectionModeDetector extends BaseParameterDetector{

    private final String TAG = ColorCorrectionModeDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectColorcorrectionMode(cameraCharacteristics);
    }


    private void detectColorcorrectionMode(CameraCharacteristics cameraCharacteristics)
    {
        int[] colorcor = null;
        if (cameraCharacteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES) != null)
            colorcor = cameraCharacteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES);
        else
            colorcor = new int[]{ 0,1,2};
        Log.d(TAG, "colormodes:" + Arrays.toString(colorcor));
        String[] lookupar = FreedApplication.getContext().getResources().getStringArray(R.array.colorcorrectionmodes);

        HashMap<String,Integer> map = new HashMap<>();
        for (int i = 0;i< colorcor.length;i++)
        {
            if(i < lookupar.length && i < colorcor.length)
                map.put(lookupar[i],colorcor[i]);
        }
        lookupar = StringUtils.IntHashmapToStringArray(map);
        SettingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).setValues(lookupar);
        SettingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).setIsSupported(true);
        SettingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).set(FreedApplication.getStringFromRessources(R.string.fast));
    }
}
