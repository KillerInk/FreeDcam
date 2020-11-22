//
// Created by troop on 22.11.2020.
//

#include "LibRawWrapper.h"
#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include "JniUtils.h"

#define  LOG_TAG    "freedcam.LibRawWrapper"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

void LibRawWrapper::openFD(int fd) {
    FILE *f = fdopen(fd, "r" );
    LOGD("FileDescriptor open");
    fseek(f, 0, SEEK_END);
    LOGD("get size");
    long fsize = ftell(f);
    LOGD("size:%l",fsize);
    fseek(f, 0, SEEK_SET);  /* same as rewind(f); */
    LOGD("malloc ");
    char *buffer = (char *)malloc((fsize+1)*sizeof(char));
    LOGD("malloc end, fill buffer");
    fread(buffer, fsize, 1, f);
    LOGD("buffer filled, close file");
    fclose(f); // Close the file
    LOGD("closed file, libraw open buffer");
    raw.open_buffer(buffer,fsize);
    free(buffer);
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
    raw.recycle();
    LOGD("rawdata recycled");

    return ob;
}
