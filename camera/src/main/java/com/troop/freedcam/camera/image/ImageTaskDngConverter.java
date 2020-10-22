package com.troop.freedcam.camera.image;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.location.Location;
import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.camera.basecamera.modules.ModuleInterface;
import com.troop.freedcam.file.FileListController;
import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.image.ImageTask;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.Log;

import java.io.File;
import java.io.IOException;

import freed.ActivityInterface;


public class ImageTaskDngConverter extends ImageTask {

    private CaptureResult captureResult;
    private Image image;
    private CameraCharacteristics characteristics;
    private File file;
    private FileListController activityInterface;
    private int orientation;
    private Location location;
    private ModuleInterface moduleInterface;

    private final String TAG = ImageTaskDngConverter.class.getSimpleName();

    public ImageTaskDngConverter(CaptureResult captureResult, Image image, CameraCharacteristics characteristics, File file, FileListController activityInterface, int orientation, Location location, ModuleInterface moduleInterface)
    {
        this.captureResult = captureResult;
        this.image = image;
        this.characteristics =characteristics;
        this. file = file;
        this.activityInterface = activityInterface;
        this.orientation = orientation;
        this.location = location;
        this.moduleInterface = moduleInterface;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean process() {
        Log.d(TAG, "Create DNG");

        DngCreator dngCreator = new DngCreator(characteristics, captureResult);
        BaseHolder fileholder = null;
        //Orientation 90 is not a valid EXIF orientation value, android doc says that is valid!
        //The clockwise rotation angle in degrees, relative to the orientation to the camera, that the JPEG picture needs to be rotated by, to be viewed upright.
        try {
            dngCreator.setOrientation(orientation);
        }
        catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }

        if (location != null)
            dngCreator.setLocation(location);
        try
        {
            fileholder = activityInterface.getNewImgFileHolder(file, SettingsManager.getInstance().GetWriteExternal(),SettingsManager.getInstance().GetBaseFolder());
            dngCreator.writeImage(fileholder.getOutputStream(), image);
            dngCreator.close();
            image.close();
            if (fileholder != null)
                moduleInterface.internalFireOnWorkDone(fileholder);
            //activityInterface.ScanFile(file);
        } catch (IOException ex) {
            Log.WriteEx(ex);
        }
        finally {

            this.captureResult = null;
            this.image = null;
            this.characteristics =null;
            this. file = null;
            this.activityInterface = null;
            this.orientation = 0;
            this.location = null;
        }
        return false;
    }
}
