package com.troop.freecam.HDR;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.troop.freecam.R;

import java.io.File;

/**
 * Created by troop on 18.10.13.
 */
public class HdrRenderActivity extends Activity
{

    public final String TAG = "HDRActivity";
    private Uri[] uris;

    Button button_renderHDR;
    ImageOverlayView overlayView;
    Button button_moveleft;
    Button button_moveright;
    Button button_movetop;
    Button button_movebottom;
    Switch switchPic;

    TextView firstleft;
    TextView secondleft;
    TextView baseleft;
    TextView firstTop;
    TextView secondTop;
    TextView baseTop;
    TextView statusText;

    ThreeDBitmapHandler threeDBitmapHandler;
    TwoDBitmapHandler twoDBitmapHandler;
    public SharedPreferences preferences;

    RelativeLayout picView;

    //should always true, if not it can be used to load the activity from start for debugging
    boolean topintent = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            setContentView(R.layout.hdr_layout);
            Bundle extras = getIntent().getExtras();
            String[] muh =null;
            if (extras != null)
                muh = extras.getStringArray("uris");
            uris = new Uri[3];
            if (muh != null)
            {
                topintent = true;
                uris[0] = Uri.fromFile(new File(muh[0]));
                uris[1] = Uri.fromFile(new File(muh[1]));
                uris[2] = Uri.fromFile(new File(muh[2]));
            }
            else
            {
                uris[0] = Uri.fromFile(new File("/mnt/sdcard/DCIM/FreeCam/Tmp/Tmp/0.jpg"));
                uris[1] = Uri.fromFile(new File("/mnt/sdcard/DCIM/FreeCam/Tmp/Tmp/1.jpg"));
                uris[2] = Uri.fromFile(new File("/mnt/sdcard/DCIM/FreeCam/Tmp/Tmp/2.jpg"));
            }
            initControls();
        }
    }

    private void disableControls()
    {
        button_movebottom.setEnabled(false);
        button_moveleft.setEnabled(false);
        button_moveright.setEnabled(false);
        button_movetop.setEnabled(false);
        switchPic.setEnabled(false);
        button_renderHDR.setEnabled(false);
        overlayView.running = false;
        overlayView.setEnabled(false);

    }

    private void initControls() {
        button_renderHDR = (Button)findViewById(R.id.button_RenderHdr);
        button_renderHDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                disableControls();
                statusText.setText("Rendering...");
                handler.post(runnableRender);
            }
        });

        statusText = (TextView)findViewById(R.id.textView_Status);

        picView = (RelativeLayout)findViewById(R.id.LayoutPics);
        overlayView = (ImageOverlayView) findViewById(R.id.view_overlay2);

        button_moveleft = (Button)findViewById(R.id.button_left);
        button_moveleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchPic.isChecked())
                    overlayView.AddLeft(true, -1);
                else
                    overlayView.AddLeft(false, -1);
                updateTextBoxes();
            }
        });
        button_moveright = (Button)findViewById(R.id.button_right);
        button_moveright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (switchPic.isChecked())
                    overlayView.AddLeft(true, 1);
                else
                    overlayView.AddLeft(false, 1);
                updateTextBoxes();

            }
        });
        button_movetop = (Button)findViewById(R.id.button_top);
        button_movetop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchPic.isChecked())
                    overlayView.AddTop(true, -1);
                else
                    overlayView.AddTop(false, -1);
                updateTextBoxes();
            }
        });
        button_movebottom = (Button)findViewById(R.id.button_bottom);
        button_movebottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchPic.isChecked())
                    overlayView.AddTop(true, 1);
                else
                    overlayView.AddTop(false, 1);
                updateTextBoxes();
            }
        });

        switchPic = (Switch) findViewById(R.id.switch_Pic);
        switchPic.setChecked(true);
        switchPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchPic.isChecked()) {
                    overlayView.drawFirstPic = true;
                    overlayView.invalidate();
                } else {
                    overlayView.drawFirstPic = false;
                    overlayView.invalidate();
                }
            }
        });

        firstleft = (TextView)findViewById(R.id.textView_firstLeft);
        firstTop =(TextView) findViewById(R.id.textView2_firstTop);
        secondleft = (TextView) findViewById(R.id.textView_secondLeft);
        secondTop =(TextView) findViewById(R.id.textView3_secondTop);
        baseleft = (TextView) findViewById(R.id.textView_baseLeft);
        baseTop = (TextView) findViewById(R.id.textView_baseTop);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        statusText.setText("Loading...");
        if (uris[0].getPath().endsWith("jps"))
        {
            threeDBitmapHandler = new ThreeDBitmapHandler(this, uris);
        }
        else
        {
            twoDBitmapHandler = new TwoDBitmapHandler(this, uris);
        }
        new Thread(runnableLoad).start();
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
            path = threeDBitmapHandler.Render3d(overlayView.baseHolder, overlayView.firstHolder, overlayView.secondHolder, overlayView.OrginalWidth ,overlayView.OrginalHeight);
        }
        else
        {
            twoDBitmapHandler.cropPictures(overlayView.baseHolder, overlayView.firstHolder, overlayView.secondHolder, overlayView.OrginalWidth ,overlayView.OrginalHeight);
            path = twoDBitmapHandler.render2d(end, sdcardpath);
        }
        if (topintent)
        {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",path);
            setResult(RESULT_OK,returnIntent);
            finish();
        }
        else
        {
            //overlayView.LoadImage(path);
        }

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        overlayView.invalidate();
    }

    private void updateTextBoxes()
    {
        firstTop.setText("ft:"+overlayView.firstHolder.Y);
        firstleft.setText("fl:" + overlayView.firstHolder.X);
        secondTop.setText("st:" + overlayView.secondHolder.Y);
        secondleft.setText("sl:" + overlayView.secondHolder.X);
        baseTop.setText("bt:" + overlayView.baseHolder.Y);
        baseleft.setText("bl:" + overlayView.baseHolder.X);
    }

    private Handler handler = new Handler();
    private Runnable runnableRender = new Runnable()
    {
        public void run()
        {
            doRender();
        }
    };

    private void doRender()
    {
        try
        {
        String end = "";
        if (uris[0].getPath().endsWith("jps"))
            end = "jps";
        else
            end = "jpg";
        File sdcardpath = Environment.getExternalStorageDirectory();
        overlayView.Destroy();
        
        renderHDRandSAve(end, sdcardpath);
        }
        catch (NullPointerException ex)
        {

        }
    }

    private Runnable runnableLoad = new Runnable() {
        @Override
        public void run() {
            if (uris[0].getPath().endsWith("jps"))
            {
                overlayView.Load(threeDBitmapHandler.split3DImagesIntoLeftRight(uris));
            }
            else
            {
                overlayView.Load(uris);
            }
            overlayView.drawFirstPic = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusText.setText("");
                    overlayView.invalidate();
                }
            });
        }
    };
}
