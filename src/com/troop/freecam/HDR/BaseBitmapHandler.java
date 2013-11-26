package com.troop.freecam.HDR;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 19.11.13.
 */
public abstract class BaseBitmapHandler
{
    protected Activity activity;
    protected Uri[] uris;

    protected BitmapHandler base;
    protected BitmapHandler first;
    protected BitmapHandler second;

    public BaseBitmapHandler(Activity activity, Uri[] uris)
    {
        this.activity = activity;
        this.uris = uris;
    }

    protected void cropPictures(BitmapHandler base, BitmapHandler first, BitmapHandler second, int width, int height)
    {
        int orgiWidth = width * 2;
        this.base = base;
        this.first = first;
        this.second = second;

        base.X *= 2;
        first.X *= 2;
        second.X *= 2;
        base.Y *= 2;
        first.Y *=2;
        second.Y *=2;

        base.Compare(base,first,second);

        setWidth(orgiWidth);
        if (base.X + base.Width> orgiWidth)
        {
            width = orgiWidth - base.X;
            setWidth(width);
        }
        if (first.X + first.Width > orgiWidth)
        {
            width = orgiWidth - first.X;
            setWidth(width);
        }
        if (second.X + second.Width > orgiWidth)
        {
            width = orgiWidth - second.X;
            setWidth(width);
        }



        int orgiHeight = height * 2;
        setHeigth(orgiHeight);
        if (base.Y + base.Height > orgiHeight)
        {
            height = orgiHeight - base.Y;
            setHeigth(height);
        }
        if (first.Y + first.Height > orgiHeight)
        {
            height = orgiHeight - first.Y;
            setHeigth(height);
        }
        if (second.Y + second.Height  > orgiHeight)
        {
            height = orgiHeight - second.Y;
            setHeigth(height);
        }
    }

    protected void setWidth(int width)
    {
        base.Width = width;
        second.Width = width;
        first.Width = width;
    }

    protected void setHeigth(int height)
    {
        first.Height = height;
        second.Height = height;
        base.Height = height;
    }

    protected void saveBitmap(String filepath, Bitmap bitmap)
    {
        File file = new File(filepath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        try {
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        bitmap =null;
    }
}
