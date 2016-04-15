
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
#include <jpeg/jerror.h>
#include <jpeg/jpeglib.h>
#include <jpeg/jmorecfg.h>
#include <jpeg/jconfig.h>
    JNIEXPORT jobject JNICALL Java_jni_staxxer_StaxxerJNI_Create(JNIEnv *env, jobject thiz);
    JNIEXPORT jbyteArray JNICALL Java_jni_staxxer_StaxxerJNI_SetJpegData(JNIEnv *env, jobject thiz, jobject handler, jint width,jint height);
    JNIEXPORT void JNICALL Java_jni_staxxer_StaxxerJNI_Release(JNIEnv *env, jobject thiz, jobject handler);


}

class RGBExtractor
{
public:


   // unsigned char* JPEGBytes;
    unsigned char* RGBBytes;
    int JPEGwidht, JPEGheight;

    long rawSize;

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

void jpg2rgb(int black, int white, double a,
            int b, size_t *w, size_t *h) {
    int rc, i;
    struct jpeg_decompress_struct dinfo;
    struct jpeg_error_mgr derr;
    FILE *infile = NULL, *outfile = NULL;
    JSAMPARRAY dbuffer;
    int dstride;
    size_t n;
    if (!(infile = fopen("/sdcard/DCIM/FreeDcam/active/in.jpg", "rb"))) {
        rc = errno ? errno : -1;
        LOGD("fopen: %s (%s)", strerror(rc), "/sdcard/DCIM/FreeDcam/active/in.jpg");
        goto finally;
    }
    if (!(outfile = fopen("/sdcard/DCIM/FreeDcam/active/out.jpg", "wb+"))) {
        rc = errno ? errno : -1;
        LOGD("fopen: %s (%s)", strerror(rc), "/sdcard/DCIM/FreeDcam/active/out.jpg");
        goto finally;
    }
    dinfo.err = jpeg_std_error(&derr);
    jpeg_create_decompress(&dinfo);
    jpeg_stdio_src(&dinfo, infile);
    jpeg_read_header(&dinfo, TRUE);
    jpeg_start_decompress(&dinfo);
    dstride = dinfo.output_width * dinfo.output_components;
    if (!(dbuffer = (*dinfo.mem->alloc_sarray)((j_common_ptr) & dinfo,
                                               JPOOL_IMAGE, dstride, 1))) {
        rc = errno ? errno : -1;
        LOGD("failed to allocate decoder buffer: %s",
              strerror(rc));
        goto finally;
    }
    while (dinfo.output_scanline < dinfo.output_height) {
        jpeg_read_scanlines(&dinfo, dbuffer, 1);
        if (black > 0 && white < 255) {
            for (i = 0; i < dstride; i++) {
                dbuffer[0][i] = stretch(dbuffer[0][i], black, white);
            }
        }
        if (a != 1.0 || b != 0) {
            for (i = 0; i < dstride; i++) {
                if (a != 1.0) {
                    dbuffer[0][i] = clamp(a *
                                          dbuffer[0][i]);
                }

                if (b != 0) {
                    dbuffer[0][i] = clamp(b +
                                          dbuffer[0][i]);
                }
            }
        }

        if ((n = fwrite(dbuffer[0], dinfo.output_components,
                        dinfo.output_width, outfile)) != dinfo.output_width) {
            rc = errno ? errno : -1;
            LOGD("fwrite: %s (%s)", strerror(rc), "/sdcard/DCIM/FreeDcam/active/out.jpg");
            goto finally;
        }
    }

    if (w) {
        *w = dinfo.output_width;
    }

    if (h) {
        *h = dinfo.output_height;
    }
    jpeg_finish_decompress(&dinfo);
    jpeg_destroy_decompress(&dinfo);
    rc = 0;
    finally:

    if (infile) {
        fclose(infile);
    }

    if (outfile) {
        fclose(outfile);
    }
}




JNIEXPORT jobject JNICALL Java_jni_staxxer_StaxxerJNI_Create(JNIEnv *env, jobject thiz)
{
    RGBExtractor *rgbExtractor = new RGBExtractor();
    return env->NewDirectByteBuffer(rgbExtractor, 0);
}


JNIEXPORT jbyteArray JNICALL JNICALL Java_jni_staxxer_StaxxerJNI_SetJpegData(JNIEnv *env, jobject thiz, jobject handler, jint width,jint height)
{
    jbyteArray ret;
    RGBExtractor* rgbExtractor = (RGBExtractor*) env->GetDirectBufferAddress(handler);
        LOGD("TRy Init JPEG data in Native");
    //rgbExtractor->JPEGBytes = new unsigned char[env->GetArrayLength(fileBytes)];
   //     LOGD("init JPEG data");
   // memcpy(rgbExtractor->JPEGBytes, env->GetByteArrayElements(fileBytes,NULL), env->GetArrayLength(fileBytes));
        LOGD(" set JPEG data");

    rgbExtractor->JPEGheight = height;
    rgbExtractor->JPEGwidht = width;
   // rgbExtractor->rawSize = env->GetArrayLength(fileBytes);

    size_t *w = (size_t*)rgbExtractor->JPEGwidht;
    size_t *h = (size_t*)rgbExtractor->JPEGheight;

    jpg2rgb(0,255,1.0,0,w,h);

    ret = (env)->NewByteArray(sizeof(rgbExtractor->RGBBytes));
    env->SetByteArrayRegion(ret,0,sizeof(rgbExtractor->RGBBytes),(jbyte*)rgbExtractor->RGBBytes);
    return ret;

}

JNIEXPORT void JNICALL Java_jni_staxxer_StaxxerJNI_Release(JNIEnv *env, jobject thiz, jobject handler)
{
RGBExtractor* rgbExtractor = (RGBExtractor*) env->GetDirectBufferAddress(handler);

 /*   if(rgbExtractor->JPEGBytes != NULL)
    {
        free(rgbExtractor->JPEGBytes);
        rgbExtractor->JPEGBytes = NULL;
    }*/

if(rgbExtractor->RGBBytes != NULL)
{
free(rgbExtractor->RGBBytes);
rgbExtractor->RGBBytes = NULL;
}

    if (rgbExtractor != NULL)
    free(rgbExtractor);
    rgbExtractor = NULL;
}