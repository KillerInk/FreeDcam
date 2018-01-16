#pragma version(1)
    #pragma rs java_package_name(freed.utils)
    #pragma rs_fp_relaxed

    rs_allocation input;
    bool peak;

    uchar4 __attribute__((kernel)) focuspeak(uint32_t x, uint32_t y) {
        uchar4 curPixel = rsGetElementAt_uchar4(input, x, y);

        int dx = x + ((x == 0) ? 1 : -1);
        //rsDebug("dx", dx);
        int sum = 0;
        uchar4 tmpPix = rsGetElementAt_uchar4(input, dx, y);
        int tmp;
        tmp = tmpPix.r - curPixel.r;
        sum += tmp * tmp;
        tmp = tmpPix.g - curPixel.g;
        sum += tmp * tmp;
        tmp = tmpPix.b - curPixel.b;
        sum += tmp * tmp;

        int dy = y + ((y == 0) ? 1 : -1);
        tmpPix = rsGetElementAt_uchar4(input, x, dy);
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
        rgb.r = mergedPixel.r; //+ sum;
        rgb.g = mergedPixel.g + sum;
        rgb.b = mergedPixel.b ;//+ sum;
        rgb.a = 255;
        rgb.r = ( rgb.r > 255 )? 255 : (( rgb.r < 0 )? 0 : rgb.r);
        rgb.g = ( rgb.g > 255 )? 255 : (( rgb.g < 0 )? 0 : rgb.g);
        rgb.b = ( rgb.b > 255 )? 255 : (( rgb.b < 0 )? 0 : rgb.b);

        //rsDebug("rgb", rgb);
        uchar4 out = convert_uchar4(rgb);
        return out;
    }