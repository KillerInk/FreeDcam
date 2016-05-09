//
// Created by troop on 13.08.2015.
//
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#include <unistd.h>
#include <android/bitmap.h>
#include <android/native_window.h> // requires ndk r5 or newer
#include <android/native_window_jni.h> // requires ndk r5 or newer
#define  LOG_TAG    "surface_native_draw"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern "C"
{
    JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_SurfaceNativeDrawActivity_drawFromNative(JNIEnv *env, jobject thiz, jintArray input, jobject surface,jint w, jint h);
}

JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_SurfaceNativeDrawActivity_drawFromNative(JNIEnv *env, jobject thiz, jintArray input, jobject surface, jint w, jint h)
{
    int* bmp = (int*) env->GetIntArrayElements(input,NULL);
    int size = env->GetArrayLength(input);
    int* native = new int[size];
    memcpy(native,bmp, w*h* sizeof(int));
    env->ReleaseIntArrayElements(input, (int*)bmp, 0);

    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_Buffer buffer;
    if (ANativeWindow_lock(window, &buffer, NULL) == 0) {
        memcpy(buffer.bits,native, w*h* sizeof(int));
    }
    ANativeWindow_unlockAndPost(window);

    ANativeWindow_release(window);
    delete [] native;
}
