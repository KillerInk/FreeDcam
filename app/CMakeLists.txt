cmake_minimum_required(VERSION 3.6)
project(freedcam VERSION 1.0.0 LANGUAGES CXX)
set(CMAKE_CXX_STANDARD 11)
SET(MY_C_AND_CXX_FLAGS  -Werror, -Wdeprecated-register, -Wc++11-narrowing, -Wconstant-conversion)

SET(MY_C_FLAGS   ${MY_C_FLAGS}   ${MY_C_AND_CXX_FLAGS})
SET(MY_CXX_FLAGS ${MY_CXX_FLAGS} ${MY_C_AND_CXX_FLAGS})

set(libraw_dir src/main/jni/LibRaw)
set(freedcam_dir src/main/jni/freedcam)



include_directories(
        ${libraw_dir}
        ${libraw_dir}/libraw
        ${freedcam_dir}
)

add_library(raw SHARED
        ${libraw_dir}/swab.cpp
        ${libraw_dir}/internal/dcraw_common.cpp
        ${libraw_dir}/internal/dcraw_fileio.cpp
        ${libraw_dir}/internal/demosaic_packs.cpp
        ${libraw_dir}/src/libraw_cxx.cpp
        ${libraw_dir}/src/libraw_c_api.cpp
        ${libraw_dir}/src/libraw_datastream.cpp
        )
#set_target_properties(raw PROPERTIES LINKER_LANGUAGE CXX)

add_library(freedcam SHARED
        ${freedcam_dir}/RawToDng.cpp
        ${freedcam_dir}/LibRawWrapper.cpp
        ${freedcam_dir}/LibRawJniWrapper.cpp
        ${freedcam_dir}/DngWriter.cpp
        ${freedcam_dir}/DngStacker.cpp
        ${freedcam_dir}/ExifInfo.cpp
        ${freedcam_dir}/GpsInfo.cpp
        ${freedcam_dir}/DngProfile.cpp
        ${freedcam_dir}/CustomMatrix.cpp
        )
#set_target_properties(freedcam PROPERTIES LINKER_LANGUAGE CXX)


find_package(libtiff REQUIRED CONFIG)
target_link_libraries(freedcam jnigraphics android log raw libtiff::tiff)