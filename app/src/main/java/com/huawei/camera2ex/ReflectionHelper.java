package com.huawei.camera2ex;

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

    public static String dumpClass(Class classtoDump)
    {
        String ret = "############## " + classtoDump.getSimpleName() + " ##############\r\n";
        Method[] m = classtoDump.getDeclaredMethods();
        ret += "############## Methods ##############\r\n";
        for (int i = 0; i < m.length; i++)
            ret += createMethodLogString(m[i]) + "\r\n";
        Field[] f = classtoDump.getDeclaredFields();
        ret += "############## Fields ##############\r\n";
        for (int i = 0; i < f.length; i++)
            ret += createFieldLogString(f[i]) +"\r\n";
        return ret;
    }

    private static String createMethodLogString(Method method)
    {
        int mod = method.getModifiers();
        String ret = getAccessType(mod);
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

    private static String createFieldLogString(Field field)
    {
        int mod = field.getModifiers();
        String ret = getAccessType(mod);
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

    private static String getAccessType(int mod)
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
