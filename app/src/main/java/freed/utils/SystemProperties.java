package freed.utils;

import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created by GeorgeKiarie on 6/17/2016.
 */
public class SystemProperties {

    private static Method sGet;
    private static Method sGetBoolean;
    private static volatile boolean sIsInit;
    private static Class<?> sSystemPropertiesClass;

    private SystemProperties() {
    }

    public static String get(String s, String s2) {
        String s3 = s2;
        try {
            SystemProperties.init();
            Object object = sGet.invoke(null, s);
            if (object != null && object instanceof String) {
                s3 = (String)object;
            }
        }
        catch (Exception e) {
            Logger.e("WrapSystemProperties", "Failed to get system properties: " + e.getMessage());
        }
        if (!TextUtils.isEmpty(s3)) return s3;
        return s2;
    }

    private static void init() throws ClassNotFoundException, NoSuchMethodException {
        if (sIsInit) return;
        synchronized (SystemProperties.class) {
            if (sIsInit) return;
            if (sSystemPropertiesClass == null) {
                sSystemPropertiesClass = Class.forName("android.os.SystemProperties");
            }
            if (sGet == null && sSystemPropertiesClass != null) {
                sGet = sSystemPropertiesClass.getMethod("get", String.class);
            }
            if (sGetBoolean == null && sSystemPropertiesClass != null) {
                Class class_ = sSystemPropertiesClass;
                Class[] arrclass = new Class[]{String.class, Boolean.TYPE};
                sGetBoolean = class_.getMethod("getBoolean", arrclass);
            }
            sIsInit = true;
            return;
        }
    }
}
