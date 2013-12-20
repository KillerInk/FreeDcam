package com.troop.freecam.HDR;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;
import com.jni.bitmap_operations.JniBitmapHolder;
import com.troop.freecam.SavePictureTask;
import com.troop.freecam.utils.BitmapUtils;
import java.io.File;


/**
 * Created by troop on 19.11.13.
 */
public class TwoDBitmapHandler extends BaseBitmapHandler
{
    JniBitmapHolder baseJni;
    JniBitmapHolder highJni;
    JniBitmapHolder lowJni;
    HdrRenderActivity activity;

    public TwoDBitmapHandler(Activity activity, Uri[] uris) {
        super(activity, uris);
        this.activity = (HdrRenderActivity) activity;
    }

    public String render2d(String end, File sdcardpath)
    {
        baseJni.ToneMapImages(highJni, lowJni);
        highJni.freeBitmap();
        lowJni.freeBitmap();

        Bitmap bitmap = baseJni.getBitmapAndFree();

        File file = SavePictureTask.getFilePath(end, sdcardpath);
        BitmapUtils.saveBitmapToFile(file, bitmap);
        return file.getAbsolutePath();
    }



    public void cropPictures(BitmapHandler base, BitmapHandler first, BitmapHandler second, int width, int height)
    {
        super.cropPictures(base, first, second, width, height);

        try
        {
            baseJni = new JniBitmapHolder(BitmapFactory.decodeFile(uris[1].getPath()));
            highJni = new JniBitmapHolder(BitmapFactory.decodeFile(uris[0].getPath()));
            lowJni  = new JniBitmapHolder(BitmapFactory.decodeFile(uris[2].getPath()));
            baseJni.cropBitmap(base.X, base.Y, base.Width + base.X, base.Y + base.Height);
            highJni.cropBitmap(first.X, first.Y, first.Width + first.X, first.Y + first.Height);
            lowJni.cropBitmap(second.X, second.Y, second.X + second.Width, second.Y + second.Height);
            if (activity.preferences.getBoolean("upsidedown", false))
            {
                baseJni.rotateBitmap180();
                highJni.rotateBitmap180();
                lowJni.rotateBitmap180();
            }
        }
        catch (OutOfMemoryError ex)
        {
            Toast.makeText(activity, "OutOFMEMORY SUCKS AS HELL", 10).show();

            ex.printStackTrace();
        }
    }
}
