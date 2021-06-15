//
// Created by troop on 22.11.2020.
//
#ifndef FREEDCAM_LIBRAWWRAPPER_H
#define FREEDCAM_LIBRAWWRAPPER_H

#include "../LibRaw/libraw/libraw.h"
#include "ExifInfo.h"
#include "DngProfile.h"
#include "CustomMatrix.h"
#include <jni.h>

class LibRawWrapper {

private:
    LibRaw raw;
    char *buffer;
    bool isFP = false;
public:
    int width;
    int height;
    void openFD(int fd);
    void openFile(const char *strFilename);
    jobject getBitmap(JNIEnv* env);
    void recycle();
    ushort * getRawData();
    void getExifInfo(ExifInfo * exifInfo);
    void getDngProfile(DngProfile * dngProfile);
    void getCustomMatrix(CustomMatrix * customMatrix);

    LibRawWrapper(){}

};


#endif //FREEDCAM_LIBRAWWRAPPER_H
