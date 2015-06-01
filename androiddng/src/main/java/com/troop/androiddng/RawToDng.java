package com.troop.androiddng;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.utils.DeviceUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import javax.crypto.spec.DESedeKeySpec;

/**
 * Created by troop on 15.02.2015.
 */
public class RawToDng
{
    static
    {
        System.loadLibrary("RawToDng");
    }

    private static final String TAG = RawToDng.class.getSimpleName();

    private static int Calculate_rowSize(int fileSize, int height)
    {
        return fileSize/height;
    }


    private ByteBuffer nativeHandler = null;
    private static native long GetRawBytesSize(ByteBuffer nativeHandler);
    private static native int GetRawHeight(ByteBuffer nativeHandler);
    private static native void SetGPSData(ByteBuffer nativeHandler,double Altitude,float[] Latitude,float[] Longitude, String Provider, long gpsTime);
    private static native void SetThumbData(ByteBuffer nativeHandler,byte[] mThumb, int widht, int height);
    private static native void WriteDNG(ByteBuffer nativeHandler);
    private static native void Release(ByteBuffer nativeHandler);
    private static native void SetRawHeight(ByteBuffer nativeHandler,int height);
    private static native void SetModelAndMake(ByteBuffer nativeHandler,String model, String make);
    private static native void SetBayerData(ByteBuffer nativeHandler,byte[] fileBytes, String fileout);
    private static native void SetBayerInfo(ByteBuffer nativeHandler,
                                     float[] colorMatrix1,
                                     float[] colorMatrix2,
                                     float[] neutralColor,
                                     int blacklevel,
                                     String bayerformat,
                                     int rowSize,
                                     String devicename,
                                     boolean tight,int width,int height);

    private static native ByteBuffer Create();
    private static native void SetExifData(ByteBuffer nativeHandler,
                                           int iso,
                                           double expo,
                                           int flash,
                                           float fNum,
                                           float focalL,
                                           String imagedescription,
                                           String orientation,
                                           double exposureIndex);

    private RawToDng()
    {
        if (nativeHandler != null) {
            Release(nativeHandler);
            nativeHandler = null;
        }
        nativeHandler = Create();
    }
    String filepath;
    String bayerpattern;

    public static RawToDng GetInstance()
    {
        return new RawToDng();
    }

    public long GetRawSize()
    {
        return GetRawBytesSize(nativeHandler);
    }

    public void SetGPSData(double Altitude,double Latitude,double Longitude, String Provider, long gpsTime)
    {
        Log.d(TAG,"Latitude:" + Latitude + "Longitude:" +Longitude);
        if (nativeHandler != null)
            SetGPSData(nativeHandler, Altitude, parseGpsvalue(Latitude), parseGpsvalue(Longitude), Provider, gpsTime);
    }

    public void setExifData(int iso,
                            double expo,
                            int flash,
                            float fNum,
                            float focalL,
                            String imagedescription,
                            String orientation,
                            double exposureIndex)
    {
        if (nativeHandler != null)
        SetExifData(nativeHandler, iso, expo, flash, fNum, focalL, imagedescription, orientation, exposureIndex);
    }

    private float[] parseGpsvalue(double val)
    {

        final String[] sec = Location.convert(val, Location.FORMAT_SECONDS).split(":");

        final double dd = Double.parseDouble(sec[0]);
        final double dm = Double.parseDouble(sec[1]);
        final double ds = Double.parseDouble(sec[2].replace(",","."));

        final float[] Longitudear = { (float)dd ,(float)dm,(float)ds};
        return Longitudear;
    }

    public void SetThumbData(byte[] mThumb, int widht, int height)
    {
        if (nativeHandler != null)
        {
            SetThumbData(nativeHandler,mThumb, widht,height);
        }
    }

    public void SetModelAndMake(String model, String make)
    {
        if (nativeHandler !=null)
            SetModelAndMake(nativeHandler, model, make);
    }

    public void SetBayerData(final byte[] fileBytes, String fileout)
    {
        filepath = fileout;
        if (filepath.contains("bayer"))
            bayerpattern = filepath.substring(filepath.length() - 8, filepath.length() -4);
        if (nativeHandler != null)
            SetBayerData(nativeHandler, fileBytes, fileout);
    }

    private void SetBayerInfo(float[] colorMatrix1,
                             float[] colorMatrix2,
                             float[] neutralColor,
                             int blacklevel,
                             String bayerformat,
                             int rowSize,
                             String devicename,
                             boolean tight,int width,int height)
    {
        if (nativeHandler != null)
            SetBayerInfo(nativeHandler, colorMatrix1, colorMatrix2, neutralColor, blacklevel, bayerformat, rowSize, devicename, tight,width,height);
    }

    public void RELEASE()
    {
        if (nativeHandler !=null)
        {
            Release(nativeHandler);
            nativeHandler = null;
        }
    }

    private void setRawHeight(int height)
    {
        if (nativeHandler != null)
            SetRawHeight(nativeHandler, height);
    }

    private DngSupportedDevices.SupportedDevices getDevice()
    {
        if (DeviceUtils.isYureka())
            return DngSupportedDevices.SupportedDevices.yureka;
        if (DeviceUtils.isLGADV())
            return DngSupportedDevices.SupportedDevices.LG_G3;
        if (DeviceUtils.isGioneE7())
            return DngSupportedDevices.SupportedDevices.Gione_E7;
        if (DeviceUtils.isHTC_M8())
            return DngSupportedDevices.SupportedDevices.HTC_One_m8;
        if (DeviceUtils.isHTC_M9())
            return DngSupportedDevices.SupportedDevices.HTC_One_m9;
        if (DeviceUtils.isHtc_One_SV())
            return DngSupportedDevices.SupportedDevices.HTC_One_Sv;
        if (DeviceUtils.isHtc_One_XL())
            return DngSupportedDevices.SupportedDevices.HTC_One_XL;
        if (DeviceUtils.isLenovoK910())
            return DngSupportedDevices.SupportedDevices.Lenovo_k910;
        if(DeviceUtils.isG2())
            return DngSupportedDevices.SupportedDevices.LG_G2;
        if (DeviceUtils.hasIMX135())
            return DngSupportedDevices.SupportedDevices.zteAdv;
        if (DeviceUtils.isXperiaL())
            return DngSupportedDevices.SupportedDevices.Sony_XperiaL;
        if(DeviceUtils.hasIMX214())
            return DngSupportedDevices.SupportedDevices.OnePlusOne;
        if (DeviceUtils.isRedmiNote())
            return DngSupportedDevices.SupportedDevices.Xiaomi_Redmi_Note;
        return null;
    }

    public void WriteDNG(DngSupportedDevices.SupportedDevices device)
    {
        DngSupportedDevices.SupportedDevices devices = device;
        if (device == null)
            devices = getDevice();
        else
            devices = device;

        if (devices != null)
        {
            DngSupportedDevices.DngProfile profile = new DngSupportedDevices().getProfile(devices, (int)GetRawSize());
            //if (profile.rowsize == 0)
                //profile.rowsize = Calculate_rowSize((int)GetRawSize(), profile.height);
            SetModelAndMake(Build.MODEL, Build.MANUFACTURER);
            SetBayerInfo(profile.matrix1, profile.matrix2, profile.neutral,profile.blacklevel, profile.BayerPattern, profile.rowsize, Build.MODEL,profile.isTightRAw,profile.widht,profile.height);
            WriteDNG(nativeHandler);
            RELEASE();
        }
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
