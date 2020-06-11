package camera2_hidden_keys;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureResult;
import android.os.Build;

import java.lang.reflect.Type;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public abstract class AbstractCaptureResult {

    public static CaptureResult.Key getKeyType(String string, Type type)
    {
        return (CaptureResult.Key) ReflectionHelper.getKeyType(string,type,CaptureResult.Key.class);
    }

    //public android.hardware.camera2.CameraCharacteristics$Key(java.lang.String,java.lang.Class)

    public static <T> CaptureResult.Key getKeyClass(String string, Class<T> type)
    {
        return (CaptureResult.Key) ReflectionHelper.getKeyClass(string,type,CaptureResult.Key.class);
    }
}
