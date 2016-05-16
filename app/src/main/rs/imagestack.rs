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
        uchar4 curPixel, lastPixel;
        curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
        lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
        uchar4 rgb;
        rgb.r = (curPixel.r/2 + lastPixel.r/2)-2;
        rgb.g = (curPixel.g/2 + lastPixel.b/2)-2;
        rgb.b = (curPixel.b/2 + lastPixel.b/2)-2;
        rgb.a = 255;
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
        if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
        if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        return rgb;
    }

    uchar4 __attribute__((kernel)) stackimage_avarage2x1(uint32_t x, uint32_t y)
    {
        uchar4 curPixel, lastPixel, curPixel1,lastPixel1;
        if(x < Width)
        {
            curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
            lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
            curPixel1 = rsGetElementAt_uchar4(gCurrentFrame, x+1, y);
            lastPixel1 = rsGetElementAt_uchar4(gLastFrame, x+1, y);
            uchar4 rgb;
            rgb.r = (curPixel.r/4 + lastPixel.r/4 + curPixel1.r/4 + lastPixel1.r/4);
            rgb.g = (curPixel.g/4 + lastPixel.b/4 + curPixel1.g/4 + lastPixel1.b/4);
            rgb.b = (curPixel.b/4 + lastPixel.b/4 + curPixel1.b/4 + lastPixel1.b/4);
            rgb.a = 255;
            if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
            if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
            if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
            return rgb;
        }
        else
        {
            curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
            lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
            uchar4 rgb;
            rgb.r = (curPixel.r/2 + lastPixel.r/2)-2;
            rgb.g = (curPixel.g/2 + lastPixel.b/2)-2;
            rgb.b = (curPixel.b/2 + lastPixel.b/2)-2;
            rgb.a = 255;
            if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
            if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
            if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
            return rgb;
        }
    }

    uchar4 __attribute__((kernel)) stackimage_avarage2x2(uint32_t x, uint32_t y)
        {
            uchar4 curPixel, lastPixel, curPixelX1,lastPixelX1,curPixelY1,lastPixelY1;
            int r,g,b;
            uchar4 rgb;
            rsDebug("Width", x);
            if(x < Width && y < Height)
            {
                curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
                lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
                curPixelX1 = rsGetElementAt_uchar4(gCurrentFrame, x+1, y);
                lastPixelX1 = rsGetElementAt_uchar4(gLastFrame, x+1, y);
                curPixelY1 = rsGetElementAt_uchar4(gCurrentFrame, x, y+1);
                lastPixelY1 = rsGetElementAt_uchar4(gLastFrame, x, y+1);
                //rsDebug("all Pixel init", curPixel);
                r = (curPixel.r + lastPixel.r + curPixelX1.r + lastPixelX1.r + curPixelY1.r + lastPixelY1.r)/6;
                g = (curPixel.g + lastPixel.g + curPixelX1.g + lastPixelX1.g + curPixelY1.g + lastPixelY1.g)/6;
                b = (curPixel.b + lastPixel.b + curPixelX1.b + lastPixelX1.b + curPixelY1.b + lastPixelY1.b)/6;
                if (r > 255) r = 255; if(r < 0) r = 0;
                if (g > 255) g = 255; if(g < 0) g = 0;
                if (b > 255) b = 255; if(b < 0) b = 0;
                rgb.r = r;
                rgb.g = g;
                rgb.b = b;
                rgb.a = 255;

            }
            else
            {
                curPixel = rsGetElementAt_uchar4(gCurrentFrame, x, y);
                lastPixel = rsGetElementAt_uchar4(gLastFrame, x, y);
                rgb.r = (curPixel.r/2 + lastPixel.r/2)-2;
                rgb.g = (curPixel.g/2 + lastPixel.b/2)-2;
                rgb.b = (curPixel.b/2 + lastPixel.b/2)-2;
                rgb.a = 255;
                if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
                if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
                if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
            }

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
        {
            rgb.r = (curPixel.r/2 + lastPixel.r/2)-2;
            rgb.g = (curPixel.g/2 + lastPixel.b/2)-2;
            rgb.b = (curPixel.b/2 + lastPixel.b/2)-2;
        }
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

