package com.troop.freecam.HDR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
    public BitmapHandler firstHolder;
    public BitmapHandler secondHolder;
    public BitmapHandler baseHolder;
    boolean running = false;
    public int OrginalWidth;
    public int OrginalHeight;

    int topmargine = 0;
    int leftmargine = 0;
    //boolean drawFirstPic = true;

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
        OrginalHeight = orginalImage.getHeight();
        OrginalWidth = orginalImage.getWidth();
    }

    public void AddTop(boolean firstpic, int value)
    {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        if (firstpic)
        {
            if (value > 0)
            {
                if (baseHolder.Y - value >= 0 && secondHolder.Y - value >= 0)
                {
                    baseHolder.Y -= value;
                    secondHolder.Y -= value;
                }
                else
                    firstHolder.Y += value;
            }
            else
            {
                if (firstHolder.Y + value >= 0)
                    firstHolder.Y += value;
                else
                {
                    baseHolder.Y -= value;
                    secondHolder.Y -= value;
                }
            }
        }
        else
        {
            if (value > 0)
            {
                if (baseHolder.Y - value >= 0 && firstHolder.Y - value >= 0)
                {
                    baseHolder.Y -= value;
                    firstHolder.Y -= value;
                }
                else
                    secondHolder.Y += value;
            }
            else
            {
                if (secondHolder.Y + value >= 0)
                    secondHolder.Y += value;
                else
                {
                    baseHolder.Y -= value;
                    firstHolder.Y -= value;
                }
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
                if (baseHolder.X - value >= 0 && secondHolder.X - value >= 0)
                {
                    baseHolder.X -= value;
                    secondHolder.X -= value;
                }
                else
                    firstHolder.X += value;
            }
            else
            {
                if (firstHolder.X + value >= 0)
                    firstHolder.X += value;
                else
                {
                    baseHolder.X -= value;
                    secondHolder.X -= value;
                }
            }
        }
        else
        {
            if (value > 0)
            {
                if (baseHolder.X - value >= 0 && firstHolder.X - value >= 0)
                {
                    baseHolder.X -= value;
                    firstHolder.X -= value;
                }
                else
                    secondHolder.X += value;
            }
            else
            {
                if (secondHolder.X + value >= 0)
                    secondHolder.X += value;
                else
                {
                    baseHolder.X -= value;
                    firstHolder.X -= value;
                }
            }
        }
        invalidate();
    }

    public void Destroy()
    {
        if (orginalImage != null)
            orginalImage.recycle();
        orginalImage = null;
        if (firstImage != null)
            firstImage.getBitmap().recycle();
        firstImage = null;
        if(secondImage != null)
            secondImage.getBitmap().recycle();
        secondImage = null;
        if (baseImage !=null)
            baseImage.getBitmap().recycle();
        if (firtorginalImage != null)
            firtorginalImage.recycle();
        firtorginalImage = null;
        if (secondorginalImage != null)
            secondorginalImage.recycle();
        secondorginalImage = null;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if  (running)
        {
            if (leftmargine + baseHolder.X +400 > OrginalWidth)
                leftmargine -= OrginalWidth - baseHolder.X - 400;
            if (leftmargine + firstHolder.X + 400 > OrginalWidth)
                leftmargine -= OrginalWidth - firstHolder.X - 400;
            if (leftmargine + secondHolder.X +400 > OrginalWidth)
                leftmargine -= OrginalWidth - secondHolder.X - 400;

            if (topmargine + baseHolder.Y + 240 > OrginalHeight)
                topmargine -= OrginalHeight - baseHolder.Y - 240;
            if (topmargine + firstHolder.Y + 240 > OrginalHeight)
                topmargine -= OrginalHeight - firstHolder.Y - 240;
            if (topmargine + secondHolder.Y + 240 > OrginalHeight)
                topmargine -= OrginalHeight - secondHolder.Y - 240;

            if(orginalImage != null && baseHolder !=null)
            {
                baseImage = new BitmapDrawable(Bitmap.createBitmap(orginalImage, leftmargine + baseHolder.X, topmargine + baseHolder.Y, 400, 240));
                baseImage.setBounds(0,0,800,480);
                baseImage.draw(canvas);
            }

            if (drawFirstPic)
            {
                drawFirstImage(canvas);
            }
            else
            {
                drawSecondImage(canvas);
            }


        }

    }

    private void drawSecondImage(Canvas canvas) {
        secondImage = new BitmapDrawable(Bitmap.createBitmap(secondorginalImage, leftmargine + secondHolder.X, topmargine + secondHolder.Y, 400, 240));
        secondImage.setBounds(0,0,800,480);
        secondImage.setAlpha(125);
        secondImage.draw(canvas);
    }

    private void drawFirstImage(Canvas canvas) {
        firstImage = new BitmapDrawable(Bitmap.createBitmap(firtorginalImage, leftmargine + firstHolder.X, topmargine + firstHolder.Y, 400, 240));
        firstImage.setBounds(0,0,800,480);
        firstImage.setAlpha(125);
        firstImage.draw(canvas);
    }

    int moveX = 0;
    int moveY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean toreturn = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            moveX = (int)event.getX();
            moveY = (int)event.getY();
            toreturn = true;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE )
        {
            int lastmovex = moveX - (int)event.getX();
            int lastmovey = moveY - (int)event.getY();
            //Log.d(TAG, "moved by: X:" + lastmovex + " Y: " + lastmovey);
            moveX = (int)event.getX();
            moveY = (int)event.getY();

            if (leftmargine + lastmovex >= 0 && leftmargine + 800 + lastmovex <= OrginalWidth)
                leftmargine = leftmargine + lastmovex;

            if(topmargine + lastmovey >= 0 && topmargine + 480 + lastmovey <= OrginalHeight)
                topmargine = topmargine + lastmovey;

            toreturn = true;
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            toreturn = false;
        }
        return  toreturn;
    }
}
