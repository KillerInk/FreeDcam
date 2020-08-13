#include "util.h"

#include "Halide.h"
#include <vector>
#include <algorithm>

using namespace Halide;
using namespace Halide::ConciseCasts;

/*
 * box_down2 -- averages 2x2 regions of an image to downsample linearly.
 */
Func box_down2(Func input, std::string name) {

    Func output(name);
    
    Var x, y, n;
    RDom r(0, 2, 0, 2);

    // output with box filter and stride 2

    output(x, y, n) = u16(sum(u32(input(2*x + r.x, 2*y + r.y, n))) / 4);

    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    output.compute_root().parallel(y).vectorize(x, 16);

    return output;
}

/*
 * gauss_down4 -- applies a 3x3 integer gauss kernel and downsamples an image by 4 in
 * one step.
 */
Func gauss_down4(Func input, std::string name) {

    Func output(name);
    Func k(name + "_filter");

    Var x, y, n;
    RDom r(-2, 5, -2, 5);

    // gaussian kernel

    k(x, y) = 0;

    k(-2,-2) = 2; k(-1,-2) =  4; k(0,-2) =  5; k(1,-2) =  4; k(2,-2) = 2;
    k(-2,-1) = 4; k(-1,-1) =  9; k(0,-1) = 12; k(1,-1) =  9; k(2,-1) = 4;
    k(-2, 0) = 5; k(-1, 0) = 12; k(0, 0) = 15; k(1, 0) = 12; k(2, 0) = 5;
    k(-2, 1) = 4; k(-1, 1) =  9; k(0, 1) = 12; k(1, 1) =  9; k(2, 1) = 4;
    k(-2, 2) = 2; k(-1, 2) =  4; k(0, 2) =  5; k(1, 2) =  4; k(2, 2) = 2;

    // output with applied kernel and stride 4

    output(x, y, n) = u16(sum(u32(input(4*x + r.x, 4*y + r.y, n) * k(r.x, r.y))) / 159);

    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    k.compute_root().parallel(y).parallel(x);

    output.compute_root().parallel(y).vectorize(x, 16);

    return output;
}

Func blurLinear(Func in)
{	
	Var x, y, _;
	Func blur_x;
	Func output;
	blur_x(x, y, _) = (in(x, y, _) + in(x + 1, y, _) + in(x + 2, y, _)) / 3;
	output(x, y, _) = (blur_x(x, y, _) + blur_x(x, y + 1, _) + blur_x(x, y + 2, _)) / 3;
	blur_x.compute_root().parallel(x).vectorize(y, 8);
	output.compute_root().parallel(y).vectorize(x, 8);
	
	return output;
}

/*
 * gauss_7x7 -- Applies a 7x7 gauss kernel with a std deviation of 4/3. Requires its input to handle boundaries.
 */
Func gauss(Func input, Func k, RDom r, std::string name) {

    Func blur_x(name + "_x");
    Func output(name);

    Var x, y, c;

    Expr val;

    if (input.dimensions() == 2) {

        blur_x(x, y) = sum(input(x + r, y) * k(r));

        val = sum(blur_x(x, y + r) * k(r));

        if (input.output_types()[0] == UInt(16)) val = u16(val);

        output(x, y) = val;
    }
    else {

        blur_x(x, y, c) = sum(input(x + r, y, c) * k(r));

        val = sum(blur_x(x, y + r, c) * k(r));

        if (input.output_types()[0] == UInt(16)) val = u16(val);

        output (x, y, c) = val;
    }

    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    Var xi, yi;

    blur_x.compute_at(output, x).vectorize(x, 16);

    output.compute_root().tile(x, y, xi, yi, 256, 128).vectorize(xi, 16).parallel(y);

    return output;
}

Func gauss_7x7(Func input, std::string name) {

    // gaussian kernel

    Func k("gauss_7x7_kernel");

    Var x;
    RDom r(-3, 7);

    k(x) = f32(0.f);

    k(-3) = 0.026267f; k(-2) = 0.100742f; k(-1) = 0.225511f; k(0) = 0.29496f; 
    k( 3) = 0.026267f; k( 2) = 0.100742f; k( 1) = 0.225511f;

    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    k.compute_root().parallel(x);

    return gauss(input, k, r, name);

}

Func gauss_15x15(Func input, std::string name) {

    // gaussian kernel

    Func k("gauss_7x7_kernel");

    Var x;
    RDom r(-7, 15);

    k(x) = f32(0.f);

    k(-7) = 0.004961f; k(-6) = 0.012246f; k(-5) = 0.026304f; k(-4) = 0.049165f; k(-3) = 0.079968f; k(-2) = 0.113193f; k(-1) = 0.139431f; k(0) = 0.149464f;
    k( 7) = 0.004961f; k( 6) = 0.012246f; k( 5) = 0.026304f; k( 4) = 0.049165f; k( 3) = 0.079968f; k( 2) = 0.113193f; k( 1) = 0.139431f; 

    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    k.compute_root().parallel(x);

    return gauss(input, k, r, name);
}

/*
 * diff -- Computes difference between two integer functions
 */
Func diff(Func im1, Func im2, std::string name) {

    Func output(name);

    Var x, y, c;

    if (im1.dimensions() == 2) {
        output(x,y) = i32(im1(x,y)) - i32(im2(x,y));
    } else {
        output(x,y,c) = i32(im1(x,y,c)) - i32(im2(x,y,c));
    }

    return output;
}

/*
 * gamma_correct -- Takes a single or multi-channel linear image and applies gamma correction
 * as described here: http://www.color.org/sRGB.xalter. See formulas 1.2a and 1.2b
 */
Func gamma_correct(Func input) {

    Func output("gamma_correct_output");

    Var x, y, c;

    // constants for gamma correction

    int cutoff = 200;                   // ceil(0.00304 * UINT16_MAX)
    float gamma_toe = 12.92;
    float gamma_pow = 0.416667;         // 1 / 2.4
    float gamma_fac = 680.552897;       // 1.055 * UINT16_MAX ^ (1 - gamma_pow);
    float gamma_con = -3604.425;        // -0.055 * UINT16_MAX

    if (input.dimensions() == 2) {
        output(x, y) = u16(select(input(x, y) < cutoff,
                            gamma_toe * input(x, y),
                            gamma_fac * pow(input(x, y), gamma_pow) + gamma_con));
    }
    else {
        output(x, y, c) = u16(select(input(x, y, c) < cutoff,
                            gamma_toe * input(x, y, c),
                            gamma_fac * pow(input(x, y, c), gamma_pow) + gamma_con));
    }

    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    output.compute_root().parallel(y).vectorize(x, 16);

    return output;
}

/*
 * gamma_inverse -- Takes a single or multi-channel image and undoes gamma correction to 
 * return in to linear RGB space.
 */
Func gamma_inverse(Func input) {

    Func output("gamma_inverse_output");

    Var x, y, c;

    // constants for inverse gamma correction

    int cutoff = 2575;                   // ceil(1/0.00304 * UINT16_MAX)
    float gamma_toe = 0.0774;            // 1 / 12.92
    float gamma_pow = 2.4;
    float gamma_fac = 57632.49226;       // 1 / 1.055 ^ gamma_pow * U_INT16_MAX;
    float gamma_con = 0.055;

    if (input.dimensions() == 2) {
        output(x, y) = u16(select(input(x, y) < cutoff,
                            gamma_toe * input(x, y),
                            pow(f32(input(x, y)) / 65535.f + gamma_con, gamma_pow) * gamma_fac));
    }
    else {
        output(x, y, c) = u16(select(input(x, y, c) < cutoff,
                            gamma_toe * input(x, y),
                            pow(f32(input(x, y, c)) / 65535.f + gamma_con, gamma_pow) * gamma_fac));
    }

    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    output.compute_root().parallel(y).vectorize(x, 16);

    return output;
}

/*
 * rgb_to_yuv -- converts a linear rgb image to a linear yuv image. Note that the output
 * is in float32
 */
Func rgb_to_yuv(Func input) {

    Func output("rgb_to_yuv_output");

    Var x, y, c;

    Expr r = input(x, y, 0);
    Expr g = input(x, y, 1);
    Expr b = input(x, y, 2);

    output(x, y, c) = f32(0);

    output(x, y, 0) =  0.298900f * r + 0.587000f * g + 0.114000f * b;           // Y
    output(x, y, 1) = -0.168935f * r - 0.331655f * g + 0.500590f * b;           // U
    output(x, y, 2) =  0.499813f * r - 0.418531f * g - 0.081282f * b;           // V
    
    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    output.compute_root().parallel(y).vectorize(x, 16);

    output.update(0).parallel(y).vectorize(x, 16);
    output.update(1).parallel(y).vectorize(x, 16);
    output.update(2).parallel(y).vectorize(x, 16);

    return output;
}

/*
 * yuv_to_rgb -- Converts a linear yuv image to a linear rgb image.
 */
Func yuv_to_rgb(Func input) {

    Func output("yuv_to_rgb_output");

    Var x, y, c;

    Expr Y = input(x, y, 0);
    Expr U = input(x, y, 1);
    Expr V = input(x, y, 2);

    output(x, y, c) = u16(0);

    output(x, y, 0) = u16_sat(Y + 1.403f * V            );          // r
    output(x, y, 1) = u16_sat(Y -  .344f * U - .714f * V);          // g
    output(x, y, 2) = u16_sat(Y + 1.770f * U            );          // b

    ///////////////////////////////////////////////////////////////////////////
    // schedule
    ///////////////////////////////////////////////////////////////////////////

    output.compute_root().parallel(y).vectorize(x, 16);

    output.update(0).parallel(y).vectorize(x, 16);
    output.update(1).parallel(y).vectorize(x, 16);
    output.update(2).parallel(y).vectorize(x, 16);

    return output;
}

// Those two parameters have critical imapct on performance
#define STRIP_WIDTH 4 // width of parallel computing strip
#define VEC_LEN 16 // in x86 structure(SSE instructions), may use length 4*N?

#define ITER 5 // iterating times for measuring timing

Func hazel_removal(ImageParam input)
{
	Var x, y, c;

	// set boundary condition
	Func clamped;
	Expr x_clamped = clamp(x, 0, input.width() - 1);
	Expr y_clamped = clamp(y, 0, input.height() - 1);

	// sqrt version does well with preserving color.
	// remove sqrt if so desire.
	clamped(x, y, c) = cast<float>(sqrt(input(x_clamped, y_clamped, c) / 255.0f));
	//clamped(x, y, c) = cast<float>(input(x_clamped, y_clamped, c)/255.0f);

	/*
	*  algorithm parts
	*/

	// find air light
	// 1. here will do better if use argmax to find most heavily hazy location
	//    this appoach will need find DCP to detect heaviliness of haze first.
	//
	// 2. Or if we can sort whole image, select top 10% brightest pixels and 
	//    calculate their mean to get a better airlight estimation.
	//
	// However, now we just use simplest way to estimate airlight
	// from brightest pixel for each channel

	/** TODO:need to use argmax to find brightest location... **/

	// use rough conversion for Y channel
	//Func lut; // use look up table for gamma correction
	//Var i;
	//lut(i) = clamp(pow(i/255.0f, 2.2f)*255.0f, 0.0f, 255.5f);

	//Func gray_img;
	//gray_img(x, y) = 0.299f*lut(clamped(x, y, 0)) + 0.587f*lut(clamped(x, y, 1)) + 0.114f*lut(clamped(x, y, 2));
	//gray_img(x, y) = clamped(x, y, 0)/3.0f + clamped(x, y, 1)/3.0f + clamped(x, y, 2)/3.0f;


	// find brightest pixel for each channel
	RDom box_wimg(0, input.width() - 1, 0, input.height() - 1);
	// TODO: this is a simplest but very rough estimation for airligh.
	// fix it using argmax as mentioned above
	Func airlight;
	airlight(c) = maximum(clamped(0 + box_wimg.x, 0 + box_wimg.y, c));

	// filter out airlight
	Func clamped_f_no_air, dcpf_p;
	clamped_f_no_air(x, y, c) = clamped(x, y, c) / airlight(c);
	// and find DCP we want
	dcpf_p(x, y) = min(clamped_f_no_air(x, y, 0), min(clamped_f_no_air(x, y, 1), clamped_f_no_air(x, y, 2)));

	// begin to estimate Adaptive Wiener filter parameters
	// TODO: actually, this filter step should be applied twice.
	// Re-write this step as function. But need to think about schedule
	int kernel_size = 21; // may have better way to determine, but roughly 21
	RDom box_wnr(-kernel_size / 2, kernel_size, -kernel_size / 2, kernel_size);

	// TODO: is there more compact implementation? it's too many Func I think...

	Func local_sum_dcp, mu_wnr; // for finding mu
	local_sum_dcp(x, y) = 0.0f;
	local_sum_dcp(x, y) += dcpf_p(x + box_wnr.x, y + box_wnr.y);
	mu_wnr(x, y) = local_sum_dcp(x, y) / (float)(kernel_size*kernel_size);

	Func dcpf2, local_sum2_dcp, sigma2_wnr; // for calculating sigma2
	dcpf2(x, y) = dcpf_p(x, y)*dcpf_p(x, y);
	local_sum2_dcp(x, y) = 0.0f;
	local_sum2_dcp(x, y) += dcpf2(x + box_wnr.x, y + box_wnr.y);
	sigma2_wnr(x, y) = max(local_sum2_dcp(x, y) / (float)(kernel_size*kernel_size) - mu_wnr(x, y)*mu_wnr(x, y), 0.0f);

	// begin to filter DCP (dcpf_p) with adaptive wiener filter
	//TODO: is this a practical implementation to find sigma2_n?
	Func sigma2_n;
	Var dummy;
	sigma2_n(dummy) = 0.0f;
	sigma2_n(0) = sum(sigma2_wnr(0 + box_wimg.x, 0 + box_wimg.y)) / (input.width()*input.height());

	Func weight;
	weight(x, y) = max((sigma2_wnr(x, y) - sigma2_n(0)), 0.0f) / max(sigma2_n(0), sigma2_wnr(x, y));
	Func dcpf_refine;
	dcpf_refine(x, y) = mu_wnr(x, y) + weight(x, y)*(dcpf_p(x, y) - mu_wnr(x, y));

	// get transmission map
	Func t_map;
	t_map(x, y) = clamp(1.0f - 0.95f*dcpf_refine(x, y), 0.01f, 1.0f);

	// recover image
	Func restore_img;
	restore_img(x, y, c) = cast<uint8_t>(clamp((clamped(x, y, c) - airlight(c)) / t_map(x, y) + airlight(c), 0.0f, 1.0f)*255.0f);


	/*
	* Schedule Parts
	*/
	// TODO: need to experiment various schedule for haze removal

	//    
	// begin to filter DCP with adaptive wiener filter
	// from bottom(restore_img) to up(dcp_f) schduling
	//

	// Now we can sigma2_n.compute_root() and do parallel both at restore_img and inside sigma2_n
	// It's becuase calculating sigma2_n has RDom box_wimg. So to avoid to scan whole
	// image for every parallel for-loop, we need sigma2_wnr and sigma2_n stored at root.
	//
	// Strategy one: divide algorithm to 2 parts:
	// Part1 for getting all wienre filter paramters and store at root. Parallel can be applied when 
	// calculating paramters.
	// Part2 for restoring image. Parallel can be applied, too.

	/* restore_img */
	// compute color channels at innermost for-loopt
	restore_img.reorder(c, x, y)
		.bound(c, 0, 3)
		.unroll(c);

	// Parallel here require at least sigma2_n stored at root.
	// Or we may need to scan whole image for every parallel for-loop because sigma2_n need whole simga2_wnr.
	// to be faster, it'll be ok to store mu_wnr, sigma2_wnr at root. But for saving memory, it's also ok to compute 
	// sigma2_wnr and mu_wnr here again. Note that we have already calculated sigma2_wnr and mu_wnr one time for getting
	// sigma2_n

	restore_img.parallel(y);
	restore_img.vectorize(x, VEC_LEN);

	/* clapmed, airlight, t_map */
	// No neighbor computing in path t_map->dcpf_refine->weight, so all inlined into restore_img

	// clamped is used almost all stages. Even airlight needs WHOLE clamped, so compute_root()
	clamped.reorder(c, x, y)
		.bound(c, 0, 3)
		.unroll(c);
	clamped.parallel(y);
	clamped.vectorize(x, VEC_LEN);
	clamped.compute_root();
	// TODO: test no reorder 'c' version. Then we can use airlight.compute_at(restore_img, c)
	airlight.parallel(c);
	airlight.compute_root();

	/* dcpf_p */
	dcpf_p.parallel(y);
	dcpf_p.vectorize(x, VEC_LEN);
	dcpf_p.compute_root();

	/* clamped_f_no_air */
	// this is only used in calculating dcpf_p. No need to store
	// and keep inline

	//
	// begin to extract wiener filter paramters. 
	// from bottom(sigma2_n) to up(local_sum_dcp and local_sum2_dcp)
	//

	Var yi, yo; // for neighboring computing
				/* sigma2_n */
	sigma2_n.compute_root();

	/* sigma2_wnr */
	/*
	sigma2_wnr.split(y, yo, yi, STRIP_WIDTH).parallel(yo);
	sigma2_wnr.vectorize(x, VEC_LEN);
	sigma2_wnr.compute_root();
	*/
	// test all compute_root()
	sigma2_wnr.compute_root().parallel(y).vectorize(x, VEC_LEN);

	/* local_sum2_dcp */
	/*
	local_sum2_dcp.store_at(sigma2_wnr, yo).compute_at(sigma2_wnr, yi);
	local_sum2_dcp.vectorize(x, VEC_LEN);
	local_sum2_dcp.update(0).split(y, yo, yi, STRIP_WIDTH);
	local_sum2_dcp.update(0).vectorize(x, VEC_LEN);
	*/
	// test all compute_root()
	local_sum2_dcp.compute_root().parallel(y).vectorize(x, VEC_LEN);
	local_sum2_dcp.update(0).parallel(y).vectorize(x, VEC_LEN);
	/* dcpf2 */
	/*
	dcpf2.store_at(local_sum2_dcp, yo).compute_at(local_sum2_dcp, yi);
	dcpf2.vectorize(x, VEC_LEN);
	*/
	// test all compute_root()
	dcpf2.compute_root().parallel(y).vectorize(x, VEC_LEN);

	/* mu_wnr */
	/*
	mu_wnr.split(y, yo, yi, STRIP_WIDTH).parallel(yo);
	mu_wnr.vectorize(x, VEC_LEN);
	mu_wnr.compute_root();
	*/
	// test all compute_root()
	mu_wnr.compute_root().parallel(y).vectorize(x, VEC_LEN);
	/* local_sum_dcp */
	/*
	local_sum_dcp.store_at(mu_wnr, yo).compute_at(mu_wnr, yi);
	local_sum_dcp.vectorize(x, VEC_LEN);
	local_sum_dcp.update(0).vectorize(x, VEC_LEN);
	*/
	// test all compute_root()
	local_sum_dcp.compute_root().parallel(y).vectorize(x, VEC_LEN);
	local_sum_dcp.update(0).parallel(y).vectorize(x, VEC_LEN);

	/*
	* AOT generate
	*/

	//std::vector<Argument> args(1);
	//args[0] = input;
	return restore_img;
	//restore_img.compile_to_file("halide_haze_removal", args);

}