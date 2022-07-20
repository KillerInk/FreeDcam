package camera2_hidden_keys;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.os.Build;
import android.util.Rational;

import androidx.annotation.RequiresApi;

import org.chickenhook.restrictionbypass.RestrictionBypass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import freed.utils.BufferedTextFileWriter;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VendorKeyTestLog {

    private static final String TAG = VendorKeyTestLog.class.getSimpleName();
    private final CameraCharacteristics characteristics;
    private final CaptureResult result;
    private final CaptureRequest captureRequest;

    private final HashMap<String,CaptureRequest.Key> captureRequestKeys;
    private final HashMap<String,CaptureResult.Key> captureResultKeys;
    private final HashMap<String,CameraCharacteristics.Key> characteristicsKeys;
    private final VendorKeyParser vendorKeyParser;

    public VendorKeyTestLog(VendorKeyParser vendorKeyParser, CameraCharacteristics characteristics, CaptureResult result, CaptureRequest captureRequest)
    {
        this.captureRequest = captureRequest;
        this.result = result;
        this.characteristics = characteristics;
        captureResultKeys = new HashMap<>();
        captureRequestKeys = new HashMap<>();
        characteristicsKeys = new HashMap<>();
        this.vendorKeyParser = vendorKeyParser;
    }

    public void testKeys()
    {
        List<CameraCharacteristics.Key>  c_keys = null;
        try {
            c_keys = vendorKeyParser.getVendorKeys(characteristics,CameraCharacteristics.Key.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        BufferedTextFileWriter bufferedTextFileWriter = null; //= new BufferedTextFileWriter("characteristics_dump");
        for (CameraCharacteristics.Key b : c_keys)
        {
            checkIsCharacteristics(b,null);
        }
        //bufferedTextFileWriter.close();

        if (result != null) {
            List<CaptureResult.Key>  cres_keys = null;
            try {
                cres_keys = vendorKeyParser.getVendorKeys(characteristics, CaptureResult.Key.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //bufferedTextFileWriter = new BufferedTextFileWriter("result_dump");
            for (CaptureResult.Key b : cres_keys) {
                checkIsResult(b,null);
            }
            //bufferedTextFileWriter.close();
        }

        if (captureRequest != null) {
            List<CaptureRequest.Key>  creq_keys = null;
            try {
                creq_keys = vendorKeyParser.getVendorKeys(characteristics, CaptureRequest.Key.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //bufferedTextFileWriter = new BufferedTextFileWriter("request_dump");
            for (CaptureRequest.Key b : creq_keys) {
                checkIsRequest(b,null);
            }
            //bufferedTextFileWriter.close();
        }

        bufferedTextFileWriter = new BufferedTextFileWriter("CameraCharacteristicsDump");
        writeCharacteristicsJavaFile(bufferedTextFileWriter);
        bufferedTextFileWriter.close();

        bufferedTextFileWriter = new BufferedTextFileWriter("CaptureResultDump");
        writeResultJavaFile(bufferedTextFileWriter);
        bufferedTextFileWriter.close();

        bufferedTextFileWriter = new BufferedTextFileWriter("CaptureRequestDump");
        writeRequestJavaFile(bufferedTextFileWriter);
        bufferedTextFileWriter.close();
    }

    private <T> void checkIsCharacteristics(CameraCharacteristics.Key<?> key, BufferedTextFileWriter bufferedTextFileWriter)
    {
        if (characteristics != null)
        {
            try {
                T ret = (T) characteristics.get(key);
                if (ret != null)
                {
                   /* Object t = getKeyType(key);
                    bufferedTextFileWriter.writeLine(key.getName() + " " + getObjectType(t) + "\n" + getObjectString(ret));*/
                    //Log.d(TAG, "CameraCharacteristics " + key.getName() + " " + getObjectType(t) + "\n" + getObjectString(ret));
                    characteristicsKeys.put(key.getName(),key);
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    private void checkIsResult(CaptureResult.Key key,BufferedTextFileWriter bufferedTextFileWriter)
    {
        if (result != null)
        {
            try {
                Object ret = result.get(key);
                if (ret != null)
                {
                    //Log.d(TAG, "CaptureResult " + key.getName() + "\n" + getObjectString(ret));
                   /* Object t = getKeyType(key);
                    bufferedTextFileWriter.writeLine(key.getName()  + " " + getObjectType(t) +  "\n" + getObjectString(ret));*/
                    captureResultKeys.put(key.getName(),key);
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    private void checkIsRequest(CaptureRequest.Key key,BufferedTextFileWriter bufferedTextFileWriter)
    {
        if (captureRequest != null)
        {
            try {
                Object ret = captureRequest.get(key);
                if (ret != null)
                {
                    //Log.d(TAG, "CaptureResult " + key.getName() + "\n" + getObjectString(ret));
                    /*Object t = getKeyType(key);
                    bufferedTextFileWriter.writeLine(key.getName()  + " " + getObjectType(t) +  "\n" + getObjectString(ret));*/
                    captureRequestKeys.put(key.getName(),key);
                }
                else
                {
                    if (characteristicsKeys.get(key.getName()) == null && captureResultKeys.get(key.getName()) == null)
                    {
                       /* Object t = getKeyType(key);
                        bufferedTextFileWriter.writeLine(key.getName()  + " " + getObjectType(t));*/
                        captureRequestKeys.put(key.getName(),key);
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    private <K,M> Type getKeyType(K key)
    {
        Type mTypeInstance = null;
        M mKeyInstance = null;
        Field mKey = null;
        Method mgetType = null;
        try {
            mKey = RestrictionBypass.getDeclaredField(key.getClass(),"mKey");
            mKey.setAccessible(true);
            mKeyInstance = (M) mKey.get(key);
            mgetType = RestrictionBypass.getDeclaredMethod(mKeyInstance.getClass(),"getType");
            mTypeInstance = (Type) mgetType.invoke(mKeyInstance);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return mTypeInstance;
    }

    private <T> String getObjectString(T o)
    {
        if (o instanceof int[])
            return Arrays.toString((int[])o);
        else if (o instanceof byte[])
            return Arrays.toString((byte[])o);
        else if (o instanceof float[])
            return Arrays.toString((float[])o);
        else if (o instanceof long[])
            return Arrays.toString((long[])o);
        else if (o instanceof Rational)
            return ((Rational)o).toString();
        else
            return o.toString();
    }

    private <T> String getObjectType(T o)
    {
        if      (o.toString().equals("class [I"))
            return "int[]";
        else if (o.toString().equals("class I"))
            return "int";
        else if (o.toString().equals("class [B"))
            return "byte[]";
        else if (o.toString().equals("class B"))
            return "Byte";
        else if (o.toString().equals("class [F"))
            return "float[]";
        else if (o.toString().equals("class F"))
            return "Float";
        else if (o.toString().equals("class [D"))
            return "double[]";
        else if (o.toString().equals("class D"))
            return "double";
        else if (o.toString().equals("class [J"))
            return "long[]";
        else if (o.toString().equals("class J"))
            return "long";
        else if (o == Rational.class)
            return "Rational.class";
        else
            return o.getClass().getSimpleName();
    }

    private <T> void writeCharacteristicsJavaFile(BufferedTextFileWriter bufferedTextFileWriter)
    {
        String head = "package camera2_hidden_keys;\n" +
                "import android.hardware.camera2.CameraCharacteristics;\n" +
                "import android.os.Build;\n" +
                "import androidx.annotation.RequiresApi;\n" +
                "import camera2_hidden_keys.AbstractCameraCharacteristics;\n" +
                "\n" +
                "@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)\n";
        head += "public class CameraCharacteristicsDump extends AbstractCameraCharacteristics\n" +
                "{";
        bufferedTextFileWriter.writeLine(head);
        List<CameraCharacteristics.Key> keys = new ArrayList<>(characteristicsKeys.values());
        java.util.Collections.sort(keys, new Comparator<CameraCharacteristics.Key>() {
            @Override
            public int compare(CameraCharacteristics.Key o1, CameraCharacteristics.Key o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (CameraCharacteristics.Key k : keys)
        {
            T ret = (T) characteristics.get(k);
            if (ret != null)
            {
                bufferedTextFileWriter.writeLine(addCommentLine(getObjectString(ret)));

                Type t = getKeyType(k);
                String name = k.getName().replace(".","_").replace("-","_");
                bufferedTextFileWriter.writeLine(addCharacteristicsStaticLine(getObjectType(t),name));
            }
        }
        bufferedTextFileWriter.writeLine("static {");

        for (CameraCharacteristics.Key k : keys)
        {
            Object t = getKeyType(k);
            bufferedTextFileWriter.writeLine(writeKeyInstance(k.getName(),getObjectType(t)));
        }

        bufferedTextFileWriter.writeLine("}");
        bufferedTextFileWriter.writeLine("}");
    }

    private <T> void writeResultJavaFile(BufferedTextFileWriter bufferedTextFileWriter)
    {
        String head = "package camera2_hidden_keys;\n" +
                "import android.hardware.camera2.CaptureResult;\n" +
                "import android.os.Build;\n" +
                "import androidx.annotation.RequiresApi;\n" +
                "import camera2_hidden_keys.AbstractCaptureResult;\n" +
                "\n" +
                "@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)\n";
        head += "public class CaptureResultDump extends AbstractCaptureResult\n" +
                "{";
        bufferedTextFileWriter.writeLine(head);
        List<CaptureResult.Key> keys = new ArrayList<>(captureResultKeys.values());
        java.util.Collections.sort(keys, new Comparator<CaptureResult.Key>() {
            @Override
            public int compare(CaptureResult.Key o1, CaptureResult.Key o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (CaptureResult.Key k : keys)
        {
            T ret = (T) result.get(k);
            if (ret != null)
            {
                bufferedTextFileWriter.writeLine(addCommentLine(getObjectString(ret)));
                String name = k.getName().replace(".","_").replace("-","_");
                Object t = getKeyType(k);
                bufferedTextFileWriter.writeLine(addResultStaticLine(getObjectType(t),name));
            }
        }
        bufferedTextFileWriter.writeLine("static {");

        for (CaptureResult.Key k : keys)
        {
            Object t = getKeyType(k);
            bufferedTextFileWriter.writeLine(writeKeyInstance(k.getName(),getObjectType(t)));
        }

        bufferedTextFileWriter.writeLine("}");
        bufferedTextFileWriter.writeLine("}");
    }

    private <T> void writeRequestJavaFile(BufferedTextFileWriter bufferedTextFileWriter)
    {
        String head = "package camera2_hidden_keys;\n" +
                "import android.hardware.camera2.CaptureRequest;\n" +
                "import android.os.Build;\n" +
                "import androidx.annotation.RequiresApi;\n" +
                "import camera2_hidden_keys.AbstractCaptureRequest;\n" +
                "\n" +
                "@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)\n";
        head += "public class CaptureRequestDump extends AbstractCaptureRequest\n" +
                "{";
        bufferedTextFileWriter.writeLine(head);
        List<CaptureRequest.Key> keys = new ArrayList<>(captureRequestKeys.values());
        java.util.Collections.sort(keys, new Comparator<CaptureRequest.Key>() {
            @Override
            public int compare(CaptureRequest.Key o1, CaptureRequest.Key o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (CaptureRequest.Key k : keys)
        {
            T ret = (T) captureRequest.get(k);
            if (ret != null)
            {
                bufferedTextFileWriter.writeLine(addCommentLine(getObjectString(ret)));

                Object t = getKeyType(k);
                String name = k.getName().replace(".","_").replace("-","_");
                bufferedTextFileWriter.writeLine(addRequestStaticLine(getObjectType(t),name));
            }
            else
            {
                Object t = getKeyType(k);
                bufferedTextFileWriter.writeLine(addRequestStaticLine(getObjectType(t),k.getName().replace(".","_")));
            }
        }
        bufferedTextFileWriter.writeLine("static {");

        for (CaptureRequest.Key k : keys)
        {
            Object t = getKeyType(k);
            bufferedTextFileWriter.writeLine(writeKeyInstance(k.getName(),getObjectType(t)));
        }

        bufferedTextFileWriter.writeLine("}");
        bufferedTextFileWriter.writeLine("}");
    }

    private String addCharacteristicsStaticLine(String type, String name)
    {
        return "public static final CameraCharacteristics.Key<"+type+"> " +name+ ";";
    }

    private String addResultStaticLine(String type, String name)
    {
        return "public static final CaptureResult.Key<"+type+"> " +name+ ";";
    }

    private String addRequestStaticLine(String type, String name)
    {
        return "public static final CaptureRequest.Key<"+type+"> " +name+ ";";
    }

    private String addCommentLine(String text)
    {
        return "//" + text;
    }

    private String writeKeyInstance(String name, String type)
    {
        return name.replace(".","_").replace("-","_") + "= getKeyType(\""+name+"\", " + type+".class);";
    }
}
