/*
 * Copyright (c) 2011, Peter Nelson (http://peterdn.com)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
*/

#include <libraw/libraw.h>

#include <string.h>
#include <jni.h>

jstring Java_org_libraw_LibRaw_version(JNIEnv* env, jclass clazz) {
	return (*env)->NewStringUTF(env, libraw_version());
}

jbyteArray Java_org_libraw_LibRaw_getThumbFromBuffer(JNIEnv* env, jclass clazz, jbyteArray bufferBytes) {
	libraw_data_t * data;
	jbyte* buffer;
	jsize len;
	jbyteArray ret;
	libraw_processed_image_t * image;
	
	data = libraw_init(0);
	len = (*env)->GetArrayLength(env, bufferBytes);
	buffer = (*env)->GetByteArrayElements(env, bufferBytes, NULL);
	libraw_open_buffer(data, (void *) buffer, (size_t) len);
	libraw_unpack_thumb(data);
	image = libraw_dcraw_make_mem_thumb(data, 0);
	ret = (*env)->NewByteArray(env, image->data_size);
	(*env)->SetByteArrayRegion(env, ret, 0, image->data_size, (jbyte *) image->data);
	(*env)->ReleaseByteArrayElements(env, bufferBytes, buffer, 0);
	return ret;
}