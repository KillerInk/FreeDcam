//
// Created by troop on 01.03.2018.
//

#include <jni.h>
#include "ExifInfo.h"

extern "C"
{
    JNIEXPORT jobject JNICALL Java_freed_jni_ExifInfo_init(JNIEnv *env, jobject thiz)
    {
        ExifInfo * writer = new ExifInfo();
        return env->NewDirectByteBuffer(writer, 0);
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_clear(JNIEnv *env, jobject thiz, jobject javaHandler)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->clear();
        delete exifInfo;
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_SetIso(JNIEnv *env, jobject thiz,jobject javaHandler,
            jint iso)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->_iso= iso;
    }

    JNIEXPORT jint JNICALL Java_freed_jni_ExifInfo_GetIso(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        return exifInfo->_iso;
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_SetFlash(JNIEnv *env, jobject thiz,jobject javaHandler,
            jint flash)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->_flash= flash;
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_SetExposureTime(JNIEnv *env, jobject thiz,jobject javaHandler,
            jdouble exposuretime)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->_exposure = exposuretime;
    }

    JNIEXPORT jdouble JNICALL Java_freed_jni_ExifInfo_GetExposureTime(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        return exifInfo->_exposure;
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_SetFocalLength(JNIEnv *env, jobject thiz,jobject javaHandler,
            jdouble focal)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->_focallength = focal;
    }

    JNIEXPORT jdouble JNICALL Java_freed_jni_ExifInfo_GetFocalLength(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        return exifInfo->_focallength;
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_SetFnumber(JNIEnv *env, jobject thiz,jobject javaHandler,
                                                              jdouble fnum)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->_fnumber = fnum;
    }

    JNIEXPORT jdouble JNICALL Java_freed_jni_ExifInfo_GetFnumber(JNIEnv *env, jobject thiz,jobject javaHandler)

    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        return exifInfo->_fnumber;
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_SetExposureIndex(JNIEnv *env, jobject thiz,jobject javaHandler,
            jfloat expoIndex)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->_exposureIndex = expoIndex;
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_SetImageDescription(JNIEnv *env, jobject thiz,jobject javaHandler,
            jstring imagedesc)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->_imagedescription = copyString(env,imagedesc);
    }

    JNIEXPORT void JNICALL Java_freed_jni_ExifInfo_SetOrientation(JNIEnv *env, jobject thiz,jobject javaHandler,
            jstring orientaiton)
    {
        ExifInfo* exifInfo = (ExifInfo*)env->GetDirectBufferAddress(javaHandler);
        exifInfo->_orientation = copyString(env,orientaiton);
    }
};
