#pragma version(1)
#pragma rs java_package_name(freed.renderscript)
#pragma rs_fp_relaxed

int factor = 2;
int maxclip = 253;

uchar4 __attribute__((kernel)) processClipping(uchar4 in, uint32_t x, uint32_t y) {

    uchar4 rgb = in;
    if(rgb.r >=maxclip && rgb.g >= maxclip && rgb.b >= maxclip)
    {
        if(x & 10)
            rgb.r =255;
        else
        {
            rgb.r =255; rgb.g = 0;  rgb.b = 0;
        }
    }
    if(rgb.r <=factor && rgb.g <= factor && rgb.b <= factor)
    {
        if(x & 10){
            rgb.b = factor;
        }
        else
        {
            rgb.r =100; rgb.g = 100;  rgb.b = 255;
        }
    }
    return rgb;
}