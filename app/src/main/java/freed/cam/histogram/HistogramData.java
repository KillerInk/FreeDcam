package freed.cam.histogram;

public class HistogramData {

    public enum HistoDataAlignment
    {
        RarrayGarrayBarray,
        RGBA,
    }

    private int [] redHistogram = new int [ 256 ];
    private int [] greenHistogram = new int [ 256 ];
    private int [] blueHistogram = new int [ 256 ];

    public int[] getRedHistogram(){return redHistogram;}
    public int[] getGreenHistogram(){return greenHistogram;}
    public int[] getBlueHistogram() {return blueHistogram;}

    public void setHistogramData(int[] histodata, HistoDataAlignment alignment)
    {
        if (alignment == HistoDataAlignment.RarrayGarrayBarray)
        {
            System.arraycopy(histodata, 0, redHistogram, 0, 256);
            System.arraycopy(histodata, 256, greenHistogram, 0, 256);
            System.arraycopy(histodata, 512, blueHistogram, 0, 256);
        }
        else if (alignment == HistoDataAlignment.RGBA)
        {
            int t = 0;
            for (int i = 0; i< histodata.length; i+=4)
            {
                redHistogram[t] = histodata[i];
                greenHistogram[t] = histodata[i+1];
                blueHistogram[t] = histodata[i+2];
                t++;
            }
        }
    }
}
