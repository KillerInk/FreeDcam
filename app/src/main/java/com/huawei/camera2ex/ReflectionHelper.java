package com.huawei.camera2ex;

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

    public static Object getTypeReference(Type type)
    {
        Class typedreference = null;
        try {
            typedreference = Class.forName("android.hardware.camera2.utils.TypeReference");

            Method method = typedreference.getMethod("createSpecializedTypeReference", Type.class);
            return method.invoke(null, type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getKeyType(String string, Type type, Class myclass)
    {
        try {
            Object typeref = ReflectionHelper.getTypeReference(type);
            Constructor<?>[] ctors = myclass.getDeclaredConstructors();
            Constructor<?> constructor = (Constructor<?>) ctors[1];
            constructor.setAccessible(true);
            return constructor.newInstance(string, typeref);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Object getKeyClass(String string, Class<T> type, Class myclass)
    {
        try {
            Constructor<?>[] ctors = myclass.getDeclaredConstructors();
            Constructor<?> constructor = (Constructor<?>) ctors[2];
            constructor.setAccessible(true);
            return constructor.newInstance(string,type);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    private final String tab = "   ";

    private String getTab(int depth)
    {
        String ret = "";
        for (int i =0; i< depth; i ++)
            ret += tab;
        return ret;
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
        String ret = getTab(depth);
        ret += getAccessType(mod);

        ret += method.getReturnType().getSimpleName() + " ";
        ret +=  method.getName();
        Class[] parametertypes = method.getParameterTypes();
        ret+= "(";
        for (Class c : parametertypes){
            if (parametertypes[parametertypes.length-1] == c)
                ret += c.getSimpleName();
            else
                ret += c.getSimpleName()+", ";
        }
        ret+=");";

        return ret;
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
