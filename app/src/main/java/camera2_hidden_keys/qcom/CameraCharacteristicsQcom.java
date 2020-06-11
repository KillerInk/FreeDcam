package camera2_hidden_keys.qcom;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCameraCharacteristics;
import camera2_hidden_keys.ReflectionHelper;

import java.lang.reflect.Type;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraCharacteristicsQcom extends AbstractCameraCharacteristics
{
    public static final CameraCharacteristics.Key<int[]> sharpness_range;
    public static final CameraCharacteristics.Key<int[]> saturation_range;

    static {
        sharpness_range = getKeyType("org.codeaurora.qcamera3.sharpness.range", int[].class);
        saturation_range = getKeyType("org.codeaurora.qcamera3.saturation.range", int[].class);
    }


}
