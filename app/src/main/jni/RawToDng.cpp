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

typedef unsigned long long UINT64;
typedef unsigned short UINT16;
typedef unsigned char uint8;

extern "C"
{
    JNIEXPORT jobject JNICALL Java_com_troop_androiddng_RawToDng_CreateAndSetExifData(JNIEnv *env, jobject thiz,
    jint iso,
    jdouble expo,
    jstring make,
    jstring model,
    jint flash,
    jfloat fNum,
    jfloat focalL,
    jstring imagedescription,
    jstring orientation);

    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jobject handler, jdouble Altitude,jdouble Latitude,jdouble Longitude, jstring Provider, jlong gpsTime);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, jint widht, jint height);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT jint JNICALL Java_com_troop_androiddng_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_Release(JNIEnv *env, jobject thiz, jobject handler);
    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jobject handler,jbyteArray fileBytes, jstring fileout, jint width,jint height);


	JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
    	jfloatArray colorMatrix1,
    	jfloatArray colorMatrix2,
    	jfloatArray neutralColor,
    	jint blacklevel,
    	jstring bayerformat,
    	jint rowSize,
    	jstring devicename,
    	jboolean tight);
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

    double Altitude;
    double Latitude;
    double Longitude;
    char* Provider;
    long gpsTime;

    float *blacklevel;
    char *fileSavePath;
    long fileLength;
    unsigned char* bayerBytes;
    int rawwidht, rawheight, rowSize;
    float *colorMatrix1;
    float *colorMatrix2;
    float *neutralColorMatrix;
    char* bayerformat;
    bool tightRaw;


    int thumbheight, thumwidth;
    unsigned char* _thumbData;

    DngWriter(JNIEnv *env, jint iso, jdouble expo, jstring make, jstring model, jint flash,  jfloat fnumber, jfloat focallength, jstring imagedescription, jstring orientation)
    {
        _iso = iso;
        _exposure = expo;
        _make = (char*) env->GetStringUTFChars(make,NULL);
        _model = (char*) env->GetStringUTFChars(model,NULL);
        _flash = flash;
        _imagedescription = (char*) env->GetStringUTFChars(imagedescription,NULL);
        _orientation = (char*) env->GetStringUTFChars(orientation,NULL);
        _fnumber = fnumber;
        _focallength = focallength;
    }
};

JNIEXPORT jint JNICALL Java_com_troop_androiddng_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    return sizeof(writer->bayerBytes);
}

JNIEXPORT jobject JNICALL Java_com_troop_androiddng_RawToDng_CreateAndSetExifData(JNIEnv *env, jobject thiz,
    jint iso,
    jdouble expo,
    jstring make,
    jstring model,
    jint flash,
    jfloat fNum,
    jfloat focalL,
    jstring imagedescription,
    jbyteArray mThumb,
    jstring orientation)
{
    DngWriter *writer = new DngWriter(env,iso, expo,make, model, flash, fNum, focalL, imagedescription,orientation);
    return env->NewDirectByteBuffer(writer, 0);
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetGPSData(JNIEnv *env, jobject thiz,jobject handler, jdouble Altitude,jdouble Latitude,jdouble Longitude, jstring Provider, jlong gpsTime)
{
      DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
      writer->Altitude = (double)Altitude;
      writer->Latitude = (double)Latitude;
      writer->Longitude = (double)Longitude;
      writer->Provider = (char*) env->GetStringUTFChars(Provider,NULL);
      writer->gpsTime = (long)(gpsTime);
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
        free(writer->bayerBytes);
    if(writer->_thumbData != NULL)
        free(writer->_thumbData);
    free(writer);
    writer = NULL;
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fileBytes, jstring fileout,jint width,jint height)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->bayerBytes = (unsigned char*) env->GetByteArrayElements(fileBytes,NULL);
    writer->fileSavePath = (char*)  env->GetStringUTFChars(fileout,NULL);
    writer->rawheight = height;
    writer->rawwidht = width;
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
	jfloatArray colorMatrix1,
	jfloatArray colorMatrix2,
	jfloatArray neutralColor,
	jint blacklevel,
	jstring bayerformat,
	jint rowSize,
	jstring devicename,
	jboolean tight)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);

    writer->blacklevel = new float[4] {blacklevel, blacklevel, blacklevel,blacklevel};
    writer->tightRaw = tight;
    writer->rowSize =rowSize;
    writer->colorMatrix1 = (float*)colorMatrix1;
    writer->colorMatrix1 =(float*)colorMatrix2;
    writer->neutralColorMatrix = (float*)neutralColor;
    writer->bayerformat = (char*)  env->GetStringUTFChars(bayerformat,NULL);
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

void writeIfd0(TIFF *tif, DngWriter *writer, UINT64 dir_offset)
{
    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, writer->rawwidht) != 0);
    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, writer->rawheight) != 0);
    assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
    assert(TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA) != 0);
            //assert(TIFFSetField(tif, TIFFTAG_ROWSPERSTRIP, 480/2) != 0);
    assert(TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE) != 0);
    TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
    TIFFSetField(tif, TIFFTAG_MAKE, writer->_make);
    TIFFSetField(tif, TIFFTAG_MODEL, writer->_model);
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
        }
    catch(...)
    {
        LOGD("Caught NULL NOT SET Orientation");
    }
    assert(TIFFSetField(tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG) != 0);
        //assert(TIFFSetField(tif, TIFFTAG_SAMPLESPERPIXEL, 3) != 0);
    TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreedCam by Troop");
    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
    TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, writer->_imagedescription);
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, writer->colorMatrix1);
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, writer->neutralColorMatrix);
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);
    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, writer->colorMatrix2);
       	    //////////////////////////////IFD POINTERS///////////////////////////////////////
       	                                ///GPS//////////
       	   // TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
       	                               ///EXIF////////
    	TIFFSetField (tif, TIFFTAG_EXIFIFD, dir_offset);
    	//CheckPOINT to KEEP EXIF IFD in MEMory
    	//Try FiX DIR

    	TIFFCheckpointDirectory(tif);
    	TIFFWriteDirectory(tif);
    	TIFFSetDirectory(tif, 0);
}



float * calculateGpsPos(int base)
{
    int seconds = abs(round(base * 3600));
    int degress = seconds / 3600;
    seconds = abs(seconds % 3600);
    int minutes = seconds / 60;
    seconds = seconds % 60;
    return new float[3]{degress, minutes, seconds};
}

void makeGPS_IFD(TIFF *tif, DngWriter *writer)
{
    LOGD("GPS IFD DATA");
    if (TIFFCreateGPSDirectory(tif) != 0)
    {
        LOGD("TIFFCreateGPSDirectory() failed" );
    }
    const char* longitudeRef = writer->Longitude  < 0 ? "E" : "W";
    if (!TIFFSetField( tif, GPSTAG_GPSLongitudeRef, longitudeRef))
    {
        LOGD("Can't write LongitudeRef" );
    }
    LOGD("LONG REF Written %c", longitudeRef);
    delete longitudeRef;
    if (!TIFFSetField(tif, GPSTAG_GPSLongitude, calculateGpsPos(writer->Longitude)))
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
    delete latitudeRef;
    if (!TIFFSetField( tif, GPSTAG_GPSLatitude,calculateGpsPos(writer->Longitude)))
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
    if (!TIFFSetField( tif, GPSTAG_GPSImgDirection, 68)) {
        LOGD("Can't write IMG Directon" );
    }
    LOGD("I DIRECTION Written");
    /*if (!TIFFSetField( tif, GPSTAG_GPSLongitude, "11deg 39' 33.410"))
    {
        LOGD("Can't write LongitudeRef" );
    }*/
    if (!TIFFSetField( tif, GPSTAG_GPSProccesingMethod, writer->Provider)) {
        LOGD("Can't write Proc Method" );
    }

    if (!TIFFSetField( tif, GPSTAG_GPSImgDirectionRef, "M")) {
        LOGD("Can't write IMG DIREC REf" );
    }
    LOGD("I DREF Written");

    /* if (!TIFFSetField( tif, GPSTAG_GPSTimeStamp, 13/01/52)) {
        LOGD("Can't write Tstamp" );
    }
    LOGD("TSAMP Written");*/
    if (!TIFFSetField( tif, GPSTAG_GPSAltitudeRef, 1.)) {
        LOGD("Can't write ALTIREF" );
    }
    LOGD("ALT Written");

}

void writeExifIfd(TIFF *tif, DngWriter *writer, UINT64 dir_offset)
{
    /////////////////////////////////// EXIF IFD //////////////////////////////
        LOGD("EXIF IFD DATA");
        if (TIFFCreateEXIFDirectory(tif) != 0) {
            LOGD("TIFFCreateEXIFDirectory() failed" );
        }
        if (!TIFFSetField( tif, EXIFTAG_ISOSPEEDRATINGS, 1, writer->_iso)) {
            LOGD("Can't write SPECTRALSENSITIVITY" );
        }
        if (!TIFFSetField( tif, EXIFTAG_EXPOSURETIME, writer->_exposure)) {
            LOGD("Can't write SPECTRALSENSITIVITY" );
        }

        if (!TIFFSetField( tif, EXIFTAG_APERTUREVALUE, writer->_fnumber)) {
            LOGD("Can't write Aper" );
        }
        if (!TIFFSetField( tif, EXIFTAG_FLASH, writer->_flash)) {
            LOGD("Can't write Flas" );
        }

        if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, writer->_focallength)) {
            LOGD("Can't write Focal" );
        }
        if (!TIFFSetField( tif, EXIFTAG_FNUMBER, writer->_fnumber)) {
            LOGD("Can't write FNum" );
        }


    //Check Point & Write are require checkpoint to update Current IFD Write Well to Write Close And Create IFD
        TIFFCheckpointDirectory(tif); //This Was missing it without it EXIF IFD was not being updated after adding SUB IFD
        TIFFWriteCustomDirectory(tif, &dir_offset);
    ///////////////////// GO Back TO IFD 0
        TIFFSetDirectory(tif, 0);
}

void writeGPSIfd(TIFF *tif, DngWriter *writer, UINT64 gpsIFD_offset)
{
    ///////////////////////////////////GPS IFD////////////////
    	if(writer->Longitude > 0)
    	{
    	    makeGPS_IFD(tif, writer);

            TIFFCheckpointDirectory(tif);
            TIFFWriteCustomDirectory(tif, &gpsIFD_offset);
        }
        //////////////////////////////////// GPS END//////////////////////////////////////////
        TIFFSetDirectory(tif, 0);
}

void processSXXX10packed(TIFF *tif,DngWriter *writer)
{
    LOGD("IN SXXXXl0");
    int i, j, row, col, b;
    unsigned char *buffer;
    unsigned char split; // single byte with 4 pairs of low-order bits
    unsigned short pixel[writer->rawwidht]; // array holds 16 bits per pixel
    buffer =(unsigned char *)malloc(writer->rowSize);
    j=0;
	for (row=0; row < writer->rawheight; row ++)
	{

		//LOGD("read row: %d", row);
		i = 0;
		for(b = row * writer->rowSize; b < row * writer->rowSize + writer->rowSize; b++)
			buffer[i++] = writer->bayerBytes[b];

		// offset into buffer
		j = 0;
		/*
		 * get 5 bytes from buffer and move first 4bytes to 16bit
		 * split the 5th byte and add the value to the first 4 bytes
		 * */
		for (col = 0; col < writer->rawwidht; col+= 4) { // iterate over pixel columns
			pixel[col+0] = buffer[j++] << 2;
			pixel[col+1] = buffer[j++] << 2;
			pixel[col+2] = buffer[j++] << 2;
			pixel[col+3] = buffer[j++] << 2;
			//LOGD("Unpacked 4bytes");
			split = buffer[j++]; // low-order packed bits from previous 4 pixels
			//LOGD("Unpack 5th byte and move to last 4bytes pixel %d", col + 5);
			pixel[col+0] += (split & 0b00000011); // unpack them bits, add to 16-bit values, left-justified

			pixel[col+1] += (split & 0b00001100)>>2;

			pixel[col+2] += (split & 0b00110000)>>4;

			pixel[col+3] += (split & 0b11000000)>>6;

		}
		if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
		LOGD("Error writing TIFF scanline.");
		}
	}
	delete[] buffer;
	delete[] pixel;
}

void processSXXX16(TIFF *tif,DngWriter *writer)
{
unsigned short a;
    int i, j, row, col, b;
     unsigned char *buffer;
     unsigned char split; // single byte with 4 pairs of low-order bits
     unsigned short pixel[writer->rawwidht]; // array holds 16 bits per pixel
     buffer =(unsigned char *)malloc(writer->rowSize);

    j=0;

	for (row=0; row < writer->rawheight; row ++)
	{

		//LOGD("read row: %d", row);
		i = 0;
		for(b = row * writer->rowSize; b < row * writer->rowSize + writer->rowSize; b++)
			buffer[i++] = writer->fileSavePath[b];

		// offset into buffer
		j = 0;
		/*
		 * get 5 bytes from buffer and move first 4bytes to 16bit
		 * split the 5th byte and add the value to the first 4 bytes
		 * */
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
	delete[] buffer;
    delete[] pixel;

}

void writeRawStuff(TIFF *tif, DngWriter *writer)
{
    if(0 == strcmp(writer->bayerformat,"BGGR"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
    if(0 == strcmp(writer->bayerformat , "GRGB"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
    if(0 == strcmp(writer->bayerformat , "RGGB"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
    if(0 == strcmp(writer->bayerformat , "GBRG"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
    long white=0x3ff;
    TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &white);

    short CFARepeatPatternDim[] = { 2,2 };
    TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);
    if(writer->blacklevel != 0)
    {
            TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, writer->blacklevel);
            LOGD("wrote blacklevel");
            TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
    }
    if(writer->tightRaw == true)
    {
        LOGD("Processing tight RAW data...");
        processSXXX10packed(tif, writer);
        LOGD("Done tight RAW data...");
    }
    else
    {
        LOGD("Processing loose RAW data...");
        processSXXX16(tif, writer);
        LOGD("Done loose RAW data...");
    }
}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler)
{
    UINT64 dir_offset = 0, dir_offset2 = 0, gpsIFD_offset = 0;
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    TIFF *tif = openfTIFF(writer->fileSavePath);
    writeIfd0(tif,writer, dir_offset);
    writeGPSIfd(tif,writer, gpsIFD_offset);
    writeExifIfd(tif,writer, dir_offset);

///////////////////////////// WRITE THE SUB IFD's SUB IFD + EXIF IFD AGain GPS IFD would also go here as well as other cust IFD
    TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
    TIFFSetField(tif, TIFFTAG_EXIFIFD, dir_offset);

    writeRawStuff(tif,writer);

    LOGD("Finalizng DNG");
    TIFFCheckpointDirectory(tif);
    TIFFWriteDirectory (tif);
    TIFFClose (tif);
    LOGD("Free Memory");

}
