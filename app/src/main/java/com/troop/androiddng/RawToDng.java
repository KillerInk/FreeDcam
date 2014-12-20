package com.troop.androiddng;

import android.os.Build;

import com.troop.freedcam.utils.DeviceUtils;

public class RawToDng 
{
	static
    {
		System.loadLibrary("RawToDng");
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
			int rowSize);

    public static void ConvertRawBytesToDng(
            byte[] data,
            String fileToSave,
            int width,
            int height
    )
    {
        if (DeviceUtils.isHTC_M8())
            convertRawBytesToDng(data, fileToSave, width, height, g3_color1, g3_color2, g3_neutral, 0, GRBG, RawToDng.HTCM8_rowSize);
        else if (DeviceUtils.isXperiaL())
            convertRawBytesToDng(data, fileToSave, width, height, g3_color1, g3_color2, g3_neutral, 0, GRBG, RawToDng.XperiaL_rowSize);
        else if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT >= 21)
            convertRawBytesToDng(data, fileToSave, width, 3080, g3_color1, g3_color2, g3_neutral, g3_blacklevel, BGGR, getG3_rowSizeL);
        else
            convertRawBytesToDng(data, fileToSave, width, height, g3_color1, g3_color2, g3_neutral, g3_blacklevel, BGGR, Calculate_rowSize(data.length, height));
    }
	
	public static float[] g3_color1 =
	{
		(float) 0.9218606949, (float) 0.0263967514, (float) -0.1110496521,
		(float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
		(float) -0.05432224274, (float) 0.2319784164, (float) 0.2338542938
	};
	
	//Color Matrix 1                  : 0.9218606949 0.0263967514 -0.1110496521 -0.333
	//1432343 1.179347992 0.1260938644 -0.05432224274 0.2319784164 0.2338542938
	
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

    public static int g3_rowSizeKitKat = 5200;
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
