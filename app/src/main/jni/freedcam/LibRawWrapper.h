//
// Created by troop on 22.11.2020.
//
#ifndef FREEDCAM_LIBRAWWRAPPER_H
#define FREEDCAM_LIBRAWWRAPPER_H

#include "../LibRaw/libraw/libraw.h"
#include "ExifInfo.h"
#include "DngProfile.h"
#include <jni.h>

class LibRawWrapper {

private:
    LibRaw raw;
public:
    void openFD(int fd);
    void openFile(const char *strFilename);
    jobject getBitmap(JNIEnv* env);
    void recycle();
    uint16_t* getRawData();
    void getExifInfo(ExifInfo * exifInfo);
    void getDngProfile(DngProfile * dngProfile);

    LibRawWrapper(){}

};


#endif //FREEDCAM_LIBRAWWRAPPER_H
