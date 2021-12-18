package freed.cam.apis.camera2.parameters.ae;

public class AeMath {

    public double getTargetEv(double luma)
    {
        return log2(luma * 100 / 12.5);
    }

    public double getIso(double aperture, double exposuretime, double ev)
    {
        double expotime_sec = getExpotimeInSec(exposuretime);
        return ((aperture*aperture) * 100.0) / (expotime_sec * Math.pow(2.0, ev));
    }

    public double getExposureTime(double exposuretime,double evdif)
    {
        double expotime_sec = getExpotimeInSec(exposuretime);
        return expotime_sec * Math.pow(2.0, -evdif);
    }

    public double getDefaultExpoTime(float focal_length)
    {
        return  1.0f / (focal_length * 1000.0f);
    }

    public double getCurrentEV(double aperture, double exposuretime, double iso)
    {
        double expotime_sec = getExpotimeInSec(exposuretime);
        double tmp =  (((aperture*aperture)*100)/(expotime_sec*iso));
        return log2(tmp);
    }

    private double log2(double l)
    {
        return (Math.log(l) / Math.log(2));
    }

    private double getExpotimeInSec(double exposuretime)
    {
        return exposuretime/1000000000;
    }


}
