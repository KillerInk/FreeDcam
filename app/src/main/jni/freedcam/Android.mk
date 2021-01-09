LOCAL_PATH := $(call my-dir)

LOCAL_ARM_MODE := arm

include $(CLEAR_VARS)
LOCAL_MODULE    := libfreedcam
LOCAL_SRC_FILES := RawToDng.cpp DngWriter.cpp DngStacker.cpp ExifInfo.cpp GpsInfo.cpp DngProfile.cpp CustomMatrix.cpp OpCode.cpp LibRawWrapper.cpp LibRawJniWrapper.cpp
LOCAL_CPPFLAGS := -fexceptions -Wno-c++11-narrowing -frtti -std=c++11 -fpic
LOCAL_SHARED_LIBRARIES := libtiff

LOCAL_LDLIBS := -lz \
	-L $(LOCAL_PATH)/libs \
	-lm \
	-llog \
	-ljnigraphics \
	-landroid


include $(BUILD_SHARED_LIBRARY)

# Add the prefab modules to the import path.
$(call import-add-path,/out)
$(call import-module,prefab/libtiff)
LOCAL_STATIC_LIBRARIES :=  libraw
include $(BUILD_SHARED_LIBRARY)


APP_OPTIM := debug
LOCAL_CFLAGS := -g
