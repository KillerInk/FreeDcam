cmake_minimum_required(VERSION 3.1)

if(NOT HALIDE_DIR)
	if($ENV{HALIDE_DIR})
			set(HALIDE_DIR, $ENV{HALIDE_DIR})
	else()
			message(FATAL_ERROR "HALIDE_DIR not set" )
	endif()
endif()


if(HOST_CLANG)
	set(hlPipelineCompiler "${HOST_CLANG}")
elseif(ENV{HOST_CLANG})
	set(hlPipelineCompiler "$ENV{HOST_CLANG}")
elseif(CLANG)
	set(hlPipelineCompiler "${CLANG}")
elseif(ENV{CLANG})
	set(hlPipelineCompiler "$ENV{CLANG}")
else()
	message(FATAL_ERROR "HOST_CLANG not set" )
endif()


if(NOT CMAKE_HOST_WIN32)
	set(hlPipelineCompilerFlags "-std=c++11 -lpthread -ldl -lz")
	set(HALIDE_LIB_PATH "${HALIDE_DIR}/bin/Release/libHalide.so")
else()
	set(hlPipelineCompilerFlags "-std=c++14")
	set(HALIDE_LIB_PATH "${HALIDE_DIR}/bin/Release/Halide.dll")
endif()


if(WIN32)
	set(hlPipelineLibSuffix ".lib")
else()
	set(hlPipelineLibSuffix ".a")
endif()

# map CMAKE_SYSTEM_PROCESSOR to HL_TARGET `arch-bits`
if(${CMAKE_SYSTEM_PROCESSOR} STREQUAL "aarch64")
	set(hlArchBits "arm-64")
elseif(${CMAKE_SYSTEM_PROCESSOR} STREQUAL "armv7-a")
    set(hlArchBits "arm-32")
elseif(${CMAKE_SYSTEM_PROCESSOR} STREQUAL "x86_64")
	set(hlArchBits "x86-64")
elseif(${CMAKE_SYSTEM_PROCESSOR} STREQUAL AMD64)
	set(hlArchBits "x86-64")
else()
	message(FATAL_ERROR "unsupported arch '${CMAKE_SYSTEM_PROCESSOR}'")
endif()

if(ANDROID)
	set(hlTarget "${hlArchBits}-android")
else()
	set(hlTarget ${hlArchBits}) # using host OS
endif()


get_filename_component(hlPipelineSourcesDir ${CMAKE_CURRENT_LIST_DIR} ABSOLUTE)

file(COPY ${HALIDE_LIB_PATH} DESTINATION ${CMAKE_CURRENT_LIST_DIR})
file(MAKE_DIRECTORY "gen")
file(WRITE gen/tool.cpp "#include \"Halide.h\"\nint main(int argc,char **argv){return Halide::Internal::generate_filter_main(argc, argv, std::cerr);}")
file(WRITE gen/dummy.c "int hl_pipelines_dummy_noop() {return 0;}")
file(MAKE_DIRECTORY "${hlPipelineSourcesDir}/${hlTarget}")



# make sure headers are generated before building:
add_custom_target(hl_pipelines_headers ALL)

set(hlPipelineLibs "")
set(hlPipelineHeaders "")

file(GLOB hlPipelineSources *.cpp)
foreach(source IN LISTS hlPipelineSources)
    get_filename_component(obj ${source} NAME_WE)
    set(objLib ${hlPipelineSourcesDir}/${hlTarget}/${obj}${hlPipelineLibSuffix})
	set(objHeader ${hlPipelineSourcesDir}/${hlTarget}/${obj}.h)
    list(APPEND hlPipelineLibs ${objLib})
	list(APPEND hlPipelineHeaders ${objHeader})

    if(CMAKE_HOST_WIN32)
		set(objBin ${obj}_gen.exe)
		set(runCmd ${hlPipelineSourcesDir}/run_gen.cmd ${hlTarget} ${objBin})
	else()
		set(objBin ${obj}_gen)
		set(runCmd HL_TARGET=${hlTarget} ${objBin})
	endif()

	set(compileGenArgs ${source} gen/tool.cpp
		-I${HALIDE_DIR}/include -L${HALIDE_DIR}/lib -L${HALIDE_DIR}/lib/Release -lHalide ${hlPipelineCompilerFlags} -o ${objBin})


	# do 2 things (if pipeline source changed)
	# 1. compile generator to host-executable generator
	# 2. run the generator
	add_custom_command(
		OUTPUT ${objLib} ${objHeader}
		COMMAND ${hlPipelineCompiler} ARGS ${compileGenArgs}
		COMMAND ${objBin} -g ${obj} -o ${hlTarget} target=${hlTarget}
		DEPENDS ${source}
		WORKING_DIRECTORY "${hlPipelineSourcesDir}"
		COMMENT "running ${obj} pipeline generator"
	)

	# This only runs if the pipeline header does not exist yet so build does not fail
	# `echo "" < ${obj}.h` is a portable file exists check
	add_custom_command(
		TARGET hl_pipelines_headers PRE_BUILD
		COMMAND echo "" < ${obj}.h || (${hlPipelineCompiler} ${compileGenArgs} && ${objBin} -g ${obj} -o ${hlTarget} target=${hlTarget} && ${CMAKE_COMMAND} -E copy ${hlTarget}/${obj}.h ${obj}.h)
		WORKING_DIRECTORY "${hlPipelineSourcesDir}"
		COMMENT "running ${obj} pipeline generator (header)"
	)

endforeach()



# we but the header here for dependencies
add_library(hl_pipelines STATIC gen/dummy.c ${hlPipelineHeaders})
target_link_libraries(hl_pipelines ${hlPipelineLibs})
set_target_properties(hl_pipelines PROPERTIES LINKER_LANGUAGE CXX)

add_dependencies(hl_pipelines hl_pipelines_headers)


# show the pipeline sources in the IDE (nothing should link against this)
add_library(hl_pipeline_sources SHARED EXCLUDE_FROM_ALL ${hlPipelineSources})
set_source_files_properties(${hlPipelineSources} PROPERTIES HEADER_FILE_ONLY TRUE)
include_directories(${HALIDE_DIR}/include )