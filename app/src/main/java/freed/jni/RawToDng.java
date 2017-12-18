package freed.jni;

import android.location.Location;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import freed.dng.DngProfile;
import freed.utils.Log;
import freed.utils.StorageFileManager;

/**
 * Created by troop on 15.02.2015.
 */
public class RawToDng
{
    static
    {
        System.loadLibrary("freedcam");
    }

    private final String TAG = RawToDng.class.getSimpleName();

    private ByteBuffer byteBuffer;

    private String wbct;
    private byte[] opcode2;
    private byte[] opcode3;

    private native ByteBuffer init();
    private native void recycle(ByteBuffer byteBuffer);
    private native long GetRawBytesSize(ByteBuffer byteBuffer);
    private native int GetRawHeight(ByteBuffer byteBuffer);
    private native void SetGPSData(double Altitude,float[] Latitude,float[] Longitude, String Provider, float[] gpsTime, String gpsDate,ByteBuffer byteBuffer);
    private native void SetThumbData(byte[] mThumb, int widht, int height,ByteBuffer byteBuffer);
    private native void WriteDNG(ByteBuffer byteBuffer);
    private native void SetOpCode3(byte[] opcode,ByteBuffer byteBuffer);
    private native void SetOpCode2(byte[] opcode,ByteBuffer byteBuffer);
    private native void SetRawHeight(int height,ByteBuffer byteBuffer);
    private native void SetModelAndMake(String model, String make,ByteBuffer byteBuffer);
    private native void SetBayerData(byte[] fileBytes, String fileout,ByteBuffer byteBuffer);
    private native void SetBayerDataFD(byte[] fileBytes, int fileout, String filename,ByteBuffer byteBuffer);
    private native void SetBayerInfo(float[] colorMatrix1,
                                     float[] colorMatrix2,
                                     float[] neutralColor,
                                     float[] fowardMatrix1,
                                     float[] fowardMatrix2,
                                     float[] reductionMatrix1,
                                     float[] reductionMatrix2,
                                     double[] noiseMatrix,
                                     int blacklevel,
                                     int whitelevel,
                                     String bayerformat,
                                     int rowSize,
                                     String devicename,
                                     int rawType,int width,int height,ByteBuffer byteBuffer);
    private native void SetExifData(int iso,
                                           double expo,
                                           int flash,
                                           float fNum,
                                           float focalL,
                                           String imagedescription,
                                           String orientation,
                                           float exposureIndex,ByteBuffer byteBuffer);

    private native void SetDateTime(String datetime,ByteBuffer byteBuffer);

    private native void SetToneCurve(float tonecurve[],ByteBuffer byteBuffer);
    private native void SetHueSatMapData1(float tonecurve[],ByteBuffer byteBuffer);
    private native void SetHueSatMapData2(float tonecurve[],ByteBuffer byteBuffer);
    private native void SetHueSatMapDims(int[] dims,ByteBuffer byteBuffer);
    private native void SetBaselineExposure(float baselineexposure,ByteBuffer byteBuffer);
    private native void SetBaselineExposureOffset(float baselineexposureoffset,ByteBuffer byteBuffer);

    public static RawToDng GetInstance()
    {
        return new RawToDng();
    }

    private RawToDng()
    {
        byteBuffer = init();
        wbct = "";
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (byteBuffer == null)
            return;
        recycle(byteBuffer);
        byteBuffer = null;
    }

    public void setOpcode2(byte[] opcode2)
    {
        this.opcode2 = opcode2;
    }

    public void setOpcode3(byte[] opcode3)
    {
        this.opcode3 = opcode3;
    }

    public void SetWBCT(String wbct)
    {
        this.wbct =wbct;
    }

    private float[] getWbCtMatrix(String wbct)
    {
        int wb = Integer.parseInt(wbct) / 100;
        double r,g,b;
        double tmpcol = 0;
        //red

        if( (double) wb <= 66 )
        {
            r = 255;
            g = (double) wb -10;
            g = 99.4708025861 * Math.log(g) - 161.1195681661;
            if( (double) wb <= 19)
            {
                b = 0;
            }
            else
            {
                b = (double) wb -10;
                b = 138.5177312231 * Math.log(b) - 305.0447927307;
            }
        }
        else
        {
            r = (double) wb - 60;
            r = 329.698727446 * Math.pow(r, -0.1332047592);
            g = (double) wb -60;
            g = 288.1221695283 * Math.pow(g, -0.0755148492);
            b = 255;
        }
        Log.d(TAG, "ColorTemp=" + (double) wb + " WBCT = r:" + r + " g:" + g + " b:" + b);
        float rf,gf,bf = 0;

        rf = (float) getRGBToDouble(checkminmax((int)r))/2;
        gf = (float) getRGBToDouble(checkminmax((int)g));
        bf = (float) getRGBToDouble(checkminmax((int)b))/2;
        Log.d(TAG, "ColorTemp=" + (double) wb + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
            rf = rf / gf;
            bf = bf / gf;
            gf = 1;
        Log.d(TAG, "ColorTemp=" + (double) wb + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
        return new float[]{rf, gf,bf};
    }

    private double getRGBToDouble(int color)
    {
        double t = color;
        t = t * 3 *2;
        t = t / 255;
        t = t / 3;
        t += 1;

        return t;
    }

    private int checkminmax(int val)
    {
        if (val>255)
            return 255;
        else if(val < 0)
            return 0;
        else return val;
    }




    private long GetRawSize()
    {
        return GetRawBytesSize(byteBuffer);
    }

    public void SetGpsData(double Altitude,double Latitude,double Longitude, String Provider, long gpsTime)
    {
        Log.d(TAG,"Latitude:" + Latitude + "Longitude:" +Longitude);
        SetGPSData(Altitude, parseGpsvalue(Latitude), parseGpsvalue(Longitude), Provider, parseGPStime(gpsTime), parseGPSdate(gpsTime), byteBuffer);
    }

    public void setExifData(int iso,
                            double expo,
                            int flash,
                            float fNum,
                            float focalL,
                            String imagedescription,
                            String orientation,
                            float exposureIndex)
    {
        SetExifData(iso, expo, flash, fNum, focalL, imagedescription, orientation, exposureIndex,byteBuffer);
        SetDateTime(StorageFileManager.getStringExifPattern().format(new Date()),byteBuffer);
        SetBaselineExposureOffset(exposureIndex,byteBuffer);
    }

    public static float[] parseGpsvalue(double val)
    {

        String[] sec = Location.convert(val, Location.FORMAT_SECONDS).split(":");

        double dd = Double.parseDouble(sec[0]);
        double dm = Double.parseDouble(sec[1]);
        double ds = Double.parseDouble(sec[2].replace(",","."));

        return new float[]{ (float)dd ,(float)dm,(float)ds};
    }

    public static float[] parseGPStime(long val)
    {
        SimpleDateFormat simpledatetime = new SimpleDateFormat("kk:mm:ss");
        String[] mytime = simpledatetime.format(val).split(":");

        double hh = Double.parseDouble(mytime[0]);
        double mm = Double.parseDouble(mytime[1]);
        double ss = Double.parseDouble(mytime[2]);

        return new float[] {(float)hh , (float)mm, (float)ss};
    }

    public static String parseGPSdate(long val)
    {
        SimpleDateFormat simpledatetime = new SimpleDateFormat("yyyy:MM:dd");
        String mydate = simpledatetime.format(val);
        return mydate;
    }

    public void setThumbData(byte[] mThumb, int widht, int height)
    {
        SetThumbData(mThumb, widht,height,byteBuffer);
    }

    private void SetModelAndMake(String make)
    {
        SetModelAndMake(Build.MODEL, Build.MANUFACTURER,byteBuffer);
    }

    public void setBayerData(byte[] fileBytes, String fileout) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }
        SetBayerData(fileBytes, fileout,byteBuffer);
        if (opcode2 != null)
            SetOpCode2(opcode2,byteBuffer);
        if (opcode3 != null)
            SetOpCode3(opcode3,byteBuffer);

    }

    public void SetBayerDataFD(byte[] fileBytes, ParcelFileDescriptor fileout, String filename) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }

        SetBayerDataFD(fileBytes, fileout.getFd(), filename,byteBuffer);
        if (opcode2 != null)
            SetOpCode2(opcode2,byteBuffer);
        if (opcode3 != null)
            SetOpCode3(opcode3,byteBuffer);
    }


    private void SetBayerInfo(float[] colorMatrix1,
                              float[] colorMatrix2,
                              float[] neutralColor,
                              float[] fowardMatrix1,
                              float[] fowardMatrix2,
                              float[] reductionMatrix1,
                              float[] reductionMatrix2,
                              double[] noise,
                              int blacklevel,
                              int whitelevel,
                              String bayerformat,
                              int rowSize,
                              int tight, int width, int height)
    {
        if (TextUtils.isEmpty(wbct))
            SetBayerInfo(colorMatrix1, colorMatrix2, neutralColor, fowardMatrix1, fowardMatrix2, reductionMatrix1, reductionMatrix2, noise, blacklevel,whitelevel, bayerformat, rowSize, Build.MODEL, tight, width, height,byteBuffer);
        else if (!TextUtils.isEmpty(wbct))
            SetBayerInfo(colorMatrix1, colorMatrix2, getWbCtMatrix(wbct), fowardMatrix1, fowardMatrix2, reductionMatrix1, reductionMatrix2, noise, blacklevel,whitelevel, bayerformat, rowSize, Build.MODEL, tight, width, height,byteBuffer);

    }


    private void setRawHeight(int height)
    {
        SetRawHeight(height,byteBuffer);
    }


    public void WriteDngWithProfile(DngProfile profile)
    {
        if (profile == null)
            return;
        SetModelAndMake(Build.MANUFACTURER);
        if (profile.toneMapProfile != null)
        {
            if (profile.toneMapProfile.getToneCurve() != null)
                SetToneCurve(profile.toneMapProfile.getToneCurve(),byteBuffer);
            if (profile.toneMapProfile.getHueSatMapData1() != null)
                SetHueSatMapData1(profile.toneMapProfile.getHueSatMapData1(),byteBuffer);
            //SetHueSatMapData2(profile.toneMapProfile.getHueSatMapData2());
            if (profile.toneMapProfile.getHueSatMapDims() != null)
                SetHueSatMapDims(profile.toneMapProfile.getHueSatMapDims(),byteBuffer);
            if (profile.toneMapProfile.getBaselineExposure() != null)
                SetBaselineExposure(profile.toneMapProfile.getBaselineExposure(),byteBuffer);
        }

        SetBayerInfo(profile.matrixes.ColorMatrix1, profile.matrixes.ColorMatrix2, profile.matrixes.NeutralMatrix,
                profile.matrixes.ForwardMatrix1,profile.matrixes.ForwardMatrix2,
                profile.matrixes.ReductionMatrix1,profile.matrixes.ReductionMatrix2,profile.matrixes.NoiseReductionMatrix,profile.blacklevel, profile.whitelevel, profile.bayerPattern, profile.rowsize, profile.rawType,profile.widht,profile.height);
        WriteDNG(byteBuffer);
        recycle(byteBuffer);
        byteBuffer = null;
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
