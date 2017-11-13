//
// Created by troop on 23.10.2016.
//

#ifndef FREEDCAM_DNGWRITER_H
#define FREEDCAM_DNGWRITER_H


#include "../tiff/libtiff/tiffio.h"
#include <android/log.h>
#include <../tiff/libtiff/tif_dir.h>
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
    void clear()
    {
        if(_make != NULL)
            delete [] _make;
        if(_model != NULL)
            delete [] _model;
        if(_imagedescription != NULL)
            delete [] _imagedescription;
        if(_dateTime != NULL)
            delete [] _dateTime;
        if(_orientation != NULL)
            delete [] _orientation;
        if(gps)
        {
            delete [] Latitude;
            delete [] Longitude;
            delete [] Provider;
            delete [] fileSavePath;
        }
        if(blacklevel != NULL)
            delete[] blacklevel;

        if(bayerBytes != NULL)
            free(bayerBytes);
        
        if(colorMatrix1 != NULL)
            delete[] colorMatrix1;
        if(colorMatrix2 != NULL)
            delete[] colorMatrix2;
        if(neutralColorMatrix != NULL)
            delete[] neutralColorMatrix;
        if(fowardMatrix1 != NULL)
            delete[] fowardMatrix1;
        if(fowardMatrix2 != NULL)
            delete[] fowardMatrix2;
        if(reductionMatrix1 != NULL)
            delete[] reductionMatrix1;
        if(reductionMatrix2 != NULL)
            delete[] reductionMatrix2;
        if(tonecurve != NULL)
            delete[] tonecurve;
        if(huesatmapdata1 != NULL)
            delete[] huesatmapdata1;
        if(huesatmapdata2 != NULL)
            delete[] huesatmapdata2;
        if(huesatmapdims != NULL)
            delete[] huesatmapdims;
        if (noiseMatrix != NULL)
            delete []noiseMatrix;
        if(_thumbData != NULL)
            delete [] _thumbData;
        if(opcode2 != NULL)
            delete [] opcode2;
        if(opcode3 != NULL)
            delete [] opcode3;


    }
public:
    int _iso, _flash;
    double _exposure;
    char* _make;
    char*_model;
    char* _imagedescription;
    char* _dateTime;
    char* _orientation;
    float _fnumber, _focallength;
    float _exposureIndex;

    double Altitude;
    float *Latitude;
    float *Longitude;
    char* Provider;
    long gpsTime;
    bool gps;


    long whitelevel;
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
    float *huesatmapdata1;
    int huesatmapdata1_size;
    float *huesatmapdata2;
    int huesatmapdata2_size;
    float baselineExposure;
    float baselineExposureOffset;

    int *huesatmapdims;
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
        fileSavePath = NULL;
    }

    void WriteDNG();
};


#endif //FREEDCAM_DNGWRITER_H
