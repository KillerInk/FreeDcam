//
// Created by troop on 23.10.2016.
//

#ifndef FREEDCAM_DNGWRITER_H
#define FREEDCAM_DNGWRITER_H


#include "../tiff/libtiff/tiffio.h"
#include "../tiff/libtiff/tiffiop.h"
#include "ExifInfo.h"
#include "GpsInfo.h"
#include "DngProfile.h"
#include "CustomMatrix.h"
#include "OpCode.h"
#include <android/log.h>
#include <../tiff/libtiff/tif_dir.h>
#include <assert.h>
#include <stdlib.h>


#define LOSY_JPEG 34892
#define LINEAR_RAW 34892

//typedef unsigned long long uint64;
class DngWriter
{

private:
    //open tiff from filepath
    TIFF *openfTIFF(char* fileSavePath);
    //open tiff from filedescriptor
    TIFF *openfTIFFFD(char* fileSavePath, int fd);
    void writeIfd0(TIFF *tif);
    void makeGPS_IFD(TIFF *tif);
    void writeExifIfd(TIFF *tif);
    void processTight(TIFF *tif);
    void process10tight(TIFF *tif);
    void process12tight(TIFF *tif);
    void processLoose(TIFF *tif);
    void processSXXX16(TIFF *tif);
    void processSXXX16crop(TIFF *tif);
    void process16to10(TIFF *tif);
    void process16to12(TIFF *tif);
    void writeRawStuff(TIFF *tif);
    void quadBayer16bit(TIFF *tif);
    void process16ToLossless(TIFF *tiff);
    unsigned short getColor(int row, int col);

public:
    ExifInfo *exifInfo;
    GpsInfo *gpsInfo;
    DngProfile * dngProfile;
    CustomMatrix * customMatrix;
    OpCode * opCode = NULL;
    char* _make = NULL;
    char*_model = NULL;
    char* _dateTime = NULL;

    long rawSize;

    char *fileSavePath;
    long fileLength;
    unsigned char* bayerBytes;

    float *tonecurve;
    int tonecurvesize;
    float *huesatmapdata1;
    int huesatmapdata1_size;
    float *huesatmapdata2;
    int huesatmapdata2_size;
    float baselineExposure;
    float baselineExposureOffset;
    unsigned int bayergreensplit;

    int *huesatmapdims;


    int fileDes;
    bool hasFileDes;

    int crop_width;
    int crop_height;
    int compression = COMPRESSION_NONE;

    int thumbheight, thumwidth;
    unsigned char* _thumbData;

    DngWriter()
    {
        exifInfo = NULL;
        gpsInfo = NULL;
        dngProfile = NULL;
        customMatrix = NULL;
        opCode = NULL;
        fileDes = -1;
        hasFileDes = false;
        tonecurve = NULL;
        fileSavePath = NULL;
        fileLength = 0;
        bayerBytes = NULL;
        tonecurvesize = 0;
        huesatmapdata1 = NULL;
        huesatmapdata1_size = 0;
        huesatmapdata2 = NULL;
        huesatmapdata2_size = 0;
        huesatmapdims = NULL;
        crop_width = 0;
        crop_height =0;
    }

    void WriteDNG();
    void clear();


};


#endif //FREEDCAM_DNGWRITER_H
