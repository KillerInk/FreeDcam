LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_TIFF_SRC_FILES := \
	libtiff/tif_dirread.c \
	libtiff/tif_zip.c \
	libtiff/tif_flush.c \
	libtiff/tif_next.c \
	libtiff/tif_ojpeg.c \
	libtiff/tif_dirwrite.c \
	libtiff/tif_dirinfo.c \
	libtiff/tif_dir.c \
	libtiff/tif_compress.c \
	libtiff/tif_close.c \
	libtiff/tif_tile.c \
	libtiff/tif_open.c \
	libtiff/tif_getimage.c \
	libtiff/tif_pixarlog.c \
	libtiff/tif_warning.c \
	libtiff/tif_dumpmode.c \
	libtiff/tif_jpeg.c \
	libtiff/tif_jbig.c \
	libtiff/tif_predict.c \
	libtiff/mkg3states.c \
	libtiff/tif_write.c \
	libtiff/tif_error.c \
	libtiff/tif_version.c \
	libtiff/tif_print.c \
	libtiff/tif_color.c \
	libtiff/tif_read.c \
	libtiff/tif_extension.c \
	libtiff/tif_thunder.c \
	libtiff/tif_lzw.c \
	libtiff/tif_fax3.c \
	libtiff/tif_luv.c \
	libtiff/tif_codec.c \
	libtiff/tif_unix.c \
	libtiff/tif_packbits.c \
	libtiff/tif_aux.c \
	libtiff/tif_fax3sm.c \
	libtiff/tif_swab.c \
	libtiff/tif_strip.c

LOCAL_TIFF_SRC_FILES += port/lfind.c 
###########################################################

LOCAL_SRC_FILES:= $(LOCAL_TIFF_SRC_FILES)
LOCAL_C_INCLUDES += \
					$(LOCAL_PATH)/libtiff \
					external/zlib \
					external/jpeg
LOCAL_CFLAGS += -DAVOID_TABLES 
LOCAL_CFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays
LOCAL_STATIC_LIBRARIES := \
	libjpeg
LOCAL_SHARED_LIBRARIES := \
	libz
LOCAL_MODULE:= libtiff
LOCAL_PRELINK_MODULE:=false
include $(BUILD_SHARED_LIBRARY)

###########################################################

include $(CLEAR_VARS)
LOCAL_ARM_MODE := arm
LOCAL_SRC_FILES:= $(LOCAL_TIFF_SRC_FILES)
LOCAL_C_INCLUDES += \
					$(LOCAL_PATH)/libtiff \
					external/zlib \
					external/jpeg
LOCAL_CFLAGS += -DAVOID_TABLES
LOCAL_CFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays
LOCAL_STATIC_LIBRARIES := \
	libjpeg
LOCAL_SHARED_LIBRARIES := \
	libz
LOCAL_MODULE:= libtiff
include $(BUILD_STATIC_LIBRARY)

###########################################################

include $(CLEAR_VARS)

LOCAL_SRC_FILES = tools/tiffinfo.c

LOCAL_C_INCLUDES += \
					$(LOCAL_PATH)/libtiff

LOCAL_SHARED_LIBRARIES = \
						 libtiff
LOCAL_MODULE:= tiffinfo

include $(BUILD_EXECUTABLE)
