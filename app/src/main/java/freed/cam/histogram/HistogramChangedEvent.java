package freed.cam.histogram;

public interface HistogramChangedEvent
{
    void onHistogramChanged(int[] histogram_data);
    int[] getRedHistogram();
    int[] getGreenHistogram();
    int[] getBlueHistogram();
    void setBlueHistogram(int[] blue);
    void setRedHistogram(int[] red);
    void setGreenHistogram(int[] green);
    void updateHistogram();
}
