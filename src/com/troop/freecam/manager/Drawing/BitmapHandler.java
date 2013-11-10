package com.troop.freecam.manager.Drawing;

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

    public BitmapHandler(Uri filePath)
    {
        this.filePath = filePath;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath.getPath(), o);
        X = 0;
        Y = 0;
        Width = o.outWidth;
        Height = o.outHeight;
    }

    public void AddX(int x)
    {
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
