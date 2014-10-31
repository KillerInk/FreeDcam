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

extern "C"
{
    JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_storeYuvFrame(JNIEnv *env, jobject thiz, jbyteArray data, jint width, jint height);
    JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_release(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_storeNextYuvFrame(JNIEnv *env, jobject thiz, jobject handler, jbyteArray data);
    JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_getMergedYuv(JNIEnv *env, jobject thiz, jobject handler, jint count);
}

class yuv
{
public:
    int y;
    int u;
    int v;
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
    LOGD("Start Merging Frame");
    int frameSize = yuvi->_width * yuvi->_height;
    int i =0;
    for (int y = 0; y < yuvi->_height; y++)
    {
        for (int x = 0; x < yuvi->_width; x++)
        {
            int yPos = y * yuvi->_width + x;
            int uPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize;
            int vPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize + (frameSize/4);
            //LOGD("Have yuv byte possis");
            /*if(yuvi->_data[i].y == NULL)
            {
                //LOGD("Yuvi data is null");
                //    LOGD("%d", data);
                //yuv pix = new yuv();
                //yuvi->_data[i] = new yuv();
                yuvi->_data[i].y = (data[yPos]);
                yuvi->_data[i].u = (data[uPos]);
                yuvi->_data[i].v = (data[vPos]);
                //LOGD("Yuvi data is now not null");
            }
            else
            {*/
                if(x == 2 && y == 2)
                    LOGD("Y: %i dataY: %i", yuvi->_data[i].y, (int)data[yPos]);
                yuvi->_data[i].y += (int)data[yPos] & 0xff;
                if(x == 2 && y == 2)
                    LOGD("Yafter: %i", yuvi->_data[i].y);
                yuvi->_data[i].u += (int)data[uPos] & 0xff;
                yuvi->_data[i].v += (int)data[vPos] & 0xff;
            //}
            i++;
        }
    }
    LOGD("Done Merging Frame");
}

JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_storeYuvFrame(JNIEnv *env, jobject thiz, jbyteArray data, jint width, jint height)
{
    LOGD("Create YuvIntContainer");
    YuvIntContainer* yuvi = new YuvIntContainer(width, height);
    LOGD("Created YuvIntContainer");
    static unsigned char* nativedata = (unsigned char*) env->GetByteArrayElements(data,NULL);
    mergeFrame(yuvi, nativedata);
    LOGD("First Frame Merged");
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

JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_storeNextYuvFrame(JNIEnv *env, jobject thiz, jobject handler, jbyteArray data)
{
    LOGD("get YuvIntContainer");
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    LOGD("Load data");
    static unsigned char* nativedata = (unsigned char*) env->GetByteArrayElements(data,NULL);
    LOGD("data nextframe loaded");
    mergeFrame(yuvi, nativedata);
    LOGD("next frame merged");
    delete[] nativedata;
    data = NULL;
    LOGD("cleaned up");
}

JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_getMergedYuv(JNIEnv *env, jobject thiz, jobject handler, jint count)
{
    LOGD("get MergedYuv");
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    LOGD("loaded yuvIntContainer");
    //yuv420sp previewsize in bytes for 2560x1440 = 5529600 bytes = w x h + (w x h) /4 *2
    int yuvsize = (yuvi->_width * yuvi->_height) + (yuvi->_width * yuvi->_height)/2;
    LOGD("new filesize: %i", yuvsize);
    int yPos, uPos, vPos;


    unsigned char * chararray = (unsigned char*)malloc(yuvsize);

    LOGD("Fill jbyteArray");
    int i = 0;
    int frameSize = yuvi->_width * yuvi->_height;
    for (int y = 0; y < yuvi->_height; y++) {
        for (int x = 0; x < yuvi->_width; x++)
        {

            //if(dataToRet == NULL)
            //    LOGD("jbyteArray is null");
            //if(yuvi->_data[i].y == NULL)
                //LOGD("yuvi data.y is null");

            yPos = y * yuvi->_width + x;
            uPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize;
            vPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize + (frameSize/4);
            if(yPos >= yuvsize || uPos >= yuvsize || vPos >= yuvsize)
                LOGD("Error: yPos:%i uPos:%i vPos:%i", yPos, uPos , vPos);
            //LOGD("have byte positions");

            unsigned char cy = (unsigned char)(yuvi->_data[i].y /count);
            unsigned char cu = (unsigned char)(yuvi->_data[i].u /count);
            unsigned char cv = (unsigned char)(yuvi->_data[i].v /count);
            if(x == 2 && y == 2)
                LOGD("Y: %i cY: %c", yuvi->_data[i].y, cy);
            //LOGD("y:%i u:%i v:%i", yuvi->_data[i].y, yuvi->_data[i].u, yuvi->_data[i].v);

            chararray[yPos] = cy;
            //LOGD("set cy char %c", cy);
            chararray[uPos] = cu;
            //LOGD("set cy char %c", cu);
            chararray[vPos] = cv;
            //LOGD("set cy char %c", cv);
            /*jbyte *jy = (jbyte*) (yuvi->_data[i].y /count & 0xff);
            jbyte *ju = (jbyte*) (yuvi->_data[i].u /count & 0xff);
            jbyte *jv = (jbyte*) (yuvi->_data[i].v /count & 0xff);
            LOGD("loaded jbytes");
            env->SetByteArrayRegion(dataToRet, yPos, 1, jy);
            env->SetByteArrayRegion(dataToRet, uPos, 1, ju);
            env->SetByteArrayRegion(dataToRet, vPos, 1, jv);*/
            i++;
            //(jbyte*)((yuvi->_data[i].v /count) & 0xff)
        }
    }
    LOGD("Filled jbyteArray");
    jbyteArray dataToRet = env->NewByteArray(yuvsize);
    env->SetByteArrayRegion(dataToRet, 0, yuvsize, reinterpret_cast<const jbyte*>(chararray));
    delete chararray;
    chararray = NULL;
    LOGD("filled Jnibytearray");
    return dataToRet;
}