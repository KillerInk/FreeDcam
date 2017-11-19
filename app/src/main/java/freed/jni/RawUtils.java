package freed.jni;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import freed.utils.Log;

public class RawUtils {

    static final String TAG = RawUtils.class.getSimpleName();
    private static final int DEFAULT_JPG_QUALITY = 85;

    public RawUtils() {

    }

    static
    {
        System.loadLibrary("freedcam");
    }


    private native byte[] unpackThumbnailBytes(String fileName);

    private native Bitmap unpackRAW(String fileName);
    
    public native void unpackRawByte(String fileName, byte[] xraw, int blackLevel,float aperture,float focalLength,float shutterSpeed,float iso);

    public native byte[] BitmapExtractor(byte[] xraw, int blackLevel);

    private native int unpackThumbnailToFile(String rawFileName, String thumbFileName);

    private native void parseExif(String fileName, Object exifMap);


    public  byte[] unpackThumbNailToBytes(String filename)
    {
        return unpackThumbnailBytes(filename);
    }

    public Bitmap UnPackRAW(String file)
    {
        if (file == null || TextUtils.isEmpty(file))
            return null;
        return unpackRAW(file);
    }


    public byte[] convertFileToByteArray(File f)
    {
        byte[] byteArray = null;
        try
        {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024*8];
            int bytesRead =0;

            while ((bytesRead = inputStream.read(b)) != -1)
            {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        }
        catch (IOException ex)
        {
            Log.WriteEx(ex);
        }
        return byteArray;
    }

    /**
     
     * @param fileName
     * @param height
     * @param width
     * @return
     */
    private Bitmap unpackThumbnailBitmapToFit(String fileName, int width, int height) {
        //TimeChecker t = TimeChecker.newInstance();
        //t.prepare();
        Bitmap thumbnail;
        byte[] thumbnailBytes = unpackThumbnailBytes(fileName);
      

        Options options = new Options();
        options.inJustDecodeBounds = true;
        thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length, options);
        options.inJustDecodeBounds = false;

        int originHeight = options.outHeight;
        int originWidth = options.outWidth;

        int scaleWidth = (int) Math.ceil(originWidth / (float) width);
        int scaleHeight = (int) Math.ceil(originHeight / (float) height);

        options.inSampleSize = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
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
    public boolean saveThumbnailToFile(String rawFileName, String thumbFileName) {
        return unpackThumbnailToFile(rawFileName, thumbFileName) == 0;
    }

    public boolean saveThumbnailToFitToFile(String rawFileName, String scaledFileName, int width, int height) {
        Bitmap bitmap = unpackThumbnailBitmapToFit(rawFileName, width, height);
        if (bitmap == null || bitmap.getByteCount() == 0) {
            return false;
        }
        HashMap exifMap = parseExif(rawFileName);
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
        }
        return compressBitmapAndSave(bitmap, scaledFileName);
    }

    public boolean scaleJPGAndSave(String originFileName, String scaledFileName, int width, int height) {

        Bitmap bitmap = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(originFileName, options);
        options.inJustDecodeBounds = false;
        int originHeight = options.outHeight;
        int originWidth = options.outWidth;

        int scaleWidth = (int) Math.ceil(originWidth / (float) width);
        int scaleHeight = (int) Math.ceil(originHeight / (float) height);
        int scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;

        if (scale == 0) {
            return false;
        }

        options.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(originFileName, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, originWidth / scale, originHeight / scale,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return compressBitmapAndSave(bitmap, scaledFileName);
    }

    private boolean compressBitmapAndSave(Bitmap bitmap, String savedFileName) {
        boolean result = false;

        if (bitmap == null) {
            return false;
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(savedFileName);

            result = bitmap.compress(CompressFormat.JPEG, DEFAULT_JPG_QUALITY, outputStream);
            outputStream.flush();

        } catch (IOException ex) {
            Log.WriteEx(ex);
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                Log.WriteEx(ex);  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return result;
    }


    private HashMap<String, String> parseExif(String fileName) {
        HashMap<String, String> exif = new HashMap<>();

        parseExif(fileName, exif);
        return exif;
    }


}
