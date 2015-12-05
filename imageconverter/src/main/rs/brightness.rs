/*
 * Copyright (C) 2011 The Android Open Source Project
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
float brightness;

uchar4 __attribute__((kernel)) processBrightness(uint32_t x, uint32_t y) {
    float4 f4 = rsUnpackColor8888(rsGetElementAt_uchar4(gCurrentFrame, x,y));
        f4.r += brightness;

        if(f4.r > 1){
        	f4.r = 1;
        }

        f4.g += brightness;
        if(f4.g > 1){
        	f4.g = 1;
        }

        f4.b += brightness;
        if(f4.b > 1){
        	f4.b = 1;
        }

     return rsPackColorTo8888(f4);
}
