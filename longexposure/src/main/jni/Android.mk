LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

include $(CLEAR_VARS)
LOCAL_MODULE    := YuvMerge
LOCAL_SRC_FILES := YuvMerge.cpp
LOCAL_LDLIBS := -lz \
	-lm \
	-llog
include $(BUILD_SHARED_LIBRARY)

APP_OPTIM := debug
LOCAL_CFLAGS := -g