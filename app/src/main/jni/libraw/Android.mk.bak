LOCAL_PATH := $(call my-dir)

#if you need to add more module, do the same as the one we started with (the one with the CLEAR_VARS)
#include $(CLEAR_VARS)

#LOCAL_MODULE    := libraw
#LOCAL_JNI_SHARD_LIBRARIES := libraw
#LOCAL_REQUIRED_MODULES := libraw
#LOCAL_CPPFLAGS  := -fexceptions
#LOCAL_SRC_FILES := src/libraw_c_api.cpp src/libraw_cxx.cpp src/libraw_jni_api.c internal/dcraw_fileio.cpp internal/dcraw_common.cpp internal/demosaic_packs.cpp
#LOCAL_LDLIBS    += -llog

#include $(BUILD_SHARED_LIBRARY)

#librawutils
#used for compiling libraw
include $(CLEAR_VARS)
LIBRAW_PATH := $(LOCAL_PATH)/LibRaw

LOCAL_CPPFLAGS  := -fexceptions -frtti
#LOCAL_CFLAGS  := -DLIBRAW_USE_OPENMP -fopenmp
LOCAL_MODULE     := libraw					# name of your module
LOCAL_C_INCLUDES := $(LIBRAW_PATH)/internal $(LIBRAW_PATH)/libraw $(LIBRAW_PATH)/ $(LOCAL_PATH)/
LOCAL_LDLIBS	:=	-llog #-fopenmp
LOCAL_SRC_FILES  :=   $(LOCAL_PATH)/swab.c $(LIBRAW_PATH)/internal/dcraw_common.cpp $(LIBRAW_PATH)/internal/dcraw_fileio.cpp $(LIBRAW_PATH)/internal/demosaic_packs.cpp $(LIBRAW_PATH)/src/libraw_cxx.cpp $(LIBRAW_PATH)/src/libraw_c_api.cpp $(LIBRAW_PATH)/src/libraw_datastream.cpp
LOCAL_EXPORT_C_INCLUDES := $(LIBRAW_PATH)/libraw
include $(BUILD_STATIC_LIBRARY)
