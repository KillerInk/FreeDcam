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
    outpix = (inPixel+outpix) /2;
    if (outpix.r > 255) outpix.r = 255; if(outpix.r < 0) outpix.r = 0;
    if (outpix.g > 255) outpix.g = 255; if(outpix.g < 0) outpix.g = 0;
    if (outpix.b > 255) outpix.b = 255; if(outpix.b < 0) outpix.b = 0;
    return outpix;
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

    if(x > 0 && y > 0) //getlefttoppixel
    {
        posX = outpixPosX -1;
        posY = outpixPosY -1;
        outpix = stackPixel(inPixel,posX,posY);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX, posY);
    }

    if(x > 0 && y > 0) //getcentertoppixel
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
        rsDebug("posX", posX);
        rsDebug("posY", posY);
        rsDebug("y", y);
        rsDebug("x", x);
        rsDebug("inpixel",inPixel);
        rsDebug("outpixel", outpix);
        rsSetElementAt_uchar4(scaledFrame, outpix, posX,posY);
    }
    return inPixel;
}
