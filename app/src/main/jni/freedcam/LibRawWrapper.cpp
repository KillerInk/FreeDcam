//
// Created by troop on 22.11.2020.
//

#include "LibRawWrapper.h"
#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include "JniUtils.h"
#include <cstring>

#define  LOG_TAG    "freedcam.LibRawWrapper"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

void copyMatrix(float* dest, float colormatrix[4][3])
{
    int m = 0;
    for (int i = 0; i < 3; i++)
    {
        for (int t = 0; t < 3; t++)
        {
            dest[m++] = colormatrix[i][t];
        }
    }
}
void copyMatrix(float* dest, float colormatrix[3][4])
{
    int m = 0;
    for (int i = 0; i < 3; i++)
    {
        for (int t = 0; t < 3; t++)
        {
            dest[m++] = colormatrix[i][t];
        }
    }
}

void copyMatrix(float* dest, float colormatrix[4])
{
    int m = 0;
    for (int i = 0; i < 3; i++)
    {
        dest[m++] = colormatrix[i];
    }
}

void LibRawWrapper::openFD(int fd) {
    isFP =true;
    FILE *f = fdopen(fd, "r" );
    LOGD("FileDescriptor open");
    fseek(f, 0, SEEK_END);
    LOGD("get size");
    long fsize = ftell(f);
    LOGD("size:%l",fsize);
    fseek(f, 0, SEEK_SET);  /* same as rewind(f); */
    LOGD("malloc ");
    buffer = (char *)malloc((fsize+1)*sizeof(char));
    LOGD("malloc end, fill buffer");
    fread(buffer, fsize, 1, f);
    LOGD("buffer filled, close file");
    fclose(f); // Close the file
    LOGD("closed file, libraw open buffer");
    raw.open_buffer(buffer,fsize);
    //free(buffer);
}

void LibRawWrapper::openFile(const char *strFilename) {
    int ret;
    if((ret = raw.open_file(strFilename)) != LIBRAW_SUCCESS)
    {
        LOGD("ERROR open File %s",libraw_strerror(ret));
    }
}

jobject LibRawWrapper::getBitmap(JNIEnv* env) {
    raw.imgdata.params.no_auto_bright = 1;
    raw.imgdata.params.use_camera_wb = 1;
    raw.imgdata.params.output_bps = 8;
    raw.imgdata.params.user_qual = 0;
    raw.imgdata.params.half_size = 1;
    int ret;
    if((ret = raw.unpack()) != LIBRAW_SUCCESS)
    {
        LOGD("ERROR unpack File %s",libraw_strerror(ret));
        return NULL;
    }
    LOGD("unpacked img %i", ret);
    if((ret = raw.dcraw_process()) != LIBRAW_SUCCESS)
    {
        LOGD("ERROR unpack File %s",libraw_strerror(ret));
        return NULL;
    }
    LOGD("processing dcraw %i", ret);
    libraw_processed_image_t *image = raw.dcraw_make_mem_image(&ret);

    LOGD("processed image, creating bitmap");
    if(image->width == 0 || image->height == 0)
        return NULL;
    jobject ob = copyToJavaBitmap(env,image->data,image->data_size,image->width,image->height);

    // recycle() is needed only if we want to free the resources right now.
    // If we process files in a cycle, the next open_file()
    // will also call recycle(). If a fatal error has happened, it means that recycle()
    // has already been called (repeated call will not cause any harm either).
    // we don't evoke recycle() or call the desctructor; C++ will do everything for us
    delete image;
    if (isFP)
        free(buffer);

    LOGD("rawdata recycled");

    return ob;
}

void LibRawWrapper::recycle() {
    raw.recycle();
}

ushort * LibRawWrapper::getRawData() {
    raw.imgdata.params.no_auto_bright = 0; //-W
    raw.imgdata.params.use_camera_wb = 1;
    raw.imgdata.params.output_bps = 16; // -6
    raw.imgdata.params.output_color = 0;
    //raw.imgdata.params.user_qual = 0;
    //raw.imgdata.params.half_size = 1;
    raw.imgdata.params.no_auto_scale = 0;
    raw.imgdata.params.gamm[0] = 1.0; //-g 1 1
    raw.imgdata.params.gamm[1] = 1.0; //-g 1 1
    raw.imgdata.params.output_tiff = 0;
    raw.imgdata.params.no_interpolation = 1;
    int ret;
    if ((ret = raw.unpack()) != LIBRAW_SUCCESS)
        return NULL;
    width = raw.imgdata.sizes.raw_width;
    height =  raw.imgdata.sizes.raw_height;
    int t = 0;
    return raw.imgdata.rawdata.raw_image;
}

void LibRawWrapper::getExifInfo(ExifInfo * exifInfo) {
    exifInfo->_iso = raw.imgdata.other.iso_speed;
    exifInfo->_exposure = raw.imgdata.other.shutter;
    exifInfo->_fnumber = raw.imgdata.other.aperture;
    exifInfo->_focallength = raw.imgdata.other.focal_len;
    exifInfo->_orientation = static_cast<char*>(static_cast<void*>(&raw.imgdata.sizes.flip));
}

bool match(char* ar1, char* ar2)
{
    bool ret = true;
    for (int i = 0; i < 4; ++i) {
        if (ar1[i] != ar2[i])
            return false;
    }
    return ret;
}

void LibRawWrapper::getDngProfile(DngProfile *dngprofile) {
    float* bl = new float[4];
    int black = (raw.imgdata.color.dng_levels.dng_cblack[6]);
    for (size_t i = 0; i < 4; i++)
    {
        bl[i] = black;
    }
    dngprofile->blacklevel = bl;
    dngprofile->whitelevel = raw.imgdata.color.dng_levels.dng_whitelevel[0];
    dngprofile->rawwidht = raw.imgdata.sizes.raw_width;
    dngprofile->rawheight = raw.imgdata.sizes.raw_height;
    dngprofile->rowSize = 0;

    char cfaar[4];
    cfaar[0] = raw.imgdata.idata.cdesc[raw.COLOR(0, 0)];
    cfaar[1] = raw.imgdata.idata.cdesc[raw.COLOR(0, 1)];
    cfaar[2] = raw.imgdata.idata.cdesc[raw.COLOR(1, 0)];
    cfaar[3] = raw.imgdata.idata.cdesc[raw.COLOR(1, 1)];

    char * cfa = cfaar;
    char* bggr = "BGGR";
    char* rggb = "RGGB";
    char* grbg = "GRBG";
    if (match(cfa,bggr))
    {
        dngprofile->bayerformat = "bggr";
    }
    else if (match(cfa,rggb))
    {
        dngprofile->bayerformat = "rggb";
    }
    else if (match(cfa,grbg))
    {
        dngprofile->bayerformat = "grbg";
    }
    else
    {
        dngprofile->bayerformat = "gbrg";
    }

    dngprofile->rawType = 6;

}

void LibRawWrapper::getCustomMatrix(CustomMatrix *matrix) {
    matrix->colorMatrix1 = new float[9];
    matrix->colorMatrix2 = new float[9];
    matrix->neutralColorMatrix = new float[3];
    matrix->fowardMatrix1 = new float[9];
    matrix->fowardMatrix2 = new float[9];
    matrix->reductionMatrix1 = new float[9];
    matrix->reductionMatrix2 = new float[9];

    copyMatrix(matrix->colorMatrix1, raw.imgdata.color.dng_color[0].colormatrix);
    copyMatrix(matrix->colorMatrix2, raw.imgdata.color.dng_color[1].colormatrix);
    copyMatrix(matrix->neutralColorMatrix, raw.imgdata.color.cam_mul);
    matrix->neutralColorMatrix[0] = 1 / matrix->neutralColorMatrix[0];
    matrix->neutralColorMatrix[1] = 1 / matrix->neutralColorMatrix[1];
    matrix->neutralColorMatrix[2] = 1 / matrix->neutralColorMatrix[2];
    copyMatrix(matrix->fowardMatrix1, raw.imgdata.color.dng_color[0].forwardmatrix);
    copyMatrix(matrix->fowardMatrix2, raw.imgdata.color.dng_color[1].forwardmatrix);
    copyMatrix(matrix->reductionMatrix1, raw.imgdata.color.dng_color[0].calibration);
    copyMatrix(matrix->reductionMatrix2, raw.imgdata.color.dng_color[1].calibration);
}

