#pragma version(1)
    #pragma rs java_package_name(freed.renderscript)
    #pragma rs_fp_relaxed

    int32_t *histodataR;
    int takeOnlyPixel;

    void clear()
    {
        for(int i = 0; i< 256; i++)
        {
            histodataR[i] = 0;
        }
    }

    uchar4 __attribute__((kernel)) processHistogram(uchar4 in, uint32_t x, uint32_t y) {
        //set histo data
        if(x & takeOnlyPixel && y &takeOnlyPixel)
        {
            volatile int32_t *addr = &histodataR[(in.r + in.g + in.b)/3];
            rsAtomicInc(addr);
        }
        return in;
    }