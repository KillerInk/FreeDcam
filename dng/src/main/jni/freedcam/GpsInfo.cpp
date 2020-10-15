//
// Created by troop on 01.03.2018.
//
#include <jni.h>
#include "GpsInfo.h"
#include "JniUtils.h"

extern "C"
{
    JNIEXPORT jobject JNICALL Java_freed_jni_GpsInfo_init(JNIEnv *env, jobject thiz) {
        GpsInfo *gpsInfo = new GpsInfo();
        return env->NewDirectByteBuffer(gpsInfo, 0);
    }

    JNIEXPORT void JNICALL Java_freed_jni_GpsInfo_clear(JNIEnv *env, jobject thiz, jobject javaHandler)
    {
        GpsInfo* gpsInfo = (GpsInfo*)env->GetDirectBufferAddress(javaHandler);
        gpsInfo->clear();
        delete gpsInfo;
    }

    JNIEXPORT void JNICALL Java_freed_jni_GpsInfo_setGpsInfo(JNIEnv *env, jobject thiz, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jfloatArray gpsTime, jstring gpsDate, jobject javaHandler)
    {
        GpsInfo* gpsInfo = (GpsInfo*)env->GetDirectBufferAddress(javaHandler);
        gpsInfo->Altitude = (double)Altitude;
        gpsInfo->Latitude =  copyfloatArray(env, Latitude);
        gpsInfo->Longitude = copyfloatArray(env, Longitude);
        gpsInfo->Provider = copyString(env, Provider);
        gpsInfo->gpsTime = copyfloatArray(env, gpsTime);
        gpsInfo->gpsDate = copyString(env, gpsDate);
    }
};