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

package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by troop on 20.09.2016.
 */
public class CompassDrawer extends View
{
    private float positionRelativeToNorth = 0;
    private int width;
    private int height;
    private float itemwidth;
    private Paint paint;
    private float degreePerPixel;
    private final String TAG = CompassDrawer.class.getSimpleName();
    private float allitemsWidth;
    private final float TEXTSHADOWMARGINE = 3;

    private String[] directionValue = { "NW", "N", "NE", "E","SE", "S", "SW", "W"};

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public CompassDrawer(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see (Context, AttributeSet, int)
     */
    public CompassDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init()
    {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
        paint.setTextSize(convertDpiToPixel(15));
        invalidate();
    }

    private float convertDpiToPixel(int dpi)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, getResources().getDisplayMetrics());
    }

    public void SetPosition(final float pos)
    {
        //Log.d(TAG, "SetPositon:" + pos);
        this.post(new Runnable() {
            @Override
            public void run() {
                positionRelativeToNorth = pos;
                invalidate();
            }
        });

    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        paint.setColor(Color.BLACK);
        canvas.drawLine(width/2+TEXTSHADOWMARGINE, 0+TEXTSHADOWMARGINE, width/2+TEXTSHADOWMARGINE, 20+TEXTSHADOWMARGINE,paint);
        paint.setColor(Color.WHITE);
        canvas.drawLine(width/2, 0, width/2, 20,paint);
        paint.setTextSize(convertDpiToPixel(15));
        for(int i = 0; i < directionValue.length; i++)
        {
            float postodraw = itemwidth * i + width/2 + (int)positionRelativeToNorth * degreePerPixel;
            if (postodraw + width/2 > allitemsWidth + itemwidth)
                postodraw -= allitemsWidth;
            paint.setColor(Color.BLACK);
            canvas.drawText(directionValue[i], getItemCenterMargine(postodraw), height/2 +TEXTSHADOWMARGINE, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(directionValue[i], getItemCenter(postodraw), height/2, paint);
            paint.setColor(Color.BLACK);
            canvas.drawLine(getItemCenterMargine(postodraw), 21+TEXTSHADOWMARGINE, getItemCenterMargine(postodraw), 41+TEXTSHADOWMARGINE,paint);
            paint.setColor(Color.WHITE);
            canvas.drawLine(getItemCenter(postodraw), 21, getItemCenter(postodraw), 41,paint);
        }
    }

    private float getItemCenter(float posTodraw)
    {
        return posTodraw+ itemwidth;
    }

    private float getItemCenterMargine(float posTodraw)
    {
        return posTodraw+ itemwidth +TEXTSHADOWMARGINE;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        itemwidth = width / 4;
        allitemsWidth = itemwidth*directionValue.length;
        degreePerPixel = allitemsWidth/360;
        invalidate();
    }
}
