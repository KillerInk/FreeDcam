#ifndef HDRPLUS_MERGE_H_
#define HDRPLUS_MERGE_H_

#include "Halide.h"

/*
 * merge -- fully merges aligned frames in the temporal and spatial
 * dimension to produce one denoised bayer frame.
 */
Halide::Func merge(Halide::ImageParam imgs, Halide::Func alignment, Halide::Expr minoffset, Halide::Expr maxoffset, Halide::Expr l1mindist, Halide::Expr l1maxdist);
#endif