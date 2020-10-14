LOCAL_PATH := $(call my-dir)

LOCAL_ARM_MODE := arm

include $(CLEAR_VARS)
LOCAL_MODULE    := librawwrapper
LOCAL_SRC_FILES := librawwrapper.cpp
LOCAL_CPPFLAGS := -fexceptions -Wno-c++11-narrowing -frtti -std=c++11 -fpic

LOCAL_LDLIBS := -lz \
	-L $(LOCAL_PATH)/libs \
	-lm \
	-llog \
	-ljnigraphics \
	-landroid
LOCAL_C_INCLUDES += \
					$(NDK_APP_PROJECT_PATH)/tiff/libtiff/ \

LOCAL_STATIC_LIBRARIES := libraw
include $(BUILD_SHARED_LIBRARY)


APP_OPTIM := debug
LOCAL_CFLAGS := -g
