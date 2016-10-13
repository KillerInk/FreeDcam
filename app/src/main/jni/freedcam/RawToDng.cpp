/*
 *
 *     Copyright (C) 2015 George Kiarie
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

#include <jni.h>
#include <../tiff/libtiff/tiffio.h>
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#include <../tiff/libtiff/tif_dir.h>
#define  LOG_TAG    "freedcam.RawToDngNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)



typedef unsigned long long uint64;
typedef unsigned short UINT16;
typedef unsigned char uint8;


extern "C"
{
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz,jobject handler,
                                                           jint iso,
                                                           jdouble expo,
                                                           jint flash,
                                                           jfloat fNum,
                                                           jfloat focalL,
                                                           jstring imagedescription,
                                                           jstring orientation,
                                                           jdouble exposureIndex);
JNIEXPORT jobject JNICALL Java_freed_jni_RawToDng_Create(JNIEnv *env, jobject thiz);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetGPSData(JNIEnv *env, jobject thiz, jobject handler, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, jint widht, jint height);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT jlong JNICALL Java_freed_jni_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jobject handler, jint height);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jobject handler,jbyteArray fileBytes, jstring fileout);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataFD(JNIEnv *env, jobject thiz, jobject handler,jbyteArray fileBytes, jint fileDescriptor, jstring filename);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jobject handler, jstring model, jstring make);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_Release(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT jint JNICALL Java_freed_jni_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode2(JNIEnv *env, jobject thiz, jobject handler, jbyteArray opcode);
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode3(JNIEnv *env, jobject thiz, jobject handler, jbyteArray opcode);

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
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
                                                            jint height);
}


class DngWriter
{
public:
    int _iso, _flash;
    double _exposure;
    char* _make;
    char*_model;
    char* _imagedescription;
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
    float *noiseMatrix;
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
};

JNIEXPORT jlong JNICALL Java_freed_jni_RawToDng_GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    return writer->rawSize;
}

JNIEXPORT jint JNICALL Java_freed_jni_RawToDng_GetRawHeight(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    return writer->rawheight;
}


JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetRawHeight(JNIEnv *env, jobject thiz, jobject handler, jint height)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->rawheight = (int) height;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetModelAndMake(JNIEnv *env, jobject thiz, jobject handler, jstring model, jstring make)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->_make = (char*) env->GetStringUTFChars(make,NULL);
    writer->_model = (char*) env->GetStringUTFChars(model,NULL);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetExifData(JNIEnv *env, jobject thiz, jobject handler,
                                                           jint iso,
                                                           jdouble expo,
                                                           jint flash,
                                                           jfloat fNum,
                                                           jfloat focalL,
                                                           jstring imagedescription,
                                                           jstring orientation,
                                                           jdouble exposureIndex)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->_iso = iso;
    writer->_exposure =expo;
    writer->_flash = flash;
    writer->_imagedescription = (char*) env->GetStringUTFChars(imagedescription,NULL);
    writer->_orientation = (char*) env->GetStringUTFChars(orientation,NULL);
    writer->_fnumber = fNum;
    writer->_focallength = focalL;
    writer->_exposureIndex = exposureIndex;
}

JNIEXPORT jobject JNICALL Java_freed_jni_RawToDng_Create(JNIEnv *env, jobject thiz)
{
    DngWriter *writer = new DngWriter();
    return env->NewDirectByteBuffer(writer, 0);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetGPSData(JNIEnv *env, jobject thiz,jobject handler, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->Altitude = (double)Altitude;
    writer->Latitude =  env->GetFloatArrayElements(Latitude,NULL);
    writer->Longitude = env->GetFloatArrayElements(Longitude,NULL);
    writer->Provider = (char*) env->GetStringUTFChars(Provider,NULL);
    writer->gpsTime = (long)(gpsTime);
    writer->gps = true;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, int widht, int height)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->_thumbData = (unsigned char*) env->GetByteArrayElements(mThumb,NULL);
    writer->thumbheight = (int) height;
    writer->thumwidth = widht;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode2(JNIEnv *env, jobject thiz, jobject handler, jbyteArray opcode)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->opcode2Size = env->GetArrayLength(opcode);
    writer->opcode2 = new unsigned char[writer->opcode2Size];
    memcpy(writer->opcode2, env->GetByteArrayElements(opcode,NULL), writer->opcode2Size);
}
JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetOpCode3(JNIEnv *env, jobject thiz, jobject handler, jbyteArray opcode)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    writer->opcode3Size = env->GetArrayLength(opcode);
    writer->opcode3 = new unsigned char[writer->opcode3Size];
    memcpy(writer->opcode3, env->GetByteArrayElements(opcode,NULL), writer->opcode3Size);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_Release(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    if(writer->bayerBytes != NULL)
    {
        free(writer->bayerBytes);
        writer->bayerBytes = NULL;
    }
    if(writer->opcode2Size >0)
    {
        free(writer->opcode2);
        writer->opcode2 = NULL;
    }
    if(writer->opcode3Size >0)
    {
        free(writer->opcode3);
        writer->opcode3 = NULL;
    }
    /*if(writer->_thumbData != NULL)
        free(writer->_thumbData);*/
    if (writer != NULL)
        free(writer);
    writer = NULL;
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerData(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fileBytes, jstring fileout)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    LOGD("Try to set Bayerdata");
    writer->bayerBytes = new unsigned char[env->GetArrayLength(fileBytes)];
    LOGD("init bayerbytes");
    //writer->bayerBytes = (unsigned char*) env->GetByteArrayElements(fileBytes,NULL);
    memcpy(writer->bayerBytes, env->GetByteArrayElements(fileBytes,NULL), env->GetArrayLength(fileBytes));
    LOGD(" set Bayerdata");
    writer->fileSavePath = (char*)  env->GetStringUTFChars(fileout,NULL);
    writer->rawSize = env->GetArrayLength(fileBytes);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerDataFD(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fileBytes, jint fileDescriptor, jstring filename)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    LOGD("Try to set SetBayerDataFD");
    writer->bayerBytes = new unsigned char[env->GetArrayLength(fileBytes)];
    LOGD("init bayerbytes");
    //writer->bayerBytes = (unsigned char*) env->GetByteArrayElements(fileBytes,NULL);
    memcpy(writer->bayerBytes, env->GetByteArrayElements(fileBytes,NULL), env->GetArrayLength(fileBytes));
    LOGD(" set Bayerdata");
    writer->fileDes = (int)fileDescriptor;
    writer->hasFileDes = true;
    LOGD(" writer->fileDes : %d", writer->fileDes);
    writer->fileSavePath = "";
    writer->rawSize = env->GetArrayLength(fileBytes);
    LOGD(" writer->rawsize : %d", writer->rawSize);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
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
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);

    writer->blacklevel = new float[4];
    for (int i = 0; i < 4; ++i) {
        writer->blacklevel[i] = blacklevel;
    }
    writer->rawType = tight;
    writer->rowSize =rowSize;
    writer->colorMatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
    writer->colorMatrix2 =env->GetFloatArrayElements(colorMatrix2, 0);
    writer->neutralColorMatrix = env->GetFloatArrayElements(neutralColor, 0);
    if(fowardMatrix1 != NULL)
        writer->fowardMatrix1 = env->GetFloatArrayElements(fowardMatrix1, 0);
    if(fowardMatrix2 != NULL)
        writer->fowardMatrix2 =env->GetFloatArrayElements(fowardMatrix2, 0);
    if(reductionMatrix1 != NULL)
        writer->reductionMatrix1 = env->GetFloatArrayElements(reductionMatrix1, 0);
    if(reductionMatrix2 != NULL)
        writer->reductionMatrix2 =env->GetFloatArrayElements(reductionMatrix2, 0);
    if(noiseMatrix != NULL)
        writer->noiseMatrix =env->GetFloatArrayElements(noiseMatrix, 0);

    writer->bayerformat = (char*)  env->GetStringUTFChars(bayerformat,0);
    writer->rawheight = height;
    writer->rawwidht = width;

}
//open tiff from filepath
TIFF *openfTIFF(char* fileSavePath)
{
    TIFF *tif;
    if (!(tif = TIFFOpen (fileSavePath, "w")))
    {
        LOGD("openfTIFF:error while creating outputfile");
    }
    return tif;
}
//open tiff from filedescriptor
TIFF *openfTIFFFD(char* fileSavePath, int fd)
{
    TIFF *tif;

    LOGD("FD: %d", fd);
    if (!(tif = TIFFFdOpen (fd,fileSavePath, "w")))
    {
        LOGD("openfTIFFFD:error while creating outputfile");
    }
    return tif;
}


void writeIfd0(TIFF *tif, DngWriter *writer)
{
    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    LOGD("subfiletype");
    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, writer->rawwidht) != 0);
    LOGD("width");
    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, writer->rawheight) != 0);
    LOGD("height");
    if(writer->rawType == 1 || writer->rawType == 3)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
    else if (writer->rawType == 4)
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
    TIFFSetField(tif, TIFFTAG_MAKE, writer->_make);
    LOGD("make");
    TIFFSetField(tif, TIFFTAG_MODEL, writer->_model);
    LOGD("model");
    try
    {
        if(0 == strcmp(writer->_orientation,"0") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_TOPLEFT);
        if(0 == strcmp(writer->_orientation,"90") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_RIGHTTOP);
        if(0 == strcmp(writer->_orientation,"180") )
            TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_BOTRIGHT);
        if(0 == strcmp(writer->_orientation,"270") )
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
    TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreeDcam by Troop");
    LOGD("software");
    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    LOGD("dngversion");
    TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
    LOGD("CameraModel");
    TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, writer->_imagedescription);
    LOGD("imagedescription");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, writer->colorMatrix1);
    LOGD("colormatrix1");
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, writer->neutralColorMatrix);
    LOGD("neutralMatrix");
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 21);
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 17);

    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, writer->colorMatrix2);
    if(writer->fowardMatrix1 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  writer->fowardMatrix1);
    if(writer->fowardMatrix2 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  writer->fowardMatrix2);
    if(writer->noiseMatrix != NULL)
        TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  writer->noiseMatrix);
    LOGD("colormatrix2");
}

void makeGPS_IFD(TIFF *tif, DngWriter *writer)
{
    LOGD("GPS IFD DATA");
    if (TIFFCreateGPSDirectory(tif) != 0)
    {
        LOGD("TIFFCreateGPSDirectory() failed" );
    }
    const char* longitudeRef = writer->Longitude  < 0 ? "W" : "E";
    if (!TIFFSetField( tif, GPSTAG_GPSLongitudeRef, longitudeRef))
    {
        LOGD("Can't write LongitudeRef" );
    }
    LOGD("LONG REF Written %c", longitudeRef);

    if (!TIFFSetField(tif, GPSTAG_GPSLongitude, writer->Longitude))
    {
        LOGD("Can't write Longitude" );
    }
    LOGD("Longitude Written");
    const char* latitudeRef = writer->Latitude < 0 ? "S" : "N";
    LOGD("PMETH Written");
    if (!TIFFSetField( tif, GPSTAG_GPSLatitudeRef, latitudeRef)) {
        LOGD("Can't write LAti REf" );
    }
    LOGD("LATI REF Written %c", latitudeRef);

    if (!TIFFSetField( tif, GPSTAG_GPSLatitude,writer->Latitude))
    {
        LOGD("Can't write Latitude" );
    }
    LOGD("Latitude Written");
    if (!TIFFSetField( tif, GPSTAG_GPSAltitude, writer->Altitude))
    {
        LOGD("Can't write Altitude" );
    }
    LOGD("Altitude Written");
}

void writeExifIfd(TIFF *tif, DngWriter *writer)
{
    /////////////////////////////////// EXIF IFD //////////////////////////////
    LOGD("EXIF IFD DATA");
    //write exif stuff direct into ifd0
    /*if (TIFFCreateEXIFDirectory(tif) != 0) {
        LOGD("TIFFCreateEXIFDirectory() failed" );
    }*/
    short iso[] = {writer->_iso};
    LOGD("EXIF dir created");
    if (!TIFFSetField( tif, EXIFTAG_ISOSPEEDRATINGS,1, iso)) {
        LOGD("Can't write SPECTRALSENSITIVITY" );
    }
    LOGD("iso");
    if (!TIFFSetField( tif, EXIFTAG_FLASH, writer->_flash)) {
        LOGD("Can't write Flas" );
    }
    LOGD("flash");
    if (!TIFFSetField( tif, EXIFTAG_APERTUREVALUE, writer->_fnumber)) {
        LOGD("Can't write Aper" );
    }
    LOGD("aperture");

    if (!TIFFSetField( tif, EXIFTAG_EXPOSURETIME,writer->_exposure)) {
        LOGD("Can't write SPECTRALSENSITIVITY" );
    }
    LOGD("exposure");


    if (!TIFFSetField( tif, EXIFTAG_FOCALLENGTH, writer->_focallength)) {
        LOGD("Can't write Focal" );
    }
    LOGD("focal");

    if (!TIFFSetField( tif, EXIFTAG_FNUMBER, writer->_fnumber)) {
        LOGD("Can't write FNum" );
    }
    LOGD("fnumber");


    //Check Point & Write are require checkpoint to update Current IFD Write Well to Write Close And Create IFD
}

void processTight(TIFF *tif,DngWriter *writer)
{
    LOGD("IN SXXXXl0");
    int i, j, row, col, b;
    unsigned char *buffer, *dp;
    unsigned short pixel[writer->rawwidht]; // array holds 16 bits per pixel

    LOGD("buffer set");
    j=0;
    if(writer->rowSize == 0)
        writer->rowSize =  -(-5 * writer->rawwidht >> 5) << 3;
    buffer =(unsigned char *)malloc(writer->rowSize);
    memset( buffer, 0, writer->rowSize);
    if (buffer == NULL)
    {
        LOGD("allocating buffer failed try again");
        buffer =(unsigned char *)malloc(writer->rowSize);
    }
    LOGD("rowsize:%i", writer->rowSize);

    for (row=0; row < writer->rawheight; row ++)
    {
        i = 0;
        for(b = row * writer->rowSize; b < row * writer->rowSize + writer->rowSize; b++)
            buffer[i++] = writer->bayerBytes[b];
        for (dp=buffer, col = 0; col < writer->rawwidht; dp+=5, col+= 4)
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
    LOGD("Mem Released");
}

void process10tight(TIFF *tif,DngWriter *writer) {
    unsigned char *ar = writer->bayerBytes;
    int bytesToSkip = 0;
    LOGD("writer-RowSize: %d  rawheight:%d ,rawwidht: %d", writer->rawSize, writer->rawheight,
         writer->rawwidht);
    int realrowsize = writer->rawSize / writer->rawheight;
    int shouldberowsize = realrowsize;
    if (realrowsize % 5 > 0)
    {
        shouldberowsize = writer->rawwidht*10/8;
        bytesToSkip = realrowsize - shouldberowsize;

    }
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    LOGD("width: %i height: %i", writer->rawwidht, writer->rawheight);
    LOGD("bytesToSkip: %i", bytesToSkip);
    int row = shouldberowsize;
    unsigned char* out = new unsigned char[(int)shouldberowsize*writer->rawheight];
    int m = 0;
    for(int i =0; i< writer->rawSize; i+=5)
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
    TIFFWriteRawStrip(tif, 0, out, writer->rawheight*shouldberowsize);

    TIFFRewriteDirectory(tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);

    delete[] out;
}

void process12tight(TIFF *tif,DngWriter *writer)
{
    unsigned char* ar = writer->bayerBytes;
    int bytesToSkip = 0;
    LOGD("writer-RowSize: %d  rawheight:%d ,rawwidht: %d",  writer->rawSize,writer->rawheight, writer->rawwidht);
    int realrowsize = writer->rawSize/writer->rawheight;
    int shouldberowsize = writer->rawwidht*12/8;
    LOGD("realrow: %i shoudlbe: %i", realrowsize, shouldberowsize);
    if (realrowsize != shouldberowsize)
        bytesToSkip = realrowsize - shouldberowsize;
    LOGD("bytesToSkip: %i", bytesToSkip);
    int row = shouldberowsize;
    unsigned char* out = new unsigned char[(int)shouldberowsize*writer->rawheight];
    int m = 0;
    for(int i =0; i< writer->rawSize; i+=3)
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
    TIFFWriteRawStrip(tif, 0, out, writer->rawheight*shouldberowsize);

    TIFFRewriteDirectory(tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);

    delete[] out;
}

void processLoose(TIFF *tif,DngWriter *writer)
{
    int i, row, col, b;
    unsigned char *buffer, *dp;
    unsigned short pixel[writer->rawwidht]; // array holds 16 bits per pixel

    uint64 colorchannel;

    writer->rowSize= (writer->rawwidht+5)/6 << 3;
    buffer =(unsigned char *)malloc(writer->rowSize);
    memset( buffer, 0, writer->rowSize);
    if (buffer == NULL)
    {
        LOGD("allocating buffer failed try again");
        buffer =(unsigned char *)malloc(writer->rowSize);
    }
    for (row=0; row < writer->rawheight; row ++)
    {
        i = 0;
        for(b = row * writer->rowSize; b < (row * writer->rowSize) + writer->rowSize; b++)
            buffer[i++] = writer->bayerBytes[b];
        /*
         * get 5 bytes from buffer and move first 4bytes to 16bit
         * split the 5th byte and add the value to the first 4 bytes
         * */
        for (dp=buffer, col = 0; col < writer->rawwidht; dp+=8, col+= 6)
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

void processSXXX16(TIFF *tif,DngWriter *writer)
{
    int j, row, col;
    unsigned short pixel[writer->rawwidht];
    unsigned short low, high;
    j=0;
    for (row=0; row < writer->rawheight; row ++)
    {
        for (col = 0; col < writer->rawwidht; col+=4)
        { // iterate over pixel columns
            for (int k = 0; k < 4; ++k)
            {
                low = writer->bayerBytes[j++];
                high =   writer->bayerBytes[j++];
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

void process16to10(TIFF *tif,DngWriter *writer)
{
    long j;
    int rowsizeInBytes= writer->rawwidht*10/8;
    long finalsize = rowsizeInBytes * writer->rawheight;
    unsigned char* byts= writer->bayerBytes;
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
    TIFFWriteRawStrip(tif, 0, pixel, writer->rawheight*rowsizeInBytes);
    TIFFRewriteDirectory(tif);
    LOGD("Finalizng DNG");
    TIFFClose(tif);
    LOGD("Free Memory");
    delete[] pixel;
}

void writeRawStuff(TIFF *tif, DngWriter *writer)
{
    if(0 == strcmp(writer->bayerformat,"bggr"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\002\001\001\0");// 0 = Red, 1 = Green, 2 = Blue, 3 = Cyan, 4 = Magenta, 5 = Yellow, 6 = White
    if(0 == strcmp(writer->bayerformat , "grbg"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\0\002\001");
    if(0 == strcmp(writer->bayerformat , "rggb"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\001\002");
    if(0 == strcmp(writer->bayerformat , "gbrg"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\001\002\0\001");
    if(0 == strcmp(writer->bayerformat , "rgbw"))
        TIFFSetField (tif, TIFFTAG_CFAPATTERN, "\0\001\002\006");
    long white=0x3ff;
    TIFFSetField (tif, TIFFTAG_WHITELEVEL, 1, &white);

    short CFARepeatPatternDim[] = { 2,2 };
    TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);

    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, writer->blacklevel);
    LOGD("wrote blacklevel");
    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);
    //**********************************************************************************

    LOGD("Set OP or not");
    if(writer->opcode2Size >0)
    {
        LOGD("Set OP2");
        TIFFSetField(tif, TIFFTAG_OPC2, writer->opcode2Size, writer->opcode2);
    }
    if(writer->opcode3Size >0)
    {
        LOGD("Set OP3");
        TIFFSetField(tif, TIFFTAG_OPC3, writer->opcode3Size, writer->opcode3);
    }
    if(writer->rawType == 0)
    {
        LOGD("Processing tight RAW data...");
        process10tight(tif, writer);
        LOGD("Done tight RAW data...");
    }
    else if (writer->rawType == 1)
    {
        LOGD("Processing loose RAW data...");
        processLoose(tif, writer);
        LOGD("Done loose RAW data...");
    }
    else if (writer->rawType == 2)
        process16to10(tif,writer);
    else if (writer->rawType == 3)
        processTight(tif, writer);
    else if (writer->rawType == 4)
        process12tight(tif, writer);
}

JNIEXPORT void JNICALL Java_freed_jni_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler)
{
    uint64 gps_offset = 0;
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    TIFF *tif;
    LOGD("has file description: %b", writer->hasFileDes);
    if(writer->hasFileDes == true)
    {
        tif = openfTIFFFD("", writer->fileDes);
    }
    else
        tif = openfTIFF(writer->fileSavePath);

    writeIfd0(tif,writer);
    const TIFFFieldArray *exif_fields = _TIFFGetExifFields();
    _TIFFMergeFields(tif, exif_fields->fields, exif_fields->count);
    writeExifIfd(tif,writer);
    LOGD("set exif");
    //CheckPOINT to KEEP IFD0 in MEMory
    TIFFCheckpointDirectory(tif);

    if(writer->gps == true)
    {
        makeGPS_IFD(tif, writer);
        TIFFCheckpointDirectory(tif);
        TIFFWriteCustomDirectory(tif, &gps_offset);
        TIFFSetDirectory(tif, 0);
        TIFFSetField (tif, TIFFTAG_GPSIFD, gps_offset);
        TIFFCheckpointDirectory(tif);
    }

    writeRawStuff(tif,writer);

    if (writer->bayerBytes == NULL)
        return;
    delete[] writer->bayerBytes;
    writer->bayerBytes = NULL;

}


