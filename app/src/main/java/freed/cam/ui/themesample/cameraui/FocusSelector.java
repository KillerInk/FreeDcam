package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FocusSelector extends ImageView
{

	private Paint bracPaint, nfPaint, hypPaint;
	private String hypF = "x", nearF = "x", farF = "x";
	private boolean isfocused;
	private int Bx, By, Bw, Bh;
	public FocusSelector(Context c, AttributeSet attr)
	{
		super(c, attr);
		bracPaint = new Paint();
		nfPaint = new Paint();
		hypPaint = new Paint();
		bracPaint.setColor(Color.WHITE);
		bracPaint.setShadowLayer(0.5f,.2f,.2f,Color.BLACK);
		bracPaint.setStyle(Paint.Style.STROKE);
		bracPaint.setStrokeWidth(4f);
		nfPaint.setARGB(255,45,156,255);
		nfPaint.setTextSize(18);
		nfPaint.setAntiAlias(true);
		nfPaint.setTypeface(Typeface.DEFAULT_BOLD);
		hypPaint.setColor(Color.YELLOW);
		hypPaint.setTextSize(18);
		hypPaint.setAntiAlias(true);
		hypPaint.setTypeface(Typeface.DEFAULT_BOLD);
		this.setMinimumHeight(120);
		this.setMinimumWidth(120);
		//this.setMaxHeight(120);
		//this.setMaxWidth(120);
		requestLayout();
		
	}
	
	
	
	public void getFocus(float[] f)
	{
		float n, far, h, opt;
		n = f[Camera.Parameters.FOCUS_DISTANCE_NEAR_INDEX];
		far = f[Camera.Parameters.FOCUS_DISTANCE_FAR_INDEX];
		//h = far-n;
		opt = f[Camera.Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX];
		if(n == Float.POSITIVE_INFINITY)
			nearF = "\u221E";
		else
			nearF = String.valueOf(n);
		
		if(far == Float.POSITIVE_INFINITY)
			farF = "\u221E";
		else
			farF = String.valueOf(far);
	
		if(opt == Float.POSITIVE_INFINITY)
			hypF = "\u221E";
		else
			hypF = String.valueOf(opt);
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
	
	public void onDraw(Canvas c)
	{
		Bx = this.getLeft();
		By = this.getTop();
		Bw = this.getWidth();
		Bh = this.getHeight();
	
		//Left Side
		c.drawLine(20,0,0,20,bracPaint);
		c.drawLine(0,20,0,90,bracPaint);
		c.drawLine(0,90,20,110,bracPaint);
		
		//Right Side
		c.drawLine(100,0,120,20,bracPaint);
		c.drawLine(120,20,120,90,bracPaint);
		c.drawLine(120,90,100,110,bracPaint);
		
		//Center Focus Info
		c.drawText("NEAR: " + nearF, 15,40, nfPaint);
		c.drawText("OPT: " +  hypF, 15, 60, hypPaint);
		c.drawText("FAR: " + farF, 15,80, nfPaint);
		invalidate();
	}

}
