package camera2_hidden_keys;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import freed.utils.Log;

/**
 * Created by KillerInk on 08.12.2017.
 */

public class ReflectionHelper {


    public static Object getKeyType(String string, Type type, Class myclass)
    {
        try {
            Class typedreference = Class.forName("android.hardware.camera2.utils.TypeReference");
            Method method = typedreference.getMethod("createSpecializedTypeReference", Type.class);
            Constructor<?> constructor = myclass.getConstructor(String.class, typedreference);
            constructor.setAccessible(true);
            return constructor.newInstance(string, method.invoke(null, type));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Object getKeyClass(String string, Class<T> type, Class myclass)
    {
        try {
            Constructor<?>[] ctors = myclass.getDeclaredConstructors();
            Constructor<?> constructor =  myclass.getConstructor(String.class, Class.class);

            constructor.setAccessible(true);
            return constructor.newInstance(string,type);
        } catch (InstantiationException e) {
            Log.WriteEx(e);
        } catch (IllegalAccessException e) {
            Log.WriteEx(e);
        } catch (InvocationTargetException e) {
            Log.WriteEx(e);
        }
        catch (IllegalArgumentException e)
        {
            Log.WriteEx(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    private final String tab = "   ";

    private String getTab(int depth)
    {
        StringBuilder ret = new StringBuilder();
        for (int i =0; i< depth; i ++)
            ret.append(tab);
        return ret.toString();
    }

    public void logClass(Class classtoDump,int depth)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getTab(depth) + getAccessType(classtoDump.getModifiers()) + "class " + classtoDump.getSimpleName() + " {\r\n");
        Field[] f = classtoDump.getDeclaredFields();
        if (f.length > 0) {
            for (int i = 0; i < f.length; i++)
                builder.append((createFieldLogString(f[i], depth + 1) + "\r\n"));
            builder.append("\r\n");
        }

        Method[] m = classtoDump.getDeclaredMethods();
        if (m.length > 0)
        {
            for (int i = 0; i < m.length; i++)
                builder.append((createMethodLogString(m[i],depth +1) + "\r\n"));
            builder.append("\r\n");
        }

        depth++;
        Class[] classes = classtoDump.getClasses();
        for (Class cls : classes) {
            logClass(cls, depth);
        }
        depth--;
        builder.append((getTab(depth) +"}\r\n"));
        String t = builder.toString();
        Log.d("ReflectionHelper",t);
    }

    public void dumpClass(Class classtoDump, FileOutputStream outputStream, int depth) throws IOException {

        outputStream.write((getTab(depth) + getAccessType(classtoDump.getModifiers()) + "class " + classtoDump.getSimpleName() + " {\r\n").getBytes());

            Field[] f = classtoDump.getDeclaredFields();
            if (f.length > 0) {
                for (int i = 0; i < f.length; i++)
                    outputStream.write((createFieldLogString(f[i], depth + 1) + "\r\n").getBytes());
                outputStream.write("\r\n".getBytes());
            }

            Method[] m = classtoDump.getDeclaredMethods();
            if (m.length > 0)
            {
                for (int i = 0; i < m.length; i++)
                    outputStream.write((createMethodLogString(m[i],depth +1) + "\r\n").getBytes());
                outputStream.write("\r\n".getBytes());
            }

            depth++;
            Class[] classes = classtoDump.getClasses();
            for (Class cls : classes) {
                dumpClass(cls, outputStream, depth);
            }
            depth--;
        outputStream.write((getTab(depth) +"}\r\n").getBytes());
    }

    private String createMethodLogString(Method method,int depth)
    {
        int mod = method.getModifiers();
        StringBuilder ret = new StringBuilder(getTab(depth));
        ret.append(getAccessType(mod));

        ret.append(method.getReturnType().getSimpleName()).append(" ");
        ret.append(method.getName());
        Class[] parametertypes = method.getParameterTypes();
        ret.append("(");
        for (Class c : parametertypes){
            if (parametertypes[parametertypes.length-1] == c)
                ret.append(c.getSimpleName());
            else
                ret.append(c.getSimpleName()).append(", ");
        }
        ret.append(");");

        return ret.toString();
    }

    private String createFieldLogString(Field field,int depth)
    {
        int mod = field.getModifiers();
        String ret = getTab(depth);
        ret += getAccessType(mod);
        ret += " " + field.getType().getSimpleName() + " ";
        ret += field.getName();

        if (Modifier.isStatic(mod)) {
            try {
                boolean publicfield = Modifier.isPublic(mod);
                if (!publicfield)
                    field.setAccessible(true);
                ret += " = " + field.get(null);
                if (!publicfield)
                    field.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ret += ";";
        return ret;
    }

    private String getAccessType(int mod)
    {
        String ret = "";
        if (Modifier.isPrivate(mod))
            ret =  "private ";
        if (Modifier.isProtected(mod))
            ret +=  "protected ";
        if (Modifier.isPublic(mod))
            ret +=  "public ";
        if (Modifier.isAbstract(mod))
            ret +=  "abstract ";
        if (Modifier.isFinal(mod))
            ret =  "final ";
        if (Modifier.isStatic(mod))
            ret +=  "static ";
        if (Modifier.isNative(mod))
            ret +=  "native ";
        if (Modifier.isSynchronized(mod))
            ret +=  "synchronized ";
        if (Modifier.isStrict(mod))
            ret +=  "strict ";
        return ret;
    }
}
