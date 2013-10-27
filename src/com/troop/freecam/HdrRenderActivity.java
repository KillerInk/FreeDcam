package com.troop.freecam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    ImageView basePicture;
    ImageView firstPic;
    ImageView secondPic;
    Button button_moveleft;
    Button button_moveright;
    Button button_movetop;
    Button button_movebottom;
    CheckBox picone;
    CheckBox pictwo;
    public  ViewGroup appViewGroup;

    RelativeLayout picView;

    boolean moving = false;
    int moveX;
    int moveY;
    int leftmargine;
    int topmargine;

    Rect currentviewRectangle;
    Rect completviewRectangle;

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
                basePicture.setImageBitmap(null);
                firstPic.setImageBitmap(null);
                secondPic.setImageBitmap(null);
                System.gc();
                renderHDRandSAve(end, sdcardpath);
            }
        });

        picView = (RelativeLayout)findViewById(R.id.LayoutPics);

        basePicture = (ImageView) findViewById(R.id.imageView_basePic);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap orginalImage = BitmapFactory.decodeFile(uris[1].getPath(), options);
        leftmargine = (options.outWidth - 800) /2;
        topmargine = (options.outHeight - 480) /2;
        completviewRectangle = new Rect(0,0, options.outWidth, options.outHeight);


        basePicture.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeFile(uris[1].getPath()), leftmargine, topmargine,800, 480));

        basePicture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if (event.getAction() == MotionEvent.ACTION_DOWN && moving == false)
                {
                    moveX = (int)event.getX();
                    moveY = (int)event.getY();
                    moving = true;
                }
                if (moving)
                {
                    int lastmovex = moveX - (int)event.getX();
                    int lastmovey = moveY - (int)event.getY();
                    moveX = (int)event.getX();
                    moveY = (int)event.getY();
                    if (leftmargine + lastmovex >= 0 && leftmargine + 800 + lastmovex <= completviewRectangle.right)
                        leftmargine = leftmargine + lastmovex;
                    if(topmargine + lastmovey >= 0 && topmargine + 480 + lastmovey <= completviewRectangle.bottom)
                        topmargine = topmargine + lastmovey;

                    basePicture.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeFile(uris[1].getPath()), leftmargine, topmargine, 800, 480));
                    firstPic.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeFile(uris[1].getPath()), leftmargine, topmargine, 800, 480));
                    secondPic.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeFile(uris[1].getPath()), leftmargine, topmargine, 800, 480));

                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    moving = false;


                }
                return false;
            }
        });

        firstPic = (ImageView) findViewById(R.id.imageView_firstPic);
        firstPic.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeFile(uris[0].getPath()), leftmargine, topmargine, 800, 480));
        firstPic.setAlpha(85);

        secondPic = (ImageView) findViewById(R.id.imageView_secondPic);
        secondPic.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeFile(uris[2].getPath()), leftmargine, topmargine, 800, 480));
        secondPic.setAlpha(85);

        picView.removeView(secondPic);


        button_moveleft = (Button)findViewById(R.id.button_left);
        button_moveright = (Button)findViewById(R.id.button_right);
        button_movetop = (Button)findViewById(R.id.button_top);
        button_movebottom = (Button)findViewById(R.id.button_bottom);

        picone = (CheckBox) findViewById(R.id.checkBox_picFirst);
        picone.setChecked(true);
        picone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!picone.isChecked()) {
                    picone.setChecked(false);
                    pictwo.setChecked(true);

                    picView.removeView(firstPic);

                    picView.addView(secondPic);
                } else {
                    picone.setChecked(true);
                    pictwo.setChecked(false);
                    picView.removeView(secondPic);

                    picView.addView(firstPic);


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
                    picView.addView(firstPic);
                    picView.removeViewInLayout(secondPic);
                }
                else
                {
                    picone.setChecked(false);
                    pictwo.setChecked(true);
                    picView.removeView(firstPic);
                    picView.addView(secondPic);
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

    }

    @Override
    protected void onPause() {
        super.onPause();
    }




    private void renderHDRandSAve(String end, File sdcardpath)
    {
        if(end.equals("jps"))
        {
            render3d(end,sdcardpath);
        }
        else
        {
            render2d(end, sdcardpath);
        }
    }

    private void render2d(String end, File sdcardpath) {
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
    }

    private void render3d(String end, File sdcardpath)
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

        File file = new File(String.format(freeCamImageDirectory + "/%d." + end, System.currentTimeMillis()));
        saveBitmap(file.getAbsolutePath(), orgi);

        orgi.recycle();
        gc();

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
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(filepath);
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

}
