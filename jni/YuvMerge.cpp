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
    LOGD("Start Merging Frame");
    int frameSize = yuvi->_width * yuvi->_height;
    int i =0, yPos, uPos, vPos;
    for (int y = 0; y < yuvi->_height; y++)
    {
        for (int x = 0; x < yuvi->_width; x++)
        {
            yPos = y * yuvi->_width + x;
            uPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize;
            vPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize + (frameSize/4);
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
                if(x == 80 && y == 80)
                    LOGD("Y: %i dataY: %i", yuvi->_data[i].y, (int)data[yPos]);
                yuvi->_data[i].y += 0xff & data[yPos];
                if(x == 80 && y == 80)
                    LOGD("Yafter: %i", yuvi->_data[i].y);
                yuvi->_data[i].u += 0xff & data[uPos];
                yuvi->_data[i].v += 0xff & data[vPos];
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
    unsigned char* nativedata = (unsigned char*) env->GetByteArrayElements(data,NULL);
    mergeFrame(yuvi, nativedata);
    delete[] nativedata;
    nativedata = NULL;
    LOGD("First Frame Merged");
    return env->NewDirectByteBuffer(yuvi, 0);
}

JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_release(JNIEnv *env, jobject thiz, jobject handler)
{
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);

    if(yuvi->_data != NULL)
    {
        delete[] yuvi->_data;
        yuvi->_data = NULL;
        yuvi->_width = NULL;
        yuvi->_height = NULL;
    }
    delete yuvi;
    yuvi = NULL;
}

JNIEXPORT void JNICALL Java_com_troop_yuv_Merge_storeNextYuvFrame(JNIEnv *env, jobject thiz, jobject handler, jbyteArray data)
{
    LOGD("get YuvIntContainer");
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    LOGD("Load data");
    unsigned char* nativedata = (unsigned char*) env->GetByteArrayElements(data,NULL);
    LOGD("data nextframe loaded");
    mergeFrame(yuvi, nativedata);
    LOGD("next frame merged");
    delete[] nativedata;
    data = NULL;
    LOGD("cleaned up");
}

JNIEXPORT jobject JNICALL Java_com_troop_yuv_Merge_getMergedYuv(JNIEnv *env, jobject thiz, jobject handler, jint count, jbyteArray byteArray)
{
    LOGD("get MergedYuv");
    YuvIntContainer* yuvi = (YuvIntContainer*) env->GetDirectBufferAddress(handler);
    LOGD("loaded yuvIntContainer");
    //yuv420sp previewsize in bytes for 2560x1440 = 5529600 bytes = w x h + (w x h) /4 *2
    int yuvsize = (yuvi->_width * yuvi->_height) + (yuvi->_width * yuvi->_height)/2;
    //LOGD("new filesize: %i", yuvsize);
    int yPos, uPos, vPos, i= 0;


    unsigned char * chararray = (unsigned char*)malloc(yuvsize);

    LOGD("Fill jbyteArray");
    int frameSize = yuvi->_width * yuvi->_height;
    for (int y = 0; y < yuvi->_height; y++) {
        for (int x = 0; x < yuvi->_width; x++)
        {
            yPos = y * yuvi->_width + x;
            uPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize;
            vPos = (y/2)*(yuvi->_width/2)+(x/2) + frameSize + (frameSize/4);
            if(yPos >= yuvsize || uPos >= yuvsize || vPos >= yuvsize)
                LOGD("Error: yPos:%i uPos:%i vPos:%i", yPos, uPos , vPos);

            if(x == 80 && y == 80)
                LOGD("Y: %i ", yuvi->_data[i].y);

            chararray[yPos] = (0xff & yuvi->_data[i].y /count); //cy;
            chararray[uPos] = (0xff & yuvi->_data[i].u /count); //cu;
            chararray[vPos] = (0xff & yuvi->_data[i].v /count);//cv;
            i++;
        }
    }
    LOGD("Filled jbyteArray");
    //jbyteArray dataToRet = env->NewByteArray(yuvsize);
    env->SetByteArrayRegion(byteArray, 0, yuvsize, reinterpret_cast<const jbyte*>(chararray));
    delete[] chararray;
    chararray = NULL;
    LOGD("filled Jnibytearray");
    return byteArray;
}