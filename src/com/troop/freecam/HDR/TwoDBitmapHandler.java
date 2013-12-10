package com.troop.freecam.HDR;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import com.troop.freecam.SavePictureTask;
import com.troop.freecam.cm.HdrSoftwareProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 19.11.13.
 */
public class TwoDBitmapHandler extends BaseBitmapHandler
{
    public TwoDBitmapHandler(Activity activity, Uri[] uris) {
        super(activity, uris);
    }

    public String render2d(String end, File sdcardpath)
    {
        HdrSoftwareProcessor HdrRender = null;
        try {
            HdrRender = new HdrSoftwareProcessor(activity);
            HdrRender.prepare(activity, uris);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] hdrpic = HdrRender.computeHDR(activity);
        File file = SavePictureTask.getFilePath(end, sdcardpath);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            outStream.write(hdrpic);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }



    public void cropPictures(BitmapHandler base, BitmapHandler first, BitmapHandler second, int width, int height)
    {
        super.cropPictures(base, first, second, width, height);

        try
        {
            Bitmap newFirstPic = Bitmap.createBitmap(BitmapFactory.decodeFile(uris[0].getPath()), first.X, first.Y, first.Width, first.Height);
            saveBitmap(uris[0].getPath(), newFirstPic);
            Bitmap newSecondPic = Bitmap.createBitmap(BitmapFactory.decodeFile(uris[2].getPath()), second.X, second.Y, second.Width, second.Height);
            saveBitmap(uris[2].getPath(), newSecondPic);
            Bitmap newBaseImage = Bitmap.createBitmap(BitmapFactory.decodeFile(uris[1].getPath()), base.X, base.Y, base.Width, base.Height);
            saveBitmap(uris[1].getPath(), newBaseImage);
        }
        catch (OutOfMemoryError ex)
        {
            Toast.makeText(activity, "OutOFMEMORY SUCKS AS HELL", 10).show();

            ex.printStackTrace();
        }
    }
}
