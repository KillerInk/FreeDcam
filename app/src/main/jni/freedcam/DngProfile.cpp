//
// Created by troop on 01.03.2018.
//

#include <jni.h>
#include "DngProfile.h"
#include "JniUtils.h"

extern "C"
{
    JNIEXPORT jobject JNICALL Java_freed_dng_DngProfile_init(JNIEnv *env, jobject thiz) {
        DngProfile *dngProfile = new DngProfile();
        return env->NewDirectByteBuffer(dngProfile, 0);
    }

    JNIEXPORT void JNICALL Java_freed_dng_DngProfile_clear(JNIEnv *env, jobject thiz, jobject javaHandler)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        dngProfile->clear();
        delete dngProfile;
    }

    JNIEXPORT void JNICALL Java_freed_dng_DngProfile_setDngInfo(JNIEnv *env, jobject thiz,  jobject javaHandler, jint blacklevel,jint whitelevel,jint widht, jint height, jint rawType, jstring bayerPattern, int rowsize)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        dngProfile->blacklevel = new float[4];
        for (int i = 0; i < 4; ++i) {
            dngProfile->blacklevel[i] = blacklevel;
        }
        dngProfile->whitelevel = whitelevel;
        dngProfile->rawwidht = widht;
        dngProfile->rawheight = height;
        dngProfile->rowSize = rowsize;
        dngProfile->rawType = rawType;
        dngProfile->bayerformat = copyString(env,bayerPattern);
    }

    JNIEXPORT jlong JNICALL Java_freed_dng_DngProfile_getWhitelvl(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        return  dngProfile->whitelevel;
    }

    JNIEXPORT jint JNICALL Java_freed_dng_DngProfile_getBlacklvl(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        return  (int)dngProfile->blacklevel[0];
    }
    JNIEXPORT jint JNICALL Java_freed_dng_DngProfile_getRawType(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        return  dngProfile->rawType;
    }

    JNIEXPORT jint JNICALL Java_freed_dng_DngProfile_getWidth(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        return  dngProfile->rawwidht;
    }
    JNIEXPORT jint JNICALL Java_freed_dng_DngProfile_getHeight(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        return  dngProfile->rawheight;
    }

    JNIEXPORT jint JNICALL Java_freed_dng_DngProfile_getRowSize(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        return  dngProfile->rowSize;
    }

    JNIEXPORT jstring JNICALL Java_freed_dng_DngProfile_getBayerPattern(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        return  env->NewStringUTF(dngProfile->bayerformat);
    }
};