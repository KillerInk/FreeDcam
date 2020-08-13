
#include "JniUtils.h"
#include <stdlib.h>
#include "RawStackPipeNative.h"
#include "CustomMatrix.h"
#include "DngProfile.h"

extern "C"
{
    JNIEXPORT jobject JNICALL Java_freed_jni_RawStack_init(JNIEnv *env, jobject thiz) {
        RawStackPipeNative * rawStackPipeNative = new RawStackPipeNative();
        return env->NewDirectByteBuffer(rawStackPipeNative, 0);
    }

    JNIEXPORT jbyteArray JNICALL Java_freed_jni_RawStack_getOutput(JNIEnv *env, jobject thiz,jobject javaHandler) {
        RawStackPipeNative * rawStackPipeNative =  (RawStackPipeNative*)env->GetDirectBufferAddress(javaHandler);

        jbyte * out = (jbyte *)rawStackPipeNative->outdata;
        jbyteArray  jbyteArray1 = env->NewByteArray(rawStackPipeNative->height*rawStackPipeNative->width*2);
        env->SetByteArrayRegion(jbyteArray1,0, (rawStackPipeNative->height*rawStackPipeNative->width*2),reinterpret_cast<jbyte*>(out));
        return jbyteArray1;
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
