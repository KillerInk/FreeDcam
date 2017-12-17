package freed.utils;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by troop on 31.07.2017.
 */

public class CurveView extends View {


    public interface CurveChangedEvent
    {
        void onCurveChanged(PointF[] pointFs);
        void onCurveChanged(PointF[] r,PointF[] g,PointF[] b);
        void onTouchStart();
        void onTouchEnd();
    }

    private int lineColor = Color.WHITE;
    private int gridColor = Color.RED;

    private PointF[] points;
    private PointF[] controlPoints;
    private Paint paint;
    private final int BUTTON_SIZE = 30;
    private final Object drawLock = new Object();
    private CurveChangedEvent curveChangedListner;

    private RectF drawPointsRects[];
    Path path = new Path();

    public CurveView(Context context) {
        super(context);
        init();
    }

    public CurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.STROKE );
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setTextSize(BUTTON_SIZE);
        setPoints(new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)});

    }

    public void setCurveChangedListner(CurveChangedEvent event)
    {
        this.curveChangedListner = event;
    }

    public void setPoints(PointF[] points)
    {
        synchronized (drawLock) {
            this.points = points;
            drawPointsRects = new RectF[points.length];
            controlPoints = new PointF[points.length];
            for (int i = 0; i< drawPointsRects.length;i++) {
                drawPointsRects[i] = new RectF();
                controlPoints[i] = new PointF();
            }

        }
        invalidate();
    }

    public void setLineColor(int color)
    {
        this.lineColor = color;
    }

    public void setGridColor(int color)
    {
        this.gridColor =color;
    }


    private void createControlPoints() {

        if (drawPointsRects == null)
            return;
        for (int i = 0; i< drawPointsRects.length; i++)
        {
            if (drawPointsRects[i] != null)  {
                if (i == 0) {
                    controlPoints[i].set((drawPointsRects[i + 1].centerX()-drawPointsRects[i].centerX()) / 3, (drawPointsRects[i + 1].centerY()-drawPointsRects[i].centerY()) / 3);
                } else if (i == drawPointsRects.length - 1) {
                    controlPoints[i].set((drawPointsRects[i].centerX() - drawPointsRects[i - 1].centerX()) / 3, (drawPointsRects[i].centerY() - drawPointsRects[i - 1].centerY()) / 3);
                } else {
                    controlPoints[i].set((drawPointsRects[i + 1].centerX() - drawPointsRects[i - 1].centerX()) / 3, (drawPointsRects[i + 1].centerY() - drawPointsRects[i - 1].centerY()) / 3);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        synchronized (drawLock) {
            float width = getWidth() -BUTTON_SIZE;
            float height = getHeight()-BUTTON_SIZE;
            float cropwidth = width-BUTTON_SIZE;
            float cropheight = height -BUTTON_SIZE;

            float x;
            float y;
            for (int i = 0; i < points.length; i++) {
                x = (points[i].x * cropwidth)+BUTTON_SIZE;
                y = cropheight - (cropheight * points[i].y)+BUTTON_SIZE;
                drawPointsRects[i].set(x - BUTTON_SIZE, y - BUTTON_SIZE, x + BUTTON_SIZE, y + BUTTON_SIZE);
            }
            createControlPoints();

            canvas.drawARGB(0, 0, 0, 0);

            if (points == null)
                return;

            paint.setColor(gridColor);
            paint.setStrokeWidth(2);

            //grid layout
            //diagonal line
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(BUTTON_SIZE, height, width, BUTTON_SIZE, paint);

            //draw grid lines
            for(float f =0.1f; f< 1; f+=0.1f)
            {
                canvas.drawLine(cropwidth * f +BUTTON_SIZE, BUTTON_SIZE, (cropwidth * f)+BUTTON_SIZE, height, paint);
                canvas.drawLine(BUTTON_SIZE, (cropheight * f)+BUTTON_SIZE, width, (cropheight * f)+BUTTON_SIZE, paint);
            }

            //draw rect around
            canvas.drawRect(BUTTON_SIZE, BUTTON_SIZE, width, height, paint);

            paint.setColor(lineColor);
            paint.setStrokeWidth(2);


            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            if (selectedPoint != -1) {
                //draw x y postition
                canvas.drawText("x:" + points[selectedPoint].x, 0, BUTTON_SIZE-6, paint);
                canvas.drawText("y:" + points[selectedPoint].y, getWidth()/2, BUTTON_SIZE-6, paint);

                //draw axis guding lines to see where the selectedpoint moves
                canvas.drawLine(BUTTON_SIZE,
                        (cropheight- points[selectedPoint].y*cropheight)+BUTTON_SIZE,
                        getWidth()-BUTTON_SIZE,
                        (cropheight- points[selectedPoint].y*cropheight)+BUTTON_SIZE,
                        paint);
                canvas.drawLine((points[selectedPoint].x*cropwidth)+BUTTON_SIZE,
                        BUTTON_SIZE,
                        (points[selectedPoint].x*cropwidth)+BUTTON_SIZE,
                        getHeight() -BUTTON_SIZE,
                        paint);
            }
            else
            {
                canvas.drawText("x:" , 0, BUTTON_SIZE-6, paint);
                canvas.drawText("y:" , getWidth()/2, BUTTON_SIZE-6, paint);
            }

            //draw spline through points
            if (drawPointsRects == null)
                return;
            paint.setStyle(Paint.Style.STROKE);
            path.reset();
            path.moveTo(drawPointsRects[0].centerX(), drawPointsRects[0].centerY());
            canvas.drawCircle(drawPointsRects[0].centerX(),drawPointsRects[0].centerY(),BUTTON_SIZE,paint);
            for (int i = 1; i < drawPointsRects.length; i++) {
                if (drawPointsRects[i] != null) {
                    path.cubicTo(
                            drawPointsRects[i - 1].centerX() + controlPoints[i - 1].x, drawPointsRects[i - 1].centerY() + controlPoints[i - 1].y,
                            drawPointsRects[i].centerX() - controlPoints[i].x, drawPointsRects[i].centerY() - controlPoints[i].y,
                            drawPointsRects[i].centerX(), drawPointsRects[i].centerY());

                    canvas.drawCircle(drawPointsRects[i].centerX(),drawPointsRects[i].centerY(),BUTTON_SIZE,paint);
                    canvas.drawCircle(controlPoints[i].x, controlPoints[i].y, BUTTON_SIZE/2,paint);
                }
            }
            canvas.drawPath(path, paint);
            path.close();

        }

    }

    private int selectedPoint = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (drawPointsRects == null)
                    break;
                for (int i = 0; i< drawPointsRects.length; i++)
                {
                    if (drawPointsRects[i] == null)
                        break;
                    if (drawPointsRects[i].contains(event.getX(),event.getY())) {
                        selectedPoint = i;
                        if(curveChangedListner != null)
                            curveChangedListner.onTouchStart();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (selectedPoint != -1) {
                    points[selectedPoint].x = event.getX() / getWidth();
                    if (points[selectedPoint].x  > 1)
                        points[selectedPoint].x = 1;
                    if(points[selectedPoint].x < 0)
                        points[selectedPoint].x = 0;
                    points[selectedPoint].y = (getHeight() - event.getY()) / getHeight();
                    if (points[selectedPoint].y  > 1)
                        points[selectedPoint].y = 1;
                    if(points[selectedPoint].y < 0)
                        points[selectedPoint].y = 0;
                    if (curveChangedListner != null)
                        curveChangedListner.onCurveChanged(points);
                    invalidate();
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:

                selectedPoint = -1;
                if (curveChangedListner != null)
                    curveChangedListner.onTouchEnd();
                return false;
        }

        return true;
    }

}
