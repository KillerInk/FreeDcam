package com.troop.androiddng;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.nio.ByteBuffer;

/**
 * Created by troop on 15.02.2015.
 */
public class RawToDng
{
    static
    {
        System.loadLibrary("RawToDng");
    }

    private static final String TAG = StringUtils.TAG + RawToDng.class.getSimpleName();

    public enum SupportedDevices
    {
        //tightraws             filesize  name                      blacklvl        matrix1     matrix2     neutral                     tight
        //G3_Mipi_KK(             16424960, "LG G3",                  g3_blacklevel,  g3_color1, g3_color2, g3_neutral, "bggr",4208,3120, true,   0),
        G3_Mipi_LL(             16224256, g3_blacklevel, "BGGR",4208,3082, true, getG3_rowSizeL),
        G3_Qcom(17326080, g3_blacklevel, "BGGR",4164,3120,false, getG3_rowSizeL),
        IMX135_214(             16424960, g3_blacklevel, "BGGR",4208,3120, true, g3_rowSizeKitKat),
        //G3_Qcom_LL(             17326080, "LG G3",                  g3_blacklevel,  g3_color1, g3_color2, g3_neutral, "bggr",4096,2592, false,   getG3_rowSizeL),
        //ElifeE7(                19906560, "Gionee Elife E7",        0,              g3_color1, g3_color2, g3_neutral, "grbg",4608,3456, true,   0),
        //OmniVision_OV5648(       6721536, "OmniVision_OV5648",      0,              g3_color1, g3_color2, g3_neutral, "grbg",2592,1944, true,   0),
        //looseraws
        XperiaL(                10788864 , 64,  "BGGR",3282,2448, false,  XperiaL_rowSize),
        OneSV(                6746112 , 0,  "BGGR",2592,1944, false,  XperiaL_rowSize),
        MT4G(                10782464 , 64,  "BGGR",3282,2448, false,  XperiaL_rowSize);
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

    //Rawsize =  10788864
    //RealSize = 10712448
    private static final int XperiaL_rowSize = 4376;

    public static String SonyXperiaLRawSize = "3282x2448";
    public static String Optimus3DRawSize = "2608x1944";

    public static String BGGR = "BGGR";
    private static final String GRBG = "GRBG";

    private static int Calculate_rowSize(int fileSize, int height)
    {
        return fileSize/height;
    }


    private ByteBuffer nativeHandler = null;
    private static native long GetRawBytesSize(ByteBuffer nativeHandler);
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

    private static native ByteBuffer CreateAndSetExifData(int iso,
                                                   double expo,
                                                   int flash,
                                                   float fNum,
                                                   float focalL,
                                                   String imagedescription,
                                                   String orientation,
                                                   double exposureIndex);
    private RawToDng(int iso,
                     double expo,
                     int flash,
                     float fNum,
                     float focalL,
                     String imagedescription,
                     String orientation,
                     double exposureIndex)
    {
        if (nativeHandler != null) {
            Release(nativeHandler);
            nativeHandler = null;
        }
        nativeHandler = CreateAndSetExifData(iso, expo,flash,fNum,focalL,imagedescription,orientation, exposureIndex);
    }

    public static RawToDng GetInstance(int iso,
                                       double expo,
                                       int flash,
                                       float fNum,
                                       float focalL,
                                       String imagedescription,
                                       String orientation,
                                       double exposureIndex)
    {
        return new RawToDng(iso, expo,flash,fNum,focalL,imagedescription,orientation, exposureIndex);
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

    public void SetBayerData(byte[] fileBytes, String fileout,int width,int height)
    {
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

    public void WriteDNG(int height, String picformat, int rawsize)
    {
        SetModelAndMake(Build.MODEL, Build.MANUFACTURER);
        if (DeviceUtils.isHTC_M8())
        {
            Log.d(TAG, "is htc m8 raw");
            //convertRawBytesToDng(data, fileToSave, width, height, nocal_color1, nocal_color2, nocal_nutral, 0, GRBG, RawToDng.HTCM8_rowSize, "HTC M8", true, iso, exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);
            SetBayerInfo(nocal_color1,nocal_color2,nocal_nutral,0,GRBG, HTCM8_rowSize, "HTC M8",true);
        }
        else
        {

            SupportedDevices device = SupportedDevices.GetValue(rawsize);
            if (device!= null)
            {
                Log.d(TAG, "is Hardcoded format: " + device.toString());
                //defcomg was here 24/01/2015 messed up if status with a random number
                if (rawsize == 164249650)
                {
                    SetBayerInfo(g3_color1, g3_color2, g3_neutral,device.blacklvl, device.imageformat, device.rowsize, Build.MODEL,device.tightraw);
                    setRawHeight(3120);
                    /*convertRawBytesToDng(data, fileToSave, device.width, 3120,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            Name, device.tightraw,iso, exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);*/
                }
                else
                {
                    if (device.tightraw) {
                        SetBayerInfo(g3_color1, g3_color2, g3_neutral, device.blacklvl, device.imageformat, device.rowsize, Build.MODEL, device.tightraw);
                        setRawHeight(device.height);
                    }
                    else
                    {
                        SetBayerInfo(g3_color1, g3_color2, g3_neutral, device.blacklvl, device.imageformat, Calculate_rowSize((int)GetRawSize(), height), Build.MODEL, device.tightraw);
                        setRawHeight(device.height);
                    }
                    /*SetBayerInfo(g3_color1, g3_color2, g3_neutral,device.blacklvl, device.imageformat, device.rowsize, Build.MODEL,device.tightraw);
                    convertRawBytesToDng(data, fileToSave, device.width, device.height,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            Name, device.tightraw,iso,exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);*/
                }
            }
            else
            {
                SetBayerInfo(g3_color1, g3_color2, g3_neutral, 0, picformat, Calculate_rowSize((int) GetRawSize(), height), Build.MODEL, true);
                setRawHeight(height);
                /*Log.d(TAG, "is default bayer format do calc the row size");
                Log.d(TAG, "rowsize :"+Calculate_rowSize(data.length, height));
                convertRawBytesToDng(data, fileToSave, width, height,
                        g3_color1, g3_color2, g3_neutral,
                        0, format, Calculate_rowSize(data.length, height),
                        Name, true,iso,exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);*/
            }

        }
        WriteDNG(nativeHandler);
        RELEASE();
    }

    private static short extractBits(final short x) {
        return (short)(0xFFFF & ((x & 0xFFFF) >>> 0 & -1 + (short)(1 << 10)));
    }

    public static byte[] SixTeenBit(final byte[] data,final int width,final int height)
    {
        int n = width /6;
        int n2 = 0;
        int n3 = 0;

        //OUTPUT Array
        final byte[] dataOut = new byte[2 * (width * height)];

        //Stride working byte Array
        final byte[] strideByteArray = new byte[2 * (width + 10)];

        while(true)
        {
            int n4;
            if (width % 6 == 0 )
            {
                n4 = width;
            }
            else
            {
                n4 = width;
            }

            if (n3 >= n4 * height / (n * 6))
            {
                break;
            }
            int n5 = 0;

            for (int i = n3; i < n3 + n; i++)
            {
                final short n6 = (short)(0xFFFF & ((0xFF & data[n2 + 1]) << 8 | (0xFF & data[n2 + 0])));
                final short bits = extractBits(n6);
                strideByteArray[n5] = (byte)(bits & 0xFF);
                strideByteArray[n5 + 1] = (byte)(0xFF & (0xFFFF & bits) >>> 8);
                final short n7 = (short)((0xFFFF & n6) >>> 10);
                final int n8 = n5 + 2;
                final short n9 = (short)(0xFFFF & ((0xFF & data[n2 + 2]) << 6 | (0xFFFF & n7)));
                final short bits2 = extractBits(n9);
                strideByteArray[n8] = (byte)(bits2 & 0xFF);
                strideByteArray[n8 + 1] = (byte)(0xFF & (0xFFFF & bits2) >>> 8);
                final short n10 = (short)((0xFFFF & n9) >>> 10);
                final int n11 = n8 + 2;
                final short n12 = (short)(0xFFFF & ((0xFF & data[n2 + 3]) << 4 | (0xFFFF & n10)));
                final short bits3 = extractBits(n12);
                strideByteArray[n11] = (byte)(bits3 & 0xFF);
                strideByteArray[n11 + 1] = (byte)(0xFF & (0xFFFF & bits3) >>> 8);
                final short n13 = (short)((0xFFFF & n12) >>> 10);
                final int n14 = n11 + 2;
                final short n15 = (short)(0xFFFF & ((0xFF & data[n2 + 4]) << 2 | (0xFFFF & n13)));
                final short bits4 = extractBits(n15);
                strideByteArray[n14] = (byte)(bits4 & 0xFF);
                strideByteArray[n14 + 1] = (byte)(0xFF & (0xFFFF & bits4) >>> 8);
                final short n16 = (short)((0xFFFF & n15) >>> 10);
                final int n17 = n14 + 2;
                final short n18 = (short)(0xFFFF & ((0xFF & data[n2 + 6]) << 8 | (0xFF & data[n2 + 5])));
                final short bits5 = extractBits(n18);
                strideByteArray[n17] = (byte)(bits5 & 0xFF);
                strideByteArray[n17 + 1] = (byte)(0xFF & (0xFFFF & bits5) >>> 8);
                final short n19 = (short)((0xFFFF & n18) >>> 10);
                final int n20 = n17 + 2;
                final short n21 = (short)(0xFFFF & ((0xFF & data[n2 + 7]) << 6 | (0xFFFF & n19)));
                final short bits6 = extractBits(n21);
                strideByteArray[n20] = (byte)(bits6 & 0xFF);
                strideByteArray[n20 + 1] = (byte)(0xFF & (0xFFFF & bits6) >>> 8);
                final short n22 = (short)((0xFFFF & n21) >>> 10);
                n5 = n20 + 2;
                n2 += 8;
            }

            System.arraycopy(strideByteArray ,0, dataOut, 2 *(n3 * width), width * 2);
            ++n3;
        }
        return dataOut;

    }

}
