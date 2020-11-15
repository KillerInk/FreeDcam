cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 11)


set(libraw_dir src/main/jni/LibRaw)

include_directories(
                    ${libraw_dir}
                    ${libraw_dir}/libraw
                   )


add_library(raw STATIC
            ${libraw_dir}/swab.cpp
            ${libraw_dir}/internal/dcraw_common.cpp
            ${libraw_dir}/internal/dcraw_fileio.cpp
            ${libraw_dir}/internal/demosaic_packs.cpp
            ${libraw_dir}/src/libraw_cxx.cpp
            ${libraw_dir}/src/libraw_c_api.cpp
            ${libraw_dir}/src/libraw_datastream.cpp
            )

add_library(rawwrapper SHARED    src/main/jni/librawwrapper/librawwrapper.cpp
)

target_link_libraries(rawwrapper jnigraphics android log raw)