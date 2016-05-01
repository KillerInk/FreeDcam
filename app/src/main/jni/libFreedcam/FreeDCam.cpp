//
// Created by GeorgeKiarie on 01/05/2016.
//
#include "FreeDCam.h"

typedef unsigned long long uint64;
typedef unsigned short UINT16;
typedef unsigned char uint8;


 jlong DngWriter::GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler)
{
    
    return rawSize;
}

 jint DngWriter::GetRawHeight(JNIEnv *env, jobject thiz, jobject handler)
{
    
    return rawheight;
}


 void DngWriter::SetRawHeight(JNIEnv *env, jobject thiz, jobject handler, jint height)
{
    
    rawheight = (int) height;
}

 void DngWriter::SetModelAndMake(JNIEnv *env, jobject thiz, jobject handler, jstring model, jstring make)
{
    
    _make = (char*) env->GetStringUTFChars(make,NULL);
    _model = (char*) env->GetStringUTFChars(model,NULL);
}

 void DngWriter::SetExifData(JNIEnv *env, jobject thiz, jobject handler,
    jint iso,
    jdouble expo,
    jint flash,
    jfloat fNum,
    jfloat focalL,
    jstring imagedescription,
    jstring orientation,
    jdouble exposureIndex)
{
    
    _iso = iso;
    _exposure =expo;
    _flash = flash;
    _imagedescription = (char*) env->GetStringUTFChars(imagedescription,NULL);
    _orientation = (char*) env->GetStringUTFChars(orientation,NULL);
    _fnumber = fNum;
    _focallength = focalL;
    _exposureIndex = exposureIndex;
}

 jobject DngWriter::Create(JNIEnv *env, jobject thiz)
{
    DngWriter *writer = new DngWriter();
    return env->NewDirectByteBuffer(writer, 0);
}

 void DngWriter::SetGPSData(JNIEnv *env, jobject thiz,jobject handler, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime)
{
      
      Altitude = (double)Altitude;
      Latitude =  env->GetFloatArrayElements(Latitude,NULL);
      Longitude = env->GetFloatArrayElements(Longitude,NULL);
      Provider = (char*) env->GetStringUTFChars(Provider,NULL);
      gpsTime = (long)(gpsTime);
      gps = true;
}

 void DngWriter::SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, int widht, int height)
{
    
    _thumbData = (unsigned char*) env->GetByteArrayElements(mThumb,NULL);
    thumbheight = (int) height;
    thumwidth = widht;
}

 void DngWriter::SetOpCode2(JNIEnv *env, jobject thiz, jobject handler, jbyteArray opcode)
{
    
    opcode2Size = env->GetArrayLength(opcode);
    opcode2 = new unsigned char[opcode2Size];
    memcpy(opcode2, env->GetByteArrayElements(opcode,NULL), opcode2Size);
}
 void DngWriter::SetOpCode3(JNIEnv *env, jobject thiz, jobject handler, jbyteArray opcode)
{
    
    opcode3Size = env->GetArrayLength(opcode);
    opcode3 = new unsigned char[opcode3Size];
    memcpy(opcode3, env->GetByteArrayElements(opcode,NULL), opcode3Size);
}

 void DngWriter::Release(JNIEnv *env, jobject thiz, jobject handler)
{
    
    if(bayerBytes != NULL)
    {
        free(bayerBytes);
        bayerBytes = NULL;
    }
    if(opcode2Size >0)
    {
        free(opcode2);
        opcode2 = NULL;
    }
    if(opcode3Size >0)
    {
        free(opcode3);
        opcode3 = NULL;
    }
    /*if(_thumbData != NULL)
        free(_thumbData);*/
    if (writer != NULL)
        free(writer);
    writer = NULL;
}

 void DngWriter::SetBayerData(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fileBytes, jstring fileout)
{
    
    LOGD("Try to set Bayerdata");
    bayerBytes = new unsigned char[env->GetArrayLength(fileBytes)];
    LOGD("init bayerbytes");
    //bayerBytes = (unsigned char*) env->GetByteArrayElements(fileBytes,NULL);
    memcpy(bayerBytes, env->GetByteArrayElements(fileBytes,NULL), env->GetArrayLength(fileBytes));
    LOGD(" set Bayerdata");
    fileSavePath = (char*)  env->GetStringUTFChars(fileout,NULL);
    rawSize = env->GetArrayLength(fileBytes);
}

 void DngWriter::SetBayerDataFD(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fileBytes, jint fileDescriptor, jstring filename)
{
    
    LOGD("Try to set SetBayerDataFD");
    bayerBytes = new unsigned char[env->GetArrayLength(fileBytes)];
    LOGD("init bayerbytes");
    //bayerBytes = (unsigned char*) env->GetByteArrayElements(fileBytes,NULL);
    memcpy(bayerBytes, env->GetByteArrayElements(fileBytes,NULL), env->GetArrayLength(fileBytes));
    LOGD(" set Bayerdata");
    fileDes = (int)fileDescriptor;
    hasFileDes = true;
    LOGD(" fileDes : %d", fileDes);
    fileSavePath = "";
    rawSize = env->GetArrayLength(fileBytes);
    LOGD(" rawsize : %d", rawSize);
}

 void DngWriter::SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
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
    

    blacklevel = new float[4] {blacklevel, blacklevel, blacklevel,blacklevel};
    rawType = tight;
    rowSize =rowSize;
    colorMatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    colorMatrix2 =env->GetFloatArrayElements(colorMatrix2, 0);
    neutralColorMatrix = env->GetFloatArrayElements(neutralColor, 0);

    fowardMatrix1 = env->GetFloatArrayElements(fowardMatrix1, 0);
    fowardMatrix2 =env->GetFloatArrayElements(fowardMatrix2, 0);
    reductionMatrix1 = env->GetFloatArrayElements(reductionMatrix1, 0);
    reductionMatrix2 =env->GetFloatArrayElements(reductionMatrix2, 0);
    noiseMatrix =env->GetFloatArrayElements(noiseMatrix, 0);

    bayerformat = (char*)  env->GetStringUTFChars(bayerformat,0);
    rawheight = height;
    rawwidht = width;

}

TIFF *openfTIFF(char* fileSavePath)
{
    
    if (!(tif = TIFFOpen (fileSavePath, "w")))
    {
    	LOGD("openfTIFF:error while creating outputfile");
    }
    return tif;
}

TIFF *openfTIFFFD(char* fileSavePath, int fd)
{
    

    LOGD("FD: %d", fd);
    if (!(tif = TIFFFdOpen (fd,fileSavePath, "w")))
    {
        LOGD("openfTIFFFD:error while creating outputfile");
    }
    return tif;
}


void DngWriter::writeIfd0()
{
    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    LOGD("subfiletype");
    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, rawwidht) != 0);
    LOGD("width");
    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, rawheight) != 0);
    LOGD("height");
    if(rawType > 0)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
    else if (rawType < 0)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 12) != 0);
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
    TIFFSetField(tif, TIFFTAG_MAKE, _make);
    LOGD("make");
    TIFFSetField(tif, TIFFTAG_MODEL, _model);
    LOGD("model");
    try
    {
        if(0 == strcmp(_orientation,"0") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_TOPLEFT);
        if(0 == strcmp(_orientation,"90") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_RIGHTTOP);
        if(0 == strcmp(_orientation,"180") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_BOTRIGHT);
        if(0 == strcmp(_orientation,"270") )
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
    TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, _imagedescription);
    LOGD("imagedescription");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, colorMatrix1);
    LOGD("colormatrix1");
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutralColorMatrix);
    LOGD("neutralMatrix");
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 21);

    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 17);

    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, colorMatrix2);

    TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  fowardMatrix1);
    TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  fowardMatrix2);

    TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  noiseMatrix);
    LOGD("colormatrix2");
}



float * DngWriter::calculateGpsPos(double base)
{
    int seconds = base * 3600;
    int degress = seconds / 3600;
    seconds = abs(seconds % 3600);
    int minutes = seconds / 60;
    seconds %=  60;
    LOGD("baseValue: %i Degress:%i Minutes:%i Seconds%i",base, degress, minutes,seconds);
    return new float[3]{degress, minutes, seconds};
}

void DngWriter::makeGPS_IFD()
{
    LOGD("GPS IFD DATA");
    if (TIFFCreateGPSDirectory(tif) != 0)
    {
        LOGD("TIFFCreateGPSDirectory() failed" );
    }
    const char* longitudeRef = Longitude  < 0 ? "W" : "E";
    if (!TIFFSetField( tif, GPSTAG_GPSLongitudeRef, longitudeRef))
    {
        LOGD("Can't write LongitudeRef" );
    }
    LOGD("LONG REF Written %c", longitudeRef);

    if (!TIFFSetField(tif, GPSTAG_GPSLongitude, Longitude))
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

    if (!TIFFSetField( tif, GPSTAG_GPSLatitude,Latitude))
    {
        LOGD("Can't write Latitude" );
    }
    LOGD("Latitude Written");
    if (!TIFFSetField( tif, GPSTAG_GPSAltitude, Altitude))
    {
        LOGD("Can't write Altitude" );
    }
    LOGD("Altitude Written");
}

void DngWriter::writeExifIfd()
{
    /////////////////////////////////// EXIF IFD //////////////////////////////
        LOGD("EXIF IFD DATA");
        if (TIFFCreateEXIFDirectory(tif) != 0) {
            LOGD("TIFFCreateEXIFDirectory() failed" );
        }
        short iso[] = {_iso};
        LOGD("EXIF dir created");
        if (!TIFFSetField( tif, EXIFTAG_ISOSPEEDRATINGS,1, iso)) {
            LOGD("Can't write SPECTRALSENSITIVITY" );
        }
        LOGD("iso");
        if (!TIFFSetField( tif, EXIFTAG_FLASH, _flash)) {
            LOGD("Can't write Flas" );
        }
        LOGD("flash");
        if (!TIFFSetField( tif, EXIFTAG_APERTUREVALUE, _fnumber)) {
            LOGD("Can't write Aper" );
        }
        LOGD("aperture");

        if (!TIFFSetField( tif, EXIFTAG_EXPOSURETIME,_exposure)) {
            LOGD("Can't write SPECTRALSENSITIVITY" );
        }
        LOGD("exposure");


        if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, _focallength)) {
            LOGD("Can't write Focal" );
        }
        LOGD("focal");

        if (!TIFFSetField( tif, EXIFTAG_FNUMBER, _fnumber)) {
            LOGD("Can't write FNum" );
        }
        LOGD("fnumber");


    //Check Point & Write are require checkpoint to update Current IFD Write Well to Write Close And Create IFD
}

void DngWriter::processTight()
{
    LOGD("IN SXXXXl0");
    int i, j, row, col, b;
    unsigned char *buffer, *dp;
    unsigned char split; // single byte with 4 pairs of low-order bits
    unsigned short pixel[rawwidht]; // array holds 16 bits per pixel

    LOGD("buffer set");
    j=0;
    if(rowSize == 0)
        rowSize =  -(-5 * rawwidht >> 5) << 3;
    buffer =(unsigned char *)malloc(rowSize);
    memset( buffer, 0, rowSize);
    if (buffer == NULL)
    {
        LOGD("allocating buffer failed try again");
        buffer =(unsigned char *)malloc(rowSize);
    }
    LOGD("rowsize:%i", rowSize);
   //#pragma omp parallel for
	for (row=0; row < rawheight; row ++)
	{
		i = 0;
		//#pragma omp parallel for
		for(b = row * rowSize; b < row * rowSize + rowSize; b++)
			buffer[i++] = bayerBytes[b];
		j = 0;


		for (dp=buffer, col = 0; col < rawwidht; dp+=5, col+= 4)
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



	//free(pixel);
	LOGD("Mem Released");
}

void DngWriter::process10tight()
{
    unsigned char* ar = bayerBytes;
    unsigned char* tmp = new unsigned char[5];
    int bytesToSkip = 0;
    LOGD("writer-RowSize: %d  rawheight:%d ,rawwidht: %d",  rawSize,rawheight, rawwidht);
    int realrowsize = rawSize/rawheight;
    int shouldberowsize = rawwidht*10/8;
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    if (realrowsize != shouldberowsize)
        bytesToSkip = realrowsize - shouldberowsize;
    LOGD("bytesToSkip: %i", bytesToSkip);
    int row = shouldberowsize;
    unsigned char* out = new unsigned char[(int)shouldberowsize*rawheight];
    int m = 0;
    for(int i =0; i< rawSize; i+=5)
    {
        //LOGD("Process i: %d  filesize: %d", i, rawSize);
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
    TIFFWriteRawStrip(tif, 0, out, rawheight*shouldberowsize);

    TIFFRewriteDirectory(tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);

    delete[] out;
}

void DngWriter::process12tight()
{
    unsigned char* ar = bayerBytes;
    unsigned char* tmp = new unsigned char[5];
    int bytesToSkip = 0;
    LOGD("writer-RowSize: %d  rawheight:%d ,rawwidht: %d",  rawSize,rawheight, rawwidht);
    int realrowsize = rawSize/rawheight;
    int shouldberowsize = rawwidht*12/8;
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    if (realrowsize != shouldberowsize)
        bytesToSkip = realrowsize - shouldberowsize;
    LOGD("bytesToSkip: %i", bytesToSkip);
    int row = shouldberowsize;
    unsigned char* out = new unsigned char[(int)shouldberowsize*rawheight];
    int m = 0;
    for(int i =0; i< rawSize; i+=3)
    {
        //LOGD("Process i: %d  filesize: %d", i, rawSize);
        if(i == row)
        {
            row += shouldberowsize +bytesToSkip;
            i+=bytesToSkip;
            //LOGD("new row: %i", row/shouldberowsize);
        }


                out[m++] = (ar[i]); // 00110001
                out[m++] = (ar[i+2] & 0b11110000 ) <<4 | (ar[i+1] & 0b11110000)>>4; // 01 001100
                out[m++] = (ar[i+1]& 0b00001111 )<< 4 | (ar[i+2] & 0b00001111 ) >>4 ;// 10 01 0011

             
    }
    TIFFWriteRawStrip(tif, 0, out, rawheight*shouldberowsize);

    TIFFRewriteDirectory(tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);

    delete[] out;
}

void DngWriter::processLoose()
{
    unsigned short a;
    int i, j, row, col, b;
    unsigned char *buffer, *dp;
    unsigned char split; // single byte with 4 pairs of low-order bits
    unsigned short pixel[rawwidht]; // array holds 16 bits per pixel

    uint64 colorchannel;

    j=0;

    rowSize= (rawwidht+5)/6 << 3;
    buffer =(unsigned char *)malloc(rowSize);
    memset( buffer, 0, rowSize);
    if (buffer == NULL)
    {
        LOGD("allocating buffer failed try again");
        buffer =(unsigned char *)malloc(rowSize);
    }


	for (row=0; row < rawheight; row ++)
	{

		//LOGD("read row: %d", row);
		i = 0;
		for(b = row * rowSize; b < (row * rowSize) + rowSize; b++)
			buffer[i++] = bayerBytes[b];

		// offset into buffer
		j = 0;
		/*
		 * get 5 bytes from buffer and move first 4bytes to 16bit
		 * split the 5th byte and add the value to the first 4 bytes
		 * */
		for (dp=buffer, col = 0; col < rawwidht; dp+=8, col+= 6)
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
    TIFFRewriteDirectory(tif);
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

void DngWriter::processSXXX16()
{
    unsigned short a;
    int i, j, row, col, b, nextlog;
    unsigned char split; // single byte with 4 pairs of low-order bits
    unsigned short pixel[rawwidht];
    j=0;
	for (row=0; row < rawheight; row ++)
	{
        nextlog = 0;
		for (col = 0; col < rawwidht; col+=4)
		{ // iterate over pixel columns
            for (int k = 0; k < 4; ++k)
            {
                unsigned short low = bayerBytes[j++];
                unsigned short high =   bayerBytes[j++];
                pixel[col+k] =  high << 8 |low;
                if(col < 4 && row < 4)
                    LOGD("Pixel : %i, high: %i low: %i ", pixel[col+k], high, low);
            }
		}
		if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
		LOGD("Error writing TIFF scanline.");
		}
	}
    TIFFRewriteDirectory(tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);
    LOGD("Free Memory");
}

unsigned char* DngWriter::BufferedRaw(const char* in)
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

void DngWriter::writeRawStuff()
{
    if(0 == strcmp(bayerformat,"bggr"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
    if(0 == strcmp(bayerformat , "grbg"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
    if(0 == strcmp(bayerformat , "rggb"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
    if(0 == strcmp(bayerformat , "gbrg"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
    long white=0x3ff;
    TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &white);

    short CFARepeatPatternDim[] = { 2,2 };
    TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);

    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, blacklevel);
    LOGD("wrote blacklevel");
    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
    //**********************************************************************************

    LOGD("Set OP or not");
    if(opcode2Size >0)
    {
        LOGD("Set OP2");
        TIFFSetField(tif, TIFFTAG_OPC2, opcode2Size, opcode2);
    }
    if(opcode3Size >0)
    {
        LOGD("Set OP3");
        TIFFSetField(tif, TIFFTAG_OPC3, opcode3Size, opcode3);
    }
    if(opcode3Size >0 || opcode2Size >0)
    {
        //TIFFCheckpointDirectory(tif);
        //TIFFRewriteDirectory(tif);
    }
    if(rawType == 0)
    {
        LOGD("Processing tight RAW data...");
        process10tight(tif, writer);
        LOGD("Done tight RAW data...");
    }
    else if (rawType == 1)
    {
        LOGD("Processing loose RAW data...");
        processLoose(tif, writer);
        LOGD("Done loose RAW data...");
    }
    else if (rawType == 2)
        processSXXX16(tif,writer);
    else if (rawType == 3)
        processTight(tif, writer);
        else if (rawType == 4)
                process12tight(tif, writer);
}

 void DngWriter::WriteDNG(JNIEnv *env, jobject thiz, jobject handler)
{
    uint64 dir_offset = 0, dir_offset2 = 0, gpsIFD_offset = 0;
    
    
    LOGD("has file description: %b", hasFileDes);
    if(hasFileDes == true)
    {
        tif = openfTIFFFD("", fileDes);
    }
    else
        tif = openfTIFF(fileSavePath);

    writeIfd0(tif,writer);
    TIFFSetField (tif, TIFFTAG_EXIFIFD, dir_offset);
    LOGD("set exif");
    //CheckPOINT to KEEP EXIF IFD in MEMory
    //Try FiX DIR
    TIFFCheckpointDirectory(tif);
    TIFFWriteDirectory(tif);
    TIFFSetDirectory(tif, 0);

    if(gps == true)
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
    if(gps)
        TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
             ///////////////////////////// WRITE THE SUB IFD's SUB IFD + EXIF IFD AGain GPS IFD would also go here as well as other cust IFD
    TIFFSetField(tif, TIFFTAG_EXIFIFD, dir_offset);

    writeRawStuff(tif,writer);

    if (bayerBytes == NULL)
        return;
    delete[] bayerBytes;
    bayerBytes = NULL;

}

void ImageProcessor::YuvToRgb(unsigned char* yuv420sp, jint width, jint height) {
    _width = width;
    _height = height;
    int frameSize = width * height;
    _data = new int[frameSize];
    _colorchannels = 4;

    int             sz;
    int             i;
    int             j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int       pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;
    int w = width;
    int h = height;
    sz = w * h;
    for(j = 0; j < h ; j++) {
        pixPtr = j * w;
        jDiv2 = j >> 1;
        for(i = 0; i < w; i++) {
            Y = yuv420sp[pixPtr];
            if(Y < 0) Y += 255;
            if((i & 0x1) != 1) {
                cOff = sz + jDiv2 * w + (i >> 1) * 2;
                Cb = yuv420sp[cOff];
                if(Cb < 0) Cb += 127; else Cb -= 128;
                Cr = yuv420sp[cOff + 1];
                if(Cr < 0) Cr += 127; else Cr -= 128;
            }

            //ITU-R BT.601 conversion
            //
            //R = 1.164*(Y-16) + 2.018*(Cr-128);
            //G = 1.164*(Y-16) - 0.813*(Cb-128) - 0.391*(Cr-128);
            //B = 1.164*(Y-16) + 1.596*(Cb-128);
            //
            Y = Y + (Y >> 3) + (Y >> 5) + (Y >> 7);
            R = Y + (Cr << 1) + (Cr >> 6);
            if(R < 0) R = 0; else if(R > 255) R = 255;
            G = Y - Cb + (Cb >> 3) + (Cb >> 4) - (Cr >> 1) + (Cr >> 3);
            if(G < 0) G = 0; else if(G > 255) G = 255;
            B = Y + Cb + (Cb >> 1) + (Cb >> 4) + (Cb >> 5);
            if(B < 0) B = 0; else if(B > 255) B = 255;
            _data[pixPtr++] = GetPixelARGBFromRGB(R,G,B);
        }
    }
}


jobject ImageProcessor::getBitmap(JNIEnv * env)
{
    void *bitmapPixels;
    int ret;
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap",
                                                            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf",
                                                                   "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass,
                                                       valueOfBitmapConfigFunction, configName);
    jobject newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction, _width,
                                                    _height, bitmapConfig);

    if ((ret = AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels)) < 0) {
        LOGD("AndroidBitmap_lockPixels() failed ! error=%d", ret);

        return NULL;
    }
    LOGD("pixel locked");
    uint32_t* newBitmapPixels = (uint32_t*) bitmapPixels;
    memcpy(newBitmapPixels,(uint32_t*) _data, (_width * _height * sizeof(uint32_t)));



    LOGD("memcopy start");
    LOGD("memcopy end");

    AndroidBitmap_unlockPixels(env, newBitmap);
    //free(_data);

    return newBitmap;
}

void ImageProcessor::Release()
{
    if (_data != NULL) {
        LOGD("Release");
        delete [] _data;
    }
}

void ImageProcessor::DrawToSurface(JNIEnv * env, jobject surface)
{
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_Buffer buffer;
    if (ANativeWindow_lock(window, &buffer, NULL) == 0) {
        memcpy(buffer.bits,_data, _width*_height* sizeof(int));
    }
    ANativeWindow_unlockAndPost(window);

    ANativeWindow_release(window);
    //delete [] native;
}

void ImageProcessor::DrawToBitmap(JNIEnv * env, jobject bitmap)
{
    void* pixels;
    int ret;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGD("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    memcpy(pixels,_data, _width*_height* sizeof(int));
    AndroidBitmap_unlockPixels(env, bitmap);
}

jobject ImageProcessor::GetData(JNIEnv * env)
{
    jintArray result;
    jint size = _width*_height;
    result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, _data);

    return result;
}

jobjectArray ImageProcessor::GetHistogramm(JNIEnv * env)
{
    jint* red = new int[256];
    jint* green = new int[256];
    jint* blue =new  int[256];
    for ( int i = 0 ; i < _width ; i ++) {
        for ( int j = 0 ; j < _height ; j ++) {
            int index = j * _width + i ;
            int b = (int) ((_data[index] >> 16) & 0xFF);
            int g = (int) ((_data[index] >> 8) & 0xFF);
            int r = (int) (_data[index] & 0xFF);
            red[r]++;
            green[g]++;
            blue[b]++;
        }
    }

    jclass intArray1DClass = env->FindClass("[I");
    jclass intArray2DClass = env->FindClass("[[I");

    jintArray  redar = env->NewIntArray(256);
    env->SetIntArrayRegion(redar, (jsize) 0, (jsize) 256, (jint*)red);

    jintArray  bluear = env->NewIntArray(256);
    env->SetIntArrayRegion(bluear, (jsize) 0, (jsize) 256, (jint*)blue);

    jintArray  greenar = env->NewIntArray(256);
    env->SetIntArrayRegion(greenar, (jsize) 0, (jsize) 256, (jint*)green);

    /*jobject rgbar = env->NewObjectArray(3, intArray2DClass, NULL);
    env->SetObjectArrayElement(rgbar, 0,(jintArray)redar);
    //env->SetObjectArrayElement(rgbar, 0, redar);
    env->SetObjectArrayElement(rgbar, 1, (jint*) greenar);
    env->SetObjectArrayElement(rgbar, 2, (jint*) bluear );*/

    jobjectArray array2D = env->NewObjectArray(
            3, intArray1DClass, NULL);
    env->SetObjectArrayElement(array2D, 0, redar);
    env->SetObjectArrayElement(array2D, 1, greenar);
    env->SetObjectArrayElement(array2D, 2, bluear);
    return array2D;
}



void ImageProcessor::applyLanczos() {
    int* newarray = new int[_width * _height];
    for (int y = 1; y < _height - 1; y++) {
        for (int x = 1; x < _width - 1; x++) {
            int c00 = GetPixel(x - 1, y - 1);
            int c01 = GetPixel(x - 1, y);
            int c02 = GetPixel(x - 1, y + 1);
            int c10 = GetPixel(x, y - 1);
            int c11 = GetPixel(x, y);
            int c12 = GetPixel(x, y + 1);
            int c20 = GetPixel(x + 1, y - 1);
            int c21 = GetPixel(x + 1, y);
            int c22 = GetPixel(x + 1, y + 1);
            int r = -GetPixelRedFromInt(c00) - GetPixelRedFromInt(c01) - GetPixelRedFromInt(c02) +
                    -GetPixelRedFromInt(c10) + 8 * GetPixelRedFromInt(c11) - GetPixelRedFromInt(c12) +
                    -GetPixelRedFromInt(c20) - GetPixelRedFromInt(c21) - GetPixelRedFromInt(c22);
            int g = -GetPixelGreenFromInt(c00) - GetPixelGreenFromInt(c01) - GetPixelGreenFromInt(c02) +
                    -GetPixelGreenFromInt(c10) + 8 * GetPixelGreenFromInt(c11) - GetPixelGreenFromInt(c12) +
                    -GetPixelGreenFromInt(c20) - GetPixelGreenFromInt(c21) - GetPixelGreenFromInt(c22);
            int b = -GetPixelBlueFromInt(c00) - GetPixelBlueFromInt(c01) - GetPixelBlueFromInt(c02) +
                    -GetPixelBlueFromInt(c10) + 8 * GetPixelBlueFromInt(c11) - GetPixelBlueFromInt(c12) +
                    -GetPixelBlueFromInt(c20) - GetPixelBlueFromInt(c21) - GetPixelBlueFromInt(c22);
            if (r < 0) r = 0; else if (r > 255) r = 255;
            if (g < 0) g = 0; else if (g > 255) g = 255;
            if (b < 0) b = 0; else if (b > 255) b = 255;
            WritePixel(x, y, GetPixelARGBFromRGB(r, g, b), newarray);
        }
    }
    memcpy(_data,newarray, (_width * _height * sizeof(int)));
    delete [] newarray;
}

void ImageProcessor::applyFocusPeak()
{
    int factorForTrans = 50;
    int* newarray = new int[_width * _height * sizeof(int)];
    for (int y = 1; y < _height - 1; y++) {
        for (int x = 1; x < _width - 1; x++) {
            int r = -GetPixelRed(x - 1, y - 1) - GetPixelRed(    x - 1, y) - GetPixelRed(x - 1, y + 1) +
                    -GetPixelRed(x    , y - 1) + 8 * GetPixelRed(x    , y) - GetPixelRed(x    , y + 1) +
                    -GetPixelRed(x + 1, y - 1) - GetPixelRed(    x + 1, y) - GetPixelRed(x + 1, y + 1);
            if (r < 0) r = 0; else if (r > 255) r = 255;
            if(r < factorForTrans ) {
                WritePixel(x, y, GetPixelFromARGB(0, 0, 0, 0), newarray);
            }
            else {
                WritePixel(x, y, GetPixelARGBFromRGB(0, 0 , 255), newarray);
                //LOGD("Wrote non black Pixel");
            }
        }
    }
    memcpy(_data,newarray, (_width * _height * sizeof(int)));
    delete [] newarray;
}

void ImageProcessor::Apply3x3Filter(int filter[3][3])
{
    LOGD("Apply 3x3 Filter");
    int* newarray = new int[_width * _height];
    double factor = 1.0;
    double bias = 0.0;
    int filterWidth = 3;
    int filterHeight = 3;
    //apply the filter
    for (int y = 1; y < _height - 1; y++) {
        for (int x = 1; x < _width - 1; x++) {
            int c00 = GetPixel(x - 1, y - 1);
            int c01 = GetPixel(x - 1, y);
            int c02 = GetPixel(x - 1, y + 1);
            int c10 = GetPixel(x, y - 1);
            int c11 = GetPixel(x, y);
            int c12 = GetPixel(x, y + 1);
            int c20 = GetPixel(x + 1, y - 1);
            int c21 = GetPixel(x + 1, y);
            int c22 = GetPixel(x + 1, y + 1);
            int r, g, b = 0;

            r =     -GetPixelRedFromInt(c00)*filter[0][0] - GetPixelRedFromInt(c01)*filter[0][1] + GetPixelRedFromInt(c02)*filter[0][2]+
                    -GetPixelRedFromInt(c10)*filter[1][0] + GetPixelRedFromInt(c11)*filter[1][1] - GetPixelRedFromInt(c12)*filter[1][2] +
                    -GetPixelRedFromInt(c20)*filter[2][0] - GetPixelRedFromInt(c21)*filter[2][1] - GetPixelRedFromInt(c22)*filter[2][2];

            g =     -GetPixelGreenFromInt(c00)*filter[0][0] - GetPixelGreenFromInt(c01)*filter[0][1]   - GetPixelGreenFromInt(c02)*filter[0][2] +
                    -GetPixelGreenFromInt(c10)*filter[1][0] + GetPixelGreenFromInt(c11)*filter[1][1]   - GetPixelGreenFromInt(c12)*filter[1][2] +
                    -GetPixelGreenFromInt(c20)*filter[2][0] - GetPixelGreenFromInt(c21)*filter[2][1]   - GetPixelGreenFromInt(c22)*filter[2][2];

            b =     -GetPixelBlueFromInt(c00)*filter[0][0]  - GetPixelBlueFromInt(c01)*filter[0][1] - GetPixelBlueFromInt(c02)*filter[0][2] +
                    -GetPixelBlueFromInt(c10)*filter[1][0]  + GetPixelBlueFromInt(c11)*filter[1][1] - GetPixelBlueFromInt(c12)*filter[1][2] +
                    -GetPixelBlueFromInt(c20)*filter[2][0]  - GetPixelBlueFromInt(c21)*filter[2][1] - GetPixelBlueFromInt(c22)*filter[2][2];
            //truncate values smaller than zero and larger than 255
            if (r < 0) r = 0; else if (r > 255) r = 255;
            if (g < 0) g = 0; else if (g > 255) g = 255;
            if (b < 0) b = 0; else if (b > 255) b = 255;
            //LOGD("R:%i G:%i B:%i",r,g,b);
            WritePixel(x, y, GetPixelARGBFromRGB(r, g, b), newarray);
        }
    }
    _data = newarray;
    LOGD("Done 3x3 Filter");
}

void ImageProcessor::unpackRAWToRGBA(JNIEnv * env,jstring jfilename)
{
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
    const char *strFilename = (env)->GetStringUTFChars(jfilename, &bIsCopy);
    raw.open_file(strFilename);
    LOGD("File opend");

    ret = raw.unpack();
    LOGD("unpacked img %i", ret);
    ret = raw.dcraw_process();
    LOGD("processing dcraw %i", ret);
    libraw_processed_image_t *image = raw.dcraw_make_mem_image(&ret);
    _width = image->width;
    _height = image->height;
    _data = new int[_width * _height];
    _colorchannels = 4;
    LOGD("memcopy start");
    int bufrow = 0;
    int size = image->width* image->height;
    for (int count = 0; count < size; count++)
    {
            uint32_t p = GetPixelARGBFromRGB(image->data[bufrow+2], image->data[bufrow+1], image->data[bufrow]);
            _data[count] = p;
            bufrow += 3;
    }
    LOGD("memcopy end");
    LibRaw::dcraw_clear_mem(image);
}

void ImageProcessor::loadJPEGToRGBA(JNIEnv * env,jstring jfilename)
{
    jboolean bIsCopy;
    const char* strFilename = (env)->GetStringUTFChars(jfilename , &bIsCopy);
    FILE *file = fopen(strFilename, "rb");
    if (file != NULL)
    {
        struct jpeg_decompress_struct info;
        struct jpeg_error_mgr derr;
        info.err = jpeg_std_error(&derr);
        jpeg_create_decompress(&info); //fills info structure
        jpeg_stdio_src(&info, file);        //void
        int ret_Read_Head = jpeg_read_header(&info, 1); //int
        if (ret_Read_Head != JPEG_HEADER_OK)
        {
        	printf("jpeg_read_header failed\n");
        	fclose(file);
        	jpeg_destroy_decompress(&info);

        }
        else
        {
        	(void) jpeg_start_decompress(&info);
        	_width = info.output_width;
        	_height = info.output_height;
        	_colorchannels = info.num_components; // 3 = RGB, 4 = RGBA
        	unsigned long dataSize = _width * _height * _colorchannels;

        	_data = (int*) malloc(_width * _height);
        	unsigned char* buffer = (unsigned char*) malloc(_width* _colorchannels);
        	if (_data != NULL && buffer != NULL)
        	{
        	    int count =0;
        	    unsigned char* rowptr;
                while (info.output_scanline < _height) {
                    rowptr = (unsigned char *)buffer + info.output_scanline * _width * _colorchannels;
                    jpeg_read_scanlines(&info, &rowptr, 1);
                    for(int t =0; t < _width*_colorchannels; t+=3)
                    {
                        _data[count] = GetPixelARGBFromRGB(buffer[t+2], buffer[t+1], buffer[t]);
                    }
                }
                free(buffer);
        	}
        	jpeg_finish_decompress(&info);
        	_colorchannels = 4;
        	fclose(file);
        }
    }
}

void ImageProcessor::unpackRAWToRGB(JNIEnv * env,jstring jfilename)
{
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
    const char *strFilename = (env)->GetStringUTFChars(jfilename, &bIsCopy);
    raw.open_file(strFilename);
    LOGD("File opend");

    ret = raw.unpack();
    LOGD("unpacked img %i", ret);
    ret = raw.dcraw_process();
    LOGD("processing dcraw %i", ret);
    libraw_processed_image_t *image = raw.dcraw_make_mem_image(&ret);
    _width = image->width;
    _height = image->height;
    _data = new int[_width * _height];
    _colorchannels = 4;
    LOGD("memcopy start");
    int bufrow = 0;
    int size = image->width* image->height;
    for (int count = 0; count < size; count++)
    {
            int p = GetPixelRGBFromRGB(image->data[bufrow+2], image->data[bufrow+1], image->data[bufrow]);
            _data[count] = p;
            bufrow += 3;
    }
    LOGD("memcopy end");
    LibRaw::dcraw_clear_mem(image);
}

void ImageProcessor::loadJPEGToRGB(JNIEnv * env,jstring jfilename)
{
    jboolean bIsCopy;
    const char* strFilename = (env)->GetStringUTFChars(jfilename , &bIsCopy);
    FILE *file = fopen(strFilename, "rb");
    if (file != NULL)
    {
        struct jpeg_decompress_struct info;
        struct jpeg_error_mgr derr;
        info.err = jpeg_std_error(&derr);
        jpeg_create_decompress(&info); //fills info structure
        jpeg_stdio_src(&info, file);        //void
        int ret_Read_Head = jpeg_read_header(&info, 1); //int
        if (ret_Read_Head != JPEG_HEADER_OK)
        {
        	printf("jpeg_read_header failed\n");
        	fclose(file);
        	jpeg_destroy_decompress(&info);

        }
        else
        {
        	(void) jpeg_start_decompress(&info);
        	_width = info.output_width;
        	_height = info.output_height;
        	_colorchannels = info.num_components; // 3 = RGB, 4 = RGBA
        	unsigned long dataSize = _width * _height * _colorchannels;

        	_data = (int*) malloc(_width * _height);
        	unsigned char* buffer = (unsigned char*) malloc(_width* _colorchannels);
        	if (_data != NULL && buffer != NULL)
        	{
        	    int count =0;
        	    unsigned char* rowptr;
                while (info.output_scanline < _height) {
                    rowptr = (unsigned char *)buffer + info.output_scanline * _width * _colorchannels;
                    jpeg_read_scanlines(&info, &rowptr, 1);
                    for(int t =0; t < _width*_colorchannels; t+=3)
                    {
                        _data[count] = GetPixelRGBFromRGB(buffer[t+2], buffer[t+1], buffer[t]);
                    }
                }
                free(buffer);
        	}
        	jpeg_finish_decompress(&info);
        	fclose(file);
        }
    }
}


void ImageProcessor::StackAverageJPEGToARGB(JNIEnv * env,jstring jfilename)
{
    jboolean bIsCopy;
    const char* strFilename = (env)->GetStringUTFChars(jfilename , &bIsCopy);
    FILE *file = fopen(strFilename, "rb");
    if (file != NULL)
    {
        struct jpeg_decompress_struct info;
        struct jpeg_error_mgr derr;
        info.err = jpeg_std_error(&derr);
        jpeg_create_decompress(&info); //fills info structure
        jpeg_stdio_src(&info, file);        //void
        int ret_Read_Head = jpeg_read_header(&info, 1); //int
        if (ret_Read_Head != JPEG_HEADER_OK)
        {
        	printf("jpeg_read_header failed\n");
        	fclose(file);
        	jpeg_destroy_decompress(&info);
        }
        else
        {
        	(void) jpeg_start_decompress(&info);
        	_width = info.output_width;
        	_height = info.output_height;
        	_colorchannels = info.num_components; // 3 = RGB, 4 = RGBA
        	unsigned long dataSize = _width * _height * _colorchannels;

        	_data = (int*) malloc(_width * _height);
        	unsigned char* buffer = (unsigned char*) malloc(_width* _colorchannels);
        	if (_data != NULL && buffer != NULL)
        	{
        	    int count =0;
        	    unsigned char* rowptr;
                while (info.output_scanline < _height) {
                    rowptr = (unsigned char *)buffer + info.output_scanline * _width * _colorchannels;
                    jpeg_read_scanlines(&info, &rowptr, 1);
                    for(int t =0; t < _width*_colorchannels; t+=3)
                    {
                        int tmppix = (_data[count] + GetPixelARGBFromRGB(buffer[t+2], buffer[t+1], buffer[t]))/2;
                        _data[count] = tmppix;
                    }
                }
                free(buffer);
        	}
        	jpeg_finish_decompress(&info);
        	fclose(file);
        }
    }
}



