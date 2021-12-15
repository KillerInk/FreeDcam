package freed.cam.histogram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.ArrayBlockingQueue;

import freed.gl.MeteringProcessor;
import freed.settings.SettingsManager;
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
    private ImageView waveFormView;
    private MeteringProcessor meteringProcessor;

    public HistogramController(SettingsManager settingsManager)
    {
        histogramProcessor = new HistogramProcessor(this);
        histogramData = new HistogramData();
        meteringProcessor = new MeteringProcessor(settingsManager);
    }

    public MeteringProcessor getMeteringProcessor() {
        return meteringProcessor;
    }

    public void setMyHistogram(MyHistogram myHistogram)
    {
        this.myHistogram = myHistogram;
        if (enabled && myHistogram != null)
            myHistogram.setVisibility(View.VISIBLE);
    }

    public void setWaveFormView(ImageView imageView)
    {
        this.waveFormView = imageView;
        if (enabled && waveFormView != null)
            waveFormView.setVisibility(View.VISIBLE);
    }

    public void setDataListner(DataListner dataListner) {
        this.dataListner = dataListner;
    }

    public void setFeedToRegister(HistogramFeed histogramFeed) {
        this.feedToRegister = histogramFeed;
    }

    public void enable(boolean en)
    {
        if(myHistogram != null) {
            enabled = en;
            myHistogram.post(new Runnable() {
                @Override
                public void run() {
                    if (en) {
                        if (myHistogram != null) {
                            myHistogram.setVisibility(View.VISIBLE);
                            myHistogram.bringToFront();
                        }
                        if (waveFormView != null) {
                            waveFormView.setVisibility(View.VISIBLE);
                            waveFormView.bringToFront();
                        }
                        if (feedToRegister != null)
                            feedToRegister.setHistogramFeed(HistogramController.this);
                        else
                            Log.d(TAG, "histogram on feed to Register is null!");
                    } else {
                        if (myHistogram != null)
                            myHistogram.setVisibility(View.GONE);
                        if (waveFormView != null)
                            waveFormView.setVisibility(View.GONE);
                        if (dataListner != null) {
                            dataListner.setData(null);
                            dataListner.setWaveFormData(null, 0, 0);
                        }
                        if (feedToRegister != null)
                            feedToRegister.setHistogramFeed(null);
                        else
                            Log.d(TAG, "histogram off feed to Register is null!");
                    }
                }
            });

        }
    }

    @Override
    public int[] getRedHistogram(){return histogramData.getRedHistogram();}
    @Override
    public int[] getGreenHistogram(){return histogramData.getGreenHistogram();}
    @Override
    public int[] getBlueHistogram() {return histogramData.getBlueHistogram();}

    @Override
    public void setBlueHistogram(int[] blue) {
        histogramData.setBlueHistogram(blue);
    }

    @Override
    public void setRedHistogram(int[] red) {
        histogramData.setRedHistogram(red);
    }

    @Override
    public void setGreenHistogram(int[] green) {
        histogramData.setGreenHistogram(green);
    }

    @Override
    public void updateHistogram() {
        myHistogram.post(new Runnable() {
            @Override

            public void run() {
                //src pos 0,256,512
                myHistogram.setHistogramData(histogramData);
            }
        });
    }

    @Override
    public void onHistogramChanged(final int[] histogram_data) {
        histogramData.setHistogramData(histogram_data, HistogramData.HistoDataAlignment.RGBA);
        myHistogram.post(() -> {
            //src pos 0,256,512
            myHistogram.setHistogramData(histogramData);
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
        if(waveFormView != null) {
            waveFormUpdater.setData(waveFormData, width, height);
            waveFormView.post(waveFormUpdater);
        }

        if (dataListner != null)
            dataListner.setWaveFormData(waveFormData,width,height);
    }


    WaveFormUpdater waveFormUpdater = new WaveFormUpdater() {

        ArrayBlockingQueue<int[]> waveformQueue = new ArrayBlockingQueue(3);
        int width; int height;
        private Bitmap bitmap;

        public void setData(int[] waveFormData,int width, int height)
        {
            synchronized (waveformQueue)
            {
                if (waveformQueue.size() == 3)
                    waveformQueue.poll();
                waveformQueue.offer(waveFormData);
            }
            this.width = width;
            this.height = height;

        }

        @Override
        public void run() {

            int[] waveFormData = waveformQueue.poll();
            if (waveFormData == null || width == 0 || height == 0)
            {
                Log.e(TAG, "Error updating waveform");
                return;
            }
            if (bitmap == null)
                bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

            try {
                bitmap.setPixels(waveFormData, 0, width, 0, 0, width, height);
                waveFormView.setImageBitmap(bitmap);
            }
            catch (IllegalArgumentException ex)
            {
                Log.e(TAG, ex.getMessage());
            }
        }
    };

    interface  WaveFormUpdater extends Runnable
    {
        public void setData(int[] waveFormData,int width, int height);
    }

    interface  HistogramUpdater extends Runnable
    {
        public void setData(HistogramData histogramData);
    }

    HistogramUpdater histogramUpdater = new HistogramUpdater() {
        ArrayBlockingQueue<HistogramData> histogramDatas = new ArrayBlockingQueue(3);
        @Override
        public void setData(HistogramData histogramData) {
            synchronized (histogramDatas)
            {
                if (histogramDatas.size() == 3)
                    histogramDatas.poll();
                histogramDatas.offer(histogramData);
            }
        }

        @Override
        public void run() {
            HistogramData data = histogramDatas.poll();
            if (data != null)
                myHistogram.setHistogramData(data);
        }
    };

    public void updateData(HistogramData data)
    {
        histogramUpdater.setData(data);
        myHistogram.post(histogramUpdater);
    }
}
