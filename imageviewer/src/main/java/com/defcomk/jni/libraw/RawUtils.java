package com.defcomk.jni.libraw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Log;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RawUtils {

    private static int DEFAULT_JPG_QUALITY = 85;

    private RawUtils() {

    }

    static {
        try {
            System.loadLibrary("rawutils");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

	
	

    private static native void native_init();

    private static native byte[] unpackThumbnailBytes(String fileName);
    
    public static native void unpackRawByte(String fileName, byte[] xraw, int blackLevel,float aperture,float focalLength,float shutterSpeed,float iso);

    private static native int unpackThumbnailToFile(String rawFileName, String thumbFileName);

    private static native void parseExif(String fileName, Object exifMap);


    public static byte[] unpackThumbNailToBytes(String filename)
    {
        return unpackThumbnailBytes(filename);
    }

    /**
     
     * @param fileName
     * @param height
     * @param width
     * @return
     */
    public static Bitmap unpackThumbnailBitmapToFit(String fileName, int width, int height) {
        //TimeChecker t = TimeChecker.newInstance();
        //t.prepare();
        Bitmap thumbnail;
        byte[] thumbnailBytes = unpackThumbnailBytes(fileName);
      

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length, options);
        options.inJustDecodeBounds = false;

        int originHeight = options.outHeight;
        int originWidth = options.outWidth;

        int scaleWidth = (int) Math.ceil(originWidth / (float) width);
        int scaleHeight = (int) Math.ceil(originHeight / (float) height);
        int scale = (scaleWidth < scaleHeight ? scaleWidth : scaleHeight);

        options.inSampleSize = scale;
       // t.check("");

        thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length, options);
       // t.check("");

        System.gc();

        return thumbnail;
    }

    /**
     * 
     * @param rawFileName
     * @param thumbFileName
     * @return
     */
    public static boolean saveThumbnailToFile(String rawFileName, String thumbFileName) {
        return (unpackThumbnailToFile(rawFileName, thumbFileName) == 0);
    }

    public static boolean saveThumbnailToFitToFile(String rawFileName, String scaledFileName, int width, int height) {
        //TimeChecker t = TimeChecker.newInstance();
        //t.prepare();
        Bitmap bitmap = unpackThumbnailBitmapToFit(rawFileName, width, height);
        //t.check("�");

        if (bitmap == null || bitmap.getByteCount() == 0) {
            return false;
        }
        HashMap exifMap = RawUtils.parseExif(rawFileName, false);
        int flip = Integer.valueOf((String)exifMap.get(ExifInterface.TAG_ORIENTATION));
        if (flip != 0) {
            Matrix matrix = new Matrix();
            int rotation = 0;
            if (flip == 3) {
                rotation = 180;
            }
            else if (flip == 5) {
                rotation = 270;
            }
            else if (flip == 6) {
                rotation = 90;
            }
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            //t.check("");
        }

        boolean result = compressBitmapAndSave(bitmap, scaledFileName);
       // t.check("JPG");
        return result;
    }

    public static boolean scaleJPGAndSave(String originFileName, String scaledFileName, int width, int height) {

        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(originFileName, options);
        options.inJustDecodeBounds = false;
        int originHeight = options.outHeight;
        int originWidth = options.outWidth;

        int scaleWidth = (int) Math.ceil(originWidth / (float) width);
        int scaleHeight = (int) Math.ceil(originHeight / (float) height);
        int scale = (scaleWidth < scaleHeight ? scaleWidth : scaleHeight);

        if (scale == 0) {
            return false;
        }

        options.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(originFileName, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, originWidth / scale, originHeight / scale,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return compressBitmapAndSave(bitmap, scaledFileName);
    }

    public static boolean compressBitmapAndSave(Bitmap bitmap, String savedFileName) {
        boolean result = false;

        if (bitmap == null) {
            return false;
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(savedFileName);

            result = bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_JPG_QUALITY, outputStream);
            outputStream.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return result;
    }


    public static HashMap<String, String> parseExif(String fileName, boolean isJPEG) {
        HashMap<String, String> exif = new HashMap<String, String>();
        try {
            if (isJPEG) {
                ExifInterface oldExif = new ExifInterface(fileName);
               
            }
            else {
                parseExif(fileName, exif);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return exif;
    }


}
