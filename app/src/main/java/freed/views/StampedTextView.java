package freed.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.troop.freedcam.R;

public class StampedTextView extends View {

    private Paint paint;
    ColorFilter filter;
    PorterDuffXfermode xfermode;

    private String text;

    float corner = 20;
    float[] corners = new float[]{
            corner, corner,        // Top left radius in px
            corner, corner,        // Top right radius in px
            corner, corner,          // Bottom right radius in px
            corner, corner           // Bottom left radius in px
    };

    public StampedTextView(Context context) {
        super(context);
        init();
    }

    public StampedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StampedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        paint = new Paint();
        paint.setTypeface(ResourcesCompat.getFont(getContext(),R.font.freedcam));
        filter = new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        text = (String) getResources().getText(R.string.font_exposurelock);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        paint.setXfermode(null);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
        final Path path = new Path();
        RectF f = new RectF(0,0,getWidth(),getHeight());
        path.addRoundRect(f, corners, Path.Direction.CW);
        paint.setColor(Color.BLACK);

        canvas.drawPath(path, paint);
        //canvas.drawRect(0,0,getWidth(),getHeight(),paint);


        //paint.setColorFilter(filter);
        paint.setColor(Color.BLUE);
        paint.setTextSize(40);
        paint.setXfermode(xfermode);
        canvas.drawText(text,20,60,paint);


    }
}
