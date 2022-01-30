package freed.jni;

import android.os.Build;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
    private ParcelFileDescriptor fileout;

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
    private native void SetBayerDataBufferFD(ByteBuffer fileBytes, int fileout, String filename,ByteBuffer byteBuffer);
    private native void SetBayerInfo(ByteBuffer matrix, ByteBuffer dngprofile,ByteBuffer byteBuffer);
    private native void SetExifData(ByteBuffer exifInfo,ByteBuffer byteBuffer);

    private native void SetDateTime(String datetime,ByteBuffer byteBuffer);

    private native void SetToneCurve(float tonecurve[],ByteBuffer byteBuffer);
    private native void SetHueSatMapData1(float tonecurve[],ByteBuffer byteBuffer);
    private native void SetHueSatMapData2(float tonecurve[],ByteBuffer byteBuffer);
    private native void SetHueSatMapDims(int[] dims,ByteBuffer byteBuffer);
    private native void SetBaselineExposure(float baselineexposure,ByteBuffer byteBuffer);
    private native void SetBaselineExposureOffset(float baselineexposureoffset,ByteBuffer byteBuffer);
    private native void setBayerGreenSplit(int greensplit,ByteBuffer byteBuffer);
    private native void setCropWidthHeight(int width,int height,ByteBuffer byteBuffer);

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
        this.fileout = fileout;
        SetBayerDataFD(fileBytes, fileout.detachFd(), filename,byteBuffer);
        if (opCode != null)
            SetOpCode(byteBuffer,opCode.getByteBuffer());
    }

    public void SetBayerDataBufFD(ByteBuffer fileBytes, ParcelFileDescriptor fileout, String filename) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }
        this.fileout = fileout;
        SetBayerDataBufferFD(fileBytes, fileout.detachFd(), filename,byteBuffer);
        if (opCode != null)
            SetOpCode(byteBuffer,opCode.getByteBuffer());
    }


    private void SetBayerInfo(ByteBuffer matrix, ByteBuffer dngprofile)
    {
        SetBayerInfo(matrix,dngprofile,byteBuffer);
    }

    public void setBaselineExposure(float baselineExposure)
    {
        if (byteBuffer == null)
            return;
        SetBaselineExposure(baselineExposure, byteBuffer);
    }

    public void setBaselineExposureOffset(float baselineExposure)
    {
        if (byteBuffer == null)
            return;
        SetBaselineExposureOffset(baselineExposure, byteBuffer);
    }

    public void setBayerGreenSplit(int greenSplit)
    {
        if (byteBuffer == null)
            return;
        setBayerGreenSplit(greenSplit,byteBuffer);
    }

    public void setCropWidthHeight(int width,int height)
    {
        if (byteBuffer == null)
            return;
        setCropWidthHeight(width,height,byteBuffer);
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
            if (profile.toneMapProfile.getHueSatMapData2() != null)
                SetHueSatMapData2(profile.toneMapProfile.getHueSatMapData2(),byteBuffer);
            if (profile.toneMapProfile.getHueSatMapDims() != null)
                SetHueSatMapDims(profile.toneMapProfile.getHueSatMapDims(),byteBuffer);
            if (profile.toneMapProfile.getBaselineExposure() != null)
                SetBaselineExposure(profile.toneMapProfile.getBaselineExposure(),byteBuffer);
        }
        SetBayerInfo(profile.matrixes.getByteBuffer(),profile.getByteBuffer(),byteBuffer);
        WriteDNG(byteBuffer);
        recycle(byteBuffer);
        byteBuffer = null;
        if (fileout != null) {
            try {
                fileout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public static MappedByteBuffer readFileToMemoryMap(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "rw");
        try {
           return f.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, f.length());
        } finally {
            f.close();
        }
    }
}
