package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;

import freed.utils.StringUtils;

public class FocusSelector extends AppCompatImageView
{

	private Paint bracPaint, nfPaint, hypPaint;
	private String hypF = "x", nearF = "x", farF = "x";
	private boolean isfocused;

	private int textsize = 20;

    public FocusSelector(Context c)
	{
		super(c);
		init();
	}

	public FocusSelector(Context c, AttributeSet attr)
	{
		super(c, attr);
		init();
	}

	private void init() {

        float txtdpi = 6;
        textsize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, txtdpi, getResources().getDisplayMetrics());
		bracPaint = new Paint();
		nfPaint = new Paint();
		hypPaint = new Paint();
		bracPaint.setColor(Color.WHITE);
		bracPaint.setShadowLayer(0.5f,.2f,.2f,Color.BLACK);
		bracPaint.setStyle(Paint.Style.STROKE);
		bracPaint.setStrokeWidth(4f);
		nfPaint.setARGB(255,45,156,255);
		nfPaint.setTextSize(textsize);
		nfPaint.setAntiAlias(true);
		nfPaint.setTypeface(Typeface.DEFAULT_BOLD);
		hypPaint.setColor(Color.YELLOW);
		hypPaint.setTextSize(textsize);
		hypPaint.setAntiAlias(true);
		hypPaint.setTypeface(Typeface.DEFAULT_BOLD);
		requestLayout();
	}


	public void getFocus(float[] f)
	{
		float n, far, h, opt;
		n = f[Camera.Parameters.FOCUS_DISTANCE_NEAR_INDEX];
		far =  f[Camera.Parameters.FOCUS_DISTANCE_FAR_INDEX];
		//h = far-n;
		opt = f[Camera.Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX];
		nearF = StringUtils.getMeterString(n);
		farF = StringUtils.getMeterString(far);
		hypF = StringUtils.getMeterString(opt);
		invalidate();
		
	}


	
	public boolean getFocusCheck()
	{
		return isfocused;
	}
	public void setFocusCheck(boolean f)
	{
		if(f)
		{
			bracPaint.setColor(Color.GREEN);
			isfocused = true;
		}
		else
		{
			isfocused = false;
			bracPaint.setColor(Color.WHITE);
			nearF = "x";
			farF = "x";
			hypF = "x";
		}
	}

	@Override
	public void onDraw(Canvas c)
	{
		super.onDraw(c);
		// startX, startY, stopX, stopY
		//Left Side
		c.drawLine(20,0,1,20,bracPaint);
		c.drawLine(1,20,1,getHeight()-20,bracPaint);
		c.drawLine(1,getHeight()-20,20,getHeight(),bracPaint);
		
		//Right Side
		c.drawLine(getWidth()-1-20,0,getWidth()-1,20,bracPaint);
		c.drawLine(getWidth()-1,20,getWidth()-1,getHeight()-20,bracPaint);
		c.drawLine(getWidth()-1,getHeight()-20,getWidth()-1-20,getHeight(),bracPaint);
		
		//Center Focus Info
		c.drawText("NEAR: " + nearF, 15,getHeight()/2 - (textsize+5), nfPaint);
		c.drawText("OPT: " +  hypF, 15,getHeight()/2 , hypPaint);
		c.drawText("FAR: " + farF, 15,getHeight()/2 +(textsize+5), nfPaint);
	}

}
