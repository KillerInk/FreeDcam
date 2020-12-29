//
// Created by troop on 01.03.2018.
//
#include <jni.h>
#include <string.h>
#include <android/bitmap.h>
#include <android/log.h>
#ifndef FREEDCAM_JNIUTILS_H
#define FREEDCAM_JNIUTILS_H

#define  LOG_TAG    "freedcam.JniUtils"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

static char* copyString(JNIEnv* env, jstring input)
{
    const char * fsp = env->GetStringUTFChars(input,NULL);
    int len = env->GetStringLength(input);
    char* out = new char[len];
    strcpy(out, fsp);
    env->ReleaseStringUTFChars(input,fsp);
    return out;
}

static float* copyfloatArray(JNIEnv *env, jfloatArray input)
{
    int size = env->GetArrayLength((jarray)input);
    float * out = new float[size];
    env->GetFloatArrayRegion (input, 0, size, reinterpret_cast<jfloat*>(out));
    return out;
}

static int* copyintArray(JNIEnv *env, jintArray input)
{
    int size = env->GetArrayLength((jarray)input);
    int * out = new int[size];
    env->GetIntArrayRegion (input, 0, size, reinterpret_cast<jint*>(out));
    return out;
}

static unsigned char* copyByteArray(JNIEnv* env, jbyteArray input)
{
    jbyte *data = env->GetByteArrayElements(input, NULL);
    return (unsigned char*) data;
}

static jobject copyToJavaBitmap(JNIEnv * env,unsigned char data[],unsigned int data_size,int width, int height)
{
    void *bitmapPixels;
    int ret;
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass, valueOfBitmapConfigFunction, configName);
    jobject newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction, width, height, bitmapConfig);
    if(data_size > 0) {
        LOGD("orginal size: %i", data_size);

        if ((ret = AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels)) < 0) {
            LOGD("AndroidBitmap_lockPixels() failed ! error=%d", ret);

            return NULL;
        }
        LOGD("pixel locked");
        uint32_t *newBitmapPixels = (uint32_t *) bitmapPixels;
        LOGD("memcopy start");
        int bufrow = 0;
        int size = width * height;
        for (int count = 0; count < size; count++) {
            uint32_t p = (0xff << 24) |
                         (data[bufrow + 2] << 16) |
                         (data[bufrow + 1] << 8) |
                         data[bufrow + 0];
            newBitmapPixels[count] = p;
            bufrow += 3;
        }
        LOGD("memcopy end");


        LOGD("dcraw mem cleared");
        AndroidBitmap_unlockPixels(env, newBitmap);
        LOGD("pixel unlocked");
        return newBitmap;
    }
}

#endif //FREEDCAM_JNIUTILS_H