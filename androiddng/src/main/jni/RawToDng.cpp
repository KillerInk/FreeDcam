#include <jni.h>
#include <tiff/libtiff/tiffio.h>
//#include <include/tif_dir.h>
//#include <include/tif_config.h>
#include <stdio.h>
//#include <exception>
#include <assert.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#include <tiff/libtiff/tif_dir.h>
#define  LOG_TAG    "freedcam.RawToDngNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

typedef unsigned long long uint64;
typedef unsigned short UINT16;
typedef unsigned char uint8;

extern "C"
{
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetExifData(JNIEnv *env, jobject thiz,jobject handler,
    jint iso,
    jdouble expo,
    jint flash,
    jfloat fNum,
    jfloat focalL,
    jstring imagedescription,
    jstring orientation,
    jdouble exposureIndex);
    JNIEXPORT jobject JNICALL Java_com_troop_androiddng_RawToDng_Create(JNIEnv *env, jobject thiz);

    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jobject handler, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, jint widht, jint height);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT jlong JNICALL Java_com_troop_androiddng_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jobject handler, jint height);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jobject handler,jbyteArray fileBytes, jstring fileout);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jobject handler, jstring model, jstring make);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_Release(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT jint JNICALL Java_com_troop_androiddng_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz, jobject handler);


	JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
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
    	jboolean tight,
    	jint width,
    	jint height);
}


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
    bool tightRaw;
    long rawSize;


    int thumbheight, thumwidth;
    unsigned char* _thumbData;

    DngWriter()
    {
        gps = false;
    }
};

JNIEXPORT jlong JNICALL Java_com_troop_androiddng_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    return writer->rawSize;
}

JNIEXPORT jint JNICALL Java_com_troop_androiddng_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    return writer->rawheight;
}


JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jobject handler, jint height)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->rawheight = (int) height;
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jobject handler, jstring model, jstring make)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->_make = (char*) env->GetStringUTFChars(make,NULL);
    writer->_model = (char*) env->GetStringUTFChars(model,NULL);
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetExifData(JNIEnv *env, jobject thiz, jobject handler,
    jint iso,
    jdouble expo,
    jint flash,
    jfloat fNum,
    jfloat focalL,
    jstring imagedescription,
    jstring orientation,
    jdouble exposureIndex)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->_iso = iso;
    writer->_exposure =expo;
    writer->_flash = flash;
    writer->_imagedescription = (char*) env->GetStringUTFChars(imagedescription,NULL);
    writer->_orientation = (char*) env->GetStringUTFChars(orientation,NULL);
    writer->_fnumber = fNum;
    writer->_focallength = focalL;
    writer->_exposureIndex = exposureIndex;
}

JNIEXPORT jobject JNICALL Java_com_troop_androiddng_RawToDng_Create(JNIEnv *env, jobject thiz)
{
    DngWriter *writer = new DngWriter();
    return env->NewDirectByteBuffer(writer, 0);
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetGPSData(JNIEnv *env, jobject thiz,jobject handler, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime)
{
      DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
      writer->Altitude = (double)Altitude;
      writer->Latitude =  env->GetFloatArrayElements(Latitude,NULL);
      writer->Longitude = env->GetFloatArrayElements(Longitude,NULL);
      writer->Provider = (char*) env->GetStringUTFChars(Provider,NULL);
      writer->gpsTime = (long)(gpsTime);
      writer->gps = true;
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, int widht, int height)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->_thumbData = (unsigned char*) env->GetByteArrayElements(mThumb,NULL);
    writer->thumbheight = (int) height;
    writer->thumwidth = widht;
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_Release(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    if(writer->bayerBytes != NULL)
    {
        free(writer->bayerBytes);
        writer->bayerBytes = NULL;
    }
    /*if(writer->_thumbData != NULL)
        free(writer->_thumbData);*/
    if (writer != NULL)
        free(writer);
    writer = NULL;
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fileBytes, jstring fileout)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    LOGD("Try to set Bayerdata");
    writer->bayerBytes = new unsigned char[env->GetArrayLength(fileBytes)];
    LOGD("init bayerbytes");
    //writer->bayerBytes = (unsigned char*) env->GetByteArrayElements(fileBytes,NULL);
    memcpy(writer->bayerBytes, env->GetByteArrayElements(fileBytes,NULL), env->GetArrayLength(fileBytes));
    LOGD(" set Bayerdata");
    writer->fileSavePath = (char*)  env->GetStringUTFChars(fileout,NULL);
    writer->rawSize = env->GetArrayLength(fileBytes);
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
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
	jboolean tight,
	jint width,
	jint height)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);

    writer->blacklevel = new float[4] {blacklevel, blacklevel, blacklevel,blacklevel};
    writer->tightRaw = tight;
    writer->rowSize =rowSize;
    writer->colorMatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    writer->colorMatrix2 =env->GetFloatArrayElements(colorMatrix2, 0);
    writer->neutralColorMatrix = env->GetFloatArrayElements(neutralColor, 0);

    writer->fowardMatrix1 = env->GetFloatArrayElements(fowardMatrix1, 0);
    writer->fowardMatrix2 =env->GetFloatArrayElements(fowardMatrix2, 0);
    writer->reductionMatrix1 = env->GetFloatArrayElements(reductionMatrix1, 0);
    writer->reductionMatrix2 =env->GetFloatArrayElements(reductionMatrix2, 0);
    writer->noiseMatrix =env->GetFloatArrayElements(noiseMatrix, 0);

    writer->bayerformat = (char*)  env->GetStringUTFChars(bayerformat,0);
    writer->rawheight = height;
    writer->rawwidht = width;

}

TIFF *openfTIFF(char* fileSavePath)
{
    TIFF *tif;
    if (!(tif = TIFFOpen (fileSavePath, "w")))
    {
    	LOGD("error while creating outputfile");
    }
    return tif;
}


void writeIfd0(TIFF *tif, DngWriter *writer)
{
    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    LOGD("subfiletype");
    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, writer->rawwidht) != 0);
    LOGD("width");
    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, writer->rawheight) != 0);
    LOGD("height");
    assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
    LOGD("bitspersample");
    assert(TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA) != 0);
    LOGD("PhotometricCFA");
            //assert(TIFFSetField(tif, TIFFTAG_ROWSPERSTRIP, 480/2) != 0);
    assert(TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE) != 0);
    LOGD("Compression");
    TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
    LOGD("sampelsperpixel");
    TIFFSetField(tif, TIFFTAG_MAKE, writer->_make);
    LOGD("make");
    TIFFSetField(tif, TIFFTAG_MODEL, writer->_model);
    LOGD("model");
    try
    {
        if(0 == strcmp(writer->_orientation,"0") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_TOPLEFT);
        if(0 == strcmp(writer->_orientation,"90") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_RIGHTTOP);
        if(0 == strcmp(writer->_orientation,"180") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_BOTRIGHT);
        if(0 == strcmp(writer->_orientation,"270") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_LEFTBOT);
        LOGD("orientation");
    }
    catch(...)
    {
        LOGD("Caught NULL NOT SET Orientation");
    }
    assert(TIFFSetField(tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG) != 0);
    LOGD("planarconfig");
        //assert(TIFFSetField(tif, TIFFTAG_SAMPLESPERPIXEL, 3) != 0);
    TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreedCam by Troop");
    LOGD("software");
    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    LOGD("dngversion");
    TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
    LOGD("CameraModel");
    TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, writer->_imagedescription);
    LOGD("imagedescription");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, writer->colorMatrix1);
    LOGD("colormatrix1");
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, writer->neutralColorMatrix);
    LOGD("neutralMatrix");
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);

    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);

    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, writer->colorMatrix2);

        static const float cam_foward1[] = {
      		// R 	G     	B
      		0.6648, 0.2566, 0.0429, 0.197, 0.9994, -0.1964, -0.0894, -0.2304, 1.145
      	};

      	static const float cam_foward2[] = {
        	0.6617, 0.3849, -0.0823, 0.24, 1.1138, -0.3538, -0.0062, -0.1147, 0.946
        	};

        	static const float cam_nex_foward1[] = {
                  		// R 	G     	B
                  		0.6328, 0.0469, 0.2813, 0.1641, 0.7578, 0.0781, -0.0469, -0.6406, 1.5078
                  	};

                  	static const float cam_nex_foward2[] = {
                    	0.7578, 0.0859, 0.1172, 0.2734, 0.8281, -0.1016, 0.0156, -0.2813, 1.0859
                    	};
    TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  writer->fowardMatrix1);
    TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  writer->fowardMatrix2);



    LOGD("colormatrix2");
       	    //////////////////////////////IFD POINTERS///////////////////////////////////////
       	                                ///GPS//////////
       	   // TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
       	                               ///EXIF////////

}



float * calculateGpsPos(double base)
{
    int seconds = base * 3600;
    int degress = seconds / 3600;
    seconds = abs(seconds % 3600);
    int minutes = seconds / 60;
    seconds %=  60;
    LOGD("baseValue: %i Degress:%i Minutes:%i Seconds%i",base, degress, minutes,seconds);
    return new float[3]{degress, minutes, seconds};
}

void makeGPS_IFD(TIFF *tif, DngWriter *writer)
{
    LOGD("GPS IFD DATA");
    if (TIFFCreateGPSDirectory(tif) != 0)
    {
        LOGD("TIFFCreateGPSDirectory() failed" );
    }
    const char* longitudeRef = writer->Longitude  < 0 ? "W" : "E";
    if (!TIFFSetField( tif, GPSTAG_GPSLongitudeRef, longitudeRef))
    {
        LOGD("Can't write LongitudeRef" );
    }
    LOGD("LONG REF Written %c", longitudeRef);

    if (!TIFFSetField(tif, GPSTAG_GPSLongitude, writer->Longitude))
    {
        LOGD("Can't write Longitude" );
    }
    LOGD("Longitude Written");
    const char* latitudeRef = writer->Latitude < 0 ? "S" : "N";
    LOGD("PMETH Written");
    if (!TIFFSetField( tif, GPSTAG_GPSLatitudeRef, latitudeRef)) {
        LOGD("Can't write LAti REf" );
    }
    LOGD("LATI REF Written %c", latitudeRef);

    if (!TIFFSetField( tif, GPSTAG_GPSLatitude,writer->Latitude))
    {
        LOGD("Can't write Latitude" );
    }
    LOGD("Latitude Written");
    if (!TIFFSetField( tif, GPSTAG_GPSAltitude, writer->Altitude))
    {
        LOGD("Can't write Altitude" );
    }
    LOGD("Altitude Written");
    /*if (!TIFFSetField( tif, GPSTAG_GPSDatestamp, gpsTime)) {
        LOGD("Can't write gpsTime" );
    }
    LOGD("gpsTime Written");*/

    //Altitude Takes Type BYTE
    /*  if (!TIFFSetField( tif, GPSTAG_GPSAltitudeRef, alti)) {
        LOGD("Can't write AltitudeRef" );

    }*/
    /*if (!TIFFSetField( tif, GPSTAG_GPSImgDirection, 68)) {
        LOGD("Can't write IMG Directon" );
    }
    LOGD("I DIRECTION Written");
    /*if (!TIFFSetField( tif, GPSTAG_GPSLongitude, "11deg 39' 33.410"))
    {
        LOGD("Can't write LongitudeRef" );
    }*/
    /*if (!TIFFSetField( tif, GPSTAG_GPSProccesingMethod, writer->Provider)) {
        LOGD("Can't write Proc Method" );
    }*/

    /*if (!TIFFSetField( tif, GPSTAG_GPSImgDirectionRef, "M")) {
        LOGD("Can't write IMG DIREC REf" );
    }
    LOGD("I DREF Written");

    /* if (!TIFFSetField( tif, GPSTAG_GPSTimeStamp, 13/01/52)) {
        LOGD("Can't write Tstamp" );
    }
    LOGD("TSAMP Written");*/
    /*if (!TIFFSetField( tif, GPSTAG_GPSAltitudeRef, 1.)) {
        LOGD("Can't write ALTIREF" );
    }
    LOGD("ALT Written");*/

}

void writeExifIfd(TIFF *tif, DngWriter *writer)
{
    /////////////////////////////////// EXIF IFD //////////////////////////////
        LOGD("EXIF IFD DATA");
        if (TIFFCreateEXIFDirectory(tif) != 0) {
            LOGD("TIFFCreateEXIFDirectory() failed" );
        }
        short iso[] = {writer->_iso};
        LOGD("EXIF dir created");
        if (!TIFFSetField( tif, EXIFTAG_ISOSPEEDRATINGS,1, iso)) {
            LOGD("Can't write SPECTRALSENSITIVITY" );
        }
        LOGD("iso");
        if (!TIFFSetField( tif, EXIFTAG_FLASH, writer->_flash)) {
            LOGD("Can't write Flas" );
        }
        LOGD("flash");
        if (!TIFFSetField( tif, EXIFTAG_APERTUREVALUE, writer->_fnumber)) {
            LOGD("Can't write Aper" );
        }
        LOGD("aperture");

        if (!TIFFSetField( tif, EXIFTAG_EXPOSURETIME,writer->_exposure)) {
            LOGD("Can't write SPECTRALSENSITIVITY" );
        }
        LOGD("exposure");


        if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, writer->_focallength)) {
            LOGD("Can't write Focal" );
        }
        LOGD("focal");

        if (!TIFFSetField( tif, EXIFTAG_FNUMBER, writer->_fnumber)) {
            LOGD("Can't write FNum" );
        }
        LOGD("fnumber");


    //Check Point & Write are require checkpoint to update Current IFD Write Well to Write Close And Create IFD
}

void processTight(TIFF *tif,DngWriter *writer)
{
    LOGD("IN SXXXXl0");
    int i, j, row, col, b;
    unsigned char *buffer, *dp;
    unsigned char split; // single byte with 4 pairs of low-order bits
    unsigned short pixel[writer->rawwidht]; // array holds 16 bits per pixel

    LOGD("buffer set");
    j=0;
    if(writer->rowSize == 0)
        writer->rowSize =  -(-5 * writer->rawwidht >> 5) << 3;
    buffer =(unsigned char *)malloc(writer->rowSize);
    memset( buffer, 0, writer->rowSize);
    if (buffer == NULL)
    {
        LOGD("allocating buffer failed try again");
        buffer =(unsigned char *)malloc(writer->rowSize);
    }
    LOGD("rowsize:%i", writer->rowSize);
	for (row=0; row < writer->rawheight; row ++)
	{
		i = 0;
		for(b = row * writer->rowSize; b < row * writer->rowSize + writer->rowSize; b++)
			buffer[i++] = writer->bayerBytes[b];
		j = 0;

		for (dp=buffer, col = 0; col < writer->rawwidht; dp+=5, col+= 4)
		{
			for(int i = 0; i< 4; i++)
			{
			    pixel[col+i] = (dp[i] <<2) | (dp[4] >> (i << 1) & 3);
			}
		}
		if (TIFFWriteScanline(tif, pixel, row, 0) != 1) {
		LOGD("Error writing TIFF scanline.");
		}
	}
	LOGD("Write done");
	//TIFFCheckpointDirectory(tif);
    LOGD("write checkpoint");
    TIFFWriteDirectory(tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);
    LOGD("Free Memory");

    if(buffer != NULL)
    {
        LOGD("Free Buffer");
        free(buffer);
        buffer = NULL;
        LOGD("Freed Buffer");
    }



	//free(pixel);
	LOGD("Mem Released");
}

void processLoose(TIFF *tif,DngWriter *writer)
{
    unsigned short a;
    int i, j, row, col, b;
    unsigned char *buffer, *dp;
    unsigned char split; // single byte with 4 pairs of low-order bits
    unsigned short pixel[writer->rawwidht]; // array holds 16 bits per pixel

    uint64 colorchannel;

    j=0;

    writer->rowSize= (writer->rawwidht+5)/6 << 3;
    buffer =(unsigned char *)malloc(writer->rowSize);
    memset( buffer, 0, writer->rowSize);
    if (buffer == NULL)
    {
        LOGD("allocating buffer failed try again");
        buffer =(unsigned char *)malloc(writer->rowSize);
    }


	for (row=0; row < writer->rawheight; row ++)
	{

		//LOGD("read row: %d", row);
		i = 0;
		for(b = row * writer->rowSize; b < (row * writer->rowSize) + writer->rowSize; b++)
			buffer[i++] = writer->bayerBytes[b];

		// offset into buffer
		j = 0;
		/*
		 * get 5 bytes from buffer and move first 4bytes to 16bit
		 * split the 5th byte and add the value to the first 4 bytes
		 * */
		for (dp=buffer, col = 0; col < writer->rawwidht; dp+=8, col+= 6)
		{ // iterate over pixel columns

		    for(int i =0; i< 8; i++)
		    {
                colorchannel = (colorchannel << 8) | dp[i^7];
		    }

		    for(int i =0; i< 6; i++)
            {
                pixel[col+i] = (colorchannel >> i*10) & 0x3ff;
            }

		}
		if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
		LOGD("Error writing TIFF scanline.");
		}
	}
    //TIFFCheckpointDirectory(tif);
    TIFFWriteDirectory (tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);
    LOGD("Free Memory");
    if(buffer != NULL)
    {
        LOGD("Free Buffer");
        free(buffer);
        buffer = NULL;
        LOGD("Freed Buffer");
    }

    LOGD("Mem Released");
}

void processSXXX16(TIFF *tif,DngWriter *writer)
{
    unsigned short a;
    int i, j, row, col, b;
    unsigned char *buffer;
    unsigned char split; // single byte with 4 pairs of low-order bits
    unsigned short * pixel=(unsigned short *)malloc(writer->rawwidht *(sizeof(unsigned short)));
    buffer =(unsigned char *)malloc(writer->rowSize * (sizeof(unsigned char)));
    j=0;
	for (row=0; row < writer->rawheight; row ++)
	{
		i = 0;
		for(b = row * writer->rowSize; b < (row * writer->rowSize) + writer->rowSize; b++)
			buffer[i++] = writer->bayerBytes[b];
		// offset into buffer
		j = 0;
		for (col = 0; col < writer->rawwidht; col+= 4)
		{ // iterate over pixel columns
            a = buffer[j++];
            unsigned short b = buffer[j++];
			pixel[col+0] = b << 8 | a ;

			unsigned short c = buffer[j++];
            unsigned short d = buffer[j++];
			pixel[col+1] = d << 8 | c ;

			unsigned short EvenHI = buffer[j++];
            unsigned short OddLow = buffer[j++];
            pixel[col+2] = OddLow << 8 | EvenHI ;

			unsigned short g = buffer[j++];
            unsigned short h = buffer[j++];
			pixel[col+3] = h << 8 | g ;
		}
		if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
		LOGD("Error writing TIFF scanline.");
		}
	}
    TIFFWriteDirectory (tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);
    LOGD("Free Memory");
}

void writeRawStuff(TIFF *tif, DngWriter *writer)
{
    if(0 == strcmp(writer->bayerformat,"bggr"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
    if(0 == strcmp(writer->bayerformat , "grbg"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
    if(0 == strcmp(writer->bayerformat , "rggb"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
    if(0 == strcmp(writer->bayerformat , "gbrg"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
    long white=0x3ff;
    TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &white);

    short CFARepeatPatternDim[] = { 2,2 };
    TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);

    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, writer->blacklevel);
    LOGD("wrote blacklevel");
    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);

    if(writer->tightRaw == true)
    {
        LOGD("Processing tight RAW data...");
        processTight(tif, writer);
        LOGD("Done tight RAW data...");
    }
    else
    {
        LOGD("Processing loose RAW data...");
        processLoose(tif, writer);
        LOGD("Done loose RAW data...");
    }
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler)
{
    uint64 dir_offset = 0, dir_offset2 = 0, gpsIFD_offset = 0;
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    TIFF *tif = openfTIFF(writer->fileSavePath);

    writeIfd0(tif,writer);
    TIFFSetField (tif, TIFFTAG_EXIFIFD, dir_offset);
    LOGD("set exif");
    //CheckPOINT to KEEP EXIF IFD in MEMory
    //Try FiX DIR
    TIFFCheckpointDirectory(tif);
    TIFFWriteDirectory(tif);
    TIFFSetDirectory(tif, 0);

    if(writer->gps == true)
    {
        makeGPS_IFD(tif, writer);
        TIFFCheckpointDirectory(tif);
        TIFFWriteCustomDirectory(tif, &gpsIFD_offset);
        TIFFSetDirectory(tif, 0);
    }


    writeExifIfd(tif,writer);
    //Check Point & Write are require checkpoint to update Current IFD Write Well to Write Close And Create IFD
    TIFFCheckpointDirectory(tif); //This Was missing it without it EXIF IFD was not being updated after adding SUB IFD
    TIFFWriteCustomDirectory(tif, &dir_offset);
    ///////////////////// GO Back TO IFD 0
    TIFFSetDirectory(tif, 0);
    if(writer->gps)
        TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
             ///////////////////////////// WRITE THE SUB IFD's SUB IFD + EXIF IFD AGain GPS IFD would also go here as well as other cust IFD
    TIFFSetField(tif, TIFFTAG_EXIFIFD, dir_offset);

    writeRawStuff(tif,writer);

    if (writer->bayerBytes == NULL)
        return;
    delete[] writer->bayerBytes;
    writer->bayerBytes = NULL;

}
