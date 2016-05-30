LOCAL_PATH := $(call my-dir)

LOCAL_ARM_MODE := arm


#librawutils
#used for compiling libraw
include $(CLEAR_VARS)
LOCAL_CPPFLAGS  := -fexceptions -frtti
LOCAL_MODULE     := libraw					# name of your module
LOCAL_LDLIBS	:=	-llog #-fopenmp
LOCAL_SRC_FILES  :=  swab.cpp internal/dcraw_common.cpp internal/dcraw_fileio.cpp internal/demosaic_packs.cpp src/libraw_cxx.cpp src/libraw_c_api.cpp src/libraw_datastream.cpp
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libraw
include $(BUILD_STATIC_LIBRARY)