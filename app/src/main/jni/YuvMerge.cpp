#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#include <unistd.h>
#define  LOG_TAG    "freedcam.YuvMergeNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define GETYPOS(x,y, width) y * width + x;
#define GETUPOS(x,y, width, frameSize) (y/2)*(width/2)+(x/2) + frameSize;
#define GETVPOS(x,y, width, frameSize) (y/2)*(width/2)+(x/2) + frameSize + (frameSize/4);

extern "C"
{
    JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_storeYuvFrame(JNIEnv *env, jobject thiz, jbyteArray data, jint width, jint height);
    JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_release(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_storeNextYuvFrame(JNIEnv *env, jobject thiz, jobject handler, jbyteArray data);
    JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_getMergedYuv(JNIEnv *env, jobject thiz, jobject handler, jint count, jbyteArray byteArray);
}

class yuv
{
public:
    uint16_t y;
    uint16_t u;
    uint16_t v;
    yuv(){};
};
class YuvIntContainer
{
public:
    int _width, _height;
    yuv* _data;
    YuvIntContainer(int width, int height)
    {
        _width =width;
        _height=height;
        _data = new yuv[_width*_height];
    }
};

void mergeFrame(YuvIntContainer* yuvi, unsigned char* data)
{
    int frameSize = yuvi->_width * yuvi->_height;
    int i =0, yPos, uPos, vPos, width =yuvi->_width , height = yuvi->_height;
    for (int y = 0; y < height; y++)
    {
        for (int x = 0; x < width; x ++)
        {
            yPos = GETYPOS(x,y,width);
            uPos = GETUPOS(x,y,width, frameSize);
            vPos = GETVPOS(x,y,width, frameSize);

            yuvi->_data[i].y += data[yPos];
            yuvi->_data[i].u += data[uPos];
            yuvi->_data[i].v += data[vPos];

            i++;
        }
    }
}

JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_storeYuvFrame(JNIEnv *env, jobject thiz, jbyteArray data, jint width, jint height)
{
    YuvIntContainer* yuvi = new YuvIntContainer(width, height);
    unsigned char* nativedata = (unsigned char*) env->GetByteArrayElements(data,NULL);
    mergeFrame(yuvi, nativedata);
    delete[] nativedata;
    nativedata = NULL;
    return env->NewDirectByteBuffer(yuvi, 0);
}

JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_release(JNIEnv *env, jobject thiz, jobject handler)
{
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);

    if(yuvi->_data != NULL)
    {
        delete[] yuvi->_data;
    }
    delete yuvi;
    yuvi = NULL;
}

JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_storeNextYuvFrame(JNIEnv *env, jobject thiz, jobject handler, jbyteArray data)
{
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    unsigned char* nativedata = (unsigned char*) env->GetByteArrayElements(data,NULL);
    mergeFrame(yuvi, nativedata);
    delete[] nativedata;
    data = NULL;
}

JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_getMergedYuv(JNIEnv *env, jobject thiz, jobject handler, jint count, jbyteArray byteArray)
{
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    //yuv420sp previewsize in bytes for 2560x1440 = 5529600 bytes = w x h + (w x h) /4 *2
    int yuvsize = (yuvi->_width * yuvi->_height) + (yuvi->_width * yuvi->_height)/2;
    int yPos, uPos, vPos, i= 0;
    unsigned char * chararray = (unsigned char*)malloc(yuvsize);
    int frameSize = yuvi->_width * yuvi->_height;
    for (int y = 0; y < yuvi->_height; y++) {
        for (int x = 0; x < yuvi->_width; x++)
        {
            yPos = GETYPOS(x,y, yuvi->_width);// y * yuvi->_width + x;
            uPos = GETUPOS(x,y,yuvi->_width, frameSize);// (y/2)*(yuvi->_width/2)+(x/2) + frameSize;
            vPos = GETVPOS(x,y,yuvi->_width,frameSize);// (y/2)*(yuvi->_width/2)+(x/2) + frameSize + (frameSize/4);
            chararray[yPos] = (yuvi->_data[i].y /count); //cy;
            chararray[uPos] = (yuvi->_data[i].u /count); //cu;
            chararray[vPos] = (yuvi->_data[i].v /count);//cv;
            i++;
        }
    }
    env->SetByteArrayRegion(byteArray, 0, yuvsize, reinterpret_cast<const jbyte*>(chararray));
    delete[] chararray;
    chararray = NULL;
    return byteArray;
}