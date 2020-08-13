
#include "Halide.h"
#include "hdrplus/align.h"
#include "hdrplus/merge.h"
#include "hdrplus/finish.h"
#include <stdlib.h>
#include <cstdio>
#include <string>

//#include "finish.h"
using namespace Halide;

Func alignmerge(ImageParam imgs, ImageParam imgs2, Param<int> minoffset, Param<int> maxoffset, Param<int> l1mindistance, Param<int> l1maxdistance)
{
	Func alignment = align(imgs);
	return merge(imgs2, alignment, minoffset, maxoffset,l1mindistance,l1maxdistance);
}

Func align_merge(ImageParam imgs, Param<int> minoffset, Param<int> maxoffset, Param<int> l1mindistance, Param<int> l1maxdistance)
{
	Func alignment = align(imgs);
	return merge(imgs, alignment, minoffset, maxoffset, l1mindistance, l1maxdistance);
}

int main(int argc, char* argv[]) {

    int xOs = 32;

    if(argc > 1)
        xOs = 64;



    std::cout << xOs <<std::endl;
	ImageParam imgs(type_of<uint16_t>(), 3);
	ImageParam alignImgs(type_of<uint16_t>(), 3);
	Param<int> minoffset;
	Param<int> maxoffset;
	Param<int> l1mindistance;
	Param<int> l1maxdistance;
	//Func alignbuf;
	//ImageParam alignbuf(type_of<uint16_t>(), 3);

	Target target;
	target.os = Target::Android; // The operating system
	target.arch = Target::ARM;   // The CPU architecture
	target.bits = xOs;            // The bit-width of the architecture
	std::vector<Target::Feature> arm_features; // A list of features to set
	//arm_features.push_back(Target::LargeBuffers);
	//arm_features.push_back(Target::NoNEON);
	target.set_features(arm_features);

	//Func alignment = align(imgs);
	Func stage1_align_merge = align_merge(imgs,minoffset, maxoffset,l1mindistance,l1maxdistance);
	std::vector<Argument> argss(5);
	argss[0] = imgs;
	argss[1] = minoffset;
	argss[2] = maxoffset;
	argss[3] = l1mindistance;
	argss[4] = l1maxdistance;


	stage1_align_merge.compile_to_static_library("stage1_align_merge", {  argss }, "stage1_align_merge", target);

    Func stage1_alignmerge = alignmerge(imgs, alignImgs,minoffset, maxoffset,l1mindistance,l1maxdistance);
    std::vector<Argument> args(6);
    args[0] = imgs;
    args[1] = alignImgs;
    args[2] = minoffset;
    args[3] = maxoffset;
    args[4] = l1mindistance;
    args[5] = l1maxdistance;
    stage1_alignmerge.compile_to_static_library("stage1_alignmerge", {  args }, "stage1_alignmerge", target);

	//target.bits = 32;
	//stage1_alignmerge.compile_to_static_library("../../../libs/armeabi-v7a", {  argss }, "stage1_align_merge", target);

	ImageParam input2(type_of<uint16_t>(), 2);
	Param<int> blackpoint;
	Param<int> whitepoint;
	Param<float> wb_r;
	Param<float> wb_g1;
	Param<float> wb_g2;
	Param<float> wb_b;
	Param<float> compression;
	Param<float> gain;

	Func stage2_RawToRgb = finish(input2, blackpoint, whitepoint, wb_r,wb_g1,wb_g2,wb_b, compression, gain);
	std::vector<Argument> args2(9);
	args2[0] = input2;
	args2[1] = blackpoint;
	args2[2] = whitepoint;
	args2[3] = wb_r;
	args2[4] = wb_g1;
	args2[5] = wb_g2;
	args2[6] = wb_b;
	args2[7] = compression;
	args2[8] = gain;

	stage2_RawToRgb.compile_to_static_library("stage2_RawToRgb", { args2 }, "stage2_RawToRgb", target);

	return 0;
}

