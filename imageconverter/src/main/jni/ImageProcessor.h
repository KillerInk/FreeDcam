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
    ImageProcessor()
    {
        _width = 0;
        _height = 0;
        _data = new int[0];
    }
    void YuvToRgb(jint* yuyv_image, jint width, jint height);
    jobject getBitmap(JNIEnv *env);
    jobject GetData(JNIEnv * env);
    void Release();
    jobjectArray GetHistogramm(JNIEnv * env);
    void applyLanczos();
    inline int GetPixelRed(int x, int y) { return GetPixelRedFromInt(_data[x + (y * _width)]); }
    inline int GetPixelGreen(int x, int y) { return GetPixelGreenFromInt(_data[x + (y * _width)]); }
    inline int GetPixelBlue(int x, int y) { return GetPixelBlueFromInt(_data[x + (y * _width)]); }
    inline int GetPixel(int x, int y) {return _data[x + (y * _width)];}
    inline int GetPixelRedFromInt(int x) { return (x >> 16) & 0xFF; }
    inline int GetPixelGreenFromInt(int x) { return (x >> 8) & 0xFF; }
    inline int GetPixelBlueFromInt(int x) { return x >> 8 & 0xFF; }
    inline int GetPixelFromBGR(int r, int g, int b) { return 0xff000000 + (b << 16) + (g << 8) + r; }
    inline int GetPixelFromRGB(int r, int g, int b) { return 0xff000000 + (r << 16) + (g << 8) + b; }
    inline int GetPixelFromARGB(int a,int r, int g, int b) { return (a << 24) + (r << 16) + (g << 8) + b; }
    inline void WritePixel(int x, int y, int val, int* data) { data[x + (y * _width)] = val;}
    void applyFocusPeak();
    void Apply3x3Filter(int filter[3][3]);
};


#endif //FREEDCAM_IMAGEPROCESSOR_H
