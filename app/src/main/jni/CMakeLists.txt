cmake_minimum_required(VERSION 3.10)

project(libfreedcam LANGUAGES CXX C)

# Set the C++ standard
set(CMAKE_CXX_STANDARD 11)

# Include directories for native sources
include_directories(
    ${PROJECT_SOURCE_DIR}/LibRaw
    ${PROJECT_SOURCE_DIR}/tiff/libtiff
    ${PROJECT_SOURCE_DIR}/freedcam
)

add_library(tiff SHARED
        tiff/libtiff/mkg3states.c
        tiff/libtiff/tif_aux.c
        tiff/libtiff/tif_close.c
        tiff/libtiff/tif_codec.c
        tiff/libtiff/tif_color.c
        tiff/libtiff/tif_compress.c
        tiff/libtiff/tif_dir.c
        tiff/libtiff/tif_dirinfo.c
        tiff/libtiff/tif_dirread.c
        tiff/libtiff/tif_dirwrite.c
        tiff/libtiff/tif_dumpmode.c
        tiff/libtiff/tif_error.c
        tiff/libtiff/tif_extension.c
        tiff/libtiff/tif_fax3.c
        tiff/libtiff/tif_fax3sm.c
        tiff/libtiff/tif_flush.c
        tiff/libtiff/tif_getimage.c
        tiff/libtiff/tif_jbig.c
        tiff/libtiff/tif_jpeg.c
        tiff/libtiff/tif_jpeg_12.c
        tiff/libtiff/tif_luv.c
        tiff/libtiff/tif_lzma.c
        tiff/libtiff/tif_lzw.c
        tiff/libtiff/tif_next.c
        tiff/libtiff/tif_ojpeg.c
        tiff/libtiff/tif_open.c
        tiff/libtiff/tif_packbits.c
        tiff/libtiff/tif_pixarlog.c
        tiff/libtiff/tif_predict.c
        tiff/libtiff/tif_print.c
        tiff/libtiff/tif_read.c
        tiff/libtiff/tif_strip.c
        tiff/libtiff/tif_swab.c
        tiff/libtiff/tif_thunder.c
        tiff/libtiff/tif_tile.c
        tiff/libtiff/tif_unix.c
        tiff/libtiff/tif_version.c
        tiff/libtiff/tif_warning.c
        tiff/libtiff/tif_write.c
        tiff/libtiff/tif_zip.c
)

# Link external libraries to libtiff if necessary
target_link_libraries(tiff PRIVATE log)

# Create shared library for libraw
add_library(libraw SHARED
    LibRaw/swab.cpp
    LibRaw/src/libraw_cxx.cpp
    LibRaw/src/libraw_c_api.cpp
    LibRaw/src/libraw_datastream.cpp
    LibRaw/internal/dcraw_common.cpp
    LibRaw/internal/dcraw_fileio.cpp
    LibRaw/internal/demosaic_packs.cpp
)

# Link external libraries to libraw if necessary
target_link_libraries(libraw PRIVATE log)

# Create shared library for freedcam
add_library(freedcam SHARED
    freedcam/RawToDng.cpp
    freedcam/DngWriter.cpp
    freedcam/DngStacker.cpp
    freedcam/ExifInfo.cpp
    freedcam/GpsInfo.cpp
    freedcam/DngProfile.cpp
    freedcam/CustomMatrix.cpp
    freedcam/OpCode.cpp
    freedcam/LibRawWrapper.cpp
    freedcam/LibRawJniWrapper.cpp
    freedcam/lj92.cpp
)

# Link libraries to freedcam
target_link_libraries(freedcam PRIVATE tiff libraw jnigraphics android log)

# Set output directory (Gradle will handle placement)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/obj/local/${ANDROID_ABI})
