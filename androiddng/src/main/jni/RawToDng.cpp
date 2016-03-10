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
#include <omp.h>

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
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_Write10bitDNG(JNIEnv *env, jobject thiz, jobject handler);


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
    	jint tight,
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
    int rawType;
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
	jint tight,
	jint width,
	jint height)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);

    writer->blacklevel = new float[4] {blacklevel, blacklevel, blacklevel,blacklevel};
    writer->rawType = tight;
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
    if(writer->rawType > 0)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
    else
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 10) != 0);
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
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, writer->colorMatrix2);
    LOGD("colormatrix1");
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, writer->neutralColorMatrix);
    LOGD("neutralMatrix");
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 21);

    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 17);

    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, writer->colorMatrix1);

    TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  writer->fowardMatrix2);
    TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  writer->fowardMatrix1);

    TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  writer->noiseMatrix);



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
   //#pragma omp parallel for
	for (row=0; row < writer->rawheight; row ++)
	{
		i = 0;
		//#pragma omp parallel for
		for(b = row * writer->rowSize; b < row * writer->rowSize + writer->rowSize; b++)
			buffer[i++] = writer->bayerBytes[b];
		j = 0;


		for (dp=buffer, col = 0; col < writer->rawwidht; dp+=5, col+= 4)
		{
		   // #pragma omp parallel for
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

void process10tight(TIFF *tif,DngWriter *writer)
{
    unsigned char* ar = writer->bayerBytes;
    unsigned char* tmp = new unsigned char[5];
    int bytesToSkip = 0;
    int realrowsize = writer->rawSize/writer->rawheight;
    int shouldberowsize = writer->rawwidht*10/8;
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    if (realrowsize != shouldberowsize)
        bytesToSkip = realrowsize - shouldberowsize;
    LOGD("bytesToSkip: %i", bytesToSkip);
    int row = shouldberowsize;
    unsigned char* out = new unsigned char[shouldberowsize*writer->rawheight];
    int m = 0;
    for(int i =0; i< writer->rawSize; i+=5)
    {
        if(i == row)
        {
            row += shouldberowsize +bytesToSkip;
            i+=bytesToSkip;
            //LOGD("new row: %i", row/shouldberowsize);
        }
        out[m++] = (ar[i]); // 00110001
        out[m++] =  (ar[i+4] & 0b00000011 ) <<6 | (ar[i+1] & 0b11111100)>>2; // 01 001100
        out[m++] = (ar[i+1]& 0b00000011 )<< 6 | (ar[i+4] & 0b00001100 ) <<2 | (ar[i +2] & 0b11110000 )>> 4;// 10 01 0011
        out[m++] = (ar[i+2] & 0b00001111 ) << 4 | (ar[i+4] & 0b00110000 )>> 2| (ar[i+3]& 0b11000000)>>6; // 0011 11 00
        out[m++] = (ar[i+3]& 0b00111111)<<2 | (ar[i+4]& 0b11000000)>>6;//110100 00
    }
    TIFFWriteRawStrip(tif, 0, out, writer->rawheight*shouldberowsize);

    TIFFRewriteDirectory (tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);

    delete[] out;
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
    TIFFRewriteDirectory (tif);
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
    int i, j, row, col, b, nextlog;
    unsigned char split; // single byte with 4 pairs of low-order bits
    unsigned short pixel[writer->rawwidht];
    j=0;
	for (row=0; row < writer->rawheight; row ++)
	{
        nextlog = 0;
		for (col = 0; col < writer->rawwidht; col+=4)
		{ // iterate over pixel columns
            for (int k = 0; k < 4; ++k)
            {
                unsigned short low = writer->bayerBytes[j++];
                unsigned short high =   writer->bayerBytes[j++];
                pixel[col+k] =  high << 8 |low;
                if(col < 4 && row < 4)
                    LOGD("Pixel : %i, high: %i low: %i ", pixel[col+k], high, low);
            }
		}
		if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
		LOGD("Error writing TIFF scanline.");
		}
	}
    TIFFRewriteDirectory (tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);
    LOGD("Free Memory");
}

unsigned char* BufferedRaw(const char* in)
{
    FILE * pFile;
    long lSize;
    char * buffer;
    size_t result;

    pFile = fopen ( "myfile.bin" , "rb" );
    if (pFile==NULL) {
        LOGD("File Read Error.");
        return NULL;
    }

    fseek (pFile , 0 , SEEK_END);
    lSize = ftell (pFile);
    rewind (pFile);

    buffer = (char*) malloc (sizeof(char)*lSize);
    if (buffer == NULL) {
        LOGD("Memory Error.");
        return NULL;
    }

    result = fread (buffer,1,lSize,pFile);
    if (result != lSize) {
        LOGD("Read into buffer Error.");
        return NULL;
    }

    fclose (pFile);

    return (unsigned char*)buffer;
    //free(buffer;)




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
    //**********************************************************************************

    LOGD("Read Op from File");
    FILE * file = fopen("/sdcard/DCIM/FreeDcam/opc2.bin", "r+");
    if (file != NULL)
    {
        fseek(file, 0, SEEK_END);
        long int sizeD = ftell(file);
        fseek(file, 0, 0);
        LOGD("Op read from File");
// Reading data to array of unsigned chars
        LOGD("OpCode Read Size", sizeD);
        unsigned char *opcode_list = (unsigned char *) malloc(sizeD);
        int bytes_read = fread(opcode_list, sizeof(unsigned char), sizeD, file);
        LOGD("bytes_read Read Size", bytes_read);
        fclose(file);
        
        LOGD("OpCode Entry");
        TIFFSetField(tif, TIFFTAG_OPC2, sizeD, opcode_list);
        LOGD("OpCode Exit");
    }
//*****************************************************************************************
    if(writer->rawType == 0)
    {
        LOGD("Processing tight RAW data...");
        process10tight(tif, writer);
        LOGD("Done tight RAW data...");
    }
    else if (writer->rawType == 1)
    {
        LOGD("Processing loose RAW data...");
        processLoose(tif, writer);
        LOGD("Done loose RAW data...");
    }
    else if (writer->rawType == 2)
        processSXXX16(tif,writer);
    else if (writer->rawType == 3)
        processTight(tif, writer);
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



JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_Write10bitDNG(JNIEnv *env, jobject thiz, jobject handler)
{
    uint64 dir_offset = 0, dir_offset2 = 0, gpsIFD_offset = 0;
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    TIFF *tif = openfTIFF(writer->fileSavePath);

    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    LOGD("subfiletype 10BIT DNG");
    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, writer->rawwidht) != 0);
    LOGD("width");
    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, writer->rawheight) != 0);
    LOGD("height");
    assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 10) != 0);
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
	TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreeDcam by Troop");
	LOGD("software");
	TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
	TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
	LOGD("dngversion");
	TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
	LOGD("CameraModel");
	TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, writer->_imagedescription);
	LOGD("imagedescription");
	TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, writer->colorMatrix2);
	LOGD("colormatrix1");
	TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, writer->neutralColorMatrix);
	LOGD("neutralMatrix");
	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 21);
	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 17);
	TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, writer->colorMatrix1);
	TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  writer->fowardMatrix2);
	TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  writer->fowardMatrix1);
	//TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  writer->noiseMatrix);
	LOGD("colormatrix2");

	//TIFFSetField(tif, TIFFTAG_ROWSPERSTRIP, writer->rawheight);
	//TIFFSetField(tif, TIFFTAG_STRIPOFFSETS, writer->rawwidht*10/8);
	//TIFFSetField(tif, TIFFTAG_STRIPBYTECOUNTS, (writer->rawSize/writer->rawheight)/10*8);
	//////////////////////////////IFD POINTERS///////////////////////////////////////
	///GPS//////////
	// TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
	///EXIF////////

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

	unsigned char* ar = writer->bayerBytes;
	unsigned char* tmp = new unsigned char[5];
    int bytesToSkip = 0;
    int realrowsize = writer->rawSize/writer->rawheight;
    int shouldberowsize = writer->rawwidht*10/8;
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    if (realrowsize != shouldberowsize)
        bytesToSkip = realrowsize - shouldberowsize;
    int row = shouldberowsize;
    unsigned char* out = new unsigned char[shouldberowsize*writer->rawheight];
    int m = 0;
	for(int i =0; i< writer->rawSize; i+=5)
	{
        if(i == row)
        {
            row += shouldberowsize +bytesToSkip;
            i+=bytesToSkip;
            //LOGD("new row: %i", row/shouldberowsize);
        }
		out[m++] = (ar[i]); // 00110001
        out[m++] =  (ar[i+4] & 0b00000011 ) <<6 | (ar[i+1] & 0b11111100)>>2; // 01 001100
        out[m++] = (ar[i+1]& 0b00000011 )<< 6 | (ar[i+4] & 0b00001100 ) <<2 | (ar[i +2] & 0b11110000 )>> 4;// 10 01 0011
        out[m++] = (ar[i+2] & 0b00001111 ) << 4 | (ar[i+4] & 0b00110000 )>> 2| (ar[i+3]& 0b11000000)>>6; // 0011 11 00
        out[m++] = (ar[i+3]& 0b00111111)<<2 | (ar[i+4]& 0b11000000)>>6;//110100 00
	}
	TIFFWriteRawStrip(tif, 0, out, writer->rawheight*shouldberowsize);

	TIFFWriteDirectory (tif);
	LOGD("Finalizng DNG");
	TIFFClose(tif);

	if (writer->bayerBytes == NULL)
	return;
	delete[] writer->bayerBytes;
    delete[] out;
	writer->bayerBytes = NULL;

}


/*
jstring Java_com_troop_androiddng_RawToDng_getFilePath( JNIEnv* env, jobject obj){

    //env->GetFloatArrayElements(Latitude,NULL);
    jstring jstr = env->NewStringUTF(env, "This comes from jni.");
    jclass clazz = env->FindClass(env, "com/troop/freedcam/camera/modules/image_saver");
    jmethodID messageMe = env->GetMethodID(env, clazz, "FeeDJNI", "(Ljava/lang/String;)Ljava/lang/String;");
    jobject result = env->CallObjectMethod(env, obj, messageMe, jstr);

    const char* str = env->GetStringUTFChars(env,(jstring) result, NULL); // should be released but what a heck, it's a tutorial :)
    LOGD("get filepath",str);
    //BufferedRaw(str);

    return env->NewStringUTF(env, str);
}*/
