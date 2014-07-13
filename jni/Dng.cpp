#include <jni.h>
#include <fstream>
#include <sstream>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sched.h>
#include <cmath>
#include <errno.h>

#define  LOG_TAG    "DEBUG"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

extern "C"
  {
//store
	JNIEXPORT jobject JNICALL Java_com_jni_dng_1utils_JniBitmapHolder_jniStoreBitmapData(JNIEnv * env, jobject obj, jobject bitmap);


  }

class JniBitmap
  {
  public:
    uint32_t* _storedBitmapPixels;
    AndroidBitmapInfo _bitmapInfo;
    JniBitmap()
      {
      _storedBitmapPixels = NULL;
      }
  };

/**store java bitmap as JNI data*/  //
JNIEXPORT jobject JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniStoreBitmapData(JNIEnv * env, jobject obj, jobject bitmap)
  {
	JniBitmap* map = createNativeBitmap(env, bitmap);
  return env->NewDirectByteBuffer(map, 0);
  }
