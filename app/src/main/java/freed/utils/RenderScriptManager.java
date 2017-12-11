/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.renderscript.Allocation;
import android.renderscript.Allocation.MipmapControl;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type.Builder;
import android.view.Surface;


/**
 * Created by troop on 23.05.2016.
 */
@TargetApi(VERSION_CODES.KITKAT)
public class RenderScriptManager
{
    private Allocation mAllocationOut;
    private Allocation mAllocationIn;
    private final RenderScript mRS;

/*        public ScriptC_focus_peak ScriptFocusPeakApi2;
    public ScriptC_focus_peak_cam1 ScriptFocusPeakApi1;
    public ScriptC_imagestack imagestack;
    public ScriptC_focuspeak_argb focuspeak_argb;
    public ScriptC_brightness brightnessRS;
    public ScriptC_contrast contrastRS;
    public ScriptC_starfinder starfinderRS;
    public ScriptC_interpolateimage2x interpolateimage2x;*/

    public ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    public ScriptIntrinsicBlur blurRS;
    public ScriptIntrinsicConvolve3x3 convolve3x3;
    public ScriptC_freedcam freedcamScript;

    private boolean sucessfullLoaded = false;

    public boolean isSucessfullLoaded() {
        return this.sucessfullLoaded;
    }

    public RenderScriptManager(Context context)
    {
        mRS = RenderScript.create(context);
        mRS.setPriority(Priority.LOW);
        try {
/*            ScriptFocusPeakApi2 = new ScriptC_focus_peak(mRS);
            ScriptFocusPeakApi1 = new ScriptC_focus_peak_cam1(mRS);
            imagestack = new ScriptC_imagestack(mRS);
            focuspeak_argb = new ScriptC_focuspeak_argb(mRS);
            brightnessRS = new ScriptC_brightness(mRS);
            contrastRS = new ScriptC_contrast(mRS);
            starfinderRS = new ScriptC_starfinder(mRS);
            interpolateimage2x = new ScriptC_interpolateimage2x(mRS);*/

            freedcamScript = new ScriptC_freedcam(mRS);
            blurRS = ScriptIntrinsicBlur.create(mRS, Element.U8_4(mRS));
            yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(mRS, Element.U8_4(mRS));
            convolve3x3 = ScriptIntrinsicConvolve3x3.create(mRS,Element.U8_4(mRS));
            sucessfullLoaded = true;
        }
        catch (RSRuntimeException ex)
        {
            Log.WriteEx(ex);
            sucessfullLoaded = false;
        }
    }

    public void SetAllocsTypeBuilder(Builder inputBuilder, Builder outputBuilder, int inputUsage, int outputUsage)
    {
        Builder inputbuilder = inputBuilder;
        Builder outputbuilder = outputBuilder;
        mAllocationIn = Allocation.createTyped(mRS, inputbuilder.create(), MipmapControl.MIPMAP_NONE,  inputUsage);
        mAllocationOut = Allocation.createTyped(mRS, outputbuilder.create(), MipmapControl.MIPMAP_NONE, outputUsage);
    }

    public Allocation GetOut()
    {
        return mAllocationOut;
    }

    public Allocation GetIn()
    {
        return mAllocationIn;
    }

    public RenderScript GetRS()
    {
        return mRS;
    }

    public void SetSurfaceToOutputAllocation(Surface surface)
    {
        mAllocationOut.setSurface(surface);
    }

    public Surface GetInputAllocationSurface()
    {
        return mAllocationIn.getSurface();
    }
}
