LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

include $(CLEAR_VARS)
LOCAL_MODULE    := imageconverter
LOCAL_SRC_FILES := ImageProcessorWrapper.cpp ImageProcessor.cpp
LOCAL_LDLIBS := -lz \
	-lm \
	-llog \
	-ljnigraphics
include $(BUILD_SHARED_LIBRARY)

APP_OPTIM := debug
LOCAL_CFLAGS := -g