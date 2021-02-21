package freed.cam.histogram;

import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import freed.cam.apis.camera2.CameraValuesChangedCaptureCallback;
import freed.utils.Log;
import freed.viewer.screenslide.views.MyHistogram;

public class HistogramController implements HistogramChangedEvent {

    private static final String TAG = HistogramController.class.getSimpleName();
    private MyHistogram myHistogram;
    private HistogramFeed feedToRegister;

    public HistogramController(MyHistogram myHistogram)
    {
        this.myHistogram = myHistogram;
    }

    public void setFeedToRegister(HistogramFeed histogramFeed) {
        this.feedToRegister = histogramFeed;
    }

    public void enable(boolean en)
    {
        if (en)
        {
            myHistogram.setVisibility(View.VISIBLE);
            myHistogram.bringToFront();
            if (feedToRegister != null)
                feedToRegister.setHistogramFeed(this);
            else
                Log.d(TAG, "histogram on feed to Register is null!");
        }
        else
        {
            myHistogram.setVisibility(View.GONE);
            if (feedToRegister != null)
                feedToRegister.setHistogramFeed(null);
            else
                Log.d(TAG, "histogram off feed to Register is null!");
        }
    }

    @Override
    public int[] getRedHistogram(){return myHistogram.getRedHistogram();}
    @Override
    public int[] getGreenHistogram(){return myHistogram.getGreenHistogram();}
    @Override
    public int[] getBlueHistogram() {return myHistogram.getBlueHistogram();}

    @Override
    public void updateHistogram() {
        myHistogram.redrawHistogram();
    }

    private int counter = 0;
    @Override
    public void onHistogramChanged(final int[] histogram_data) {
        counter++;
        if (histogram_data.length > 256*3)
        {
            counter = 0;
            myHistogram.post(new Runnable() {
                @Override

                public void run() {
                    //src pos 0,256,512
                    myHistogram.setLumaHistogram(histogram_data,0);
                }
            });
        }
    }
}
