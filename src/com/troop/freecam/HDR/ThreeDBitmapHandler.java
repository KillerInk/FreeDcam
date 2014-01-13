package com.troop.freecam.HDR;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.jni.bitmap_operations.JniBitmapHolder;
import com.troop.freecam.utils.SavePictureTask;

import java.io.File;

/**
 * Created by troop on 16.11.13.
 */
public class ThreeDBitmapHandler extends BaseBitmapHandler
{
    public Uri[] LeftUris;
    public Uri[] RightUris;
    public File sdcardpath = Environment.getExternalStorageDirectory();
    public File freeCamImageDirectoryTmp = new File(sdcardpath.getAbsolutePath() + "/DCIM/FreeCam/Tmp/");
    HdrRenderActivity activity;

    public ThreeDBitmapHandler(Activity activity, Uri[] orginalUris)
    {
        super(activity, orginalUris);
        this.uris = orginalUris;
        this.activity = (HdrRenderActivity) activity;
        LeftUris = new Uri[3];
        RightUris = new Uri[3];
    }

    public Uri[] split3DImagesIntoLeftRight(Uri[] uris)
    {

        String end = "";
        if (uris[0].getPath().endsWith("jps"))
            end = "jps";
        else
            end = "jpg";

        for(int i=0; i < uris.length; i++ )
        {
            JniBitmapHolder orgi = new JniBitmapHolder(BitmapFactory.decodeFile(uris[i].getPath()));
            int newheigt = orgi.getWidth() /32 * 9;
            int tocrop = orgi.getHeight() - newheigt ;
            if (activity.preferences.getBoolean("upsidedown", false))
                orgi.rotateBitmap180();
            orgi.cropBitmap(0, tocrop / 2, orgi.getWidth(), tocrop / 2 + newheigt);
            orgi.cropBitmap(0, 0, orgi.getWidth() / 2, orgi.getHeight());
            File file = new File(String.format(freeCamImageDirectoryTmp + "/left" + String.valueOf(i) + "." + end));
            saveBitmap(file.getAbsolutePath(), orgi.getBitmapAndFree());
            LeftUris[i] = Uri.fromFile(file);

            orgi = new JniBitmapHolder(BitmapFactory.decodeFile(uris[i].getPath()));
            if (activity.preferences.getBoolean("upsidedown", false))
                orgi.rotateBitmap180();
            orgi.cropBitmap(0, tocrop / 2, orgi.getWidth(), tocrop / 2 + newheigt);
            orgi.cropBitmap(orgi.getWidth() / 2, 0, orgi.getWidth(), orgi.getHeight());
            File fileright = new File(String.format(freeCamImageDirectoryTmp + "/right" + String.valueOf(i) + "." + end));
            saveBitmap(fileright.getAbsolutePath(), orgi.getBitmapAndFree());
            RightUris[i] = Uri.fromFile(fileright);
        }
        return  LeftUris;
    }

    public String Render3d(BitmapHandler base, BitmapHandler first, BitmapHandler second, int width, int height)
    {
        super.cropPictures(base, first, second, width, height);
        File file = SavePictureTask.getFilePath("jps", sdcardpath);
        try
        {
            //base Image
            JniBitmapHolder baseJni = new JniBitmapHolder(Bitmap.createBitmap(base.Width * 2, base.Height, Bitmap.Config.ARGB_8888));
            //left
            JniBitmapHolder baseJniL = new JniBitmapHolder(BitmapFactory.decodeFile(LeftUris[1].getPath()));
            baseJniL.cropBitmap(base.X, base.Y, base.Width + base.X, base.Y + base.Height);
            baseJni.AddImageIntoExisting(baseJniL._handler, 0,0);
            baseJniL.freeBitmap();
            //right
            JniBitmapHolder baseJniR = new JniBitmapHolder(BitmapFactory.decodeFile(RightUris[1].getPath()));
            baseJniR.cropBitmap(base.X, base.Y, base.Width + base.X, base.Y + base.Height);
            baseJni.AddImageIntoExisting(baseJniR._handler, base.Width,0);
            baseJniR.freeBitmap();

            //High image
            JniBitmapHolder highJni = new JniBitmapHolder(Bitmap.createBitmap(first.Width * 2, first.Height, Bitmap.Config.ARGB_8888));
            //left
            JniBitmapHolder highJniL = new JniBitmapHolder(BitmapFactory.decodeFile(LeftUris[0].getPath()));
            highJniL.cropBitmap(first.X, first.Y, first.X + first.Width, first.Y + first.Height);
            highJni.AddImageIntoExisting(highJniL._handler, 0,0);
            highJniL.freeBitmap();
            //right
            JniBitmapHolder highJniR = new JniBitmapHolder(BitmapFactory.decodeFile(RightUris[0].getPath()));
            highJniR.cropBitmap(first.X, first.Y, first.X + first.Width, first.Y + first.Height);
            highJni.AddImageIntoExisting(highJniR._handler, first.Width,0);
            highJniR.freeBitmap();

            //low Image
            JniBitmapHolder lowJni = new JniBitmapHolder(Bitmap.createBitmap(second.Width * 2, second.Height, Bitmap.Config.ARGB_8888));
            //left
            JniBitmapHolder lowJniL  = new JniBitmapHolder(BitmapFactory.decodeFile(LeftUris[2].getPath()));
            lowJniL.cropBitmap(second.X, second.Y, second.X + second.Width, second.Y + second.Height);
            lowJni.AddImageIntoExisting(lowJniL._handler, 0,0);
            lowJniL.freeBitmap();
            //right
            JniBitmapHolder lowJniR  = new JniBitmapHolder(BitmapFactory.decodeFile(RightUris[2].getPath()));
            lowJniR.cropBitmap(second.X, second.Y, second.X + second.Width, second.Y + second.Height);
            lowJni.AddImageIntoExisting(lowJniR._handler, second.Width, 0);
            lowJniR.freeBitmap();

            //Render Images
            baseJni.ToneMapImages(highJni, lowJni);
            highJni.freeBitmap();
            lowJni.freeBitmap();

            saveBitmap(file.getAbsolutePath(), baseJni.getBitmapAndFree());
        }
        catch (OutOfMemoryError ex)
        {
            //Toast.makeText(this, "OutOFMEMORY SUCKS AS HELL", 10).show();

            ex.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
