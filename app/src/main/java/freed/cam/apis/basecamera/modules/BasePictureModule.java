package freed.cam.apis.basecamera.modules;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.troop.freedcam.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera2.modules.ImageHolder;
import freed.dng.DngProfile;
import freed.jni.RawToDng;
import freed.utils.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 09.12.2016.
 */



public class BasePictureModule extends ModuleAbstract implements ImageHolder.ImageSaveImp {

    private final String TAG = BasePictureModule.class.getSimpleName();
    protected ActivityInterface activityInterface;
    private RawToDng dngConverter;

    public BasePictureModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_picture);
        this.activityInterface = cameraUiWrapper.getActivityInterface();
        dngConverter = RawToDng.GetInstance();
        dngConverter.loadOpCode();
    }

    @Override
    public String ShortName() {
        return "Pic";
    }

    @Override
    public String LongName() {
        return "Picture";
    }

    //ModuleInterface START
    @Override
    public String ModuleName() {
        return name;
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

    @Override
    public void saveRawToDng(File fileName, byte[] bytes, float fnumber, float focal, float exposuretime, int iso, int orientation, String wb, DngProfile dngProfile, float expoindex)
    {
        Log.d(TAG,"saveDng");
        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;
        if (activityInterface.getAppSettings().getApiString(AppSettingsManager.SETTING_LOCATION).equals(activityInterface.getAppSettings().getResString(R.string.on_)))
        {
            if (activityInterface.getLocationHandler().getCurrentLocation() != null)
            {
                Location location = activityInterface.getLocationHandler().getCurrentLocation();
                Log.d(TAG, "location:" + location.toString());
                Altitude = location.getAltitude();
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
                Provider = location.getProvider();
                gpsTime = location.getTime();
                dngConverter.SetGpsData(Altitude, Latitude, Longitude, Provider, gpsTime);
            }
        }
        Log.d(TAG,"setExifData expoindex: "+expoindex);
        dngConverter.setExifData(iso, exposuretime, 0, fnumber, focal, "0", orientation + "", expoindex);
        if (wb != null)
            dngConverter.SetWBCT(wb);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !activityInterface.getAppSettings().GetWriteExternal())
        {
            //DngMatrixCalc dngMatrixCalc = new DngMatrixCalc();
            // dngMatrixCalc.CalcualteD65();

            Log.d(TAG, "Write To internal or kitkat<");
            checkFileExists(fileName);
            dngConverter.setBayerData(bytes, fileName.getAbsolutePath());
            dngConverter.WriteDngWithProfile(dngProfile);
        }
        else
        {
            DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
            Log.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/dng", fileName.getName().replace(".jpg", ".dng"));
            Log.d(TAG,"Filepath: " + wr.getUri());
            ParcelFileDescriptor pfd = null;
            try {
                pfd = activityInterface.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Log.WriteEx(e);
            }
            if (pfd != null)
            {
                dngConverter.SetBayerDataFD(bytes, pfd, fileName.getName());
                dngConverter.WriteDngWithProfile(dngProfile);
                try {
                    pfd.close();
                } catch (IOException e) {
                    Log.WriteEx(e);
                }
                pfd = null;
            }
        }
        activityInterface.ScanFile(fileName);
        bytes = null;
    }

    protected void saveBitmap(File file, Bitmap bitmap)
    {
        OutputStream outStream = null;
        boolean writetoExternalSD = activityInterface.getAppSettings().GetWriteExternal();
        Log.d(TAG, "Write External " + writetoExternalSD);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || !writetoExternalSD && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            try {
                outStream= new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        }
        else
        {
            DocumentFile df =  activityInterface.getFreeDcamDocumentFolder();
            Log.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/*", file.getName());
            Log.d(TAG,"Filepath: " + wr.getUri());
            try {
                outStream = activityInterface.getContext().getContentResolver().openOutputStream(wr.getUri());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        }
        activityInterface.ScanFile(file);
        bitmap = null;
        file = null;
    }

    @Override
    public void saveJpeg(File file, byte[] bytes)
    {
        Log.d(TAG, "Start Saving Bytes");
        OutputStream outStream = null;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !activityInterface.getAppSettings().GetWriteExternal())
            {
                checkFileExists(file);
                outStream = new FileOutputStream(file);
            }
            else
            {
                DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
                Log.d(TAG,"Filepath: " + df.getUri());
                DocumentFile wr = df.createFile("image/*", file.getName());
                Log.d(TAG,"Filepath: " + wr.getUri());
                outStream = activityInterface.getContext().getContentResolver().openOutputStream(wr.getUri());
            }
            outStream.write(bytes);
            outStream.flush();
            outStream.close();


        } catch (IOException e) {
            Log.WriteEx(e);
        }
        activityInterface.ScanFile(file);
        Log.d(TAG, "End Saving Bytes");
        bytes = null;
        file = null;
    }

    @Override
    public void internalFireOnWorkDone(File file) {

    }
}
