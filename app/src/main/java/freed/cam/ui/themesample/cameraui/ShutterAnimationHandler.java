package freed.cam.ui.themesample.cameraui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.troop.freedcam.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.utils.Log;

/**
 * Created by KillerInk on 04.12.2017.
 */

public class ShutterAnimationHandler extends Handler
{
    private ModuleHandlerAbstract.CaptureStates currentShow = ModuleHandlerAbstract.CaptureStates.image_capture_stop;
    int red_top, red_bottom, red_right,red_left, padding_red, red_radius, halfsize;
    float txt_left, txt_right,txt_top,txt_bottom, txt_length,txt_height;

    private final int MSG_START_ANIMATION = 0;
    private final int MSG_INVALIDATE = 2;

    //shutter_open_radius for the Transparent Radius to draw to simulate shutter open
    private float shutter_open_radius = 0.0f;
    //true when the red recording button should get shown, used for continouse capture and video
    private boolean drawRecordingImage = false;
    //current size of the red circle to draw
    private int recordingRadiusCircle;
    //current size of the red rectangle to draw
    private int recordingRadiusRectangle;

    //holds the time from a capture start
    private long startime;
    //frames to draw
    private final int MAXFRAMES = 10;
    //holds the currentframe number
    private int currentframe = 0;

    private boolean running = false;

    private boolean stopTimer = false;
    private int MAX_SHUTTER_OPEN;
    private int MAX_RECORDING_OPEN;
    private int RECORDING_OPEN_STEP;

    //the step wich the shutter_open_radius gets increased/decrased
    private int SHUTTER_OPEN_STEP;
    private boolean shutteractive = false;

    private boolean drawTimer = false;
    private Paint shutteropentimePaint;

// used to track how long values calc took, and reduce depending on it the sleep time for next calc/draw
    private long calcstartTime;


    private Paint transparent;
    private Paint red;
    private ShutterButton shutterButton;
    private UIHandler uiHandler;

    private final int space = 3;

    public boolean isRunning()
    {
        return running;
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss:SSS");

    private final int FPS = 1000/ 30;
    private String shutteropentime;

    public ShutterAnimationHandler(Looper looper, Resources resources,ShutterButton shutterButton)
    {

        super(looper);
        halfsize = resources.getDimensionPixelSize(R.dimen.cameraui_shuttericon_size) /2;
        uiHandler = new UIHandler(Looper.getMainLooper());
        this.shutterButton = shutterButton;
        //used to draw green timer inside the shutter button
        shutteropentimePaint = new Paint();
        shutteropentimePaint.setColor(Color.GREEN);
        shutteropentimePaint.setTextSize(resources.getDimension(R.dimen.cameraui_infooverlay_textsize));
        shutteropentimePaint.setStyle(Paint.Style.FILL);
        shutteropentimePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        shutteropentimePaint.setAntiAlias(true);

        //used to open the shutter
        transparent = new Paint();
        transparent.setColor(Color.TRANSPARENT);
        transparent.setStyle(Paint.Style.FILL);
        transparent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        transparent.setAntiAlias(true);

        //used to for the recording button
        red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.FILL);
        red.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        red.setAntiAlias(true);
    }

    public void setCaptureState(ModuleHandlerAbstract.CaptureStates captureState)
    {
        synchronized (this) {
            currentShow = captureState;
        }
    }

    private void startAnimation()
    {
        this.obtainMessage(MSG_START_ANIMATION).sendToTarget();
    }

    public void startDrawing() {
        shutteractive = true;
        stopTimer = false;
        if (isRunning()) {
            setStartTime(System.currentTimeMillis());
        }
        else {
            setStartTime(System.currentTimeMillis());
            startAnimation();
        }
    }

    public void drawTimer(boolean drawTimer)
    {
        synchronized (this) {
            this.drawTimer = drawTimer;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch(msg.what)
        {
            case MSG_START_ANIMATION:
                if (!isRunning())
                    run();
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }

    public void setStartTime(long startTime)
    {
        synchronized (this)
        {
            this.startime = startTime;
        }
    }

    public void stopShutterTimer() {
        synchronized (this) {
            stopTimer = true;
        }
    }

    private void run()
    {
        calcstartTime = System.nanoTime();
        MAX_SHUTTER_OPEN = (shutterButton.getWidth() - 100) / 2;
        SHUTTER_OPEN_STEP = (MAX_SHUTTER_OPEN) / MAXFRAMES;
        MAX_RECORDING_OPEN = shutterButton.getWidth() /4;
        RECORDING_OPEN_STEP = MAX_RECORDING_OPEN/MAXFRAMES;

        int recordingSize = MAX_RECORDING_OPEN;
        recordingRadiusCircle = recordingSize;
        recordingRadiusRectangle = 0;
        running = true;
        while (shutteractive)
        {
            if (currentframe < MAXFRAMES)
                draw();
            else {
                draw();
                currentframe = 0;
                if (stopTimer) {
                    sendMsgToSButton("");
                    shutteractive = false;
                }
            }
            if (drawTimer)
                sendMsgToSButton(getTimeGoneString(startime));
            else
                sendMsgToSButton("");
            currentframe++;
            try {
                long sleep = FPS-((System.nanoTime()-calcstartTime)/1000000L);
                if (sleep > 0)
                    Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Log.WriteEx(e);
            }
        }
        running = false;

        sendMsgToSButton("");
    }


    private void sendMsgToSButton(String s)
    {
        shutteropentime = s;
        uiHandler.obtainMessage(MSG_INVALIDATE).sendToTarget();
    }

    private String getTimeGoneString(long startime)
    {
        long now = System.currentTimeMillis();
        long dif = now - startime;
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(new Date(dif));

    }

    private void draw() {
        synchronized (this) {
            switch (currentShow) {
                case video_recording_stop:
                    shutter_open_radius = 0;
                    recordingRadiusCircle += RECORDING_OPEN_STEP;
                    if (recordingRadiusCircle > MAX_RECORDING_OPEN || currentframe == MAXFRAMES)
                        recordingRadiusCircle = MAX_RECORDING_OPEN;

                    recordingRadiusRectangle -= RECORDING_OPEN_STEP;
                    if (recordingRadiusRectangle < 0 || currentframe == MAXFRAMES)
                        recordingRadiusRectangle = 0;
                    drawRecordingImage = true;
                    break;
                case video_recording_start:
                    shutter_open_radius = 0;
                    recordingRadiusCircle -= RECORDING_OPEN_STEP;
                    if (recordingRadiusCircle < 0 || currentframe == MAXFRAMES)
                        recordingRadiusCircle = 0;

                    recordingRadiusRectangle += RECORDING_OPEN_STEP;
                    if (recordingRadiusRectangle > MAX_RECORDING_OPEN || currentframe == MAXFRAMES)
                        recordingRadiusRectangle = MAX_RECORDING_OPEN;
                    drawRecordingImage = true;
                    break;
                case image_capture_stop:
                    drawRecordingImage = false;
                    shutter_open_radius -= SHUTTER_OPEN_STEP;
                    if (shutter_open_radius < 0 || currentframe == MAXFRAMES)
                        shutter_open_radius = 0;
                    break;
                case image_capture_start:
                    drawRecordingImage = false;
                    shutter_open_radius += SHUTTER_OPEN_STEP;
                    if (shutter_open_radius > MAX_SHUTTER_OPEN || currentframe == MAXFRAMES)
                        shutter_open_radius = MAX_SHUTTER_OPEN;
                    break;
                case continouse_capture_start:
                    drawRecordingImage = true;
                    shutter_open_radius += SHUTTER_OPEN_STEP;
                    if (shutter_open_radius > MAX_SHUTTER_OPEN || currentframe == MAXFRAMES)
                        shutter_open_radius = MAX_SHUTTER_OPEN;

                    recordingRadiusCircle -= RECORDING_OPEN_STEP;
                    if (recordingRadiusCircle < 0 || currentframe == MAXFRAMES)
                        recordingRadiusCircle = 0;
                    recordingRadiusRectangle += RECORDING_OPEN_STEP;
                    if (recordingRadiusRectangle > MAX_RECORDING_OPEN || currentframe == MAXFRAMES)
                        recordingRadiusRectangle = MAX_RECORDING_OPEN;
                    break;
                case cont_capture_stop_while_working:
                    drawRecordingImage = true;
                    //shutter_open_radius += SHUTTER_OPEN_STEP;
                    recordingRadiusCircle += RECORDING_OPEN_STEP;
                    if (recordingRadiusCircle > MAX_RECORDING_OPEN || currentframe == MAXFRAMES)
                        recordingRadiusCircle = MAX_RECORDING_OPEN;
                    recordingRadiusRectangle -= RECORDING_OPEN_STEP;
                    if (recordingRadiusRectangle < 0 || currentframe == MAXFRAMES)
                        recordingRadiusRectangle = 0;
                    break;
                case cont_capture_stop_while_notworking:
                    shutter_open_radius = 0;
                    recordingRadiusCircle += RECORDING_OPEN_STEP;
                    if (recordingRadiusCircle > MAX_RECORDING_OPEN || currentframe == MAXFRAMES)
                        recordingRadiusCircle = MAX_RECORDING_OPEN;
                    recordingRadiusRectangle -= RECORDING_OPEN_STEP;
                    if (recordingRadiusRectangle < 0 || currentframe == MAXFRAMES)
                        recordingRadiusRectangle = 0;
                    drawRecordingImage = true;
                    break;
                case continouse_capture_stop:
                    recordingRadiusCircle += RECORDING_OPEN_STEP;
                    if (recordingRadiusCircle > MAX_RECORDING_OPEN || currentframe == MAXFRAMES)
                        recordingRadiusCircle = MAX_RECORDING_OPEN;
                    recordingRadiusRectangle -= RECORDING_OPEN_STEP;
                    if (recordingRadiusRectangle < 0 || currentframe == MAXFRAMES)
                        recordingRadiusRectangle = 0;
                    drawRecordingImage = true;
                    break;
                case continouse_capture_work_start:
                    drawRecordingImage = true;
                    shutter_open_radius += SHUTTER_OPEN_STEP;
                    if (shutter_open_radius > MAX_SHUTTER_OPEN)
                        shutter_open_radius = MAX_SHUTTER_OPEN;

                    break;
                case continouse_capture_work_stop:
                    drawRecordingImage = true;
                    shutter_open_radius -= SHUTTER_OPEN_STEP;
                    if (shutter_open_radius < 0 || currentframe == MAXFRAMES)
                        shutter_open_radius = 0;
                    break;
            }

            padding_red = halfsize;
            red_radius = recordingRadiusRectangle / 2;
            if (drawRecordingImage) {
                red_top = padding_red - red_radius;
                red_bottom = padding_red + red_radius;
                red_left = padding_red - red_radius;
                red_right = padding_red + red_radius;
                padding_red = red_bottom + red_radius + space;
            }
            if (drawTimer && !TextUtils.isEmpty(shutteropentime)) {
                txt_length = shutteropentimePaint.measureText(shutteropentime);
                txt_height = shutteropentimePaint.getTextSize();
                txt_left = halfsize - (txt_length / 2);
                txt_top = padding_red - txt_height - space;
                txt_right = halfsize + (txt_length / 2) + space;
                txt_bottom = padding_red + txt_height / 2 + space;
            }
        }
        //Log.d(TAG,"shutter_open:" + shutter_open_radius + " recCircle:" + recordingRadiusCircle + " recRect:" + recordingRadiusRectangle +  " captureState:" + currentShow);
    }

    public void onDraw(Canvas canvas)
    {
        synchronized (this) {
            canvas.drawCircle(halfsize, halfsize, shutter_open_radius, transparent);
            if (drawRecordingImage) {
                canvas.drawCircle(halfsize, halfsize, recordingRadiusCircle / 2, red);
                canvas.drawRect(red_left, red_top, red_right, red_bottom, red);

            }
            if (drawTimer && !TextUtils.isEmpty(shutteropentime)) {

                shutteropentimePaint.setColor(Color.BLACK);
                shutteropentimePaint.setAlpha(125);

                canvas.drawRect(txt_left - space, txt_top, txt_right, txt_bottom, shutteropentimePaint);

                shutteropentimePaint.setColor(Color.GREEN);
                shutteropentimePaint.setAlpha(255);
                canvas.drawText(shutteropentime, txt_left, padding_red, shutteropentimePaint);
            }
        }
    }

    private class UIHandler extends Handler
    {
        public UIHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case MSG_INVALIDATE:
                    shutterButton.invalidate();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    }
}
