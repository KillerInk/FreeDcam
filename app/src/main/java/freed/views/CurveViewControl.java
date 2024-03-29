package freed.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.troop.freedcam.R;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.FreedApplication;
import freed.cam.histogram.HistogramController;
import freed.cam.histogram.HistogramData;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.VideoToneCurveProfile;
import freed.views.pagingview.PagingViewTouchState;

/**
 * Created by troop on 05.08.2017.
 */

@AndroidEntryPoint
public class CurveViewControl extends LinearLayout implements CurveView.CurveChangedEvent, HistogramController.DataListner {

    private static final String TAG = CurveViewControl.class.getSimpleName();
    private Button button_rgb;
    private Button button_r;
    private Button button_g;
    private Button button_b;
    private Button button_addPoint;
    private Button button_removePoint;
    private Button button_save;
    private Button button_load;
    private Button button_drag;

    private EditText savePanel_editText_toneCurveName;
    private Button savePanel_saveButton;

    private LinearLayout savePanel;
    private LinearLayout loadPanel;

    private PointF[] rgbCurve;
    private PointF[] rCurve;
    private PointF[] gCurve;
    private PointF[] bCurve;
    private CurveView curveView;
    CurveView.CurveChangedEvent curveChangedListner;
    private Button activeButton;
    private float startPosX;
    private float startPosY;

    private enum PointStates
    {
        none,
        add,
        remove,
    }

    private PointStates pointState = PointStates.none;

    @Inject
    SettingsManager settingsManager;
    @Inject
    PagingViewTouchState pagingViewTouchState;

    @Inject
    HistogramController histogramController;

    public CurveViewControl(Context context) {
        super(context);
        init(context);
    }

    public CurveViewControl(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CurveViewControl(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(histogramController != null)
            histogramController.setDataListner(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        if(histogramController != null)
            histogramController.setDataListner(null);
        super.onDetachedFromWindow();
    }

    @Override
    public void setData(HistogramData data) {
        this.post(new Runnable() {
            @Override
            public void run() {
                curveView.setHistogramData(data);
            }
        });

    }

    @Override
    public void setWaveFormData(int[] data, int width, int height) {
        this.post(new Runnable() {
            @Override
            public void run() {
                curveView.setWaveformData(data,width,height);
            }
        });
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.curve_view_control, this);

        this.button_rgb = findViewById(R.id.button_rgb);
        button_rgb.setOnClickListener(onR_G_B_ButtonClick);
        activeButton = button_rgb;

        this.button_r = findViewById(R.id.button_red);
        button_r.setOnClickListener(onR_G_B_ButtonClick);

        this.button_g = findViewById(R.id.button_green);
        button_g.setOnClickListener(onR_G_B_ButtonClick);

        this.button_b = findViewById(R.id.button_blue);
        button_b.setOnClickListener(onR_G_B_ButtonClick);

        button_addPoint = findViewById(R.id.button_add_point);
        button_addPoint.setOnClickListener(onAddPointClick);

        button_removePoint = findViewById(R.id.button_remove_point);
        button_removePoint.setOnClickListener(onRemovePointClick);

        button_save =findViewById(R.id.button_save);
        button_save.setOnClickListener(onSaveButtonClick);

        button_load =findViewById(R.id.button_load);
        button_load.setOnClickListener(onLoadButtonClick);

        button_drag = findViewById(R.id.button_drag);
        button_drag.setOnTouchListener(new OnTouchListener() {
            private float lastx;
            private float lasty;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.d(TAG,event.toString());
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if (startPosX == 0.0f)
                    {
                        startPosX = getX() + getWidth();
                        startPosY = getY() + getHeight();
                    }
                    lastx = event.getRawX();
                    lasty = event.getRawY();
                    pagingViewTouchState.setTouchEnable(false);
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE)
                {

                    float difX = lastx - event.getRawX();
                    float difY = lasty - event.getRawY();
                    lastx = event.getRawX();
                    lasty = event.getRawY();

                    ViewGroup.LayoutParams params = getLayoutParams();
                    params.height = (int) (startPosY - (getY() - difY));
                    params.width = (int) (startPosX - (getX() - difX));
                    requestLayout();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    pagingViewTouchState.setTouchEnable(true);
                return false;
            }
        });

        this.curveView = findViewById(R.id.curveViewHolder);
        curveView.setCurveChangedListner(this);
        curveView.bringToFront();

        savePanel = findViewById(R.id.save_panel);
        savePanel.setVisibility(GONE);

        savePanel_editText_toneCurveName = findViewById(R.id.editText_curvename);
        savePanel_saveButton = findViewById(R.id.button_savecurve);
        savePanel_saveButton.setOnClickListener(onSavePanelSaveCurveClick);

        loadPanel = findViewById(R.id.load_panel);
        loadPanel.setVisibility(GONE);

        rgbCurve = new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)};
        rCurve = new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)};
        gCurve = new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)};
        bCurve = new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)};
    }

    public void setCurveChangedListner(CurveView.CurveChangedEvent event)
    {
        this.curveChangedListner = event;
    }

    private final OnClickListener onSaveButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (savePanel.getVisibility() == GONE) {
                curveView.setVisibility(GONE);
                loadPanel.setVisibility(GONE);
                savePanel.setVisibility(VISIBLE);
                savePanel.bringToFront();
            }
            else {
                savePanel.setVisibility(GONE);
                curveView.setVisibility(VISIBLE);
                curveView.bringToFront();
            }
        }
    };

    private final OnClickListener onLoadButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (loadPanel.getVisibility() == GONE) {
                HashMap<String, VideoToneCurveProfile> profiles = FreedApplication.settingsManager().getVideoToneCurveProfiles();
                String[] pro = new String[profiles.keySet().size()];
                profiles.keySet().toArray(pro);
                loadPanel.removeAllViews();
                for (String s : pro)
                {
                    Button button = new Button(getContext());
                    button.setText(s);
                    button.setOnClickListener(onLoadPanelButtonClick);
                    loadPanel.addView(button);
                    button.bringToFront();
                }
                savePanel.setVisibility(GONE);
                curveView.setVisibility(GONE);
                loadPanel.setVisibility(VISIBLE);
                loadPanel.bringToFront();

            }
            else {
                loadPanel.setVisibility(GONE);
                curveView.setVisibility(VISIBLE);
                curveView.bringToFront();
            }
        }
    };

    private final OnClickListener onLoadPanelButtonClick = new OnClickListener()
    {
        @Override
        public void onClick(View v) {
            String s =(String)((Button)v).getText();
            VideoToneCurveProfile profile = settingsManager.getVideoToneCurveProfiles().get(s);
            settingsManager.get(SettingKeys.TONE_CURVE_PARAMETER).set(s);
            rgbCurve = profile.rgb;
            rCurve = profile.r;
            gCurve = profile.g;
            bCurve = profile.b;
            invalidate();
            loadPanel.setVisibility(GONE);
            curveView.setPoints(rgbCurve);
            curveView.setVisibility(VISIBLE);
            curveView.bringToFront();
            if (curveChangedListner != null)
                curveChangedListner.onCurveChanged(rgbCurve);
        }
    };

    private final OnClickListener onSavePanelSaveCurveClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!TextUtils.isEmpty(savePanel_editText_toneCurveName.getText().toString()))
            {
                VideoToneCurveProfile curveProfile = new VideoToneCurveProfile();
                curveProfile.name = savePanel_editText_toneCurveName.getText().toString();
                curveProfile.rgb = rgbCurve;
                curveProfile.r = rCurve;
                curveProfile.g = gCurve;
                curveProfile.b = bCurve;
                settingsManager.saveVideoToneCurveProfile(curveProfile);
                settingsManager.get(SettingKeys.TONE_CURVE_PARAMETER).set(curveProfile.name);
                savePanel.setVisibility(GONE);
                curveView.setVisibility(VISIBLE);
                curveView.bringToFront();
            }
        }
    };

    private final OnClickListener onRemovePointClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (pointState != PointStates.remove) {
                pointState = PointStates.remove;
                button_removePoint.setBackgroundColor(getResources().getColor(R.color.button_clicked));
            }
            else {
                pointState = PointStates.none;
                button_removePoint.setBackgroundColor(getResources().getColor(R.color.button_notclicked));
            }
        }
    };

    private final OnClickListener onAddPointClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (pointState != PointStates.add) {
                pointState = PointStates.add;
                button_addPoint.setBackgroundColor(getResources().getColor(R.color.button_clicked));
            }
            else {
                pointState = PointStates.none;
                button_addPoint.setBackgroundColor(getResources().getColor(R.color.button_notclicked));
            }
        }
    };

    private final OnClickListener onR_G_B_ButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == activeButton.getId())
                return;
            switch (v.getId())
            {
                case R.id.button_rgb:
                    curveView.setLineColor(Color.WHITE);
                    curveView.setGridColor(Color.RED);
                    curveView.setPoints(rgbCurve);
                    if (curveChangedListner != null)
                        curveChangedListner.onCurveChanged(rgbCurve);
                    break;
                case R.id.button_red:
                    curveView.setLineColor(Color.RED);
                    curveView.setGridColor(Color.WHITE);
                    curveView.setPoints(rCurve);
                    if (curveChangedListner != null)
                        curveChangedListner.onCurveChanged(rCurve,gCurve,bCurve);
                    break;
                case R.id.button_green:
                    curveView.setLineColor(Color.GREEN);
                    curveView.setGridColor(Color.WHITE);
                    curveView.setPoints(gCurve);
                    if (curveChangedListner != null)
                        curveChangedListner.onCurveChanged(rCurve,gCurve,bCurve);
                    break;
                case R.id.button_blue:
                    curveView.setLineColor(Color.BLUE);
                    curveView.setGridColor(Color.WHITE);
                    curveView.setPoints(bCurve);
                    if (curveChangedListner != null)
                        curveChangedListner.onCurveChanged(rCurve,gCurve,bCurve);
                    break;
            }
            activeButton =(Button) v;

        }
    };

    @Override
    public void onCurveChanged(PointF[] pointFs) {

        switch (activeButton.getId())
        {
            case R.id.button_rgb:
                rgbCurve = pointFs;
                if (curveChangedListner != null)
                    curveChangedListner.onCurveChanged(rgbCurve);
                break;
            case R.id.button_red:
                rCurve = pointFs;
                if (curveChangedListner != null)
                    curveChangedListner.onCurveChanged(rCurve,gCurve,bCurve);
                break;
            case R.id.button_green:
                gCurve = pointFs;
                if (curveChangedListner != null)
                    curveChangedListner.onCurveChanged(rCurve,gCurve,bCurve);
                break;
            case R.id.button_blue:
                bCurve = pointFs;
                if (curveChangedListner != null)
                    curveChangedListner.onCurveChanged(rCurve,gCurve,bCurve);
                break;
        }
    }

    @Override
    public void onCurveChanged(PointF[] r, PointF[] g, PointF[] b) {

    }

    @Override
    public void onTouchStart() {
        if (curveChangedListner != null)
            curveChangedListner.onTouchStart();
    }

    @Override
    public void onTouchEnd() {
        if (curveChangedListner != null)
            curveChangedListner.onTouchEnd();
    }

    @Override
    public synchronized void onClick(PointF pointF) {
        if (pointState == PointStates.add)
        {
            switch (activeButton.getId())
            {
                case R.id.button_rgb:
                    rgbCurve = addPointToCurve(rgbCurve, pointF);
                    curveView.setPoints(rgbCurve);
                    break;
                case R.id.button_red:
                    rCurve = addPointToCurve(rCurve, pointF);
                    curveView.setPoints(rCurve);
                    break;
                case R.id.button_green:
                    gCurve = addPointToCurve(gCurve, pointF);
                    curveView.setPoints(gCurve);
                    break;
                case R.id.button_blue:
                    bCurve = addPointToCurve(bCurve, pointF);
                    curveView.setPoints(bCurve);
                    break;
            }
            button_addPoint.setBackgroundColor(getResources().getColor(R.color.button_notclicked));
            pointState = PointStates.none;
            return;
        }
        else if (pointState == PointStates.remove)
        {
            switch (activeButton.getId())
            {
                case R.id.button_rgb:
                    rgbCurve = removePointFromCurve(rgbCurve, pointF);
                    curveView.setPoints(rgbCurve);
                    break;
                case R.id.button_red:
                    rCurve = removePointFromCurve(rCurve, pointF);
                    curveView.setPoints(rCurve);
                    break;
                case R.id.button_green:
                    gCurve = removePointFromCurve(gCurve, pointF);
                    curveView.setPoints(gCurve);
                    break;
                case R.id.button_blue:
                    bCurve = removePointFromCurve(bCurve, pointF);
                    curveView.setPoints(bCurve);
                    break;
            }
            button_removePoint.setBackgroundColor(getResources().getColor(R.color.button_notclicked));
            pointState = PointStates.none;
            return;
        }
    }

    private PointF[] addPointToCurve(PointF[] arr, PointF pointToAdd)
    {
        PointF[] ret = new PointF[arr.length +1];
        int t = 0;
        boolean added = false;
        for (int i = 0; i< arr.length; i++)
        {
            if (arr[i].x < pointToAdd.x || added)
            {
                ret[t++] = arr[i];
            }
            else if (arr[i].x > pointToAdd.x && !added)
            {
                ret[t++] = pointToAdd;
                ret[t++] = arr[i];
                added = true;
            }

        }
        return ret;
    }

    private PointF[] removePointFromCurve(PointF[] arr, PointF pointToRemove)
    {
        PointF[] ret = new PointF[arr.length-1];
        if (ret.length < 2)
            return arr;
        int t = 0;
        for (int i = 0; i< arr.length; i++)
        {
            if (arr[i].x != pointToRemove.x)
                ret[t++] =arr[i];
        }
        return ret;
    }
}
