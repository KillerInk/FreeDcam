package com.troop.freedcam.ui.guide;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by George on 1/19/2015.
 */
public class GuideHandler extends Fragment implements AbstractModeParameter.I_ModeParameterEvent , I_ParametersLoaded {
    View view;
    ImageView img;
    Context contextt;
    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container,null);
        view = inflater.inflate(com.troop.freedcam.ui.guide.R.layout.guides, container,false);
        img = (ImageView) view.findViewById(com.troop.freedcam.ui.guide.R.id.imageViewGyide);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //SetViewG(appSettingsManager.getString(AppSettingsManager.SETTING_GUIDE));
    }

    public void setCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        cameraUiWrapper.camParametersHandler.GuideList.addEventListner(this);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);

        //cameraUiWrapper.camParametersHandler.PreviewSize.addEventListner(previewSizeChanged);
    }

    public int[] GetScreenSize() {
        int width = 0;
        int height = 0;
        if(view == null || view.getContext() == null)
            return null;
        if (Build.VERSION.SDK_INT >= 17)
        {
            WindowManager wm = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
            Point size =  new Point();
            wm.getDefaultDisplay().getRealSize(size);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width = size.x;
                height = size.y;
            }
            else
            {
                height = size.x;
                width = size.y;
            }
        }
        else
        {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            }
            else
            {
                width = metrics.heightPixels;
                height = metrics.widthPixels;
            }

        }
        return new int[]{width,height};
    }


    public void SetViewG(final String str)
    {
        if (img == null)
            return;
        //int sizes[] = GetScreenSize();
        double quckRationMath = 1.43;
        /*if (sizes != null)
            quckRationMath = sizes[0]/sizes[1];*/
        if(quckRationMath < 1.44) {

            img.post(new Runnable() {
                @Override
                public void run() {


                    if (str.equals("Golden Spiral"))
                    {
                        img.setImageResource(R.drawable.ic_guide_golden_spiral_4_3);
                    } else if (str.equals("Rule Of Thirds")) {
                        //ImageView img = (ImageView) findViewById(R.id.imageViewGyide);
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_rule_3rd_4_3);
                    } else if (str.equals("Square 1:1")) {
                        //ImageView img = (ImageView) findViewById(R.id.imageViewGyide);
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_insta_1_1);
                    } else if (str.equals("Square 4:3"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_insta_4_3);
                    else if (str.equals("Square 16:9"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_insta_16_9);
                    else if (str.equals("Diagonal Type 1"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_diagonal_type_1_4_3);
                    else if (str.equals("Diagonal Type 2"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_diagonal_type_2_4_3);
                    else if (str.equals("Diagonal Type 3"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_diagonal_type_3);
                    else if (str.equals("Diagonal Type 4"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_diagonal_type_4);
                    else if (str.equals("Diagonal Type 5"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_diagonal_type_5);
                    else if (str.equals("Golden Ratio"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_golden_ratio_type_1_4_3);
                    else if (str.equals("Golden Hybrid"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_golden_spriral_ratio_4_3);
                    else if (str.equals("Golden R/S 1"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_golden_fuse1_4_3);
                    else if (str.equals("Golden R/S 2"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_golden_fusion2_4_3);
                    else if (str.equals("Golden Triangle"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_golden_triangle_4_3);
                    else if (str.equals("Group POV Five"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_groufie_five);
                    else if (str.equals("Group POV Three"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_groufie_three);
                    else if (str.equals("Group POV Potrait"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_groupshot_potrait);
                    else if (str.equals("Group POV Full"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_groupshot_fullbody);
                    else if (str.equals("Group POV Elvated"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_groupshot_elevated_pov);
                    else if (str.equals("Group by Depth"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_groupshot_outfocusing);
                    else if (str.equals("Group Center Lead"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_groupshot_center_leader);
                    else if (str.equals("Center Type x"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_center_type_1_4_3);
                    else if (str.equals("Center Type +"))
                        img.setImageResource(com.troop.freedcam.ui.guide.R.drawable.ic_guide_center_type_2_4_3);
                    else if (str.equals("None"))
                        img.setImageBitmap(null);
                    img.invalidate();
                }
            });
        }
        else
        {
            if (str.equals("Golden Spiral")) {

                img.setImageResource(R.drawable.ic_guide_golden_spiral_16_9);
            }
            else if (str.equals("Golden Triangle"))
                img.setImageResource(R.drawable.ic_golden_triangle_16_9);
            else if (str.equals("Rule Of Thirds"))
                img.setImageResource(R.drawable.ic_guide_rule_3rd_16_9);
            else if (str.equals("Center Type x"))
                img.setImageResource(R.drawable.ic_guide_center_type_1_4_3);
            else if (str.equals("Center Type +"))
                img.setImageResource(R.drawable.ic_guide_center_type_2_4_3);
            else if (str.equals("Square 1:1")) {
                img.setImageResource(R.drawable.ic_guide_insta_1_1);
            } else if (str.equals("Square 4:3"))
                img.setImageResource(R.drawable.ic_guide_insta_4_3);
            else if (str.equals("Square 16:9"))
                img.setImageResource(R.drawable.ic_guide_insta_16_9);
            else if (str.equals("None"))
                img.setImageBitmap(null);
            img.invalidate();

        }

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


    public void OnPreviewSizeChanged(int w, int h)
    {
        /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i_activity.GetPreviewWidth(), i_activity.GetPreviewHeight());
        layoutParams.gravity = Gravity.CENTER;
        img.setLayoutParams(layoutParams);*/
    }

    AbstractModeParameter.I_ModeParameterEvent previewSizeChanged = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val) {
            /*String split[] = val.split("x");
            int w = Integer.parseInt(split[0]);
            int h = Integer.parseInt(split[1]);
            OnPreviewSizeChanged(w,h);*/
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
    };

    @Override
    public void ParametersLoaded()
    {
        //if (cameraUiWrapper.camParametersHandler.PreviewSize != null)
        //    cameraUiWrapper.camParametersHandler.PreviewSize.addEventListner(previewSizeChanged);
    }
}