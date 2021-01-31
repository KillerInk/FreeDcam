package camera2_hidden_keys;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.chickenhook.restrictionbypass.RestrictionBypass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VendorKeyParser
{

    private List<CaptureRequest.Key> availiblekeys;

    public VendorKeyParser()
    {
        availiblekeys = new ArrayList<>();
    }

    public void readVendorKeys(CameraCharacteristics cameraCharacteristics) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList keys = null;
        if (get_mProperties() != null)
        {
            keys = getAllvendorKeysApi29(cameraCharacteristics);
        }
        else if (getNativeCopy() != null)
        {
            keys = getAllvendorKeysApi26(cameraCharacteristics);
        }

        if (keys != null)
        {
            for (Object b : keys)
            {
                availiblekeys.add((CaptureRequest.Key) b);
            }
        }
    }

    public List<CaptureRequest.Key> getRequests() {
        return availiblekeys;
    }


    private ArrayList getAllvendorKeysApi29(CameraCharacteristics characteristics) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Field mPropertiesField = get_mProperties();
        mPropertiesField.setAccessible(true);
        Object  mProperties = mPropertiesField.get(characteristics);
        Method getAllVendorKeys = RestrictionBypass.getDeclaredMethod(mProperties.getClass(), "getAllVendorKeys", Class.class);
        getAllVendorKeys.setAccessible(true);
        //seems to be equal wich key.class we use, it returns always all key for CameraCharateristics, CaptureRequest and CaptureResult
        return (ArrayList) getAllVendorKeys.invoke(mProperties, CaptureRequest.Key.class);
    }

    private ArrayList getAllvendorKeysApi26(CameraCharacteristics characteristics) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getNativeCopy = getNativeCopy();
        getNativeCopy.setAccessible(true);
        Object metadata = getNativeCopy.invoke(characteristics,null);
        Method getAllVendorKeys = RestrictionBypass.getDeclaredMethod(metadata.getClass(),"getAllVendorKeys",Class.class);
        getAllVendorKeys.setAccessible(true);
        //seems to be equal wich key.class we use, it returns always all key for CameraCharateristics, CaptureRequest and CaptureResult
        return (ArrayList) getAllVendorKeys.invoke(metadata, CaptureRequest.Key.class);
    }

    private Field get_mProperties() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return RestrictionBypass.getDeclaredField(CameraCharacteristics.class, "mProperties");
    }

    private Method getNativeCopy() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return RestrictionBypass.getDeclaredMethod(CameraCharacteristics.class, "getNativeCopy",null);
    }


}