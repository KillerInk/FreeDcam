package com.troop.freecam.manager;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by troop on 19.11.13.
 */
public class ExifManager
{
    String ImageLength;
    String Orientation;
    String ImageWidth;
    String ISOSpeedRatings;
    String MeteringMode;
    String WhiteBalance;
    String FocalLength;
    String Flash;
    String LightSource;
    String DateTime;
    String ExposureTime;
    String FNumber;
    String DigitalZoomRatio;
    String Make;
    String Model;

    public String getExposureTime(){ return ExposureTime;}

    public void LoadExifFrom(String path) throws IOException {
        ExifInterface loader = new ExifInterface(path);

        ImageLength = loader.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        Orientation = loader.getAttribute(ExifInterface.TAG_ORIENTATION);
        ImageWidth = loader.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        ISOSpeedRatings = loader.getAttribute(ExifInterface.TAG_ISO);
        MeteringMode = loader.getAttribute("MeteringMode");
        WhiteBalance = loader.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
        FocalLength = loader.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        Flash = loader.getAttribute(ExifInterface.TAG_FLASH);
        LightSource = loader.getAttribute("LightSource");
        DateTime = loader.getAttribute(ExifInterface.TAG_DATETIME);
        ExposureTime = loader.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        FNumber = loader.getAttribute("FNumber");
        DigitalZoomRatio = loader.getAttribute("DigitalZoomRatio");
        Make = loader.getAttribute("Make");
        Model = loader.getAttribute("Model");


    }

    public void SaveExifTo(String path) throws IOException {
        ExifInterface saver = new ExifInterface(path);
        //saver.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, ImageLength);
        //saver.setAttribute(ExifInterface.TAG_ORIENTATION, Orientation);
        //saver.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, ImageWidth);
        saver.setAttribute(ExifInterface.TAG_ISO, ISOSpeedRatings);
        saver.setAttribute("MeteringMode", MeteringMode);
        saver.setAttribute(ExifInterface.TAG_WHITE_BALANCE, WhiteBalance);
        saver.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, FocalLength);
        saver.setAttribute(ExifInterface.TAG_FLASH, Flash);
        saver.setAttribute("LightSource", LightSource);
        saver.setAttribute(ExifInterface.TAG_DATETIME, DateTime);
        saver.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, ExposureTime);
        saver.setAttribute("FNumber", FNumber);
        saver.setAttribute("DigitalZoomRatio", DigitalZoomRatio);
        saver.setAttribute("Make", Make);
        saver.setAttribute("Model", Model);
        saver.saveAttributes();
    }
}
