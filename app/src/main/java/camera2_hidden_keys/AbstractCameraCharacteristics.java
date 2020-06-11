package camera2_hidden_keys;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Type;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class AbstractCameraCharacteristics {

    public static CameraCharacteristics.Key getKeyType(String string, Type type)
    {
        return (CameraCharacteristics.Key) ReflectionHelper.getKeyType(string,type,CameraCharacteristics.Key.class);
    }


    public static <T> CameraCharacteristics.Key getKeyClass(String string, Class<T> type)
    {

        return (CameraCharacteristics.Key) ReflectionHelper.getKeyClass(string,type,CameraCharacteristics.Key.class);
    }
}
