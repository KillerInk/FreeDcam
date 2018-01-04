//
// Created by troop on 25.10.2016.
//

#include <jni.h>
#include <stdlib.h>
#include <android/log.h>
#include "../tiff/libtiff/tiffio.h"
#define  LOG_TAG    "freedcam.RawToDngNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern "C"
{
    JNIEXPORT void JNICALL Java_freed_jni_DngStack_startStack(JNIEnv *env, jobject thiz, jobjectArray filesToStack, jstring outputfile);
}



//move in pointer values to different mem region that it not get cleared on TIFFClose(tif);
void moveToMem(float * in, float *out, int count)
{
    for (int i = 0; i < count; ++i) {
        out[i] = in[i];
    }
}


JNIEXPORT void JNICALL Java_freed_jni_DngStack_startStack(JNIEnv *env, jobject thiz, jobjectArray filesToStack, jstring outputfile)
{
    int stringCount = (*env).GetArrayLength(filesToStack);
    int width,height, outputcount;
    const char * files[stringCount];
    const char * outfile =(*env).GetStringUTFChars( outputfile, NULL);
    unsigned short tmpPixel, bitdeep,bitdeeptemp;
    unsigned char * rawOutputData;
    unsigned char* cfa= new unsigned char[4];
    unsigned char* cfatmp= new unsigned char[4];
    float* cmat1 = new float[9];
    float * cmat2 = new float[9];
    float * neutMat = new float[3];
    float * fmat1= new float[9];
    float * fmat2= new float[9];
    float * calib1= new float[9];
    float * calib2= new float[9];
    double *noisemat  = new double[6];
    float *blackleveltmp;
    short blacklevel;
    float * tmpmat;
    double * tmpdouble;
    long * whitelvl;
    long white;

    //short * whitelvltmp;
    //short whitelvl;
    unsigned char * inbuf;
    /*unsigned char * opcodetmp;
    unsigned char * opcode2;
    unsigned char * opcode3;*/
    LOGD("FilesToReadCount: %i", stringCount);
    for (int i=0; i<stringCount; i++) {
        jstring string = (jstring) (*env).GetObjectArrayElement(filesToStack, i);
        files[i] = (*env).GetStringUTFChars( string, NULL);
    }

    TIFF *tif=TIFFOpen(files[0], "rw");
    //read needed dng tags
    TIFFGetField(tif, TIFFTAG_IMAGEWIDTH, &width);
    LOGD("GetWidth");
    TIFFGetField(tif, TIFFTAG_IMAGELENGTH, &height);
    LOGD("GetHeight");
    TIFFGetField(tif, TIFFTAG_BITSPERSAMPLE, &bitdeeptemp);
    LOGD("GetBitDeep");
    bitdeep = bitdeeptemp;
    TIFFGetField(tif, TIFFTAG_COLORMATRIX1, &tmpmat);
    LOGD("cc1");
    moveToMem(tmpmat, cmat1,9);
    TIFFGetField(tif, TIFFTAG_COLORMATRIX2, &tmpmat);
    LOGD("cc2");
    moveToMem(tmpmat, cmat2,9);
    TIFFGetField(tif, TIFFTAG_ASSHOTNEUTRAL, &tmpmat);
    LOGD("neutral");
    moveToMem(tmpmat, neutMat,3);
    TIFFGetField(tif, TIFFTAG_FOWARDMATRIX1, &tmpmat);
    moveToMem(tmpmat, fmat1,9);
    TIFFGetField(tif, TIFFTAG_FOWARDMATRIX2, &tmpmat);
    moveToMem(tmpmat, fmat2,9);
    TIFFGetField(tif, TIFFTAG_CAMERACALIBRATION1, &tmpmat);
    moveToMem(tmpmat, calib1,9);
    TIFFGetField(tif, TIFFTAG_CAMERACALIBRATION2, &tmpmat);
    moveToMem(tmpmat, calib2,9);
    TIFFGetField(tif, TIFFTAG_NOISEPROFILE, &tmpdouble);
    for (int i = 0; i < 6; ++i) {
        noisemat[i] = tmpdouble[i];
    }
    TIFFGetField(tif, TIFFTAG_CFAPATTERN, &cfatmp);
    for (int i = 0; i < 4; ++i) {
        cfa[i] = cfatmp[i];
    }
    LOGD("cfa pattern %c%c&c&c", cfa[0],cfa[1],cfa[2],cfa[3]);

    TIFFGetField(tif, TIFFTAG_WHITELEVEL, &whitelvl);
    LOGD("whitelvl:%i" , whitelvl[0]);
    white = whitelvl[0];
    LOGD("whitelvl:%i" , white);
    //whitelvl = whitelvltmp[0];
    TIFFGetField(tif, TIFFTAG_BLACKLEVEL, &blackleveltmp);
    /*TIFFGetField(tif, TIFFTAG_OPC2, &opcodetmp);
    opcode2 = opcodetmp;
    TIFFGetField(tif, TIFFTAG_OPC3, &opcodetmp);
    opcode3 = opcodetmp;*/
    blacklevel = blackleveltmp[0];

    rawOutputData = new unsigned char[((width*height)*16)/8];

    int scanlinesize = TIFFStripSize(tif);
    inbuf = (unsigned char*)_TIFFmalloc(scanlinesize);
    outputcount = 0;
    for (int row = 0; row < height; row++)
    {
        TIFFReadRawStrip(tif,row, inbuf, scanlinesize);
        if(bitdeep == 10)
        {
            for (int i = 0; i < scanlinesize; i+=5) {
                tmpPixel = (inbuf[i] << 2 | (inbuf[i+1] & 0b11000000) >> 6); //11111111 11
                rawOutputData[outputcount++] = tmpPixel & 0xff;
                rawOutputData[outputcount++] = tmpPixel >>8;

                tmpPixel = ((inbuf[i+1] & 0b00111111 ) << 4 | (inbuf[i+2] & 0b11110000) >> 4); // 222222 2222
                rawOutputData[outputcount++] = tmpPixel & 0xff;
                rawOutputData[outputcount++] = tmpPixel >>8;

                tmpPixel = ((inbuf[i+2]& 0b00001111 ) << 6 | (inbuf[i+3] & 0b11111100) >> 2); // 3333 333333
                rawOutputData[outputcount++] = tmpPixel & 0xff;
                rawOutputData[outputcount++] = tmpPixel >>8;

                tmpPixel = ((inbuf[i+3]& 0b00000011 ) << 8 | inbuf[i+4]); // 44 44444444
                rawOutputData[outputcount++] = tmpPixel & 0xff;
                rawOutputData[outputcount++] = tmpPixel >>8;
            }
        }
        else if(bitdeep == 16)
        {
            for (int i = 0; i < scanlinesize; i+=8) {
                tmpPixel = (inbuf[i] | inbuf[i+1]<<8);
                rawOutputData[outputcount++] = tmpPixel & 0xff;
                rawOutputData[outputcount++] = tmpPixel >>8;

                tmpPixel = (inbuf[i+2] | inbuf[i+3]<<8);
                rawOutputData[outputcount++] = tmpPixel & 0xff;
                rawOutputData[outputcount++] = tmpPixel >>8;

                tmpPixel = (inbuf[i+4] | inbuf[i+5]<<8);
                rawOutputData[outputcount++] = tmpPixel & 0xff;
                rawOutputData[outputcount++] = tmpPixel >>8;

                tmpPixel = (inbuf[i+6] | inbuf[i+7]<<8);
                rawOutputData[outputcount++] = tmpPixel & 0xff;
                rawOutputData[outputcount++] = tmpPixel >>8;
            }
        }
    }
    TIFFClose(tif);

    //read left dngs and merge them
    for (int i = 1; i < stringCount; ++i) {
        TIFF *tif=TIFFOpen(files[i], "rw");
        TIFFGetField(tif, TIFFTAG_BITSPERSAMPLE, &bitdeeptemp);
        bitdeep = bitdeeptemp;
        outputcount = 0;
        for (int row = 0; row < height; row++)
        {
            TIFFReadRawStrip(tif,row, inbuf, scanlinesize);
            if(bitdeep == 10)
            {
                for (int i = 0; i < scanlinesize; i+=5) {
                    tmpPixel = (((inbuf[i] << 2 | (inbuf[i+1] & 0b11000000) >> 6)) + (rawOutputData[outputcount] | rawOutputData[outputcount+1]<<8))/2; //11111111 11
                    rawOutputData[outputcount++] = tmpPixel & 0xff;
                    rawOutputData[outputcount++] = tmpPixel >>8;

                    tmpPixel = ((((inbuf[i+1] & 0b00111111 ) << 4 | (inbuf[i+2] & 0b11110000) >> 4)) + (rawOutputData[outputcount] | rawOutputData[outputcount+1]<<8))/2; // 222222 2222
                    rawOutputData[outputcount++] = tmpPixel & 0xff;
                    rawOutputData[outputcount++] = tmpPixel >>8;

                    tmpPixel = ((((inbuf[i+2]& 0b00001111 )  | (inbuf[i+3] & 0b11111100) >> 2)) + (rawOutputData[outputcount] | rawOutputData[outputcount+1]<<8))/2; // 3333 333333
                    rawOutputData[outputcount++] = tmpPixel & 0xff;
                    rawOutputData[outputcount++] = tmpPixel >>8;

                    tmpPixel = ((((inbuf[i+3]& 0b00000011 ) << 8 | inbuf[i+4])) + (rawOutputData[outputcount] | rawOutputData[outputcount+1]<<8))/2; // 44 44444444
                    rawOutputData[outputcount++] = tmpPixel & 0xff;
                    rawOutputData[outputcount++] = tmpPixel >>8;
                }
            }
            else if(bitdeep == 16)
            {
                for (int i = 0; i < scanlinesize; i+=8) {
                    tmpPixel = (((inbuf[i] | inbuf[i+1]<<8))+ (rawOutputData[outputcount] | rawOutputData[outputcount+1]<<8))/2;
                    rawOutputData[outputcount++] = tmpPixel & 0xff;
                    rawOutputData[outputcount++] = tmpPixel >>8;

                    tmpPixel = (((inbuf[i+2] | inbuf[i+3]<<8))+ (rawOutputData[outputcount] | rawOutputData[outputcount+1]<<8))/2;
                    rawOutputData[outputcount++] = tmpPixel & 0xff;
                    rawOutputData[outputcount++] = tmpPixel >>8;

                    tmpPixel = (((inbuf[i+4] | inbuf[i+5]<<8))+ (rawOutputData[outputcount] | rawOutputData[outputcount+1]<<8))/2;
                    rawOutputData[outputcount++] = tmpPixel & 0xff;
                    rawOutputData[outputcount++] = tmpPixel >>8;

                    tmpPixel = (((inbuf[i+6] | inbuf[i+7]<<8))+ (rawOutputData[outputcount] | rawOutputData[outputcount+1]<<8))/2;
                    rawOutputData[outputcount++] = tmpPixel & 0xff;
                    rawOutputData[outputcount++] = tmpPixel >>8;
                }
            }
        }
        TIFFClose(tif);
    }
    free(inbuf);
    //create stacked dng
    tif=TIFFOpen(outfile, "w");

    TIFFSetField (tif, TIFFTAG_SUBFILETYPE, 0);
    TIFFSetField(tif, TIFFTAG_IMAGEWIDTH, width);
    TIFFSetField(tif, TIFFTAG_IMAGELENGTH, height);
    TIFFSetField(tif, TIFFTAG_BITSPERSAMPLE, 16);
    TIFFSetField(tif, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_CFA);
    TIFFSetField(tif, TIFFTAG_COMPRESSION, COMPRESSION_NONE);
    TIFFSetField (tif, TIFFTAG_SAMPLESPERPIXEL, 1);
    TIFFSetField(tif, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
    TIFFSetField(tif, TIFFTAG_DNGVERSION, "\001\003\0\0");
    TIFFSetField(tif, TIFFTAG_DNGBACKWARDVERSION, "\001\001\0\0");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX1, 9, cmat1);
    LOGD("colormatrix1");
    TIFFSetField(tif, TIFFTAG_COLORMATRIX2, 9, cmat2);
    TIFFSetField(tif, TIFFTAG_ASSHOTNEUTRAL, 3, neutMat);
    LOGD("neutralMatrix");
    if(fmat1 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX1, 9,  fmat1);
    if(fmat2 != NULL)
        TIFFSetField(tif, TIFFTAG_FOWARDMATRIX2, 9,  fmat2);
    if(calib1 != NULL)
        TIFFSetField(tif, TIFFTAG_CAMERACALIBRATION1, 9,  calib1);
    if(calib2 != NULL)
        TIFFSetField(tif, TIFFTAG_CAMERACALIBRATION2, 9,  calib2);
    if(noisemat != NULL)
        TIFFSetField(tif, TIFFTAG_NOISEPROFILE, 6,  noisemat);
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT1, 17);
    TIFFSetField(tif, TIFFTAG_CALIBRATIONILLUMINANT2, 21);

    TIFFSetField(tif, TIFFTAG_ORIENTATION, ORIENTATION_TOPLEFT);

    TIFFSetField (tif, TIFFTAG_CFAPATTERN, cfa);
    LOGD("whitelvl:%i" , white);
    TIFFSetField (tif, TIFFTAG_WHITELEVEL,1, &white);

    short CFARepeatPatternDim[] = { 2,2 };
    TIFFSetField (tif, TIFFTAG_CFAREPEATPATTERNDIM, CFARepeatPatternDim);
    int bl = blacklevel;
    float *blacklevelar = new float[4];
    for (int i = 0; i < 4; ++i) {
        blacklevelar[i] = bl;
    }
    TIFFSetField (tif, TIFFTAG_BLACKLEVEL, 4, blacklevelar);
    LOGD("wrote blacklevel");
    TIFFSetField (tif, TIFFTAG_BLACKLEVELREPEATDIM, CFARepeatPatternDim);

    //TODO find out why OPCODE cause ueof ex in ps
    /*if(sizeof(opcode2)>0)
        TIFFSetField(tif,TIFFTAG_OPC2, sizeof(opcode2), opcode2);
    if(sizeof(opcode3)>0)
        TIFFSetField(tif,TIFFTAG_OPC3, sizeof(opcode3), opcode3);*/
    TIFFCheckpointDirectory(tif);

    TIFFWriteRawStrip(tif, 0, rawOutputData, ((width*height)*16)/8);

    TIFFRewriteDirectory(tif);

    //TIFFWriteRawStrip(tif, 0, rawOutputData, width*height);

    TIFFClose(tif);
    delete[] rawOutputData;
}