package com.troop.freecam.HDR;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.troop.freecam.SavePictureTask;
import com.troop.freecam.cm.HdrSoftwareProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 16.11.13.
 */
public class ThreeDBitmapHandler extends BaseBitmapHandler
{
    //public Uri[] OrginalUris;
    public Uri[] LeftUris;
    public Uri[] RightUris;
    Uri[] urisLeftTop;
    Uri[] urisLeftBottom;
    Uri[] urisRightTop;
    Uri[] urisRightBottom;

    public File sdcardpath = Environment.getExternalStorageDirectory();
    public File freeCamImageDirectoryTmp = new File(sdcardpath.getAbsolutePath() + "/DCIM/FreeCam/Tmp/");

    BitmapHandler base;
    BitmapHandler first;
    BitmapHandler second;

    Activity activity;

    public ThreeDBitmapHandler(Activity activity, Uri[] orginalUris)
    {
        super(activity, orginalUris);
        this.uris = orginalUris;
        this.activity = activity;
        LeftUris = new Uri[3];
        RightUris = new Uri[3];
    }

    public Uri[] split3DImagesIntoLeftRight(Uri[] uris)
    {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uris[0].getPath(), op);
        String end = "";
        if (uris[0].getPath().endsWith("jps"))
            end = "jps";
        else
            end = "jpg";

        for(int i=0; i < uris.length; i++ )
        {
            croptTosixtenToNine(uris[i].getPath(), op.outWidth, op.outHeight);
            System.gc();
            Runtime.getRuntime().gc();
            System.gc();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(uris[i].getPath(), o);
            Bitmap left = Bitmap.createBitmap(BitmapFactory.decodeFile(uris[i].getPath()), 0, 0, o.outWidth / 2, o.outHeight);
            File file = new File(String.format(freeCamImageDirectoryTmp + "/left" + String.valueOf(i) + "." + end));
            saveBitmap(file.getAbsolutePath(), left);
            LeftUris[i] = Uri.fromFile(file);

            System.gc();
            Bitmap right = Bitmap.createBitmap(BitmapFactory.decodeFile(uris[i].getPath()), o.outWidth / 2, 0, o.outWidth/2, o.outHeight);
            File fileright = new File(String.format(freeCamImageDirectoryTmp + "/right" + String.valueOf(i) + "." + end));
            saveBitmap(fileright.getAbsolutePath(), right);
            RightUris[i] = Uri.fromFile(fileright);
        }
        return  LeftUris;
    }


    public  void CropImagesToNewSize(BitmapHandler base, BitmapHandler first, BitmapHandler second, int width, int height)
    {
        super.cropPictures(base, first, second, width, height);
        try
        {
            Bitmap newFirstPic = Bitmap.createBitmap(BitmapFactory.decodeFile(LeftUris[0].getPath()), first.X, first.Y, first.Width, first.Height);
            saveBitmap(LeftUris[0].getPath(), newFirstPic);
            Bitmap newSecondPic = Bitmap.createBitmap(BitmapFactory.decodeFile(LeftUris[2].getPath()), second.X, second.Y, second.Width, second.Height);
            saveBitmap(LeftUris[2].getPath(), newSecondPic);
            Bitmap newBaseImage = Bitmap.createBitmap(BitmapFactory.decodeFile(LeftUris[1].getPath()), base.X, base.Y, base.Width, base.Height);
            saveBitmap(LeftUris[1].getPath(), newBaseImage);
        }
        catch (OutOfMemoryError ex)
        {
            //Toast.makeText(this, "OutOFMEMORY SUCKS AS HELL", 10).show();

            ex.printStackTrace();
        }
        try
        {
            Bitmap newFirstPic = Bitmap.createBitmap(BitmapFactory.decodeFile(RightUris[0].getPath()), first.X, first.Y, first.Width, first.Height);
            saveBitmap(RightUris[0].getPath(), newFirstPic);
            Bitmap newSecondPic = Bitmap.createBitmap(BitmapFactory.decodeFile(RightUris[2].getPath()), second.X, second.Y, second.Width, second.Height);
            saveBitmap(RightUris[2].getPath(), newSecondPic);
            Bitmap newBaseImage = Bitmap.createBitmap(BitmapFactory.decodeFile(RightUris[1].getPath()), base.X, base.Y, base.Width, base.Height);
            saveBitmap(RightUris[1].getPath(), newBaseImage);
        }
        catch (OutOfMemoryError ex)
        {
            //Toast.makeText(this, "OutOFMEMORY SUCKS AS HELL", 10).show();

            ex.printStackTrace();
        }
    }

    public String Render3d()
    {
        urisLeftTop = new Uri[3];
        urisLeftBottom = new Uri[3];
        urisRightTop = new Uri[3];
        urisRightBottom = new Uri[3];

        splitLeftAndRightIntoTopAndBottom();
        renderSplittetPics();
        return mergeRenderedImages();
    }

    private void splitLeftAndRightIntoTopAndBottom()
    {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(LeftUris[0].getPath(), o);
        for(int i=0; i < LeftUris.length; i++ )
        {
            File filelefttop = new File(String.format(freeCamImageDirectoryTmp + "/lefttop" + String.valueOf(i) + ".jps"));
            Bitmap lefttop = Bitmap.createBitmap(BitmapFactory.decodeFile(LeftUris[i].getPath()), 0, 0 , o.outWidth, o.outHeight /2);
            saveBitmap(filelefttop.getAbsolutePath(), lefttop);
            urisLeftTop[i] = Uri.fromFile(filelefttop);

            File fileleftbottom = new File(String.format(freeCamImageDirectoryTmp + "/leftbottom" + String.valueOf(i) + ".jps"));
            Bitmap leftbottom = Bitmap.createBitmap(BitmapFactory.decodeFile(LeftUris[i].getPath()), 0, o.outHeight /2, o.outWidth, o.outHeight /2);
            saveBitmap(fileleftbottom.getAbsolutePath(), leftbottom);
            urisLeftBottom[i] = Uri.fromFile(fileleftbottom);

        }

        for(int i=0; i < RightUris.length; i++ )
        {
            File filerighttop = new File(String.format(freeCamImageDirectoryTmp + "/righttop" + String.valueOf(i) + ".jps"));
            Bitmap righttop = Bitmap.createBitmap(BitmapFactory.decodeFile(RightUris[i].getPath()), 0, 0 , o.outWidth, o.outHeight /2);
            saveBitmap(filerighttop.getAbsolutePath(), righttop);
            urisRightTop[i] = Uri.fromFile(filerighttop);

            File filerightbottom = new File(String.format(freeCamImageDirectoryTmp + "/rightbottom" + String.valueOf(i) + ".jps"));
            Bitmap rightbottom = Bitmap.createBitmap(BitmapFactory.decodeFile(RightUris[i].getPath()), 0, o.outHeight /2, o.outWidth, o.outHeight /2);
            saveBitmap(filerightbottom.getAbsolutePath(), rightbottom);
            urisRightBottom[i] = Uri.fromFile(filerightbottom);
        }
    }

    private void renderSplittetPics()
    {
        try {
            HdrSoftwareProcessor HdrRender = new HdrSoftwareProcessor(activity);
            HdrRender.prepare(activity,urisLeftTop);
            byte[] hdrpic = HdrRender.computeHDR(activity);


            saveFile(String.format(freeCamImageDirectoryTmp + "/lefttop.jps"), hdrpic);

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError ex)
        {
            ex.printStackTrace();
        }
        //left pic bottom
        try {
            HdrSoftwareProcessor HdrRender = new HdrSoftwareProcessor(activity);
            HdrRender.prepare(activity,urisLeftBottom);
            byte[] hdrpic = HdrRender.computeHDR(activity);

            saveFile(String.format(freeCamImageDirectoryTmp + "/leftbottom.jps"), hdrpic);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError ex)
        {
            ex.printStackTrace();
        }

        ///render right pic top
        try {
            HdrSoftwareProcessor HdrRender = new HdrSoftwareProcessor(activity);
            HdrRender.prepare(activity,urisRightTop);
            byte[] hdrpic = HdrRender.computeHDR(activity);

            saveFile(String.format(freeCamImageDirectoryTmp + "/righttop.jps"), hdrpic);
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError ex)
        {
            ex.printStackTrace();
        }

        ///render right pic bottom
        try {
            HdrSoftwareProcessor HdrRender = new HdrSoftwareProcessor(activity);
            HdrRender.prepare(activity,urisRightBottom);
            byte[] hdrpic = HdrRender.computeHDR(activity);

            saveFile(String.format(freeCamImageDirectoryTmp + "/rightbottom.jps"), hdrpic);
            HdrRender = null;
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError ex)
        {
            ex.printStackTrace();
        }
    }

    private String mergeRenderedImages()
    {
        Paint paint = new Paint();

        Bitmap left = BitmapFactory.decodeFile(String.format(freeCamImageDirectoryTmp + "/lefttop.jps"));
        Bitmap orgi = Bitmap.createBitmap(left.getWidth() * 2, left.getHeight()*2, left.getConfig());
        Canvas cav = new Canvas(orgi);
        cav.drawBitmap(left,0,0,paint);
        int width = orgi.getWidth();
        int height = orgi.getHeight();
        left.recycle();
        left =null;
        //gc();

        Bitmap leftbottom = BitmapFactory.decodeFile(String.format(freeCamImageDirectoryTmp + "/leftbottom.jps"));
        cav.drawBitmap(leftbottom, 0,orgi.getHeight()/2,paint);
        leftbottom.recycle();


        Bitmap rightTop = BitmapFactory.decodeFile(String.format(freeCamImageDirectoryTmp + "/righttop.jps"));
        cav.drawBitmap(rightTop, orgi.getWidth()/2, 0, paint);
        rightTop.recycle();

        Bitmap rightbottom = BitmapFactory.decodeFile(String.format(freeCamImageDirectoryTmp + "/rightbottom.jps"));
        cav.drawBitmap(rightbottom, orgi.getWidth()/2, orgi.getHeight()/2, paint);
        rightbottom.recycle();

        File file = SavePictureTask.getFilePath("jps", sdcardpath);

        //croptTosixtenToNine(orgi, width, height, file.getAbsolutePath());
        saveBitmap(file.getAbsolutePath(), orgi);

        orgi.recycle();
        orgi = null;

        //croptTosixtenToNine(file.getAbsolutePath(), width, height);
        return file.getAbsolutePath();
    }



    private void saveFile(String filepath, byte[] bytes)
    {
        File file = new File(filepath);
        FileOutputStream outStream = null;
        try {
            file.createNewFile();
            outStream = new FileOutputStream(file);
            outStream.write(bytes);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void croptTosixtenToNine(String path, int width, int height)
    {
        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("crop", false) == true)
        {
            int newheigt = width /32 * 9;
            int tocrop = height - newheigt ;

            System.gc();
            Runtime.getRuntime().gc();
            System.gc();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.gc();
            Runtime.getRuntime().gc();
            System.gc();

            //Bitmap bitmap =
            try {
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(path, true);
                saveBitmap(path, decoder.decodeRegion(new Rect(0, tocrop / 2, width, newheigt), null));
                decoder.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
