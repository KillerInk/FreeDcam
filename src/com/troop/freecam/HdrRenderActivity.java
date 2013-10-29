package com.troop.freecam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.troop.freecam.cm.HdrSoftwareProcessor;
import com.troop.freecam.cm.HdrSoftwareRS;
import com.troop.freecam.manager.Drawing.OverlayView;

/**
 * Created by troop on 18.10.13.
 */
public class HdrRenderActivity extends Activity
{

    public final String TAG = "HDRActivity";
    private Uri[] uris;
    Uri[] urisLeftTop = new Uri[3];
    Uri[] urisLeftBottom = new Uri[3];
    Uri[] urisRightTop = new Uri[3];
    Uri[] urisRightBottom = new Uri[3];
    Uri[] urisLeft = new Uri[3];
    Uri[] urisRight = new Uri[3];
    HdrSoftwareProcessor HdrRender;

    Button button_renderHDR;
    OverlayView overlayView;
    Button button_moveleft;
    Button button_moveright;
    Button button_movetop;
    Button button_movebottom;
    CheckBox picone;
    CheckBox pictwo;
    public  ViewGroup appViewGroup;


    RelativeLayout picView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //appViewGroup = (ViewGroup) inflater.inflate(R.layout.hdr_layout, null);

            setContentView(R.layout.hdr_layout);
            Bundle extras = getIntent().getExtras();
            String[] muh = new String[3];
            muh = extras.getStringArray("uris");
            uris = new Uri[3];
            uris[0] = Uri.fromFile(new File(muh[0]));
            uris[1] = Uri.fromFile(new File(muh[1]));
            uris[2] = Uri.fromFile(new File(muh[2]));
            HdrRender = new HdrSoftwareProcessor(this);
            initControls();



            //basePicture.setBackgroundDrawable(draw);

            //basePicture.invalidate();

        }
    }

    private void initControls() {
        button_renderHDR = (Button)findViewById(R.id.button_RenderHdr);
        button_renderHDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String end = "";
                if (uris[0].getPath().endsWith("jps"))
                    end = "jps";
                else
                    end = "jpg";
                File sdcardpath = Environment.getExternalStorageDirectory();
                overlayView.Destroy();
                System.gc();
                renderHDRandSAve(end, sdcardpath);
            }
        });

        picView = (RelativeLayout)findViewById(R.id.LayoutPics);
        overlayView = (OverlayView) findViewById(R.id.view_overlay);

        //basePicture = (ImageView) findViewById(R.id.imageView_basePic);
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;


        button_moveleft = (Button)findViewById(R.id.button_left);
        button_moveleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picone.isChecked())
                    overlayView.AddLeft(true, -1);
                else
                    overlayView.AddLeft(false, -1);
            }
        });
        button_moveright = (Button)findViewById(R.id.button_right);
        button_moveright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (picone.isChecked())
                    overlayView.AddLeft(true, 1);
                else
                    overlayView.AddLeft(false, 1);

            }
        });
        button_movetop = (Button)findViewById(R.id.button_top);
        button_movetop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picone.isChecked())
                    overlayView.AddTop(true, 1);
                else
                    overlayView.AddTop(false, 1);
            }
        });
        button_movebottom = (Button)findViewById(R.id.button_bottom);
        button_movebottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picone.isChecked())
                    overlayView.AddTop(true, -1);
                else
                    overlayView.AddTop(false, -1);
            }
        });

        picone = (CheckBox) findViewById(R.id.checkBox_picFirst);
        picone.setChecked(true);
        picone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picone.isChecked()) {
                    picone.setChecked(false);
                    pictwo.setChecked(true);
                    overlayView.drawFirstPic = true;
                    overlayView.invalidate();
                } else {
                    picone.setChecked(true);
                    pictwo.setChecked(false);
                    overlayView.drawFirstPic = false;
                    overlayView.invalidate();
                }
            }
        });
        pictwo = (CheckBox)findViewById(R.id.checkBox_picSecond);
        pictwo.setChecked(false);
        pictwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pictwo.isChecked())
                {
                    picone.setChecked(true);
                    pictwo.setChecked(false);
                    overlayView.drawFirstPic = true;
                    overlayView.invalidate();
                }
                else
                {
                    picone.setChecked(false);
                    pictwo.setChecked(true);
                    overlayView.drawFirstPic = false;
                    overlayView.invalidate();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        overlayView.Load(uris);
        overlayView.drawFirstPic = true;

    }

    @Override
    protected void onPause()
    {
        overlayView.Destroy();
        super.onPause();

    }




    private void renderHDRandSAve(String end, File sdcardpath)
    {

        String path = "";
        if(end.equals("jps"))
        {
            path = render3d(end,sdcardpath);
        }
        else
        {
            cropPictures();
            path = render2d(end, sdcardpath);
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",path);
        setResult(RESULT_OK,returnIntent);
        finish();

    }

    private void cropPictures()
    {
        //create the current Picture Overlay Rectangles
        Rect firstimage = new Rect(
                overlayView.completviewRectangle.left + overlayView.leftMargineFirstPic *2,
                overlayView.completviewRectangle.top + overlayView.topMargineFirstPic *2,
                overlayView.completviewRectangle.right * 2 + overlayView.leftMargineFirstPic*2,
                overlayView.completviewRectangle.bottom * 2 + overlayView.topMargineFirstPic *2);
        Rect secondimage = new Rect(
                overlayView.completviewRectangle.left + overlayView.leftMargineSecondPic*2,
                overlayView.completviewRectangle.top + overlayView.topMargineSecondPic*2,
                overlayView.completviewRectangle.right * 2 + overlayView.leftMargineSecondPic*2,
                overlayView.completviewRectangle.bottom * 2 + overlayView.topMargineSecondPic*2);
        //calculate new top
        int right = overlayView.completviewRectangle.right * 2;
        if (firstimage.left < 0 || secondimage.left < 0)
        {
            if (firstimage.left < 0 && firstimage.left < secondimage.left)
            {
                if ((right + firstimage.left) < (overlayView.completviewRectangle.right * 2))
                right += firstimage.left;
            }
            if (secondimage.left < 0 && secondimage.left < firstimage.left)
            {
                if ((right + secondimage.left) < (overlayView.completviewRectangle.right * 2))
                right += secondimage.left;
            }
        }
        

        int left = 0;
        if (firstimage.left > 0 || secondimage.left > 0)
        {
            if (firstimage.left > 0 && firstimage.left > secondimage.left)
                left = firstimage.left;
            if (secondimage.left > 0 && secondimage.left > firstimage.left)
                left = secondimage.left;
        }
        int top = 0;
        if (firstimage.top > 0 || secondimage.top > 0)
        {
            if (firstimage.top > secondimage.top)
                top = firstimage.top;
            if (secondimage.top > firstimage.top)
                top = secondimage.top;
        }
        int bottom = overlayView.completviewRectangle.bottom * 2;
        if (firstimage.top < 0 || secondimage.top < 0)
        {
            if (firstimage.top < secondimage.top)
                if ((bottom + firstimage.top) < (overlayView.completviewRectangle.right * 2))
                    bottom += firstimage.top;
            if (secondimage.top < firstimage.top)
                if ((bottom + secondimage.top) < (overlayView.completviewRectangle.right * 2))
                    bottom += secondimage.top;
        }
        Rect newImageSize = new Rect(left, top, right,bottom);
        if (overlayView.leftMargineFirstPic < 0)
            overlayView.leftMargineFirstPic = 0;
        if (overlayView.topMargineFirstPic < 0)
            overlayView.topMargineFirstPic = 0;
        if (overlayView.leftMargineSecondPic < 0)
            overlayView.leftMargineSecondPic = 0;
        if (overlayView.topMargineSecondPic < 0)
            overlayView.topMargineSecondPic = 0;
        System.gc();
        Runtime.getRuntime().gc();
        System.gc();
        Bitmap newFirstPic = Bitmap.createBitmap(BitmapFactory.decodeFile(uris[0].getPath()), overlayView.leftMargineFirstPic, overlayView.topMargineFirstPic, newImageSize.width(), newImageSize.height());
        saveBitmap(uris[0].getPath(), newFirstPic);
        Bitmap newSecondPic = Bitmap.createBitmap(BitmapFactory.decodeFile(uris[2].getPath()), overlayView.leftMargineSecondPic, overlayView.topMargineSecondPic, newImageSize.width(), newImageSize.height());
        saveBitmap(uris[2].getPath(), newSecondPic);
        Bitmap newBaseImage = Bitmap.createBitmap(BitmapFactory.decodeFile(uris[1].getPath()), 0, 0, newImageSize.width(), newImageSize.height());
        saveBitmap(uris[1].getPath(), newBaseImage);

    }

    private String render2d(String end, File sdcardpath) {
        try {
            HdrRender = new HdrSoftwareProcessor(this);
            HdrRender.prepare(this, uris);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] hdrpic = HdrRender.computeHDR(this);
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

    private String render3d(String end, File sdcardpath)
    {
        urisLeftTop = new Uri[3];
        urisLeftBottom = new Uri[3];
        urisRightTop = new Uri[3];
        urisRightBottom = new Uri[3];
        urisLeft = new Uri[3];
        urisRight = new Uri[3];
        File freeCamImageDirectory = new File(sdcardpath.getAbsolutePath() + "/DCIM/FreeCam/Tmp/");

        Log.d(TAG, "Start splitting images");
        Bitmap orgi = null;
        //split pictues
        splitBitmaps(end, freeCamImageDirectory, orgi);
        ///render left/right pic
        renderSplittetHDRPics(end, freeCamImageDirectory);
        ///merge pics
        gc();
        Paint paint = new Paint();

        Bitmap left = BitmapFactory.decodeFile(String.format(freeCamImageDirectory + "/lefttop." + end));
        orgi = Bitmap.createBitmap(left.getWidth() * 2, left.getHeight()*2, Bitmap.Config.ARGB_8888);
        Canvas cav = new Canvas(orgi);
        cav.drawBitmap(left,0,0,paint);
        left.recycle();
        left =null;
        gc();

        Bitmap leftbottom = BitmapFactory.decodeFile(String.format(freeCamImageDirectory + "/leftbottom." + end));
        cav.drawBitmap(leftbottom, 0,orgi.getHeight()/2,paint);
        leftbottom.recycle();
        gc();

        Bitmap rightTop = BitmapFactory.decodeFile(String.format(freeCamImageDirectory + "/righttop." + end));
        cav.drawBitmap(rightTop, orgi.getWidth()/2, 0, paint);
        rightTop.recycle();
        gc();

        Bitmap rightbottom = BitmapFactory.decodeFile(String.format(freeCamImageDirectory + "/rightbottom." + end));
        cav.drawBitmap(rightbottom, orgi.getWidth()/2, orgi.getHeight()/2, paint);
        rightbottom.recycle();
        gc();

        File file = SavePictureTask.getFilePath("jps", sdcardpath);
        saveBitmap(file.getAbsolutePath(), orgi);

        orgi.recycle();
        gc();
        return file.getAbsolutePath();
    }

    private void renderSplittetHDRPics(String end,File freeCamImageDirectory)
    {
        //left pic top
        try {
            HdrRender = new HdrSoftwareProcessor(this);
            HdrRender.prepare(this,urisLeftTop);
            byte[] hdrpic = HdrRender.computeHDR(this);


            saveFile(String.format(freeCamImageDirectory + "/lefttop." + end), hdrpic);
            gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError ex)
        {
            ex.printStackTrace();
        }
        //left pic bottom
        try {
            HdrRender = new HdrSoftwareProcessor(this);
            HdrRender.prepare(this,urisLeftBottom);
            byte[] hdrpic = HdrRender.computeHDR(this);

            saveFile(String.format(freeCamImageDirectory + "/leftbottom." + end), hdrpic);
            gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError ex)
        {
            ex.printStackTrace();
        }

        ///render right pic top
        try {
            HdrRender = new HdrSoftwareProcessor(this);
            HdrRender.prepare(this,urisRightTop);
            byte[] hdrpic = HdrRender.computeHDR(this);

            saveFile(String.format(freeCamImageDirectory + "/righttop." + end), hdrpic);
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
            HdrRender = new HdrSoftwareProcessor(this);
            HdrRender.prepare(this,urisRightBottom);
            byte[] hdrpic = HdrRender.computeHDR(this);

            saveFile(String.format(freeCamImageDirectory + "/rightbottom." + end), hdrpic);
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError ex)
        {
            ex.printStackTrace();
        }
    }

    private void splitBitmaps(String end, File freeCamImageDirectory, Bitmap orgi) {
        for(int i=0; i < uris.length; i++ )
        {

            try {
                /// LEFT TOP IMAGE
                Log.d(TAG, "Split " + i + " Image");
                gc();
                File file = new File(String.format(freeCamImageDirectory + "/lefttop" + String.valueOf(i) + "." + end));
                urisLeftTop[i] = Uri.fromFile(file);
                file.createNewFile();

                File orginalImageFile = new File(uris[i].getPath());
                byte[] bytes = loadBytesFromFile(orginalImageFile);

                orgi = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Log.d(TAG, "Orginal Image Top " + i + " Size :" + orgi.getWidth() + "x"+ orgi.getHeight());
                bytes = null;
                gc();
                Bitmap lefttop = Bitmap.createBitmap(orgi,0,0, orgi.getWidth()/2, orgi.getHeight()/2);
                saveBitmap(file.getAbsolutePath(), lefttop);
                gc();
                //LEFT BOTTOM IMAGE
                File fileb = new File(String.format(freeCamImageDirectory + "/leftbottom" + String.valueOf(i) + "." + end));
                urisLeftBottom[i] = Uri.fromFile(fileb);
                fileb.createNewFile();
                Bitmap leftbottom = Bitmap.createBitmap(orgi, 0, orgi.getHeight() / 2, orgi.getWidth() / 2, orgi.getHeight() / 2);
                Log.d(TAG, "Left Image Bottom " + i + " Size :" + lefttop.getWidth() + "x"+ lefttop.getHeight());
                saveBitmap(fileb.getAbsolutePath(), leftbottom);
                gc();
                //RIGHT TOP IMAGE
                Bitmap right = Bitmap.createBitmap(orgi,orgi.getWidth()/2, 0, orgi.getWidth()/2, orgi.getHeight()/2);
                Log.d(TAG, "Right Image Top " + i + " Size :" + right.getWidth() + "x"+ right.getHeight());
                File fileright = new File(String.format(freeCamImageDirectory + "/righttop" + String.valueOf(i) + "." + end));
                fileright.createNewFile();
                urisRightTop[i] = Uri.fromFile(fileright);
                saveBitmap(fileright.getAbsolutePath(), right);
                gc();
                Bitmap rightbottom = Bitmap.createBitmap(orgi,orgi.getWidth()/2, orgi.getHeight()/2, orgi.getWidth()/2, orgi.getHeight()/2);
                Log.d(TAG, "Right Image Bottom " + i + " Size :" + right.getWidth() + "x"+ right.getHeight());
                File filerightb = new File(String.format(freeCamImageDirectory + "/rightbottom" + String.valueOf(i) + "." + end));
                fileright.createNewFile();
                urisRightBottom[i] = Uri.fromFile(filerightb);
                saveBitmap(filerightb.getAbsolutePath(), rightbottom);


                orgi.recycle();
                orgi = null;
                gc();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Log.d(TAG, "Splitting Images done!");
    }

    private void gc() {
        /*System.gc();
        Runtime.getRuntime().gc();
        System.gc();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
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

    private void saveBitmap(String filepath, Bitmap bitmap)
    {
        File file = new File(filepath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
        try {
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        bitmap =null;
    }

    private byte[] loadBytesFromFile(File file)
    {
        FileInputStream is =null;
        ByteArrayOutputStream bos = null;
        byte[] bytes = null;
        try {
            is = new FileInputStream(file);

            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead = 0;
            while (
                    (bytesRead = is.read(b)) != -1
                    )
            {
                bos.write(b, 0, bytesRead);
            }
            bytes = bos.toByteArray();
            is.close();
            bos.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return  bytes;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        overlayView.invalidate();
    }
}
