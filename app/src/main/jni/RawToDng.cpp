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
			jstring iDesc,
			jbyteArray mThumb);

}

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
        jdouble expo,
        jstring make,
        jstring model,
        jint flash,
        jfloat fNum,
        jfloat focalL,
        jstring iDesc,
        jbyteArray mThumb)
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
	UINT64 dir_offset = 0, dir_offset2 = 0;

    int tx = 176, ty = 144;  // thumbnail image size

	short miso[] = {iso};

////////////////////////////THUMB////////////////////////////////
unsigned char *thumbByte= (unsigned char*) env->GetByteArrayElements(mThumb,NULL);
unsigned long bLen = env->GetArrayLength(mThumb);
LOGD("ThumbStream Dize: %d", bLen);
unsigned char *bufferThumb;
unsigned short bits[176*144*2];

//////////////////////////////////////////////////////////////////////////////////////




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

//////////////////////////////////////IFD 0//////////////////////////////////////////////////////

	LOGD("TIFF Header");
	    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 1);
	    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, 176) != 0);
        assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, 144) != 0);
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 8) != 0);
        assert(TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB) != 0);
        assert(TIFFSetField(tif, TIFFTAG_ROWSPERSTRIP, 480/2) != 0);
        assert(TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE) != 0);
        TIFFSetField(tif, TIFFTAG_MAKE, mMake);
        TIFFSetField(tif, TIFFTAG_MODEL, mModel);
        assert(TIFFSetField(tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG) != 0);
        assert(TIFFSetField(tif, TIFFTAG_SAMPLESPERPIXEL, 3) != 0);
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
	    TIFFSetField (tif, TIFFTAG_EXIFIFD, dir_offset);
	//CheckPOINT to KEEP EXIF IFD in MEMory
	TIFFCheckpointDirectory(tif);
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
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}

        if (!TIFFSetField( tif, EXIFTAG_FLASH, flash)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}

        if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, focalL)) {
        		 LOGD("Can't write SPECTRALSENSITIVITY" );

        	}

        if (!TIFFSetField( tif, EXIFTAG_FNUMBER, fNum)) {
               		 LOGD("Can't write SPECTRALSENSITIVITY" );

                	}

//Check Point & Write are require checkpoint to update Current IFD Write Well to Write Close And Create IFD
    TIFFCheckpointDirectory(tif); //This Was missing it without it EXIF IFD was not being updated after adding SUB IFD
    TIFFWriteCustomDirectory(tif, &dir_offset);
///////////////////// GO Back TO IFD 0
         TIFFSetDirectory(tif, 0);
         ///////////////////////////// WRITE THE SUB IFD's SUB IFD + EXIF IFD AGain GPS IFD would also go here as well as other cust IFD
         TIFFSetField(tif, TIFFTAG_EXIFIFD, dir_offset);
         //////////////Assign Diff Array Offset dir_offset2
         TIFFSetField (tif, TIFFTAG_SUBIFD, 1, &dir_offset2);

// Fake Thumb
    write_image(tif, tx, ty, 255);

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
    TIFFCheckpointDirectory(tif);
    TIFFWriteDirectory(tif);
        LOGD("SUB IFD 1");
                TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);

               	TIFFSetField (tif, TIFFTAG_IMAGEWIDTH, width);

               	TIFFSetField (tif, TIFFTAG_IMAGELENGTH, height);

               	TIFFSetField (tif, TIFFTAG_BITSPERSAMPLE, 16);

               	TIFFSetField (tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA);

               	TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE);

               	TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);

               	TIFFSetField (tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);

                	if(0 == strcmp(bayer,"bggr"))
                		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
                	if(0 == strcmp(bayer , "grbg"))
                		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
                	if(0 == strcmp(bayer , "rggb"))
                    		TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
                    if(0 == strcmp(bayer , "gbrg"))
                        	TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");


              	TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);
                TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, black);
                TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);

                buffer =(unsigned char *)malloc(rowSize);
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

    LOGD("Finalizng DNG");
    TIFFWriteDirectory (tif);

    LOGD("Free Memory");
    	free(pixel);
    	free(buffer);
    	free(colormatrix1);
    	free(colormatrix2);
    	free(neutral);
    	//\thumb
    	free(bufferThumb);
        free(bits);

	TIFFClose (tif);
	    LOGD("DNG Written to File");

}



