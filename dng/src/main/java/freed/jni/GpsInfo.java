package freed.jni;

import android.location.Location;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

/**
 * Created by KillerInk on 01.03.2018.
 */

public class GpsInfo {

    private ByteBuffer byteBuffer;

    static
    {
        System.loadLibrary("freedcam");
    }

    private native ByteBuffer init();
    private native void clear(ByteBuffer byteBuffer);
    private native void setGpsInfo(double Altitude,float[] Latitude,float[] Longitude, String Provider, float[] gpsTime, String gpsDate,ByteBuffer byteBuffer);

    public GpsInfo(Location location)
    {
        byteBuffer = init();
        setGpsInfo(location.getAltitude(), parseGpsvalue(location.getLatitude()), parseGpsvalue(location.getLongitude()), location.getProvider(), parseGPStime(location.getTime()), parseGPSdate(location.getTime()), byteBuffer);
    }

    public ByteBuffer getByteBuffer()
    {
        return byteBuffer;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (byteBuffer != null)
            clear(byteBuffer);
        byteBuffer = null;
    }

    private float[] parseGpsvalue(double val)
    {

        String[] sec = Location.convert(val, Location.FORMAT_SECONDS).split(":");

        double dd = Double.parseDouble(sec[0]);
        double dm = Double.parseDouble(sec[1]);
        double ds = Double.parseDouble(sec[2].replace(",","."));

        return new float[]{ (float)dd ,(float)dm,(float)ds};
    }

    private float[] parseGPStime(long val)
    {
        SimpleDateFormat simpledatetime = new SimpleDateFormat("kk:mm:ss");
        String[] mytime = simpledatetime.format(val).split(":");

        double hh = Double.parseDouble(mytime[0]);
        double mm = Double.parseDouble(mytime[1]);
        double ss = Double.parseDouble(mytime[2]);

        return new float[] {(float)hh , (float)mm, (float)ss};
    }

    private String parseGPSdate(long val)
    {
        SimpleDateFormat simpledatetime = new SimpleDateFormat("yyyy:MM:dd");
        return simpledatetime.format(val);
    }
}
