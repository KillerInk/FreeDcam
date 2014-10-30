package com.troop.yuv;

import java.nio.ByteBuffer;

/**
 * Created by troop on 30.10.2014.
 */
public class Merge
{
    static
    {
        System.loadLibrary("YuvMerge");
    }

    private ByteBuffer nativeHandler = null;

    private native ByteBuffer storeYuvFrame(byte data[]);


    public void AddYuvFrame(byte data[])
    {
        if (nativeHandler != null)
            return;
        nativeHandler = storeYuvFrame(data);
    }
}
