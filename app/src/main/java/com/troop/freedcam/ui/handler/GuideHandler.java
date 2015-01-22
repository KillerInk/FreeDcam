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
import com.troop.freedcam.ui.MainActivity_v2;

/**
 * Created by George on 1/19/2015.
 */
public class GuideHandler extends LinearLayout {
    LinearLayout linearLayout;
    ImageView img;
    Context contextt;

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



    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.guides, this);
        img = (ImageView) findViewById(R.id.imageViewGyide);

    }

    public void SetViewG(String str)
    {

        System.out.println("defcomg "+ "fuck");

        if(str == "Golden Spiral"){

            img.setImageResource(R.drawable.ic_guide_golden_spiral);
        }

        if(str == "Rule of Thirds"){
            //ImageView img = (ImageView) findViewById(R.id.imageViewGyide);
            img.setImageResource(R.drawable.ic_guide_3rd);
        }

        if(str.equals("Instagram 1:1")){
            //ImageView img = (ImageView) findViewById(R.id.imageViewGyide);
            img.setImageResource(R.drawable.ic_guide_insta_1_1);
        }


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

}
