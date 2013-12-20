package com.troop.freecam.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jni.bitmap_operations.JniBitmapHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 06.12.13.
 */
public class BitmapUtils
{
    public static Bitmap rotateBitmap(Bitmap originalBmp)
    {
        JniBitmapHolder holder = new JniBitmapHolder();
        holder.storeBitmap(originalBmp);
        originalBmp.recycle();
        holder.rotateBitmap180();
        originalBmp = holder.getBitmapAndFree();
        holder =null;
        return originalBmp;
        //BitmapUtils utils = new BitmapUtils();
        //return utils.rotate180(originalBmp);
        /*Matrix m = new Matrix();
        m.postRotate(180);
        System.gc();
        Bitmap rot = Bitmap.createBitmap(originalBmp, 0, 0, originalBmp.getWidth(), originalBmp.getHeight(), m, false);
        originalBmp.recycle();
        return rot;*/
    }

    private  Bitmap  rotate180(Bitmap originalBmp)
    {
        JniBitmapHolder holder = new JniBitmapHolder();
        holder.storeBitmap(originalBmp);
        originalBmp.recycle();
        holder.rotateBitmap180();
        return holder.getBitmapAndFree();
    }

    public static void saveBitmapToFile(File file, Bitmap bitmap)
    {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveBitmapNativeToFile(File file, JniBitmapHolder jniBitmapHolder)
    {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            jniBitmapHolder.Save(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            jniBitmapHolder.freeBitmap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBytesToFile(File file, byte[] bytes)
    {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            outStream.write(bytes);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Bitmap loadFromBytes(byte[] bytes)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPurgeable = true; // Tell to gc that whether it needs free
        // memory, the Bitmap can be cleared
        opts.inInputShareable = true;
        //opts.inDither = true;
        //opts.inPreferQualityOverSpeed = true;
        Bitmap originalBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        return  originalBmp;
    }

    public static Bitmap loadFromPath(String path)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        Bitmap ret = BitmapFactory.decodeFile(path, opts);
        return ret;
    }

    public static Bitmap cropBitmap(Bitmap orgi, int x, int y, int width, int height)
    {
        JniBitmapHolder holder = new JniBitmapHolder();
        holder.storeBitmap(orgi);
        orgi.recycle();
        holder.cropBitmap(x,y,width,height);
        return holder.getBitmapAndFree();
    }

    public static Bitmap createEmptyBitmpap(int width, int height, Bitmap.Config config)
    {
        return Bitmap.createBitmap(width, height, config);
    }
}
