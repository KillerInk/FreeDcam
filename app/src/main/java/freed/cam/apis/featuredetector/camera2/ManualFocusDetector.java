package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;
import freed.utils.StringFloatArray;
import freed.utils.StringUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ManualFocusDetector extends BaseParameterDetector
{

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectManualFocus(cameraCharacteristics);
    }


    private void detectManualFocus(CameraCharacteristics cameraCharacteristics)
    {
        SettingMode mf = SettingsManager.get(SettingKeys.M_Focus);
        float maxfocusrange = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        if (SettingsManager.getInstance().getCamera2MinFocusPosition() > 0)
            maxfocusrange = SettingsManager.getInstance().getCamera2MinFocusPosition();
        if (maxfocusrange == 0)
        {
            mf.setIsSupported(false);
            return;
        }
        float step = 0.001f;
        List<Float> floats = new ArrayList<>();
        for (float i = step; i < maxfocusrange; i += step)
        {
            floats.add(i);
            if (i > 0.01f)
                step = 0.02f;
            if (i > 0.1f)
                step = 0.1f;
            if (i > 1)
                step = 0.2f;
            if (i + step > maxfocusrange)
                floats.add(maxfocusrange);
        }

        StringFloatArray focusranges = new StringFloatArray(floats.size() + 2);
        focusranges.add(0, FreedApplication.getStringFromRessources(R.string.auto),0f);
        focusranges.add(1,"∞", 0.0001f); //10000m
        int t = 2;
        for (int i = 0; i < floats.size(); i++)
        {
            focusranges.add(t++, StringUtils.getMeterString(1/floats.get(i)),floats.get(i));
        }

        if (focusranges.getSize() > 0) {
            mf.setIsSupported(true);
            mf.setValues(focusranges.getStringArray());
        }
        else {
            mf.setIsSupported(false);
            SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS).setIsSupported(false);
        }
    }
}
