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

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_overloadWrite(JNIEnv *env, jobject thiz,
                                                            //exif
                                                             jint iso,
                                                             jdouble expo,
                                                             jint flash,
                                                             jfloat fNum,
                                                             jfloat focalL,
                                                             jstring imagedescription,
                                                             jstring orientation,
                                                             jfloat exposureIndex,
                                                             //gps
                                                             jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime,
                                                             //thumb
                                                             jbyteArray mThumb, jint thumb_widht, jint thumb_height,
                                                             //data
                                                             jbyteArray fileBytes, jstring fileout,jint fileDescriptor,
                                                             jstring model, jstring make,jbyteArray opcode2,jbyteArray opcode3,
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

                                                             jint tight,
                                                             jint width,
                                                             jint height,
                                                             jstring datetime,
                                                             jfloatArray tonecurve,
                                                             jintArray huesatmapdims,
                                                             jfloatArray huesatmapdata1,
                                                             jfloatArray huesatmapdata2,
                                                             jfloat baselineexposure,
                                                             jfloat baselineexposureoffset

);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz,
                                                           jint iso,
                                                           jdouble expo,
                                                           jint flash,
                                                           jfloat fNum,
                                                           jfloat focalL,
                                                           jstring imagedescription,
                                                           jstring orientation,
                                                           jfloat exposureIndex);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetThumbData(JNIEnv *env, jobject thiz,  jbyteArray mThumb, jint widht, jint height);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz);
JNIEXPORT jlong JNICALL Java_freed_jni_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jint height);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerData(JNIEnv *env, jobject thiz,jbyteArray fileBytes, jstring fileout);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataFD(JNIEnv *env, jobject thiz,jbyteArray fileBytes, jint fileDescriptor, jstring filename);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jstring model, jstring make);
JNIEXPORT jint JNICALL Java_freed_jni_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode2(JNIEnv *env, jobject thiz, jbyteArray opcode);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode3(JNIEnv *env, jobject thiz, jbyteArray opcode);

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
                                                            jint height);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetDateTime(JNIEnv *env, jobject thiz, jstring datetime);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetToneCurve(JNIEnv *env, jobject thiz,jfloatArray tonecurve);


JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapDims(JNIEnv *env, jobject thiz,jintArray huesatmapdims);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData1(JNIEnv *env, jobject thiz,jfloatArray huesatmapdata);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData2(JNIEnv *env, jobject thiz,jfloatArray huesatmapdata);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposure(JNIEnv *env, jobject thiz,jfloat baselineexposure);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposureOffset(JNIEnv *env, jobject thiz,jfloat baselineexposureoffset);
}

DngWriter writer;

static char* copyString(JNIEnv* env, jstring input)
{
    const char * fsp = env->GetStringUTFChars(input,NULL);
    int len = env->GetStringLength(input);
    char* out = new char[len];
    strcpy(out, fsp);
    env->ReleaseStringUTFChars(input,fsp);
    return out;
}

static float* copyfloatArray(JNIEnv *env, jfloatArray input)
{
    int size = env->GetArrayLength((jarray)input);
    float * out = new float[size];
    jfloat * mat =env->GetFloatArrayElements(input, 0);
    memcpy(out,mat, size * sizeof(jfloat));
    env->ReleaseFloatArrayElements(input,mat,JNI_ABORT);
    return out;
}

static int* copyintArray(JNIEnv *env, jintArray input)
{
    int size = env->GetArrayLength((jarray)input);
    int * out = new int[size];
    jint * mat =env->GetIntArrayElements(input, 0);
    memcpy(out,mat, size * sizeof(jint));
    env->ReleaseIntArrayElements(input,mat,JNI_ABORT);
    return out;
}

static unsigned char* copyByteArray(JNIEnv* env, jbyteArray input)
{
    int size = env->GetArrayLength((jarray)input);
    unsigned char* out = new unsigned char[size];
    jbyte * bytes = env->GetByteArrayElements(input,NULL);
    memcpy(out,bytes,size * sizeof(jbyte));
    env->ReleaseByteArrayElements(input, bytes, JNI_ABORT);
    return out;
}

JNIEXPORT jlong JNICALL Java_freed_jni_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz)
{
    return writer.rawSize;
}

JNIEXPORT jint JNICALL Java_freed_jni_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz)
{
    return writer.rawheight;
}


JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jint height)
{
    writer.rawheight = (int) height;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jstring model, jstring make)
{
    writer._make = copyString(env,make);
    writer._model = copyString(env, model);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetDateTime(JNIEnv *env, jobject thiz, jstring datetime)
{
    writer._dateTime = copyString(env,datetime);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz,
                                                           jint iso,
                                                           jdouble expo,
                                                           jint flash,
                                                           jfloat fNum,
                                                           jfloat focalL,
                                                           jstring imagedescription,
                                                           jstring orientation,
                                                           jfloat exposureIndex)
{
    writer._iso = iso;
    writer._exposure =expo;
    writer._flash = flash;
    writer._imagedescription = copyString(env,imagedescription);
    writer._orientation = copyString(env,orientation);
    writer._fnumber = fNum;
    LOGD("fnum jni: %9.6f", fNum);
    writer._focallength = focalL;
    LOGD("expoindex jni: %9.6f", exposureIndex);
    writer._exposureIndex = exposureIndex;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime)
{
    writer.Altitude = (double)Altitude;
    writer.Latitude =  copyfloatArray(env, Latitude);
    writer.Longitude = copyfloatArray(env, Longitude);
    writer.Provider = copyString(env,Provider);
    writer.gpsTime = (long)(gpsTime);
    writer.gps = true;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetThumbData(JNIEnv *env, jobject thiz,  jbyteArray mThumb, int widht, int height)
{
    writer._thumbData = copyByteArray(env,mThumb);
    writer.thumbheight = (int) height;
    writer.thumwidth = widht;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode2(JNIEnv *env, jobject thiz, jbyteArray opcode)
{
    writer.opcode2Size = env->GetArrayLength(opcode);
    writer.opcode2 = copyByteArray(env,opcode);
}
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode3(JNIEnv *env, jobject thiz, jbyteArray opcode)
{
    writer.opcode3Size = env->GetArrayLength(opcode);
    writer.opcode3 = copyByteArray(env,opcode);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jbyteArray fileBytes, jstring fileout)
{
    writer.rawSize = env->GetArrayLength(fileBytes);
    LOGD("Try to set Bayerdata");
    writer.bayerBytes = copyByteArray(env, fileBytes);

    writer.fileSavePath = copyString(env,fileout);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataFD(JNIEnv *env, jobject thiz, jbyteArray fileBytes, jint fileDescriptor, jstring filename)
{
    writer.rawSize = env->GetArrayLength(fileBytes);
    LOGD("Try to set SetBayerDataFD");
    writer.bayerBytes = copyByteArray(env,fileBytes);
    LOGD(" set Bayerdata");
    writer.fileDes = (int)fileDescriptor;
    writer.hasFileDes = true;
    LOGD(" writer.fileDes : %d", writer.fileDes);
    writer.fileSavePath = "";

    LOGD(" writer.rawsize : %d", writer.rawSize);
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
                                                            jint height)
{

    writer.blacklevel = new float[4];
    for (int i = 0; i < 4; ++i) {
        writer.blacklevel[i] = blacklevel;
    }
    writer.whitelevel = whitelevel;
    writer.rawType = tight;
    writer.rowSize =rowSize;
    writer.colorMatrix1 = copyfloatArray(env,colorMatrix1);
    //writer.colorMatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    writer.colorMatrix2 = copyfloatArray(env,colorMatrix2);
    writer.neutralColorMatrix = copyfloatArray(env,neutralColor);
    if(fowardMatrix1 != NULL)
        writer.fowardMatrix1 = copyfloatArray(env,fowardMatrix1);
    if(fowardMatrix2 != NULL)
        writer.fowardMatrix2 =copyfloatArray(env,fowardMatrix2);
    if(reductionMatrix1 != NULL)
        writer.reductionMatrix1 =copyfloatArray(env,reductionMatrix1);
    if(reductionMatrix2 != NULL)
        writer.reductionMatrix2 =copyfloatArray(env,reductionMatrix2);
    if(noiseMatrix != NULL){
        int size = env->GetArrayLength((jarray)noiseMatrix);
        writer.noiseMatrix = new double[size];
        jdouble * mat =env->GetDoubleArrayElements(noiseMatrix, 0);
        memcpy(writer.noiseMatrix,mat, size * sizeof(jdouble));
        env->ReleaseDoubleArrayElements(noiseMatrix,mat,JNI_ABORT);
    }


    writer.bayerformat = copyString(env,bayerformat);
    writer.rawheight = height;
    writer.rawwidht = width;

}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz)
{
    writer.WriteDNG();
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetToneCurve(JNIEnv *env, jobject thiz,jfloatArray tonecurve)
{
    writer.tonecurve = copyfloatArray(env,tonecurve);
    writer.tonecurvesize = env->GetArrayLength(tonecurve);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapDims(JNIEnv *env, jobject thiz,jintArray huesatmapdims)
{
    writer.huesatmapdims =  copyintArray(env, huesatmapdims);
}


JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData1(JNIEnv *env, jobject thiz,jfloatArray tonecurve)
{
    writer.huesatmapdata1 = copyfloatArray(env,tonecurve);
    writer.huesatmapdata1_size = env->GetArrayLength(tonecurve);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetHueSatMapData2(JNIEnv *env, jobject thiz,jfloatArray tonecurve)
{
    writer.huesatmapdata2 = copyfloatArray(env,tonecurve);
    writer.huesatmapdata2_size = env->GetArrayLength(tonecurve);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposure(JNIEnv *env, jobject thiz,jfloat baselineexposure)
{
    writer.baselineExposure = baselineexposure;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBaselineExposureOffset(JNIEnv *env, jobject thiz,jfloat baselineexposureoffset)
{
    writer.baselineExposureOffset = baselineexposureoffset;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_overloadWrite(JNIEnv *env, jobject thiz,
        //exif
                                                             jint iso,
                                                             jdouble expo,
                                                             jint flash,
                                                             jfloat fNum,
                                                             jfloat focalL,
                                                             jstring imagedescription,
                                                             jstring orientation,
                                                             jfloat exposureIndex,
        //gps
                                                             jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime,
        //thumb
                                                             jbyteArray mThumb, jint thumb_widht, jint thumb_height,
        //data
                                                             jbyteArray fileBytes, jstring fileout,jint fileDescriptor,
                                                             jstring model, jstring make,jbyteArray opcode2,jbyteArray opcode3,
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
                                                             jint tight,
                                                             jint width,
                                                             jint height,
                                                             jstring datetime,
                                                             jfloatArray tonecurve,
                                                             jintArray huesatmapdims,
                                                             jfloatArray huesatmapdata1,
                                                             jfloatArray huesatmapdata2,
                                                             jfloat baselineexposure,
                                                             jfloat baselineexposureoffset)
{
    DngWriter* writer1 = new DngWriter();
    writer1->_iso = iso;
    writer1->_exposure =expo;
    writer1->_flash = flash;
    LOGD("imagedescription");
    if(imagedescription != NULL)
        writer1->_imagedescription = copyString(env,imagedescription);
    LOGD("orientation");
    writer1->_orientation = copyString(env,orientation);
    writer1->_fnumber = fNum;
    LOGD("fnum jni: %9.6f", fNum);
    writer1->_focallength = focalL;
    LOGD("expoindex jni: %9.6f", exposureIndex);
    writer1->_exposureIndex = exposureIndex;

    if(Latitude != NULL && Longitude != NULL)
    {
        LOGD("gps altitude");
        writer1->Altitude = (double)Altitude;
        LOGD("gps latitude");
        writer1->Latitude =  copyfloatArray(env, Latitude);
        LOGD("gps longitude");
        writer1->Longitude = copyfloatArray(env, Longitude);
        LOGD("gps Provider");
        writer1->Provider = copyString(env,Provider);
        LOGD("gps gpsTime");
        writer1->gpsTime = (long)(gpsTime);
        writer1->gps = true;
    }

    if(mThumb != NULL)
    {
        LOGD("thumb");
        writer1->_thumbData = copyByteArray(env,mThumb);
        writer1->thumbheight = (int) thumb_height;
        writer1->thumwidth = thumb_widht;
    }
    LOGD("rawsize");
    writer1->rawSize = env->GetArrayLength(fileBytes);
    LOGD("Try to set Bayerdata");
    writer1->bayerBytes = copyByteArray(env, fileBytes);

    LOGD("filesavepath");
    writer1->fileSavePath = copyString(env,fileout);

    if(fileDescriptor != -1)
    {
        LOGD("has filedescriptor");
        writer1->fileDes = (int)fileDescriptor;
        writer1->hasFileDes = true;
    }

    LOGD("make");
    writer1->_make = copyString(env,make);
    LOGD("model");
    writer1->_model = copyString(env, model);

    if(opcode2 != NULL){
        LOGD("opcode2");
        writer1->opcode2Size = env->GetArrayLength(opcode2);
        writer1->opcode2 = copyByteArray(env,opcode2);
    }
    if(opcode3 != NULL){
        LOGD("opcode3");
        writer1->opcode3Size = env->GetArrayLength(opcode3);
        writer1->opcode3 = copyByteArray(env,opcode3);
    }

    LOGD("blacklvl");
    writer1->blacklevel = new float[4];
    for (int i = 0; i < 4; ++i) {
        writer1->blacklevel[i] = blacklevel;
    }
    LOGD("whitelvl");
    writer1->whitelevel = whitelevel;
    writer1->rawType = tight;
    writer1->rowSize =rowSize;
    LOGD("color1");
    writer1->colorMatrix1 = copyfloatArray(env,colorMatrix1);
    //writer1->colorMatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    writer1->colorMatrix2 = copyfloatArray(env,colorMatrix2);
    writer1->neutralColorMatrix = copyfloatArray(env,neutralColor);
    if(fowardMatrix1 != NULL)
        writer1->fowardMatrix1 = copyfloatArray(env,fowardMatrix1);
    if(fowardMatrix2 != NULL)
        writer1->fowardMatrix2 =copyfloatArray(env,fowardMatrix2);
    if(reductionMatrix1 != NULL)
        writer1->reductionMatrix1 =copyfloatArray(env,reductionMatrix1);
    if(reductionMatrix2 != NULL)
        writer1->reductionMatrix2 =copyfloatArray(env,reductionMatrix2);
    if(noiseMatrix != NULL){
        int size = env->GetArrayLength((jarray)noiseMatrix);
        writer1->noiseMatrix = new double[size];
        jdouble * mat =env->GetDoubleArrayElements(noiseMatrix, 0);
        memcpy(writer1->noiseMatrix,mat, size * sizeof(jdouble));
        env->ReleaseDoubleArrayElements(noiseMatrix,mat,JNI_ABORT);
    }

    LOGD("bayerformat");
    writer1->bayerformat = copyString(env,bayerformat);
    writer1->rawheight = height;
    writer1->rawwidht = width;
    LOGD("datetime");
    writer1->_dateTime = copyString(env,datetime);
    /*if(tonecurve != NULL){
        writer1->tonecurve = copyfloatArray(env,tonecurve);
        writer1->tonecurvesize = env->GetArrayLength(tonecurve);
    }

    if(huesatmapdims != NULL)
        writer1->huesatmapdims =  copyintArray(env, huesatmapdims);

    if(huesatmapdata1 != NULL)
    {
        writer1->huesatmapdata1 = copyfloatArray(env,huesatmapdata1);
        writer1->huesatmapdata1_size = env->GetArrayLength(huesatmapdata1);
    }
    if(huesatmapdata2 != NULL)
    {
        writer1->huesatmapdata2 = copyfloatArray(env,huesatmapdata2);
        writer1->huesatmapdata2_size = env->GetArrayLength(huesatmapdata2);
    }*/

    writer1->baselineExposure = baselineexposure;
    writer1->baselineExposureOffset = baselineexposureoffset;
    LOGD("WriteDNG");
    writer1->WriteDNG();
    LOGD("delte");
    delete writer1;
}

