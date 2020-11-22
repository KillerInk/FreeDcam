//
// Created by troop on 22.11.2020.
//
#include "../LibRaw/libraw/libraw.h"
#include <jni.h>
#ifndef FREEDCAM_LIBRAWWRAPPER_H
#define FREEDCAM_LIBRAWWRAPPER_H




class LibRawWrapper {

private:
    LibRaw raw;
public:
    void openFD(int fd);
    void openFile(const char *strFilename);
    jobject getBitmap(JNIEnv* env);
    void recycle();

    LibRawWrapper(){}

};


#endif //FREEDCAM_LIBRAWWRAPPER_H
