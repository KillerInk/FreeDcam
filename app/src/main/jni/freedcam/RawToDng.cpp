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
#include <android/log.h>
#include <DngWriter.h>
#define  LOG_TAG    "freedcam.RawToDngNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern "C"
{

JNIEXPORT jobject JNICALL Java_freed_jni_RawToDng_init(JNIEnv * env, jobject obj);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz,
                                                           jint iso,
                                                           jdouble expo,
                                                           jint flash,
                                                           jfloat fNum,
                                                           jfloat focalL,
                                                           jstring imagedescription,
                                                           jstring orientation,
                                                           jfloat exposureIndex, jobject javaHandle);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetThumbData(JNIEnv *env, jobject thiz,  jbyteArray mThumb, jint widht, jint height,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz,jobject javaHandle);
JNIEXPORT jlong JNICALL Java_freed_jni_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jint height,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerData(JNIEnv *env, jobject thiz,jbyteArray fileBytes, jstring fileout,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataFD(JNIEnv *env, jobject thiz,jbyteArray fileBytes, jint fileDescriptor, jstring filename,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jstring model, jstring make,jobject javaHandle);
JNIEXPORT jint JNICALL Java_freed_jni_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode2(JNIEnv *env, jobject thiz, jbyteArray opcode,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode3(JNIEnv *env, jobject thiz, jbyteArray opcode,jobject javaHandle);

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
                                                            jint height,jobject javaHandle);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetDateTime(JNIEnv *env, jobject thiz, jstring datetime,jobject javaHandle);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetToneCurve(JNIEnv *env, jobject thiz,jfloatArray tonecurve,jobject javaHandle);


JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapDims(JNIEnv *env, jobject thiz,jintArray huesatmapdims,jobject javaHandle);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData1(JNIEnv *env, jobject thiz,jfloatArray huesatmapdata,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData2(JNIEnv *env, jobject thiz,jfloatArray huesatmapdata,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposure(JNIEnv *env, jobject thiz,jfloat baselineexposure,jobject javaHandle);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposureOffset(JNIEnv *env, jobject thiz,jfloat baselineexposureoffset,jobject javaHandle);
}

/*void copy_uChars(JNIEnv *env, unsigned char* dest, jbyteArray src, int sizedst)
{
    sizedst =  env->GetArrayLength(src);
    dest = new unsigned char[sizedst];
    jbyte* data = env->GetByteArrayElements(src,NULL);
    memcpy(dest, data, env->GetArrayLength(src));
    env->ReleaseByteArrayElements(src, data, JNI_COMMIT);
}*/

/*void copy_String(JNIEnv *env, char* out ,jstring src)
{
    const char* fpath = env->GetStringUTFChars(src,NULL);
    out = new char[env->GetStringLength(src)];
    strcpy(out,fpath);
    LOGD(" set filepath");
    env->ReleaseStringUTFChars(src,fpath);
}*/

float* copyFloatArray(JNIEnv *env,jfloatArray src)
{
    int size = env->GetArrayLength((jarray)src);
    float * out = new float[size];
    jfloat * mat = env->GetFloatArrayElements(src, 0);
    for (int i = 0; i < size; ++i) {
        out[i] = mat[i];
    }
    env->ReleaseFloatArrayElements(src,mat,JNI_COMMIT);
    return out;
}

/*void copyDoubleMatrix(JNIEnv *env, double* out, jdoubleArray src)
{
    int size = env->GetArrayLength((jarray)src);
    out = new jdouble[size];
    jdouble * mat = env->GetDoubleArrayElements(src, 0);
    memcpy(out, mat,size);
    env->ReleaseDoubleArrayElements(src,mat,JNI_COMMIT);
}*/

JNIEXPORT jobject JNICALL Java_freed_jni_RawToDng_init(JNIEnv * env, jobject obj)
{
    DngWriter *writer = new DngWriter();
    return env->NewDirectByteBuffer(writer, 0);
}

JNIEXPORT jlong JNICALL Java_freed_jni_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    return writer->rawSize;
}

JNIEXPORT jint JNICALL Java_freed_jni_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    return writer->rawheight;
}


JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jint height,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->rawheight = (int) height;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jstring model, jstring make,jobject javaHandle)
{
    LOGD("Java_freed_jni_RawToDng_SetModelAndMake");
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->_make = (char*) env->GetStringUTFChars(make,NULL);
    writer->_model = (char*) env->GetStringUTFChars(model,NULL);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetDateTime(JNIEnv *env, jobject thiz, jstring datetime,jobject javaHandle)
{
    LOGD("Java_freed_jni_RawToDng_SetDateTime");
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    const char* fpath = env->GetStringUTFChars(datetime,NULL);
    writer->_dateTime = new char[env->GetStringLength(datetime)];
    strcpy(writer->_dateTime,fpath);
    env->ReleaseStringUTFChars(datetime,fpath);
    //copy_String(env,writer->_dateTime,datetime);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz,
                                                           jint iso,
                                                           jdouble expo,
                                                           jint flash,
                                                           jfloat fNum,
                                                           jfloat focalL,
                                                           jstring imagedescription,
                                                           jstring orientation,
                                                           jfloat exposureIndex,jobject javaHandle)
{
    LOGD("Java_freed_jni_RawToDng_SetExifData");
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->_iso = iso;
    writer->_exposure =expo;
    writer->_flash = flash;

    const char* fpath = env->GetStringUTFChars(imagedescription,NULL);
    writer->_imagedescription = new char[env->GetStringLength(imagedescription)];
    strcpy(writer->_imagedescription,fpath);
    env->ReleaseStringUTFChars(imagedescription,fpath);

    fpath = env->GetStringUTFChars(orientation,NULL);
    writer->_orientation = new char[env->GetStringLength(orientation)];
    strcpy(writer->_orientation,fpath);
    env->ReleaseStringUTFChars(orientation,fpath);

    writer->_fnumber = fNum;
    LOGD("fnum jni: %9.6f", fNum);
    writer->_focallength = focalL;
    LOGD("expoindex jni: %9.6f", exposureIndex);
    writer->_exposureIndex = exposureIndex;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime,jobject javaHandle)
{
    LOGD("Java_freed_jni_RawToDng_SetGPSData");
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->Altitude = (double)Altitude;
    //copyFloatArray(env, writer->Latitude, Latitude);
    int size = env->GetArrayLength((jarray)Latitude);
    writer->Latitude = new float[size];
    jfloat * mat = env->GetFloatArrayElements(Latitude, 0);
    memcpy(writer->Latitude, mat,size);
    env->ReleaseFloatArrayElements(Latitude,mat,JNI_COMMIT);

    //copyFloatArray(env, writer->Longitude, Longitude);
    size = env->GetArrayLength((jarray)Longitude);
    writer->Longitude = new float[size];
    mat = env->GetFloatArrayElements(Longitude, 0);
    memcpy(writer->Longitude, mat,size);
    env->ReleaseFloatArrayElements(Longitude,mat,JNI_COMMIT);
    //copy_String(env,writer->Provider, Provider);

    const char* fpath = env->GetStringUTFChars(Provider,NULL);
    writer->Provider = new char[env->GetStringLength(Provider)];
    strcpy(writer->Provider,fpath);
    env->ReleaseStringUTFChars(Provider,fpath);

    writer->gpsTime = (long)(gpsTime);
    writer->gps = true;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetThumbData(JNIEnv *env, jobject thiz,  jbyteArray mThumb, int widht, int height,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->_thumbData = (unsigned char*) env->GetByteArrayElements(mThumb,NULL);
    writer->thumbheight = (int) height;
    writer->thumwidth = widht;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode2(JNIEnv *env, jobject thiz, jbyteArray opcode,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->opcode2Size =  env->GetArrayLength(opcode);
    writer->opcode2 = new unsigned char[writer->opcode2Size];
    jbyte* data = env->GetByteArrayElements(opcode,NULL);
    memcpy(writer->opcode2, data, writer->opcode2Size);
    env->ReleaseByteArrayElements(opcode, data, JNI_COMMIT);
    //copy_uChars(env, writer->opcode2,opcode,writer->opcode2Size);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode3(JNIEnv *env, jobject thiz, jbyteArray opcode,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->opcode3Size =  env->GetArrayLength(opcode);
    writer->opcode3 = new unsigned char[writer->opcode3Size];
    jbyte* data = env->GetByteArrayElements(opcode,NULL);
    memcpy(writer->opcode3, data, writer->opcode3Size);
    env->ReleaseByteArrayElements(opcode, data, JNI_COMMIT);
    //copy_uChars(env, writer->opcode3,opcode,writer->opcode3Size);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jbyteArray fileBytes, jstring fileout,jobject javaHandle)
{
    LOGD("Java_freed_jni_RawToDng_SetBayerData");
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);

    writer->rawSize =  env->GetArrayLength(fileBytes);
    writer->bayerBytes = new unsigned char[writer->rawSize];
    jbyte* data = env->GetByteArrayElements(fileBytes,NULL);
    memcpy(writer->bayerBytes, data, env->GetArrayLength(fileBytes));
    env->ReleaseByteArrayElements(fileBytes, data, JNI_COMMIT);

    //copy_uChars(env,writer->bayerBytes,fileBytes,writer->rawSize);
    if(writer->bayerBytes != NULL)
        LOGD("copied bayerdata");
    else
        LOGD("copied bayerdata FAILED!");

    const char* fpath = env->GetStringUTFChars(fileout,NULL);
    writer->fileSavePath = new char[env->GetStringLength(fileout)];
    strcpy(writer->fileSavePath,fpath);
    LOGD(" set filepath");
    env->ReleaseStringUTFChars(fileout,fpath);
    //copy_String(env,writer->fileSavePath, fileout);
    if(writer->fileSavePath != NULL)
        LOGD("copied filesavepath");
    else
        LOGD("copied filesavepath FAILED!");
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataFD(JNIEnv *env, jobject thiz, jbyteArray fileBytes, jint fileDescriptor, jstring filename,jobject javaHandle)
{
    LOGD("Java_freed_jni_RawToDng_SetBayerDataFD");
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->rawSize =  env->GetArrayLength(fileBytes);
    writer->bayerBytes = new unsigned char[writer->rawSize];
    jbyte* data = env->GetByteArrayElements(fileBytes,NULL);
    memcpy(writer->bayerBytes, data, env->GetArrayLength(fileBytes));
    env->ReleaseByteArrayElements(fileBytes, data, JNI_COMMIT);
    //copy_uChars(env,writer->bayerBytes,fileBytes,writer->rawSize);
    LOGD("copied Bayerdata");
    writer->fileDes = (int)fileDescriptor;
    writer->hasFileDes = true;
    LOGD(" writer->fileDes : %d", writer->fileDes);
    writer->fileSavePath = "";
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
                                                            jint height,jobject javaHandle)
{
    LOGD("Java_freed_jni_RawToDng_SetBayerInfo");
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->blacklevel = new float[4];
    for (int i = 0; i < 4; ++i) {
        writer->blacklevel[i] = blacklevel;
    }
    writer->whitelevel = whitelevel;
    writer->rawType = tight;
    writer->rowSize =rowSize;

    LOGD("color1");
    writer->colorMatrix1 = copyFloatArray(env, colorMatrix1);
    /*int size = env->GetArrayLength((jarray)colorMatrix1);
    writer->colorMatrix1 = new float[size];
    jfloat * mat = env->GetFloatArrayElements(colorMatrix1, 0);
    memcpy(writer->colorMatrix1, mat,size);
    env->ReleaseFloatArrayElements(colorMatrix1,mat,JNI_COMMIT);*/

    LOGD("color2");
    writer->colorMatrix2 = copyFloatArray(env, colorMatrix2);
    /*size = env->GetArrayLength((jarray)colorMatrix2);
    writer->colorMatrix2 = new float[size];
    mat = env->GetFloatArrayElements(colorMatrix2, 0);
    memcpy(writer->colorMatrix2, mat,size);
    env->ReleaseFloatArrayElements(colorMatrix2,mat,JNI_COMMIT);*/

    LOGD("neutral");
    writer->neutralColorMatrix = copyFloatArray(env,neutralColor);
   /* size = env->GetArrayLength((jarray)neutralColor);
    writer->neutralColorMatrix = new float[size];
    mat = env->GetFloatArrayElements(neutralColor, 0);
    memcpy(writer->neutralColorMatrix, mat,size);
    env->ReleaseFloatArrayElements(neutralColor,mat,JNI_COMMIT);*/

    if(fowardMatrix1 != NULL){
        LOGD("forward1");
        writer->fowardMatrix1 = copyFloatArray(env,fowardMatrix1);
        /*size = env->GetArrayLength((jarray)fowardMatrix1);
        writer->fowardMatrix1 = new float[size];
        mat = env->GetFloatArrayElements(fowardMatrix1, 0);
        memcpy(writer->fowardMatrix1, mat,size);
        env->ReleaseFloatArrayElements(fowardMatrix1,mat,JNI_COMMIT);*/
    }
        //copyFloatArray(env, writer->fowardMatrix1, fowardMatrix1);
    if(fowardMatrix2 != NULL){
        LOGD("forward2");
        writer->fowardMatrix2 = copyFloatArray(env, fowardMatrix2);
        /*size = env->GetArrayLength((jarray)fowardMatrix2);
        writer->fowardMatrix2 = new float[size];
        mat = env->GetFloatArrayElements(fowardMatrix2, 0);
        memcpy(writer->fowardMatrix2, mat,size);
        env->ReleaseFloatArrayElements(fowardMatrix2,mat,JNI_COMMIT);*/
    }

    if(reductionMatrix1 != NULL){
        LOGD("reduction1");
        writer->reductionMatrix1 = copyFloatArray(env,reductionMatrix1);
        /*size = env->GetArrayLength((jarray)reductionMatrix1);
        writer->reductionMatrix1 = new float[size];
        mat = env->GetFloatArrayElements(reductionMatrix1, 0);
        memcpy(writer->reductionMatrix1, mat,size);
        env->ReleaseFloatArrayElements(reductionMatrix1,mat,JNI_COMMIT);*/
    }

    if(reductionMatrix2 != NULL){
        LOGD("reduction2");
        writer->reductionMatrix2 = copyFloatArray(env,reductionMatrix2);
       /* size = env->GetArrayLength((jarray)reductionMatrix2);
        writer->reductionMatrix2 = new float[size];
        mat = env->GetFloatArrayElements(reductionMatrix2, 0);
        memcpy(writer->reductionMatrix2, mat,size);
        env->ReleaseFloatArrayElements(reductionMatrix2,mat,JNI_COMMIT);*/
    }

    if(noiseMatrix != NULL){
        LOGD("noise");
        int size = env->GetArrayLength((jarray)noiseMatrix);
        writer->noiseMatrix = new jdouble[size];
        jdouble * mat = env->GetDoubleArrayElements(noiseMatrix, 0);
        memcpy(writer->noiseMatrix, mat,size);
        env->ReleaseDoubleArrayElements(noiseMatrix,mat,JNI_COMMIT);
    }

    LOGD(" set filepath");
    const char* fpath = env->GetStringUTFChars(bayerformat,NULL);
    writer->bayerformat = new char[env->GetStringLength(bayerformat)];
    strcpy(writer->bayerformat,fpath);
    env->ReleaseStringUTFChars(bayerformat,fpath);

    writer->rawheight = height;
    writer->rawwidht = width;

}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->WriteDNG();
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetToneCurve(JNIEnv *env, jobject thiz,jfloatArray tonecurve,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->tonecurve = env->GetFloatArrayElements(tonecurve, 0);
    writer->tonecurvesize = env->GetArrayLength(tonecurve);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapDims(JNIEnv *env, jobject thiz,jintArray huesatmapdims,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->huesatmapdims =  env->GetIntArrayElements(huesatmapdims, 0);
}


JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData1(JNIEnv *env, jobject thiz,jfloatArray tonecurve,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->huesatmapdata1 = env->GetFloatArrayElements(tonecurve, 0);
    writer->huesatmapdata1_size = env->GetArrayLength(tonecurve);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData2(JNIEnv *env, jobject thiz,jfloatArray tonecurve,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->huesatmapdata1 = env->GetFloatArrayElements(tonecurve, 0);
    writer->huesatmapdata1_size = env->GetArrayLength(tonecurve);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposure(JNIEnv *env, jobject thiz,jfloat baselineexposure,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->baselineExposure = baselineexposure;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposureOffset(JNIEnv *env, jobject thiz,jfloat baselineexposureoffset,jobject javaHandle)
{
    DngWriter* writer= (DngWriter*) env->GetDirectBufferAddress(javaHandle);
    writer->baselineExposureOffset = baselineexposureoffset;
}

