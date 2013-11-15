package com.troop.freecam.HDR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by troop on 14.11.13.
 */
public class ImageOverlayView extends View
{

    Uri[] uris;
    BitmapDrawable firstImage;
    BitmapDrawable secondImage;
    BitmapDrawable baseImage;
    Bitmap orginalImage;
    Bitmap firtorginalImage;
    Bitmap secondorginalImage;
    BitmapHandler firstHolder;
    BitmapHandler secondHolder;
    BitmapHandler baseHolder;
    boolean running = false;

    public boolean drawFirstPic = false;

    public ImageOverlayView(Context context) {
        super(context);
    }

    public ImageOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Load(Uri[] uris)
    {
        this.uris = uris;
        init();
    }

    private void init()
    {
        running = true;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        orginalImage = BitmapFactory.decodeFile(uris[1].getPath(), o);
        firtorginalImage = BitmapFactory.decodeFile(uris[0].getPath(), o);
        secondorginalImage = BitmapFactory.decodeFile(uris[2].getPath(), o);
        baseHolder = new BitmapHandler(orginalImage.getWidth(), orginalImage.getHeight());
        firstHolder = new BitmapHandler(orginalImage.getWidth(), orginalImage.getHeight());
        secondHolder = new BitmapHandler(orginalImage.getWidth(), orginalImage.getHeight());
    }

    public void AddTop(boolean firstpic, int value)
    {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        if (firstpic)
        {
            if (value > 0)
            {

                firstHolder.Height -= value;
                baseHolder.Height -= value;
            }
            else
            {
                firstHolder.Y -= value;
                firstHolder.Height += value;
                baseHolder.Height += value;
            }
        }
        else
        {
            if (value > 0)
            {

                secondHolder.Height -= value;
                baseHolder.Y += value;
                baseHolder.Height -= value;
            }
            else
            {
                secondHolder.Y -= value;
                secondHolder.Height += value;
                baseHolder.Height += value;
            }
        }
        invalidate();
    }

    public void AddLeft(boolean firspic, int value)
    {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        if (firspic)
        {
            if (value > 0)
            {

                firstHolder.Width -= value;
                baseHolder.X += value;
                baseHolder.Width -= value;
            }
            else
            {
                firstHolder.X -= value;
                firstHolder.Width += value;
                baseHolder.Width += value;
            }
        }
        else
        {
            if (value > 0)
            {

                secondHolder.Width -= value;
                baseHolder.X += value;
                baseHolder.Width -= value;
            }
            else
            {
                secondHolder.X -= value;
                secondHolder.Width += value;
                baseHolder.Width += value;
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if  (running)
        {
            if (orginalImage != null && baseImage != null && baseHolder !=null)
            {
                baseImage = new BitmapDrawable(Bitmap.createBitmap(orginalImage, baseHolder.X, baseHolder.Y, 800, 480));
            }

            if (secondorginalImage != null && secondImage != null && secondHolder !=null)
            {
                secondImage = new BitmapDrawable(Bitmap.createBitmap(secondorginalImage, secondHolder.X, secondHolder.Y, 800, 480));
            }
            if (firstImage != null  && firtorginalImage!= null && firstHolder != null);
            {
                firstImage = new BitmapDrawable(Bitmap.createBitmap(firtorginalImage, firstHolder.X, firstHolder.Y, 800, 480));
            }
        }

    }
}
