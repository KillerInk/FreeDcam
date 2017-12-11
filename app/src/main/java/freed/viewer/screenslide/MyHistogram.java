/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.viewer.screenslide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import freed.utils.Log;

public class MyHistogram extends View {

    public MyHistogram(Context context) {
        super(context);
    }

    public MyHistogram(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHistogram(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final Paint mPaint = new Paint ();
    private int [] redHistogram = new int [ 256 ];
    private int [] greenHistogram = new int [ 256 ];
    private int [] blueHistogram = new int [ 256 ];
    private final Path mHistoPath = new Path ();

    public int[] getRedHistogram(){return redHistogram;}
    public int[] getGreenHistogram(){return greenHistogram;}
    public int[] getBlueHistogram() {return blueHistogram;}

    private void drawHistogram(Canvas canvas, int[] histogram, int color) {
        int max = 0 ;
        for ( int i = 0 ; i < histogram . length ; i ++) {
            if ( histogram [ i ] > max ) {
                max = histogram [ i ];
            }
        }
        float w = getWidth(); // - Spline.curveHandleSize();
        float h = getHeight(); // - Spline.curveHandleSize() / 2.0f;
        float dx = 0 ; // Spline.curveHandleSize() / 2.0f;
        float wl = w / histogram.length ;
        float wh = h / max ;

        mPaint.reset ();
        mPaint.setAntiAlias(true);
        mPaint.setARGB( 100 , 255 , 255 , 255 );
        mPaint.setStrokeWidth ((int) Math . ceil ( wl ));

// Draw grid
        mPaint.setStyle(Style.STROKE );
        canvas.drawRect( dx, 0 , dx + w , h , mPaint);
        canvas.drawLine( dx + w / 3 , 0 , dx + w / 3 , h , mPaint);
        canvas.drawLine( dx + 2 * w / 3 , 0 , dx + 2 * w / 3 , h , mPaint);

        mPaint.setStyle(Style.FILL );
        mPaint.setColor( color );
        mPaint.setStrokeWidth( 6 );
        mPaint.setXfermode( new PorterDuffXfermode(Mode.SCREEN));
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
        mPaint.setStyle ( Style . STROKE );
        mPaint.setARGB( 255 , 200 , 200 , 200 );
        canvas.drawPath (mHistoPath, mPaint);
    }

    public void SetRgbArrays(int[] r, int[]g, int[] b)
    {
        redHistogram = r;
        greenHistogram = g;
        blueHistogram = b;
        invalidate();
    }

    public void SetHistogramData(int[] histo)
    {
        if (histo == null)
            return;
        System.arraycopy( histo , 0 , redHistogram, 0 , 256 );
        System.arraycopy( histo , 256 , greenHistogram, 0 , 256 );
        System.arraycopy( histo , 512 , blueHistogram, 0 , 256 );
        invalidate();
    }

    public void redrawHistogram()
    {
        post(redrawHisto);
    }


    private Runnable redrawHisto = new Runnable() {
        @Override
        public void run() {
            bringToFront();
            invalidate();
        }
    };


    public void onDraw (Canvas canvas)
    {
        try {

        canvas.drawARGB ( 0 , 0 , 0 , 0 );
            drawHistogram(canvas , redHistogram, Color.RED);
            drawHistogram(canvas , greenHistogram, Color.GREEN);
            drawHistogram(canvas , blueHistogram, Color.BLUE);
        }
        catch (RuntimeException ex)
        {
            Log.d("histogram","bitmap got released");
        }
    }


}