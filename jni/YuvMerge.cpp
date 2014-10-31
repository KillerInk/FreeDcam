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
    JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_storeNextYuvFrame(JNIEnv *env, jobject thiz, jobject handler, jbyte data[]);
    JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_getMergedYuv(JNIEnv *env, jobject thiz, jobject handler, jint count);
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

void mergeFrame(YuvIntContainer* yuvi, jbyte data[])
{
    int frameSize = yuvi->_width * yuvi->_height;
    int i =0;
    for (int y = 0; y < yuvi->_height; y++)
    {
        for (int x = 0; x < yuvi->_width; x++)
        {
            int yPos = y * yuvi->_width + x;
            int uPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize;
            int vPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize + (frameSize/4);
            yuvi->_data[i].y += (data[yPos] & 0xff);
            yuvi->_data[i].u += (data[uPos] & 0xff);
            yuvi->_data[i].v += (data[vPos] & 0xff);
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

JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_storeNextYuvFrame(JNIEnv *env, jobject thiz, jobject handler, jbyte data[])
{
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    mergeFrame(yuvi, data);
    delete[] data;
    data = NULL;
}

JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_getMergedYuv(JNIEnv *env, jobject thiz, jobject handler, jint count)
{
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    //yuv420sp previewsize in bytes for 2560x1440 = 5529600 bytes = w x h + (w x h) /4 *2
    int yuvsize = yuvi->_width * yuvi->_height + (yuvi->_width + yuvi->_height)/4 *2;
    jbyte *dataToRet = (jbyte *)malloc(yuvsize);

    int i = 0;
    int frameSize = yuvi->_width * yuvi->_height;
    for (int y = 0; y < yuvi->_height; y++) {
        for (int x = 0; x < yuvi->_width; x++) {
            int yPos = y * yuvi->_width + x;
            int uPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize;
            int vPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize + (frameSize/4);
            dataToRet[yPos] = (yuvi->_data[i].y /count & 0xff);
            dataToRet[uPos] = (yuvi->_data[i].u /count & 0xff);
            dataToRet[vPos] = (yuvi->_data[i].v /count & 0xff);
            i++;
        }
    }
    return (jobject) dataToRet;
}