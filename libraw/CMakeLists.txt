cmake_minimum_required(VERSION 3.6)
set(CMAKE_CXX_STANDARD 11)
SET(MY_C_AND_CXX_FLAGS  -Werror, -Wdeprecated-register, -Wc++11-narrowing, -Wconstant-conversion)

SET(MY_C_FLAGS   ${MY_C_FLAGS}   ${MY_C_AND_CXX_FLAGS})
SET(MY_CXX_FLAGS ${MY_CXX_FLAGS} ${MY_C_AND_CXX_FLAGS})

set(libraw_dir src/main/cpp/LibRaw)



include_directories(
        ${libraw_dir}
        ${libraw_dir}/libraw
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

target_link_libraries(raw)