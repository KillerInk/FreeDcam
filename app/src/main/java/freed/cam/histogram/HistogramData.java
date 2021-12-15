package freed.cam.histogram;

public class HistogramData {

    public enum HistoDataAlignment
    {
        RarrayGarrayBarray,
        RGBA,
    }

    public HistogramData()
    {
        redHistogram = new int [ 256 ];
        greenHistogram = new int [ 256 ];
        blueHistogram = new int [ 256 ];
    }

    public HistogramData(int[] redHistogram, int[] greenHistogram, int[] blueHistogram)
    {
        this.redHistogram = redHistogram;
        this.greenHistogram = greenHistogram;
        this.blueHistogram = blueHistogram;
    }

    private int [] redHistogram;
    private int [] greenHistogram;
    private int [] blueHistogram;

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

    public void setGreenHistogram(int[] greenHistogram) {
        this.greenHistogram = greenHistogram;
    }

    public void setRedHistogram(int[] redHistogram) {
        this.redHistogram = redHistogram;
    }

    public void setBlueHistogram(int[] blueHistogram) {
        this.blueHistogram = blueHistogram;
    }
}
