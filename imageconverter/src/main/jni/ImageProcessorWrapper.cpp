//
// Created by troop on 08.08.2015.
//

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
#define  LOG_TAG    "ImageProcessor"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define GETYPOS(x,y, width) y * width + x;
#define GETUPOS(x,y, width, frameSize) (y/2)*(width/2)+(x/2) + frameSize;
#define GETVPOS(x,y, width, frameSize) (y/2)*(width/2)+(x/2) + frameSize + (frameSize/4);

#define SCALEYUV(v) (((v)+128000)/256000)

static int rcoeff(int y, int u, int v){ return 298082*y +      0*u + 408583*v; }
static int gcoeff(int y, int u, int v){ return 298082*y - 100291*u - 208120*v; }
static int bcoeff(int y, int u, int v){ return 298082*y + 516411*u +      0*v; }

class RGBContainer
{
public:
    jint _width;
    jint _height;
    int* _data;
    JNIEnv *env;
    RGBContainer()
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

void RGBContainer::YuvToRgb(unsigned char* yuv420sp, jint width, jint height) {

    _width = width;
    _height = height;
    int frameSize = width * height;
    _data = new int[frameSize];

    /*int r,g,b,yi,y,u,v,nextPX = 0;
    int curPix = 0;
    for (int y = 0; y < _height; y++) {
        for (int x = 0; x < _width; x++) {
            yi = GETYPOS(x,y,_width);
            u = GETUPOS(x,y,_width, frameSize);
            v = GETVPOS(x,y,_width, frameSize);

            b = 1.164 * (yuv420sp[yi] - 16) + 2.018 * (yuv420sp[u] - 128);
            g = 1.164 * (yuv420sp[yi] - 16) - 0.813 * (yuv420sp[v] - 128) - 0.391 * (yuv420sp[u] - 128);
            r = 1.164 * (yuv420sp[yi] - 16) + 1.596 * (yuv420sp[v] - 128);
            if (r < 0) r = 0; else if (r > 255) r = 255;
            if (g < 0) g = 0; else if (g > 255) g = 255;
            if (b < 0) b = 0; else if (b > 255) b = 255;
            LOGD("R: %i G: %i B: %i", r, b, g);

            _data[curPix++] = 0xff000000 + (b << 16) + (g << 8) + r;
            if(nextPX == y) {
                LOGD("RGB Pixel: %i R: %i G: %i B: %i", _data[curPix], r, b, g);
                nextPX += 10;
            }
        }
    }*/

    /*int i =0, yi, u, v;
    for (int y = 0; y < _height; y++)
    {
        for (int x = 0; x < _width; x ++)
        {
            yi = GETYPOS(x,y,_width);
            u = GETUPOS(x,y,_width, frameSize);
            v = GETVPOS(x,y,_width, frameSize);

            int y1192 = 1192 * yuv420sp[yi];
            int r = (y1192 + 1634 * yuv420sp[v]);
            int g = (y1192 - 833 * yuv420sp[v] - 400 * yuv420sp[u]);
            int b = (y1192 + 2066 * yuv420sp[u]);

            r = r >>8;
            g = g >> 8;
            b = b >> 8;
            //LOGD("Width: %i Height: %i R: %i  G: %i B: %i",x,y, r,g,b);
            if (r < 0) r = 0; else if (r > 255) r = 255;
            if (g < 0) g = 0; else if (g > 255) g = 255;
            if (b < 0) b = 0; else if (b > 255) b = 255;
            //LOGD("R: %i  G: %i B: %i", r,g,b);
            LOGD("Write to data: %i", y *frameSize+x);
            _data[i] = (0 << 24) |
            (r << 16) |
            (r << 8) |
            b;
            //_data[i] = 0xff000000 | (r & 0xff0000) | (g & 0xff00) | (b & 0xff);
            i++;
        }
    }*/
    /*int i = 0, j = 0,yp = 0;
    int uvp = 0, u = 0, v = 0;
    for (j = 0, yp = 0; j < height; j++)
    {
        uvp = frameSize + (j >> 1) * width;
        u = 0;
        v = 0;
        for (i = 0; i < width; i++, yp++)
        {
            int y = (0xff & ((int) yuv420sp[yp])) - 16;
            if (y < 0)
                y = 0;
            if ((i & 1) == 0)
            {
                v = (0xff & yuv420sp[uvp++]) - 128;
                u = (0xff & yuv420sp[uvp++]) - 128;
            }

            int y1192 = 1192 * y;
            int r = (y1192 + 1634 * v);
            int g = (y1192 - 833 * v - 400 * u);
            int b = (y1192 + 2066 * u);

            if (r < 0) r = 0; else if (r > 262143) r = 262143;
            if (g < 0) g = 0; else if (g > 262143) g = 262143;
            if (b < 0) b = 0; else if (b > 262143) b = 262143;

            _data[yp] = 0xff000000 + (b << 16) + (g << 8) + r;

        }
    }*/

    int             sz;
    int             i;
    int             j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int       nextpx =0,      pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;
    int w = width;
    int h = height;
    sz = w * h;
    int nextPX = 0;
    for(j = 0; j < h ; j++) {
        pixPtr = j * w;
        jDiv2 = j >> 1;
        for(i = 0; i < w; i++) {
            Y = yuv420sp[pixPtr];
            if(Y < 0) Y += 255;
            if((i & 0x1) != 1) {
                cOff = sz + jDiv2 * w + (i >> 1) * 2;
                Cb = yuv420sp[cOff];
                if(Cb < 0) Cb += 127; else Cb -= 128;
                Cr = yuv420sp[cOff + 1];
                if(Cr < 0) Cr += 127; else Cr -= 128;
            }

            //ITU-R BT.601 conversion
            //
            //R = 1.164*(Y-16) + 2.018*(Cr-128);
            //G = 1.164*(Y-16) - 0.813*(Cb-128) - 0.391*(Cr-128);
            //B = 1.164*(Y-16) + 1.596*(Cb-128);
            //
            Y = Y + (Y >> 3) + (Y >> 5) + (Y >> 7);
            R = Y + (Cr << 1) + (Cr >> 6);
            if(R < 0) R = 0; else if(R > 255) R = 255;
            G = Y - Cb + (Cb >> 3) + (Cb >> 4) - (Cr >> 1) + (Cr >> 3);
            if(G < 0) G = 0; else if(G > 255) G = 255;
            B = Y + Cb + (Cb >> 1) + (Cb >> 4) + (Cb >> 5);
            if(B < 0) B = 0; else if(B > 255) B = 255;
            _data[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;

            if(nextPX == j) {
                LOGD("RGB Pixel: %i R: %i G: %i B: %i", _data[pixPtr], R, B, G);
                nextPX += 10;
            }
        }
    }
    //LOGD("DataSize: %i", frameSize);
    //LOGD("RGBPos:, %i: yuvpos: height: %i width: %i", yp, j, i);
    LOGD("free yuv420sp");
    //free(yuv420sp);

}


jobject RGBContainer::getBitmap(JNIEnv * env) {
    void *bitmapPixels;
    int ret;
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap",
                                                            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf",
                                                                   "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass,
                                                       valueOfBitmapConfigFunction, configName);
    jobject newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction, _width,
                                                    _height, bitmapConfig);

    if ((ret = AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels)) < 0) {
        LOGD("AndroidBitmap_lockPixels() failed ! error=%d", ret);

        return NULL;
    }
    LOGD("pixel locked");
    uint32_t* newBitmapPixels = (uint32_t*) bitmapPixels;
    memcpy(newBitmapPixels,(uint32_t*) _data, (_width * _height * sizeof(uint32_t)));
    LOGD("memcopy start");
    LOGD("memcopy end");

    AndroidBitmap_unlockPixels(env, newBitmap);
    free(_data);

    return newBitmap;
}

void RGBContainer::Release()
{
    if (_data != NULL)
        LOGD("Release");
        //free(_data);

}

jobject RGBContainer::GetData(JNIEnv * env)
{
    jintArray result;
    jint size = _width*_height;
    result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, _data);

    return result;
}

extern "C"
{
    JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessor_INIT(JNIEnv *env, jobject thiz);
    JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessor_YUVtoRGB(JNIEnv *env, jobject thiz, jbyteArray yuv420sp, jint width, jint height);
    JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessor_GetBitmap(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void    JNICALL Java_troop_com_imageconverter_ImageProcessor_Release(JNIEnv *env, jobject thiz);
    JNIEXPORT jobject    JNICALL Java_troop_com_imageconverter_ImageProcessor_GetRgbData(JNIEnv *env, jobject thiz);
}

static RGBContainer* rgbContainer;

JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessor_INIT(JNIEnv *env, jobject thiz)
{
    rgbContainer = new RGBContainer();
    return env->NewDirectByteBuffer(rgbContainer, 0);
}

JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessor_YUVtoRGB(JNIEnv *env, jobject thiz, jbyteArray yuv420sp, jint width, jint height)
{
    unsigned char* yuv = (unsigned char*) env->GetByteArrayElements(yuv420sp,NULL);

    rgbContainer->YuvToRgb(yuv, width, height);
}

JNIEXPORT jobject JNICALL Java_troop_com_imageconverter_ImageProcessor_GetBitmap(JNIEnv *env, jobject thiz, jobject handler)
{
    //RGBContainer* rgbContainer = (RGBContainer*) env->GetDirectBufferAddress(handler);
    return rgbContainer->getBitmap(env);
}

JNIEXPORT void JNICALL Java_troop_com_imageconverter_ImageProcessor_Release(JNIEnv *env, jobject thiz)
{
    //RGBContainer* rgbContainer = (RGBContainer*) env->GetDirectBufferAddress(handler);
    rgbContainer->Release();
}

JNIEXPORT jobject    JNICALL Java_troop_com_imageconverter_ImageProcessor_GetRgbData(JNIEnv *env, jobject thiz)
{
    return rgbContainer->GetData(env);
}
