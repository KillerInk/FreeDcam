package com.troop.freecam.manager.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
    CamPreview camPreview;
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

    public SizeAbleRectangle(CamPreview camPreview)
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

    public  void Draw(Canvas canvas)
    {


        if (drawRectangle == true && Enabled && canvas != null)
        {
            String tmp = camPreview.preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);
            if (tmp.equals(CameraManager.SwitchCamera_MODE_3D))
            {
                int pos_x = (int)beginCoordinate.x;
                int depth = 3;
                int pos_y = (int)beginCoordinate.y;
                int size_w = (int)mainRect.width();
                int size_h = (int)mainRect.height();
                int c_width = (int) canvas.getWidth();

                int startxleft = pos_x + depth;
                int endxleft = pos_x + depth + size_w;

                int startXright = pos_x + c_width/2 - depth;
                int endXright = pos_x + c_width/2 - depth + size_w;

                canvas.drawRect(new Rect(startxleft, pos_y, endxleft, pos_y + size_h), mPaint);
                canvas.drawRect(new Rect(startXright, pos_y, endXright , pos_y + size_h), mPaint);

                canvas.drawRect(new Rect(184, 260, 184 + 80, 360), mPaint);
                canvas.drawRect(new Rect(400+186, 260, 400 + 186 + 80, 360), mPaint);
                //canvas.drawRect(new Rect(pos_x,pos_y,size_w/2,size_h), mPaint);
                //canvas.drawRect(new Rect(size_w/2+1,pos_y,size_w,size_h), mPaint);
            }
            else
            {
                canvas.drawRect(mainRect, mPaint);
                canvas.drawRect(topRect, mPaint);
                canvas.drawRect(leftRect, mPaint);
            }

            //c.drawBitmap(bmL, null, new Rect(0,0,mCanvasWidth/2,mCanvasHeight), null);

            //c.drawBitmap(bmR, null, new Rect(mCanvasWidth/2+1,0,mCanvasWidth,mCanvasHeight), null);
            //c.drawBitmap(bm, null, new Rect(pos_x + depth, pos_y, pos_x + depth + size_w, pos_y + size_h), mPaint);
            //c.drawBitmap(bm, null, new Rect(pos_x + c_width/2 - depth, pos_y, pos_x + c_width/2 - depth + size_w, pos_y + size_h), mPaint);
        }

    }

    public void OnTouch(MotionEvent event)
    {
        if  (Enabled)
        {
            if (drawRectangle == false)
            {
                drawRectangle = true;
                camPreview.invalidate();
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
                    camPreview.invalidate();

                }
            }
            else
                camPreview.invalidate();
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
                camPreview.invalidate();
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
                camPreview.invalidate();
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
                camPreview.invalidate();
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
