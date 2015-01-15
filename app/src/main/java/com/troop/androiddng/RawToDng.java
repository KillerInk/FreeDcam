package com.troop.androiddng;

import android.os.Build;

import com.troop.freedcam.utils.DeviceUtils;

public class RawToDng 
{
	static
    {
		System.loadLibrary("RawToDng");
    }

    enum SupportedDevices
    {
        //tightraws             filesize  name                      blacklvl        matrix1     matrix2     neutral                     tight
        G3_Mipi_KK(             16424960, "LG G3",                  g3_blacklevel,  g3_color1, g3_color2, g3_neutral, "bggr",4208,3120, true,   g3_rowSizeKitKat),
        G3_Mipi_LL(             16224256, "LG G3",                  g3_blacklevel,  g3_color1, g3_color2, g3_neutral, "bggr",4208,3082, true,   getG3_rowSizeL),
        G3_Qcom_LL(             17326080, "LG G3",                  g3_blacklevel,  g3_color1, g3_color2, g3_neutral, "bggr",4096,2592, false,   getG3_rowSizeL),
        ElifeE7(                19906560, "Gionee Elife E7",        0,              g3_color1, g3_color2, g3_neutral, "grbg",4608,3456, true,   0),
        OmniVision_OV5648(       6721536, "OmniVision_OV5648",      0,              g3_color1, g3_color2, g3_neutral, "grbg",2592,1944, true,   0),
        //looseraws
        XperiaL(                10788864, "Sony XperiaL",           0,              g3_color1, g3_color2, g3_neutral, "grbg",3282,2448, false,  XperiaL_rowSize),
        OmniVision_OV5648_1(    6721536,  "OmniVision_OV5648_1",    0,              g3_color1, g3_color2, g3_neutral, "grbg",2592,1944, false,  0),
        HTCOneSV(               6746112,  "HTCOneSV",               0,              g3_color1, g3_color2, g3_neutral, "grbg",2592,1944, false,  0),
        HTC_MyTouch_4G_Slide(   10782464, "HTC_MyTouch_4G_Slide",   0,              g3_color1, g3_color2, g3_neutral, "grbg",3282,2448, false,  0);


        private final int filesize;
        private final String Name;
        private final int blacklvl;
        private final float[] colormatrix1;
        private final float[] colormatrix2;
        private final float[] neutralmatrix;
        private final String imageformat;
        private final int width;
        private final int height;
        private final boolean tightraw;
        //if rowsize = 0calculate it!
        private final int rowsize;

        private SupportedDevices(int filesize,
                                 String Name,
                                 int blacklvl,
                                 float[] colormatrix1,
                                 float[] colormatrix2,
                                 float[] neutralmatrix,
                                 String imageformat,
                                 int width,
                                 int height,
                                 boolean tightraw,
                                 int rowsize)
        {
            this.filesize = filesize;
            this.Name = Name;
            this.blacklvl = blacklvl;
            this.colormatrix1 = colormatrix1;
            this.colormatrix2 = colormatrix2;
            this.neutralmatrix = neutralmatrix;
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
	
	public static native void convertRawBytesToDng(
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
            boolean tightraw);

    public static void ConvertRawBytesToDng(
            byte[] data,
            String fileToSave,
            int width,
            int height
    )
    {
        if (DeviceUtils.isHTC_M8())
            convertRawBytesToDng(data, fileToSave, width, height, g3_color1, g3_color2, g3_neutral, 0, GRBG, RawToDng.HTCM8_rowSize, "HTC M8", false);
        else
        {
            SupportedDevices device = SupportedDevices.GetValue(data.length);
            if (device!= null)
            {
                if (device.rowsize == 0)
                {
                    convertRawBytesToDng(data, fileToSave, device.width, device.height,
                            device.colormatrix1, device.colormatrix2, device.neutralmatrix,
                            device.blacklvl, device.imageformat, Calculate_rowSize(data.length, device.height),
                            device.Name, device.tightraw);
                }
                else
                {
                    convertRawBytesToDng(data, fileToSave, device.width, device.height,
                            device.colormatrix1, device.colormatrix2, device.neutralmatrix,
                            device.blacklvl, device.imageformat, device.rowsize,
                            device.Name, device.tightraw);
                }
            }

        }
    }
	
	public static float[] g3_color1 =
	{
		(float) 0.9218606949, (float) 0.0263967514, (float) -0.1110496521,
		(float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
		(float) -0.05432224274, (float) 0.2319784164, (float) 0.2338542938
	};
	
	//Color Matrix 1                  : 0.9218606949 0.0263967514 -0.1110496521 -0.3331432343 1.179347992 0.1260938644 -0.05432224274 0.2319784164 0.2338542938
	//
	
	public static  float[] g3_color2 =
	{
		 (float) 0.6053285599, (float) 0.0173330307, (float) -0.07291889191,
		 (float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
		 (float) -0.0853471756, (float) 0.3644628525, (float) 0.3674106598
	};
	
	public static float[] g3_neutral = 
	{
		(float) 0.3566446304, (float) 0.613401413, (float) 0.3468151093
	};
	
	public static int g3_blacklevel = 64;

    //16424960,4208,3120
    public static int g3_rowSizeKitKat = 5264;
    //16224256,4152,3072
    public static int getG3_rowSizeL = 5264;
	
	
	public static int HTCM8_rowSize = 3360;

    //Rawsize =  10788864
    //RealSize = 10712448
    public static int XperiaL_rowSize = 4376;

    public static String SonyXperiaLRawSize = "3282x2448";
    public static String Optimus3DRawSize = "2608x1944";

    public static String BGGR = "bggr";
    public static String GRBG = "grbg";
	
	public static int Calculate_rowSize(int fileSize, int height)
	{
		return fileSize/height;
	}
}
