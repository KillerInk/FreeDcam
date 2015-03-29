package com.troop.theme.ambient;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by George on 3/16/2015.
 */
public class BitmapUtil {


    private static final String LOG_TAG = BitmapUtil.class.getCanonicalName();

    private static final int RGB_MASK= 0x00FFFFFF;

    private static RenderScript rs = null;

    private static Allocation blurInputAllocation = null;

    private static Allocation blurOutpuAllocation = null;

    private static ScriptIntrinsicBlur blurScript = null;

    private static int currentBlurBitmapHeight = -1;

    private static int currentBlurBitmapWIdth = -1;






    private static Drawable getDeviceWallpaper(Context ctx)
    {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ctx);

        return wallpaperManager.getDrawable();
    }


    public static Bitmap getWallpaperBitmap(Context ctx)
    {


        return ((BitmapDrawable)getDeviceWallpaper(ctx)).getBitmap();
    }

    public static Bitmap Drawable2Bitmap(Drawable source)
    {
        return ((BitmapDrawable)source).getBitmap();
    }

    public static void initBlur(Context ctx,Bitmap Source)
    {
        if(Source.getHeight() != currentBlurBitmapHeight || Source.getWidth() != currentBlurBitmapWIdth)
        {
            android.util.Log.d(LOG_TAG,"Intialize RS");
            if (rs != null){
                rs.destroy();
                blurInputAllocation.destroy();
                blurOutpuAllocation.destroy();

            }
            rs = RenderScript.create(ctx);
            blurInputAllocation = Allocation.createFromBitmap(rs,Source, Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
            blurOutpuAllocation = Allocation.createTyped(rs,blurInputAllocation.getType());
            if (Build.VERSION.SDK_INT > 16)
                blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            currentBlurBitmapWIdth = Source.getWidth();
            currentBlurBitmapHeight = Source.getHeight();
        }
    }

    public static void doGausianBlur(Bitmap output,Bitmap Source,float BlurRadius)
    {

        blurInputAllocation.copyFrom(Source);
        if (Build.VERSION.SDK_INT > 16) {
            blurScript.setRadius(BlurRadius);
            blurScript.setInput(blurInputAllocation);
            blurScript.forEach(blurOutpuAllocation);
        }
        blurOutpuAllocation.copyTo(output);



    }

    public static Bitmap ScaleUP(Bitmap in,int SysW,int SysH)
    {
        int width = in.getWidth();
        int height = in.getHeight();


        float scaleWidth = ((float)SysW)/width;
        float scaleHeight = ((float)SysH)/height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);


        return Bitmap.createBitmap(in,0,0,in.getWidth(),in.getHeight(),matrix,true);
    }

    public static Bitmap CropBitmap(Bitmap crop,int[] X, Boolean zeroOffset)
    {

        if(zeroOffset)
            return Bitmap.createBitmap(crop,0,0,X[0],X[1]);
        else
            return Bitmap.createBitmap(crop,X[0],0,X[2],X[3]);
    }



    public static Bitmap RotateBitmap(Bitmap source,float angle,int SysW,int SysH)
    {
        int width = source.getWidth();
        int height = source.getHeight();

        int nWidth = (SysW)/6;
        int nHeight = SysH/6;

        float scaleWidth = ((float)nWidth)/width;
        float scaleHeight = ((float)nHeight)/height;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        matrix.postScale(scaleWidth,scaleHeight);


        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),matrix,true);
    }

    public static Bitmap InverseBitmap(Bitmap Source)
    {
        Bitmap Inverted = Source.copy(Bitmap.Config.ARGB_8888,true);

        int Width = Inverted.getWidth();
        int Height = Inverted.getHeight();
        int Pixels = Width*Height;

        int[] pixel = new int[Pixels];
        Inverted.getPixels(pixel, 0, Width, 0, 0, Width, Height);

        for (int i=0; i<Pixels; i++)
        {
            pixel[i] ^=RGB_MASK;
        }
        Inverted.setPixels(pixel,0,Width,0,0,Width,Height);

        return Inverted;
    }



}
