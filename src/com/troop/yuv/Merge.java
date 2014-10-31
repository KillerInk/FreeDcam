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
    private native void release(ByteBuffer nativeHandler);
    private native void storeNextYuvFrame(ByteBuffer nativeHandler, byte data[]);
    private native byte[] getMergedYuv(ByteBuffer nativeHandler, int count);


    public void AddFirstYuvFrame(byte data[])
    {
        if (nativeHandler != null)
            Release();
        nativeHandler = storeYuvFrame(data);
    }

    public void AddNextYuvFrame(byte data[])
    {
        if (nativeHandler == null)
            return;
        storeNextYuvFrame(nativeHandler, data);
    }

    public void Release()
    {
        if (nativeHandler == null)
            return;
        release(nativeHandler);
        nativeHandler =null;
    }

    public byte[] GetMergedYuv(int count)
    {
        if (nativeHandler == null)
            return null;
        return getMergedYuv(nativeHandler, count);
    }
}
