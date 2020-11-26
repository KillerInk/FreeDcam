//
// Created by troop on 23.11.2020.
//
#include <jni.h>
#include "LibRawWrapper.h"

extern "C"
{
    JNIEXPORT jobject JNICALL Java_freed_jni_LibRawJniWrapper_init(JNIEnv *env, jobject thiz)
    {
        LibRawWrapper *writer = new LibRawWrapper();
        return env->NewDirectByteBuffer(writer, 0);
    }

    JNIEXPORT void JNICALL Java_freed_jni_LibRawJniWrapper_openFile(JNIEnv *env, jobject thiz, jobject byte_buffer, jstring filename)
    {
        LibRawWrapper* writer = (LibRawWrapper*)env->GetDirectBufferAddress(byte_buffer);
        jboolean bIsCopy;
        const char *strFilename = (env)->GetStringUTFChars(filename, &bIsCopy);
        writer->openFile(strFilename);
        (env)->ReleaseStringUTFChars(filename, strFilename);
    }

    JNIEXPORT void JNICALL Java_freed_jni_LibRawJniWrapper_openFD(JNIEnv *env, jobject thiz, jobject byte_buffer, jint fd)
    {
        LibRawWrapper* writer = (LibRawWrapper*)env->GetDirectBufferAddress(byte_buffer);
        writer->openFD(fd);
    }

    JNIEXPORT jobject JNICALL Java_freed_jni_LibRawJniWrapper_getBitmap(JNIEnv *env, jobject thiz, jobject byte_buffer)
    {
        LibRawWrapper* writer = (LibRawWrapper*)env->GetDirectBufferAddress(byte_buffer);
        return writer->getBitmap(env);
    }

    JNIEXPORT void JNICALL Java_freed_jni_LibRawJniWrapper_release(JNIEnv *env, jobject thiz, jobject byte_buffer) {
        LibRawWrapper* writer = (LibRawWrapper*)env->GetDirectBufferAddress(byte_buffer);
        writer->recycle();
        delete writer;
    }

    JNIEXPORT void JNICALL Java_freed_jni_LibRawJniWrapper_getExifInfo(JNIEnv *env, jobject thiz, jobject byte_buffer, jobject exif_info)
    {
        LibRawWrapper* writer = (LibRawWrapper*)env->GetDirectBufferAddress(byte_buffer);
        ExifInfo * exifInfo = (ExifInfo*)env->GetDirectBufferAddress(exif_info);
        writer->getExifInfo(exifInfo);
    }

    JNIEXPORT void JNICALL Java_freed_jni_LibRawJniWrapper_getDngProfile(JNIEnv *env, jobject thiz, jobject byte_buffer, jobject dngprofile) {
        LibRawWrapper* writer = (LibRawWrapper*)env->GetDirectBufferAddress(byte_buffer);
        DngProfile * dngProfile = (DngProfile*)env->GetDirectBufferAddress(dngprofile);
        writer->getDngProfile(dngProfile);
    }

    JNIEXPORT void JNICALL Java_freed_jni_LibRawJniWrapper_getCustomMatrix(JNIEnv *env, jobject thiz, jobject byte_buffer, jobject customatrix) {
        LibRawWrapper* writer = (LibRawWrapper*)env->GetDirectBufferAddress(byte_buffer);
        CustomMatrix * customMatrix = (CustomMatrix*)env->GetDirectBufferAddress(customatrix);
        writer->getCustomMatrix(customMatrix);
    }

    JNIEXPORT jshortArray JNICALL Java_freed_jni_LibRawJniWrapper_getRawData(JNIEnv *env, jobject thiz, jobject byte_buffer) {
        LibRawWrapper* writer = (LibRawWrapper*)env->GetDirectBufferAddress(byte_buffer);
        ushort * img = writer->getRawData();
        int len = (writer->width * writer->height);
        jshortArray ret = env->NewShortArray(len);
        env->SetShortArrayRegion(ret, 0, len, reinterpret_cast<const jshort *>(img));

        return ret;
    }
}