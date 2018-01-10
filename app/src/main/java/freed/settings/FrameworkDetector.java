package freed.settings;

import android.os.Build;

import java.lang.reflect.Method;

import freed.utils.Log;

/**
 * Created by KillerInk on 10.01.2018.
 */

public class FrameworkDetector {
    private static String TAG = FrameworkDetector.class.getSimpleName();
    private static boolean hasLGFramework()
    {
        try {
            Class c = Class.forName("com.lge.hardware.LGCameraRef");
            Log.d(TAG, "Has Lg Framework");
            c = Class.forName("com.lge.media.CamcorderProfileEx");
            Log.d(TAG, "Has Lg Framework");
            return true;

        } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {

            Log.d(TAG, "No LG Framework");
            return false;
        }
    }

    private static boolean isMotoExt()
    {
        try {
            Class c = Class.forName("com.motorola.android.camera.CameraMotExt");
            Log.d(TAG, "Has Moto Framework");
            c = Class.forName("com.motorola.android.media.MediaRecorderExt");
            Log.d(TAG, "Has Moto Framework");
            return true;

        } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {
            Log.d(TAG, "No Moto Framework");
            return false;
        }
    }

    private static boolean isMTKDevice()
    {
        try
        {
            Class camera = Class.forName("android.hardware.Camera");
            Method[] meths = camera.getMethods();
            Method app = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setProperty"))
                    app = m;
            }
            if (app != null) {
                Log.d(TAG,"MTK Framework found");
                return true;
            }
            Log.d(TAG, "MTK Framework not found");
            return false;
        }
        catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e)
        {
            Log.WriteEx(e);
            Log.d(TAG, "MTK Framework not found");
            return false;
        }
    }

    private static boolean isSonyCameraEx()
    {
        try {
            System.loadLibrary("cameraextensionjni");
            return true;
        }
        catch (RuntimeException ex)
        {
            Log.d(TAG, "no sony camera extension");
            return false;
        }
        catch (UnsatisfiedLinkError ex)
        {
            Log.d(TAG, "no sony camera extension");
            return false;
        }
    }

    public static Frameworks getFramework()
    {
        if (hasLGFramework())
            return Frameworks.LG;
        else if (isMTKDevice())
            return Frameworks.MTK;
        else if (Build.MANUFACTURER.contains("Xiaomi"))
            return Frameworks.Xiaomi;
        else if (isMotoExt())
            return Frameworks.Moto_Ext;
        else if(isSonyCameraEx())
            return Frameworks.SonyCameraExtension;
        else
            return Frameworks.Default;
    }

}
