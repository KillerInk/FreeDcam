#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#include <unistd.h>
#define  LOG_TAG    "DEBUG"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern "C"
{
    JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_storeYuvFrame(JNIEnv *env, jobject thiz, jbyte data[]);
    JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_release(JNIEnv *env, jobject thiz, jobject handler);
}

struct yuv
{
    uint32_t y;
    uint32_t u;
    uint32_t v;
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

void mergeFrame(YuvIntContainer* merge, jbyte data[])
{
    int frameSize = merge->_width * merge->_height;
    int i =0;
    for (int y = 0; y < merge->_height; y++)
    {
        for (int x = 0; x < merge->_width; x++)
        {
            int yPos = y * merge->_width + x;
            int uPos = (y/2)*(merge->_width/2)+(x/2) + frameSize;
            int vPos = (y/2)*(merge->_width/2)+(x/2) + frameSize + (frameSize/4);
            merge->_data[i].y += (data[yPos] & 0xff);
            merge->_data[i].u += (data[uPos] & 0xff);
            merge->_data[i].v += (data[vPos] & 0xff);
            i++;
        }
    }
}

JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_storeYuvFrame(JNIEnv *env, jobject thiz, jbyte data[], jint width, jint height)
{
    YuvIntContainer* yuvi = new YuvIntContainer(width, height);
    mergeFrame(yuvi, data);
    return env->NewDirectByteBuffer(yuvi, 0);
}

JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_release(JNIEnv *env, jobject thiz, jobject handler)
{
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    if(yuvi->_data == NULL)
        return;
    delete[] yuvi->_data;
    yuvi->_data = NULL;
    delete yuvi;
}