//
// Created by troop on 08.08.2015.
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
#include "ImageProcessor.h"
#define  LOG_TAG    "ImageProcessor"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define GETYPOS(x,y, width) y * width + x;
#define GETUPOS(x,y, width, frameSize) (y/2)*(width/2)+(x/2) + frameSize;
#define GETVPOS(x,y, width, frameSize) (y/2)*(width/2)+(x/2) + frameSize + (frameSize/4);

#define SCALEYUV(v) (((v)+128000)/256000)

static int rcoeff(int y, int u, int v){ return 298082*y +      0*u + 408583*v; }
static int gcoeff(int y, int u, int v){ return 298082*y - 100291*u - 208120*v; }
static int bcoeff(int y, int u, int v){ return 298082*y + 516411*u +      0*v; }





extern "C"
{
    JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_INIT(JNIEnv *env, jobject thiz);
    JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_YUVtoRGB(JNIEnv *env, jobject thiz,jobject handler, jbyteArray yuv420sp, jint width, jint height);
    JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_GetBitmap(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void    JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_Release(JNIEnv *env, jobject thiz,jobject handler);
    JNIEXPORT jobject    JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_GetRgbData(JNIEnv *env, jobject thiz,jobject handler);
    JNIEXPORT jobjectArray    JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_GetHistogram(JNIEnv *env, jobject thiz,jobject handler);
    JNIEXPORT void    JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_ApplyHighPassFilter(JNIEnv *env, jobject thiz,jobject handler);

}


JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_INIT(JNIEnv *env, jobject thiz)
{
    ImageProcessor* rgbContainer = new ImageProcessor();
    return env->NewDirectByteBuffer(rgbContainer, 0);
}

JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_YUVtoRGB(JNIEnv *env, jobject thiz,jobject handler, jbyteArray yuv420sp, jint width, jint height)
{
    unsigned char* yuv = (unsigned char*) env->GetByteArrayElements(yuv420sp,NULL);
    int size = env->GetArrayLength(yuv420sp);
    unsigned char* nativeyuv = new unsigned char[size];
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    memcpy(nativeyuv,yuv, size);
    env->ReleaseByteArrayElements(yuv420sp, (jbyte*)yuv, 0);
    rgbContainer->YuvToRgb(nativeyuv, width, height);
}

JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_GetBitmap(JNIEnv *env, jobject thiz, jobject handler)
{
    //RGBContainer* rgbContainer = (RGBContainer*) env->GetDirectBufferAddress(handler);
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    return rgbContainer->getBitmap(env);
}

JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_Release(JNIEnv *env, jobject thiz,jobject handler)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->Release();
    rgbContainer = NULL;
}

JNIEXPORT jobject    JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_GetRgbData(JNIEnv *env, jobject thiz,jobject handler)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    return rgbContainer->GetData(env);
}

JNIEXPORT jobjectArray    JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_GetHistogram(JNIEnv *env, jobject thiz,jobject handler)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    return rgbContainer->GetHistogramm(env);
}

JNIEXPORT void    JNICALL Java_troop_com_imageconverter_ImageProcessorWrapper_ApplyHighPassFilter(JNIEnv *env, jobject thiz,jobject handler)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    /*int filter[3][3] = {{0,  -1, 0},
                        {-1, 8,  -1},
                        {0,  -1, 0}};
    rgbContainer->Apply3x3Filter(filter);*/

    rgbContainer->applyFocusPeak();
}
