//
// Created by troop on 23.10.2016.
//

#include <string.h>
#include <math.h>
#include "DngWriter.h"
#include "lj92.h"


//#define LOG_RAW_DATA

#define  LOG_TAG    "freedcam.DngWriter"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

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

#define RAW_16BIT_TO_12BIT 5
#define RAW_16BIT 6
#define QUADBAYER_16BIT 7
#define RAW16_TO_LOSSLESS 8

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
        LOGD("openfTIFF:error while creating outputfile");
    }
    return tif;
}

TIFF* DngWriter::openfTIFFFD(char *fileSavePath, int fd) {
    TIFF *tif;

    LOGD("FD: %d", fd);
    if (!(tif = TIFFFdOpen (fd,fileSavePath, "w")))
    {
        LOGD("openfTIFFFD:error while creating outputfile");
    }
    return tif;
}

void DngWriter::writeIfd0(TIFF *tif) {
    int width,height,bits_per_sample, photometric;

    if (crop_width == 0 && crop_height == 0)
    {
        width = dngProfile->rawwidht;
        height = dngProfile->rawheight;
    }
    else
    {
        width = crop_width;
        height = crop_height;
    }

    if(dngProfile->rawType == RAW_10BIT_LOOSE_SHIFT
       || dngProfile->rawType == RAW_10BIT_TO_16BIT
       || dngProfile->rawType == RAW_16BIT
       || dngProfile->rawType == QUADBAYER_16BIT && dngProfile->rawType == RAW16_TO_LOSSLESS)
        bits_per_sample = 16;
    else if (dngProfile->rawType == RAW_12BIT_SHIFT || dngProfile->rawType == RAW_16BIT_TO_12BIT)
        bits_per_sample = 12;
    else
        bits_per_sample = 10;

    if(dngProfile->rawType == RAW16_TO_LOSSLESS)
        compression = COMPRESSION_JPEG;

    if (compression == COMPRESSION_NONE || compression == COMPRESSION_JPEG)
        photometric = PHOTOMETRIC_CFA;
    else if (compression == LOSY_JPEG)
        photometric = LINEAR_RAW;

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

    LOGD("cfa pattern %c%c&c&c", cfa[0],cfa[1],cfa[2],cfa[3]);

    short CFARepeatPatternDim[] = { 2,2 };
    float margin = 8;
    float scale[] = {1,1};
    float  defaultCropOrigin[] = {margin, margin};
    float  defaultCropSize[2];
    if (crop_width == 0 && crop_height == 0) {
        defaultCropSize[0] = dngProfile->rawwidht - defaultCropOrigin[0] - margin;
        defaultCropSize[1] = dngProfile->rawheight - defaultCropOrigin[1] - margin;
    }
    else
    {
        defaultCropSize[0] = crop_width - defaultCropOrigin[0] - margin;
        defaultCropSize[1] = crop_height - defaultCropOrigin[1] - margin;
    }

    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\004\0\0");
    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, width);
    TIFFSetField(tif, TIFFTAG_IMAGELENGTH, height);
    LOGD("width x height:  %i x i%", width,height);
    TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, bits_per_sample);
    LOGD("bitspersample %i", bits_per_sample);
    TIFFSetField(tif, TIFFTAG_COMPRESSION, compression);
    LOGD("Compression i%", compression);
    TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, photometric);
    LOGD("PhotometricCFA i%", photometric);

    TIFFSetField(tif, TIFFTAG_FILLORDER, FILLORDER_MSB2LSB);
    if(exifInfo != NULL)
        TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, exifInfo->_imagedescription);
    TIFFSetField(tif, TIFFTAG_MAKE, _make);
    LOGD("make %s", _make);
    TIFFSetField(tif, TIFFTAG_MODEL, _model);
    LOGD("model %s",_model);
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
            LOGD("orientation %s",exifInfo->_orientation);
        }
        catch (...)
        {
            LOGD("failed to set orientation");
        }
    }
    else
        TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_TOPLEFT);
    TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
    LOGD("sampelsperpixel %i" ,1);
    TIFFSetField( tif, TIFFTAG_XRESOLUTION, 7200 );
    TIFFSetField( tif, TIFFTAG_YRESOLUTION, 7200 );
    TIFFSetField(tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
    TIFFSetField(tif, TIFFTAG_RESOLUTIONUNIT, RESUNIT_INCH);
    TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreeDcam DNG Writer 2022");
    if(_dateTime != NULL)
        TIFFSetField(tif,TIFFTAG_DATETIME, _dateTime);
    TIFFSetField(tif, TIFFTAG_SAMPLEFORMAT, SAMPLEFORMAT_UINT);
    //30k
    TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);
    TIFFSetField (tif, TIFFTAG_CFAPATTERN, cfa);
    TIFFSetField(tif, TIFFTAG_EP_STANDARD_ID, "\001\000\0\0");
    //50k
    if(_model != NULL){
        TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, _model);
        LOGD("CameraModel %s",_model);
    }
    TIFFSetField( tif, TIFFTAG_CFAPLANECOLOR, 3, "\00\01\02" ); // RGB
    TIFFSetField( tif, TIFFTAG_CFALAYOUT, 1 );

    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, dngProfile->blacklevel);
    LOGD("wrote blacklevel");
    TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &dngProfile->whitelevel);
    TIFFSetField(tif,TIFFTAG_DEFAULTSCALE, scale);

    LOGD("defaultCropOrigin");
    TIFFSetField(tif, TIFFTAG_DEFAULTCROPORIGIN, defaultCropOrigin);
    LOGD("defaultCropSize");
    TIFFSetField(tif, TIFFTAG_DEFAULTCROPSIZE, defaultCropSize);

    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9,customMatrix->colorMatrix1);
    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, customMatrix->colorMatrix2);

    LOGD("reductionMatrix1");
    if(customMatrix->reductionMatrix1 != NULL)
        TIFFSetField(tif, TIFFTAG_CAMERACALIBRATION1, 9,  customMatrix->reductionMatrix1);
    LOGD("reductionMatrix2");
    if(customMatrix->reductionMatrix2 != NULL)
        TIFFSetField(tif, TIFFTAG_CAMERACALIBRATION2, 9,  customMatrix->reductionMatrix2);
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, customMatrix->neutralColorMatrix);

    LOGD("baselineExposure");
    double baseS = baselineExposure;
    TIFFSetField(tif,TIFFTAG_BASELINEEXPOSURE, baseS);

    //TIFFSetField(tif,TIFFTAG_BAYERGREENSPLIT, bayergreensplit);

    //STANDARD A = FIIRST 17
    //D65 21 Second According to DNG SPEC 1.4 this is the correct order
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 21);
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 17);

    if(dngProfile->activearea != nullptr && crop_width == 0 && crop_height == 0)
    {
        TIFFSetField(tif,TIFFTAG_ACTIVEAREA, dngProfile->activearea);
    }
    else
    {
        int active_area[] = {0,0,crop_width,crop_height};
        TIFFSetField(tif,TIFFTAG_ACTIVEAREA, active_area);
    }

    LOGD("huesatmapdims");
    if(huesatmapdims != NULL)
    {
        TIFFSetField(tif, TIFFTAG_PROFILEHUESATMAPDIMS, 3, huesatmapdims);
    }
    LOGD("huesatmapdata1");
    if(huesatmapdata1 != NULL)
    {
        TIFFSetField(tif,TIFFTAG_PROFILEHUESATMAPDATA1, huesatmapdata1_size,huesatmapdata1);
    }
    LOGD("huesatmapdata2");
    if(huesatmapdata2 != NULL)
    {
        TIFFSetField(tif,TIFFTAG_PROFILEHUESATMAPDATA2, huesatmapdata2_size,huesatmapdata2);
    }
    LOGD("tonecurve");
    if(tonecurve != NULL)
    {
        TIFFSetField(tif,TIFFTAG_PROFILETONECURVE, tonecurvesize,tonecurve);
    }
    LOGD("fowardMatrix1");
    if(customMatrix->fowardMatrix1 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  customMatrix->fowardMatrix1);
    LOGD("fowardMatrix2");
    if(customMatrix->fowardMatrix2 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  customMatrix->fowardMatrix2);
    //51k
    /*if(opCode != NULL){
        if(opCode->op2Size > 0){
            LOGD("Set OP2 %i", opCode->op2Size);
            TIFFSetField(tif, TIFFTAG_OPC2, opCode->op2Size, opCode->op2);
        }else{
            LOGD("opcode2 null");
        }
        if(opCode->op3Size > 0){
            LOGD("Set OP3 %i", opCode->op3Size);
            TIFFSetField(tif, TIFFTAG_OPC3, opCode->op3Size, opCode->op3);
        }else{
            LOGD("opcode3 null");
        }
    } else{
        LOGD("opcode null");
    }*/
    LOGD("noiseMatrix");
    if(customMatrix->noiseMatrix != NULL)
        TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  customMatrix->noiseMatrix);
    LOGD("baselineExposureOffset");
    if(baselineExposureOffset != NULL)
    {
        TIFFSetField(tif,TIFFTAG_BASELINEEXPOSUREOFFSET, baselineExposureOffset);
    }
}

void DngWriter::makeGPS_IFD(TIFF *tif) {
    LOGD("GPS IFD DATA");
    if (TIFFCreateGPSDirectory(tif) != 0)
    {
        LOGD("TIFFCreateGPSDirectory() failed" );
    }
    if (!TIFFSetField( tif, GPSTAG_GPSVersionID, "\002\003\0\0"))
    {
        LOGD("Can't write GPSVersionID" );
    }
    LOGD("Wrote GPSVersionID" );

    const char* longitudeRef = "E";
    if (gpsInfo->Longitude[0] < 0) {
        longitudeRef = "W";
        gpsInfo->Longitude[0] = fabsf(gpsInfo->Longitude[0]);
    }
    if (!TIFFSetField( tif, GPSTAG_GPSLongitudeRef, longitudeRef))
    {
        LOGD("Can't write LongitudeRef" );
    }
    LOGD("LONG REF Written %c", longitudeRef);

    if (!TIFFSetField(tif, GPSTAG_GPSLongitude, gpsInfo->Longitude))
    {
        LOGD("Can't write Longitude" );
    }
    LOGD("Longitude Written");
    const char* latitudeRef = "N";
    if (gpsInfo->Latitude[0] < 0) {
        latitudeRef = "S";
        gpsInfo->Latitude[0] = fabsf(gpsInfo->Latitude[0]);
    }
    LOGD("PMETH Written");
    if (!TIFFSetField( tif, GPSTAG_GPSLatitudeRef, latitudeRef)) {
        LOGD("Can't write LAti REf" );
    }
    LOGD("LATI REF Written %c", latitudeRef);

    if (!TIFFSetField( tif, GPSTAG_GPSLatitude,gpsInfo->Latitude))
    {
        LOGD("Can't write Latitude" );
    }
    LOGD("Latitude Written");
    if (!TIFFSetField( tif, GPSTAG_GPSAltitude,gpsInfo->Altitude))
    {
        LOGD("Can't write Altitude" );
    }
    LOGD("Altitude Written");

    if (!TIFFSetField( tif, GPSTAG_GPSTimeStamp, gpsInfo->gpsTime))
        {
            LOGD("Can't write gpsTime" );
        }
    LOGD("GPSTimeStamp Written");

    if (!TIFFSetField( tif, GPSTAG_GPSDateStamp, gpsInfo->gpsDate))
        {
            LOGD("Can't write gpsTime" );
        }
    LOGD("GPSTimeDate Written");
}

void DngWriter::writeExifIfd(TIFF *tif) {

    /////////////////////////////////// EXIF IFD //////////////////////////////
    short iso[] = {exifInfo->_iso};
    LOGD("EXIF dir created");
    if (!TIFFSetField( tif, EXIFTAG_ISOSPEEDRATINGS,1, iso)) {
        LOGD("Can't write SPECTRALSENSITIVITY" );
    }
    LOGD("iso");
    if (!TIFFSetField( tif, EXIFTAG_FLASH, exifInfo->_flash)) {
        LOGD("Can't write Flas" );
    }
    LOGD("flash");
    if (!TIFFSetField( tif, EXIFTAG_APERTUREVALUE, exifInfo->_fnumber)) {
        LOGD("Can't write Aper" );
    }
    LOGD("aperture");

    if (!TIFFSetField( tif, EXIFTAG_EXPOSURETIME,exifInfo->_exposure)) {
        LOGD("Can't write SPECTRALSENSITIVITY" );
    }
    LOGD("exposure");


    if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, exifInfo->_focallength)) {
        LOGD("Can't write Focal" );
    }
    LOGD("focal");

    if (!TIFFSetField( tif, EXIFTAG_FNUMBER, exifInfo->_fnumber)) {
        LOGD("Can't write FNum" );
    }

    if(!TIFFSetField(tif,EXIFTAG_EXPOSUREINDEX, exifInfo->_exposureIndex))
        LOGD("Cant write expoindex");
    LOGD("fnumber");
}

//process mipi10bit to 16bit 10bit values stored
void DngWriter::processTight(TIFF *tif) {
    LOGD("IN SXXXXl0");
    int i, j, row, col, b;
    unsigned char *buffer, *dp;
    unsigned short pixel[dngProfile->rawwidht]; // array holds 16 bits per pixel

    LOGD("buffer set");
    j=0;
    if(dngProfile->rowSize == 0)
        dngProfile->rowSize =  -(-5 * dngProfile->rawwidht >> 5) << 3;
    buffer =(unsigned char *)malloc(dngProfile->rowSize);
    memset( buffer, 0, dngProfile->rowSize);
    if (buffer == NULL)
    {
        LOGD("allocating buffer failed try again");
        buffer =(unsigned char *)malloc(dngProfile->rowSize);
    }
    LOGD("rowsize:%i", dngProfile->rowSize);

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
            LOGD("Error writing TIFF scanline.");
        }
    }
    LOGD("Write done");
    if(buffer != NULL)
    {
        LOGD("Free Buffer");
        free(buffer);
        LOGD("Freed Buffer");
    }
    LOGD("Mem Released");
}

//shift 10bit mipi into 10bit readable raw data
void DngWriter::process10tight(TIFF *tif) {
    unsigned char *ar = bayerBytes;
    int bytesToSkip = 0;
    int realrowsize;
    int shouldberowsize;
    unsigned char* out;
    LOGD("writer-RowSize: %d  rawheight:%d ,rawwidht: %d", rawSize, dngProfile->rawheight,
         dngProfile->rawwidht);

    realrowsize = -(-5 * dngProfile->rawwidht >> 5) << 3;
    shouldberowsize = realrowsize;
    if (realrowsize % 5 > 0) {
        shouldberowsize = dngProfile->rawwidht * 10 / 8;
        bytesToSkip = realrowsize - shouldberowsize;
    }
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    LOGD("width: %i height: %i", dngProfile->rawwidht, dngProfile->rawheight);
    LOGD("bytesToSkip: %i", bytesToSkip);

    int row = shouldberowsize;
    out = new unsigned char[shouldberowsize*dngProfile->rawheight];
    if(out == NULL)
    {
        LOGD("failed to set buffer");
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
    LOGD("Finalizng DNG");
    delete[] out;
}

void DngWriter::process12tight(TIFF *tif) {
    unsigned char* ar = bayerBytes;
    int bytesToSkip = 0;
    LOGD("writer-RowSize: %d  rawheight:%d ,rawwidht: %d",  rawSize,dngProfile->rawheight, dngProfile->rawwidht);
    int realrowsize = rawSize/dngProfile->rawheight;
    int shouldberowsize = dngProfile->rawwidht*12/8;
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    if (realrowsize != shouldberowsize)
        bytesToSkip = realrowsize - shouldberowsize;
    LOGD("bytesToSkip: %i", bytesToSkip);
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
    LOGD("Finalizng DNG");
    delete[] out;
}

void DngWriter::processLoose(TIFF *tif) {
    int i, row, col, b;
    unsigned char *buffer, *dp;
    unsigned short pixel[dngProfile->rawwidht]; // array holds 16 bits per pixel

    uint64 colorchannel;

    dngProfile->rowSize= (dngProfile->rawwidht+5)/6 << 3;
    buffer =(unsigned char *)malloc(dngProfile->rowSize);
    memset( buffer, 0, dngProfile->rowSize);
    if (buffer == NULL)
    {
        LOGD("allocating buffer failed try again");
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
            LOGD("Error writing TIFF scanline.");
        }
    }
    LOGD("Free Memory processLoose");
    if(buffer != NULL)
    {
        LOGD("Free Buffer");
        free(buffer);
        LOGD("Freed Buffer");
    }

    LOGD("Mem Released");
}

void DngWriter::processSXXX16(TIFF *tif) {
    int j, row, col;
    unsigned short pixel[dngProfile->rawwidht];
    unsigned short pixel2[dngProfile->rawwidht];
    unsigned short low, high;
    j=0;
    for (row=0; row < dngProfile->rawheight; row+=2)
    {
        for (col = 0; col < dngProfile->rawwidht; col++)
        { // iterate over pixel columns
            int pos = (dngProfile->rawwidht * row + col)*2;
            low = bayerBytes[pos];
            high =   bayerBytes[pos+1];
            pixel[col] =  high << 8 |low;
            pos = (dngProfile->rawwidht * (row+1) + col)*2;
            low = bayerBytes[pos];
            high =   bayerBytes[pos+1];
            pixel2[col] =  high << 8 |low;
        }
        if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
            LOGD("Error writing TIFF scanline.");
        }
        if (TIFFWriteScanline (tif, pixel2, row+1, 0) != 1) {
            LOGD("Error writing TIFF scanline.");
        }
    }

    LOGD("Finalizng DNG");
    LOGD("Free Memory processSXXX16");
}

//taken from https://github.com/ifb/makeDNG
void DngWriter::process16ToLossless(TIFF *tiff) {
    int height = dngProfile->rawheight;
    int width = dngProfile->rawwidht;
    int halfwidth = width/2;
    TIFFSetField( tiff, TIFFTAG_TILEWIDTH, halfwidth);
    LOGD("wrote TIFFTAG_TILEWIDTH %i", halfwidth);
    TIFFSetField( tiff, TIFFTAG_TILELENGTH, height);
    LOGD("wrote TIFFTAG_TILELENGTH %i", height);
    LOGD("width %i", width);
    int ret = 0;
    uint8_t* input = bayerBytes;
    uint8_t* encoded = NULL;
    int encodedLength = 0;
    ret = lj92_encode( (uint16_t*)&input[0], halfwidth, height, 10, halfwidth, halfwidth, NULL, 0, &encoded, &encodedLength );
    TIFFWriteRawTile(tiff, 0, encoded, encodedLength );
    LOGD("endcoded tile 0: %i", encodedLength);
    free( encoded );
    ret = lj92_encode( (uint16_t*)&input[width], halfwidth, height, 10, halfwidth, halfwidth, NULL, 0, &encoded, &encodedLength );
    TIFFWriteRawTile(tiff, 1, encoded, encodedLength );
    LOGD("encoded tile 1: %i", encodedLength);
    free( encoded );
}

void DngWriter::processSXXX16crop(TIFF *tif) {
    LOGD("processSXXX16crop");
    int j, row, col, j2;
    unsigned short pixel[crop_width];
    unsigned short low, high;
    int w_diff = dngProfile->rawwidht - crop_width;
    int h_diff = dngProfile->rawheight - crop_height;
    int offset_x = w_diff/2;
    int offset_y = h_diff/2;
    LOGD("crop w: %i h: %i offset x: %i y: %i" ,crop_width,crop_height, offset_x,offset_y);
    j=0;
    j2 = 0;
    for (row=offset_y; row < dngProfile->rawheight-offset_y; row++)
    {
        j=0;
        for (col = offset_x; col < dngProfile->rawwidht - offset_x; col++)
        { // iterate over pixel columns
            int pos = (dngProfile->rawwidht * row + col)*2;
            low = bayerBytes[pos];
            high =   bayerBytes[pos+1];
            pixel[j++] =  high << 8 |low;
        }
        if (TIFFWriteScanline (tif, pixel, j2++, 0) != 1) {
            LOGD("Error writing TIFF scanline.");
        }
    }

    LOGD("Finalizng DNG");
    LOGD("Free Memory processSXXX16crop");
}

unsigned short DngWriter::getColor(int row, int col)
{
    int pos = (dngProfile->rawwidht * row + col)*2;
    unsigned short low = bayerBytes[pos];
    unsigned short high =   bayerBytes[pos+1];
    return high << 8 | low;
}

void DngWriter::quadBayer16bit(TIFF *tif) {

    int row, col;
    unsigned short pixel[dngProfile->rawwidht];
    unsigned short pixel2[dngProfile->rawwidht];
    unsigned short pixel3[dngProfile->rawwidht];
    unsigned short pixel4[dngProfile->rawwidht];
    unsigned short r1,r2,r3,r4;
    unsigned short g1,g2,g3,g4;
    unsigned short gg1,gg2,gg3,gg4;
    unsigned short b1,b2,b3,b4;

    for (row=0; row < dngProfile->rawheight; row+=4)
    {
        for (col = 0; col < dngProfile->rawwidht; col+=4)
        { // iterate over pixel columns
            r1 = getColor(row,col);
            r2 = getColor(row, col+1);
            r3 = getColor(row+1,col);
            r4 = getColor(row+1,col+1);
            g1 = getColor(row,col+2);
            g2 = getColor(row,col+3);
            g3 = getColor(row+1,col+2);
            g4 = getColor(row+1,col+3);

            gg1 = getColor(row+2,col);
            gg2 = getColor(row+2,col+1);
            gg3 = getColor(row+3,col);
            gg4 = getColor(row+3,col+1);
            b1 = getColor(row+2,col+2);
            b2 = getColor(row+2,col+3);
            b3 = getColor(row+3,col+2);
            b4 = getColor(row+3,col+3);

            pixel[col] = r1;
            pixel[col+1] = g1;
            pixel[col+2] = r2;
            pixel[col+3] = g2;
            pixel3[col] = r3;
            pixel3[col+1] = g3;
            pixel3[col+2] = r4;
            pixel3[col+3] = g4;

            pixel2[col] = gg1;
            pixel2[col+1] = b1;
            pixel2[col+2] = gg2;
            pixel2[col+3] = b2;
            pixel4[col] = gg3;
            pixel4[col+1] = b3;
            pixel4[col+2] = gg4;
            pixel4[col+3] = b4;

        }
        if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
            LOGD("Error writing TIFF scanline.");
        }
        if (TIFFWriteScanline (tif, pixel2, row+1, 0) != 1) {
            LOGD("Error writing TIFF scanline.");
        }
        if (TIFFWriteScanline (tif, pixel3, row+2, 0) != 1) {
            LOGD("Error writing TIFF scanline.");
        }
        if (TIFFWriteScanline (tif, pixel4, row+3, 0) != 1) {
            LOGD("Error writing TIFF scanline.");
        }
    }

    LOGD("Finalizng DNG");
    LOGD("Free Memory quadBayer16bit");
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
    LOGD("Finalizng DNG");

    LOGD("Free Memory process16to10");
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
    LOGD("Finalizng DNG");

    LOGD("Free Memory process16to12");
    delete[] pixel;
}


void DngWriter::writeRawStuff(TIFF *tif) {
    //**********************************************************************************

    if (compression == COMPRESSION_JPEG)
    {
        process16ToLossless(tif);
    }
    else if(dngProfile->rawType == RAW_10BIT_TIGHT_SHIFT)
    {
        LOGD("Processing tight RAW data...");
        process10tight(tif);
        LOGD("Done tight RAW data...");
    }
    else if (dngProfile->rawType == RAW_10BIT_LOOSE_SHIFT)
    {
        LOGD("Processing loose RAW data...");
        processLoose(tif);
        LOGD("Done loose RAW data...");
    }
    else if (dngProfile->rawType == RAW_16BIT_TO_10BIT) {
        LOGD("process16to10(tif);");
        process16to10(tif);
    }
    else if (dngProfile->rawType == RAW_10BIT_TO_16BIT) {
        LOGD("processTight(tif);");
        processTight(tif);
    }
    else if (dngProfile->rawType == RAW_12BIT_SHIFT) {
        LOGD("process12tight");
        process12tight(tif);
    }
    else if (dngProfile->rawType == RAW_16BIT_TO_12BIT) {
        LOGD("process16to12(tif);");
        process16to12(tif);
    }
    else if (dngProfile->rawType == RAW_16BIT)
    {
        if (crop_width == 0 && crop_height == 0)
            processSXXX16(tif);
        else
            processSXXX16crop(tif);
    }
    else if (dngProfile->rawType == QUADBAYER_16BIT)
        quadBayer16bit(tif);
    else
        LOGD("rawType is not implented");
}


void DngWriter::clear() {
    LOGD("delete Opcode2");
    opCode = NULL;
    LOGD("delete bayerbytes");
    /*if (bayerBytes != NULL){
        delete [] bayerBytes;
        rawSize = NULL;
        bayerBytes = NULL;
    }*/
    LOGD("delete filesavepath");
    if(fileSavePath != NULL)
    {
        if(!hasFileDes)
            delete[] fileSavePath;
        fileSavePath = NULL;
    }
    LOGD("delete exif");
    if(exifInfo != NULL)
        exifInfo = NULL;
    LOGD("delete dngprofile");
    if(dngProfile != NULL)
        dngProfile = NULL;
    LOGD("delete customMatrix");
    if(customMatrix != NULL)
        customMatrix = NULL;
    fileDes = NULL;
    thumbheight = NULL;
    thumwidth = NULL;
}

void DngWriter::WriteDNG() {

    LOGD("init ext tags");
    LOGD("init ext tags done");
    TIFF *tif;
    LOGD("has file description: %b", hasFileDes);
    if(hasFileDes == true)
    {
        tif = openfTIFFFD("", fileDes);
    }
    else
        tif = openfTIFF(fileSavePath);

    LOGD("writeIfd0");
    writeIfd0(tif);

    if(opCode != NULL)
    {
        if(opCode->op2Size > 0)
        {
            LOGD("Set OP2 %i", opCode->op2Size);
            TIFFSetField(tif, TIFFTAG_OPC2, opCode->op2Size, opCode->op2);
        }
        else
        {
            LOGD("opcode2 null");
        }
        if(opCode->op3Size > 0)
        {
            LOGD("Set OP3 %i", opCode->op3Size);
            TIFFSetField(tif, TIFFTAG_OPC3, opCode->op3Size, opCode->op3);
        }
        else
        {
            LOGD("opcode3 null");
        }
    } else
    {
        LOGD("opcode null");
    }
    LOGD("writeRawStuff");
    writeRawStuff(tif);

    TIFFWriteDirectory(tif);
    LOGD("set exif");
    if(exifInfo != NULL)
    {
        uint64 exif_offset = 0;
        TIFFCreateEXIFDirectory(tif);
        writeExifIfd(tif);
        TIFFWriteCustomDirectory(tif, &exif_offset);
        TIFFSetDirectory(tif, 0);
        TIFFSetField (tif, TIFFTAG_EXIFIFD, exif_offset);
        TIFFRewriteDirectory(tif);
    }

    if(gpsInfo != NULL)
    {
        uint64 gps_offset = 0;
        LOGD("makeGPSIFD");
        makeGPS_IFD(tif);
        LOGD("TIFFWriteCustomDirectory");
        TIFFWriteCustomDirectory(tif, &gps_offset);
        // set GPSIFD tag
        LOGD("TIFFSetDirectory");
        TIFFSetDirectory(tif, 0);
        LOGD("setgpsoffset");
        TIFFSetField (tif, TIFFTAG_GPSIFD, gps_offset);

    }

    TIFFClose(tif);


    LOGD("DONE");
}
