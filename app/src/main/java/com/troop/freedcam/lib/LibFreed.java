package com.troop.freedcam.lib;

import android.graphics.Bitmap;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * Created by GeorgeKiarie on 01/05/2016.
 */
public class LibFreed {

    static
    {
        System.loadLibrary("FreeDCam");
    }

    //RawToDNG HOOKS
    public static native long GetRawBytesSize(ByteBuffer nativeHandler);
    public static native int GetRawHeight(ByteBuffer nativeHandler);
    public static native void SetGPSData(ByteBuffer nativeHandler,double Altitude,float[] Latitude,float[] Longitude, String Provider, long gpsTime);
    public static native void SetThumbData(ByteBuffer nativeHandler,byte[] mThumb, int widht, int height);
    public static native void WriteDNG(ByteBuffer nativeHandler);
    public static native void Release(ByteBuffer nativeHandler);
    public static native void SetOpCode3(ByteBuffer nativeHandler, byte[] opcode);
    public static native void SetOpCode2(ByteBuffer nativeHandler, byte[] opcode);
    public static native void SetRawHeight(ByteBuffer nativeHandler,int height);
    public static native void SetModelAndMake(ByteBuffer nativeHandler,String model, String make);
    public static native void SetBayerData(ByteBuffer nativeHandler,byte[] fileBytes, String fileout);
    public static native void SetBayerDataFD(ByteBuffer nativeHandler,byte[] fileBytes, int fileout, String filename);
    public static native void SetLensData(ByteBuffer nativeHandler,byte[] fileBytes, String hasLensData);
    public static native void SetBayerInfo(ByteBuffer nativeHandler,
                                            float[] colorMatrix1,
                                            float[] colorMatrix2,
                                            float[] neutralColor,
                                            float[] fowardMatrix1,
                                            float[] fowardMatrix2,
                                            float[] reductionMatrix1,
                                            float[] reductionMatrix2,
                                            float[] noiseMatrix,
                                            int blacklevel,
                                            String bayerformat,
                                            int rowSize,
                                            String devicename,
                                            int rawType,int width,int height);
    public static native ByteBuffer Create();
    public static native void SetExifData(ByteBuffer nativeHandler,
                                           int iso,
                                           double expo,
                                           int flash,
                                           float fNum,
                                           float focalL,
                                           String imagedescription,
                                           String orientation,
                                           double exposureIndex);
//**************************************************************************************************

    //YUVMerger Long Expo HOOKS
    public static native ByteBuffer storeYuvFrame(byte data[], int widht, int height);
    public static native void release(ByteBuffer nativeHandler);
    public static native void storeNextYuvFrame(ByteBuffer nativeHandler, byte data[]);
    public static native byte[] getMergedYuv(ByteBuffer nativeHandler, int count, byte arrayToFill[]);
    //*******************************************************************************************************

    //Libraw Hooks
    public static native byte[] unpackThumbnailBytes(String fileName);
    public static native Bitmap unpackRAW(String fileName);
    public static native void unpackRawByte(String fileName, byte[] xraw, int blackLevel,float aperture,float focalLength,float shutterSpeed,float iso);
    public static native byte[] BitmapExtractor(byte[] xraw, int blackLevel);
    public static native int unpackThumbnailToFile(String rawFileName, String thumbFileName);
    public static native void parseExif(String fileName, Object exifMap);
    //*****************************************************************************************************

    //Image Converter hooks
    public synchronized static native ByteBuffer INIT();
    public native void DrawToSurface(ByteBuffer nativeHandler, Surface view);
    public native void DrawToBitmap(ByteBuffer nativeHandler, Bitmap bitmap);
    public synchronized static native void YUVtoRGB(ByteBuffer nativeHandler,byte data[], int width, int height);
    public synchronized static native Bitmap GetBitmap(ByteBuffer nativeHandler);
    //private synchronized static native void Release(ByteBuffer nativeHandler);
    public synchronized static native int[] GetRgbData(ByteBuffer nativeHandler);
    public synchronized static native int[][] GetHistogram(ByteBuffer nativeHandler);
    public synchronized static native void ApplyHighPassFilter(ByteBuffer nativeHandler);
    public native static void loadJPEGtoARGB(ByteBuffer nativeHandler, String path);
    public native static void loadJPEGtoRGB(ByteBuffer nativeHandler, String path);
    public native static void unpackRAWtoRGB(ByteBuffer nativeHandler, String path);
    public native static void unpackRAWtoARGB(ByteBuffer nativeHandler, String path);
    public native static void stackAverageJPEGtoARGB(ByteBuffer nativeHandler, String path);
}
