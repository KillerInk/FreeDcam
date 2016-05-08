//
// Created by troop on 08.08.2015.
//


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
    JNIEXPORT jobject JNICALL Java_com_imageconverter_ImageProcessorWrapper_INIT(JNIEnv *env, jobject thiz);
    JNIEXPORT jobject JNICALL Java_com_imageconverter_ImageProcessorWrapper_DrawToSurface(JNIEnv *env, jobject thiz,jobject handler, jobject nativewindow);
    JNIEXPORT jobject JNICALL Java_com_imageconverter_ImageProcessorWrapper_DrawToBitmap(JNIEnv *env, jobject thiz,jobject handler, jobject bitmap);
    JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_YUVtoRGB(JNIEnv *env, jobject thiz,jobject handler, jbyteArray yuv420sp, jint width, jint height);
    JNIEXPORT jobject JNICALL Java__com_imageconverter_ImageProcessorWrapper_GetBitmap(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void    JNICALL Java_com_imageconverter_ImageProcessorWrapper_Release(JNIEnv *env, jobject thiz,jobject handler);
    JNIEXPORT jobject    JNICALL Java_com_imageconverter_ImageProcessorWrapper_GetRgbData(JNIEnv *env, jobject thiz,jobject handler);
    JNIEXPORT jobjectArray JNICALL Java_com_imageconverter_ImageProcessorWrapper_GetHistogram(JNIEnv *env, jobject thiz,jobject handler);
    JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_ApplyHighPassFilter(JNIEnv *env, jobject thiz,jobject handler);

    JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_unpackRAWtoARGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename);
    JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_unpackRAWtoRGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename);
    JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_loadJPEGtoARGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename);
    JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_loadJPEGtoRGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename);
    JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_stackAverageJPEGtoARGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename);

}


JNIEXPORT jobject JNICALL Java_com_imageconverter_ImageProcessorWrapper_INIT(JNIEnv *env, jobject thiz)
{
    ImageProcessor* rgbContainer = new ImageProcessor();
    return env->NewDirectByteBuffer(rgbContainer, 0);
}

JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_Release(JNIEnv *env, jobject thiz,jobject handler)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->Release();
    rgbContainer = NULL;
}

JNIEXPORT jobject JNICALL Java_com_imageconverter_ImageProcessorWrapper_DrawToSurface(JNIEnv *env, jobject thiz,jobject handler, jobject nativewindow)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->DrawToSurface(env, nativewindow);
}

JNIEXPORT jobject JNICALL Java_com_imageconverter_ImageProcessorWrapper_DrawToBitmap(JNIEnv *env, jobject thiz,jobject handler, jobject bitmap)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->DrawToBitmap(env, bitmap);
}

JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_YUVtoRGB(JNIEnv *env, jobject thiz,jobject handler, jbyteArray yuv420sp, jint width, jint height)
{
    unsigned char* yuv = (unsigned char*) env->GetByteArrayElements(yuv420sp,NULL);
    int size = env->GetArrayLength(yuv420sp);
    unsigned char* nativeyuv = new unsigned char[size];
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    memcpy(nativeyuv,yuv, size);
    env->ReleaseByteArrayElements(yuv420sp, (jbyte*)yuv, 0);
    rgbContainer->YuvToRgb(nativeyuv, width, height);
}

JNIEXPORT jobject JNICALL Java_com_imageconverter_ImageProcessorWrapper_GetBitmap(JNIEnv *env, jobject thiz, jobject handler)
{
    //RGBContainer* rgbContainer = (RGBContainer*) env->GetDirectBufferAddress(handler);
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    return rgbContainer->getBitmap(env);
}



JNIEXPORT jobject    JNICALL Java_com_imageconverter_ImageProcessorWrapper_GetRgbData(JNIEnv *env, jobject thiz,jobject handler)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    return rgbContainer->GetData(env);
}

JNIEXPORT jobjectArray    JNICALL Java_com_imageconverter_ImageProcessorWrapper_GetHistogram(JNIEnv *env, jobject thiz,jobject handler)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    return rgbContainer->GetHistogramm(env);
}

JNIEXPORT void    JNICALL Java_com_imageconverter_ImageProcessorWrapper_ApplyHighPassFilter(JNIEnv *env, jobject thiz,jobject handler)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    /*int filter[3][3] = {{0,  -1, 0},
                        {-1, 8,  -1},
                        {0,  -1, 0}};
    rgbContainer->Apply3x3Filter(filter);*/

    rgbContainer->applyFocusPeak();
}

JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_unpackRAWtoARGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->unpackRAWToRGBA(env,jfilename);
}

JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_unpackRAWtoRGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->unpackRAWToRGB(env,jfilename);
}


JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_loadJPEGtoARGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename)
{
    LOGD("loadJPEGtoARGB");
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->loadJPEGToRGBA(env,jfilename);
}

JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_loadJPEGtoRGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename)
{
    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->loadJPEGToRGB(env,jfilename);
}

JNIEXPORT void JNICALL Java_com_imageconverter_ImageProcessorWrapper_stackAverageJPEGtoARGB(JNIEnv * env, jobject obj,jobject handler, jstring jfilename)
{

    ImageProcessor* rgbContainer = (ImageProcessor*)env->GetDirectBufferAddress(handler);
    rgbContainer->StackAverageJPEGToARGB(env,jfilename);
}
