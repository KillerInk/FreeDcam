#pragma version(1)
    #pragma rs java_package_name(com.imageconverter)
    #pragma rs_fp_relaxed
    rs_allocation gCurrentFrame;
    rs_allocation gLastFrame;
    uchar4 __attribute__((kernel)) stackimage(uint32_t x, uint32_t y) {
        uchar4 lastPixel;
        uchar3 curPixel;
        //rsDebug("x/y", x,y);
        curPixel = rsGetElementAt_uchar3(gCurrentFrame, x, y);
        //rsDebug("curPixel", curPixel);
        lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
        //rsDebug("lastPixel", lastPixel);
        int4 rgb;
        rgb.r = (curPixel.r + lastPixel.r)/2;
        rgb.g = (curPixel.g + lastPixel.b)/2;
        rgb.b = (curPixel.b + lastPixel.b)/2;
        rgb.a = 255;
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;


        uchar4 out = convert_uchar4(rgb);
        //rsDebug("out Pixel", out);
        return out;
    }