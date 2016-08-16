#pragma version(1)
#pragma rs java_package_name(freed.utils)
#pragma rs_fp_relaxed

rs_allocation inputFrame;
rs_allocation scaledFrame;
int width;
int height;

uchar4 __attribute__((kernel)) fillPixelInterpolate(uint32_t x, uint32_t y)
{
    uchar4 inPixel1,inPixel2, outpix;
    int outpixPosX= x *2;
    int outpixPosY= y*2;
    inPixel1 = rsGetElementAt_uchar4(inputFrame,x, y);

    rsSetElementAt_uchar4(scaledFrame, inPixel1, outpixPosX, outpixPosY);
    if(x+1 < width){
        rsSetElementAt_uchar4(scaledFrame, inPixel1, outpixPosX+1, outpixPosY);
    }
    if(y+1 < height)
        rsSetElementAt_uchar4(scaledFrame, inPixel1, outpixPosX, outpixPosY+1);
    if(y+1 < height && x+1 < width)
        rsSetElementAt_uchar4(scaledFrame, inPixel1, outpixPosX+1, outpixPosY+1);
    return inPixel1;
}

uchar4 __attribute__((kernel)) clear(uint32_t x, uint32_t y)
{
    uchar4 inPixel1,inPixel2, outpix;
    inPixel1 = rsGetElementAt_uchar4(scaledFrame,x, y);
    inPixel1.r = 0;
    inPixel1.g = 0;
    inPixel1.b = 0;
    return inPixel1;
}

