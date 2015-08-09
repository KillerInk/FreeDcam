//
// Created by troop on 09.08.2015.
//

#ifndef FREEDCAM_IMAGEPROCESSOR_H
#define FREEDCAM_IMAGEPROCESSOR_H
#define  LOG_TAG    "ImageProcessor"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#include <unistd.h>
#include <android/bitmap.h>

class ImageProcessor {
public:
    jint _width;
    jint _height;
    int* _data;
    JNIEnv *env;
    ImageProcessor()
    {
        _width = 0;
        _height = 0;
        _data = new int[0];
    }
    void YuvToRgb(unsigned char* yuyv_image, jint width, jint height);
    jobject getBitmap(JNIEnv *env);
    jobject GetData(JNIEnv * env);
    void Release();
};


#endif //FREEDCAM_IMAGEPROCESSOR_H
