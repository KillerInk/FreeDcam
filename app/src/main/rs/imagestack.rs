#pragma version(1)
    #pragma rs java_package_name(com.imageconverter)
    #pragma rs_fp_relaxed

    typedef struct MinMaxPixel
        {
            uchar4 min;
            uchar4 max;
        } MinMaxPixel_t;

    MinMaxPixel_t *medianMinMaxPixel;
    rs_allocation gCurrentFrame;
    rs_allocation gLastFrame;



    uchar4 __attribute__((kernel)) stackimage_avarage(uint32_t x, uint32_t y) {
        uchar4 curPixel, lastPixel;
        curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
        lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
        uchar4 rgb;
        rgb.r = curPixel.r/2 + lastPixel.r/2;
        rgb.g = curPixel.g/2 + lastPixel.b/2;
        rgb.b = curPixel.b/2 + lastPixel.b/2;
        rgb.a = 255;
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        return rgb;
    }

    uchar4 __attribute__((kernel)) stackimage_lighten(uint32_t x, uint32_t y)
    {
        uchar4 curPixel, lastPixel;
        curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
        lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
        uchar4 rgb;

        if(curPixel.r > lastPixel.r && curPixel.g > lastPixel.g && curPixel.b > lastPixel.b)
            rgb = curPixel;
        else
            rgb = lastPixel;
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        return rgb;
    }

    uchar4 __attribute__((kernel)) stackimage_median(uint32_t x, uint32_t y)
        {
            uchar4 curPixel, lastPixel;
            curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
            struct MinMaxPixel t = medianMinMaxPixel[x+y];
            if(curPixel.r < t.min.r && curPixel.g < t.min.g && curPixel.b < t.min.b)
                t.min = curPixel;
            else if(curPixel.r > t.max.r && curPixel.g > t.max.g && curPixel.b > t.max.b);
                t.max = curPixel;
            uchar4 rgb = t.max - t.min;
            if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
            if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
            if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
            return rgb;
        }

