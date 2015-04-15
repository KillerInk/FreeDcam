package com.troop.androiddng;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.utils.DeviceUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by troop on 15.02.2015.
 */
public class RawToDng
{

    public int OverWriteRowSize = -1;

    static
    {
        System.loadLibrary("RawToDng");
    }

    private static final String TAG = RawToDng.class.getSimpleName();

    public enum SupportedDevices
    {                           //size       blacklevlel   pattern  width  height  tight    rowsize=filesize/height = 0
        G3_Mipi_LL(             16224256,   g3_blacklevel,  BGGR,    4208, 3082,    true,    getG3_rowSizeL),
        G3_Qcom(                17326080,   g3_blacklevel,  BGGR,    4164, 3120,    false,   getG3_rowSizeL),
        K910Qcom(               17522688,   g3_blacklevel,  BGGR,    4212, 3120,    false,   getG3_rowSizeL),
        IMX135_214(             16424960,   g3_blacklevel,  BGGR,    4208, 3120,    true,    g3_rowSizeKitKat),
        //G3_Qcom_LL(             17326080, "LG G3",                  g3_blacklevel,  g3_color1, g3_color2, g3_neutral, "bggr",4096,2592, false,   getG3_rowSizeL),
        //ElifeE7(                19906560, "Gionee Elife E7",        0,              g3_color1, g3_color2, g3_neutral, "grbg",4608,3456, true,   0),
        //OmniVision_OV5648(       6721536, "OmniVision_OV5648",      0,              g3_color1, g3_color2, g3_neutral, "grbg",2592,1944, true,   0),
        //looseraws
        XperiaL(                10788864,   g3_blacklevel,  BGGR,    3282, 2448,    false,   XperiaL_rowSize),
        OneSV(                  6746112 ,   0,              BGGR,    2592, 1944,    false,   XperiaL_rowSize),
        MT4G(                   10782464,   g3_blacklevel,  RGGb,    3282, 2448,    false,   XperiaL_rowSize),
        HtcOneSv(               6746112 ,   0,              GRBG,    2592, 1952,    false,   0),
        M9Mipi(                 25677824,   g3_blacklevel,  GRBG,    5388, 3752,    true,    0),
        M9Qcom(                 27127808,   g3_blacklevel,  GRBG,    5388, 3752,    false,   0);
        //OmniVision_OV5648_1(    6721536,  "OmniVision_OV5648_1",    0,              g3_color1, g3_color2, g3_neutral, "grbg",2592,1944, false,  0),
        //HTCOneSV(               6746112,  "HTCOneSV",               0,              g3_color1, g3_color2, g3_neutral, "grbg",2592,1944, false,  0),
        //HTC_MyTouch_4G_Slide(   10782464, "HTC_MyTouch_4G_Slide",   0,              g3_color1, g3_color2, g3_neutral, "grbg",3282,2448, false,  0);


        private final int filesize;
        private final int blacklvl;
        private final String imageformat;
        public final int width;
        public final int height;
        final boolean tightraw;
        //if rowsize = 0calculate it!
        final int rowsize;

        SupportedDevices(int filesize,
                         int blacklvl,
                         String imageformat,
                         int width,
                         int height,
                         boolean tightraw,
                         int rowsize)
        {
            this.filesize = filesize;
            this.blacklvl = blacklvl;
            this.imageformat = imageformat;
            this.width = width;
            this.height = height;
            this.tightraw = tightraw;
            this.rowsize = rowsize;
        }

        public static SupportedDevices GetValue(int _id)
        {
            SupportedDevices[] As = SupportedDevices.values();
            for(int i = 0; i < As.length; i++)
            {
                if(As[i].filesize == _id)
                    return As[i];
            }
            return null;
        }
    }

    private static final float[] g3_color1 =
            {
                    (float) 0.9218606949, (float) 0.0263967514, (float) -0.1110496521,
                    (float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
                    (float) -0.05432224274, (float) 0.2319784164, (float) 0.2338542938
            };

    //Color Matrix 1                  : 0.9218606949 0.0263967514 -0.1110496521 -0.3331432343 1.179347992 0.1260938644 -0.05432224274 0.2319784164 0.2338542938
    //

    private static final float[] g3_color2 =
            {
                    (float) 0.6053285599, (float) 0.0173330307, (float) -0.07291889191,
                    (float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
                    (float) -0.0853471756, (float) 0.3644628525, (float) 0.3674106598
            };

    private static final float[] g3_neutral =
            {
                    (float) 0.3566446304, (float) 0.613401413, (float) 0.3468151093
            };

    private static final float[] nocal_color1 =
            {
                    (float) 1.000, (float) 0.000, (float) 0.000,
                    (float) 0.000, (float) 1.000, (float) 0.000,
                    (float) 0.000, (float) 0.000, (float) 1.000
            };

    private static final float[] nocal_color2 =
            {
                    (float) 1.000, (float) 0.000, (float) 0.000,
                    (float) 0.000, (float) 1.000, (float) 0.000,
                    (float) 0.000, (float) 0.000, (float) 1.000
            };

    private static final float[] nocal_nutral =
            {
                    (float) 1.0, (float) 1.0, (float) 1.0
            };

    private static final int g3_blacklevel = 64;

    //16424960,4208,3120
    private static final int g3_rowSizeKitKat = 5264;
    //16224256,4152,3072
    private static final int getG3_rowSizeL = 5264;


    private static final int HTCM8_rowSize = 3360;
    private static String HTCM8_Size= "2688x1520";

    //Rawsize =  10788864
    //RealSize = 10712448
    private static final int XperiaL_rowSize = 4376;

    public static String SonyXperiaLRawSize = "3282x2448";
    public static String Optimus3DRawSize = "2608x1944";

    public static String BGGR = "bggr";
    public static String RGGb = "rggb";
    private static final String GRBG = "grbg";

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
    private static native void SetBayerData(ByteBuffer nativeHandler,byte[] fileBytes, String fileout,int width,int height);
    private static native void SetBayerInfo(ByteBuffer nativeHandler,
                                     float[] colorMatrix1,
                                     float[] colorMatrix2,
                                     float[] neutralColor,
                                     int blacklevel,
                                     String bayerformat,
                                     int rowSize,
                                     String devicename,
                                     boolean tight);

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
            SetGPSData(nativeHandler, Altitude,parseGpsvalue(Latitude),parseGpsvalue(Longitude),Provider,gpsTime);
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
        SetExifData(nativeHandler,iso,expo,flash,fNum,focalL,imagedescription,orientation,exposureIndex);
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
            SetModelAndMake(nativeHandler,model,make);
    }

    public void SetBayerData(final byte[] fileBytes, String fileout,int width,int height)
    {
        filepath = fileout;
        if (filepath.contains("bayer"))
            bayerpattern = filepath.substring(filepath.length() - 8, filepath.length() -4);
        if (nativeHandler != null)
            SetBayerData(nativeHandler, fileBytes, fileout, width,height);
    }

    private void SetBayerInfo(float[] colorMatrix1,
                             float[] colorMatrix2,
                             float[] neutralColor,
                             int blacklevel,
                             String bayerformat,
                             int rowSize,
                             String devicename,
                             boolean tight)
    {
        if (nativeHandler != null)
            SetBayerInfo(nativeHandler, colorMatrix1, colorMatrix2, neutralColor, blacklevel, bayerformat, rowSize, devicename, tight);
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

    public void WriteDNG()
    {

        SetModelAndMake(Build.MODEL, Build.MANUFACTURER);
        if (DeviceUtils.isHTC_M8())
        {
            if (filepath.contains("qcom")) {
                SetBayerInfo(nocal_color1, nocal_color2, nocal_nutral, 0, GRBG, Calculate_rowSize((int) GetRawSize(), 1520), "HTC M8", false);
                setRawHeight(1520);
            }
            else {
                Log.d(TAG, "is htc m8 raw");
                //convertRawBytesToDng(data, fileToSave, width, height, nocal_color1, nocal_color2, nocal_nutral, 0, GRBG, RawToDng.HTCM8_rowSize, "HTC M8", true, iso, exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);
                SetBayerInfo(nocal_color1, nocal_color2, nocal_nutral, 0, GRBG, HTCM8_rowSize, "HTC M8", true);
                setRawHeight(1520);
            }

        }
        else
        {

            final SupportedDevices device = SupportedDevices.GetValue((int)GetRawSize());
            if (device!= null)
            {
                int rowsize = 0;

                if (OverWriteRowSize == -1)
                {
                    if (device.rowsize > 0)
                        rowsize = device.rowsize;
                }
                else
                    rowsize = OverWriteRowSize;
                Log.d(TAG, "is Hardcoded format: " + device.toString());
                //defcomg was here 24/01/2015 messed up if status with a random number
                if (GetRawSize() == 164249650 && !DeviceUtils.isLGADV())
                {
                    SetBayerInfo(g3_color1, g3_color2, g3_neutral,device.blacklvl, device.imageformat, rowsize, Build.MODEL,device.tightraw);
                    setRawHeight(3120);
                    Log.d(TAG, "mipi");
                    /*convertRawBytesToDng(data, fileToSave, device.width, 3120,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            Name, device.tightraw,iso, exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);*/
                }
                else
                {
                    if (device.tightraw)
                    {
                        Log.d(TAG, "mipi");
                        SetBayerInfo(g3_color1, g3_color2, g3_neutral, device.blacklvl, device.imageformat, rowsize, Build.MODEL, device.tightraw);
                        setRawHeight(device.height);
                    }
                    else
                    {
                        if (filepath.contains("ideal-qcom")) {
                            Log.d(TAG, "ideal");
                            SetBayerInfo(g3_color1, g3_color2, g3_neutral, 0, device.imageformat, rowsize, Build.MODEL, device.tightraw);
                            setRawHeight(device.height);
                        }
                        else
                        {
                            Log.d(TAG, "qcom");
                            SetBayerInfo(g3_color1, g3_color2, g3_neutral, device.blacklvl, device.imageformat, rowsize, Build.MODEL, device.tightraw);
                            setRawHeight(device.height);
                        }
                    }
                }
            }
            else
            {
                if (filepath.contains("qcom") || filepath.contains("raw"))
                {
                    Log.d(TAG, "qcom/ideal");
                    SetBayerInfo(nocal_color1, nocal_color2, nocal_nutral, 0, bayerpattern, 0, Build.MODEL, false);
                    setRawHeight(GetRawHeight(nativeHandler));
                }
                else {
                    Log.d(TAG, "mipi");
                    SetBayerInfo(g3_color1, g3_color2, g3_neutral, 0, bayerpattern, 0, Build.MODEL, true);
                    setRawHeight(GetRawHeight(nativeHandler));
                }
            }

        }
        WriteDNG(nativeHandler);
        RELEASE();
        //System.gc();
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
