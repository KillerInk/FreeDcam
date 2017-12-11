package freed.image;

import android.location.Location;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.dng.DngProfile;
import freed.jni.RawToDng;
import freed.utils.Log;
import freed.utils.StorageFileManager;

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


    private byte[] bytesTosave, opcode2, opcode3;
    private int imageFormat = JPEG;
    private DngProfile profile;
    private File filename;
    private boolean externalSD;
    private int orientation = 0;
    private Location location;
    private boolean forceRawToDng = false;

    private float fnum, focal = 0;
    private int mISO;
    private float exposureTime;
    private float expoindex;
    private String whitebalance;
    private ActivityInterface activityInterface;
    private ModuleInterface moduleInterface;

    private Thread currentThread;


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
        this.opcode2 = null;
        this.opcode3 = null;
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

    public void setExposureIndex(float expoindex)
    {
        this.expoindex = expoindex;
    }

    public void setWhiteBalance(String wb)
    {
        this.whitebalance = wb;
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
        return false;
    }

    @Override
    public Thread getThread() {
        return currentThread;
    }

    private void saveRawToDng()
    {
        double Altitude = 0;
        float[] Latitude = null;
        float[] Longitude = null;
        String gpsProvider = null;
        long gpsTime = 0;
        ParcelFileDescriptor pfd = null;
        int pfdint = -1;
        if (location != null)
        {
            Altitude = location.getAltitude();
            Latitude = RawToDng.parseGpsvalue(location.getLatitude());
            Longitude = RawToDng.parseGpsvalue(location.getLongitude());
            gpsProvider = location.getProvider();
            gpsTime = location.getTime();
            //rawToDng.SetGpsData(Altitude, Latitude, Longitude, Provider, gpsTime);
        }
        //rawToDng.setExifData(mISO, exposureTime, 0, fnum, focal, "0", orientation + "", expoindex);
//        if (whitebalance != null)
//            rawToDng.SetWBCT(whitebalance);

       /* rawToDng.setOpcode2(opcode2);
        rawToDng.setOpcode3(opcode3);*/

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
        float tonemap[] = null;
        float huesatmapdata1[] = null;
        int huesatmapdim[] = null;
        float baselineExpo =0;
        if (profile.toneMapProfile != null)
        {
            tonemap = profile.toneMapProfile.getToneCurve();
            huesatmapdim = profile.toneMapProfile.getHueSatMapDims();
            huesatmapdata1 = profile.toneMapProfile.getHueSatMapData1();
            try {
                baselineExpo = profile.toneMapProfile.getBaselineExposure();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        RawToDng.overloadWrite(mISO,exposureTime,0,fnum,focal,"0",orientation+"",expoindex,
                Altitude,Latitude,Longitude,gpsProvider,gpsTime,
                null,0,0,bytesTosave, filename.getAbsolutePath(),pfdint,Build.MODEL, Build.MANUFACTURER,opcode2,opcode3,
                profile.matrixes.ColorMatrix1,
                profile.matrixes.ColorMatrix2,
                profile.matrixes.NeutralMatrix,
                profile.matrixes.ForwardMatrix1,
                profile.matrixes.ForwardMatrix2,
                profile.matrixes.ReductionMatrix1,
                profile.matrixes.ReductionMatrix2,
                profile.matrixes.NoiseReductionMatrix,
                profile.blacklevel, profile.whitelevel, profile.bayerPattern,profile.rowsize,profile.rawType,profile.widht,profile.height,
                StorageFileManager.getStringExifPattern().format(new Date()),
                tonemap,
                huesatmapdim,
                huesatmapdata1,
                null,baselineExpo,expoindex);
        if (pfd != null)
            try {
                pfd.close();
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        //rawToDng = null;
        activityInterface.ScanFile(filename);
        moduleInterface.internalFireOnWorkDone(filename);
    }

    private void saveJpeg()
    {
        Log.d(TAG, "Start Saving Bytes");
        OutputStream outStream = null;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !externalSD)
            {
                checkFileExists(filename);
                outStream = new FileOutputStream(filename);
            }
            else
            {
                DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
                Log.d(TAG,"Filepath: " + df.getUri());
                DocumentFile wr = df.createFile("image/*", filename.getName());
                Log.d(TAG,"Filepath: " + wr.getUri());
                outStream = activityInterface.getContext().getContentResolver().openOutputStream(wr.getUri());
            }
            outStream.write(bytesTosave);
            outStream.flush();
            outStream.close();


        } catch (IOException e) {
            Log.WriteEx(e);
        }
        activityInterface.ScanFile(filename);
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
