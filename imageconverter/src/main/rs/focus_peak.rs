    /*
     * Copyright (C) 2015 The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    #pragma version(1)
    #pragma rs java_package_name(troop.com.imageconverter)
    #pragma rs_fp_relaxed
    rs_allocation gCurrentFrame;
    uchar4 __attribute__((kernel)) peak(uint32_t x, uint32_t y) {
        uchar4 curPixel;
        curPixel.r = rsGetElementAtYuv_uchar_Y(gCurrentFrame, x, y);
        curPixel.g = rsGetElementAtYuv_uchar_U(gCurrentFrame, x, y);
        curPixel.b = rsGetElementAtYuv_uchar_V(gCurrentFrame, x, y);
        int dx = x + ((x == 0) ? 1 : -1);
        int sum = 0;
        int tmp;
        tmp = rsGetElementAtYuv_uchar_Y(gCurrentFrame, dx, y) - curPixel.r;
        sum += tmp * tmp;
        tmp = rsGetElementAtYuv_uchar_U(gCurrentFrame, dx, y) - curPixel.g;
        sum += tmp * tmp;
        tmp = rsGetElementAtYuv_uchar_V(gCurrentFrame, dx, y) - curPixel.b;
        sum += tmp * tmp;
        int dy = y + ((y == 0) ? 1 : -1);
        tmp = rsGetElementAtYuv_uchar_Y(gCurrentFrame, x, dy) - curPixel.r;
        sum += tmp * tmp;
        tmp = rsGetElementAtYuv_uchar_U(gCurrentFrame, x, dy) - curPixel.g;
        sum += tmp * tmp;
        tmp = rsGetElementAtYuv_uchar_V(gCurrentFrame, x, dy) - curPixel.b;
        sum += tmp * tmp;
        sum >>= 9;
        sum *= sum * sum;
        curPixel.a = 255;
        uchar4 mergedPixel = curPixel;
        int4 rgb;
        rgb.r = mergedPixel.r +
                mergedPixel.b * 1436 / 1024 - 179 + sum;
        rgb.g = mergedPixel.r -
                mergedPixel.g * 46549 / 131072 + 44 -
                mergedPixel.b * 93604 / 131072 + 91 + sum;
        rgb.b = mergedPixel.r +
                mergedPixel.g * 1814 / 1024 - 227;
        rgb.a = 255;
        //if(rgb.r < 200)
        //   {
        //        rgb.r = 0;
        //        rgb.g = 0;
        //        rgb.b = 0;
        //        rgb.a = 0;
        //   }
        //else {
        //    rgb.r = 255;
        //    rgb.g = 0;
        //    rgb.b = 0;
        //    rgb.a = 0;
        //}
        // Write out merged HDR result
        if (rgb.r > 255) rgb.r = 255; if(rgb.r < 0) rgb.r = 0;
                if (rgb.g > 255) rgb.g = 255; if(rgb.g < 0) rgb.g = 0;
                if (rgb.b > 255) rgb.b = 255; if(rgb.b < 0) rgb.b = 0;
        uchar4 out = convert_uchar4(rgb);
        return out;
    }