package com.troop.yuv;

import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by troop on 30.10.2014.
 */
public class Merge
{
    public Merge()
    {
        nativeHandler = null;
    }
    static String TAG = "YuvMerge";

    static
    {
        System.loadLibrary("YuvMerge");
    }

    private ByteBuffer nativeHandler = null;

    private native ByteBuffer storeYuvFrame(byte data[], int widht, int height);
    private native void release(ByteBuffer nativeHandler);
    private native void storeNextYuvFrame(ByteBuffer nativeHandler, byte data[]);
    private native byte[] getMergedYuv(ByteBuffer nativeHandler, int count);


    public void AddFirstYuvFrame(byte data[], int width, int height)
    {
        if (nativeHandler != null)
            Release();
        Log.d(TAG, "first frame");
        nativeHandler = storeYuvFrame(data, width, height);
        Log.d(TAG, "first frame stored");
    }

    public void AddNextYuvFrame(byte data[])
    {
        if (nativeHandler == null)
            return;
        Log.d(TAG, "next frame");
        storeNextYuvFrame(nativeHandler, data);
        Log.d(TAG, "next frame stored");
    }

    public void Release()
    {
        if (nativeHandler == null)
            return;
        Log.d(TAG, "Realease nativeHandler");
        release(nativeHandler);
        nativeHandler =null;
    }

    public byte[] GetMergedYuv(int count)
    {
        if (nativeHandler == null)
            return null;
        Log.d(TAG, "Get MErged Yuv");
        return getMergedYuv(nativeHandler, count);
    }
}
