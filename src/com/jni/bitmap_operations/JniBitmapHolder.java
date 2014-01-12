package com.jni.bitmap_operations;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class JniBitmapHolder
  {
  public ByteBuffer _handler =null;
  static
    {
    System.loadLibrary("JniBitmapOperations");
    }

  private native ByteBuffer jniStoreBitmapData(Bitmap bitmap);

  private native Bitmap jniGetBitmapFromStoredBitmapData(ByteBuffer handler);

  private native void jniFreeBitmapData(ByteBuffer handler);

  private native void jniRotateBitmapCcw90(ByteBuffer handler);

  private native void jniRotateBitmapCw90(ByteBuffer handler);

  private native void jniCropBitmap(ByteBuffer handler,final int left,final int top,final int right,final int bottom);

  private native void jniScaleNNBitmap(ByteBuffer handler,final int newWidth,final int newHeight);
  
  private native void jniRotateBitmap180(ByteBuffer handler);
  
  private native void jniAddImageIntoImage(ByteBuffer handler, ByteBuffer hand, int margineX, int margineY);
  
  private native void jniToneMapImages(ByteBuffer base, ByteBuffer high, ByteBuffer low);
  
  private native int jniWidth(ByteBuffer buffer);
  private native int jniHeight(ByteBuffer buffer);
  private native void jniSave(ByteBuffer buffer,FileOutputStream outstreamFileOutputStream);
  private native ByteBuffer jniLoadFromPath(String path);

  public JniBitmapHolder()
    {}

  public JniBitmapHolder(final Bitmap bitmap)
    {
    storeBitmap(bitmap);
    }
  
  public JniBitmapHolder(final String path)
  {
	  loadFromPath(path);
  }
  
  public void AddImageIntoExisting(ByteBuffer nioBuffer, int x, int y)
  {
	  if(_handler==null)
	      return;
	  jniAddImageIntoImage(_handler, nioBuffer, x,y);
  }
  
  public int getWidth()
  {
	  if(_handler==null)
		return 0;
	  return jniWidth(_handler);
  }
  
  public int getHeight()
  {
	  if(_handler==null)
			return 0;
	  return jniHeight(_handler);
  }
  
  public void Save(FileOutputStream outputStream)
  {
	  if(_handler == null)
		  return;
	  jniSave(_handler, outputStream);
  }
  
  public void ToneMapImages(JniBitmapHolder high, JniBitmapHolder low)
  {
	  jniToneMapImages(_handler, high._handler, low._handler);
  }

  public void storeBitmap(final Bitmap bitmap)
    {
    if(_handler!=null)
      freeBitmap();
    _handler=jniStoreBitmapData(bitmap);
    }
  
  public void loadFromPath(String path)
  {
	  if (_handler != null) 
	  {
		  freeBitmap();
	  }
	  _handler = jniLoadFromPath(path);
  }
  

  public void rotateBitmapCcw90()
    {
    if(_handler==null)
      return;
    jniRotateBitmapCcw90(_handler);
    }

  public void rotateBitmapCw90()
    {
    if(_handler==null)
      return;
    jniRotateBitmapCw90(_handler);
    }
  
  public void rotateBitmap180()
  {
  if(_handler==null)
    return;
  jniRotateBitmap180(_handler);
  }

  public void cropBitmap(final int left,final int top,final int right,final int bottom)
    {
    if(_handler==null)
      return;
    jniCropBitmap(_handler,left,top,right,bottom);
    }

  public Bitmap getBitmap()
    {
    if(_handler==null)
      return null;
    return jniGetBitmapFromStoredBitmapData(_handler);
    }

  public Bitmap getBitmapAndFree()
    {
    final Bitmap bitmap=getBitmap();
    freeBitmap();
    
    return bitmap;
    }

  public void scaleBitmap(final int newWidth,final int newHeight)
    {
    if(_handler==null)
      return;
    jniScaleNNBitmap(_handler,newWidth,newHeight);
    }

  public void freeBitmap()
    {
    if(_handler==null)
      return;
    jniFreeBitmapData(_handler);
    _handler=null;
    }

  @Override
  protected void finalize() throws Throwable
    {
    super.finalize();
    if(_handler==null)
      return;
    Log.w("DEBUG","JNI bitmap wasn't freed nicely.please rememeber to free the bitmap as soon as you can");
    freeBitmap();
    }
  }
