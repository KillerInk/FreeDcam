LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_TIFF_SRC_FILES := \
	tiff/libtiff/tif_dirread.c \
	tiff/libtiff/tif_zip.c \
	tiff/libtiff/tif_flush.c \
	tiff/libtiff/tif_next.c \
	tiff/libtiff/tif_ojpeg.c \
	tiff/libtiff/tif_dirwrite.c \
	tiff/libtiff/tif_dirinfo.c \
	tiff/libtiff/tif_dir.c \
	tiff/libtiff/tif_compress.c \
	tiff/libtiff/tif_close.c \
	tiff/libtiff/tif_tile.c \
	tiff/libtiff/tif_open.c \
	tiff/libtiff/tif_getimage.c \
	tiff/libtiff/tif_pixarlog.c \
	tiff/libtiff/tif_warning.c \
	tiff/libtiff/tif_dumpmode.c \
	tiff/libtiff/tif_jpeg.c \
	tiff/libtiff/tif_jbig.c \
	tiff/libtiff/tif_predict.c \
	tiff/libtiff/mkg3states.c \
	tiff/libtiff/tif_write.c \
	tiff/libtiff/tif_error.c \
	tiff/libtiff/tif_version.c \
	tiff/libtiff/tif_print.c \
	tiff/libtiff/tif_color.c \
	tiff/libtiff/tif_read.c \
	tiff/libtiff/tif_extension.c \
	tiff/libtiff/tif_thunder.c \
	tiff/libtiff/tif_lzw.c \
	tiff/libtiff/tif_fax3.c \
	tiff/libtiff/tif_luv.c \
	tiff/libtiff/tif_codec.c \
	tiff/libtiff/tif_unix.c \
	tiff/libtiff/tif_packbits.c \
	tiff/libtiff/tif_aux.c \
	tiff/libtiff/tif_fax3sm.c \
	tiff/libtiff/tif_swab.c \
	tiff/libtiff/tif_strip.c

LOCAL_TIFF_SRC_FILES += tiff/port/lfind.c
###########################################################

LOCAL_SRC_FILES:= $(LOCAL_TIFF_SRC_FILES)
LOCAL_C_INCLUDES += \
					$(LOCAL_PATH)/tiff/libtiff \
					$(LOCAL_PATH)/jpeg
LOCAL_CFLAGS += -DAVOID_TABLES
LOCAL_CFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays
#LOCAL_CFLAGS := -fpermissive
LOCAL_MODULE:= libtiff
LOCAL_LDLIBS := -lz \
	-L $(LOCAL_PATH)/libs \
	-lm \
	-ljpeg
LOCAL_STATIC_LIBRARIES  += $(LOCAL_PATH)/libs
#LOCAL_PRELINK_MODULE:=false
include $(BUILD_STATIC_LIBRARY)


#librawutils
#used for compiling libraw
include $(CLEAR_VARS)
LOCAL_CPPFLAGS  := -fexceptions -frtti
LOCAL_MODULE     := libraw					# name of your module
LOCAL_LDLIBS	:=	-llog #-fopenmp
LOCAL_SRC_FILES  :=  swab.cpp internal/dcraw_common.cpp internal/dcraw_fileio.cpp internal/demosaic_packs.cpp src/libraw_cxx.cpp src/libraw_c_api.cpp src/libraw_datastream.cpp
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libraw
include $(BUILD_STATIC_LIBRARY)






include $(CLEAR_VARS)
LOCAL_MODULE    := libfreedcam
LOCAL_SRC_FILES := raw2dng/RawToDng.cpp librawutils/librawutils.cpp imageprocessor/ImageProcessorWrapper.cpp imageprocessor/ImageProcessor.cpp imageprocessor/surfacenativedraw.cpp imageprocessor/Staxxer.cpp
LOCAL_CPPFLAGS := -fexceptions -fopenmp
LOCAL_CFLAGS += -fopenmp
LOCAL_LDFLAGS += -fopenmp
LOCAL_LDLIBS := -lz \
	-L $(LOCAL_PATH)/libs \
	-lm \
	-llog \
	-fopenmp \
	-ljnigraphics \
	-landroid
LOCAL_C_INCLUDES += \
					$(LOCAL_PATH)/tiff/libtiff \
                    $(LOCAL_PATH)/../libjpeg/jpeg-9b/
LOCAL_STATIC_LIBRARIES := libtiff libraw libjpeg
include $(BUILD_SHARED_LIBRARY)


APP_OPTIM := debug
LOCAL_CFLAGS := -g 