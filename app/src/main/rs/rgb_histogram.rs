#pragma version(1)
    #pragma rs java_package_name(freed.utils)
    #pragma rs_fp_relaxed

    int32_t *histodataR;
    int32_t *histodataG;
    int32_t *histodataB;

    uchar4 __attribute__((kernel)) processHistogram(uchar4 in, uint32_t x, uint32_t y) {
        uchar4 curPixel = in;
        //set histo data
        if(x & 10 && y &10)
        {
            volatile int32_t *addr = &histodataR[curPixel.r];
            rsAtomicInc(addr);
            addr = &histodataG[curPixel.g];
            rsAtomicInc(addr);
            addr = &histodataB[curPixel.b];
            rsAtomicInc(addr);
        }
        return curPixel;
    }