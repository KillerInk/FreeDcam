/*
 * Copyright (C) 2012 The CyanogenMod Project
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

import java.io.IOException;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.renderscript.RenderScript;
import android.util.Log;

import com.troop.freecam.R;

public class HdrSoftwareProcessor {
    public final static String TAG = "SW_HDR";

    private static class RGB {
        int r, g, b;

        public RGB(int color) {
            set(color);
        }

        public void set(int color) {
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
        }

        public void set(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    };

    private Bitmap mSourceBitmap;
    private Bitmap mOutputBitmap;
    private int mImageWidth;
    private int mImageHeight;
    private RenderScript mRSRenderer;
    private HdrSoftwareRS mRSHost;

    /**
     * Default constructor
     */
    public HdrSoftwareProcessor(Context ctx) {
        mRSRenderer = RenderScript.create(ctx);
        mRSHost = new HdrSoftwareRS(mRSRenderer, ctx.getResources(), R.raw.hdrsoftware);
    }

    /**
     * Prepare the processor with the provided source images paths
     * @param sourceImages Source images at different exposures
     */
    public void prepare(Context ctx, Uri[] sourceImages) throws IOException {
        // We load every source image in one Bitmap and ask the renderscript to
        // copy to the allocation. This way we save memory by using only one Bitmap
        // intermediate.
        for (int i = 0; i < sourceImages.length; i++) {
            mSourceBitmap = Media.getBitmap(ctx.getContentResolver(), sourceImages[i]);

            // load it in the renderscript
            mRSHost.setBitmapInput(mSourceBitmap, i);

            // try to use as few memory as possible
            mSourceBitmap.recycle();
            //System.gc();
        }
    }

    /**
     * Compute the final image from the source bitmaps using the stored method,
     * and output a final JPEG file at the specified output path
     */
    public byte[] computeHDR(Context ctx) {
        computeHDRRenderScript(ctx);

        // Save image to memory - will be later fed into ImageSaver
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            mOutputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            mOutputBitmap.recycle();
            return out.toByteArray();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Computes the HDR image using renderscript
     */
    public void computeHDRRenderScript(Context ctx) {
        Log.d(TAG, "Starting HDR render (RS)");

        // process and grab output
        mRSHost.process();
        mOutputBitmap = mRSHost.getOutput();
    }
}
