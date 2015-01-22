package com.troop.freedcam.ui.handler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.MainActivity_v2;

/**
 * Created by George on 1/19/2015.
 */
public class GuideHandler extends LinearLayout implements AbstractModeParameter.I_ModeParameterEvent, I_ParametersLoaded {
    LinearLayout linearLayout;
    ImageView img;
    Context contextt;
    AbstractCameraUiWrapper cameraUiWrapper;

    public GuideHandler(Context context) {

        super(context);
        init(context);
        this.contextt = context;
    }

    public GuideHandler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        alignment();

    }

    public GuideHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        alignment();
    }

    public void setCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);

    }



    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.guides, this);
        img = (ImageView) findViewById(R.id.imageViewGyide);

    }

    public void SetViewG(String str)
    {

        System.out.println("defcomg "+ "fuck");

        if(str.equals("Golden Spiral")){

            img.setImageResource(R.drawable.ic_guide_golden_spiral);
        }
        else if(str.equals("Rule Of Thirds")){
            //ImageView img = (ImageView) findViewById(R.id.imageViewGyide);
            img.setImageResource(R.drawable.ic_guide_3rd);
        }
        else if(str.equals("Instagram 1:1")){
            //ImageView img = (ImageView) findViewById(R.id.imageViewGyide);
            img.setImageResource(R.drawable.ic_guide_insta_1_1);
        }
        else if (str.equals("Instagram 4:3"))
            img.setImageResource(R.drawable.ic_guide_insta_4_3);
        else if (str.equals("Instagram 16:9"))
            img.setImageResource(R.drawable.ic_guide_insta_16_9);
        else if (str.equals("Diagonal"))
            img.setImageResource(R.drawable.ic_guide_diagononal);
        else if (str.equals("Golden Ratio"))
            img.setImageResource(R.drawable.ic_guide_gold_ratio);
        else if (str.equals("None"))
            img.setImageBitmap(null);


    }

    private void alignment()
    {
       linearLayout = (LinearLayout)findViewById(R.id.GuideView);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = RelativeLayout.ALIGN_PARENT_RIGHT;
        layoutParams.height = 20;
        linearLayout.setLayoutParams(layoutParams);

        //hytythy


    }

    @Override
    public void onValueChanged(String val) {
        SetViewG(val);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void ParametersLoaded() {
        cameraUiWrapper.camParametersHandler.GuideList.addEventListner(this);
        onValueChanged(cameraUiWrapper.camParametersHandler.GuideList.GetValue());
    }
}
