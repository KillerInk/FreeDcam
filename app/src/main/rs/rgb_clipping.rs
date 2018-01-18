#pragma version(1)
#pragma rs java_package_name(freed.renderscript)
#pragma rs_fp_relaxed

int factor = 2;

uchar4 __attribute__((kernel)) processClipping(uchar4 in, uint32_t x, uint32_t y) {

    uchar4 rgb = in;
    if(rgb.r >=255 -factor && rgb.g >= 255 -factor && rgb.b >= 255 -factor)
    {
        rgb.r =255; rgb.g = 0;  rgb.b = 0;
    }
    if(rgb.r <=0 +factor && rgb.g <= 0 +factor && rgb.b <= 0 +factor)
    {
        rgb.r =0; rgb.g = 0;  rgb.b = 255;
    }
    return rgb;
}