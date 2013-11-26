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

    public void Compare(BitmapHandler base, BitmapHandler first, BitmapHandler second)
    {
        BitmapHandler[] widthhandlers = getBiggestX(base, first,second);
        BitmapHandler[] heighthandlers = getBiggestY(base, first, second);
        int X1 = widthhandlers[0].X - widthhandlers[1].X;
        widthhandlers[2].X = widthhandlers[0].X;
        widthhandlers[1].X = X1;
        widthhandlers[0].X = 0; 
        int Y1 = heighthandlers[0].Y - heighthandlers[1].Y;
        heighthandlers[2].Y = heighthandlers[0].Y;
        heighthandlers[1].Y = Y1;
        heighthandlers[0].Y = 0;



    }

    private BitmapHandler[] getBiggestX(BitmapHandler base , BitmapHandler first, BitmapHandler second)
    {
        BitmapHandler[] widthhandlers = new BitmapHandler[3];

        if (base.X > first.X && base.X > second.X)
        {
            widthhandlers[0] = base;
            if (first.X > second.X)
            {
                widthhandlers[1] = first;
                widthhandlers[2] = second;
            }
            else
            {
                widthhandlers[2] = first;
                widthhandlers[1] = second;
            }
        }
        if (first.X > base.X && first.X > second.X)
        {
            widthhandlers[0] = first;
            if (base.X > second.X)
            {
                widthhandlers[1] = base;
                widthhandlers[2] = second;
            }
            else
            {
                widthhandlers[2] = base;
                widthhandlers[1] = second;
            }
        }
        if (second.X > base.X && second.X > first.X)
        {
            widthhandlers[0] = second;
            if (base.X > first.X)
            {
                widthhandlers[1] = base;
                widthhandlers[2] = first;
            }
            else
            {
                widthhandlers[2] = base;
                widthhandlers[1] = first;
            }
        }
        return widthhandlers;
    }

    private BitmapHandler[] getBiggestY(BitmapHandler base , BitmapHandler first, BitmapHandler second)
    {
        BitmapHandler[] widthhandlers = new BitmapHandler[3];

        if (base.Y > first.Y && base.Y > second.Y)
        {
            widthhandlers[0] = base;
            if (first.Y > second.Y)
            {
                widthhandlers[1] = first;
                widthhandlers[2] = second;
            }
            else
            {
                widthhandlers[2] = first;
                widthhandlers[1] = second;
            }
        }
        if (first.Y > base.Y && first.Y > second.Y)
        {
            widthhandlers[0] = first;
            if (base.Y > second.Y)
            {
                widthhandlers[1] = base;
                widthhandlers[2] = second;
            }
            else
            {
                widthhandlers[2] = base;
                widthhandlers[1] = second;
            }
        }
        if (second.Y > base.Y && second.Y > first.Y)
        {
            widthhandlers[0] = second;
            if (base.Y > first.Y)
            {
                widthhandlers[1] = base;
                widthhandlers[2] = first;
            }
            else
            {
                widthhandlers[2] = base;
                widthhandlers[1] = first;
            }
        }
        return widthhandlers;
    }

}
