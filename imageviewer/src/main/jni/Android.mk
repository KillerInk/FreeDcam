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
#LOCAL_CFLAGS += -I$(SYSROOT)/usr/lib/include/libraw -pthread -w
#LOCAL_CXXFLAGS += -I$(SYSROOT)/usr/lib/include/libraw -pthread -w
LOCAL_CPPFLAGS  := -fexceptions -frtti
#CFLAGS_DP1  := LibRaw-demosaic-pack-GPL2-0.16.0
LOCAL_CFLAGS  := -DLIBRAW_USE_OPENMP -fopenmp
LOCAL_MODULE     := libraw					# name of your module
#LOCAL_LDLIBS     += -L$(SYSROOT)/usr/lib -lstdc++ # libraries to link against, lstdc++ is auto-linked
LOCAL_LDLIBS	:=	-llog -fopenmp
LOCAL_SRC_FILES  :=  swab.cpp internal/dcraw_common.cpp internal/dcraw_fileio.cpp internal/demosaic_packs.cpp src/libraw_cxx.cpp src/libraw_c_api.cpp src/libraw_datastream.cpp
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libraw
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS 	:= -llog -fopenmp
LOCAL_CFLAGS  := -fopenmp
LOCAL_MODULE    := librawutils
LOCAL_SRC_FILES := librawutils/librawutils.cpp
LOCAL_STATIC_LIBRARIES := libraw
include $(BUILD_SHARED_LIBRARY)