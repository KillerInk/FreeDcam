package com.troop.androiddng;

import java.io.File;
import java.nio.ByteBuffer;

import android.R.integer;

public class RawToDng 
{
	static
    {
		System.loadLibrary("RawToDng");
    }
	
	private static native void convertRawBytesToDng(
			byte[] data, 
			String fileToSave, 
			int width, 
			int height, 
			float[] colorMatrix1, 
			float[] colorMatrix2, 
			float[] neutral, 
			int blacklevel
			);

    public static void ConvertRawBytesToDng(
            byte[] data,
            String fileToSave,
            int width,
            int height
    )
    {
        convertRawBytesToDng(data, fileToSave, width, height, g3_color1, g3_color2, g3_neutral, g3_blacklevel);
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
}
