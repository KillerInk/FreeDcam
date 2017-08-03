package freed.utils;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by troop on 31.07.2017.
 */

public class CurveView extends View {


    private PointF[] points;
    private PointF[] controlPoints;
    private Paint paint;
    private final int BUTTON_SIZE = 15;
    private final Object drawLock = new Object();

    private RectF drawPointsRects[];

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
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE );
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        setPoints(new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)});

    }


    public void setPoints(PointF[] points)
    {
        synchronized (drawLock) {
            this.points = points;
            drawPointsRects = new RectF[points.length];
            controlPoints = new PointF[points.length];
        }
        invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    private void createControlPoints() {

        if (drawPointsRects == null)
            return;
        for (int i = 0; i< drawPointsRects.length; i++)
        {
            if (drawPointsRects[i] != null)  {
                if (i == 0) {
                    controlPoints[i] = new PointF((drawPointsRects[i].centerX() - drawPointsRects[i + 1].centerX()) / 3, (drawPointsRects[i].centerY() - drawPointsRects[i + 1].centerY()) / 3);
                } else if (i == drawPointsRects.length - 1) {
                    controlPoints[i] = new PointF((drawPointsRects[i].centerX() - drawPointsRects[i - 1].centerX()) / 3, (drawPointsRects[i].centerY() - drawPointsRects[i - 1].centerY()) / 3);
                } else {
                    controlPoints[i] = new PointF((drawPointsRects[i + 1].centerX() - drawPointsRects[i - 1].centerX()) / 3, (drawPointsRects[i + 1].centerY() - drawPointsRects[i - 1].centerY()) / 3);
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

            for (int i = 0; i < points.length; i++) {
                PointF drawPoints = new PointF((points[i].x * cropwidth)+BUTTON_SIZE, cropheight - (cropheight * points[i].y)+BUTTON_SIZE);
                drawPointsRects[i] = new RectF(drawPoints.x - BUTTON_SIZE, drawPoints.y - BUTTON_SIZE, drawPoints.x + BUTTON_SIZE, drawPoints.y + BUTTON_SIZE);
            }
            createControlPoints();

            canvas.drawARGB(0, 0, 0, 0);
            if (points == null)
                return;

            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);

            //grid layout
            //diagonal line
            canvas.drawLine(BUTTON_SIZE, height, width, BUTTON_SIZE, paint);
            //first line top  to bottom vertical
            canvas.drawLine(cropwidth * 0.25f +BUTTON_SIZE, BUTTON_SIZE, (cropwidth * 0.25f)+BUTTON_SIZE, height, paint);
            //mid line top to bottom vertical
            canvas.drawLine((cropwidth * 0.5f)+BUTTON_SIZE, BUTTON_SIZE, (cropwidth * 0.5f)+BUTTON_SIZE, height, paint);
            //last line top to bottom vertical
            canvas.drawLine(cropwidth * 0.75f +BUTTON_SIZE, BUTTON_SIZE, cropwidth * 0.75f +BUTTON_SIZE, height, paint);
            // top horizontal line
            canvas.drawLine(BUTTON_SIZE, (cropheight * 0.25f)+BUTTON_SIZE, width, (cropheight * 0.25f)+BUTTON_SIZE, paint);
            // mid horizontal line
            canvas.drawLine(BUTTON_SIZE, (cropheight * 0.5f)+BUTTON_SIZE, width, (cropheight * 0.5f)+BUTTON_SIZE, paint);
            //bottom horizontal line
            canvas.drawLine(BUTTON_SIZE, (cropheight * 0.75f)+BUTTON_SIZE, width, (cropheight * 0.75f)+BUTTON_SIZE, paint);
            //draw rect around
            canvas.drawRect(BUTTON_SIZE, BUTTON_SIZE, width, height, paint);

            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(3);

            //draw spline through points
            if (drawPointsRects == null)
                return;

            Path path = new Path();
            path.moveTo(drawPointsRects[0].centerX(), drawPointsRects[0].centerY());
            canvas.drawRect(drawPointsRects[0], paint);
            for (int i = 1; i < drawPointsRects.length; i++) {
                if (drawPointsRects[i] != null) {
                    path.cubicTo(
                            drawPointsRects[i - 1].centerX() + controlPoints[i - 1].x, drawPointsRects[i - 1].centerY() + controlPoints[i - 1].y,
                            drawPointsRects[i].centerX() - controlPoints[i].x, drawPointsRects[i].centerY() - controlPoints[i].y,
                            drawPointsRects[i].centerX(), drawPointsRects[i].centerY());

                    canvas.drawRect(drawPointsRects[i], paint);
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
                    if (drawPointsRects[i].contains(event.getX(),event.getY()))
                        selectedPoint = i;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (selectedPoint != -1) {
                    points[selectedPoint].x = event.getX() / getWidth();
                    points[selectedPoint].y = (getHeight() - event.getY()) / getHeight();
                    //drawPointsRects[selectedPoint].set(event.getX()-BUTTON_SIZE,event.getY() -BUTTON_SIZE, event.getX() + BUTTON_SIZE, event.getY()+BUTTON_SIZE);
                    invalidate();
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                selectedPoint = -1;
                return false;
        }

        return true;
    }
}
