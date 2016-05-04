//
// Created by GeorgeKiarie on 01/05/2016.
//

#ifndef INC_3_3_115_FREEDCAM_H
#define INC_3_3_115_FREEDCAM_H
#include <jni.h>
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <unistd.h>

//Droid Imports
#include <android/bitmap.h>
#include <android/log.h>
#include <android/native_window.h> // requires ndk r5 or newer
#include <android/native_window_jni.h> // requires ndk r5 or newer


//LibTiff Imports
#include <tiff/libtiff/tiffio.h>
#include <tiff/libtiff/tif_dir.h>
//LibJpeg Imports
#include <jpeglib.h>
//Libraw Imports
#include "libraw.h"

//Defs
#define  LOG_TAG    "freedcam.Native"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)



class FreeDCam {

};

class ImageProcessor {
public:
    jint _width;
    jint _height;
    jint _colorchannels;
    int* _data;
    ImageProcessor()
    {
        _width = 0;
        _height = 0;
        _colorchannels = 0;
        _data = new int[0];
    }
    void DrawToSurface(JNIEnv * env, jobject surface);
    void DrawToBitmap(JNIEnv * env, jobject bitmap);
    void YuvToRgb(unsigned char* yuyv_image, jint width, jint height);
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
    inline int GetPixelARGBFromBGR(int r, int g, int b) { return 0xff000000 + (b << 16) + (g << 8) + r; }
    inline int GetPixelARGBFromRGB(int r, int g, int b) { return 0xff000000 + (r << 16) + (g << 8) + b; }
    inline int GetPixelRGBFromBGR(int r, int g, int b) { return (b << 16) + (g << 8) + r; }
    inline int GetPixelRGBFromRGB(int r, int g, int b) { return (r << 16) + (g << 8) + b; }
    inline int GetPixelFromARGB(int a,int r, int g, int b) { return (a << 24) + (r << 16) + (g << 8) + b; }
    inline void WritePixel(int x, int y, int val, int* data) { data[x + (y * _width)] = val;}
    void applyFocusPeak();
    void Apply3x3Filter(int filter[3][3]);
    void unpackRAWToRGBA(JNIEnv * env,jstring jfilename);
    void loadJPEGToRGBA(JNIEnv * env,jstring jfilename);

    void unpackRAWToRGB(JNIEnv * env,jstring jfilename);
    void loadJPEGToRGB(JNIEnv * env,jstring jfilename);
    void StackAverageJPEGToARGB(JNIEnv * env,jstring jfilename);
};

class DngWriter
{
public:
    int _iso, _flash;
    double _exposure;
    char* _make;
    char*_model;
    char* _imagedescription;
    char* _orientation;
    float _fnumber, _focallength;
    double _exposureIndex;

    double Altitude;
    float *Latitude;
    float *Longitude;
    char* Provider;
    long gpsTime;
    bool gps;
    TIFF *tif;


    float *blacklevel;
    char *fileSavePath;
    long fileLength;
    unsigned char* bayerBytes;
    int rawwidht, rawheight, rowSize;
    float *colorMatrix1;
    float *colorMatrix2;
    float *neutralColorMatrix;
    float *fowardMatrix1;
    float *fowardMatrix2;
    float *reductionMatrix1;
    float *reductionMatrix2;
    float *noiseMatrix;
    char* bayerformat;
    int rawType;
    long rawSize;

    int fileDes;
    bool hasFileDes;

    int thumbheight, thumwidth;
    unsigned char* _thumbData;

    int opcode2Size;
    unsigned char* opcode2;
    int opcode3Size;
    unsigned char* opcode3;

    DngWriter()
    {
        gps = false;
        fileDes = -1;
        hasFileDes = false;
        opcode2Size =0;
        opcode3Size = 0;
    }
   void SetExifData(JNIEnv *env, jobject thiz,jobject handler,
        jint iso,
        jdouble expo,
        jint flash,
        jfloat fNum,
        jfloat focalL,
        jstring imagedescription,
        jstring orientation,
        jdouble exposureIndex);
         jobject  Create(JNIEnv *env, jobject thiz);
         void  SetGPSData(JNIEnv *env, jobject thiz, jobject handler, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime);
         void  SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, jint widht, jint height);
         void  WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
         jlong  GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler);
         void  SetRawHeight(JNIEnv *env, jobject thiz, jobject handler, jint height);
         void  SetBayerData(JNIEnv *env, jobject thiz, jobject handler,jbyteArray fileBytes, jstring fileout);
         void  SetBayerDataFD(JNIEnv *env, jobject thiz, jobject handler,jbyteArray fileBytes, jint fileDescriptor, jstring filename);
         void  WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
         void  SetModelAndMake(JNIEnv *env, jobject thiz, jobject handler, jstring model, jstring make);
         void  Release(JNIEnv *env, jobject thiz, jobject handler);
         jint  GetRawHeight(JNIEnv *env, jobject thiz, jobject handler);
         void  SetOpCode2(JNIEnv *env, jobject thiz, jobject handler, jbyteArray opcode);
         void  SetOpCode3(JNIEnv *env, jobject thiz, jobject handler, jbyteArray opcode);
    
    	 void SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
        	jfloatArray colorMatrix1,
        	jfloatArray colorMatrix2,
        	jfloatArray neutralColor,
        	jfloatArray fowardMatrix1,
            jfloatArray fowardMatrix2,
            jfloatArray reductionMatrix1,
            jfloatArray reductionMatrix2,
            jfloatArray noiseMatrix,
        	jint blacklevel,
        	jstring bayerformat,
        	jint rowSize,
        	jstring devicename,
        	jint tight,
        	jint width,
        	jint height);

        	void writeIfd0(TIFF *tif, DngWriter *writer);
        	float * calculateGpsPos(double base);
        	void makeGPS_IFD(TIFF *tif, DngWriter *writer);
        	void writeExifIfd(TIFF *tif, DngWriter *writer);
        	void processTight(TIFF *tif,DngWriter *writer);
        	void process10tight(TIFF *tif,DngWriter *writer);
        	void process12tight(TIFF *tif,DngWriter *writer);
        	void processLoose(TIFF *tif,DngWriter *writer);
        	void processSXXX16(TIFF *tif,DngWriter *writer);
        	unsigned char* BufferedRaw(const char* in);
        	void writeRawStuff(TIFF *tif, DngWriter *writer)
};



#endif //INC_3_3_115_FREEDCAM_H
