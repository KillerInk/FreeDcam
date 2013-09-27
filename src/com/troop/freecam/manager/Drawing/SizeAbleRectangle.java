package com.troop.freecam.manager.Drawing;

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

/**
 * Created by troop on 24.09.13.
 */
public class SizeAbleRectangle
{
    DrawingOverlaySurface camPreview;
    Paint mPaint;

    public PointF beginCoordinate = new PointF(100,100);
    public PointF endCoordinate = new PointF(300,300);
    RectF topRect = new RectF(0, 0, 20, 20);
    RectF leftRect = new RectF(0, 0, 20, 20);
    public RectF mainRect = new RectF(beginCoordinate.x, beginCoordinate.y, endCoordinate.x, endCoordinate.y);
    public boolean drawRectangle = false;
    public boolean Enabled = false;

    boolean topRecMoving = false;
    boolean mainRecMoving = false;
    boolean leftRecMoving = false;

    public SizeAbleRectangle(DrawingOverlaySurface camPreview)
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
    }

    public  void Draw()
    {
        if (camPreview.RDY)
        {
            Canvas canvas;
            canvas = camPreview.mHolder.lockCanvas(null);

            if (drawRectangle == true && Enabled && canvas != null)
            {
                String tmp = camPreview.preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);
                if (tmp.equals(CameraManager.SwitchCamera_MODE_3D))
                {
                    //draw mainRectangle
                    int pos_x = (int)beginCoordinate.x;
                    int depth = 4;
                    int depth2 = 4;
                    int pos_y = (int)beginCoordinate.y;
                    int size_w = (int)mainRect.width();
                    int size_h = (int)mainRect.height();
                    int c_width = (int) canvas.getWidth();

                    int startxleft = pos_x /2;
                    int endxleft = pos_x /2 + size_w/2;

                    int startXright = pos_x /2 + c_width/2 + depth;
                    int endXright = pos_x /2 + c_width/2 + depth + size_w /2;

                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    Rect leftmain = new Rect(startxleft, pos_y, endxleft, pos_y + size_h);
                    Rect rightmain = new Rect(startXright, pos_y, endXright , pos_y + size_h);
                    canvas.drawRect(leftmain, mPaint);
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
                    canvas.drawRect(leftright, mPaint);

                }
                else
                {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    canvas.drawRect(mainRect, mPaint);
                    canvas.drawRect(topRect, mPaint);
                    canvas.drawRect(leftRect, mPaint);
                }


            }
            else if (canvas !=null && drawRectangle == false)
            {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            camPreview.mHolder.unlockCanvasAndPost(canvas);
        }

    }

    public void OnTouch(MotionEvent event)
    {
        if  (Enabled)
        {
            if (drawRectangle == false)
            {
                drawRectangle = true;
                Draw();
                //camPreview.invalidate();
                return;

            }

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
                else
                if (mainRect.contains(event.getX(), event.getY()) || mainRecMoving)
                {
                    moveRect(event);
                }
                else
                {
                    drawRectangle = false;
                    Draw();
                    //camPreview.invalidate();

                }
            }
            else
            {
                drawRectangle = false;
                Draw();
            }
                //camPreview.invalidate();
        }
    }


    private void moveRect(MotionEvent event)
    {
        if (topRecMoving == false && leftRecMoving == false)
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
        if (mainRecMoving == false && leftRecMoving == false)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                topRecMoving =true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                beginCoordinate.y = event.getY();
                setRectanglePosition();
                //camPreview.invalidate();
                Draw();
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                topRecMoving = false;
            }
        }
    }

    private void moveLeftRect(MotionEvent event)
    {
        if (mainRecMoving == false && topRecMoving == false)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                leftRecMoving =true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                beginCoordinate.x = event.getX();
                setRectanglePosition();
                Draw();
                //camPreview.invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                leftRecMoving = false;
            }
        }
    }

    private void setRectanglePosition()
    {
        mainRect = new RectF(beginCoordinate.x, beginCoordinate.y, endCoordinate.x ,endCoordinate.y);
        RectF f = mainRect;
        float centerWidth = (f.width() /2) + beginCoordinate.x ;
        float centerHeigth = (f.height() / 2) + beginCoordinate.y;
        topRect.set(centerWidth - 20, beginCoordinate.y, centerWidth+20, beginCoordinate.y+40);
        leftRect.set(beginCoordinate.x, centerHeigth - 20, beginCoordinate.x + 40, centerHeigth +20);
    }
}
