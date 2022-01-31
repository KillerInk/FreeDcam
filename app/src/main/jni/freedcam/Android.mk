LOCAL_PATH := $(call my-dir)

LOCAL_ARM_MODE := arm

include $(CLEAR_VARS)
LOCAL_MODULE    := alignmerge
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../../libs/$(TARGET_ARCH_ABI)/stage1_alignmerge.a
LOCAL_C_INCLUDES += include/
LOCAL_CPP_INCLUDES += include/
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := align_merge
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../../libs/$(TARGET_ARCH_ABI)/stage1_align_merge.a
LOCAL_C_INCLUDES += include/
LOCAL_CPP_INCLUDES += include/
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := avarage_generator
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../../libs/$(TARGET_ARCH_ABI)/avarage_generator.a
LOCAL_C_INCLUDES += include/
LOCAL_CPP_INCLUDES += include/
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := libfreedcam
LOCAL_SRC_FILES := RawToDng.cpp DngWriter.cpp DngStacker.cpp ExifInfo.cpp GpsInfo.cpp DngProfile.cpp CustomMatrix.cpp OpCode.cpp RawStackPipeNative.cpp LibRawWrapper.cpp LibRawJniWrapper.cpp lj92.cpp
LOCAL_CPPFLAGS := -fexceptions -Wno-c++11-narrowing -frtti -std=c++11 -fPIC
LOCAL_LDFLAGS += -Wl,-Bsymbolic

LOCAL_LDLIBS := -lz \
	-L $(LOCAL_PATH)/libs \
	-lm \
	-llog \
	-ljnigraphics \
	-landroid \
	-lEGL \
	-lGLESv2
LOCAL_C_INCLUDES += \
					$(NDK_APP_PROJECT_PATH)/tiff/libtiff/ \

LOCAL_STATIC_LIBRARIES := libtiff libraw alignmerge align_merge avarage_generator
include $(BUILD_SHARED_LIBRARY)


APP_OPTIM := debug
LOCAL_CFLAGS := -g
