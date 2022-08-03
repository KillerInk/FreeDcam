
#include "JniUtils.h"
#include <stdlib.h>
#include "RawStackPipeNative.h"
#include "CustomMatrix.h"
#include "DngProfile.h"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern "C"
{
    JNIEXPORT jobject JNICALL Java_freed_jni_RawStack_init(JNIEnv *env, jobject thiz) {
        RawStackPipeNative * rawStackPipeNative = new RawStackPipeNative();
        return env->NewDirectByteBuffer(rawStackPipeNative, 0);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_getOutput(JNIEnv *env, jobject thiz,jobject javaHandler,jbyteArray bytes) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);

        jbyte * out = (jbyte *)rawStackPipeNative->outdata;
        env->SetByteArrayRegion(bytes,0, (rawStackPipeNative->height*rawStackPipeNative->width*2),reinterpret_cast<jbyte*>(out));
    }

    JNIEXPORT void JNICALL
    Java_freed_jni_RawStack_setFirstFrame(JNIEnv *env, jobject thiz, jobject buffer, jobject img,
                                          jint width, jint height, jint imagecount) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(buffer);
        rawStackPipeNative->setBaseFrame(width,height,(uint16_t*)env->GetDirectBufferAddress(img),imagecount);
    }

    JNIEXPORT void JNICALL
    Java_freed_jni_RawStack_setNextFrame(JNIEnv *env, jobject thiz, jobject buffer, jobject img) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(buffer);
        rawStackPipeNative->setNextFrame((uint16_t*)env->GetDirectBufferAddress(img));
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_stackImages(JNIEnv *env, jobject thiz, jobject input ,jbyteArray output) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(input);

        uint16_t * out = rawStackPipeNative->merge_align();
        env->SetByteArrayRegion(output,0, (rawStackPipeNative->height*rawStackPipeNative->width*2),reinterpret_cast<jbyte*>(out));
        rawStackPipeNative->clear();
    }


    JNIEXPORT void JNICALL Java_freed_jni_RawStack_stackTo14(JNIEnv *env, jobject thiz, jobject input ,jbyteArray output) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(input);

        uint16_t * out = rawStackPipeNative->merge_to_14bit();
        env->SetByteArrayRegion(output,0, (rawStackPipeNative->height*rawStackPipeNative->width*2),reinterpret_cast<jbyte*>(out));
        rawStackPipeNative->clear();
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_setBaseFrame(JNIEnv *env, jobject thiz, jobject javaHandler, jbyteArray input, jint width, jint height) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        rawStackPipeNative->init(width,height, (uint16_t*)copyByteArray(env, input));
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_setBaseFrameBuffer(JNIEnv *env, jobject thiz, jobject javaHandler, jobject input, jint width, jint height) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        rawStackPipeNative->init(width,height, (uint16_t*)env->GetDirectBufferAddress(input));
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_stackFrame(JNIEnv *env, jobject thiz, jobject javaHandler, jbyteArray input) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        rawStackPipeNative->stackFrame((uint16_t*)copyByteArray(env, input));
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_stackFrameAvarage(JNIEnv *env, jobject thiz, jobject javaHandler, jbyteArray input) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        rawStackPipeNative->stackFrameAvarage((uint16_t*)copyByteArray(env, input));
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_stackByteBufferAvarage(JNIEnv *env, jobject thiz, jobject javaHandler, jobject input) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        rawStackPipeNative->stackFrameAvarage((uint16_t*)env->GetDirectBufferAddress(input));
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_stackFrameBuffer(JNIEnv *env, jobject thiz, jobject javaHandler, jobject input) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        rawStackPipeNative->stackFrame((uint16_t*)env->GetDirectBufferAddress(input));
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_SetOpCode(JNIEnv *env, jobject thiz, jobject opcode,jobject javaHandler)
    {
        RawStackPipeNative* writer = (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        writer->opCode = (OpCode*)env->GetDirectBufferAddress(opcode);
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_setUpShift(JNIEnv *env, jobject thiz,jobject javaHandler,jint upshift)
    {
        RawStackPipeNative* writer = (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        writer->upshift = upshift;
    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_clear(JNIEnv *env, jobject thiz,jobject javaHandler)
    {
        RawStackPipeNative* writer = (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        writer->clear();
    }


    JNIEXPORT void JNICALL Java_freed_jni_RawStack_writeDng(JNIEnv *env, jobject thiz, jobject javaHandler, jobject dngprofile, jobject matrix,jstring fileout, jobject exifinfo) {
        RawStackPipeNative * rawStackPipeNative = (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        DngProfile * profile = (DngProfile*)env->GetDirectBufferAddress(dngprofile);
        CustomMatrix * cmatrix = (CustomMatrix*)env->GetDirectBufferAddress(matrix);
        ExifInfo * exifInfo = (ExifInfo*)env->GetDirectBufferAddress(exifinfo);
        char * outfile = copyString(env,fileout);
        rawStackPipeNative->writeDng(profile, cmatrix, outfile,exifInfo);
        delete rawStackPipeNative;

    }

    JNIEXPORT void JNICALL Java_freed_jni_RawStack_writeJpeg(JNIEnv *env, jobject thiz, jobject javaHandler, jobject dngprofile, jobject matrix,jstring fileout, jobject exifinfo) {
       /* RawStackPipeNative * rawStackPipeNative = (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);
        DngProfile * profile = (DngProfile*)env->GetDirectBufferAddress(dngprofile);
        CustomMatrix * cmatrix = (CustomMatrix*)env->GetDirectBufferAddress(matrix);
        ExifInfo * exifInfo = (ExifInfo*)env->GetDirectBufferAddress(exifinfo);
        char * outfile = copyString(env,fileout);
        rawStackPipeNative->writeJpeg(profile, cmatrix, outfile,exifInfo);*/
    }

};
