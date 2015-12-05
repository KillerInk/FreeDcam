/*
 * Copyright (C) 2012 The Android Open Source Project
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
int contrast;
rs_allocation gCurrentFrame;

uchar4 __attribute__((kernel)) processContrast(uint32_t x, uint32_t y)
{
        float factor = (259 * (contrast + 255)) / (255 * (259 - contrast));
        uchar4 in = rsGetElementAt_uchar4(gCurrentFrame, x,y);
        in.r = (factor * in.r - 128) + 128;
        in.g = (int)(factor * in.g - 128) + 128;
        in.b = (int)(factor * in.b - 128) + 128;
        if (in.r > 255) in.r = 255; if(in.r < 0) in.r = 0;
                if (in.g > 255) in.g = 255; if(in.g < 0) in.g = 0;
                if (in.b > 255) in.b = 255; if(in.b < 0) in.b = 0;
        return in;
}