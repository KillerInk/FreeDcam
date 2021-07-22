package freed.cam.histogram;

import android.view.View;

import freed.utils.Log;

public class HistogramController implements HistogramChangedEvent {

    public interface DataListner
    {
        void setData(HistogramData data);
        void setWaveFormData(int[] data, int width, int height);
    }

    private static final String TAG = HistogramController.class.getSimpleName();
    private MyHistogram myHistogram;
    private HistogramFeed feedToRegister;
    private HistogramProcessor histogramProcessor;
    private boolean enabled;
    private HistogramData histogramData;
    private DataListner dataListner;

    public HistogramController()
    {
        histogramProcessor = new HistogramProcessor(this);
        histogramData = new HistogramData();
    }

    public void setMyHistogram(MyHistogram myHistogram)
    {
        this.myHistogram = myHistogram;
    }

    public void setDataListner(DataListner dataListner) {
        this.dataListner = dataListner;
    }

    public void setFeedToRegister(HistogramFeed histogramFeed) {
        this.feedToRegister = histogramFeed;
    }

    public void enable(boolean en)
    {
        enabled = en;
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
            if (dataListner != null) {
                dataListner.setData(null);
                dataListner.setWaveFormData(null,0,0);
            }
            if (feedToRegister != null)
                feedToRegister.setHistogramFeed(null);
            else
                Log.d(TAG, "histogram off feed to Register is null!");
        }
    }

    @Override
    public int[] getRedHistogram(){return histogramData.getRedHistogram();}
    @Override
    public int[] getGreenHistogram(){return histogramData.getGreenHistogram();}
    @Override
    public int[] getBlueHistogram() {return histogramData.getBlueHistogram();}

    @Override
    public void updateHistogram() {
        myHistogram.redrawHistogram();
    }

    @Override
    public void onHistogramChanged(final int[] histogram_data) {
        histogramData.setHistogramData(histogram_data, HistogramData.HistoDataAlignment.RGBA);
        myHistogram.post(new Runnable() {
            @Override

            public void run() {
                //src pos 0,256,512
                myHistogram.setHistogramData(histogramData);
            }
        });
        if (dataListner != null)
            dataListner.setData(histogramData);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void setImageData(final byte[] imagedata,int width, int height)
    {
        histogramProcessor.add(imagedata,width,height);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setWaveFormData(int[] waveFormData,int width, int height)
    {
        if (dataListner != null)
            dataListner.setWaveFormData(waveFormData,width,height);
    }
}
