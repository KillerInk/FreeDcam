/*___Generated_by_IDEA___*/

/*
 * Copyright (C) 2011-2013 The Android Open Source Project
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

/*
 * This file is auto-generated. DO NOT MODIFY!
 * The source Renderscript file: C:\\Android\\Code\\FreeDCamMyUI\\src\\com\\troop\\freecam\\cm\\HdrSoftware.rs
 */
package com.android.camera;

import android.renderscript.*;
import android.content.res.Resources;

/**
 * @hide
 */
public class ScriptC_HdrSoftware extends ScriptC {
    private static final String __rs_resource_name = "hdrsoftware";
    // Constructor
    public  ScriptC_HdrSoftware(RenderScript rs) {
        this(rs,
             rs.getApplicationContext().getResources(),
             rs.getApplicationContext().getResources().getIdentifier(
                 __rs_resource_name, "raw",
                 rs.getApplicationContext().getPackageName()));
    }

    public  ScriptC_HdrSoftware(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        __SCRIPT = Element.SCRIPT(rs);
        __ALLOCATION = Element.ALLOCATION(rs);
        __I32 = Element.I32(rs);
    }

    private Element __ALLOCATION;
    private Element __I32;
    private Element __SCRIPT;
    private FieldPacker __rs_fp_ALLOCATION;
    private FieldPacker __rs_fp_I32;
    private FieldPacker __rs_fp_SCRIPT;
    private final static int mExportVarIdx_gScript = 0;
    private Script mExportVar_gScript;
    public synchronized void set_gScript(Script v) {
        setVar(mExportVarIdx_gScript, v);
        mExportVar_gScript = v;
    }

    public Script get_gScript() {
        return mExportVar_gScript;
    }

    public Script.FieldID getFieldID_gScript() {
        return createFieldID(mExportVarIdx_gScript, null);
    }

    private final static int mExportVarIdx_gInIndex = 1;
    private Allocation mExportVar_gInIndex;
    public synchronized void set_gInIndex(Allocation v) {
        setVar(mExportVarIdx_gInIndex, v);
        mExportVar_gInIndex = v;
    }

    public Allocation get_gInIndex() {
        return mExportVar_gInIndex;
    }

    public Script.FieldID getFieldID_gInIndex() {
        return createFieldID(mExportVarIdx_gInIndex, null);
    }

    private final static int mExportVarIdx_gInputLow = 2;
    private Allocation mExportVar_gInputLow;
    public void bind_gInputLow(Allocation v) {
        mExportVar_gInputLow = v;
        if (v == null) bindAllocation(null, mExportVarIdx_gInputLow);
        else bindAllocation(v, mExportVarIdx_gInputLow);
    }

    public Allocation get_gInputLow() {
        return mExportVar_gInputLow;
    }

    private final static int mExportVarIdx_gInputMid = 3;
    private Allocation mExportVar_gInputMid;
    public void bind_gInputMid(Allocation v) {
        mExportVar_gInputMid = v;
        if (v == null) bindAllocation(null, mExportVarIdx_gInputMid);
        else bindAllocation(v, mExportVarIdx_gInputMid);
    }

    public Allocation get_gInputMid() {
        return mExportVar_gInputMid;
    }

    private final static int mExportVarIdx_gInputHi = 4;
    private Allocation mExportVar_gInputHi;
    public void bind_gInputHi(Allocation v) {
        mExportVar_gInputHi = v;
        if (v == null) bindAllocation(null, mExportVarIdx_gInputHi);
        else bindAllocation(v, mExportVarIdx_gInputHi);
    }

    public Allocation get_gInputHi() {
        return mExportVar_gInputHi;
    }

    private final static int mExportVarIdx_gOutput = 5;
    private Allocation mExportVar_gOutput;
    public void bind_gOutput(Allocation v) {
        mExportVar_gOutput = v;
        if (v == null) bindAllocation(null, mExportVarIdx_gOutput);
        else bindAllocation(v, mExportVarIdx_gOutput);
    }

    public Allocation get_gOutput() {
        return mExportVar_gOutput;
    }

    private final static int mExportVarIdx_gImageWidth = 6;
    private int mExportVar_gImageWidth;
    public synchronized void set_gImageWidth(int v) {
        setVar(mExportVarIdx_gImageWidth, v);
        mExportVar_gImageWidth = v;
    }

    public int get_gImageWidth() {
        return mExportVar_gImageWidth;
    }

    public Script.FieldID getFieldID_gImageWidth() {
        return createFieldID(mExportVarIdx_gImageWidth, null);
    }

    private final static int mExportForEachIdx_root = 0;
    public Script.KernelID getKernelID_root() {
        return createKernelID(mExportForEachIdx_root, 3, null, null);
    }

    public void forEach_root(Allocation ain, Allocation aout) {
        forEach_root(ain, aout, null);
    }

    public void forEach_root(Allocation ain, Allocation aout, Script.LaunchOptions sc) {
        // check ain
        if (!ain.getType().getElement().isCompatible(__I32)) {
            throw new RSRuntimeException("Type mismatch with I32!");
        }
        // check aout
        if (!aout.getType().getElement().isCompatible(__I32)) {
            throw new RSRuntimeException("Type mismatch with I32!");
        }
        // Verify dimensions
        Type tIn = ain.getType();
        Type tOut = aout.getType();
        if ((tIn.getCount() != tOut.getCount()) ||
            (tIn.getX() != tOut.getX()) ||
            (tIn.getY() != tOut.getY()) ||
            (tIn.getZ() != tOut.getZ()) ||
            (tIn.hasFaces() != tOut.hasFaces()) ||
            (tIn.hasMipmaps() != tOut.hasMipmaps())) {
            throw new RSRuntimeException("Dimension mismatch between input and output parameters!");
        }
        forEach(mExportForEachIdx_root, ain, aout, null, sc);
    }

    private final static int mExportFuncIdx_performHdrComputation = 0;
    public void invoke_performHdrComputation() {
        invoke(mExportFuncIdx_performHdrComputation);
    }

}

