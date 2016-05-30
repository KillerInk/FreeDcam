#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/bitmap.h>

#include "../libraw/libraw.h"
#include "../libraw/libraw_types.h"

#define TAG_DEBUG "RAW"

#define  LOG_TAG    "freedcam.Librawutils"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)


extern "C" JNIEXPORT jbyteArray JNICALL Java_com_defcomk_jni_libraw_RawUtils_BitmapExtractor(JNIEnv * env, jobject obj,jbyteArray bufferBytes, jint blackLevel)
{
	char outfn[1024];
	LibRaw raw;
	jbyte* buffer;
	jsize len;
	jbyteArray ret;
	libraw_processed_image_t* image = NULL;

	jboolean buff = false;
	int kv = blackLevel;



#define P1  raw.imgdata.idata
#define S   raw.imgdata.sizes
#define C   raw.imgdata.color
#define T   raw.imgdata.thumbnail
#define P2  raw.imgdata.other
#define OUT raw.imgdata.params


	OUT.output_bps = 8;
//	OUT.gamm[0] = OUT.gamm[1] = OUT.no_auto_bright = 1;
//	OUT.user_black = kv;
	OUT.user_qual = 0;
	OUT.half_size=1;

//	OUT.use_auto_wb = 1;
//	OUT.android_raw = 1;
//	OUT.android_aperture = 2.0f;
//	OUT.android_focal = 3.83f;

    LOGD("init thumb Ext");

	len = (env)->GetArrayLength(bufferBytes);

	LOGD("Buffer Length",len);

	buffer = (env)->GetByteArrayElements(bufferBytes,NULL);

	raw.open_buffer(buffer,len);

	int errorCode;
 	LOGD("buffer opened");
	raw.unpack();
	 LOGD("buffer unpacked");
	raw.dcraw_process();
	 LOGD("buffer demosaiced");



	image = raw.dcraw_make_mem_image(&errorCode);
	 LOGD("pulling to memeory");

    image->type = LIBRAW_IMAGE_JPEG;


	ret = (env)->NewByteArray(image->data_size);

	(env)->SetByteArrayRegion(ret,0,image->data_size,(jbyte *)image->data);

	 LOGD("free input bytwa");
	(env)->ReleaseByteArrayElements(bufferBytes,buffer,0);
	// Finally, let us free the image processor for work with the next image

		//raw.recycle();
		raw.dcraw_clear_mem(image);

	return ret;
}

extern "C" JNIEXPORT jbyteArray JNICALL Java_com_defcomk_jni_libraw_RawUtils_unpackThumbnailBytes(JNIEnv * env, jobject obj, jstring jfilename)
{
	LibRaw raw;

    __android_log_print(ANDROID_LOG_DEBUG, TAG_DEBUG, "raw unpackThumbnailBytes");
	jboolean bIsCopy;
	const char* strFilename = (env)->GetStringUTFChars(jfilename , &bIsCopy);

	// Open the file and read the metadata
	raw.open_file(strFilename);
	__android_log_print(ANDROID_LOG_DEBUG, TAG_DEBUG, "raw file : %c", strFilename);


	(env)->ReleaseStringUTFChars(jfilename, strFilename);// release jstring

	// Let us unpack the image
	raw.unpack_thumb();

	jsize length = raw.imgdata.thumbnail.tlength;

	jbyteArray jb = (env)->NewByteArray(length);
	env->SetByteArrayRegion(jb,0,length,(jbyte *)raw.imgdata.thumbnail.thumb);

	// Finally, let us free the image processor for work with the next image
	raw.recycle();
	return jb;
}

extern "C" JNIEXPORT void JNICALL Java_com_defcomk_jni_libraw_RawUtils_unpackRawByte(JNIEnv * env, jobject obj, jstring jfilename, jbyteArray bufferBytes, jint blackLevel, jfloat aperture, jfloat focalLength, jfloat shutterSpeed, jfloat isoSpeed)
{
    jclass classHashMap = (env)->FindClass("java/util/HashMap");
    jmethodID methodPut = (env)->GetMethodID(classHashMap, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
	//libraw_data_t * data;
	char outfn[1024];
	LibRaw raw;
	jbyte* buffer;
	jsize len;
	jbyteArray ret;
	libraw_processed_image_t * image;
	jboolean buff = false;
	int kv = blackLevel;
	float Fnumber = aperture;
	float FocalLenth = focalLength;
	float ShutterSpeed = shutterSpeed;
	float IsoSpeed = isoSpeed;


#define P1  raw.imgdata.idata
#define S   raw.imgdata.sizes
#define C   raw.imgdata.color
#define T   raw.imgdata.thumbnail
#define P2  raw.imgdata.other
#define OUT raw.imgdata.params

	OUT.output_tiff = 1;
	OUT.output_bps = 16;
	OUT.gamm[0] = OUT.gamm[1] = OUT.no_auto_bright = 1;
	OUT.user_black = kv;
	OUT.user_qual = 4;

	//OUT.green_matching = 1;


	OUT.use_auto_wb = 1;


	//P2.aperture = 2.0f;
	//P2.focal_len = 3.83f;
	//P2.iso_speed = 100.0f;

	/*OUT.android_raw = 1;
	OUT.android_aperture = 2.0f;
	OUT.android_focal = 3.83f;*/






    __android_log_print(ANDROID_LOG_DEBUG, TAG_DEBUG, "raw Droid");
	jboolean bIsCopy;
	const char* strFilename = (env)->GetStringUTFChars(jfilename , &bIsCopy);

	len = (env)->GetArrayLength(bufferBytes);

	buffer = (env)->GetByteArrayElements(bufferBytes,NULL);


	// Open the file and read the metadata
	//raw.open_file(strFilename);



	raw.open_buffer(buffer,len);
	//__android_log_print(ANDROID_LOG_DEBUG, TAG_DEBUG, "raw file : %c", strFilename);

	__android_log_print(ANDROID_LOG_DEBUG, TAG_DEBUG, "Buffer Length In JNI ");


	//

	// Let us unpack the image
	//raw.unpack_thumb();

	raw.unpack();
	raw.dcraw_process();
	//raw.dcraw_make_mem_image();
	raw.dcraw_ppm_tiff_writer(strFilename);
	(env)->ReleaseStringUTFChars(jfilename, strFilename);// release j

	//raw.dcraw_ppm_tiff_writer(outfn);
	//ret = (*env)->NewByteArray(env, image->data_size);
	//	(*env)->SetByteArrayRegion(env, ret, 0, image->data_size, (jbyte *) image->data);
	//	(*env)->ReleaseByteArrayElements(env, bufferBytes, buffer, 0);


	//image = raw.dcraw_make_mem_image();

	///ret = (env)->NewByteArray(image->data_size);
	//	(env)->SetByteArrayRegion(ret,0,image->data_size,(jbyte *)image->data);
	//	(env)->ReleaseByteArrayElements(bufferBytes,buffer,0);
	// Finally, let us free the image processor for work with the next image

		raw.recycle();
	//return bufferBytes;
}

extern "C" JNIEXPORT jint JNICALL Java_com_defcomk_jni_libraw_RawUtils_unpackThumbnailToFile(JNIEnv * env, jobject obj, jstring jrawfilename, jstring jthumbfilename) {
    LibRaw raw;

	jboolean bIsCopy;
	const char* strRawFilename = (env)->GetStringUTFChars(jrawfilename , &bIsCopy);
	const char* strThumbFilename = (env)->GetStringUTFChars(jthumbfilename , &bIsCopy);

	// Open the file and read the metadata
	raw.open_file(strRawFilename);

	// Let us unpack the image
	raw.unpack_thumb();

	jint ret = raw.dcraw_thumb_writer(strThumbFilename);

	(env)->ReleaseStringUTFChars(jrawfilename, strRawFilename);// release jstring
	(env)->ReleaseStringUTFChars(jthumbfilename, strThumbFilename);// release jstring

	// Finally, let us free the image processor for work with the next image
	raw.recycle();
    return ret;
}

extern "C" JNIEXPORT void JNICALL Java_com_defcomk_jni_libraw_RawUtils_parseExif(JNIEnv * env, jobject obj, jstring jfilename, jobject jexifMap) {

//    jclass ExifInterface = (env)->FindClass("android/media/ExifInterface");
//    jmethodID SetAttributeMethod = (env)->GetMethodID(ExifInterface, "setAttribute", "(Ljava/lang/String;Ljava/lang/String;)V");

    jclass classHashMap = (env)->FindClass("java/util/HashMap");
    jmethodID methodPut = (env)->GetMethodID(classHashMap, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    LibRaw raw;

	jboolean bIsCopy;

	const char* strFilename = (env)->GetStringUTFChars(jfilename , &bIsCopy);

	raw.open_file(strFilename);

	(env)->ReleaseStringUTFChars(jfilename, strFilename);

    char buf[15];

    sprintf(buf, "%.0f", raw.imgdata.other.iso_speed);
	(env)->CallObjectMethod(jexifMap, methodPut, (env)->NewStringUTF("ISOSpeedRatings"), (env)->NewStringUTF(buf));

    sprintf(buf, "%u", raw.imgdata.sizes.iwidth);
	(env)->CallObjectMethod(jexifMap, methodPut, (env)->NewStringUTF("ImageWidth"), (env)->NewStringUTF(buf));

	sprintf(buf, "%u", raw.imgdata.sizes.iheight);
    (env)->CallObjectMethod(jexifMap, methodPut, (env)->NewStringUTF("ImageLength"), (env)->NewStringUTF(buf));

    sprintf(buf, "%ld", raw.imgdata.other.timestamp);
    (env)->CallObjectMethod(jexifMap, methodPut, (env)->NewStringUTF("DateTime"), (env)->NewStringUTF(buf));

    sprintf(buf, "%f", raw.imgdata.other.shutter);
    (env)->CallObjectMethod(jexifMap, methodPut, (env)->NewStringUTF("Shutter"), (env)->NewStringUTF(buf));

    sprintf(buf, "%f", raw.imgdata.other.aperture);
    (env)->CallObjectMethod(jexifMap, methodPut, (env)->NewStringUTF("FNumber"), (env)->NewStringUTF(buf));

    sprintf(buf, "%d", raw.imgdata.sizes.flip);
    (env)->CallObjectMethod(jexifMap, methodPut, (env)->NewStringUTF("Orientation"), (env)->NewStringUTF(buf));

	raw.recycle();
}

extern "C" JNIEXPORT jobject JNICALL Java_com_defcomk_jni_libraw_RawUtils_unpackRAW(JNIEnv * env, jobject obj, jstring jfilename) {
	int ret;
	LibRaw raw;
#define P1 raw.imgdata.idata
#define S raw.imgdata.sizes
#define C raw.imgdata.color
#define T raw.imgdata.thumbnail
#define P2 raw.imgdata.other
#define OUT raw.imgdata.params
	OUT.no_auto_bright = 1;
	OUT.use_camera_wb = 1;
	OUT.output_bps = 8;
	OUT.user_qual = 0;
	OUT.half_size = 1;
	jboolean bIsCopy;
	void *bitmapPixels;

	//raw.recycle();

	const char *strFilename = (env)->GetStringUTFChars(jfilename, &bIsCopy);
	raw.open_file(strFilename);
	LOGD("File opend");

	ret = raw.unpack();
	LOGD("unpacked img %i", ret);
	ret = raw.dcraw_process();
	LOGD("processing dcraw %i", ret);
	libraw_processed_image_t *image = raw.dcraw_make_mem_image(&ret);

	LOGD("processed image, creating bitmap");
	if(image->width == 0 || image->height == 0)
		return NULL;

	jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass, valueOfBitmapConfigFunction, configName);
    jobject newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction, image->width, image->height, bitmapConfig);
	if(image->data_size > 0)
	{
    	LOGD("orginal size: %i",image->data_size);

		if ((ret = AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels)) < 0)
        {
        	LOGD("AndroidBitmap_lockPixels() failed ! error=%d", ret);

             return NULL;
        }
        LOGD("pixel locked");
        uint32_t *newBitmapPixels = (uint32_t*) bitmapPixels;
        LOGD("memcopy start");
        int bufrow = 0;
        int size = image->width* image->height;
        for (int count = 0; count < size; count++)
        {
        	uint32_t p =  (0xff << 24) |
        					(image->data[bufrow+2] << 16) |
                            (image->data[bufrow+1] << 8) |
                            image->data[bufrow+0];
            newBitmapPixels[count] = p;
            bufrow += 3;
        }
        LOGD("memcopy end");


		LOGD("dcraw mem cleared");
        AndroidBitmap_unlockPixels(env, newBitmap);
        LOGD("pixel unlocked");
		//raw.free_image();
		LibRaw::dcraw_clear_mem(image);

	}
	(env)->ReleaseStringUTFChars(jfilename, strFilename);
	raw.recycle();
	LOGD("rawdata recycled");

    return newBitmap;
}
