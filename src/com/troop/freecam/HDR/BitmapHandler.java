package com.troop.freecam.HDR;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

/**
 * Created by troop on 08.11.13.
 */
public class BitmapHandler
{
    public Uri filePath;


    public int X;
    public int Y;
    public int Width;
    public int Height;
    public int DifFromNullTop;
    public int DifFromNullLeft;

    public BitmapHandler(Uri filePath)
    {
        this.filePath = filePath;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath.getPath(), o);
        this.X = 0;
        this.Y = 0;
        this.Width = o.outWidth;
        this.Height = o.outHeight;
        this.DifFromNullLeft = 0;
        this.DifFromNullTop = 0;
    }

    public BitmapHandler(int width , int height)
    {
        this.X=0;
        this.Y = 0;
        this.Width = width;
        this.Height = height;
        this.DifFromNullLeft = 0;
        this.DifFromNullTop = 0;
    }
    public void AddX(int x)
    {
        DifFromNullLeft += x;
        if (x >= 0)
        {
            //X += x;
            Width -= x;
        }
        else
        {
            X -= x;
            Width += x;
        }
    }

    public void AddY(int y)
    {
        DifFromNullTop += y;
        if (y >= 0)
        {
            //Y += y;
            Height -= y;
        }
        else
        {
            Y -= y;
            Height += y;
        }
    }

}
