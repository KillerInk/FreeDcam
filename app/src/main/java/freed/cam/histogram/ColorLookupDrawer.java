package freed.cam.histogram;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class ColorLookupDrawer {

    private final Paint mPaint = new Paint ();
    private final Path mHistoPathR = new Path ();
    private final Path mHistoPathG = new Path ();
    private final Path mHistoPathB = new Path ();
    public void drawColorLookup(Canvas canvas, int[] waveformdata, int data_width, int data_height, int width, int height, int offset_left)
    {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth( 0 );
        //mPaint.setXfermode( new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        /*mHistoPathR.reset();
        mHistoPathR.moveTo( 0 , 0 );
        mHistoPathG.reset();
        mHistoPathG.moveTo( 0 , 0 );
        mHistoPathB.reset();
        mHistoPathB.moveTo( 0 , 0 );*/
        float drawheight = width / data_width;
        boolean lastred = false;
        boolean lastgreen = false;
        boolean lastblue = false;
        HistogramProcessor.Colormerge input = new HistogramProcessor.Colormerge();
        for (int y = 0; y < data_height; y++)
        {
            for(int x = 0; x < data_width; x++)
            {
                input.setARGBColor(waveformdata[y*data_width+x]);
                float lum = input.getLuminanceNormalized();
                float ypos = height - (height*lum);
                float xpos = (width-offset_left*2)/data_width * x +offset_left;
                mPaint.setColor(input.getARGB());
                canvas.drawCircle(xpos, ypos, 1, mPaint);
                /*if (x == 0)
                {
                    mHistoPathR.reset();
                    mHistoPathR.moveTo( xpos , ypos + (height /255 * input.red));
                    if (input.red > 0)
                        lastred = true;
                    else
                        lastred = false;
                    mHistoPathG.reset();
                    mHistoPathG.moveTo( xpos , ypos + (height /255 *input.green));
                    if (input.green > 0)
                        lastgreen = true;
                    else
                        lastgreen = false;
                    mHistoPathB.reset();
                    mHistoPathB.moveTo( xpos , ypos + (height /255 *input.blue));
                    if (input.blue > 0)
                        lastblue = true;
                    else
                        lastblue = false;
                }
                else
                {
                    lastred = processPath(canvas, lastred, ypos, xpos, input.red, mHistoPathR, Color.RED);

                    lastgreen = processPath(canvas, lastgreen, ypos, xpos, input.green, mHistoPathG, Color.GREEN);

                    lastblue = processPath(canvas, lastblue, ypos, xpos, input.blue, mHistoPathB, Color.BLUE);
                }*/

               /* input.setARGBColor(waveformdata[y*data_width+x+1]);
                lum = input.getLuminanceNormalized();
                float ypos2 = height - (height*lum);
                float xpos2 = (width/data_width * x) +1*drawheight +offset_left;
                canvas.drawLine(xpos,ypos,xpos2,ypos2,mPaint);*/
                //canvas.drawCircle(xpos, ypos, 4, mPaint);

                /* int pix = waveformdata[y*data_width+x];
                int pix2 = waveformdata[y*data_width+x+1];


               float luminance = (Color.red(pix) * 0.2126f + Color.green(pix) * 0.7152f + Color.blue(pix) * 0.0722f);
                float luminance2 = (Color.red(pix2) * 0.2126f + Color.green(pix2) * 0.7152f + Color.blue(pix2) * 0.0722f);
                float ypos = height - (drawheight*luminance);
                float ypos2 = height - (drawheight*luminance2);
                float xpos = width/data_width * x;
                mPaint.setColor(pix);
                canvas.drawLine(xpos, ypos,xpos, ypos2, mPaint);*/
                //canvas.drawCircle(xpos, ypos, 1, mPaint);

                /*float luminance = (Color.alpha(pix) * 0.2126f + Color.red(pix) * 0.7152f + Color.green(pix) * 0.0722f);
                float luminance2 = (Color.alpha(pix2) * 0.2126f + Color.red(pix2) * 0.7152f + Color.green(pix2) * 0.0722f);
                float ypos = height/256*luminance;
                float xpos = width/256 * x;
                float ypos2= height/256*luminance2;
                float xpos2 = width/256 * x;
                mPaint.setColor(pix);
                canvas.drawLine(xpos,ypos,xpos2,ypos2,mPaint);*/

                /*int r = Color.alpha(pix);
                int g = Color.red(pix);
                int b = Color.green(pix);

                int r1 = Color.alpha(pix2);
                int g1 = Color.red(pix2);
                int b1 = Color.green(pix2);

                float ypos = drawheight * r;
                float ypos1 = drawheight * r1;
                float xpos = width / data_width * x;
                mPaint.setColor(Color.RED);
                canvas.drawLine(xpos,ypos,xpos,ypos1,mPaint);

                ypos = drawheight*g;
                ypos1 = drawheight * g1;
                mPaint.setColor(Color.GREEN);
                canvas.drawLine(xpos,ypos,xpos,ypos1,mPaint);

                ypos = drawheight*b;
                ypos1 = drawheight * b1;
                mPaint.setColor(Color.BLUE);
                canvas.drawLine(xpos,ypos,xpos,ypos1,mPaint);*/
            }
            /*drawPath(mHistoPathR,Color.RED,canvas);
            drawPath(mHistoPathG,Color.GREEN,canvas);
            drawPath(mHistoPathB,Color.BLUE,canvas);*/
        }
        //mHistoPath.lineTo(w, h);
        /*mHistoPath.close();
        canvas.drawPath(mHistoPath, mPaint);*/

        /*float w = width; // - Spline.curveHandleSize();
        float h = height; // - Spline.curveHandleSize() / 2.0f;
        float wl = w / waveformdata.length ;
        float hl = h / waveformdata.length ;
        for ( int i = 0 ; i < waveformdata.length ; i ++) {
            float x = i * wl;
            float y = i * hl;
            mPaint.setColor(waveformdata[i]);
            canvas.drawCircle(x, y, 2, mPaint);
        }*/
    }

    private boolean processPath(Canvas canvas, boolean lastred, float ypos, float xpos, int color, Path mHistoPathR, int red2) {
        if (color > 0 && lastred)
            mHistoPathR.lineTo(xpos, ypos + canvas.getHeight()/255 * color);
        else if (color == 0 && lastred) {
            drawPath(mHistoPathR, red2, canvas);
            lastred = false;
        } else if (!lastred && color > 0) {
            mHistoPathR.reset();
            mHistoPathR.moveTo(xpos,  canvas.getHeight()/255 * color);
            lastred = true;
        }
        return lastred;
    }

    private void drawPath(Path path, int color, Canvas canvas)
    {
        path.close();
        mPaint.setColor(color);
        canvas.drawPath(path, mPaint);
    }

}
