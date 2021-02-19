package freed.cam.histogram;

public interface HistogramChangedEvent
{
    void onHistogramChanged(int[] histogram_data);
    int[] getRedHistogram();
    int[] getGreenHistogram();
    int[] getBlueHistogram();
    void updateHistogram();
}
