#pragma version(1)
    #pragma rs java_package_name(freed.utils)
    #pragma rs_fp_relaxed
    rs_allocation gCurrentFrame;
    uchar4 __attribute__((kernel)) peak(uint32_t x, uint32_t y) {
        uchar4 curPixel;
        curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
        //rsDebug("CurPixel", curPixel);

        int dx = x + ((x == 0) ? 1 : -1);
        //rsDebug("dx", dx);
        int sum = 0;
        uchar4 tmpPix = rsGetElementAt_uchar4(gCurrentFrame, dx, y);
        int tmp;
        tmp = tmpPix.r - curPixel.r;
        sum += tmp * tmp;
        tmp = tmpPix.g - curPixel.g;
        sum += tmp * tmp;
        tmp = tmpPix.b - curPixel.b;
        sum += tmp * tmp;

        int dy = y + ((y == 0) ? 1 : -1);
        tmpPix = rsGetElementAt_uchar4(gCurrentFrame, x, dy);
        tmp = tmpPix.r - curPixel.r;
        sum += tmp * tmp;
        tmp = tmpPix.g - curPixel.g;
        sum += tmp * tmp;
        tmp = tmpPix.b - curPixel.b;
        sum += tmp * tmp;

        sum >>= 9;
        sum *= sum * sum;
        int4 rgb;
        uchar4 mergedPixel = curPixel;
        //rsDebug("curPixel", curPixel);
        rgb.r = mergedPixel.r  + sum;
        rgb.g = mergedPixel.g + sum;
        rgb.b = mergedPixel.b + sum;
        rgb.a = 255;
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;

        //rsDebug("rgb", rgb);
        uchar4 out = convert_uchar4(rgb);
        return out;
    }