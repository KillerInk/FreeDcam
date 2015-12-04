#pragma version(1)
    #pragma rs java_package_name(troop.com.imageconverter)
    #pragma rs_fp_relaxed
    rs_allocation gCurrentFrame;
    rs_allocation gLastFrame;
    uchar4 __attribute__((kernel)) stackimage(uint32_t x, uint32_t y) {
        uchar4 curPixel, lastPixel;
        curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
        lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
        int4 rgb;
        //rsDebug("curPixel", curPixel);
        //rsDebug("lastPixel", lastPixel);
        rgb.r = (curPixel.r + lastPixel.r);
        rgb.g = (curPixel.g + lastPixel.b);
        rgb.b = (curPixel.b + lastPixel.b);
        rgb.a = 255;
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;

        //rsDebug("rgb", rgb);
        uchar4 out = convert_uchar4(rgb);
        return out;
    }