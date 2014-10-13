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
	
	public static native void convertRawBytesToDng(byte[] data, String fileToSave, int width, int height);
	
	public native void convertRawToDng(String fileToLoad, String fileToSave, int width, int height);
	
	public void ConvertRawToDng(String fileToLoad, String fileToSave, int width, int height)
	{
		convertRawToDng(fileToLoad, fileToSave, width, height);
	}

    public static void ConvertRawToDng(byte bytes[], String fileToSave, int width, int height)
    {
        convertRawBytesToDng(bytes, fileToSave, width, height);
    }
}
