
//
// Created by GeorgeKiarie on 14/04/2016.
//
#include <jni.h>
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#define  LOG_TAG    "freedcam.Staxxer"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#include <errno.h>


extern "C"
{
#include <jerror.h>
#include <jpeglib.h>
#include <jmorecfg.h>
    JNIEXPORT jobject JNICALL Java_com_freedcam_Native_StaxxerJNI_Create(JNIEnv *env, jobject thiz);
    JNIEXPORT jbyteArray JNICALL Java_com_freedcam_Native_StaxxerJNI_GetRGB(JNIEnv *env,jobject thiz, jbyteArray fromCamera, jint Length);
    JNIEXPORT jbyteArray JNICALL Java_com_freedcam_Native_StaxxerJNI_GetMerged(JNIEnv *env, jobject thiz,jobject handler);
    JNIEXPORT void JNICALL Java_com_freedcam_Native_StaxxerJNI_StoreMerged(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fromRS);
    JNIEXPORT void JNICALL Java_com_freedcam_Native_StaxxerJNI_Release(JNIEnv *env, jobject thiz, jobject handler);


}

class RGBExtractor
{
public:



    unsigned char* Native_Buffer;


};

    static int jpgerr = 0;

    static void jpg_error_exit(j_common_ptr cinfo) {
    jpgerr = errno ? errno : -1; }

     unsigned char clamp(int c) {
        if (c < 0) {
            return 0;
        } else if (c > 255) {

            return 255;
        } else {
             return (unsigned char) c;
        }      }

     unsigned char stretch(int c, int min, int max) {
        return clamp((c - min) * 255 / (max - min)); }




JNIEXPORT jobject JNICALL Java_com_freedcam_Native_StaxxerJNI_Create(JNIEnv *env, jobject thiz)
{
    RGBExtractor *rgbExtractor = new RGBExtractor();
    return env->NewDirectByteBuffer(rgbExtractor, 0);
}

JNIEXPORT jbyteArray JNICALL Java_com_freedcam_Native_StaxxerJNI_GetRGB(JNIEnv *env, jobject thiz,jbyteArray fromCamera, jint Length)
{
    unsigned char* dIN = new unsigned char[Length];
    memcpy(dIN, env->GetByteArrayElements(fromCamera,NULL), Length);

    LOGD("GetRGB: Enter Method");

    struct jpeg_decompress_struct info;
    struct jpeg_error_mgr derr;

    info.err = jpeg_std_error(&derr);

    jpeg_create_decompress(&info); //fills info structure
    jpeg_mem_src(&info, dIN,Length);
    jpeg_read_header(&info, TRUE); //int
    jpeg_start_decompress(&info);
    int w = info.output_width;
    int h = info.output_height;
    int numChannels = info.num_components; // 3 = RGB, 4 = RGBA
    unsigned long dataSize = w * h * numChannels;
    unsigned char *data = (unsigned char *) malloc(dataSize);
    if (!data)
    	return NULL;
    unsigned char* rowptr;
    while (info.output_scanline < h)
    {
        rowptr = data + info.output_scanline * w * numChannels;
        jpeg_read_scanlines(&info, &rowptr, 1);
    }
    jpeg_finish_decompress(&info);
    jbyteArray result;
    result = env->NewByteArray(dataSize);
    (env)->SetByteArrayRegion(result, 0, dataSize,reinterpret_cast<jbyte*>( data));
    free(data);
    if(dIN != NULL)
    {
        free(dIN);
        dIN = NULL;
    }
    return result;
}


JNIEXPORT void JNICALL Java_com_freedcam_Native_StaxxerJNI_StoreMerged(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fromRS)
{
    LOGD("StoreMerged: Enter Method");
    RGBExtractor* rgbExtractor = (RGBExtractor*) env->GetDirectBufferAddress(handler);
    rgbExtractor->Native_Buffer = new unsigned char[env->GetArrayLength(fromRS)];
    memcpy(rgbExtractor->Native_Buffer, env->GetByteArrayElements(fromRS,NULL), env->GetArrayLength(fromRS));
}

JNIEXPORT jbyteArray JNICALL Java_com_defcomk_jni_staxxer_StaxxerJNI_GetMerged(JNIEnv *env, jobject thiz, jobject handler)
{
    LOGD("GetMerged: Enter Method");

    RGBExtractor* rgbExtractor = (RGBExtractor*) env->GetDirectBufferAddress(handler);
    jbyteArray result;
    result = env->NewByteArray(sizeof(rgbExtractor->Native_Buffer));
    (env)->SetByteArrayRegion(result, 0, sizeof(rgbExtractor->Native_Buffer),reinterpret_cast<jbyte*>( rgbExtractor->Native_Buffer));
    return result;
}

JNIEXPORT void JNICALL Java_com_defcomk_jni_staxxer_StaxxerJNI_Release(JNIEnv *env, jobject thiz, jobject handler)
{
    RGBExtractor* rgbExtractor = (RGBExtractor*) env->GetDirectBufferAddress(handler);
    if(rgbExtractor->Native_Buffer != NULL)
    {
        free(rgbExtractor->Native_Buffer);
        rgbExtractor->Native_Buffer = NULL;
    }

    if (rgbExtractor != NULL)
    free(rgbExtractor);
    rgbExtractor = NULL;
}