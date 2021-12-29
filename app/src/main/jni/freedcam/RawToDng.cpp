/*
 *
 *     Copyright (C) 2015 George Kiarie
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <DngWriter.h>
#include "JniUtils.h"
#include "DngWriter.h"

#define  LOG_TAG    "freedcam.RawToDngNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern "C"
{

    JNIEXPORT jobject JNICALL Java_freed_jni_RawToDng_init(JNIEnv *env, jobject thiz)
    {
        DngWriter * writer = new DngWriter();
        return env->NewDirectByteBuffer(writer, 0);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_recycle(JNIEnv *env, jobject thiz,jobject handle)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(handle);
        delete writer;
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz,
                                                               jobject exifinfo,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->exifInfo = (ExifInfo*)env->GetDirectBufferAddress(exifinfo);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jobject javaHandler, jobject gpsInfobuf)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        GpsInfo * gpsInfo = (GpsInfo*)env->GetDirectBufferAddress(gpsInfobuf);
        writer->gpsInfo = gpsInfo;
    }
    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetThumbData(JNIEnv *env, jobject thiz,  jbyteArray mThumb, jint widht, jint height,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->_thumbData = copyByteArray(env,mThumb);
        writer->thumbheight = (int) height;
        writer->thumwidth = widht;
    }

    JNIEXPORT jlong JNICALL Java_freed_jni_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        return writer->rawSize;
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerData(JNIEnv *env, jobject thiz,jbyteArray fileBytes, jstring fileout,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->rawSize = env->GetArrayLength(fileBytes);
        LOGD("Try to set Bayerdata");
        writer->bayerBytes = copyByteArray(env, fileBytes);

        writer->fileSavePath = copyString(env,fileout);
    }
    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataFD(JNIEnv *env, jobject thiz,jbyteArray fileBytes, jint fileDescriptor, jstring filename,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->rawSize = env->GetArrayLength(fileBytes);
        LOGD("Try to set SetBayerDataFD");
        writer->bayerBytes = copyByteArray(env,fileBytes);
        LOGD(" set Bayerdata");
        writer->fileDes = (int)fileDescriptor;
        writer->hasFileDes = true;
        LOGD(" writer->fileDes : %d", writer->fileDes);
        writer->fileSavePath = "";

        LOGD(" writer->rawsize : %d", writer->rawSize);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataBufferFD(JNIEnv *env, jobject thiz,jobject fileBytes, jint fileDescriptor, jstring filename,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        //writer->rawSize = env->GetArrayLength(fileBytes);
        LOGD("Try to set SetBayerDataFD");

        writer->bayerBytes = static_cast<unsigned char *>(env->GetDirectBufferAddress(fileBytes));

        LOGD(" set Bayerdata");
        writer->fileDes = (int)fileDescriptor;
        writer->hasFileDes = true;
        LOGD(" writer->fileDes : %d", writer->fileDes);
        writer->fileSavePath = "";

        LOGD(" writer->rawsize : %d", writer->rawSize);
    }
    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->WriteDNG();
        writer->clear();
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jstring model, jstring make,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->_make = copyString(env,make);
        writer->_model = copyString(env, model);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode(JNIEnv *env, jobject thiz,jobject javaHandler,jobject opcodeHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->opCode = (OpCode*)env->GetDirectBufferAddress(opcodeHandler);
    }



    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerInfo(JNIEnv *env, jobject thiz,
                                                                jobject matrix,
                                                                jobject dngprofile,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->dngProfile = (DngProfile*)env->GetDirectBufferAddress(dngprofile);
        writer->customMatrix = (CustomMatrix*)env->GetDirectBufferAddress(matrix);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetDateTime(JNIEnv *env, jobject thiz, jstring datetime,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->_dateTime = copyString(env,datetime);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetToneCurve(JNIEnv *env, jobject thiz,jfloatArray tonecurve,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->tonecurve = copyfloatArray(env,tonecurve);
        writer->tonecurvesize = env->GetArrayLength(tonecurve);
    }


    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapDims(JNIEnv *env, jobject thiz,jintArray huesatmapdims,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->huesatmapdims =  copyintArray(env, huesatmapdims);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData1(JNIEnv *env, jobject thiz,jfloatArray huesatmapdata,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->huesatmapdata1 = copyfloatArray(env,huesatmapdata);
        writer->huesatmapdata1_size = env->GetArrayLength(huesatmapdata);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData2(JNIEnv *env, jobject thiz,jfloatArray huesatmapdata,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->huesatmapdata2 = copyfloatArray(env,huesatmapdata);
        writer->huesatmapdata2_size = env->GetArrayLength(huesatmapdata);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposure(JNIEnv *env, jobject thiz,jfloat baselineexposure,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->baselineExposure = baselineexposure;
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposureOffset(JNIEnv *env, jobject thiz,jfloat baselineexposureoffset,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->baselineExposureOffset = baselineexposureoffset;
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_setBayerGreenSplit(JNIEnv *env, jobject thiz,jint greensplit,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->bayergreensplit = greensplit;
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_setCropWidthHeight(JNIEnv *env, jobject thiz,jint width, jint height,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->crop_width = width;
        writer->crop_height = height;
    }
};







