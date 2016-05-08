
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS 	:= -llog
LOCAL_MODULE    := librawutils
LOCAL_SRC_FILES := librawutils.cpp
LOCAL_STATIC_LIBRARIES := libraw
include $(BUILD_SHARED_LIBRARY)
