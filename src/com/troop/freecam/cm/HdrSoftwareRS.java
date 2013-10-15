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
package com.troop.freecam.cm;

import com.android.camera.ScriptC_HdrSoftware;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Matrix4f;
import android.renderscript.ProgramFragment;
import android.renderscript.ProgramFragmentFixedFunction;
import android.renderscript.ProgramStore;
import android.renderscript.ProgramVertex;
import android.renderscript.ProgramVertexFixedFunction;
import android.renderscript.RenderScript;
import android.renderscript.Sampler;
import android.util.Log;

/**
 * RenderScript host class for HdrSoftware RenderScript.
 */
public class HdrSoftwareRS {
    private RenderScript mRS;
    private ScriptC_HdrSoftware mScript;
    private Allocation[] mInBitmapAlloc;
    private Allocation mOutBitmapAlloc;
    private Bitmap mOutBitmap;

    public final static int BITMAP_LOW = 0;
    public final static int BITMAP_MID = 1;
    public final static int BITMAP_HI = 2;
    public final static String TAG = "HdrSoftwareRS";

    /**
     * Default constructor
     * @param rs The host RenderScriptGL pointer
     * @param res
     * @param resId
     */
    public HdrSoftwareRS(RenderScript rs, Resources res, int resId) {
        mRS = rs;
        mScript = new ScriptC_HdrSoftware(rs, res, resId);
        mInBitmapAlloc = new Allocation[3];
    }

    /**
     * Run the processing
     */
    public void process() {
        // We make the output bitmap based on the inputs.
        // We don't really care about the content at this point, we just need the same size
        // and pixel depth.
        mOutBitmapAlloc = Allocation.createTyped(mRS, mInBitmapAlloc[BITMAP_LOW].getType());
        mScript.bind_gOutput(mOutBitmapAlloc);

        // We refer to the row of the images through an alloc to parallelize processing
        int num_rows = mOutBitmap.getHeight();
        int row_width = mOutBitmap.getWidth();

        int[] row_indices = new int[num_rows];
        for (int i = 0; i < num_rows; i++) {
            row_indices[i] = i * row_width;
        }

        Allocation row_indices_alloc = Allocation.createSized(mRS, Element.I32(mRS), num_rows, Allocation.USAGE_SCRIPT);
        row_indices_alloc.copyFrom(row_indices);

        mScript.set_gInIndex(row_indices_alloc);
        mScript.set_gImageWidth(row_width);
        mScript.set_gScript(mScript);

        // We run the script...
        mScript.invoke_performHdrComputation();

        // And we copy the output to a bitmap
        mOutBitmapAlloc.copyTo(mOutBitmap);
    }

    /**
     * Returns a Bitmap containing a copy of the Output buffer allocation
     */
    public Bitmap getOutput() {
        return mOutBitmap;
    }

    /**
     * Set the input bitmaps for the processing.
     * @param input Bitmap to import
     * @param input_image HdrSoftwareRS.BITMAP_LOW, BITMAP_MID, BITMAP_HI
     */
    public void setBitmapInput(Bitmap input, int input_image) {
        if (input == null) {
            Log.e(TAG, "Cannot set HdrSoftware input bitmap " + input_image + ": input is null");
            return;
	}

        if (input_image < BITMAP_LOW || input_image > BITMAP_HI) {
            Log.e(TAG, "Invalid slot " + input_image + " for HDR input");
            return;
        }

        mInBitmapAlloc[input_image] = Allocation.createFromBitmap(mRS, input, 
                                          Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        // Bind our allocations to our script
        switch (input_image) {
        case BITMAP_LOW:
            mScript.bind_gInputLow(mInBitmapAlloc[BITMAP_LOW]);
            break;

        case BITMAP_MID:
            mScript.bind_gInputMid(mInBitmapAlloc[BITMAP_MID]);
            break;

        case BITMAP_HI:
            mScript.bind_gInputHi(mInBitmapAlloc[BITMAP_HI]);
            break;
        }

        if (mOutBitmap == null) {
            // We prepare our local output bitmap, we can copy the format and size from the
            // current inputs
            mOutBitmap = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
        }
    }
}

