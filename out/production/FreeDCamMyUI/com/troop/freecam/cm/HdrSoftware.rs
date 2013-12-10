/*
 * Copyright (C) 2013 The CyanogenMod Project
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
#pragma rs java_package_name(com.android.camera)

#include "rs_graphics.rsh"

rs_script gScript;

rs_allocation gInIndex;
const uchar4* gInputLow;
const uchar4* gInputMid;
const uchar4* gInputHi;
uchar4* gOutput;

int gImageWidth;

void init() {
	rsDebug("HDR init", rsUptimeMillis());
}

void root(const int32_t* v_in, int32_t* v_out) {
	// Get the row from the input
	int32_t y = *v_in;

	// Compute the average of each pixels from the 3 image samples
	float3 pxOut;

	for (int x = 0; x < gImageWidth; x++) {
		const int32_t index = y+x;

		// Gather the pixels
		float3 pxLow = rsUnpackColor8888(gInputLow[index]).rgb;
		float3 pxMid = rsUnpackColor8888(gInputMid[index]).rgb;
		float3 pxHi = rsUnpackColor8888(gInputHi[index]).rgb;

		// Compute the average
		pxOut.r = (pxLow.r + pxMid.r + pxHi.r)/3.0f;
		pxOut.g = (pxLow.g + pxMid.g + pxHi.g)/3.0f;
		pxOut.b = (pxLow.b + pxMid.b + pxHi.b)/3.0f;

		// Copy the pixel to the output image
		gOutput[index] = rsPackColorTo8888(pxOut);
	}

	// Done computing this row in the output allocation
}



void performHdrComputation() {
	if (gInputLow == 0 || gInputMid == 0
		|| gInputHi == 0 || gOutput == 0) {
		// TODO: Compute if there are only 2 images
		rsDebug("There are pointers missing, skipping rendering.", rsUptimeMillis());
	}
	else {
		// v_out is not used, so we pass gInIndex again
		rsForEach(gScript, gInIndex, gInIndex);
	}
}
