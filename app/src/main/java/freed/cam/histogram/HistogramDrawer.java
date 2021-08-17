package freed.cam.histogram;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class HistogramDrawer {

    private final Paint mPaint = new Paint ();
    private final Path mHistoPath = new Path ();

    public void drawHistogram(Canvas canvas, int[] histogram, int color, int width, int height) {
        int max = 0 ;
        for ( int i = 0 ; i < histogram . length ; i ++) {
            if ( histogram [ i ] > max ) {
                max = histogram [ i ];
            }
        }
        float w = width; // - Spline.curveHandleSize();
        float h = height; // - Spline.curveHandleSize() / 2.0f;
        float dx = 0 ; // Spline.curveHandleSize() / 2.0f;
        float wl = w / histogram.length ;
        float wh = h / max ;

        mPaint.reset ();
        mPaint.setAntiAlias(true);
        mPaint.setARGB( 100 , 255 , 255 , 255 );
        mPaint.setStrokeWidth ((int) Math . ceil ( wl ));

// Draw grid
        mPaint.setStyle(Paint.Style.STROKE );
        canvas.drawRect( dx, 0 , dx + w , h , mPaint);
        canvas.drawLine( dx + w / 3 , 0 , dx + w / 3 , h , mPaint);
        canvas.drawLine( dx + 2 * w / 3 , 0 , dx + 2 * w / 3 , h , mPaint);

        mPaint.setStyle(Paint.Style.FILL );
        mPaint.setColor( color );
        mPaint.setStrokeWidth( 6 );
        mPaint.setXfermode( new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        mHistoPath.reset();
        mHistoPath.moveTo( dx , h );
        boolean firstPointEncountered = false;
        float prev = 0 ;
        float last = 0 ;
        for ( int i = 0 ; i < histogram . length ; i ++) {
            float x = i * wl + dx;
            float l = histogram [ i ] * wh;
            if ( l != 0 ) {
                float v = h - ( l + prev ) / 2.0f;
                if (!firstPointEncountered ) {
                    mHistoPath.lineTo ( x , h );
                    firstPointEncountered = true;
                }
                mHistoPath.lineTo(x ,v);
                prev = l ;
                last = x ;
            }
        }
        mHistoPath.lineTo(last, h);
        mHistoPath.lineTo(w, h);
        mHistoPath.close();
        canvas.drawPath(mHistoPath, mPaint);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle ( Paint.Style. STROKE );
        mPaint.setARGB( 255 , 200 , 200 , 200 );
        canvas.drawPath (mHistoPath, mPaint);
    }
}
