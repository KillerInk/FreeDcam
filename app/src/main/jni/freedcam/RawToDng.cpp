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
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz,
                                                           jint iso,
                                                           jdouble expo,
                                                           jint flash,
                                                           jfloat fNum,
                                                           jfloat focalL,
                                                           jstring imagedescription,
                                                           jstring orientation,
                                                           jdouble exposureIndex);

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
                                                            jstring bayerformat,
                                                            jint rowSize,
                                                            jstring devicename,
                                                            jint tight,
                                                            jint width,
                                                            jint height);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetDateTime(JNIEnv *env, jobject thiz, jstring datetime);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetToneCurve(JNIEnv *env, jobject thiz,jfloatArray tonecurve);
}


DngWriter writer;
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
    writer._make = (char*) env->GetStringUTFChars(make,NULL);
    writer._model = (char*) env->GetStringUTFChars(model,NULL);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetDateTime(JNIEnv *env, jobject thiz, jstring datetime)
{
    writer._dateTime = (char*) env->GetStringUTFChars(datetime,NULL);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz,
                                                           jint iso,
                                                           jdouble expo,
                                                           jint flash,
                                                           jfloat fNum,
                                                           jfloat focalL,
                                                           jstring imagedescription,
                                                           jstring orientation,
                                                           jdouble exposureIndex)
{
    writer._iso = iso;
    writer._exposure =expo;
    writer._flash = flash;
    writer._imagedescription = (char*) env->GetStringUTFChars(imagedescription,NULL);
    writer._orientation = (char*) env->GetStringUTFChars(orientation,NULL);
    writer._fnumber = fNum;
    writer._focallength = focalL;
    writer._exposureIndex = exposureIndex;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime)
{
    writer.Altitude = (double)Altitude;
    writer.Latitude =  env->GetFloatArrayElements(Latitude,NULL);
    writer.Longitude = env->GetFloatArrayElements(Longitude,NULL);
    writer.Provider = (char*) env->GetStringUTFChars(Provider,NULL);
    writer.gpsTime = (long)(gpsTime);
    writer.gps = true;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetThumbData(JNIEnv *env, jobject thiz,  jbyteArray mThumb, int widht, int height)
{
    writer._thumbData = (unsigned char*) env->GetByteArrayElements(mThumb,NULL);
    writer.thumbheight = (int) height;
    writer.thumwidth = widht;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode2(JNIEnv *env, jobject thiz, jbyteArray opcode)
{
    writer.opcode2Size = env->GetArrayLength(opcode);
    writer.opcode2 = new unsigned char[writer.opcode2Size];
    memcpy(writer.opcode2, env->GetByteArrayElements(opcode,NULL), writer.opcode2Size);
}
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode3(JNIEnv *env, jobject thiz, jbyteArray opcode)
{
    writer.opcode3Size = env->GetArrayLength(opcode);
    writer.opcode3 = new unsigned char[writer.opcode3Size];
    memcpy(writer.opcode3, env->GetByteArrayElements(opcode,NULL), writer.opcode3Size);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jbyteArray fileBytes, jstring fileout)
{
    LOGD("Try to set Bayerdata");
    writer.bayerBytes = new unsigned char[env->GetArrayLength(fileBytes)];
    LOGD("init bayerbytes");
    //writer.bayerBytes = (unsigned char*) env->GetByteArrayElements(fileBytes,NULL);
    memcpy(writer.bayerBytes, env->GetByteArrayElements(fileBytes,NULL), env->GetArrayLength(fileBytes));
    LOGD(" set Bayerdata");
    writer.fileSavePath = (char*)  env->GetStringUTFChars(fileout,NULL);
    writer.rawSize = env->GetArrayLength(fileBytes);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataFD(JNIEnv *env, jobject thiz, jbyteArray fileBytes, jint fileDescriptor, jstring filename)
{
    LOGD("Try to set SetBayerDataFD");
    writer.bayerBytes = new unsigned char[env->GetArrayLength(fileBytes)];
    LOGD("init bayerbytes");
    //writer.bayerBytes = (unsigned char*) env->GetByteArrayElements(fileBytes,NULL);
    memcpy(writer.bayerBytes, env->GetByteArrayElements(fileBytes,NULL), env->GetArrayLength(fileBytes));
    LOGD(" set Bayerdata");
    writer.fileDes = (int)fileDescriptor;
    writer.hasFileDes = true;
    LOGD(" writer.fileDes : %d", writer.fileDes);
    writer.fileSavePath = "";
    writer.rawSize = env->GetArrayLength(fileBytes);
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
    writer.rawType = tight;
    writer.rowSize =rowSize;
    writer.colorMatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    writer.colorMatrix2 =env->GetFloatArrayElements(colorMatrix2, 0);
    writer.neutralColorMatrix = env->GetFloatArrayElements(neutralColor, 0);
    if(fowardMatrix1 != NULL)
        writer.fowardMatrix1 = env->GetFloatArrayElements(fowardMatrix1, 0);
    if(fowardMatrix2 != NULL)
        writer.fowardMatrix2 =env->GetFloatArrayElements(fowardMatrix2, 0);
    if(reductionMatrix1 != NULL)
        writer.reductionMatrix1 = env->GetFloatArrayElements(reductionMatrix1, 0);
    if(reductionMatrix2 != NULL)
        writer.reductionMatrix2 =env->GetFloatArrayElements(reductionMatrix2, 0);
    if(noiseMatrix != NULL)
        writer.noiseMatrix = env->GetDoubleArrayElements(noiseMatrix, 0);

    writer.bayerformat = (char*)  env->GetStringUTFChars(bayerformat,0);
    writer.rawheight = height;
    writer.rawwidht = width;

}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz)
{
    writer.WriteDNG();
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetToneCurve(JNIEnv *env, jobject thiz,jfloatArray tonecurve)
{
    writer.tonecurve = env->GetFloatArrayElements(tonecurve, 0);
    writer.tonecurvesize = env->GetArrayLength(tonecurve);
}


