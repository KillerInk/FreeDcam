package Camera2EXT;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Keys {
  //  public final CaptureRequest EIS_MODE;

    public Keys(CaptureRequest eis_mode) {
        //EIS_MODE = (CaptureRequest.Key);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String A (Object object)
    {
        if(object instanceof CameraCharacteristics.Key)
        {
            return ((CameraCharacteristics.Key)object).getName();
        }
        else if(object instanceof CaptureRequest.Key)
        {
            return ((CaptureRequest.Key)object).getName();
        }
        else if (object instanceof CaptureResult.Key)
        {
            return ((CaptureResult.Key)object).getName();
        }
        else return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Object B (ArrayList arrayList, String string)
    {
        if(arrayList == null) return null;

        if(TextUtils.isEmpty((CharSequence)(string)))
        {
            return null;
        }

        int  a = arrayList.size() -1;
        while (a >= 0)
        {
            Object object= arrayList.get(a);
            if(TextUtils.equals((CharSequence)A(object),(CharSequence)string))
            {
                arrayList.remove(a);
                return object;
            }

            --a;
        }
        return null;

    }

    public ArrayList C(Class serial,CameraMetadata cameraMetadata)
    {
        if(Build.VERSION.SDK_INT < 26)
            return getAllVendorKeys((Class) serial);
        return D(serial,cameraMetadata);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList D(Class serial, CameraMetadata object)
    {
        try {
            Field field = Class.forName("android.hardware.camera2.CameraMetadata").getDeclaredField("mNativeInstance");
            field.setAccessible(true);
            object = (CameraMetadata) field.get(object);
            return (ArrayList)Class.forName("android.hardware.camera2.impl.CameraMetadataNative").getMethod("getAllVendorKeys",Class.class).invoke(object,serial);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

 /*   public ArrayList e (Class serial, CameraMetadata object,String string)
    {
        if( ( object = C(serial,object) ) == null)
        {

        }
    }
    */

    public ArrayList getAllVendorKeys(Class serial)
    {
        try {
            return (ArrayList)Class.forName("android.hardware.camera2.impl.CameraMetadataNative").getMethod("getAllVendorKeys",Class.class).invoke(null,serial);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
