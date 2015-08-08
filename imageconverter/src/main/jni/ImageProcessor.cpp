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
#define  LOG_TAG    "freedcam.YuvMergeNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)



class RGBContainer
{
public:
    int _width, _height;
    unsigned char* _data;
    JNIEnv *env;
    RGBContainer()
    {
    }
    void YuvToRgb(unsigned char* yuyv_image, jint width, jint height);
    jobject getBitmap(JNIEnv *env);
    void Release();


};

void RGBContainer::YuvToRgb(unsigned char* yuyv_image, jint width, jint height) {

    _width = width;
    _height = height;
    //_data  =(unsigned char *)malloc(_width*_height*3);
    int y;
    int cr;
    int cb;

    double r;
    double g;
    double b;
    LOGD("Start ConvertRGBtoYuv");
    for (int i = 0, j = 0; i < _width * _height * 3; i += 6, j += 4) {
        //first pixel
        y = yuyv_image[j];
        cb = yuyv_image[j + 1];
        cr = yuyv_image[j + 3];

        r = y + (1.4065 * (cr - 128));
        g = y - (0.3455 * (cb - 128)) - (0.7169 * (cr - 128));
        b = y + (1.7790 * (cb - 128));

        //This prevents colour distortions in your rgb image
        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        _data[i] = (unsigned char) r;
        _data[+1] = (unsigned char) g;
        _data[i + 2] = (unsigned char) b;

        //second pixel
        y = yuyv_image[j + 2];
        cb = yuyv_image[j + 1];
        cr = yuyv_image[j + 3];

        r = y + (1.4065 * (cr - 128));
        g = y - (0.3455 * (cb - 128)) - (0.7169 * (cr - 128));
        b = y + (1.7790 * (cb - 128));

        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        _data[i + 3] = (unsigned char) r;
        _data[+4] = (unsigned char) g;
        _data[i + 5] = (unsigned char) b;
        LOGD("Done ConvertRGBtoYuv");
    }
}


jobject RGBContainer::getBitmap(JNIEnv * env) {
    void *bitmapPixels;
    int ret;
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap",
                                                            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf",
                                                                   "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass,
                                                       valueOfBitmapConfigFunction, configName);
    jobject newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction, _width,
                                                    _height, bitmapConfig);

    if ((ret = AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels)) < 0) {
        LOGD("AndroidBitmap_lockPixels() failed ! error=%d", ret);

        return NULL;
    }
    LOGD("pixel locked");
    memcpy(_data, newBitmap, sizeof(uint32_t) * (_width * _height));
    LOGD("memcopy start");
    LOGD("memcopy end");

    AndroidBitmap_unlockPixels(env, newBitmap);
    free(_data);

    return newBitmap;
}

void RGBContainer::Release() {
    if (_data != NULL)
        free(_data);

}

extern "C"
{
    JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessor_INIT(JNIEnv *env, jobject thiz);
    JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessor_YUVtoRGB(JNIEnv *env, jobject thiz, jbyteArray yuv420sp, jint width, jint height);
    JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessor_GetBitmap(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void    JNICALL Java_troop_com_imageconverter_ImageProcessor_Release(JNIEnv *env, jobject thiz);
}

static RGBContainer* rgbContainer;

JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessor_INIT(JNIEnv *env, jobject thiz)
{
    rgbContainer = new RGBContainer();
    return env->NewDirectByteBuffer(rgbContainer, 0);
}

JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessor_YUVtoRGB(JNIEnv *env, jobject thiz, jbyteArray yuv420sp, jint width, jint height)
{
    rgbContainer->YuvToRgb((unsigned char*) env->GetByteArrayElements(yuv420sp,NULL), width, height);
}

JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessor_GetBitmap(JNIEnv *env, jobject thiz, jobject handler)
{
    //RGBContainer* rgbContainer = (RGBContainer*) env->GetDirectBufferAddress(handler);
    return rgbContainer->getBitmap(env);
}

JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessor_Release(JNIEnv *env, jobject thiz)
{
    //RGBContainer* rgbContainer = (RGBContainer*) env->GetDirectBufferAddress(handler);
    rgbContainer->Release();
}
