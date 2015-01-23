#include <jni.h>
#include <include/tiffio.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#define  LOG_TAG    "freedcam.RawToDngNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

typedef unsigned long long UINT64;

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
			jfloat expo);

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
        jfloat expo)
{
	LOGD("Start Converting");
	//load the rawdata into chararray

	unsigned char *strfile= (unsigned char*) env->GetByteArrayElements(filein,NULL);
	const char *bayer = (char*) env->GetStringUTFChars(bayerformat, NULL);
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

	short miso = iso;

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

	LOGD("write tiffheader");
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
	TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
	LOGD("wrote dngversion");
	TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
	LOGD("wrote dngbackversion");
	TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
	LOGD("wrote cameraModel");
	TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, colormatrix1);
	LOGD("wrote colormatrix1");
	if(neutral != null)
	    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutral);
	LOGD("wrote neutralmatrix");
	TIFFSetField(tif, EXIFTAG_ISOSPEEDRATINGS, miso);
	LOGD("wrote isoSpeed");
	TIFFSetField(tif, EXIFTAG_EXPOSURETIME,2, expo);
	LOGD("wrote exposuretime");
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



	//LOGD("write whitelvl");
	//TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &white);
	if(blacklevel != 0)
	{
	    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, black);
	    LOGD("wrote blacklevel");

	    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
	}
	LOGD("write CALIBRATIONILLUMINANT1");
	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
	LOGD("write CALIBRATIONILLUMINANT2");
	TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);
	LOGD("write colormatrix2");
	TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, colormatrix2);



	buffer =(unsigned char *)malloc(rowSize);

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

	TIFFWriteDirectory (tif);

	LOGD("work finished");
	//free(buffer);
	//free(strfile);
	free(pixel);
	free(buffer);
	free(colormatrix1);
	free(colormatrix2);
	free(neutral);
	LOGD("freed buffer, strfile, pixel");
	TIFFClose (tif);

	LOGD("all stuff freed");
}



