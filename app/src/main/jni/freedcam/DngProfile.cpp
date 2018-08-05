//
// Created by troop on 01.03.2018.
//

#include <jni.h>
#include "DngProfile.h"
#include "JniUtils.h"
#include <android/log.h>


#define  LOG_TAG    "freedcam.DngProfile"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

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

    JNIEXPORT void JNICALL Java_freed_dng_DngProfile_setActiveArea(JNIEnv *env, jobject thiz,  jobject javaHandler, jintArray input)
    {
        DngProfile* dngProfile = (DngProfile*)env->GetDirectBufferAddress(javaHandler);
        unsigned int *data = (unsigned int*)env->GetIntArrayElements(input, NULL);
        LOGD("activearea: %i %i %i %i", data[0],data[1],data[2],data[3]);
        unsigned int  xmin = data[0];
        unsigned int  ymin =  data[1];
        unsigned int width = data[2];
        unsigned int  height =data[3];
        unsigned int * activearea = new  unsigned int[4];
        activearea[0] = ymin;
        activearea[1] = xmin;
        activearea[2] = ymin + height;
        activearea[3] = xmin + width;
        LOGD("activearea: %i %i %i %i", activearea[0],activearea[1],activearea[2],activearea[3]);
        dngProfile->activearea = activearea;
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