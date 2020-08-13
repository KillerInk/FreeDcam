#ifndef HDRPLUS_FINISH_H_
#define HDRPLUS_FINISH_H_

#include "Halide.h"

/*
* finish -- Applies a series of standard local and global image processing
* operations to an input mosaicked image, producing a pleasant color output.
* Input pecifies black-level, white-level and white balance. Additionally,
* tone mapping is applied to the image, as specified by the input compression
* and gain amounts. This produces natural-looking brightened shadows, without
* blowing out highlights. The output values are 8-bit.
*/
Halide::Func finish(Halide::ImageParam input, Halide::Param<int> bp, Halide::Param<int> wp, Halide::Param<float> wb_r, Halide::Param<float> wb_g1, Halide::Param<float> wb_g2, Halide::Param<float> wb_b, Halide::Param<float> compression, Halide::Param<float> gain);

#endif