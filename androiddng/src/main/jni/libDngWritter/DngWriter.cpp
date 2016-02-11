//
// Created by GeorgeKiarie on 1/22/2016.
//

#include "DngWriter.h"
#include <jni.h>
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <time.h>
#include <math.h>
#include <android/log.h>
#define  LOG_TAG    "freedcam.DngWriterNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern "C"
{
JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetMetaData(JNIEnv *env, jobject thiz,jobject handler,
jint iso,
        jdouble expo,
jint flash,
        jfloat fNum,
jfloat focalL,
        jstring imagedescription,
jstring orientation,
        jdouble exposureIndex);
JNIEXPORT jobject JNICALL Java_za_kiarie_DNGwriter_Create(JNIEnv *env, jobject thiz);

JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetGPSData(JNIEnv *env, jobject thiz, jobject handler, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime);
JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, jint widht, jint height);
JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT jlong JNICALL Java_za_kiarie_DNGwriter_GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetRawHeight(JNIEnv *env, jobject thiz, jobject handler, jint height);
JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetBayerData(JNIEnv *env, jobject thiz, jobject handler,jbyteArray fileBytes, jstring fileout);
JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_WriteDNG(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetModelAndMake(JNIEnv *env, jobject thiz, jobject handler, jstring model, jstring make);
JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_RawToDng_Release(JNIEnv *env, jobject thiz, jobject handler);
JNIEXPORT jint JNICALL Java_za_kiarie_DNGwriter_GetRawHeight(JNIEnv *env, jobject thiz, jobject handler);



JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
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


    int thumbheight, thumwidth;
    unsigned char* _thumbData;

    DngWriter()
    {
        gps = false;
    }
};

JNIEXPORT jlong Java_za_kiarie_DNGwriter_GetRawBytesSize(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    return writer->rawSize;
}

JNIEXPORT jint JNICALL Java_za_kiarie_DNGwriter_GetRawHeight(JNIEnv *env, jobject thiz, jobject handler)
{
    DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
    return writer->rawheight;
}


JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetRawHeight(JNIEnv *env, jobject thiz, jobject handler, jint height)
{
DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
writer->rawheight = (int) height;
}

JNIEXPORT void JNICALL JJava_za_kiarie_DNGwriter_SetModelAndMake(JNIEnv *env, jobject thiz, jobject handler, jstring model, jstring make)
{
DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
writer->_make = (char*) env->GetStringUTFChars(make,NULL);
writer->_model = (char*) env->GetStringUTFChars(model,NULL);
}

JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetMetaData(JNIEnv *env, jobject thiz, jobject handler,
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

JNIEXPORT jobject JNICALL Java_za_kiarie_DNGwriter_Create(JNIEnv *env, jobject thiz)
{
    DngWriter *writer = new DngWriter();
    return env->NewDirectByteBuffer(writer, 0);
}

JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetGPSData(JNIEnv *env, jobject thiz,jobject handler, jdouble Altitude,jfloatArray Latitude,jfloatArray Longitude, jstring Provider, jlong gpsTime)
{
DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
writer->Altitude = (double)Altitude;
writer->Latitude =  env->GetFloatArrayElements(Latitude,NULL);
writer->Longitude = env->GetFloatArrayElements(Longitude,NULL);
writer->Provider = (char*) env->GetStringUTFChars(Provider,NULL);
writer->gpsTime = (long)(gpsTime);
writer->gps = true;
}

JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetThumbData(JNIEnv *env, jobject thiz, jobject handler,  jbyteArray mThumb, int widht, int height)
{
DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
writer->_thumbData = (unsigned char*) env->GetByteArrayElements(mThumb,NULL);
writer->thumbheight = (int) height;
writer->thumwidth = widht;
}

JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_Release(JNIEnv *env, jobject thiz, jobject handler)
{
DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);
if(writer->bayerBytes != NULL)
{
free(writer->bayerBytes);
writer->bayerBytes = NULL;
}
/*if(writer->_thumbData != NULL)
    free(writer->_thumbData);*/
if (writer != NULL)
free(writer);
writer = NULL;
}

JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetBayerData(JNIEnv *env, jobject thiz, jobject handler, jbyteArray fileBytes, jstring fileout)
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

JNIEXPORT void JNICALL Java_za_kiarie_DNGwriter_SetBayerInfo(JNIEnv *env, jobject thiz, jobject handler,
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

writer->blacklevel = new float[4] {blacklevel, blacklevel, blacklevel,blacklevel};
writer->rawType = tight;
writer->rowSize =rowSize;
writer->colorMatrix1 = env->GetFloatArrayElements(colorMatrix1, 0);
writer->colorMatrix2 =env->GetFloatArrayElements(colorMatrix2, 0);
writer->neutralColorMatrix = env->GetFloatArrayElements(neutralColor, 0);

writer->fowardMatrix1 = env->GetFloatArrayElements(fowardMatrix1, 0);
writer->fowardMatrix2 =env->GetFloatArrayElements(fowardMatrix2, 0);
writer->reductionMatrix1 = env->GetFloatArrayElements(reductionMatrix1, 0);
writer->reductionMatrix2 =env->GetFloatArrayElements(reductionMatrix2, 0);
writer->noiseMatrix =env->GetFloatArrayElements(noiseMatrix, 0);

writer->bayerformat = (char*)  env->GetStringUTFChars(bayerformat,0);
writer->rawheight = height;
writer->rawwidht = width;

}

char* TagGen(int Tag, Char Type)
{
    //Todo Convert Decimal to Hex then ConCat String
    //      0100 0003 0000 0001 0064 0000
  //        |    |    |         |
  //  tag --+    |    |         |
  //  short int -+    |         |
  //  one value ------+         |
  //  value of 100 -------------+

    // More Info http://paulbourke.net/dataformats/tiff/


}

void writeIfd0(FILE *fptr, DngWriter *writer)
{
    /* Write the header */
    WriteHexString(fptr,"4d4d002a");    /* Big endian & TIFF identifier */
    offset = writer->rawwidht * writer->rawheight * 3 + 8;
    putc((offset & 0xff000000) / 16777216,fptr);
    putc((offset & 0x00ff0000) / 65536,fptr);
    putc((offset & 0x0000ff00) / 256,fptr);
    putc((offset & 0x000000ff),fptr);

    //raw file ?
    /* Write the binary data */
    for (j=0;j<ny;j++) {
        for (i=0;i<nx;i++) {
            //... calculate the RGB value between 0 and 255 ...
            fputc(red,fptr);
            fputc(green,fptr);
            fputc(blue,fptr);
        }
    }

    /* Write the footer */
    WriteHexString(fptr,"000e");  /* The number of directory entries (14) */

    //Use Tiff Tags for ints added To DngWriter Header file

    WriteHexString(fptr,TagGen(TIFFTAG_SUBFILETYPE,Sshort));  /* The number of directory entries (14) */

    //TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);

    /* Width tag, short int */
    WriteHexString(fptr,"0100000300000001");
    fputc((writer->rawwidht & 0xff00) / 256,fptr);    /* Image width */
    fputc((writer->rawwidht & 0x00ff),fptr);
    WriteHexString(fptr,"0000");

    /* Height tag, short int */
    WriteHexString(fptr,"0101000300000001");
    fputc((writer->rawheight & 0xff00) / 256,fptr);    /* Image height */
    fputc((writer->rawheight & 0x00ff),fptr);
    WriteHexString(fptr,"0000");

    LOGD("subfiletype");

    assert(TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, writer->rawwidht) != 0);
    LOGD("width");


    assert(TIFFSetField(tif, TIFFTAG_IMAGELENGTH, writer->rawheight) != 0);
    LOGD("height");


    if(writer->rawType > 0)
        assert(TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16) != 0);
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
    TIFFSetField(tif, TIFFTAG_SOFTWARE, "FreedCam by Troop");
    LOGD("software");
    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    LOGD("dngversion");
    TIFFSetField(tif, TIFFTAG_UNIQUECAMERAMODEL, "SonyIMX");
    LOGD("CameraModel");
    TIFFSetField(tif, TIFFTAG_IMAGEDESCRIPTION, writer->_imagedescription);
    LOGD("imagedescription");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, writer->colorMatrix2);
    LOGD("colormatrix1");
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, writer->neutralColorMatrix);
    LOGD("neutralMatrix");
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 21);

    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 17);

    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, writer->colorMatrix1);

    static const float cam_foward1[] = {
            // R 	G     	B
            0.6648, 0.2566, 0.0429, 0.197, 0.9994, -0.1964, -0.0894, -0.2304, 1.145
    };

    static const float cam_foward2[] = {
            0.6617, 0.3849, -0.0823, 0.24, 1.1138, -0.3538, -0.0062, -0.1147, 0.946
    };

    static const float cam_nex_foward1[] = {
            // R 	G     	B
            0.6328, 0.0469, 0.2813, 0.1641, 0.7578, 0.0781, -0.0469, -0.6406, 1.5078
    };

    static const float cam_nex_foward2[] = {
            0.7578, 0.0859, 0.1172, 0.2734, 0.8281, -0.1016, 0.0156, -0.2813, 1.0859
    };
    TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  writer->fowardMatrix2);
    TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  writer->fowardMatrix1);
    static const float testNR[] = {
            0.00051471, 0, 0.00051471,0, 0.00051471, 0};
    TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  writer->noiseMatrix);



    LOGD("colormatrix2");
    //////////////////////////////IFD POINTERS///////////////////////////////////////
    ///GPS//////////
    // TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
    ///EXIF////////

}




void WriteHexString(FILE *fptr, char *s) {
    unsigned int i, c;
    char hex[3];

    for (i = 0; i < strlen(s); i += 2) {
        hex[0] = s[i];
        hex[1] = s[i + 1];
        hex[2] = '\0';
        sscanf(hex, "%X", &c);
        putc(c, fptr);
    }
}

    JNIEXPORT void JNICALL Java_com_troop_androiddng_RawToDng_WriteDNG(JNIEnv *env, jobject thiz, jobject handler)
    {
        uint64 dir_offset = 0, dir_offset2 = 0, gpsIFD_offset = 0;
        DngWriter* writer = (DngWriter*) env->GetDirectBufferAddress(handler);

        FILE* fptr = fopen(writer->fileSavePath,"w+");


        writeIfd0(fptr, writer);

        TIFFSetField (tif, TIFFTAG_EXIFIFD, dir_offset);
        LOGD("set exif");
        //CheckPOINT to KEEP EXIF IFD in MEMory
        //Try FiX DIR


        if(writer->gps == true)
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
        if(writer->gps)
            TIFFSetField (tif, TIFFTAG_GPSIFD, gpsIFD_offset);
        ///////////////////////////// WRITE THE SUB IFD's SUB IFD + EXIF IFD AGain GPS IFD would also go here as well as other cust IFD
        TIFFSetField(tif, TIFFTAG_EXIFIFD, dir_offset);

        writeRawStuff(tif,writer);

        if (writer->bayerBytes == NULL)
            return;
        delete[] writer->bayerBytes;
        writer->bayerBytes = NULL;

    }


