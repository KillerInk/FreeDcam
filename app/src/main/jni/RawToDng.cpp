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


static TIFFField
customFields[] = {
	{ TIFFTAG_IMAGEWIDTH, -1, -1, TIFF_ASCII, 0, TIFF_SETGET_ASCII, TIFF_SETGET_UNDEFINED, FIELD_CUSTOM, 1, 0, "Custom1", NULL },
	{ TIFFTAG_DOTRANGE, -1, -1, TIFF_ASCII, 0, TIFF_SETGET_ASCII, TIFF_SETGET_UNDEFINED, FIELD_CUSTOM, 1, 0, "Custom2", NULL },
};

static TIFFFieldArray customFieldArray = { tfiatOther, 0, 2, customFields };


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
			jstring iDesc);

}

/*void CLASS android_loose_load_raw()
{
  uchar *data, *dp;
  int bwide, row, col, c;
  UINT64 bitbuf=0;

  bwide = (raw_width+5)/6 << 3;
  data = (uchar *) malloc (bwide);
  merror (data, "android_loose_load_raw()");
  for (row=0; row < raw_height; row++) {
    if (fread (data, 1, bwide, ifp) < bwide) derror();
    for (dp=data, col=0; col < raw_width; dp+=8, col+=6) {
      FORC(8) bitbuf = (bitbuf << 8) | dp[c^7];
      FORC(6) RAW(row,col+c) = (bitbuf >> c*10) & 0x3ff;
    }
  }
  free (data);
}*/

void processLooseRaw(TIFF *tif,unsigned short *pixel,unsigned char *buffer, unsigned char *strfile, int rowSize, int width, int height)
{
    int i, j, row, col, b;
    unsigned char split;
    rowSize = (width+5)/6 << 3;
    UINT64 bitbuf=0;
    for (row=0; row < height; row ++)
    {
        //LOGD("read row: %d", row);
        i = 0;
        for(b = row * rowSize; b < row * rowSize + rowSize; b++)
        	buffer[i++] = strfile[b];

        j = 0;

        for (col = 0; col < width; col+= 6)
        {
            for(int c = 0; c < 8; c++)
            {
                bitbuf = (bitbuf << 8) | buffer[c^7];
            }
            for(int c = 0; c < 6; c++)
            {
                pixel[col+c] = (bitbuf >> c*10) & 0x3ff;
            }

        }
        if (TIFFWriteScanline (tif, pixel, row, 0) != 1)
        {
        	LOGD("Error writing TIFF scanline.");
        }
    }
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

static void set_tags(TIFF *tif, int nx, int ny, int compression) {
   assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, nx) != 0);
   assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, ny) != 0);
   assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 8) != 0);
   assert(TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB) != 0);
   assert(TIFFSetField(tif, TIFFTAG_ROWSPERSTRIP, ny/2) != 0);
   assert(TIFFSetField(tif, TIFFTAG_COMPRESSION, compression) != 0);
   if (compression == COMPRESSION_LZW)
     assert(TIFFSetField(tif, TIFFTAG_PREDICTOR,
PREDICTOR_HORIZONTAL) != 0);
  TIFFSetField(tif, TIFFTAG_SAMPLEFORMAT, SAMPLEFORMAT_UINT);
   assert(TIFFSetField(tif, TIFFTAG_PLANARCONFIG,
PLANARCONFIG_CONTIG) != 0);
   assert(TIFFSetField(tif, TIFFTAG_SAMPLESPERPIXEL, 3) != 0);
}

void processTightRaw(TIFF *tif,unsigned short *pixel,unsigned char *buffer, unsigned char *strfile, int rowSize, int width, int height)
{
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
			pixel[col+0] = buffer[j++] << 8;
			pixel[col+1] = buffer[j++] << 8;
			pixel[col+2] = buffer[j++] << 8;
			pixel[col+3] = buffer[j++] << 8;
			//LOGD("Unpacked 4bytes");
			split = buffer[j++]; // low-order packed bits from previous 4 pixels
			//LOGD("Unpack 5th byte and move to last 4bytes pixel %d", col + 5);
			pixel[col+0] += (split & 0b11000000); // unpack them bits, add to 16-bit values, left-justified
			pixel[col+1] += (split & 0b00110000)<<2;
			pixel[col+2] += (split & 0b00001100)<<4;
			pixel[col+3] += (split & 0b00000011)<<6;
			//LOGD("Unpacked 5thbyte and moved");
		}
		if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
		LOGD("Error writing TIFF scanline.");
		}

	}
}

//static TIFFField


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
        jstring iDesc)
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
	float blackval;
	const char *devicena = env->GetStringUTFChars(devicename, 0);
	UINT64 dir_offset = 0, dir_offset2 = 1;
	UINT16 dir_seti = 0;
	int px = 640, py = 480;  // primary image size
    int tx = 160, ty = 120;  // thumbnail image size

	short miso[] = {iso};
	unsigned long sub_offset=0;

	//uint16_t dirr = 0;

	/*
	 * i seems the input=1024 is a long but need to converted *16 to a floatvalue??
	 */
	//long white=1024 * 16;

	//calculate the blacklevel
		//blacklevel =short
		//blacklevel *4 = long??
		//blacklevel * 16 = float??
    if(blacklevel != 0)
	    blackval = (blacklevel *4) *16;
	static const float black[] = {blackval, blackval, blackval , blackval};
	static const short CFARepeatPatternDim[] = { 2,2 };
	int status=1, i, j, row, col, b;
	TIFF *tif;
	unsigned char *buffer;
	unsigned char split; // single byte with 4 pairs of low-order bits
	unsigned short pixel[width]; // array holds 16 bits per pixel

	LOGD("filesize: %d", fileLen);

	//create tiff file
	if (!(tif = TIFFOpen (strfileout, "w")))
	{
		LOGD("error while creating outputfile");
	}
	LOGD("created outputfile");
/////////////////////////////////////////////////////////////////////////////////////////////////////

	LOGD("write tiffheader");
	TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 1);
    LOGD("wrote SUbIT");
	assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, 640) != 0);
    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, 480) != 0);
    assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 8) != 0);
    assert(TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB) != 0);
    assert(TIFFSetField(tif, TIFFTAG_ROWSPERSTRIP, 480/2) != 0);
    assert(TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE) != 0);
    TIFFSetField(tif, TIFFTAG_MAKE, mMake);
    LOGD("wrote Make");
    TIFFSetField(tif, TIFFTAG_MODEL, mModel);
    LOGD("wrote Model");

    assert(TIFFSetField(tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG) != 0);
    assert(TIFFSetField(tif, TIFFTAG_SAMPLESPERPIXEL, 3) != 0);
    TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreedCam by Troop");
    LOGD("wrote Software");
    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");

    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    LOGD("wrote dngbackversion");
    TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
    LOGD("wrote UniqueModel");
   // TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, ImageDescription);
    LOGD("wrote Software");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, colormatrix1);
    LOGD("wrote colormatrix1");
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutral);
    LOGD("wrote neutralmatrix");
    LOGD("write CALIBRATIONILLUMINANT1");
    	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
    	LOGD("write CALIBRATIONILLUMINANT2");
    	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);
    	LOGD("write colormatrix2");
    	TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, colormatrix2);

	TIFFSetField (tif, TIFFTAG_EXIFIFD, dir_offset);
	TIFFCheckpointDirectory(tif);
	TIFFSetDirectory(tif, 0);

	LOGD("CReating ExiF Directory");
        if (TIFFCreateEXIFDirectory(tif) != 0) {
    		 LOGD("TIFFCreateEXIFDirectory() failed" );

    	}
	 LOGD("Writing ISO");
    	if (!TIFFSetField( tif, EXIFTAG_ISOSPEEDRATINGS, 1, miso)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}

        LOGD("Writing ExposureTime");
        if (!TIFFSetField( tif, EXIFTAG_EXPOSURETIME, expo)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}
        	LOGD("Writing Aperture");
        if (!TIFFSetField( tif, EXIFTAG_APERTUREVALUE, fNum)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}
        	LOGD("Writing Flash");
        if (!TIFFSetField( tif, EXIFTAG_FLASH, flash)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}
        	LOGD("Writing Focal Lenght");
        if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, focalL)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}
        	LOGD("Writing FNUM ");
        if (!TIFFSetField( tif, EXIFTAG_FNUMBER, fNum)) {
               		 LOGD("Can't write SPECTRALSENSITIVITY" );

                	}

      TIFFWriteCustomDirectory(tif, &dir_offset);
      TIFFSetDirectory(tif, 0);

      TIFFSetField(tif, TIFFTAG_EXIFIFD, dir_offset);

      TIFFCheckpointDirectory(tif);

      TIFFWriteDirectory(tif);


       bool thumbnail = false;
         if (thumbnail) {
           //--------------------------------------------- IFD1
           //set_tags(tif, tx, ty, COMPRESSION_NONE);
           // write IFD1 (preliminary version) and create an empty IFD2

            LOGD("CheckPointl");
           TIFFCheckpointDirectory(tif);
           LOGD("Write DIR");
           TIFFWriteDirectory(tif);
           // set current dir to IFD1
           LOGD("Swt DIR");
           TIFFSetDirectory(tif, 0);
           // write pixels of thumbnail (has blue in upper left)
           LOGD("Done");
 TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, ImageDescription);
           // rewrite IFD1
           //LOGD("Done);
           TIFFWriteDirectory(tif);
           LOGD("IFD Written");
         }
         LOGD("Switch to IFD0");

         TIFFSetDirectory(tif, 0);
         LOGD("wrote dngversion");
         TIFFSetField (tif, TIFFTAG_SUBIFD, 1, &dir_offset);





    write_image(tif, tx, ty, 255);
    TIFFWriteDirectory(tif);

    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
               	LOGD("wrote SUbIT");
               	TIFFSetField (tif, TIFFTAG_IMAGEWIDTH, width);
               	LOGD("wrote width");
               	TIFFSetField (tif, TIFFTAG_IMAGELENGTH, height);
               	LOGD("wrote height");
               	TIFFSetField (tif, TIFFTAG_BITSPERSAMPLE, 16);
               	LOGD("wrote bitspersample");
               	TIFFSetField (tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA);
               	LOGD("wrote photmetric cfa");
               	TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE);
               	LOGD("wrote compression");
               	TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
               	LOGD("wrote samplesperpixel");
               	TIFFSetField (tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
               	LOGD("wrote planaerconfig");

               	LOGD("bayerformat = %s", bayer);
                	if(0 == strcmp(bayer,"bggr"))
                		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
                	if(0 == strcmp(bayer , "grbg"))
                		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
                	if(0 == strcmp(bayer , "rggb"))
                    		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
                    if(0 == strcmp(bayer , "gbrg"))
                        	TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
                    LOGD("wrote Bayerformat");

                	TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);
                	LOGD("wrote cfa pattern");


                    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, black);
                	LOGD("wrote blacklevel");

                	TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);

                	          buffer =(unsigned char *)malloc(rowSize);
                    LOGD("Write Main image");

                                   //processTightRaw(TIFF *tif,unsigned short *pixel,unsigned char *buffer, unsigned char *strfile, int rowSize, int width, int height)
                                  if(tight == true)
                                   {
                                       LOGD("Processing tight RAW data...");
                                       processTightRaw(tif, pixel, buffer, strfile, rowSize, width, height);
                                       LOGD("Done tight RAW data...");
                                   }
                                   else
                                   {
                                       LOGD("Processing loose RAW data...");
                                       processLooseRaw(tif, pixel, buffer, strfile, rowSize, width, height);
                                       LOGD("Done loose RAW data...");
                                   }

/*









    TIFFWriteDirectory (tif);*/

    LOGD("freed buffer, strfile, pixel");
    	free(pixel);
    	free(buffer);
    	free(colormatrix1);
    	free(colormatrix2);
    	free(neutral);



    LOGD("Writing ExiF Fields");
    /*if (!TIFFSetField( tif, EXIFTAG_SPECTRALSENSITIVITY, "EXIF Spectral Sensitivity")) {
		 LOGD("Can't write SPECTRALSENSITIVITY" );

	}*/






	LOGD("Done");



	LOGD("PIXELS and Tags Complete");
	//free(buffer);
	//free(strfile);


	TIFFClose (tif);
	LOGD("DNG FliseStream Closed");

	LOGD("Back to You Mr JAVA");
}



