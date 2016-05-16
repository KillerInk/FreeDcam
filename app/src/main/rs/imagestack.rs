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
    int Width;
    int Height;



    uchar4 __attribute__((kernel)) stackimage_avarage(uint32_t x, uint32_t y) {
        float4 curPixel, lastPixel, merged;
        uchar4 rgb;
        curPixel = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y));
        lastPixel = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y));
        merged = (curPixel + lastPixel)/2;
        rgb = rsPackColorTo8888(merged);
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        return rgb;
    }

    uchar4 __attribute__((kernel)) stackimage_avarage2x1(uint32_t x, uint32_t y)
    {
        float4 curPixel, lastPixel, curPixel1,lastPixel1,merged;
        uchar4 rgb;
        if(x < Width)
        {
            curPixel = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y));
            lastPixel = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y));
            curPixel1 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x+1, y));
            lastPixel1 = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x+1, y));

            merged = (curPixel + lastPixel + curPixel1 + lastPixel1)/4;
            rgb = rsPackColorTo8888(merged);
        }
        else
        {
            curPixel = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y));
            lastPixel = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y));
            rgb = rsPackColorTo8888(merged);
        }
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        return rgb;
    }

    uchar4 __attribute__((kernel)) stackimage_avarage2x2(uint32_t x, uint32_t y)
    {
        uchar4 curPixel, lastPixel, curPixelX1,lastPixelX1,curPixelY1,lastPixelY1;
        float4 cPix, lPix, cPixX1,lPixX1, cPixY1, lPixY1 , mergedPix;

        uchar4 rgb =rsGetElementAt_uchar4(gCurrentFrame, x, y);
        //rsDebug("Width", x);
        if(x+1 < Width && y+1 < Height)
        {
            cPix = rsUnpackColor8888(rgb);
            lPix = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y));
            cPixX1 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x+1, y));
            lPixX1 = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x+1, y));
            cPixY1 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y+1));
            lPixY1 = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y+1));
            mergedPix = (cPix + lPix + cPixX1 + lPixX1 + cPixY1 + lPixY1)/6;
            rgb = rsPackColorTo8888(mergedPix);
        }
        else
        {
            curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
            lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
            rgb.r = (curPixel.r/2 + lastPixel.r/2)-2;
            rgb.g = (curPixel.g/2 + lastPixel.b/2)-2;
            rgb.b = (curPixel.b/2 + lastPixel.b/2)-2;
            rgb.a = 255;

        }
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        return rgb;
    }

    uchar4 __attribute__((kernel)) stackimage_lighten(uint32_t x, uint32_t y)
    {
        float4 curPixel, lastPixel;
        uchar4 rgb;
        curPixel = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y));
        lastPixel = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y));
        if(curPixel > lastPixel)
            rgb = rsPackColorTo8888(curPixel);
        else
            rgb = rsPackColorTo8888(lastPixel);
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
        else if(curPixel.r > t.max.r && curPixel.g > t.max.g && curPixel.b > t.max.b)
            t.max = curPixel;
        uchar4 rgb = t.max - t.min;
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        return rgb;
    }

