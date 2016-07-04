
#pragma version(1)
    #pragma rs java_package_name(freed.utils)
    #pragma rs_fp_relaxed

rs_allocation gCurrentFrame;

uchar4 __attribute__((kernel)) processBrightness(uint32_t x, uint32_t y) {
    uchar4 pix = rsGetElementAt_uchar4(gCurrentFrame, x,y);
    if(pix.r < 10 && pix.g < 10 && pix.b < 10)
    {
        pix.r = 10;
        pix.g = 10;
        pix.b = 10;
    }
    if(pix.r < 20 && pix.g < 20 && pix.b < 20)
    {
        pix.r = 20;
        pix.g = 20;
        pix.b = 20;
    }
    else if(pix.r <30 && pix.g < 30 && pix.b < 30)
    {
        pix.r = 30;
        pix.g = 30;
        pix.b = 30;
    }
    else if(pix.r < 40 && pix.g < 40 && pix.b < 40)
    {
        pix.r = 40;
        pix.g = 40;
        pix.b = 40;
    }
    else if(pix.r < 50 && pix.g < 50 && pix.b < 50)
    {
        pix.r = 50;
        pix.g = 50;
        pix.b = 50;
    }
    else if(pix.r < 60 && pix.g < 60 && pix.b < 60)
    {
        pix.r = 60;
        pix.g = 60;
        pix.b = 60;
    }
    else if(pix.r < 70 && pix.g < 70 && pix.b < 70)
    {
        pix.r = 70;
        pix.g = 70;
        pix.b = 70;
    }
    else if(pix.r < 80 && pix.g < 80 && pix.b < 80)
    {
        pix.r = 80;
        pix.g = 80;
        pix.b = 80;
    }
    else if(pix.r < 90 && pix.g < 90 && pix.b < 90)
    {
        pix.r = 90;
        pix.g = 90;
        pix.b = 90;
    }
    else if(pix.r < 100 && pix.g < 100 && pix.b < 100)
    {
        pix.r = 100;
        pix.g = 100;
        pix.b = 100;
    }
    else if(pix.r < 110 && pix.g < 110 && pix.b < 110)
    {
        pix.r = 110;
        pix.g = 110;
        pix.b = 110;
    }
    else if(pix.r < 120 && pix.g < 120 && pix.b < 120)
    {
        pix.r = 120;
        pix.g = 120;
        pix.b = 120;
    }
    else if(pix.r < 130 && pix.g < 130 && pix.b < 130)
    {
        pix.r = 130;
        pix.g = 130;
        pix.b = 130;
    }
    else if(pix.r < 140 && pix.g < 140 && pix.b < 140)
    {
        pix.r = 140;
        pix.g = 140;
        pix.b = 140;
    }
    else if(pix.r < 150 && pix.g < 150 && pix.b < 150)
    {
        pix.r = 150;
        pix.g = 150;
        pix.b = 150;
    }
    else if(pix.r < 160 && pix.g < 160 && pix.b < 160)
    {
        pix.r = 160;
        pix.g = 160;
        pix.b = 160;
    }
    else if(pix.r < 170 && pix.g < 170 && pix.b < 170)
    {
        pix.r = 170;
        pix.g = 170;
        pix.b = 170;
    }
    else if(pix.r < 180 && pix.g < 180 && pix.b < 180)
    {
        pix.r = 180;
        pix.g = 180;
        pix.b = 180;
    }
    else if(pix.r < 190 && pix.g < 190 && pix.b < 190)
    {
        pix.r = 190;
        pix.g = 190;
        pix.b = 190;
    }
    else if(pix.r < 200 && pix.g < 200 && pix.b < 200)
    {
        pix.r = 200;
        pix.g = 200;
        pix.b = 200;
    }
    else if(pix.r < 210 && pix.g < 210 && pix.b < 210)
    {
        pix.r = 210;
        pix.g = 210;
        pix.b = 210;
    }
    else if(pix.r < 220 && pix.g < 220 && pix.b < 220)
    {
        pix.r = 220;
        pix.g = 220;
        pix.b = 220;
    }
    else if(pix.r < 230 && pix.g < 230 && pix.b < 230)
    {
        pix.r = 230;
        pix.g = 230;
        pix.b = 230;
    }
    else if(pix.r < 240 && pix.g < 240 && pix.b < 240)
    {
        pix.r = 240;
        pix.g = 240;
        pix.b = 240;
    }
    else
    {
        pix.r = 255;
        pix.g = 255;
        pix.b = 255;
    }

     return pix;
}