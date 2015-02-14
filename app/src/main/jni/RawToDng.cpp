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
	JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_convertRawBytesToDng(JNIEnv *env, jobject thiz,
			jbyteArray filein,
			jstring fileout,
			jint width,
			jint height,
			jfloatArray colorMatrix1,
			jfloatArray colorMatrix2,
			jfloatArray neutralColor,
			jint blacklevel,
			jstring bayerformat,
			jint rowSize,
			jstring devicename,
			jboolean tight,
			jint iso,
			jdouble expo,
			jstring make,
			jstring model,
			jint flash,
			jfloat fNum,
			jfloat focalL,
			jstring iDesc,
			jbyteArray mThumb,
			jstring orientation,
			jdouble Altitude,
            jdouble Latitude,
            jdouble Longitude,
            jstring Provider,
            jlong gpsTime);

			JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_convertRawBytesToDngFast(JNIEnv *env, jobject thiz,
            			jbyteArray filein,
            			jstring fileout,
            			jint width,
            			jint height,
            			jfloatArray colorMatrix1,
            			jfloatArray colorMatrix2,
            			jfloatArray neutralColor,
            			jint blacklevel,
            			jstring bayerformat,
            			jint rowSize,
            			jboolean tight,
            			jstring Make,
            			jstring Model);

            JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_convertRawBytesToDngFastHDR2(JNIEnv *env, jobject thiz,
                        			jbyteArray filein,
                        			jstring fileout,
                        			jint width,
                        			jint height,
                        			jfloatArray colorMatrix1,
                        			jfloatArray colorMatrix2,
                        			jfloatArray neutralColor,
                        			jint blacklevel,
                        			jstring bayerformat,
                        			jint rowSize,
                        			jboolean tight,
                        			jstring Make,
                        			jstring Model);


            JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_convertRawBytesToDngFastHDR3(JNIEnv *env, jobject thiz,
                        			jbyteArray filein,
                        			jstring fileout,
                        			jint width,
                        			jint height,
                        			jfloatArray colorMatrix1,
                        			jfloatArray colorMatrix2,
                        			jfloatArray neutralColor,
                        			jint blacklevel,
                        			jstring bayerformat,
                        			jint rowSize,
                        			jboolean tight,
                        			jstring Make,
                        			jstring Model);

}



static void write_image(TIFF *tif, int nx, int ny, int blue) {
   uint8 *buf = (uint8 *)_TIFFmalloc(nx*3);

   for (int y=0; y<ny; y++) {
     uint8 *p = buf;
     for (int x=0; x<nx; x++) {
       *p++ = x*255/(nx-1);  // r
       *p++ = y*255/(ny-1);  // g
       *p++ = blue;          // b
     }
     assert(TIFFWriteScanline(tif, buf, y, 0) != -1);
   }
   _TIFFfree(buf);
}


void processSXXX10packed(TIFF *tif,unsigned short *pixel,unsigned char *buffer, unsigned char *strfile, int rowSize, int width, int height)
{
    LOGD("IN SXXXXl0");
    int i, j, row, col, b;
    unsigned char split;

    j=0;
	for (row=0; row < height; row ++)
	{

		//LOGD("read row: %d", row);
		i = 0;
		for(b = row * rowSize; b < row * rowSize + rowSize; b++)
			buffer[i++] = strfile[b];

		// offset into buffer
		j = 0;
		/*
		 * get 5 bytes from buffer and move first 4bytes to 16bit
		 * split the 5th byte and add the value to the first 4 bytes
		 * */
		for (col = 0; col < width; col+= 4) { // iterate over pixel columns
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

}

void processSXXX16(TIFF *tif,unsigned short *pixel,unsigned char *buffer, unsigned char *strfile, int rowSize, int width, int height)
{
unsigned short a;
    int i, j, row, col, b;
    unsigned char split;

    j=0;

	for (row=0; row < height; row ++)
	{

		//LOGD("read row: %d", row);
		i = 0;
		for(b = row * rowSize; b < row * rowSize + rowSize; b++)
			buffer[i++] = strfile[b];

		// offset into buffer
		j = 0;
		/*
		 * get 5 bytes from buffer and move first 4bytes to 16bit
		 * split the 5th byte and add the value to the first 4 bytes
		 * */
		for (col = 0; col < width; col+= 4) { // iterate over pixel columns

		 /*   while (j %2  != 0)
                            j++;
			pixel[col+0] = buffer[j++];
			while (j %2  != 0)
                            j++;
			pixel[col+1] = buffer[j++];
			while (j %2  != 0)
                            j++;
			pixel[col+2] = buffer[j++] ;
			while (j %2  != 0)
                            j++;
			pixel[col+3] = buffer[j++];*/

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

			// unpack them bits, add to 16-bit values, left-justified
		  //pixel[col+0] += 0b11000000 >> 6;

           //pixel[col+1] += 0b00110000 >> 4;

           //pixel[col+2] += 0b00001100 >> 2;

           //pixel[col+3] += 0b00000011 ;





		}
		if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
		LOGD("Error writing TIFF scanline.");
		}

	}


}

void makeEXIF_IFD(TIFF *tif)
{

}

void makeGPS_IFD(TIFF *tif, jdouble Altitude,
                                    jdouble Latitude,
                                    jdouble Longitude,
                                    jstring Provider,
                                    jlong gpsTime)
{
    LOGD("GPS IFD DATA");
    if (TIFFCreateGPSDirectory(tif) != 0)
    {
        LOGD("TIFFCreateGPSDirectory() failed" );
    }


    double value = Longitude;
    LOGD("Longitude %i", value);
    int longitudeSeconds = abs(round(value * 3600));
    LOGD("longitudeSeconds %i", longitudeSeconds);
    int longitudeDegrees = value;
    LOGD("longitudeDegrees %i", longitudeDegrees);
    longitudeSeconds = abs(longitudeSeconds % 3600);
    LOGD("longitudeSeconds %i", longitudeSeconds);
    int longitudeMinutes = longitudeSeconds / 60;
    LOGD("longitudeMinutes %i", longitudeMinutes);
    longitudeSeconds = longitudeSeconds % 60;
    LOGD("longitudeSeconds %i", longitudeSeconds);
    const char* longitudeRef = longitudeDegrees  > 0 ? "E" : "W";


        float longitudees[] = {longitudeDegrees, longitudeMinutes, longitudeSeconds};

    if (!TIFFSetField( tif, GPSTAG_GPSLongitudeRef, longitudeRef))
    {
        LOGD("Can't write LongitudeRef" );
    }
    LOGD("LONG REF Written %c", longitudeRef);


    if (!TIFFSetField( tif, GPSTAG_GPSLongitude,3, longitudees))
    {
        LOGD("Can't write Longitude" );
    }
    LOGD("Longitude Written");


    const char* latitudeRef = Latitude < 0 ? "S" : "N";
    LOGD("PMETH Written");
    if (!TIFFSetField( tif, GPSTAG_GPSLatitudeRef, latitudeRef)) {
        LOGD("Can't write LAti REf" );
    }
    LOGD("LATI REF Written %c", latitudeRef);





    value = Latitude;
    longitudeSeconds = value * 3600;
    longitudeDegrees = longitudeSeconds / 3600;
    longitudeMinutes = longitudeSeconds / 60;
    longitudeSeconds %= 60;

    float latitudes[] = {longitudeDegrees, longitudeMinutes, longitudeSeconds};
    if (!TIFFSetField( tif, GPSTAG_GPSLatitude,3, latitudes))
    {
        LOGD("Can't write Latitude" );
    }
    LOGD("Latitude Written");


    if (!TIFFSetField( tif, GPSTAG_GPSAltitude, Altitude))
    {
        LOGD("Can't write Altitude" );
    }
    LOGD("Altitude Written");

    if (!TIFFSetField( tif, GPSTAG_GPSDatestamp, gpsTime)) {
        LOGD("Can't write gpsTime" );
    }
    LOGD("gpsTime Written");

                                	//Altitude Takes Type BYTE
              /*  if (!TIFFSetField( tif, GPSTAG_GPSAltitudeRef, alti)) {
                                		 LOGD("Can't write AltitudeRef" );

                                	}*/



                if (!TIFFSetField( tif, GPSTAG_GPSImgDirection, 68)) {
                                		 LOGD("Can't write IMG Directon" );

                                	}
              LOGD("I DIRECTION Written");
               // if (!TIFFSetField( tif, GPSTAG_GPSLongitude, "11deg 39' 33.410")) {
                // 		 LOGD("Can't write LongitudeRef" );

              //                                  	}
                if (!TIFFSetField( tif, GPSTAG_GPSProccesingMethod, Provider)) {
                         		 LOGD("Can't write Proc Method" );

                	}

                if (!TIFFSetField( tif, GPSTAG_GPSImgDirectionRef, "M")) {
                                   LOGD("Can't write IMG DIREC REf" );
                                }
                                LOGD("I DREF Written");

               /* if (!TIFFSetField( tif, GPSTAG_GPSTimeStamp, 13/01/52)) {
                                                   LOGD("Can't write Tstamp" );
                                                }*/
                LOGD("TSAMP Written");
                if (!TIFFSetField( tif, GPSTAG_GPSAltitudeRef, 1.)) {
                                                                   LOGD("Can't write ALTIREF" );
                                                                }
                                                            LOGD("ALT Written");

}


JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_convertRawBytesToDng(JNIEnv *env, jobject thiz,
		jbyteArray filein,
		jstring fileout,
		jint width,
		jint height,
		jfloatArray colorMatrix1,
		jfloatArray colorMatrix2,
		jfloatArray neutralColor,
		jint blacklevel,
		jstring bayerformat,
		jint rowSize,
		jstring devicename,
		jboolean tight,
        jint iso,
        jdouble expo,
        jstring make,
        jstring model,
        jint flash,
        jfloat fNum,
        jfloat focalL,
        jstring iDesc,
        jbyteArray mThumb,
        jstring orientation,
        jdouble Altitude,
        jdouble Latitude,
        jdouble Longitude,
        jstring Provider,
        jlong gpsTime)
{
	LOGD("Start Converting");
	//load the rawdata into chararray

	unsigned char *strfile= (unsigned char*) env->GetByteArrayElements(filein,NULL);
	const char *bayer = (char*) env->GetStringUTFChars(bayerformat, NULL);
	const char *mMake = (char*) env->GetStringUTFChars(make, NULL);
    const char *mModel = (char*) env->GetStringUTFChars(model, NULL);
    const char *ImageDescription = (char*) env->GetStringUTFChars(iDesc, NULL);
	LOGD("Data Loaded");
	const char *strfileout= env->GetStringUTFChars(fileout, 0);
	LOGD("output path set");
	// number of bytes in file
	unsigned long fileLen = env->GetArrayLength(filein);
	jfloat *colormatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
	jfloat *colormatrix2 = env->GetFloatArrayElements(colorMatrix2, 0);
	jfloat *neutral = env->GetFloatArrayElements(neutralColor, 0);
	LOGD("Matrixes set");

	const char *devicena = env->GetStringUTFChars(devicename, 0);
	UINT64 dir_offset = 0, dir_offset2 = 0, gpsIFD_offset = 0;

    int tx = 176, ty = 144;  // thumbnail image size

	short miso[] = {iso};


	const char *mOrientation = (char*) env->GetStringUTFChars(orientation, NULL);


////////////////////////////THUMB////////////////////////////////
unsigned char *thumbByte= (unsigned char*) env->GetByteArrayElements(mThumb,NULL);
unsigned long bLen = env->GetArrayLength(mThumb);
LOGD("ThumbStream Dize: %d", bLen);
unsigned char *bufferThumb;
unsigned short bits[176*144*2];

//////////////////////////////////////////////////////////////////////////////////////




	static const float black[] = {blacklevel, blacklevel, blacklevel , blacklevel};

	static const short CFARepeatPatternDim[] = { 2,2 };
	int status=1, i, j, row, col, b;
	TIFF *tif;
	unsigned char *buffer;
	unsigned char split; // single byte with 4 pairs of low-order bits
	unsigned short pixel[width]; // array holds 16 bits per pixel
	long white=0x3ff;

	LOGD("filesize: %d", fileLen);

	//create tiff file
	if (!(tif = TIFFOpen (strfileout, "w")))
	{
		LOGD("error while creating outputfile");
	}

//////////////////////////////////////IFD 0//////////////////////////////////////////////////////

	LOGD("TIFF Header");
	    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
	    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, width) != 0);
        assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, height) != 0);
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
        assert(TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA) != 0);
        //assert(TIFFSetField(tif, TIFFTAG_ROWSPERSTRIP, 480/2) != 0);
        assert(TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE) != 0);
        TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
        TIFFSetField(tif, TIFFTAG_MAKE, mMake);
        TIFFSetField(tif, TIFFTAG_MODEL, mModel);
        try
                        {
        if(0 == strcmp(mOrientation,"0") )
                    TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_TOPLEFT);
        if(0 == strcmp(mOrientation,"90") )
                    TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_RIGHTTOP);
        if(0 == strcmp(mOrientation,"180") )
                    TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_BOTRIGHT);
        if(0 == strcmp(mOrientation,"270") )
                    TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_LEFTBOT);
                    }
                    catch(...)
                    {
                        LOGD("Caught NULL NOT SET Orientation");
                    }
        assert(TIFFSetField(tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG) != 0);
      //  assert(TIFFSetField(tif, TIFFTAG_SAMPLESPERPIXEL, 3) != 0);
        TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreedCam by Troop");
        TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
        TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
        TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
        TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, ImageDescription);
        TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, colormatrix1);
        TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutral);
  	    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
   	    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);
   	    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, colormatrix2);
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
	///////////////////////////////////GPS IFD////////////////
	makeGPS_IFD(tif, Altitude,Latitude,Longitude,Provider,gpsTime);

        TIFFCheckpointDirectory(tif);
        TIFFWriteCustomDirectory(tif, &gpsIFD_offset);
    //////////////////////////////////// GPS END//////////////////////////////////////////
       TIFFSetDirectory(tif, 0);


    /////////////////////////////////// EXIF IFD //////////////////////////////
    LOGD("EXIF IFD DATA");
        if (TIFFCreateEXIFDirectory(tif) != 0) {
    		 LOGD("TIFFCreateEXIFDirectory() failed" );

    	}

    	if (!TIFFSetField( tif, EXIFTAG_ISOSPEEDRATINGS, 1, miso)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}


        if (!TIFFSetField( tif, EXIFTAG_EXPOSURETIME, expo)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}

        if (!TIFFSetField( tif, EXIFTAG_APERTUREVALUE, fNum)) {
        		 LOGD("Can't write Aper" );

        	}

        if (!TIFFSetField( tif, EXIFTAG_FLASH, flash)) {
        		 LOGD("Can't write Flas" );

        	}

        if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, focalL)) {
        		 LOGD("Can't write Focal" );

        	}

        if (!TIFFSetField( tif, EXIFTAG_FNUMBER, fNum)) {
               		 LOGD("Can't write FNum" );

                	}


//Check Point & Write are require checkpoint to update Current IFD Write Well to Write Close And Create IFD
    TIFFCheckpointDirectory(tif); //This Was missing it without it EXIF IFD was not being updated after adding SUB IFD
    TIFFWriteCustomDirectory(tif, &dir_offset);
///////////////////// GO Back TO IFD 0
         TIFFSetDirectory(tif, 0);
          TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
         ///////////////////////////// WRITE THE SUB IFD's SUB IFD + EXIF IFD AGain GPS IFD would also go here as well as other cust IFD
         TIFFSetField(tif, TIFFTAG_EXIFIFD, dir_offset);
         //////////////Assign Diff Array Offset dir_offset2
        // TIFFSetField (tif, TIFFTAG_SUBIFD, 1, &dir_offset2);

// Fake Thumb
   // write_image(tif, tx, ty, 255);

    //// to add either preview frame or jpeg or gen from bayer
/*
    for (int y = 0; y < 600; y++)
    {
        bits = rgbBuff + y*600;
        if (TIFFWriteScanline (tif, bits, y, 0) != 1) {
        		LOGD("Error writing TIFF thumb.");
        		}
    } */
    //Checkpoint to Update Write to Move to SUB IFD with Primary Raw Image
   // TIFFCheckpointDirectory(tif);
   // TIFFWriteDirectory(tif);
        LOGD("SUB IFD 1");
              /*  TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);

               	TIFFSetField (tif, TIFFTAG_IMAGEWIDTH, width);

               	TIFFSetField (tif, TIFFTAG_IMAGELENGTH, height);

               	TIFFSetField (tif, TIFFTAG_BITSPERSAMPLE, 16);

               	TIFFSetField (tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA);

               	TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE);

               	TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);

               	TIFFSetField (tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);*/

                	if(0 == strcmp(bayer,"BGGR"))
                		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
                	if(0 == strcmp(bayer , "GRGB"))
                		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
                	if(0 == strcmp(bayer , "RGGB"))
                    		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
                    if(0 == strcmp(bayer , "GBRG"))
                        	TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");

                	TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &white);


              	TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);
                if(blacklevel != 0)
                	{
                	    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, black);
                	    LOGD("wrote blacklevel");

                	    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
                	}
                buffer =(unsigned char *)malloc(rowSize);
                   if(tight == true)
                     {
                       LOGD("Processing tight RAW data...");
                       processSXXX10packed(tif, pixel, buffer, strfile, rowSize, width, height);
                       LOGD("Done tight RAW data...");
                     }
                   else
                     {
                       LOGD("Processing loose RAW data...");
                       processSXXX16(tif, pixel, buffer, strfile, rowSize, width, height);
                       LOGD("Done loose RAW data...");
                     }

    LOGD("Finalizng DNG");
    TIFFCheckpointDirectory(tif);
    TIFFWriteDirectory (tif);



	TIFFClose (tif);
	    LOGD("Free Memory");
        	free(pixel);
        	free(buffer);
        	free(colormatrix1);
        	free(colormatrix2);
        	free(neutral);
        	free(bufferThumb);
            free(bits);
	    LOGD("DNG Written to File");

}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_convertRawBytesToDngFast(JNIEnv *env, jobject thiz,
		        jbyteArray filein,
        		jstring fileout,
        		jint width,
        		jint height,
        		jfloatArray colorMatrix1,
        		jfloatArray colorMatrix2,
        		jfloatArray neutralColor,
        		jint blacklevel,
        		jstring bayerformat,
        		jint rowSize,
        		jboolean tight,
                jstring Make,
                jstring Model)
{

    	unsigned char *strfile= (unsigned char*) env->GetByteArrayElements(filein,NULL);
    	const char *bayer = (char*) env->GetStringUTFChars(bayerformat, NULL);
    	const char *strfileout= env->GetStringUTFChars(fileout, 0);
    	unsigned long fileLen = env->GetArrayLength(filein);
    	jfloat *colormatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    	jfloat *colormatrix2 = env->GetFloatArrayElements(colorMatrix2, 0);
    	jfloat *neutral = env->GetFloatArrayElements(neutralColor, 0);

    	const char *make = env->GetStringUTFChars(Make, 0);
    	const char *model = env->GetStringUTFChars(Model, 0);
        static const float black[] = {blacklevel, blacklevel, blacklevel , blacklevel};
    	static const short CFARepeatPatternDim[] = { 2,2 };
    	int status=1, i, j, row, col, b;
    	long white=0x3ff;
    	TIFF *tif;
    	unsigned char *buffer;
    	unsigned char split; // single byte with 4 pairs of low-order bits
    	unsigned short pixel[width]; // array holds 16 bits per pixel



    	//create tiff file
    	if (!(tif = TIFFOpen (strfileout, "w")))
    	{
    		LOGD("error while creating outputfile");
    	}




	LOGD("write tiffheader");
	TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
	TIFFSetField (tif, TIFFTAG_IMAGEWIDTH, width);
	TIFFSetField (tif, TIFFTAG_IMAGELENGTH, height);
	TIFFSetField (tif, TIFFTAG_BITSPERSAMPLE, 16);
	//TIFFSetField (tif, TIFFTAG_ROWSPERSTRIP, height);
	TIFFSetField (tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA);
	TIFFSetField (tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE);
	TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
	TIFFSetField (tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
	TIFFSetField (tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
	TIFFSetField (tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
	TIFFSetField (tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
	TIFFSetField (tif, TIFFTAG_MAKE, make);
    TIFFSetField (tif, TIFFTAG_MODEL, model);
	TIFFSetField (tif, TIFFTAG_COLORMATRIX1, 9, colormatrix1);
	TIFFSetField (tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutral);
	LOGD("bayerformat = %s", bayer);

                	if(0 == strcmp(bayer,"BGGR"))
                		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
                	if(0 == strcmp(bayer , "GRGB"))
                		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
                	if(0 == strcmp(bayer , "RGGB"))
                    		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
                    if(0 == strcmp(bayer , "GBRG"))
                        	TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");


	TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);


	//LOGD("write whitelvl");
	TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &white);
	TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, black);
	TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
	LOGD("write CALIBRATIONILLUMINANT1");
	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
	LOGD("write CALIBRATIONILLUMINANT2");
	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);
	LOGD("write colormatrix2");
	TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, colormatrix2);
	//LOGD("write FowardMatrix");
	//TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9, fowardmatrix1);
	//LOGD("write FowardMatrix2");
	//TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9, fowardmatrix2);

	LOGD("Processing RAW data...");
buffer =(unsigned char *)malloc(rowSize);

   // processSXXX16(tif, pixel, buffer, strfile, rowSize, width, height);

     // processSXXX10packed(tif, pixel, buffer, strfile, rowSize, width, height);

      if(tight == true)
                                 {
         processSXXX10packed(tif, pixel, buffer, strfile, rowSize, width, height);
         }

      else{
         processSXXX16(tif, pixel, buffer, strfile, rowSize, width, height);
         }


	TIFFWriteDirectory (tif);



	TIFFClose (tif);
    	 LOGD("Free Memory");
                	free(pixel);
                	free(buffer);
                	free(colormatrix1);
                	free(colormatrix2);
                	free(neutral);





}


JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_convertRawBytesToDngFastHDR2(JNIEnv *env, jobject thiz,
		        jbyteArray filein,
        		jstring fileout,
        		jint width,
        		jint height,
        		jfloatArray colorMatrix1,
        		jfloatArray colorMatrix2,
        		jfloatArray neutralColor,
        		jint blacklevel,
        		jstring bayerformat,
        		jint rowSize,
        		jboolean tight,
                jstring Make,
                jstring Model)
{

    	unsigned char *strfile= (unsigned char*) env->GetByteArrayElements(filein,NULL);
    	const char *bayer = (char*) env->GetStringUTFChars(bayerformat, NULL);
    	const char *strfileout= env->GetStringUTFChars(fileout, 0);
    	unsigned long fileLen = env->GetArrayLength(filein);
    	jfloat *colormatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    	jfloat *colormatrix2 = env->GetFloatArrayElements(colorMatrix2, 0);
    	jfloat *neutral = env->GetFloatArrayElements(neutralColor, 0);

    	const char *make = env->GetStringUTFChars(Make, 0);
    	const char *model = env->GetStringUTFChars(Model, 0);
        static const float black[] = {blacklevel, blacklevel, blacklevel , blacklevel};
    	static const short CFARepeatPatternDim[] = { 2,2 };
    	int status=1, i, j, row, col, b;
    	long white=0x3ff;
    	TIFF *tif;
    	unsigned char *buffer;
    	unsigned char split; // single byte with 4 pairs of low-order bits
    	unsigned short pixel[width]; // array holds 16 bits per pixel



    	//create tiff file
    	if (!(tif = TIFFOpen (strfileout, "w")))
    	{
    		LOGD("error while creating outputfile");
    	}



LOGD("Header");
    	TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    	TIFFSetField (tif, TIFFTAG_IMAGEWIDTH, width);
    	TIFFSetField (tif, TIFFTAG_IMAGELENGTH, height);
    	TIFFSetField (tif, TIFFTAG_BITSPERSAMPLE, 16);
    	TIFFSetField (tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA);
    	TIFFSetField(tif, TIFFTAG_MAKE, make);
        TIFFSetField(tif, TIFFTAG_MODEL, model);
    	TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE);
    	TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
    	TIFFSetField (tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
    	TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
    	TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    	TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
    	TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, colormatrix1);

    	    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutral);
    	if(0 == strcmp(bayer,"BGGR"))
    		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
    	if(0 == strcmp(bayer , "GRGB"))
    		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
    	if(0 == strcmp(bayer , "RGGB"))
        		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
        if(0 == strcmp(bayer , "GBRG"))
            	TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
    	TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);

        TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &white);

    	if(blacklevel != 0)
    	{
    	    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, black);
    	    LOGD("wrote blacklevel");

    	    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
    	}
    	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
    	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);
    	TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, colormatrix2);

    	LOGD("Header Done");
    	buffer =(unsigned char *)malloc(rowSize);
                            if(tight == true)
                            {

                              processSXXX10packed(tif, pixel, buffer, strfile, rowSize, width, height);
                              LOGD("Packed Done");

                            }
                          else
                            {

                              processSXXX16(tif, pixel, buffer, strfile, rowSize, width, height);

                            }
    	TIFFWriteDirectory (tif);
    	free(pixel);
                    	free(buffer);
                    	free(colormatrix1);
                    	free(colormatrix2);
                    	free(neutral);
    	TIFFClose (tif);




}

JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_convertRawBytesToDngFastHDR3(JNIEnv *env, jobject thiz,
		        jbyteArray filein,
        		jstring fileout,
        		jint width,
        		jint height,
        		jfloatArray colorMatrix1,
        		jfloatArray colorMatrix2,
        		jfloatArray neutralColor,
        		jint blacklevel,
        		jstring bayerformat,
        		jint rowSize,
        		jboolean tight,
                jstring Make,
                jstring Model)
{

    	unsigned char *strfile= (unsigned char*) env->GetByteArrayElements(filein,NULL);
    	const char *bayer = (char*) env->GetStringUTFChars(bayerformat, NULL);
    	const char *strfileout= env->GetStringUTFChars(fileout, 0);
    	unsigned long fileLen = env->GetArrayLength(filein);
    	jfloat *colormatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    	jfloat *colormatrix2 = env->GetFloatArrayElements(colorMatrix2, 0);
    	jfloat *neutral = env->GetFloatArrayElements(neutralColor, 0);

    	const char *make = env->GetStringUTFChars(Make, 0);
    	const char *model = env->GetStringUTFChars(Model, 0);
        static const float black[] = {blacklevel, blacklevel, blacklevel , blacklevel};
    	static const short CFARepeatPatternDim[] = { 2,2 };
    	int status=1, i, j, row, col, b;
    	long white=0x3ff;
    	TIFF *tif;
    	unsigned char *buffer;
    	unsigned char split; // single byte with 4 pairs of low-order bits
    	unsigned short pixel[width]; // array holds 16 bits per pixel



    	//create tiff file
    	if (!(tif = TIFFOpen (strfileout, "w")))
    	{
    		LOGD("error while creating outputfile");
    	}



LOGD("Header");
    	TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    	TIFFSetField (tif, TIFFTAG_IMAGEWIDTH, width);
    	TIFFSetField (tif, TIFFTAG_IMAGELENGTH, height);
    	TIFFSetField (tif, TIFFTAG_BITSPERSAMPLE, 16);
    	TIFFSetField (tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA);
    	TIFFSetField(tif, TIFFTAG_MAKE, make);
        TIFFSetField(tif, TIFFTAG_MODEL, model);
    	TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE);
    	TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
    	TIFFSetField (tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
    	TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
    	TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    	TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
    	TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, colormatrix1);
    	if(neutral != NULL)
    	    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutral);
    	if(0 == strcmp(bayer,"bggr"))
    		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
    	if(0 == strcmp(bayer , "grbg"))
    		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
    	if(0 == strcmp(bayer , "rggb"))
        		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
        if(0 == strcmp(bayer , "gbrg"))
            	TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
    	TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);


    	if(blacklevel != 0)
    	{
    	    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, black);
    	    LOGD("wrote blacklevel");

    	    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
    	}
    	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
    	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);
    	TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, colormatrix2);

    	LOGD("Header Done");
    	buffer =(unsigned char *)malloc(rowSize);
                            if(tight == true)
                            {

                              processSXXX10packed(tif, pixel, buffer, strfile, rowSize, width, height);
                              LOGD("Packed Done");

                            }
                          else
                            {

                              processSXXX16(tif, pixel, buffer, strfile, rowSize, width, height);

                            }
    	TIFFWriteDirectory (tif);
    	free(pixel);
                    	free(buffer);
                    	free(colormatrix1);
                    	free(colormatrix2);
                    	free(neutral);
    	TIFFClose (tif);




}







