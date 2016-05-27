package com.freedcam.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.view.Surface;

import com.freedcam.apis.camera1.camera.renderscript.ScriptC_focus_peak_cam1;
import com.freedcam.apis.camera2.camera.renderscript.ScriptC_focus_peak;
import com.imageconverter.ScriptC_imagestack;

/**
 * Created by troop on 23.05.2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class RenderScriptHandler
{
    private Allocation mAllocationOut;
    private Allocation mAllocationIn;
    private RenderScript mRS;
    private Type.Builder inputbuilder;
    private Type.Builder outputbuilder;
    public ScriptC_focus_peak ScriptFocusPeakApi2;
    public ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    public ScriptC_focus_peak_cam1 ScriptFocusPeakApi1;
    public ScriptC_imagestack imagestack;

    public RenderScriptHandler(Context context)
    {
        mRS = RenderScript.create(context);
        mRS.setPriority(Priority.LOW);
        ScriptFocusPeakApi2 = new ScriptC_focus_peak(mRS);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(mRS, Element.U8_4(mRS));
        ScriptFocusPeakApi1 = new ScriptC_focus_peak_cam1(mRS);
        imagestack = new ScriptC_imagestack(mRS);
    }

    public void SetAllocsTypeBuilder(Type.Builder inputBuilder, Type.Builder outputBuilder, int inputUsage, int outputUsage)
    {
        this.inputbuilder = inputBuilder;
        this.outputbuilder = outputBuilder;
        mAllocationIn = Allocation.createTyped(mRS, inputbuilder.create(), Allocation.MipmapControl.MIPMAP_NONE,  inputUsage);
        mAllocationOut = Allocation.createTyped(mRS, outputbuilder.create(), Allocation.MipmapControl.MIPMAP_NONE, outputUsage);
    }

    public Allocation GetOut()
    {
        return  mAllocationOut;
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
