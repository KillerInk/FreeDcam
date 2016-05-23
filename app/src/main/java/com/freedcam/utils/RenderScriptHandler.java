package com.freedcam.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.view.Surface;

import com.freedcam.apis.camera2.camera.renderscript.ScriptC_focus_peak;

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
    private ScriptC_focus_peak mScriptFocusPeak;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;

    public RenderScriptHandler(Context context)
    {
        mRS = RenderScript.create(context);
        mRS.setPriority(Priority.LOW);
    }

    public void SetAllocsTypeBuilder(Type.Builder inputBuilder, Type.Builder outputBuilder)
    {
        this.inputbuilder = inputBuilder;
        this.outputbuilder = outputBuilder;
        mAllocationIn = Allocation.createTyped(mRS, inputbuilder.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_IO_INPUT | Allocation.USAGE_SCRIPT);
        mAllocationOut = Allocation.createTyped(mRS, outputbuilder.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_IO_OUTPUT | Allocation.USAGE_SCRIPT);
    }

    public Allocation GetOutputAllocation()
    {
        return  mAllocationOut;
    }

    public Allocation GetInputAllocation()
    {
        return mAllocationIn;
    }

    public RenderScript GetRenderScript()
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
