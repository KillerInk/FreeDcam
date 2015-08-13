LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

include $(CLEAR_VARS)
LOCAL_MODULE    := imageconverter
LOCAL_SRC_FILES := ImageProcessorWrapper.cpp ImageProcessor.cpp
LOCAL_LDLIBS := -lz -lm -llog -ljnigraphics -landroid
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/android
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := surfacenativedraw
LOCAL_SRC_FILES := surfacenativedraw.cpp
LOCAL_LDLIBS := -lz -lm -llog -ljnigraphics -landroid
include $(BUILD_SHARED_LIBRARY)

APP_OPTIM := debug
LOCAL_CFLAGS := -g