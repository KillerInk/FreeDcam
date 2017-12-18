package com.huawei.camera2ex;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
}
