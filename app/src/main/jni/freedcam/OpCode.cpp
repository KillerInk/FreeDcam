//
// Created by troop on 21.07.2018.
//
#include <jni.h>
#include "OpCode.h"
#include "JniUtils.h"

extern "C"
{
    JNIEXPORT jobject JNICALL Java_freed_jni_OpCode_init(JNIEnv *env, jobject thiz) {
        OpCode *writer = new OpCode();
        return env->NewDirectByteBuffer(writer, 0);
    }

    JNIEXPORT void JNICALL Java_freed_jni_OpCode_setOp2(JNIEnv *env, jobject thiz, jobject javaHandler, jbyteArray input)
    {
        OpCode * opcode =  (OpCode*)env->GetDirectBufferAddress(javaHandler);
        opcode->op2 = (unsigned char*)copyByteArray(env, input);
        opcode->op2Size = env->GetArrayLength(input);
    }

    JNIEXPORT void JNICALL Java_freed_jni_OpCode_setOp3(JNIEnv *env, jobject thiz, jobject javaHandler, jbyteArray input)
    {
        OpCode * opcode =  (OpCode*)env->GetDirectBufferAddress(javaHandler);
        opcode->op3 = (unsigned char*)copyByteArray(env, input);
        opcode->op3Size = env->GetArrayLength(input);
    }

    JNIEXPORT void JNICALL Java_freed_jni_OpCode_clear(JNIEnv *env, jobject thiz, jobject javaHandler)
    {
        OpCode* opcode = (OpCode*)env->GetDirectBufferAddress(javaHandler);
        opcode->clear();
        delete opcode;
    }
}