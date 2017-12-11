package freed.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.troop.freedcam.R;

/**
 * Created by troop on 05.08.2017.
 */

public class CurveViewControl extends LinearLayout implements CurveView.CurveChangedEvent {

    private Button button_rgb;
    private Button button_r;
    private Button button_g;
    private Button button_b;

    private PointF[] rgbCurve;
    private PointF[] rCurve;
    private PointF[] gCurve;
    private PointF[] bCurve;
    private CurveView curveView;
    CurveView.CurveChangedEvent curveChangedListner;
    private Button activeButton;

    public CurveViewControl(Context context) {
        super(context);
        init(context);
    }

    public CurveViewControl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CurveViewControl(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.curve_view_control, this);
        this.button_rgb = (Button)findViewById(R.id.button_rgb);
        button_rgb.setOnClickListener(onButtonClick);
        activeButton = button_rgb;
        this.button_r = (Button)findViewById(R.id.button_red);
        button_r.setOnClickListener(onButtonClick);
        this.button_g = (Button)findViewById(R.id.button_green);
        button_g.setOnClickListener(onButtonClick);
        this.button_b = (Button)findViewById(R.id.button_blue);
        button_b.setOnClickListener(onButtonClick);
        this.curveView = (CurveView)findViewById(R.id.curveViewHolder);
        curveView.setCurveChangedListner(this);
        rgbCurve = new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)};
        rCurve = new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)};
        gCurve = new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)};
        bCurve = new PointF[]{new PointF(0,0),new PointF(0.25f,0.25f), new PointF(0.5f,0.5f),new PointF(0.75f,0.75f),new PointF(1,1)};
    }

    public void setCurveChangedListner(CurveView.CurveChangedEvent event)
    {
        this.curveChangedListner = event;
    }

    private OnClickListener onButtonClick = new OnClickListener() {
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
}
