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

    // takes two pixel sample shows a 3x3 array PIX are the used one
    // PIX PIX pix
    // pix pix pix
    // pix pix pix
    uchar4 __attribute__((kernel)) stackimage_avarage1x2(uint32_t x, uint32_t y)
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

    // takes three pixel sample shows a 3x3 array PIX are the used one
    //  PIX   PIX  pix
    //  PIX   pix  pix
    //  pix   pix  pix
    uchar4 __attribute__((kernel)) stackimage_avarage1x3(uint32_t x, uint32_t y)
    {
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
            cPix = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y));
            lPix = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y));
            mergedPix = (cPix + lPix)/2;
            rgb = rsPackColorTo8888(mergedPix);

        }
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        return rgb;
    }

    // takes three pixel sample shows a 3x3 array PIX are the used one
    //  PIX1   PIX2  PIX3
    //  PIX4   PIX5  PIX6
    //  PIX7   PIX8  PIX9
    uchar4 __attribute__((kernel)) stackimage_avarage3x3(uint32_t x, uint32_t y)
    {
        float4 PIX1, PIX2, PIX3,PIX4, PIX5, PIX6 , PIX7, PIX8, PIX9,
        PIX1L, PIX2L, PIX3L,PIX4L, PIX5L, PIX6L , PIX7L, PIX8L, PIX9L, mergedPix;

        uchar4 rgb =rsGetElementAt_uchar4(gCurrentFrame, x, y);
        //rsDebug("Width", x);
        if(x > 0 && x+1 < Width && y > 0 && y+1 < Height)
        {
            PIX5 = rsUnpackColor8888(rgb); // pixel that get merged
            PIX1 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x-1, y-1));
            PIX2 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y-1));
            PIX3 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x+1, y-1));
            PIX4 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x-1, y));
            PIX6 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x+1, y));
            PIX7 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x+1, y+1));
            PIX8 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y+1));
            PIX9 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x+1, y+1));

            PIX5L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y)); // pixel that get merged
            PIX1L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x-1, y-1));
            PIX2L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y-1));
            PIX3L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x+1, y-1));
            PIX4L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x-1, y));
            PIX6L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x+1, y));
            PIX7L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x+1, y+1));
            PIX8L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y+1));
            PIX9L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x+1, y+1));

            mergedPix = (PIX1 + PIX2 +PIX3+PIX4+PIX5+PIX6+PIX7+PIX8+PIX9+PIX1L + PIX2L +PIX3L+PIX4L+PIX5L+PIX6L+PIX7L+PIX8L+PIX9L)/18;
            rgb = rsPackColorTo8888(mergedPix);
        }
        else
        {
            PIX5 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x, y));
            PIX5L = rsUnpackColor8888(rsGetElementAt_uchar4(gLastFrame, x, y));
            mergedPix = (PIX5 + PIX5L)/2;
            rgb = rsPackColorTo8888(mergedPix);

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
        if(curPixel.x > lastPixel.x && curPixel.y > lastPixel.y && curPixel.z > lastPixel.z)
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

