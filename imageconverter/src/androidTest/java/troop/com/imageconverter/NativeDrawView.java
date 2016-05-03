package troop.com.imageconverter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by troop on 13.08.2015.
 */
public class NativeDrawView extends View
{
    private Bitmap mBitmap;

    public NativeDrawView(Context context) {
        super(context);
    }

    public NativeDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NativeDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onDraw(Canvas canvas)
    {
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap, 0, 0, null);
        // force a redraw, with a different time-based pattern.
        invalidate();
    }
}
