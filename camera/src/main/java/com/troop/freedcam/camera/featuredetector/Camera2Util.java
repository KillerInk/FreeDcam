package com.troop.freedcam.camera.featuredetector;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.settings.mode.SettingMode;
import com.troop.freedcam.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public static void detectIntMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<int[]> requestKey, SettingMode settingMode, String[] lookupar)
    {
        if (SettingsManager.getInstance().hasCamera2Features() && characteristics.get(requestKey) != null) {
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

        }
    }

    public static void detectByteMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<byte[]> requestKey, SettingMode settingMode, String[] lookupar)
    {
        if (SettingsManager.getInstance().hasCamera2Features() && characteristics.get(requestKey) != null) {

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
}
