#pragma version(1)
#pragma rs java_package_name(freed.utils)
#pragma rs_fp_relaxed
rs_allocation gCurrentFrame;
rs_allocation gLastFrame;
uchar4 __attribute__((kernel)) stack(uint32_t x, uint32_t y)
{
    int dx = x + ((x == 0) ? 1 : -1);
    int dy = y + ((y == 0) ? 1 : -1);
    uchar4 curPixel = rsGetElementAt_uchar4(gCurrentFrame, dx, y);
    uchar4 nextPix = rsGetElementAt_uchar4(gCurrentFrame, x, dy);
    int redcount = curPixel.r - nextPix.r;
    if(redcount < 5)
        return curPixel;
    else
        return rsGetElementAt_uchar4(gLastFrame, x, y);
}