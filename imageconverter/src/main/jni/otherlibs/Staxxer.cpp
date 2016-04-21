
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
#include <../libjpeg/jpeg-9b/jerror.h>
#include <../libjpeg/jpeg-9b/jpeglib.h>
#include <../libjpeg/jpeg-9b/jmorecfg.h>
#include <../libjpeg/jpeg-9b/include/jconfig.h>
    JNIEXPORT jobject JNICALL Java_jni_staxxer_StaxxerJNI_Create(JNIEnv *env, jobject thiz);
    JNIEXPORT jbyteArray JNICALL Java_jni_staxxer_StaxxerJNI_GetRGB(JNIEnv *env,jobject thiz, jbyteArray fromCamera, jint Length);
    JNIEXPORT jbyteArray JNICALL Java_jni_staxxer_StaxxerJNI_GetMerged(JNIEnv *env, jobject thiz,jobject handler);
    JNIEXPORT void JNICALL Java_jni_staxxer_StaxxerJNI_StoreMerged(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fromRS);
    JNIEXPORT void JNICALL Java_jni_staxxer_StaxxerJNI_Release(JNIEnv *env, jobject thiz, jobject handler);


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




JNIEXPORT jobject JNICALL Java_jni_staxxer_StaxxerJNI_Create(JNIEnv *env, jobject thiz)
{
    RGBExtractor *rgbExtractor = new RGBExtractor();
    return env->NewDirectByteBuffer(rgbExtractor, 0);
}

JNIEXPORT jbyteArray JNICALL Java_jni_staxxer_StaxxerJNI_GetRGB(JNIEnv *env, jobject thiz,jbyteArray fromCamera, jint Length)
{
    unsigned char* dIN = new unsigned char[Length];
    memcpy(dIN, env->GetByteArrayElements(fromCamera,NULL), Length);

LOGD("GetRGB: Enter Method");

LOGD("Line %d",__LINE__);
    	struct jpeg_decompress_struct info;
LOGD("Line %d",__LINE__);
    	struct jpeg_error_mgr derr;
LOGD("Line %d",__LINE__);

    	info.err = jpeg_std_error(&derr);
LOGD("Line %d",__LINE__);

    	jpeg_create_decompress(&info); //fills info structure
LOGD("Line %d",__LINE__);
        jpeg_mem_src(&info, dIN,Length);
LOGD("Line %d",__LINE__);
        jpeg_read_header(&info, TRUE); //int
LOGD("Line %d",__LINE__);
    LOGD("Line %d",__LINE__);
    jpeg_start_decompress(&info);
LOGD("Line %d",__LINE__);
    	int w = info.output_width;
LOGD("Line %d",__LINE__);
    	int h = info.output_height;
LOGD("Line %d",__LINE__);
    	int numChannels = info.num_components; // 3 = RGB, 4 = RGBA
LOGD("Line %d",__LINE__);
    	unsigned long dataSize = w * h * numChannels;
LOGD("Line %d",__LINE__);

    	unsigned char *data = (unsigned char *) malloc(dataSize);
LOGD("Line %d",__LINE__);
    	if (!data)
    		return NULL;
LOGD("Line %d",__LINE__);

    	unsigned char* rowptr;
LOGD("Line %d",__LINE__);
    	while (info.output_scanline < h) {
    		rowptr = data + info.output_scanline * w * numChannels;
    		jpeg_read_scanlines(&info, &rowptr, 1);
    	}
LOGD("Line %d",__LINE__);

    	jpeg_finish_decompress(&info);

LOGD("Line %d",__LINE__);
    	jbyteArray result;
LOGD("Line %d",__LINE__);
        result = env->NewByteArray(dataSize);
LOGD("Line %d",__LINE__);
        (env)->SetByteArrayRegion(result, 0, dataSize,reinterpret_cast<jbyte*>( data));
LOGD("Line %d",__LINE__);
        free(data);
    LOGD("Line %d",__LINE__);
   /* if(rowptr != NULL)
    {
        LOGD("Line %d",__LINE__);
        free(rowptr);
        LOGD("Line %d",__LINE__);
        rowptr = NULL;
        LOGD("Line %d",__LINE__);
    }*/
    LOGD("Line %d",__LINE__);
    if(dIN != NULL)
    {
        LOGD("Line %d",__LINE__);
        free(dIN);
        LOGD("Line %d",__LINE__);
        dIN = NULL;
        LOGD("Line %d",__LINE__);
    }

LOGD("Line %d",__LINE__);
    	return result;
}


JNIEXPORT void JNICALL Java_jni_staxxer_StaxxerJNI_StoreMerged(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fromRS)
{
    LOGD("StoreMerged: Enter Method");
    RGBExtractor* rgbExtractor = (RGBExtractor*) env->GetDirectBufferAddress(handler);
LOGD("Line %d",__LINE__);
    rgbExtractor->Native_Buffer = new unsigned char[env->GetArrayLength(fromRS)];
LOGD("Line %d",__LINE__);
    memcpy(rgbExtractor->Native_Buffer, env->GetByteArrayElements(fromRS,NULL), env->GetArrayLength(fromRS));
LOGD("Line %d",__LINE__);
}

JNIEXPORT jbyteArray JNICALL Java_jni_staxxer_StaxxerJNI_GetMerged(JNIEnv *env, jobject thiz, jobject handler)
{
LOGD("GetMerged: Enter Method");

LOGD("Line %d",__LINE__);
    RGBExtractor* rgbExtractor = (RGBExtractor*) env->GetDirectBufferAddress(handler);
LOGD("Line %d",__LINE__);

    jbyteArray result;
    LOGD("Line %d",__LINE__);
    result = env->NewByteArray(sizeof(rgbExtractor->Native_Buffer));
    LOGD("Line %d",__LINE__);
    (env)->SetByteArrayRegion(result, 0, sizeof(rgbExtractor->Native_Buffer),reinterpret_cast<jbyte*>( rgbExtractor->Native_Buffer));




LOGD("Line %d",__LINE__);
    return result;
}

JNIEXPORT void JNICALL Java_jni_staxxer_StaxxerJNI_Release(JNIEnv *env, jobject thiz, jobject handler)
{
LOGD("Line %d",__LINE__);
RGBExtractor* rgbExtractor = (RGBExtractor*) env->GetDirectBufferAddress(handler);
LOGD("Line %d",__LINE__);
if(rgbExtractor->Native_Buffer != NULL)
{
free(rgbExtractor->Native_Buffer);
rgbExtractor->Native_Buffer = NULL;
}
LOGD("Line %d",__LINE__);

    if (rgbExtractor != NULL)
    free(rgbExtractor);
    rgbExtractor = NULL;
LOGD("Line %d",__LINE__);
}