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

package freed.cam.histogram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import freed.utils.Log;

public class MyHistogram extends View {

    public MyHistogram(Context context) {
        super(context);
        init();
    }

    public MyHistogram(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyHistogram(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        histogramDrawer = new HistogramDrawer();
    }

    private HistogramDrawer histogramDrawer;
    private HistogramData histogramData;

    public void setHistogramData(HistogramData histogramData) {
        this.histogramData = histogramData;
        invalidate();
    }

    public void SetHistogramData(int[] data)
    {
        if (histogramData == null)
            histogramData = new HistogramData();
        histogramData.setHistogramData(data, HistogramData.HistoDataAlignment.RGBA);
        invalidate();
    }



    public void redrawHistogram()
    {
        post(redrawHisto);
    }

    private final Runnable redrawHisto = () -> {
        bringToFront();
        invalidate();
    };

    public void onDraw (Canvas canvas)
    {
        try {
            canvas.drawARGB ( 0 , 0 , 0 , 0 );
            histogramDrawer.drawHistogram(canvas , histogramData.getRedHistogram(), Color.RED,getWidth(),getHeight());
            histogramDrawer.drawHistogram(canvas , histogramData.getGreenHistogram(), Color.GREEN,getWidth(),getHeight());
            histogramDrawer.drawHistogram(canvas , histogramData.getBlueHistogram(), Color.BLUE,getWidth(),getHeight());
        }
        catch (RuntimeException ex)
        {
            Log.d("histogram","bitmap got released");
        }
    }
}