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
    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jint height,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->rawheight = (int) height;
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
    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->WriteDNG();
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jstring model, jstring make,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->_make = copyString(env,make);
        writer->_model = copyString(env, model);
    }

    JNIEXPORT jint JNICALL Java_freed_jni_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz,jobject javaHandler){
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        return writer->rawheight;
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode2(JNIEnv *env, jobject thiz, jbyteArray opcode,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->opcode2Size = env->GetArrayLength(opcode);
        writer->opcode2 = copyByteArray(env,opcode);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode3(JNIEnv *env, jobject thiz, jbyteArray opcode,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->opcode3Size = env->GetArrayLength(opcode);
        writer->opcode3 = copyByteArray(env,opcode);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerInfo(JNIEnv *env, jobject thiz,
                                                                jfloatArray colorMatrix1,
                                                                jfloatArray colorMatrix2,
                                                                jfloatArray neutralColor,
                                                                jfloatArray fowardMatrix1,
                                                                jfloatArray fowardMatrix2,
                                                                jfloatArray reductionMatrix1,
                                                                jfloatArray reductionMatrix2,
                                                                jdoubleArray noiseMatrix,
                                                                jint blacklevel,
                                                                jint whitelevel,
                                                                jstring bayerformat,
                                                                jint rowSize,
                                                                jstring devicename,
                                                                jint tight,
                                                                jint width,
                                                                jint height,jobject javaHandler)
    {
        DngWriter* writer = (DngWriter*)env->GetDirectBufferAddress(javaHandler);
        writer->blacklevel = new float[4];
        for (int i = 0; i < 4; ++i) {
            writer->blacklevel[i] = blacklevel;
        }
        writer->whitelevel = whitelevel;
        writer->rawType = tight;
        writer->rowSize =rowSize;
        writer->colorMatrix1 = copyfloatArray(env,colorMatrix1);
        //writer->colorMatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
        writer->colorMatrix2 = copyfloatArray(env,colorMatrix2);
        writer->neutralColorMatrix = copyfloatArray(env,neutralColor);
        if(fowardMatrix1 != NULL)
            writer->fowardMatrix1 = copyfloatArray(env,fowardMatrix1);
        if(fowardMatrix2 != NULL)
            writer->fowardMatrix2 =copyfloatArray(env,fowardMatrix2);
        if(reductionMatrix1 != NULL)
            writer->reductionMatrix1 =copyfloatArray(env,reductionMatrix1);
        if(reductionMatrix2 != NULL)
            writer->reductionMatrix2 =copyfloatArray(env,reductionMatrix2);
        if(noiseMatrix != NULL){
            int size = env->GetArrayLength((jarray)noiseMatrix);
            writer->noiseMatrix = new double[size];
            jdouble * mat =env->GetDoubleArrayElements(noiseMatrix, 0);
            memcpy(writer->noiseMatrix,mat, size * sizeof(jdouble));
            env->ReleaseDoubleArrayElements(noiseMatrix,mat,JNI_ABORT);
        }


        writer->bayerformat = copyString(env,bayerformat);
        writer->rawheight = height;
        writer->rawwidht = width;

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
};







