package freed.image;

import android.location.Location;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.dng.DngProfile;
import freed.jni.ExifInfo;
import freed.jni.GpsInfo;
import freed.jni.OpCode;
import freed.jni.RawToDng;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by KillerInk on 13.11.2017.
 */

public class ImageSaveTask extends ImageTask
{
    private final String TAG = ImageSaveTask.class.getSimpleName();

    public final static int JPEG = 0;
    public final static int RAW10 = 1;
    public final static int RAW12 = 2;
    public final static int RAW_SENSOR = 3;
    public final static int DUMP_RAWDATA = 4;


    private byte[] bytesTosave;
    private int imageFormat = JPEG;
    private DngProfile profile;
    private File filename;
    private boolean externalSD;
    private int orientation = 0;
    private Location location;
    private boolean forceRawToDng = false;

    private float fnum, focal = 0;
    private int mISO, cropWidth, cropHeight;
    private float exposureTime;
    private int flash = 0;
    private float expoindex;
    private String whitebalance;
    private ActivityInterface activityInterface;
    private ModuleInterface moduleInterface;

    private Thread currentThread;
    private OpCode opcode;
    private float baselineExposure = 0;
    private int greensplit = 0;


    public ImageSaveTask(ActivityInterface activityInterface, ModuleInterface moduleInterface)
    {
        this.activityInterface = activityInterface;
        this.moduleInterface = moduleInterface;
    }


    private void clear()
    {
        this.activityInterface = null;
        this.whitebalance = null;
        this.location =null;
        this.filename = null;
        this.profile = null;
        this.bytesTosave = null;
    }

    public void setBytesTosave(byte[] bytes, int imageFormat)
    {
        this.bytesTosave = bytes;
        this.imageFormat = imageFormat;
    }

    public void setDngProfile(DngProfile profile)
    {
        this.profile = profile;
    }

    public void setFilePath(File file, boolean externalSD)
    {
        this.filename = file;
        this.externalSD =externalSD;
    }

    public void setOrientation(int orientation)
    {
        this.orientation = orientation;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setForceRawToDng(boolean forceRawToDng)
    {
        this.forceRawToDng =forceRawToDng;
    }

    public void setFnum(float fnum)
    {
        this.fnum = fnum;
    }

    public void setFocal(float focal)
    {
        this.focal = focal;
    }

    public void setIso(int iso)
    {
        this.mISO = iso;
    }

    public void setExposureTime(float exposureTime)
    {
        this.exposureTime = exposureTime;
    }

    public void setFlash(int flash)
    {
        this.flash = flash;
    }

    public void setExposureIndex(float expoindex)
    {
        this.expoindex = expoindex;
    }

    public void setWhiteBalance(String wb)
    {
        this.whitebalance = wb;
    }

    public void setOpCode(OpCode opCode){
        this.opcode = opCode;
    }

    public void setBaselineExposure(float baselineExposure)
    {
        this.baselineExposure = baselineExposure;
    }

    public void setBayerGreenSplit(int greenSplit)
    {
        this.greensplit = greenSplit;
    }

    public void setCropSize(int cropWidth,int cropHeight)
    {
        this.cropHeight = cropHeight;
        this.cropWidth = cropWidth;
    }

    @Override
    public boolean process()
    {
        if(imageFormat == RAW10 || (imageFormat == RAW_SENSOR && forceRawToDng)){
            Log.d(TAG, "saveRawToDng");
            saveRawToDng();
            clear();
            return true;
        }
        else if (imageFormat == JPEG) {
            Log.d(TAG, "saveJpeg");
            saveJpeg();
            clear();
            return true;
        }
        else if (imageFormat == DUMP_RAWDATA){
            saveJpeg();
            clear();
        }
        Log.d(TAG,"Save done");
        return false;
    }

    @Override
    public Thread getThread() {
        return currentThread;
    }

    private void saveRawToDng()
    {
        RawToDng rawToDng = RawToDng.GetInstance();
        ParcelFileDescriptor pfd = null;
        int pfdint = -1;
        if (location != null)
        {
            GpsInfo gpsInfo = new GpsInfo(location);
            rawToDng.SetGpsData(gpsInfo.getByteBuffer());
        }
        ExifInfo info = new ExifInfo(mISO,flash,exposureTime,focal,fnum,expoindex,"",orientation+"");
        rawToDng.setExifData(info);
//        if (whitebalance != null)
//            rawToDng.SetWBCT(whitebalance);
        if (SettingsManager.getInstance().getOpCode() != null)
            rawToDng.setOpCode(SettingsManager.getInstance().getOpCode());
        else if (opcode != null)
            rawToDng.setOpCode(opcode);

        rawToDng.setBaselineExposure(baselineExposure);
        rawToDng.setBayerGreenSplit(greensplit);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !externalSD)
        {
            checkFileExists(filename);

        }
        else
        {
            DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
            Log.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/dng", filename.getName().replace(".jpg", ".dng"));
            Log.d(TAG,"Filepath: " + wr.getUri());

            try {
                pfd = activityInterface.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                pfdint =pfd.getFd();
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Log.WriteEx(e);
            }
        }
        if (pfd == null)
            rawToDng.setBayerData(bytesTosave,filename.getAbsolutePath());
        else
            rawToDng.SetBayerDataFD(bytesTosave,pfd,filename.getAbsolutePath());

        if (cropHeight >0 && cropWidth >0) {
            rawToDng.cropCenter(cropWidth, cropHeight);
        }

        rawToDng.WriteDngWithProfile(profile);
        if (pfd != null)
            try {
                pfd.close();
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        //rawToDng = null;
        //activityInterface.ScanFile(filename);
        moduleInterface.internalFireOnWorkDone(filename);
    }

    private void saveJpeg()
    {
        Log.d(TAG, "Start Saving Bytes");
        BufferedOutputStream outStream = null;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !externalSD)
            {
                checkFileExists(filename);
                outStream = new BufferedOutputStream(new FileOutputStream(filename));
            }
            else
            {
                DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
                Log.d(TAG,"Filepath: " + df.getUri());
                DocumentFile wr = df.createFile("image/*", filename.getName());
                Log.d(TAG,"Filepath: " + wr.getUri());
                outStream = new BufferedOutputStream(activityInterface.getContext().getContentResolver().openOutputStream(wr.getUri()));
            }
            outStream.write(bytesTosave);
            outStream.flush();
            outStream.close();


        } catch (IOException e) {
            Log.WriteEx(e);
        }
        //activityInterface.ScanFile(filename);
        moduleInterface.internalFireOnWorkDone(filename);
        Log.d(TAG, "End Saving Bytes");
    }


    protected void checkFileExists(File fileName)
    {
        if (fileName == null)
            return;
        if (fileName.getParentFile() == null)
            return;
        if(!fileName.getParentFile().exists())
            fileName.getParentFile().mkdirs();
        if (!fileName.exists())
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                Log.WriteEx(e);
            }
    }
}
