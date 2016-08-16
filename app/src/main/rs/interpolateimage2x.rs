#pragma version(1)
#pragma rs java_package_name(freed.utils)
#pragma rs_fp_relaxed

rs_allocation inputFrame;
rs_allocation scaledFrame;
int width;
int height;


uchar4 __attribute__((kernel)) stackPixel(uchar4 inPixel, uint32_t x, uint32_t y) {
    uchar4 outpix;
    outpix = rsGetElementAt_uchar4(scaledFrame,x, y);
    if(outpix.r >0 && outpix.g >0 && outpix.b >0)
        outpix = (inPixel+outpix) /2;
    else
        outpix = inPixel;
    if (outpix.r > 255) outpix.r = 255; if(outpix.r < 0) outpix.r = 0;
    if (outpix.g > 255) outpix.g = 255; if(outpix.g < 0) outpix.g = 0;
    if (outpix.b > 255) outpix.b = 255; if(outpix.b < 0) outpix.b = 0;
    return outpix;
}

uchar4 __attribute__((kernel)) fillPixelInterpolate(uint32_t x, uint32_t y)
{
    uchar4 inPixel1,inPixel2, outpix;
    int outpixPosX= x *2;
    int outpixPosY= y*2;
    inPixel1 = rsGetElementAt_uchar4(inputFrame,x, y);
    inPixel2 = (rsGetElementAt_uchar4(inputFrame,x+1, y) + inPixel1)/2;
    rsSetElementAt_uchar4(scaledFrame, inPixel1, outpixPosX, outpixPosY);
    rsSetElementAt_uchar4(scaledFrame, inPixel2, outpixPosX+1, outpixPosY);
    return inPixel1;
}

uchar4 __attribute__((kernel)) fillPixel(uint32_t x, uint32_t y) {
    uchar4 inPixel, outpix;
    int outpixPosX= x *2;
    int outpixPosY= y*2;
    inPixel = rsGetElementAt_uchar4(inputFrame,x, y);
    rsSetElementAt_uchar4(scaledFrame, inPixel, outpixPosX, outpixPosY);
    return inPixel;
}

uchar4 __attribute__((kernel)) interpolatePixel(uint32_t x, uint32_t y) {
    uchar4 inPixel, outpix;
    inPixel = rsGetElementAt_uchar4(scaledFrame,x, y);
    if(inPixel.r == 0 && inPixel.g == 0 && inPixel.b == 0 && x>0 && x+1 < width*2)
    {
        inPixel =  (rsGetElementAt_uchar4(scaledFrame,x-1, y) + rsGetElementAt_uchar4(scaledFrame,x+1,y))/2;
        if (inPixel.r > 255) inPixel.r = 255; if(inPixel.r < 0) inPixel.r = 0;
        if (inPixel.g > 255) inPixel.g = 255; if(outpix.g < 0) inPixel.g = 0;
        if (inPixel.b > 255) inPixel.b = 255; if(inPixel.b < 0) inPixel.b = 0;
    }
    return inPixel;
}

// applys a 3x3 around each pixels to scaledFrame
//lefttop      centertop    rightop
//leftcenter   inPixel      rightcenter
//leftbottom   centerbottom rightbottom
uchar4 __attribute__((kernel)) stackimage_avarage(uint32_t x, uint32_t y) {
    uchar4 inPixel, outpix;
    int outpixPosX= x *2;
    int outpixPosY= y*2;
    int posX,posY;
    inPixel = rsGetElementAt_uchar4(inputFrame,x, y);
    if(x > 0) //getleftpixel
    {
        posX = outpixPosX-1;
        posY = outpixPosY;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX, posY);
    }
    //setCenterPix
    rsSetElementAt_uchar4(scaledFrame, inPixel, outpixPosX, outpixPosY);
    //setRightPix
    if(x < width)
    {
        posX = outpixPosX +1;
        posY = outpixPosY;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX, posY);
    }

    /*if(x > 0 && y > 0) //getlefttoppixel
    {
        posX = outpixPosX -1;
        posY = outpixPosY -1;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX, posY);
    }*/

    /*if(x > 0 && y > 0) //getcentertoppixel
    {
        posX = outpixPosX;
        posY = outpixPosY -1;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX, posY);
    }

    if(x > 0 && y > 0) //getrighttoppixel
    {
        posX = outpixPosX+1;
        posY = outpixPosY -1;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX, posY);
    }

    if(x >0 && y < height) //getleftbottompixel
    {
        posX = outpixPosX-1;
        posY = outpixPosY +1;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX,posY);
    }

    if(y < height) //getcenterbottompixel
    {
        posX = outpixPosX;
        posY = outpixPosY +1;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX,posY);
    }

    if(x <width && y < height) //getrightbottompixel
    {
        posX = outpixPosX+1;
        posY = outpixPosY +1;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX,posY);
    }*/
    return inPixel;
}
