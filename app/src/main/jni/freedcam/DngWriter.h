//
// Created by troop on 23.10.2016.
//

#ifndef FREEDCAM_DNGWRITER_H
#define FREEDCAM_DNGWRITER_H


#include "../tiff/libtiff/tiffio.h"
#include <android/log.h>
#include <../tiff/libtiff/tif_dir.h>
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
    void makeGPS_IFD(TIFF *tif);
    void writeExifIfd(TIFF *tif);
    void processTight(TIFF *tif);
    void process10tight(TIFF *tif);
    void process12tight(TIFF *tif);
    void processLoose(TIFF *tif);
    void processSXXX16(TIFF *tif);
    void process16to10(TIFF *tif);
    void writeRawStuff(TIFF *tif);
public:
    int _iso, _flash;
    double _exposure;
    char* _make;
    char*_model;
    char* _imagedescription;
    char* _dateTime;
    char* _orientation;
    float _fnumber, _focallength;
    double _exposureIndex;

    double Altitude;
    float *Latitude;
    float *Longitude;
    char* Provider;
    long gpsTime;
    bool gps;


    float *blacklevel;
    char *fileSavePath;
    long fileLength;
    unsigned char* bayerBytes;
    int rawwidht, rawheight, rowSize;
    float *colorMatrix1;
    float *colorMatrix2;
    float *neutralColorMatrix;
    float *fowardMatrix1;
    float *fowardMatrix2;
    float *reductionMatrix1;
    float *reductionMatrix2;
    float *tonecurve;
    int tonecurvesize;
    double *noiseMatrix;
    char* bayerformat;
    int rawType;
    long rawSize;

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
        gps = false;
        fileDes = -1;
        hasFileDes = false;
        opcode2Size =0;
        opcode3Size = 0;
    }

    void WriteDNG();
};


#endif //FREEDCAM_DNGWRITER_H
