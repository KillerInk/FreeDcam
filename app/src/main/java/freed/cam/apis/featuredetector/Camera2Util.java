package freed.cam.apis.featuredetector;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;
import freed.utils.Log;
import freed.utils.StringUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Util
{
    public static Size[] getOutputSizeForImageFormat(int[] arraysize, int imgFormat)
    {
        List<Size> sizes =new ArrayList<>();
        for (int i = 0; i< arraysize.length;i+=4)
        {
            if (arraysize[i] == imgFormat) {
                Size s = new Size(arraysize[i + 1], arraysize[i + 2]);
                if (!sizes.contains(s))
                    sizes.add(s);
            }
        }
        return sizes.toArray(new Size[sizes.size()]);
    }

    public static void detectIntMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<int[]> requestKey, SettingMode settingMode, String[] lookupar,SettingsManager settingsManager)
    {
        if (settingsManager.hasCamera2Features() && characteristics.get(requestKey) != null) {
            int[]  scenes = characteristics.get(requestKey);
            if (scenes.length >1)
                settingMode.setIsSupported(true);
            else {
                settingMode.setIsSupported(false);
                return;
            }
            HashMap<String,Integer> map = new HashMap<>();
            for (int i = 0; i< scenes.length; i++)
            {
                int t = scenes[i];
                if (t <lookupar.length)
                    map.put(lookupar[t], t);
            }
            lookupar = StringUtils.IntHashmapToStringArray(map);
            settingMode.setValues(lookupar);
            if (map.get("off") != null)
                settingMode.set("off");
            else if (map.get("Auto") != null)
                settingMode.set("Auto");
            else if (map.get("Fast") != null)
                settingMode.set("Fast");
            else if (map.get("Off") != null)
                settingMode.set("Off");
            else if (map.get("Disable") != null)
                settingMode.set("Disable");
        }
    }

    public static void detectByteMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<byte[]> requestKey, SettingMode settingMode, String[] lookupar,SettingsManager settingsManager)
    {
        if (settingsManager.hasCamera2Features() && characteristics.get(requestKey) != null) {

            byte[] scenes = characteristics.get(requestKey);
            if (scenes == null ||scenes.length == 0) {
                settingMode.setIsSupported(false);
                return;
            }
            HashMap<String,Integer> map = new HashMap<>();
            for (int i = 0; i< scenes.length; i++)
            {
                map.put(lookupar[i], (int)scenes[i]);
            }
            settingMode.setIsSupported(true);
            settingMode.set(lookupar[0]);
            lookupar = StringUtils.IntHashmapToStringArray(map);
            settingMode.setValues(lookupar);

        }
    }

    public static void dumpIntArray(int[] array,String TAG,String name)
    {
        if (array != null)
            Log.d(TAG, name +":"+ Arrays.toString(array));
        else
            Log.d(TAG, name +": isNull");
    }
}
