package com.troop.freecam.manager.Drawing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.troop.freecam.CamPreview;
import com.troop.freecam.CameraManager;
import com.troop.freecam.R;
import com.troop.freecam.manager.ParametersManager;
import com.troop.freecam.MainActivity;

/**
 * Created by troop on 24.09.13.
 */
public class SizeAbleRectangle
{
    MainActivity mainActivity;
    DrawingOverlaySurface camPreview;
    Paint mPaint;

    public PointF beginCoordinate = new PointF(200,140);

    public PointF endCoordinate = new PointF(600,340);
    RectF topRect = new RectF(0, 0, 20, 20);
    RectF leftRect = new RectF(0, 0, 20, 20);
    RectF rightRect = new RectF(0,0,20,20);
    RectF bottomRect = new RectF(0,0,20,20);
    public RectF mainRect = new RectF(beginCoordinate.x, beginCoordinate.y, endCoordinate.x, endCoordinate.y);
    public boolean drawRectangle = false;
    public boolean Enabled = false;

    boolean topRecMoving = false;
    boolean mainRecMoving = false;
    boolean leftRecMoving = false;
    boolean rightRecMoving = false;
    boolean bottomRecMoving = false;
    Bitmap croshairLeft;
    Bitmap croshairRight;
    public CameraManager cameraManager;

    int width = 250;
    int height = 250;
    final int minsize = 250;

    public SizeAbleRectangle(DrawingOverlaySurface camPreview, CameraManager cameraManager)
    {
        this.camPreview = camPreview;

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);
        mPaint.setFilterBitmap(true);
        croshairLeft = BitmapFactory.decodeResource(camPreview.context.getResources(), R.drawable.crosshair);
        croshairRight = BitmapFactory.decodeResource(camPreview.context.getResources(), R.drawable.crosshair);
        this.cameraManager = cameraManager;
    }

    public  void Draw()
    {
        if (camPreview.RDY)
        {
            Canvas canvas;
            canvas = camPreview.mHolder.lockCanvas(null);

            if (drawRectangle == true && Enabled && canvas != null)
            {
                String tmp = camPreview.preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);
                if (tmp.equals(ParametersManager.SwitchCamera_MODE_3D))
                {
                    //draw mainRectangle
                    int pos_x = (int)beginCoordinate.x;
                    int depth = 5;
                    int depth2 = 4;
                    int pos_y = (int)beginCoordinate.y;
                    int size_w = (int)mainRect.width();
                    int size_h = (int)mainRect.height();
                    int c_width = (int) canvas.getWidth();

                    int startxleft = pos_x/2 - depth /2;
                    int endxleft = pos_x/2  + size_w/2 - depth/2;

                    int startXright = pos_x/2  + c_width/2 + depth/2;
                    int endXright = pos_x/2  + c_width/2 + depth/2 + size_w /2;

                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    Rect leftmain = new Rect(startxleft, pos_y, endxleft, pos_y + size_h);
                    Rect rightmain = new Rect(startXright, pos_y, endXright , pos_y + size_h);
                    canvas.drawBitmap(croshairLeft, null, leftmain, mPaint);
                    canvas.drawBitmap(croshairRight, null, rightmain, mPaint);
                    /*canvas.drawRect(leftmain, mPaint);
                    canvas.drawRect(rightmain, mPaint);

                    //draw TopRectangle
                    int centerWidth = (int)(leftmain.width() /2) ;
                    int centerHeigth = (int) (leftmain.height() / 2);
                    int topstartX = startxleft + centerWidth - 10;
                    int topstartY = pos_y;
                    int topendY = pos_y + 40;
                    int topendX = startxleft + centerWidth  + 10;
                    Rect topleft  = new Rect(topstartX, topstartY, topendX, topendY);
                    Rect topright = new Rect(topstartX + c_width /2 + depth2,topstartY, topendX + c_width/2+ depth2, topendY );
                    canvas.drawRect(topleft, mPaint);
                    canvas.drawRect(topright, mPaint);

                    //draw left

                    int leftSx = startxleft;
                    int leftSy = pos_y + centerHeigth -20;
                    int leftEx = startxleft + 20;
                    int leftEy = pos_y + centerHeigth +20;
                    Rect leftleft = new Rect(leftSx, leftSy, leftEx, leftEy);
                    Rect leftright = new Rect(leftSx + c_width /2 +depth2, leftSy, leftEx + c_width/2 +depth2, leftEy);
                    canvas.drawRect(leftleft, mPaint);
                    canvas.drawRect(leftright, mPaint);*/

                }
                else
                {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    canvas.drawBitmap(croshairLeft, null, mainRect, mPaint);
                    /*canvas.drawRect(mainRect, mPaint);
                    canvas.drawRect(topRect, mPaint);
                    canvas.drawRect(leftRect, mPaint);*/
                }


            }
            else if (canvas !=null && drawRectangle == false)
            {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            camPreview.mHolder.unlockCanvasAndPost(canvas);
        }

    }

    long lastclick;
    int waitTime = 300;
//Touched @ 1:22AM
    public void OnTouch(MotionEvent event)
    {
        if  (Enabled)
        {


            long timeelapsed = event.getEventTime() - lastclick;
            if (timeelapsed > waitTime)
            {
                lastclick = event.getEventTime();
                drawRectangle = true;
                beginCoordinate = new PointF(event.getX() - width/2, event.getY() - height/2);
                endCoordinate = new PointF(beginCoordinate.x +width, beginCoordinate.y +height);
                setRectanglePosition();
                Draw();
                cameraManager.StartFocus();
            }

            /*if (event.getAction() == MotionEvent.ACTION_DOWN)
            {

                if (drawRectangle == false)
                {
                    if (timeelapsed > waitTime)
                    {
                        lastclick = event.getEventTime();
                        drawRectangle = true;
                        beginCoordinate = new PointF(event.getX(), event.getY());
                        endCoordinate = new PointF(event.getX() +width, event.getY() +height);
                        setRectanglePosition();
                        Draw();
                        //camPreview.invalidate();
                        return;
                    }
                }
                else
                {
                    drawRectangle = false;
                    Draw();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (drawRectangle == true)
                {
                    if (topRect.contains(event.getX(), event.getY()) || topRecMoving == true)
                    {
                        moveTopRect(event);
                    }
                    else if (leftRect.contains(event.getX(), event.getY()) || leftRecMoving == true)
                    {
                        moveLeftRect(event);
                    }
                    else if (rightRect.contains(event.getX(), event.getY())|| rightRecMoving)
                    {
                        moveRightRect(event);
                    }
                    else if (bottomRect.contains(event.getX(), event.getY())|| bottomRecMoving)
                    {
                        moveBottomRect(event);
                    }
                    else
                    if (mainRect.contains(event.getX(), event.getY()) || mainRecMoving)
                    {
                        moveRect(event);
                    }
                    else
                    {
                        if (timeelapsed > waitTime)
                        {
                            drawRectangle = false;
                            Draw();
                            lastclick = event.getEventTime();
                        }
                    }
                }

            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                if (drawRectangle && mainRect != null)
                {
                    cameraManager.StartFocus();
                }

            }*/
        }
    }

    private void moveRightRect(MotionEvent event)
    {
        if (mainRecMoving == false && topRecMoving == false && bottomRecMoving == false && leftRecMoving == false)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                rightRecMoving =true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                int tempwidth = (int)event.getX() - (int)beginCoordinate.x;
                if(tempwidth >= minsize)
                {
                    endCoordinate.x = event.getX();
                    width = (int)endCoordinate.x - (int)beginCoordinate.x;
                    setRectanglePosition();
                    Draw();
                }
                else if (tempwidth < minsize)
                {

                    setRectanglePosition();
                    Draw();
                }
                //camPreview.invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                rightRecMoving = false;
                if (width < minsize)
                    width = minsize;
                if (height < minsize)
                    height = minsize;
            }
        }
    }


    private void moveRect(MotionEvent event)
    {
        if (topRecMoving == false && leftRecMoving == false && rightRecMoving == false && bottomRecMoving ==false)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                mainRecMoving = true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                float movedY = mainRect.height() /2;
                if (movedY < 0)
                    movedY = movedY * -1;
                beginCoordinate.y = (event.getY() - movedY);
                endCoordinate.y = (event.getY() + movedY);
                float movedX = mainRect.width() / 2;
                if (movedX < 0)
                    movedX = movedX * -1;
                beginCoordinate.x = (event.getX() - movedX);
                endCoordinate.x = (event.getX() + movedX);
                setRectanglePosition();
                //camPreview.invalidate();
                Draw();
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                mainRecMoving = false;
            }
        }
    }

    private void moveTopRect(MotionEvent event)
    {
        if (mainRecMoving == false && leftRecMoving == false && rightRecMoving == false && bottomRecMoving == false)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                topRecMoving =true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                int tempheight = (int)endCoordinate.y - (int)event.getY();
                if (tempheight >= minsize)
                {
                    beginCoordinate.y = event.getY();
                    height = (int)endCoordinate.y - (int)beginCoordinate.y;
                    setRectanglePosition();
                //camPreview.invalidate();
                    Draw();
                }
                else if (tempheight < minsize)
                {
                    setRectanglePosition();
                    //camPreview.invalidate();
                    Draw();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                topRecMoving = false;
                if (width < minsize)
                    width = minsize;
                if (height < minsize)
                    height = minsize;
            }
        }
    }

    private void moveBottomRect(MotionEvent event)
    {
        if (mainRecMoving == false && leftRecMoving == false && rightRecMoving == false && topRecMoving == false)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                bottomRecMoving =true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                int tempheight = (int)event.getY() - (int)beginCoordinate.y;
                if (tempheight >= minsize)
                {
                   endCoordinate.y = event.getY();
                    height = (int)endCoordinate.y - (int)beginCoordinate.y;
                    setRectanglePosition();
                    //camPreview.invalidate();
                    Draw();
                }
                else if (tempheight < minsize)
                {
                    setRectanglePosition();
                    //camPreview.invalidate();
                    Draw();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
               bottomRecMoving = false;
                if (width < minsize)
                    width = minsize;
                if (height < minsize)
                    height = minsize;
            }
        }
    }

    private void moveLeftRect(MotionEvent event)
    {
        if (mainRecMoving == false && topRecMoving == false && rightRecMoving == false && bottomRecMoving == false)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                leftRecMoving =true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                int temp = (int)endCoordinate.x - (int)event.getX();
                if(temp >= minsize)
                {
                    beginCoordinate.x = event.getX();
                    width = (int)endCoordinate.x - (int)beginCoordinate.x;
                    setRectanglePosition();
                    Draw();
                }
                else
                {
                    setRectanglePosition();
                    Draw();

                }
                //camPreview.invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                leftRecMoving = false;
                if (width < minsize)
                    width = minsize;
                if (height < minsize)
                    height = minsize;
            }
        }
    }

    private void setRectanglePosition()
    {
        mainRect = new RectF(beginCoordinate.x, beginCoordinate.y, endCoordinate.x ,endCoordinate.y);
        RectF f = mainRect;
        float centerWidth = (f.width() /2) + beginCoordinate.x ;
        float centerHeigth = (f.height() / 2) + beginCoordinate.y;
        topRect.set(beginCoordinate.x + 25, beginCoordinate.y, endCoordinate.y - 25, beginCoordinate.y+50);
        leftRect.set(beginCoordinate.x, beginCoordinate.y + 25, beginCoordinate.x + 50, endCoordinate.y - 25);
        rightRect.set(endCoordinate.x - 50, beginCoordinate.y + 25, endCoordinate.x, endCoordinate.y - 25);
        bottomRect.set(beginCoordinate.x + 25, endCoordinate.y - 50, endCoordinate.x - 25, endCoordinate.y);
    }
}
