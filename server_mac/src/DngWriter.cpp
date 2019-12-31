//
// Created by troop on 23.10.2016.
//

#include <string.h>
#include <math.h>
#include "DngWriter.h"


//#define LOG_RAW_DATA

//shift 10bit tight data into readable bitorder
#define RAW_10BIT_TIGHT_SHIFT 0
//shift 10bit loose data into readable bitorder
#define RAW_10BIT_LOOSE_SHIFT 1
//drops the 6 first bit from pure 16bit data(mtk soc, Camera2 RAW_SENSOR)
#define RAW_16BIT_TO_10BIT 2
//convert and shift 10bit tight data into 16bit pure
#define RAW_10BIT_TO_16BIT 3
//shift 12bit data into readable bitorder
#define RAW_12BIT_SHIFT 4
//write 16bit data
#define RAW_16BIT 6

#define RAW_16BIT_TO_12BIT 5

#ifdef LOG_RAW_DATA
const char *bit_rep[16] = {
        [ 0] = "0000", [ 1] = "0001", [ 2] = "0010", [ 3] = "0011",
        [ 4] = "0100", [ 5] = "0101", [ 6] = "0110", [ 7] = "0111",
        [ 8] = "1000", [ 9] = "1001", [10] = "1010", [11] = "1011",
        [12] = "1100", [13] = "1101", [14] = "1110", [15] = "1111",
};
#endif


TIFF* DngWriter::openfTIFF(char *fileSavePath)
{
    TIFF *tif;
    if (!(tif = TIFFOpen (fileSavePath, "w")))
    {
        printf("openfTIFF:error while creating outputfile\n");
    }
    return tif;
}

TIFF* DngWriter::openfTIFFFD(char *fileSavePath, int fd) {
    TIFF *tif;

    printf("FD: %d", fd);
    if (!(tif = TIFFFdOpen (fd,fileSavePath, "w")))
    {
		printf("openfTIFFFD:error while creating outputfile\n");
    }
    return tif;
}

void DngWriter::writeIfd0(TIFF *tif) {
    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
	printf("subfiletype\n");
    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, dngProfile->rawwidht) != 0);
	printf("width\n");
    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, dngProfile->rawheight) != 0);
	printf("height\n");
    if(dngProfile->rawType == RAW_10BIT_LOOSE_SHIFT 
		|| dngProfile->rawType == RAW_10BIT_TO_16BIT
		|| dngProfile->rawType == RAW_16BIT)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
    else if (dngProfile->rawType == RAW_12BIT_SHIFT || dngProfile->rawType == RAW_16BIT_TO_12BIT)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 12) != 0);
    else
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 10) != 0);
	printf("bitspersample\n");
    assert(TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA) != 0);
	printf("PhotometricCFA\n");
    assert(TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE) != 0);
	printf("Compression\n");
    TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
	printf("sampelsperpixel\n");
	if (_make != NULL)
	{
		TIFFSetField(tif, TIFFTAG_MAKE, _make);
		printf("make\n");
	}
	if (_model != NULL)
	{
		TIFFSetField(tif, TIFFTAG_MODEL, _model);
		printf("model\n");
	}
   
	if (exifInfo != NULL)
	{
		try
		{
			if (0 == strcmp(exifInfo->_orientation, "0"))
				TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_TOPLEFT);
			if (0 == strcmp(exifInfo->_orientation, "90"))
				TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_RIGHTTOP);
			if (0 == strcmp(exifInfo->_orientation, "180"))
				TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_BOTRIGHT);
			if (0 == strcmp(exifInfo->_orientation, "270"))
				TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_LEFTBOT);
			printf("orientation\n");
		}
		catch (...)
		{
			printf("Caught NULL NOT SET Orientation\n");
		}
	}
	else
		TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_TOPLEFT);
    
    assert(TIFFSetField(tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG) != 0);
	printf("planarconfig\n");
    TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreeDcam DNG Writter 2017\n");
    if(_dateTime != NULL)
        TIFFSetField(tif,TIFFTAG_DATETIME, _dateTime);
	printf("software\n");
    TIFFSetField(tif, TIFFTAG_EP_STANDARD_ID, "\001\000\0\0");
    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\004\0\0");
    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
	printf("dngversion\n");
	if (_model != NULL)
	{
		TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, _model);
		printf("CameraModel\n");
	}
  
	if (exifInfo != NULL)
	{
		TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, exifInfo->_imagedescription);
		printf("imagedescription\n");
	}
    
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9,customMatrix->colorMatrix1);
	printf("colormatrix1\n");
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, customMatrix->neutralColorMatrix);
	printf("neutralMatrix\n");
    //STANDARD A = FIIRST 17
    //D65 21 Second According to DNG SPEC 1.4 this is the correct order
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 21);
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 17);
	printf("colormatrix2\n");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, customMatrix->colorMatrix2);
	printf("fowardMatrix1\n");
    if(customMatrix->fowardMatrix1 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  customMatrix->fowardMatrix1);
	printf("fowardMatrix2\n");
    if(customMatrix->fowardMatrix2 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  customMatrix->fowardMatrix2);
	printf("reductionMatrix1\n");
    if(customMatrix->reductionMatrix1 != NULL)
        TIFFSetField(tif, TIFFTAG_CAMERACALIBRATION1, 9,  customMatrix->reductionMatrix1);
	printf("reductionMatrix2\n");
    if(customMatrix->reductionMatrix2 != NULL)
        TIFFSetField(tif, TIFFTAG_CAMERACALIBRATION2, 9,  customMatrix->reductionMatrix2);

	printf("noiseMatrix\n");
    if(customMatrix->noiseMatrix != NULL)
        TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  customMatrix->noiseMatrix);
	printf("tonecurve\n");
    if(tonecurve != NULL)
    {
        TIFFSetField(tif,TIFFTAG_PROFILETONECURVE, tonecurvesize,tonecurve);
    }
	printf("huesatmapdims\n");
    if(huesatmapdims != NULL)
    {
        TIFFSetField(tif, TIFFTAG_PROFILEHUESATMAPDIMS, 3, huesatmapdims);
    }
	printf("huesatmapdata1\n");
    if(huesatmapdata1 != NULL)
    {
        TIFFSetField(tif,TIFFTAG_PROFILEHUESATMAPDATA1, huesatmapdata1_size,huesatmapdata1);
    }
	printf("huesatmapdata2\n");
    if(huesatmapdata2 != NULL)
    {
        TIFFSetField(tif,TIFFTAG_PROFILEHUESATMAPDATA2, huesatmapdata2_size,huesatmapdata2);
    }
    if(baselineExposure != NULL)
        TIFFSetField(tif,TIFFTAG_BASELINEEXPOSURE, baselineExposure);
    if(baselineExposureOffset != NULL)
    {
        TIFFSetField(tif,TIFFTAG_BASELINEEXPOSUREOFFSET, baselineExposureOffset);
    }

}

/*
void DngWriter::makeGPS_IFD(TIFF *tif) {
	printf("GPS IFD DATA\n");
    if (TIFFCreateGPSDirectory(tif) != 0)
    {
		printf("TIFFCreateGPSDirectory() failed\n" );
    }

    if (!TIFFSetField( tif, GPSTAG_GPSVersionID, "\002\003\0\0"))
    {
		printf("Can't write GPSVersionID\n" );
    }
	printf("Wrote GPSVersionID\n" );

    const char* longitudeRef = "E";
    if (gpsInfo->Longitude[0] < 0) {
        longitudeRef = "W";
        gpsInfo->Longitude[0] = fabsf(gpsInfo->Longitude[0]);
    }
    if (!TIFFSetField( tif, GPSTAG_GPSLongitudeRef, longitudeRef))
    {
		printf("Can't write LongitudeRef\n" );
    }
	printf("LONG REF Written %c\n", longitudeRef);

    if (!TIFFSetField(tif, GPSTAG_GPSLongitude, gpsInfo->Longitude))
    {
		printf("Can't write Longitude\n" );
    }
	printf("Longitude Written\n");
    const char* latitudeRef = "N";
    if (gpsInfo->Latitude[0] < 0) {
        latitudeRef = "S";
        gpsInfo->Latitude[0] = fabsf(gpsInfo->Latitude[0]);
    }
	printf("PMETH Written");
    if (!TIFFSetField( tif, GPSTAG_GPSLatitudeRef, latitudeRef)) {
		printf("Can't write LAti REf\n" );
    }
	printf("LATI REF Written %c\n", latitudeRef);

    if (!TIFFSetField( tif, GPSTAG_GPSLatitude,gpsInfo->Latitude))
    {
		printf("Can't write Latitude\n" );
    }
	printf("Latitude Written");
    if (!TIFFSetField( tif, GPSTAG_GPSAltitude,gpsInfo->Altitude))
    {
		printf("Can't write Altitude\n" );
    }
	printf("Altitude Written\n");

    if (!TIFFSetField( tif, GPSTAG_GPSTimeStamp, gpsInfo->gpsTime))
        {
		printf("Can't write gpsTime\n" );
        }
	printf("GPSTimeStamp Written\n");

    if (!TIFFSetField( tif, GPSTAG_GPSDateStamp, gpsInfo->gpsDate))
        {
		printf("Can't write gpsTime\n" );
        }
	printf("GPSTimeDate Written\n");
}
*/

void DngWriter::writeExifIfd(TIFF *tif) {
    /////////////////////////////////// EXIF IFD //////////////////////////////
    int iso[] = {exifInfo->_iso};
	printf("EXIF dir created\n");
    if (!TIFFSetField( tif, EXIFTAG_ISOSPEEDRATINGS,1, iso)) {
		printf("Can't write SPECTRALSENSITIVITY" );
    }
	printf("iso\n");
    if (!TIFFSetField( tif, EXIFTAG_FLASH, exifInfo->_flash)) {
		printf("Can't write Flas\n" );
    }
	printf("flash\n");
    if (!TIFFSetField( tif, EXIFTAG_APERTUREVALUE, exifInfo->_fnumber)) {
		printf("Can't write Aper\n" );
    }
	printf("aperture\n");

    if (!TIFFSetField( tif, EXIFTAG_EXPOSURETIME,exifInfo->_exposure)) {
		printf("Can't write SPECTRALSENSITIVITY\n" );
    }
	printf("exposure\n");


    if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, exifInfo->_focallength)) {
		printf("Can't write Focal\n" );
    }
	printf("focal\n");

    if (!TIFFSetField( tif, EXIFTAG_FNUMBER, exifInfo->_fnumber)) {
		printf("Can't write FNum\n" );
    }

    if(!TIFFSetField(tif,EXIFTAG_EXPOSUREINDEX, exifInfo->_exposureIndex))
		printf("Cant write expoindex\n");
	printf("fnumber\n");
}

//process mipi10bit to 16bit 10bit values stored
void DngWriter::processTight(TIFF *tif) {
	printf("IN SXXXXl0");
    int i, j, row, col, b;
    unsigned char *buffer, *dp;
	int wid = dngProfile->rawwidht;
    unsigned short * pixel = new unsigned short[wid]; // array holds 16 bits per pixel

	printf("buffer set\n");
    j=0;
    if(dngProfile->rowSize == 0)
        dngProfile->rowSize =  -(-5 * dngProfile->rawwidht >> 5) << 3;
    buffer =(unsigned char *)malloc(dngProfile->rowSize);
    memset( buffer, 0, dngProfile->rowSize);
    if (buffer == NULL)
    {
		printf("allocating buffer failed try again\n");
        buffer =(unsigned char *)malloc(dngProfile->rowSize);
    }
	printf("rowsize:%i", dngProfile->rowSize);

    for (row=0; row < dngProfile->rawheight; row ++)
    {
        i = 0;
        for(b = row * dngProfile->rowSize; b < row * dngProfile->rowSize + dngProfile->rowSize; b++)
            buffer[i++] = bayerBytes[b];
        for (dp=buffer, col = 0; col < dngProfile->rawwidht; dp+=5, col+= 4)
        {
            for(int i = 0; i< 4; i++)
            {
                pixel[col+i] = (dp[i] <<2) | (dp[4] >> (i << 1) & 3);
            }
        }

        if (TIFFWriteScanline(tif, pixel, row, 0) != 1) {
			printf("Error writing TIFF scanline.\n");
        }
    }
	printf("Write done\n");
    if(buffer != NULL)
    {
		printf("Free Buffer\n");
        free(buffer);
        buffer = NULL;
		printf("Freed Buffer\n");
    }
	printf("Mem Released\n");
}

//shift 10bit mipi into 10bit readable raw data
void DngWriter::process10tight(TIFF *tif) {
    unsigned char *ar = bayerBytes;
    int bytesToSkip = 0;
    int realrowsize;
    int shouldberowsize;
    unsigned char* out;
	printf("writer-RowSize: %d  rawheight:%d ,rawwidht: %d\n", rawSize, dngProfile->rawheight,
         dngProfile->rawwidht);

    realrowsize = -(-5 * dngProfile->rawwidht >> 5) << 3;
    shouldberowsize = realrowsize;
    if (realrowsize % 5 > 0) {
        shouldberowsize = dngProfile->rawwidht * 10 / 8;
        bytesToSkip = realrowsize - shouldberowsize;
    }
	printf("realrow: %i shoudlbe: %i\n", realrowsize, shouldberowsize);
	printf("width: %i height: %i\n", dngProfile->rawwidht, dngProfile->rawheight);
	printf("bytesToSkip: %i\n", bytesToSkip);

    int row = shouldberowsize;
    out = new unsigned char[shouldberowsize*dngProfile->rawheight];
    if(out == NULL)
    {
		printf("failed to set buffer");
        return;
    }

    int m = 0;
    for(int i =0; i< rawSize; i+=5)
    {
        if(i == row)
        {
            row += shouldberowsize + bytesToSkip;
            i+=bytesToSkip;
        }

        out[m++] = (ar[i]); // 00110001
        out[m++] =  (ar[i+4] & 0b00000011 ) <<6 | (ar[i+1] & 0b11111100)>>2; // 01 001100
        out[m++] = (ar[i+1]& 0b00000011 )<< 6 | (ar[i+4] & 0b00001100 ) <<2 | (ar[i +2] & 0b11110000 )>> 4;// 10 01 0011
        out[m++] = (ar[i+2] & 0b00001111 ) << 4 | (ar[i+4] & 0b00110000 )>> 2| (ar[i+3]& 0b11000000)>>6; // 0011 11 00
        out[m++] = (ar[i+3]& 0b00111111)<<2 | (ar[i+4]& 0b11000000)>>6;//110100 00
    }
    TIFFWriteRawStrip(tif, 0, out, dngProfile->rawheight*shouldberowsize);
	printf("Finalizng DNG\n");
    delete[] out;
}

void DngWriter::process12tight(TIFF *tif) {
    unsigned char* ar = bayerBytes;
    int bytesToSkip = 0;
	printf("writer-RowSize: %d  rawheight:%d ,rawwidht: %d\n",  rawSize,dngProfile->rawheight, dngProfile->rawwidht);
    int realrowsize = rawSize/dngProfile->rawheight;
    int shouldberowsize = dngProfile->rawwidht*12/8;
	printf("realrow: %i shoudlbe: %i\n", realrowsize, shouldberowsize);
    if (realrowsize != shouldberowsize)
        bytesToSkip = realrowsize - shouldberowsize;
	printf("bytesToSkip: %i\n", bytesToSkip);
    int row = shouldberowsize;
    unsigned char* out = new unsigned char[shouldberowsize*dngProfile->rawheight];
    int m = 0;
    for(int i =0; i< rawSize; i+=3)
    {
        if(i == row)
        {
            row += shouldberowsize +bytesToSkip;
            i+=bytesToSkip;
        }
        out[m++] = (ar[i]); // 00110001
        out[m++] = (ar[i+2] & 0b11110000 ) <<4 | (ar[i+1] & 0b11110000)>>4; // 01 001100
        out[m++] = (ar[i+1]& 0b00001111 )<< 4 | (ar[i+2] & 0b00001111 ) >>4 ;// 10 01 0011
    }
    TIFFWriteRawStrip(tif, 0, out, dngProfile->rawheight*shouldberowsize);
	printf("Finalizng DNG\n");
    delete[] out;
    out = NULL;
}

void DngWriter::processLoose(TIFF *tif) {
    int i, row, col, b;
    unsigned char *buffer, *dp;
    unsigned short * pixel = new unsigned short[dngProfile->rawwidht]; // array holds 16 bits per pixel

    uint64 colorchannel;

    dngProfile->rowSize= (dngProfile->rawwidht+5)/6 << 3;
    buffer =(unsigned char *)malloc(dngProfile->rowSize);
    memset( buffer, 0, dngProfile->rowSize);
    if (buffer == NULL)
    {
		printf("allocating buffer failed try again\n");
        buffer =(unsigned char *)malloc(dngProfile->rowSize);
    }
    for (row=0; row < dngProfile->rawheight; row ++)
    {
        i = 0;
        for(b = row * dngProfile->rowSize; b < (row * dngProfile->rowSize) + dngProfile->rowSize; b++)
            buffer[i++] = bayerBytes[b];
        /*
         * get 5 bytes from buffer and move first 4bytes to 16bit
         * split the 5th byte and add the value to the first 4 bytes
         * */
        for (dp=buffer, col = 0; col < dngProfile->rawwidht; dp+=8, col+= 6)
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
			printf("Error writing TIFF scanline.\n");
        }
    }
	printf("Free Memory\n");
    if(buffer != NULL)
    {
		printf("Free Buffer\n");
        free(buffer);
        buffer = NULL;
        printf("Freed Buffer\n");
    }

	printf("Mem Released");
}

void DngWriter::processSXXX16(TIFF *tif) {
    int j, row, col;
	unsigned short * pixel = new unsigned short[dngProfile->rawwidht];
    unsigned short low, high;
    j=0;
    for (row=0; row < dngProfile->rawheight; row ++)
    {
        for (col = 0; col < dngProfile->rawwidht; col+=4)
        { // iterate over pixel columns
            for (int k = 0; k < 4; ++k)
            {
                low = bayerBytes[j++];
                high =   bayerBytes[j++];
                pixel[col+k] =  high << 8 |low;
                if(col < 4 && row < 4)
					printf("Pixel : %i, high: %i low: %i \n", pixel[col+k], high, low);
            }
        }
        if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
			printf("Error writing TIFF scanline.\n");
        }
    }
	printf("Finalizng DNG\n");
	printf("Free Memory\n");
    free(pixel);
}

void DngWriter::process16to10(TIFF *tif) {
    long j;
    int rowsizeInBytes= dngProfile->rawwidht*10/8;
    long finalsize = rowsizeInBytes * dngProfile->rawheight;
    unsigned char* byts= bayerBytes;
    unsigned char* pixel = new unsigned char[finalsize];
    unsigned char B_ar[2];
    unsigned char G1_ar[2];
    unsigned char G2_ar[2];
    unsigned char R_ar[2];
    j=0;
    for (long i = 0; i < finalsize; i +=5)
    {

        B_ar[0] = byts[j];
        B_ar[1] = byts[j+1];
#ifdef LOG_RAW_DATA
        LOGD("P:%i B0:%s%s,B1:%s%s", i, bit_rep[B_ar[0] >> 4], bit_rep[B_ar[0] & 0x0F], bit_rep[B_ar[1] >> 4], bit_rep[B_ar[1] & 0x0F]);
#endif

        G1_ar[0] = byts[j+2];
        G1_ar[1] = byts[j+3];
#ifdef LOG_RAW_DATA
        LOGD("P:%i G10:%s%s,G11:%s%s", i, bit_rep[G1_ar[0] >> 4], bit_rep[G1_ar[0] & 0x0F], bit_rep[G1_ar[1] >> 4], bit_rep[G1_ar[1] & 0x0F]);
#endif
        G2_ar[0] = byts[j+4];
        G2_ar[1] = byts[j+5];
#ifdef LOG_RAW_DATA
        LOGD("P:%i G20:%s%s,G21:%s%s", i, bit_rep[G2_ar[0] >> 4], bit_rep[G2_ar[0] & 0x0F], bit_rep[G2_ar[1] >> 4], bit_rep[G2_ar[1] & 0x0F]);
#endif

        R_ar[0] = byts[j+6];
        R_ar[1] = byts[j+7];
#ifdef LOG_RAW_DATA
        LOGD("P:%i R0:%s%s,R1:%s%s", i, bit_rep[R_ar[0] >> 4], bit_rep[R_ar[0] & 0x0F], bit_rep[R_ar[1] >> 4], bit_rep[R_ar[1] & 0x0F]);
#endif
        j+=8;

        //00000011 1111111      H11 111111
        //00000011 1111111      11 H11 1111
        //00000011 1111111      1111 H11 11
        //00000011 1111111      111111 H11
        //00000011 1111111      11111111
        pixel[i] = (B_ar[1] & 0b00000011) << 6 | (B_ar[0] & 0b11111100) >> 2;//H11 111111
        pixel[i+1] =  (B_ar[0] & 0b00000011 ) << 6 | (G1_ar[1] & 0b00000011) << 4 | (G1_ar[0] & 0b11110000) >> 4;//11 H22 2222
        pixel[i+2] =  (G1_ar[0] & 0b00001111 ) << 4 | (G2_ar[1] & 0b00000011) << 2 | (G2_ar[0] & 0b11000000) >> 6; //2222 H33 33
        pixel[i+3] = (G2_ar[0] & 0b00111111 ) << 2 | (R_ar[1] & 0b00000011);//333333 H44
        pixel[i+4] = R_ar[0]; //44444444
    }
    TIFFWriteRawStrip(tif, 0, pixel, dngProfile->rawheight*rowsizeInBytes);
	printf("Finalizng DNG\n");

	printf("Free Memory\n");
    delete[] pixel;
}

void DngWriter::process16to12(TIFF *tif) {
    long j;
    int rowsizeInBytes= dngProfile->rawwidht*12/8;
    long finalsize = rowsizeInBytes * dngProfile->rawheight;
    unsigned char* byts= bayerBytes;
    unsigned char* pixel = new unsigned char[finalsize];
    unsigned char B_ar[3];
    unsigned char G1_ar[3];
    unsigned char G2_ar[3];
    unsigned char R_ar[3];
    j=0;
    for (long i = 0; i < finalsize; i +=6)
    {

        B_ar[0] = byts[j];
        B_ar[1] = byts[j+1];
#ifdef LOG_RAW_DATA
		printf("P:%i B0:%s%s,B1:%s%s", i, bit_rep[B_ar[0] >> 4], bit_rep[B_ar[0] & 0x0F], bit_rep[B_ar[1] >> 4], bit_rep[B_ar[1] & 0x0F]);
#endif

        G1_ar[0] = byts[j+2];
        G1_ar[1] = byts[j+3];
#ifdef LOG_RAW_DATA
		printf("P:%i G10:%s%s,G11:%s%s", i, bit_rep[G1_ar[0] >> 4], bit_rep[G1_ar[0] & 0x0F], bit_rep[G1_ar[1] >> 4], bit_rep[G1_ar[1] & 0x0F]);
#endif
        G2_ar[0] = byts[j+4];
        G2_ar[1] = byts[j+5];
#ifdef LOG_RAW_DATA
		printf("P:%i G20:%s%s,G21:%s%s", i, bit_rep[G2_ar[0] >> 4], bit_rep[G2_ar[0] & 0x0F], bit_rep[G2_ar[1] >> 4], bit_rep[G2_ar[1] & 0x0F]);
#endif

        R_ar[0] = byts[j+6];
        R_ar[1] = byts[j+7];
#ifdef LOG_RAW_DATA
		printf("P:%i R0:%s%s,R1:%s%s", i, bit_rep[R_ar[0] >> 4], bit_rep[R_ar[0] & 0x0F], bit_rep[R_ar[1] >> 4], bit_rep[R_ar[1] & 0x0F]);
#endif
        j+=8;

        //00001111 1111111      H1111 1111
        //00001111 1111111      1111 H1111
        //00001111 1111111      1111 1111
        //00001111 1111111      H1111 1111
        //00001111 1111111      1111 H1111
        //00001111 1111111      1111 1111

        pixel[i] = (B_ar[1] & 0b00001111) << 4 | (B_ar[0] & 0b11110000) >> 4;//B1111 1111
        pixel[i+1] =  (B_ar[0] & 0b00001111 ) << 4 | (G1_ar[1] & 0b00001111);//1111 G2222
        pixel[i+2] =  G1_ar[0];//2222 2222
        pixel[i+3] = (G2_ar[1] & 0b00001111 ) << 4 | (G2_ar[0] & 0b11110000)>>4;//3333 3333
        pixel[i+4] = (G2_ar[0] & 0b00001111 ) << 4 | (R_ar[1] &  0b00001111); //3333 4444
        pixel[i+5] = R_ar[0]; //4444 4444
    }
    TIFFWriteRawStrip(tif, 0, pixel, dngProfile->rawheight*rowsizeInBytes);
	printf("Finalizng DNG\n");

	printf("Free Memory\n");
    delete[] pixel;
}

void DngWriter::process16to16(TIFF *tif)
{
	TIFFWriteRawStrip(tif, 0, bayerBytes, dngProfile->rawwidht*dngProfile->rawheight*2);
}

void DngWriter::writeRawStuff(TIFF *tif) {
    char cfa[4] = {0,0,0,0};
    if(0 == strcmp(dngProfile->bayerformat,"bggr")){
        cfa[0] = 2;cfa[1] = 1;cfa[2] = 1;cfa[3] = 0;}
        //TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
    if(0 == strcmp(dngProfile->bayerformat , "grbg")){
        cfa[0] = 1;cfa[1] = 0;cfa[2] = 2;cfa[3] = 1;}//TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
    if(0 == strcmp(dngProfile->bayerformat , "rggb")){
        cfa[0] = 0;cfa[1] = 1;cfa[2] = 1;cfa[3] = 2;}//TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
    if(0 == strcmp(dngProfile->bayerformat , "gbrg")){
        cfa[0] = 1;cfa[1] = 2;cfa[2] = 0;cfa[3] = 1;}//TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
    if(0 == strcmp(dngProfile->bayerformat , "rgbw")){
        cfa[0] = 0;cfa[1] = 1;cfa[2] = 2;cfa[3] = 6;}//TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\002\006");

	printf("cfa pattern\n", cfa[0],cfa[1],cfa[2],cfa[3]);

    TIFFSetField (tif, TIFFTAG_CFAPATTERN, cfa);

    printf("whitelvl\n");
    TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &dngProfile->whitelevel);
    printf("cfa repeat dim\n");
    short CFARepeatPatternDim[] = { 2,2 };
    TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);

    printf("blacklvl\n");
    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, dngProfile->blacklevel);
	printf("wrote blacklevel\n");
    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
    //**********************************************************************************

	printf("Set OP or not\n");
    if(opcode2Size >0)
    {
		printf("Set OP2\n");
        TIFFSetField(tif, TIFFTAG_OPC2, opcode2Size, opcode2);
    }
    if(opcode3Size >0)
    {
		printf("Set OP3\n");
        TIFFSetField(tif, TIFFTAG_OPC3, opcode3Size, opcode3);
    }
	if (dngProfile->rawType == RAW_10BIT_TIGHT_SHIFT)
	{
		printf("Processing tight RAW data...\n");
		process10tight(tif);
		printf("Done tight RAW data...");
	}
	else if (dngProfile->rawType == RAW_10BIT_LOOSE_SHIFT)
	{
		printf("Processing loose RAW data...\n");
		processLoose(tif);
		printf("Done loose RAW data...\n");
	}
	else if (dngProfile->rawType == RAW_16BIT_TO_10BIT) {
		printf("process16to10(tif);\n");
		process16to10(tif);
	}
	else if (dngProfile->rawType == RAW_10BIT_TO_16BIT) {
		printf("processTight(tif);\n");
		processTight(tif);
	}
	else if (dngProfile->rawType == RAW_12BIT_SHIFT) {
		printf("process12tight\n");
		process12tight(tif);
	}
	else if (dngProfile->rawType == RAW_16BIT_TO_12BIT) {
		printf("process16to12(tif);\n");
		process16to12(tif);
	}
	else if (dngProfile->rawType == RAW_16BIT)
		processSXXX16(tif);
    else
		printf("rawType is not implented");
}

void DngWriter::WriteDNG() {
    uint64 gps_offset = 0;
	printf("init ext tags\n");
    _XTIFFInitialize();
	printf("init ext tags done\n");
    TIFF *tif;
	printf("has file description: %b\n", hasFileDes);
    if(hasFileDes == true)
    {
        tif = openfTIFFFD("", fileDes);
    }
    else
        tif = openfTIFF(fileSavePath);

	printf("writeIfd0\n");
    writeIfd0(tif);
    if(exifInfo != NULL)
        writeExifIfd(tif);
    if(gpsInfo != NULL)
    {   //allocate empty GPSIFD tag
        TIFFSetField (tif, TIFFTAG_GPSIFD, gps_offset);        
    }
    //save directory
	printf("TIFFCheckpointDirectory\n");
    TIFFCheckpointDirectory(tif);
    
	printf("set exif\n");

    if(gpsInfo != NULL)
    {
        //makeGPS_IFD(tif);
        TIFFWriteCustomDirectory(tif, &gps_offset);
        // set GPSIFD tag
        TIFFSetDirectory(tif, 0);
        TIFFSetField (tif, TIFFTAG_GPSIFD, gps_offset);
        TIFFCheckpointDirectory(tif);
        TIFFSetDirectory(tif, 0);
    }
    
    writeRawStuff(tif);

    TIFFWriteDirectory(tif);
    TIFFClose(tif);

	printf("delete Opcode2\n");
    /*if(opcode2 != NULL)
    {
        delete[] opcode2;
        opcode2Size = NULL;
        opcode2 = NULL;
    }
	printf("delete Opcode3");
    if(opcode3 != NULL)
    {
        delete[] opcode3;
        opcode2Size = NULL;
        opcode3 = NULL;
    }
	printf("delete bayerbytes");
    if (bayerBytes != NULL){
        delete [] bayerBytes;
        rawSize = NULL;
        bayerBytes = NULL;
    }
	printf("delete filesavepath");
    if(fileSavePath != NULL)
    {
        delete[] fileSavePath;
        fileSavePath = NULL;
    }
	printf("delete exif");
    if(exifInfo != NULL)
        exifInfo = NULL;
	printf("delete dngprofile");
    if(dngProfile != NULL)
        dngProfile = NULL;
	printf("delete customMatrix");
    if(customMatrix != NULL)
        customMatrix = NULL;
    fileDes = NULL;
    thumbheight = NULL;
    thumwidth = NULL;
	printf("DONE");*/
}