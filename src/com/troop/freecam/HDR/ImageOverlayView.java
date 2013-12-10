package com.troop.freecam.HDR;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

//import android.support.v4.view.GestureDetectorCompat;

/**
 * Created by troop on 14.11.13.
 */
public class ImageOverlayView extends View
{

    Uri[] uris;
    BitmapDrawable firstImage;
    BitmapDrawable secondImage;
    BitmapDrawable baseImage;
    //Bitmap orginalImage;
    //Bitmap firtorginalImage;
    //Bitmap secondorginalImage;
    public BitmapHandler firstHolder;
    public BitmapHandler secondHolder;
    public BitmapHandler baseHolder;
    public boolean running = false;
    public int OrginalWidth;
    public int OrginalHeight;

    int topmargine = 0;
    int leftmargine = 0;
    int rightmargine = 0;
    int bottommargine = 0;
    //private GestureDetectorCompat mDetector;
    //boolean drawFirstPic = true;

    public boolean drawFirstPic = false;

    public ImageOverlayView(Context context)
    {
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
        BitmapFactory.Options sizes = new BitmapFactory.Options();
        sizes.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uris[1].getPath(), sizes);
        OrginalHeight = sizes.outHeight /2;
        OrginalWidth = sizes.outWidth / 2;

        baseImage = new BitmapDrawable(BitmapFactory.decodeFile(uris[1].getPath(), o));
        firstImage = new BitmapDrawable(BitmapFactory.decodeFile(uris[0].getPath(), o));
        secondImage = new BitmapDrawable(BitmapFactory.decodeFile(uris[2].getPath(), o));
        baseHolder = new BitmapHandler(OrginalWidth, OrginalHeight);
        firstHolder = new BitmapHandler(OrginalWidth, OrginalHeight);
        secondHolder = new BitmapHandler(OrginalWidth, OrginalHeight);
        rightmargine = OrginalWidth;
        bottommargine = OrginalHeight;

    }

    public void AddTop(boolean firstpic, int value)
    {
        //value *= -1;
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
        //value *= -1;
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
        running = false;
        /*if (orginalImage != null)
            orginalImage.recycle();
        orginalImage = null;*/
        if (firstImage != null)
            firstImage.getBitmap().recycle();
        firstImage = null;
        if(secondImage != null)
            secondImage.getBitmap().recycle();
        secondImage = null;
        if (baseImage !=null)
            baseImage.getBitmap().recycle();
        /*if (firtorginalImage != null)
            firtorginalImage.recycle();
        firtorginalImage = null;
        if (secondorginalImage != null)
            secondorginalImage.recycle();
        secondorginalImage = null;*/
        
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //super.onDraw(canvas);
        if  (running && baseHolder != null && firstHolder != null && secondHolder != null)
        {
            drawImage(canvas, baseImage, baseHolder,false);
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
        //secondImage = new BitmapDrawable(Bitmap.createBitmap(secondorginalImage, leftmargine + secondHolder.X, topmargine + secondHolder.Y, 800, 480));
        drawImage(canvas, secondImage, secondHolder, true);
    }

    private void drawFirstImage(Canvas canvas) {
        //firstImage = new BitmapDrawable(Bitmap.createBitmap(firtorginalImage, leftmargine + firstHolder.X, topmargine + firstHolder.Y, 800, 480));
        drawImage(canvas, firstImage, firstHolder,true);
    }

    private void drawImage(Canvas canvas, BitmapDrawable bitmapdraw,BitmapHandler holder, boolean alpha)
    {
        int left = ((leftmargine + holder.X  )* scale) / 1000;
        int top = ((topmargine  + holder.Y) * scale) / 1000;
        int right = ((rightmargine + holder.X ) * scale) / 1000;
        int bottom = ((bottommargine + holder.Y ) * scale ) / 1000;

        bitmapdraw.setBounds(left,top,right,bottom);
        if (alpha)
            bitmapdraw.setAlpha(100);
        bitmapdraw.draw(canvas);

    }

    int moveX = 0;
    int moveY = 0;
    int moveX2 = 0;
    int moveY2 = 0;
    int scale = 1000;
    double distance = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean toreturn = false;
        if (running)
        {

            if (event.getPointerCount() == 1)
            {
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

                    //if (leftmargine + lastmovex >= 0 && leftmargine + 800 + lastmovex <= OrginalWidth)
                    leftmargine = leftmargine - lastmovex;
                    rightmargine = rightmargine - lastmovex;

                    //if(topmargine + lastmovey >= 0 && topmargine + 480 + lastmovey <= OrginalHeight)
                    topmargine = topmargine - lastmovey;
                    bottommargine -= lastmovey;

                    toreturn = true;
                    invalidate();
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    toreturn = false;
                }
            }
            else
            {
                if (event.getAction() == MotionEvent.ACTION_MOVE )
                {
                    double dist = Math.sqrt(Math.pow(event.getY(0) - event.getY(1), 2) + Math.pow(event.getX(0)- event.getX(1), 2));
                    if (dist > distance && scale < 3000)
                        scale+=20;
                    if (dist < distance && scale > 1000)
                        scale-=20;
                    distance = dist;

                    /*int lastmovey2 = (int)event.getY(0) - (int)event.getY(1);
                    if (moveY2 == 0)
                        moveY2 = lastmovey2;
                    if (lastmovey2 > moveY2 && scale < 3000)
                        scale += 20;
                    if (lastmovey2 < moveY2 && scale > 1000)
                        scale -= 20;
                    moveY2 = lastmovey2;*/
                    toreturn = true;
                    invalidate();
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    toreturn = false;
                }
            }
        }
        return  toreturn;
    }
}
