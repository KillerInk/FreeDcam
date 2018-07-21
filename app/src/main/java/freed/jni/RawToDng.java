package freed.jni;

import android.os.Build;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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

    private OpCode opCode;

    private native ByteBuffer init();
    private native void recycle(ByteBuffer byteBuffer);
    private native long GetRawBytesSize(ByteBuffer byteBuffer);
    private native void SetGPSData(ByteBuffer byteBuffer, ByteBuffer gpsBuffer);
    private native void SetThumbData(byte[] mThumb, int widht, int height,ByteBuffer byteBuffer);
    private native void WriteDNG(ByteBuffer byteBuffer);
    private native void SetOpCode(ByteBuffer byteBuffer, ByteBuffer opcode);
    private native void SetModelAndMake(String model, String make,ByteBuffer byteBuffer);
    private native void SetBayerData(byte[] fileBytes, String fileout,ByteBuffer byteBuffer);
    private native void SetBayerDataFD(byte[] fileBytes, int fileout, String filename,ByteBuffer byteBuffer);
    private native void SetBayerInfo(ByteBuffer matrix, ByteBuffer dngprofile,ByteBuffer byteBuffer);
    private native void SetExifData(ByteBuffer exifInfo,ByteBuffer byteBuffer);

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

    public void setOpCode(OpCode opCode)
    {
        this.opCode = opCode;
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

    public void SetGpsData(ByteBuffer gpsBuffer)
    {
        if (byteBuffer != null)
            SetGPSData(byteBuffer, gpsBuffer);
    }

    public void setExifData(ExifInfo exifData)
    {
        SetExifData(exifData.getByteBuffer(),byteBuffer);
        SetDateTime(StorageFileManager.getStringExifPattern().format(new Date()),byteBuffer);
        //SetBaselineExposureOffset(exifData,byteBuffer);
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
        if (opCode != null)
            SetOpCode(byteBuffer,opCode.getByteBuffer());

    }

    public void SetBayerDataFD(byte[] fileBytes, ParcelFileDescriptor fileout, String filename) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }

        SetBayerDataFD(fileBytes, fileout.getFd(), filename,byteBuffer);
        if (opCode != null)
            SetOpCode(byteBuffer,opCode.getByteBuffer());
    }


    private void SetBayerInfo(ByteBuffer matrix, ByteBuffer dngprofile)
    {
        SetBayerInfo(matrix,dngprofile,byteBuffer);
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
        SetBayerInfo(profile.matrixes.getByteBuffer(),profile.getByteBuffer(),byteBuffer);
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
