package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

//Exposure Selector
public class ExposureSelector extends AppCompatImageView
{
	private int shutterangle=180; //i wish
	private int shutterstart = 90;
	private RectF shutterPos;
	private Paint bracketPaint = new Paint();
	private Paint shutterPaint = new Paint();
	private Rect mySize;
	private boolean sel;
	
	public ExposureSelector(Context c, AttributeSet attr)
	{
		super(c, attr);
		shutterPaint.setColor(Color.WHITE);
		shutterPaint.setStyle(Paint.Style.FILL);
		shutterPaint.setAntiAlias(true);
		shutterPaint.setAlpha(200);
		bracketPaint.setStyle(Paint.Style.STROKE);
		bracketPaint.setShadowLayer(3.0f, .5f,.5f,Color.DKGRAY);
		bracketPaint.setStrokeWidth(4.0f);
		bracketPaint.setColor(Color.WHITE);
		bracketPaint.setAntiAlias(true);
		setMinimumWidth(100);
		setMinimumHeight(65);
		shutterPos = new RectF(30, 10, 75, 55);
	
		
	}
	
	public void selected(boolean v)
	{
		sel = v;
		if(v)
			bracketPaint.setColor(Color.RED);
		else
			bracketPaint.setColor(Color.WHITE);	
	}
	
	public void onDraw(Canvas c)
	{
		c.drawRect(0, 0, 100, 65, bracketPaint);
		if(!sel)
		{
			if(shutterangle <= 349)
			{
				shutterangle++;
				c.drawArc(shutterPos, 90, shutterangle , true, shutterPaint);
			}
			else
			{
				shutterangle=180;
				c.drawArc(shutterPos, 90, shutterangle , true, shutterPaint);
			}
		}
		else
		{
			c.drawArc(shutterPos, 90, 270, true, shutterPaint);
		}
		invalidate();
	}
	
	
	
	
}
