package com.jni.dng_utils;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;

public class Dng {
	public ByteBuffer _handler =null;
	  static
	    {
	    System.loadLibrary("Dng");
	    }
	  
	  private native ByteBuffer jniStoreBitmapData(Bitmap bitmap);
	  
	  public void storeBitmap(final Bitmap bitmap)
	    {
	    if(_handler!=null)
	      //freeBitmap();
	    _handler=jniStoreBitmapData(bitmap);
	    }


}
