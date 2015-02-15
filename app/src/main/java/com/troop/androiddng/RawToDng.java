package com.troop.androiddng;

import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera.modules.HdrModule;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

public class RawToDng 
{
	static
    {
		System.loadLibrary("RawToDng");
    }

    private static final String TAG = StringUtils.TAG + RawToDng.class.getSimpleName();

    enum SupportedDevices
    {
        //tightraws             filesize  name                      blacklvl        matrix1     matrix2     neutral                     tight
        //G3_Mipi_KK(             16424960, "LG G3",                  g3_blacklevel,  g3_color1, g3_color2, g3_neutral, "bggr",4208,3120, true,   0),
        G3_Mipi_LL(             16224256, g3_blacklevel, "BGGR",4208,3082, true, getG3_rowSizeL),
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
        private final int width;
        private final int height;
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

	private static native void convertRawBytesToDng(
			byte[] data,
			String fileToSave,
			int width,
			int height,
			float[] colorMatrix1,
			float[] colorMatrix2,
			float[] neutral,
			int blacklevel,
			String bayerformat,
			int rowSize,
            String deviceName,
            boolean tightraw,
            int iso,
            double exposuretime,
            String make,
            String model,
            int flash,
            float apeture,
            float focal,
            String iDESC,
            byte[] thumb,
            String ori,
            double Altitude,
            double Latitude,
            double Longitude,
            String Provider,
            long gpsTime);


    private static native void convertRawBytesToDngFast(
            byte[] data,
            String fileToSave,
            int width,
            int height,
            float[] colorMatrix1,
            float[] colorMatrix2,
            float[] neutral,
            int blacklevel,
            String bayerformat,
            int rowSize,
            boolean tightraw,
            String make,
            String Model);

    private static native void convertRawBytesToDngFastHDR2(
            byte[] data,
            String fileToSave,
            int width,
            int height,
            float[] colorMatrix1,
            float[] colorMatrix2,
            float[] neutral,
            int blacklevel,
            String bayerformat,
            int rowSize,
            boolean tightraw,
            String make,
            String Model);


    private static native void convertRawBytesToDngFastHDR3(
            byte[] data,
            String fileToSave,
            int width,
            int height,
            float[] colorMatrix1,
            float[] colorMatrix2,
            float[] neutral,
            int blacklevel,
            String bayerformat,
            int rowSize,
            boolean tightraw,
            String make,
            String Model);






    public void ConvertRawBytesToDng(
            byte[] data,
            String fileToSave,
            int width,
            int height,
            String Name,
            int iso,
            double exposure,
            String format,
            int Flash,
            float Aperture,
            float Focal,
            String IDESC,
            byte[] Thumb,
            String orr,
            boolean Tight,
            double Altitude,
            double Latitude,
            double Longitude,
            String Provider,
            long gpsTime
    )
    {
        if (DeviceUtils.isHTC_M8())
        {
            Log.d(TAG, "is htc m8 raw");
            convertRawBytesToDng(data, fileToSave, width, height, nocal_color1, nocal_color2, nocal_nutral, 0, GRBG, RawToDng.HTCM8_rowSize, "HTC M8", true, iso, exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);
        }
        else
        {

            SupportedDevices device = SupportedDevices.GetValue(data.length);
            if (device!= null)
            {
                Log.d(TAG, "is Hardcoded format: " + device.toString());
                //defcomg was here 24/01/2015 messed up if status with a random number
                if (data.length == 164249650)
                {
                    convertRawBytesToDng(data, fileToSave, device.width, 3120,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            Name, device.tightraw,iso, exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);
                }
                else
                {
                    convertRawBytesToDng(data, fileToSave, device.width, device.height,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            Name, device.tightraw,iso,exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);
                }
            }
            else
            {
                Log.d(TAG, "is default bayer format do calc the row size");
                Log.d(TAG, "rowsize :"+Calculate_rowSize(data.length, height));
                convertRawBytesToDng(data, fileToSave, width, height,
                        g3_color1, g3_color2, g3_neutral,
                        0, format, Calculate_rowSize(data.length, height),
                        Name, true,iso,exposure,Build.MANUFACTURER,Build.MODEL,Flash,Aperture,Focal,IDESC,Thumb,orr,Altitude,Latitude,Longitude,Provider, gpsTime);
            }

        }
    }



    public static void ConvertRawBytesToDngFast(
            byte[] data,
            String fileToSave,
            int width,
            int height,
            String format
    )
    {

        Log.d(TAG, "Start Converting to DNG");
        Log.d(TAG, "filesize:" + data.length);
        Log.d(TAG, "W x H : "+ width + "x" +height);

        Log.d(TAG, "Looking for RawConverting");
        if (DeviceUtils.isHTC_M8())
        {
            Log.d(TAG, "is htc m8 raw");
            convertRawBytesToDngFast(data, fileToSave, width, height, g3_color1, g3_color2, null, 0, GRBG, RawToDng.HTCM8_rowSize,true,Build.MANUFACTURER,Build.MODEL);
            synchronized (HdrModule.DoNext)
            {
                HdrModule.DoNext.notify();
            }
        }
        else
        {

            SupportedDevices device = SupportedDevices.GetValue(data.length);
            if (device!= null)
            {
                Log.d(TAG, "is Hardcoded format: " + device.toString());
                if (Build.MODEL.equals("Lenovo K910"))
                {
                    convertRawBytesToDngFast(data, fileToSave, device.width, 3120,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                             device.tightraw,Build.MANUFACTURER,Build.MODEL);
                    synchronized (HdrModule.DoNext)
                    {
                        HdrModule.DoNext.notify();
                    }
                }
                else
                {
                    convertRawBytesToDngFast(data, fileToSave, device.width, device.height,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            device.tightraw,Build.MANUFACTURER,Build.MODEL);
                    synchronized (HdrModule.DoNext)
                    {
                        HdrModule.DoNext.notify();
                    }

                }
            }
            else
            {
                Log.d(TAG, "is default bayer format do calc the row size");
                Log.d(TAG, "rowsize :"+Calculate_rowSize(data.length, height));
                convertRawBytesToDngFast(data, fileToSave, width, height,
                        g3_color1, g3_color2, g3_neutral,
                        0, format, Calculate_rowSize(data.length, height),
                        true,Build.MANUFACTURER,Build.MODEL);
                synchronized (HdrModule.DoNext)
                {
                    HdrModule.DoNext.notify();
                }
            }

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

    public static void ConvertRawBytesToDngFastHDR2(
            byte[] data,
            String fileToSave,
            int width,
            int height,
            String format
    )
    {

        Log.d(TAG, "Start Converting to DNG");
        Log.d(TAG, "filesize:" + data.length);
        Log.d(TAG, "W x H : "+ width + "x" +height);

        Log.d(TAG, "Looking for RawConverting");
        if (DeviceUtils.isHTC_M8())
        {
            Log.d(TAG, "is htc m8 raw");
            convertRawBytesToDngFastHDR2(data, fileToSave, width, height, g3_color1, g3_color2, null, 0, GRBG, RawToDng.HTCM8_rowSize,true,Build.MANUFACTURER,Build.MODEL);
        }
        else
        {

            SupportedDevices device = SupportedDevices.GetValue(data.length);
            if (device!= null)
            {
                Log.d(TAG, "is Hardcoded format: " + device.toString());
                if (Build.MODEL.equals("Lenovo K910"))
                {
                    convertRawBytesToDngFastHDR2(data, fileToSave, device.width, 3120,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            device.tightraw,Build.MANUFACTURER,Build.MODEL);
                }
                else
                {
                    convertRawBytesToDngFastHDR2(data, fileToSave, device.width, device.height,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            device.tightraw,Build.MANUFACTURER,Build.MODEL);
                }
            }
            else
            {
                Log.d(TAG, "is default bayer format do calc the row size");
                Log.d(TAG, "rowsize :"+Calculate_rowSize(data.length, height));
                convertRawBytesToDngFastHDR2(data, fileToSave, width, height,
                        g3_color1, g3_color2, g3_neutral,
                        0, format, Calculate_rowSize(data.length, height),
                        true,Build.MANUFACTURER,Build.MODEL);
            }

        }


    }

    public static void ConvertRawBytesToDngFastHDR3(
            byte[] data,
            String fileToSave,
            int width,
            int height,
            String format
    )
    {

        Log.d(TAG, "Start Converting to DNG");
        Log.d(TAG, "filesize:" + data.length);
        Log.d(TAG, "W x H : "+ width + "x" +height);

        Log.d(TAG, "Looking for RawConverting");
        if (DeviceUtils.isHTC_M8())
        {
            Log.d(TAG, "is htc m8 raw");
            convertRawBytesToDngFastHDR3(data, fileToSave, width, height, g3_color1, g3_color2, null, 0, GRBG, RawToDng.HTCM8_rowSize,true,Build.MANUFACTURER,Build.MODEL);
        }
        else
        {

            SupportedDevices device = SupportedDevices.GetValue(data.length);
            if (device!= null)
            {
                Log.d(TAG, "is Hardcoded format: " + device.toString());
                if (Build.MODEL.equals("Lenovo K910"))
                {
                    convertRawBytesToDngFastHDR3(data, fileToSave, device.width, 3120,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            device.tightraw,Build.MANUFACTURER,Build.MODEL);
                }
                else
                {
                    convertRawBytesToDngFastHDR3(data, fileToSave, device.width, device.height,
                            g3_color1, g3_color2, g3_neutral,
                            device.blacklvl, device.imageformat, device.rowsize,
                            device.tightraw,Build.MANUFACTURER,Build.MODEL);
                }
            }
            else
            {
                Log.d(TAG, "is default bayer format do calc the row size");
                Log.d(TAG, "rowsize :"+Calculate_rowSize(data.length, height));
                convertRawBytesToDngFastHDR3(data, fileToSave, width, height,
                        g3_color1, g3_color2, g3_neutral,
                        0, format, Calculate_rowSize(data.length, height),
                        true,Build.MANUFACTURER,Build.MODEL);
            }

        }


    }


}
