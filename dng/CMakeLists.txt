cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 11)


include_directories(
                    src/main/jni/freedcam
                   )

add_library(tiff STATIC
                    src/main/jni/tiff/libtiff/mkg3states.c
                    src/main/jni/tiff/libtiff/tif_aux.c
                    src/main/jni/tiff/libtiff/tif_close.c
                    src/main/jni/tiff/libtiff/tif_codec.c
                    src/main/jni/tiff/libtiff/tif_color.c
                    src/main/jni/tiff/libtiff/tif_compress.c
                    src/main/jni/tiff/libtiff/tif_dir.c
                    src/main/jni/tiff/libtiff/tif_dirinfo.c
                    src/main/jni/tiff/libtiff/tif_dirread.c
                    src/main/jni/tiff/libtiff/tif_dirwrite.c
                    src/main/jni/tiff/libtiff/tif_dumpmode.c
                    src/main/jni/tiff/libtiff/tif_error.c
                    src/main/jni/tiff/libtiff/tif_extension.c
                    src/main/jni/tiff/libtiff/tif_fax3.c
                    src/main/jni/tiff/libtiff/tif_fax3sm.c
                    src/main/jni/tiff/libtiff/tif_flush.c
                    src/main/jni/tiff/libtiff/tif_getimage.c
                    src/main/jni/tiff/libtiff/tif_jbig.c
                    src/main/jni/tiff/libtiff/tif_jpeg.c
                    src/main/jni/tiff/libtiff/tif_jpeg_12.c
                    src/main/jni/tiff/libtiff/tif_luv.c
                    src/main/jni/tiff/libtiff/tif_lzma.c
                    src/main/jni/tiff/libtiff/tif_lzw.c
                    src/main/jni/tiff/libtiff/tif_next.c
                    src/main/jni/tiff/libtiff/tif_ojpeg.c
                    src/main/jni/tiff/libtiff/tif_open.c
                    src/main/jni/tiff/libtiff/tif_packbits.c
                    src/main/jni/tiff/libtiff/tif_pixarlog.c
                    src/main/jni/tiff/libtiff/tif_predict.c
                    src/main/jni/tiff/libtiff/tif_print.c
                    src/main/jni/tiff/libtiff/tif_read.c
                    src/main/jni/tiff/libtiff/tif_strip.c
                    src/main/jni/tiff/libtiff/tif_swab.c
                    src/main/jni/tiff/libtiff/tif_thunder.c
                    src/main/jni/tiff/libtiff/tif_tile.c
                    src/main/jni/tiff/libtiff/tif_unix.c
                    src/main/jni/tiff/libtiff/tif_version.c
                    src/main/jni/tiff/libtiff/tif_warning.c
                    src/main/jni/tiff/libtiff/tif_write.c
                    src/main/jni/tiff/libtiff/tif_zip.c
                    )




add_library(freedcam SHARED    src/main/jni/freedcam/RawToDng.cpp
                              src/main/jni/freedcam/DngWriter.cpp
                              src/main/jni/freedcam/DngStacker.cpp
                              src/main/jni/freedcam/ExifInfo.cpp
                              src/main/jni/freedcam/GpsInfo.cpp
                              src/main/jni/freedcam/DngProfile.cpp
                              src/main/jni/freedcam/CustomMatrix.cpp
)


target_link_libraries(freedcam jnigraphics android log tiff)