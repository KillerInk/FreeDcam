cmake_minimum_required(VERSION 3.6)
project(freedcam VERSION 1.0.0 LANGUAGES CXX)
set(CMAKE_CXX_STANDARD 11)
SET(MY_C_AND_CXX_FLAGS  -Werror, -Wdeprecated-register, -Wc++11-narrowing, -Wconstant-conversion)

SET(MY_C_FLAGS   ${MY_C_FLAGS}   ${MY_C_AND_CXX_FLAGS})
SET(MY_CXX_FLAGS ${MY_CXX_FLAGS} ${MY_C_AND_CXX_FLAGS})

set(freedcam_dir src/main/jni/freedcam)



include_directories(
        ${freedcam_dir}
)

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
find_package(libraw REQUIRED CONFIG)
target_link_libraries(freedcam jnigraphics android log libraw::raw libtiff::tiff)