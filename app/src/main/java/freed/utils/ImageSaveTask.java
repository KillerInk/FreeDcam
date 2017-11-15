package freed.utils;

import android.location.Location;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.troop.freedcam.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;
import freed.jni.RawToDng;

/**
 * Created by KillerInk on 13.11.2017.
 */

public class ImageSaveTask implements SaveTask
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
    private RawToDng rawToDng;

    private float fnum, focal = 0;
    private int mISO;
    private float exposureTime;
    private float expoindex;
    private String whitebalance;
    private ActivityInterface activityInterface;
    private ModuleInterface moduleInterface;


    public ImageSaveTask(ActivityInterface activityInterface, ModuleInterface moduleInterface)
    {
        this.activityInterface = activityInterface;
        this.moduleInterface = moduleInterface;
    }


    private void clear()
    {
        this.activityInterface = null;
        this.whitebalance = null;
        this.rawToDng = null;
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

    public void setDngConverter(RawToDng rawToDng)
    {
        this.rawToDng = rawToDng;
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
    public boolean save()
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

    private void saveRawToDng()
    {
        if (location != null)
        {
            double Altitude = location.getAltitude();
            double Latitude = location.getLatitude();
            double Longitude = location.getLongitude();
            String Provider = location.getProvider();
            long gpsTime = location.getTime();
            rawToDng.SetGpsData(Altitude, Latitude, Longitude, Provider, gpsTime);
        }
        rawToDng.setExifData(mISO, exposureTime, 0, fnum, focal, "0", orientation + "", expoindex);
        if (whitebalance != null)
            rawToDng.SetWBCT(whitebalance);

       /* rawToDng.setOpcode2(opcode2);
        rawToDng.setOpcode3(opcode3);*/

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !externalSD)
        {
            checkFileExists(filename);
            rawToDng.setBayerData(bytesTosave, filename.getAbsolutePath());
            rawToDng.WriteDngWithProfile(profile);
        }
        else
        {
            DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
            Log.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/dng", filename.getName().replace(".jpg", ".dng"));
            Log.d(TAG,"Filepath: " + wr.getUri());
            ParcelFileDescriptor pfd = null;
            try {
                pfd = activityInterface.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Log.WriteEx(e);
            }
            if (pfd != null)
            {
                rawToDng.SetBayerDataFD(bytesTosave, pfd, filename.getName());
                rawToDng.WriteDngWithProfile(profile);
                try {
                    pfd.close();
                } catch (IOException e) {
                    Log.WriteEx(e);
                }
                pfd = null;
            }
        }
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
