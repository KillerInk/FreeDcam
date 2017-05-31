//
// Created by troop on 23.10.2016.
//

#include "DngWriter.h"


#define  LOG_TAG    "freedcam.DngWriter"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

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
    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    LOGD("subfiletype");
    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, rawwidht) != 0);
    LOGD("width");
    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, rawheight) != 0);
    LOGD("height");
    if(rawType == 1 || rawType == 3)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
    else if (rawType == 4)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 12) != 0);
    else
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 10) != 0);
    LOGD("bitspersample");
    assert(TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA) != 0);
    LOGD("PhotometricCFA");
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
    TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreeDcam DNG Writter 2017");
    if(_dateTime != NULL)
        TIFFSetField(tif,TIFFTAG_DATETIME, _dateTime);
    LOGD("software");
    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    LOGD("dngversion");
    TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, _model);
    LOGD("CameraModel");
    TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, _imagedescription);
    LOGD("imagedescription");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, colorMatrix1);
    LOGD("colormatrix1");
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutralColorMatrix);
    LOGD("neutralMatrix");
    //STANDARD A = FIIRST 17
    //D65 21 Second According to DNG SPEC 1.4 this is the correct order
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);

    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, colorMatrix2);
    if(fowardMatrix1 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  fowardMatrix1);
    if(fowardMatrix2 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  fowardMatrix2);

    if(reductionMatrix1 != NULL)
        TIFFSetField(tif, TIFFTAG_CAMERACALIBRATION1, 9,  reductionMatrix1);
    if(reductionMatrix2 != NULL)
        TIFFSetField(tif, TIFFTAG_CAMERACALIBRATION2, 9,  reductionMatrix2);

    if(noiseMatrix != NULL)
        TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  noiseMatrix);
    LOGD("colormatrix2");
}

void DngWriter::makeGPS_IFD(TIFF *tif) {
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
    if (!TIFFSetField( tif, GPSTAG_GPSAltitude,Altitude))
    {
        LOGD("Can't write Altitude" );
    }
    LOGD("Altitude Written");
}

void DngWriter::writeExifIfd(TIFF *tif) {
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
}

//process mipi10bit to 16bit 10bit values stored
void DngWriter::processTight(TIFF *tif) {
    LOGD("IN SXXXXl0");
    int i, j, row, col, b;
    unsigned char *buffer, *dp;
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

    for (row=0; row < rawheight; row ++)
    {
        i = 0;
        for(b = row * rowSize; b < row * rowSize + rowSize; b++)
            buffer[i++] = bayerBytes[b];
        for (dp=buffer, col = 0; col < rawwidht; dp+=5, col+= 4)
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
        buffer = NULL;
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
    LOGD("writer-RowSize: %d  rawheight:%d ,rawwidht: %d", rawSize, rawheight,
         rawwidht);
    if (rowSize == 0) {
        realrowsize = rawSize / rawheight;
        shouldberowsize = realrowsize;
        if (realrowsize % 5 > 0) {
            shouldberowsize = rawwidht * 10 / 8;
            bytesToSkip = realrowsize - shouldberowsize;
        }
        LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
        LOGD("width: %i height: %i", rawwidht, rawheight);
        LOGD("bytesToSkip: %i", bytesToSkip);
    }
    else{
        realrowsize = rawSize / rawheight;
        shouldberowsize = rowSize;
        bytesToSkip = realrowsize - shouldberowsize;
        LOGD("realrowsize:%i shouldbe:%i bytestoskip: %i", realrowsize, shouldberowsize, bytesToSkip);
    }

    int row = shouldberowsize;
    out = (unsigned char *)malloc((int)shouldberowsize*rawheight);
    if(out == NULL)
    {
        out = (unsigned char *)malloc((int)shouldberowsize*rawheight);
        if (out == NULL)
        {
        LOGD("failed to set buffer");
        return;}
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
    TIFFWriteRawStrip(tif, 0, out, rawheight*shouldberowsize);
    LOGD("Finalizng DNG");
    delete[] out;
}

void DngWriter::process12tight(TIFF *tif) {
    unsigned char* ar = bayerBytes;
    int bytesToSkip = 0;
    LOGD("writer-RowSize: %d  rawheight:%d ,rawwidht: %d",  rawSize,rawheight, rawwidht);
    int realrowsize = rawSize/rawheight;
    int shouldberowsize = rawwidht*12/8;
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    if (realrowsize != shouldberowsize)
        bytesToSkip = realrowsize - shouldberowsize;
    LOGD("bytesToSkip: %i", bytesToSkip);
    int row = shouldberowsize;
    unsigned char* out = (unsigned char *)malloc((int)shouldberowsize*rawheight);;
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
    TIFFWriteRawStrip(tif, 0, out, rawheight*shouldberowsize);
    LOGD("Finalizng DNG");
    delete[] out;
    out = NULL;
}

void DngWriter::processLoose(TIFF *tif) {
    int i, row, col, b;
    unsigned char *buffer, *dp;
    unsigned short pixel[rawwidht]; // array holds 16 bits per pixel

    uint64 colorchannel;

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
        i = 0;
        for(b = row * rowSize; b < (row * rowSize) + rowSize; b++)
            buffer[i++] = bayerBytes[b];
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

void DngWriter::processSXXX16(TIFF *tif) {
    int j, row, col;
    unsigned short pixel[rawwidht];
    unsigned short low, high;
    j=0;
    for (row=0; row < rawheight; row ++)
    {
        for (col = 0; col < rawwidht; col+=4)
        { // iterate over pixel columns
            for (int k = 0; k < 4; ++k)
            {
                low = bayerBytes[j++];
                high =   bayerBytes[j++];
                pixel[col+k] =  high << 8 |low;
                if(col < 4 && row < 4)
                    LOGD("Pixel : %i, high: %i low: %i ", pixel[col+k], high, low);
            }
        }
        if (TIFFWriteScanline (tif, pixel, row, 0) != 1) {
            LOGD("Error writing TIFF scanline.");
        }
    }
    LOGD("Finalizng DNG");
    LOGD("Free Memory");
    free(pixel);
}

void DngWriter::process16to10(TIFF *tif) {
    long j;
    int rowsizeInBytes= rawwidht*10/8;
    long finalsize = rowsizeInBytes * rawheight;
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

        G1_ar[0] = byts[j+2];
        G1_ar[1] = byts[j+3];

        G2_ar[0] = byts[j+4];
        G2_ar[1] = byts[j+5];

        R_ar[0] = byts[j+6];
        R_ar[1] = byts[j+7];
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
    TIFFWriteRawStrip(tif, 0, pixel, rawheight*rowsizeInBytes);
    LOGD("Finalizng DNG");

    LOGD("Free Memory");
    free(pixel);
}

void DngWriter::writeRawStuff(TIFF *tif) {
    char cfa[4] = {0,0,0,0};
    if(0 == strcmp(bayerformat,"bggr")){
        cfa[0] = 2;cfa[1] = 1;cfa[2] = 1;cfa[3] = 0;}
        //TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
    if(0 == strcmp(bayerformat , "grbg")){
        cfa[0] = 1;cfa[1] = 0;cfa[2] = 2;cfa[3] = 1;}//TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
    if(0 == strcmp(bayerformat , "rggb")){
        cfa[0] = 0;cfa[1] = 1;cfa[2] = 1;cfa[3] = 2;}//TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
    if(0 == strcmp(bayerformat , "gbrg")){
        cfa[0] = 1;cfa[1] = 2;cfa[2] = 0;cfa[3] = 1;}//TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
    if(0 == strcmp(bayerformat , "rgbw")){
        cfa[0] = 0;cfa[1] = 1;cfa[2] = 2;cfa[3] = 6;}//TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\002\006");

    LOGD("cfa pattern %c%c&c&c", cfa[0],cfa[1],cfa[2],cfa[3]);

    TIFFSetField (tif, TIFFTAG_CFAPATTERN, cfa);
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
    if(rawType == 0)
    {
        LOGD("Processing tight RAW data...");
        process10tight(tif);
        LOGD("Done tight RAW data...");
    }
    else if (rawType == 1)
    {
        LOGD("Processing loose RAW data...");
        processLoose(tif);
        LOGD("Done loose RAW data...");
    }
    else if (rawType == 2)
        process16to10(tif);
    else if (rawType == 3)
        processTight(tif);
    else if (rawType == 4)
        process12tight(tif);
}

void DngWriter::WriteDNG() {
    uint64 gps_offset = 0;
    uint64 exif_offset = 0;
    TIFF *tif;
    LOGD("has file description: %b", hasFileDes);
    if(hasFileDes == true)
    {
        tif = openfTIFFFD("", fileDes);
    }
    else
        tif = openfTIFF(fileSavePath);

    writeIfd0(tif);
    //allocate empty exifIFD tag
    TIFFSetField (tif, TIFFTAG_EXIFIFD, exif_offset);
    if(gps == true)
    {   //allocate empty GPSIFD tag
        TIFFSetField (tif, TIFFTAG_GPSIFD, gps_offset);        
    }
    //save directory
    TIFFCheckpointDirectory(tif);

    //write and store exififd
    writeExifIfd(tif);    
    TIFFWriteCustomDirectory(tif, &exif_offset);    
    
    LOGD("set exif");

    if(gps == true)
    {
        makeGPS_IFD(tif);        
        TIFFWriteCustomDirectory(tif, &gps_offset);	
        
	// set GPSIFD tag
	TIFFSetDirectory(tif, 0);
	TIFFSetField (tif, TIFFTAG_GPSIFD, gps_offset);    	
	TIFFCheckpointDirectory(tif);    	        
    }
    
    //set exififd tag
    TIFFSetDirectory(tif, 0);
    TIFFSetField (tif, TIFFTAG_EXIFIFD, exif_offset);        
    
    writeRawStuff(tif);

    TIFFRewriteDirectory(tif);
    TIFFClose(tif);
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
    if (bayerBytes == NULL)
        return;
    free(bayerBytes);
    bayerBytes = NULL;
}