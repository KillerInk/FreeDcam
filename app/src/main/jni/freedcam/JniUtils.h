//
// Created by troop on 01.03.2018.
//
#include <jni.h>
#include <string.h>
#ifndef FREEDCAM_JNIUTILS_H
#define FREEDCAM_JNIUTILS_H

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
    env->GetFloatArrayRegion (input, 0, size, reinterpret_cast<jfloat*>(out));
    return out;
}

static int* copyintArray(JNIEnv *env, jintArray input)
{
    int size = env->GetArrayLength((jarray)input);
    int * out = new int[size];
    env->GetIntArrayRegion (input, 0, size, reinterpret_cast<jint*>(out));
    return out;
}

static unsigned char* copyByteArray(JNIEnv* env, jbyteArray input)
{
    jbyte *data = env->GetByteArrayElements(input, NULL);
    return (unsigned char*) data;
}

#endif //FREEDCAM_JNIUTILS_H