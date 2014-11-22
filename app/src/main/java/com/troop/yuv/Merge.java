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
    int width;
    int height;

    private static native ByteBuffer storeYuvFrame(byte data[], int widht, int height);
    private static native void release(ByteBuffer nativeHandler);
    private static native void storeNextYuvFrame(ByteBuffer nativeHandler, byte data[]);
    private static native byte[] getMergedYuv(ByteBuffer nativeHandler, int count, byte arrayToFill[]);


    public void AddFirstYuvFrame(byte data[], int width, int height)
    {
        if (nativeHandler != null) {
            Release();
            Log.d(TAG, "Native handler not null realasing it");
            nativeHandler = null;
        }
        this.width = width;
        this.height =height;
        nativeHandler = storeYuvFrame(data, width, height);
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
        Log.d(TAG, "Realease nativeHandler");
        release(nativeHandler);
        nativeHandler =null;
    }

    public byte[] GetMergedYuv(int count)
    {
        if (nativeHandler == null)
            return null;
        Log.d(TAG, "Get MErged Yuv");
        int yuvsize = (width * height) + (width * height)/2;
        byte ar[] = new byte[yuvsize];
        return getMergedYuv(nativeHandler, count, ar);
    }
}
