#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <android/bitmap.h>
#include <cstring>
#include <unistd.h>

#define  LOG_TAG    "DEBUG"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

extern "C"
  {
  //store
  JNIEXPORT jobject JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniStoreBitmapData(JNIEnv * env, jobject obj, jobject bitmap);

  //get
  JNIEXPORT jobject JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniGetBitmapFromStoredBitmapData(JNIEnv * env, jobject obj, jobject handle);
  //free
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniFreeBitmapData(JNIEnv * env, jobject obj, jobject handle);
  //rotate 90 degrees CCW
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniRotateBitmapCcw90(JNIEnv * env, jobject obj, jobject handle);
  //rotate 90 degrees CW
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniRotateBitmapCw90(JNIEnv * env, jobject obj, jobject handle);
  //rotate 180 degrees
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniRotateBitmap180(JNIEnv * env, jobject obj, jobject handle);
  //crop
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniCropBitmap(JNIEnv * env, jobject obj, jobject handle, uint32_t left, uint32_t top, uint32_t right, uint32_t bottom);
  //scale using nearest neighbor
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniScaleNNBitmap(JNIEnv * env, jobject obj, jobject handle, uint32_t newWidth, uint32_t newHeight);
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniAddImageIntoImage(JNIEnv * env, jobject obj, jobject handle, jobject bitmap, jint margineX, jint margineY);
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniToneMapImages(JNIEnv * env, jobject obj, jobject handle, jobject high, jobject low);
  JNIEXPORT jint JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniHeight(JNIEnv * env, jobject obj, jobject handle);
  JNIEXPORT jint JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniWidth(JNIEnv * env, jobject obj, jobject handle);
  JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniSave(JNIEnv * env, jobject obj, jobject handle, jobject fileoutputstream);
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

jobject createjBitmap(JNIEnv * env, JniBitmap* jniBitmap)
{
	  jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
	  jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
	  jstring configName = env->NewStringUTF("ARGB_8888");
	  jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
	  jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
	  jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass, valueOfBitmapConfigFunction, configName);
	  jobject newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction, jniBitmap->_bitmapInfo.width, jniBitmap->_bitmapInfo.height, bitmapConfig);

	  //
	  // putting the pixels into the new bitmap:
	  //
	  int ret;
	  void* bitmapPixels;
	  if ((ret = AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels)) < 0)
	    {
	    LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	    return NULL;
	    }
	  uint32_t* newBitmapPixels = (uint32_t*) bitmapPixels;
	  int pixelsCount = jniBitmap->_bitmapInfo.height * jniBitmap->_bitmapInfo.width;
	  memcpy(newBitmapPixels, jniBitmap->_storedBitmapPixels, sizeof(uint32_t) * pixelsCount);
	  AndroidBitmap_unlockPixels(env, newBitmap);
	  return newBitmap;
}

/**crops the bitmap within to be smaller. note that no validations are done*/ //
JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniCropBitmap(JNIEnv * env, jobject obj, jobject handle, uint32_t left, uint32_t top, uint32_t right, uint32_t bottom)
  {
  JniBitmap* jniBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
  if (jniBitmap->_storedBitmapPixels == NULL)
    return;
  uint32_t* previousData = jniBitmap->_storedBitmapPixels;
  uint32_t oldWidth = jniBitmap->_bitmapInfo.width;
  uint32_t newWidth = right - left, newHeight = bottom - top;
  uint32_t* newBitmapPixels = new uint32_t[newWidth * newHeight];
  uint32_t whereToGet =oldWidth * top + left;
  uint32_t whereToPut = 0;
  for (int y = 0;y < newHeight; y++)
    {
      for (int x = 0; x < newWidth; x++)
  	  {
  		  uint32_t pixel = previousData[oldWidth * (y + top) + (x + left)];
  		  newBitmapPixels[newWidth * y + x] = pixel;
  	  }
    }

  //done copying , so replace old data with new one
  delete[] previousData;
  jniBitmap->_storedBitmapPixels = newBitmapPixels;
  jniBitmap->_bitmapInfo.width = newWidth;
  jniBitmap->_bitmapInfo.height = newHeight;
  }

JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniRotateBitmap180(JNIEnv * env, jobject obj, jobject handle)
  {
  JniBitmap* jniBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
  if (jniBitmap->_storedBitmapPixels == NULL)
    return;
  uint32_t* previousData = jniBitmap->_storedBitmapPixels;
  AndroidBitmapInfo bitmapInfo = jniBitmap->_bitmapInfo;
  uint32_t* newBitmapPixels = new uint32_t[bitmapInfo.height * bitmapInfo.width];
  int whereToGet = 0;

  for (int y = 0;y < bitmapInfo.height; y++)
  {
      for (int x = 0; x < bitmapInfo.width; x++)
	  {
		  uint32_t pixel = previousData[whereToGet++];

		  newBitmapPixels[bitmapInfo.width * (bitmapInfo.height - 1 - y) + x] = pixel;
	  }
  }

  delete[] previousData;
  jniBitmap->_storedBitmapPixels = newBitmapPixels;
  uint32_t temp = bitmapInfo.width;
  bitmapInfo.width = bitmapInfo.height;
  bitmapInfo.height = temp;
  }

/**rotates the inner bitmap data by 90 degrees counter clock wise*/ //
JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniRotateBitmapCcw90(JNIEnv * env, jobject obj, jobject handle)
  {
  JniBitmap* jniBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
  if (jniBitmap->_storedBitmapPixels == NULL)
    return;
  uint32_t* previousData = jniBitmap->_storedBitmapPixels;
  AndroidBitmapInfo bitmapInfo = jniBitmap->_bitmapInfo;
  uint32_t* newBitmapPixels = new uint32_t[bitmapInfo.height * bitmapInfo.width];
  int whereToGet = 0;
  // XY. ... ... ..X
  // ...>Y..>...>..Y
  // ... X.. .YX ...
  for (int x = 0; x < bitmapInfo.width; ++x)
    for (int y = bitmapInfo.height - 1; y >= 0; --y)
      {
      //take from each row (up to bottom), from left to right
      uint32_t pixel = previousData[whereToGet++];
      newBitmapPixels[bitmapInfo.width * y + x] = pixel;
      }
  delete[] previousData;
  jniBitmap->_storedBitmapPixels = newBitmapPixels;
  uint32_t temp = bitmapInfo.width;
  bitmapInfo.width = bitmapInfo.height;
  bitmapInfo.height = temp;
  }

JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniRotateBitmapCw90(JNIEnv * env, jobject obj, jobject handle)
  {
  JniBitmap* jniBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
  if (jniBitmap->_storedBitmapPixels == NULL)
    return;
  uint32_t* previousData = jniBitmap->_storedBitmapPixels;
  AndroidBitmapInfo bitmapInfo = jniBitmap->_bitmapInfo;
  uint32_t* newBitmapPixels = new uint32_t[bitmapInfo.height * bitmapInfo.width];
  int whereToGet = 0;
  // XY. ..X ... ...
  // ...>..Y>...>Y..
  // ... ... .YX X..
  for (int x = bitmapInfo.width - 1; x >= 0; --x)
    for (int y = 0; y < bitmapInfo.height; ++y)
      {
      //take from each row (up to bottom), from left to right
      uint32_t pixel = previousData[whereToGet++];
      newBitmapPixels[bitmapInfo.width * y + x] = pixel;
      }
  delete[] previousData;
  jniBitmap->_storedBitmapPixels = newBitmapPixels;
  uint32_t temp = bitmapInfo.width;
  bitmapInfo.width = bitmapInfo.height;
  bitmapInfo.height = temp;
  }

/**free bitmap*/  //
JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniFreeBitmapData(JNIEnv * env, jobject obj, jobject handle)
  {
  JniBitmap* jniBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
  if (jniBitmap->_storedBitmapPixels == NULL)
    return;
  delete[] jniBitmap->_storedBitmapPixels;
  jniBitmap->_storedBitmapPixels = NULL;

  delete jniBitmap;
  }

/**restore java bitmap (from JNI data)*/  //
JNIEXPORT jobject JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniGetBitmapFromStoredBitmapData(JNIEnv * env, jobject obj, jobject handle)
  {
  JniBitmap* jniBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
  if (jniBitmap->_storedBitmapPixels == NULL)
    {
    LOGD("no bitmap data was stored. returning null...");
    return NULL;
    }

  return createjBitmap(env, jniBitmap);
  }

/**store java bitmap as JNI data*/  //
JNIEXPORT jobject JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniStoreBitmapData(JNIEnv * env, jobject obj, jobject bitmap)
  {
  AndroidBitmapInfo bitmapInfo;
  uint32_t* storedBitmapPixels = NULL;
  //LOGD("reading bitmap info...");
  int ret;
  if ((ret = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) < 0)
    {
    LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
    return NULL;
    }
  //LOGD("width:%d height:%d stride:%d", bitmapInfo.width, bitmapInfo.height, bitmapInfo.stride);
  if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
    {
    LOGE("Bitmap format is not RGBA_8888!");
    return NULL;
    }
  //
  //read pixels of bitmap into native memory :
  //
  //LOGD("reading bitmap pixels...");
  void* bitmapPixels;
  if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0)
    {
    LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    return NULL;
    }
  uint32_t* src = (uint32_t*) bitmapPixels;
  storedBitmapPixels = new uint32_t[bitmapInfo.height * bitmapInfo.width];
  int pixelsCount = bitmapInfo.height * bitmapInfo.width;
  memcpy(storedBitmapPixels, src, sizeof(uint32_t) * pixelsCount);
  AndroidBitmap_unlockPixels(env, bitmap);
  JniBitmap *jniBitmap = new JniBitmap();
  jniBitmap->_bitmapInfo = bitmapInfo;
  jniBitmap->_storedBitmapPixels = storedBitmapPixels;
  return env->NewDirectByteBuffer(jniBitmap, 0);
  }

/**scales the image using the fastest, simplest algorithm called "nearest neighbor" */ //
JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniScaleNNBitmap(JNIEnv * env, jobject obj, jobject handle, uint32_t newWidth, uint32_t newHeight)
  {
  JniBitmap* jniBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
  if (jniBitmap->_storedBitmapPixels == NULL)
    return;
  uint32_t oldWidth = jniBitmap->_bitmapInfo.width;
  uint32_t oldHeight = jniBitmap->_bitmapInfo.height;
  uint32_t* previousData = jniBitmap->_storedBitmapPixels;
  uint32_t* newBitmapPixels = new uint32_t[newWidth * newHeight];
  int x2, y2;
  int whereToPut = 0;
  for (int y = 0; y < newHeight; ++y)
    {
    for (int x = 0; x < newWidth; ++x)
      {
      x2 = x * oldWidth / newWidth;
      if (x2 < 0)
        x2 = 0;
      else if (x2 >= oldWidth)
        x2 = oldWidth - 1;
      y2 = y * oldHeight / newHeight;
      if (y2 < 0)
        y2 = 0;
      else if (y2 >= oldHeight)
        y2 = oldHeight - 1;
      newBitmapPixels[whereToPut++] = previousData[(y2 * oldWidth) + x2];
      //same as : newBitmapPixels[(y * newWidth) + x] = previousData[(y2 * oldWidth) + x2];
      }
    }

  delete[] previousData;
  jniBitmap->_storedBitmapPixels = newBitmapPixels;
  jniBitmap->_bitmapInfo.width = newWidth;
  jniBitmap->_bitmapInfo.height = newHeight;
  }

JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniAddImageIntoImage(JNIEnv * env, jobject obj, jobject handle, jobject bitmap,
		jint margineX, jint margineY)
{
	JniBitmap* jniBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
	JniBitmap* jniToDrawBitmap = (JniBitmap*) env->GetDirectBufferAddress(bitmap);
	if (jniBitmap->_storedBitmapPixels == NULL || jniToDrawBitmap->_storedBitmapPixels == NULL)
	    return;
	//uint32_t* previousData = jniBitmap->_storedBitmapPixels;
	AndroidBitmapInfo bitmapInfo = jniBitmap->_bitmapInfo;
	AndroidBitmapInfo bitmapInfoSecond = jniToDrawBitmap->_bitmapInfo;
	uint32_t* newBitmapPixels = jniBitmap->_storedBitmapPixels;
	int whereToGet = 0;

	for (int y = 0;y < bitmapInfoSecond.height; y++)
	{
	    for (int x = 0; x < bitmapInfoSecond.width; x++)
		{
			uint32_t pixel = jniToDrawBitmap->_storedBitmapPixels[whereToGet++];

				newBitmapPixels[bitmapInfo.width * (y + margineY) + (x + margineX)] = pixel;
		}
	}
	jniBitmap->_storedBitmapPixels = newBitmapPixels;
	if(jniToDrawBitmap->_storedBitmapPixels != NULL)
	  {
		  delete[] jniToDrawBitmap->_storedBitmapPixels;
		  jniToDrawBitmap->_storedBitmapPixels = NULL;
	  }
}

class rgba
{
public:
    uint8_t red;
    uint8_t green;
    uint8_t blue;
    uint8_t alpha;
    rgba()
    {}
};


rgba unPackPixelFromByte(uint32_t pixel)
{
	rgba toret;
	toret.alpha = (int)((pixel >> 24 ) & 0xFF);
	toret.red = (int) ((pixel >> 16) & 0xFF);
	toret.green = (int)((pixel >> 8) & 0xFF);
	toret.blue = (int) (pixel & 0xFF);



	return toret;
}

uint32_t packPixelToByte(rgba rgb)
{
	uint32_t pixel = (rgb.alpha << 24) |
					 (rgb.red << 16) |
					 (rgb.green << 8) |
					 rgb.blue;
	return pixel;
}

uint32_t tonemapColors(rgba base, rgba high, rgba low)
{
	rgba toReturn;
	toReturn.blue = (base.blue + high.blue + low.blue)/3;
	toReturn.red = (base.red + high.red + low.red)/3;
	toReturn.green = (base.green + high.green + low.green)/3;

	uint32_t pixel = packPixelToByte(toReturn);
	return pixel;
}

JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniToneMapImages(JNIEnv * env, jobject obj, jobject handle, jobject high,
		jobject low)
{
	JniBitmap* baseBitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
	JniBitmap* highBitmap = (JniBitmap*) env->GetDirectBufferAddress(high);
	JniBitmap* lowBitmap = (JniBitmap*) env->GetDirectBufferAddress(low);
	AndroidBitmapInfo bitmapInfo = baseBitmap->_bitmapInfo;
	int whereToGet = 0;
	for (int y = 0;y < bitmapInfo.height; y++)
		{
		    for (int x = 0; x < bitmapInfo.width; x++)
			{
				uint32_t bpixel = baseBitmap->_storedBitmapPixels[whereToGet];
				rgba basePixelColor = unPackPixelFromByte(bpixel);
				uint32_t hpixel = highBitmap->_storedBitmapPixels[whereToGet];
				rgba highPixelColor = unPackPixelFromByte(hpixel);
				uint32_t lpixel = lowBitmap->_storedBitmapPixels[whereToGet];
				rgba lowPixelColor = unPackPixelFromByte(lpixel);
				uint32_t pixel = tonemapColors(basePixelColor, highPixelColor, lowPixelColor);
				baseBitmap->_storedBitmapPixels[whereToGet] = pixel;
				whereToGet++;
			}
		}
}

JNIEXPORT jint JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniWidth(JNIEnv * env, jobject obj, jobject handle)
{
	JniBitmap* bitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
	if(bitmap == NULL)
		return 0;
	AndroidBitmapInfo bitmapInfo = bitmap->_bitmapInfo;
	return (jint) bitmapInfo.width;;
}

JNIEXPORT jint JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniHeight(JNIEnv * env, jobject obj, jobject handle)
{
	JniBitmap* bitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
	if(bitmap == NULL)
		return 0;
	AndroidBitmapInfo bitmapInfo = bitmap->_bitmapInfo;
	int ret = bitmapInfo.height;

	return (jint) ret;
}

void compress(JNIEnv* env, jobject fOut, jobject bitmap)
{
	jclass bitmapCompressClass = env->FindClass("android/graphics/Bitmap$CompressFormat");
	jstring enumValue = env->NewStringUTF("JPEG");
	jmethodID valueOfBitmapCompressFunction = env->GetStaticMethodID(bitmapCompressClass, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$CompressFormat;");
	jobject bitmapCompress = env->CallStaticObjectMethod(bitmapCompressClass, valueOfBitmapCompressFunction, enumValue);
	jclass bitmapCls = env->GetObjectClass(bitmap);
	jmethodID compressBitmapMethodID = env->GetMethodID(bitmapCls,"compress","(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z");

	env->CallBooleanMethod(bitmap, compressBitmapMethodID, bitmapCompress, (jint)100, fOut);

}

JNIEXPORT void JNICALL Java_com_jni_bitmap_1operations_JniBitmapHolder_jniSave(JNIEnv * env, jobject obj, jobject handle, jobject fileoutputstream)
{
	JniBitmap* bitmap = (JniBitmap*) env->GetDirectBufferAddress(handle);
	jobject bitmapToSave = createjBitmap(env, bitmap);
	compress(env, fileoutputstream, bitmapToSave);
	jclass bitmapCls = env->GetObjectClass(bitmapToSave);
	jmethodID recycle = env->GetMethodID(bitmapCls,"recycle","()V");
	env->CallVoidMethod(bitmapToSave, recycle, bitmapCls);
}




