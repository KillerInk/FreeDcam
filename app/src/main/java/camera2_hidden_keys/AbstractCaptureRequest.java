package camera2_hidden_keys;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Type;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class AbstractCaptureRequest {

    public static CaptureRequest.Key getKeyType(String string, Type type)
    {
        return (CaptureRequest.Key) ReflectionHelper.getKeyType(string,type, CaptureRequest.Key.class);
    }


    public static <T> CaptureRequest.Key getKeyClass(String string, Class<T> type)
    {
        return (CaptureRequest.Key) ReflectionHelper.getKeyClass(string,type, CaptureRequest.Key.class);
    }
}
