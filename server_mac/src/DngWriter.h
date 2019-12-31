//
// Created by troop on 23.10.2016.
//

#ifndef FREEDCAM_DNGWRITER_H
#define FREEDCAM_DNGWRITER_H


#include "tiff/libtiff/tiffio.h"
#include "ExifInfo.h"
#include "GpsInfo.h"
#include "DngProfile.h"
#include "CustomMatrix.h"
#include "DngTags.h"
#include "tiff/libtiff/tif_dir.h"
#include <assert.h>
#include <stdlib.h>

typedef unsigned long long uint64;
class DngWriter
{

private:
    //open tiff from filepath
    TIFF *openfTIFF(char* fileSavePath);
    //open tiff from filedescriptor
    TIFF *openfTIFFFD(char* fileSavePath, int fd);
    void writeIfd0(TIFF *tif);
    //void makeGPS_IFD(TIFF *tif);
    void writeExifIfd(TIFF *tif);
    void processTight(TIFF *tif);
    void process10tight(TIFF *tif);
    void process12tight(TIFF *tif);
    void processLoose(TIFF *tif);
    void processSXXX16(TIFF *tif);
    void process16to10(TIFF *tif);
    void process16to12(TIFF *tif);
	void process16to16(TIFF *tif);
    void writeRawStuff(TIFF *tif);
public:
	ExifInfo * exifInfo = NULL;;
    GpsInfo *gpsInfo = NULL;
	DngProfile * dngProfile = NULL;
    CustomMatrix * customMatrix = NULL;
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
    float baselineExposure = 0;
    float baselineExposureOffset = 0;

    int *huesatmapdims;


    int fileDes;
    bool hasFileDes;

    int thumbheight, thumwidth;
    unsigned char* _thumbData;

    int opcode2Size;
    unsigned char* opcode2;
    int opcode3Size;
    unsigned char* opcode3;

    DngWriter()
    {
        exifInfo = NULL;
        gpsInfo = NULL;
        dngProfile = NULL;
        customMatrix = NULL;
        fileDes = -1;
        hasFileDes = false;
        opcode2 = NULL;
        opcode3 = NULL;
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

        opcode2Size =0;
        opcode3Size = 0;
    }

    void WriteDNG();
};


#endif //FREEDCAM_DNGWRITER_H
