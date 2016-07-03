package freed.cam.ui.guide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.R.drawable;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.I_ParametersLoaded;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import freed.utils.AppSettingsManager;

/**
 * Created by George on 1/19/2015.
 */
public class GuideHandler extends Fragment implements I_ModeParameterEvent , I_ParametersLoaded {
    private View view;
    private ImageView img;
    private CameraWrapperInterface cameraUiWrapper;
    private float quckRationMath;
    private AppSettingsManager appSettingsManager;

    public static GuideHandler GetInstance(AppSettingsManager appSettingsManager)
    {
        GuideHandler g = new GuideHandler();
        g.appSettingsManager = appSettingsManager;
        return g;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container,null);
        view = inflater.inflate(layout.cameraui_guides_fragment, container,false);
        img = (ImageView) view.findViewById(id.imageViewGyide);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraUiWrapper !=  null && cameraUiWrapper.GetParameterHandler() != null && cameraUiWrapper.GetParameterHandler().PreviewSize != null)
            previewSizeChanged.onParameterValueChanged(cameraUiWrapper.GetParameterHandler().PreviewSize.GetValue());
    }
    @Override
    public void onPause(){
        super.onPause();

    }

    public void setCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.GetParameterHandler().GuideList.addEventListner(this);
        cameraUiWrapper.GetParameterHandler().AddParametersLoadedListner(this);
    }

    private void SetViewG(final String str)
    {

        img.post(new Runnable() {
                @Override
                public void run() {
                    if (quckRationMath < 1.44f) {
                        switch (str) {
                            case "Golden Spiral":
                                img.setImageResource(drawable.ic_guide_golden_spiral_4_3);
                                break;
                            case "Rule Of Thirds":
                                img.setImageResource(drawable.ic_guide_rule_3rd_4_3);
                                break;
                            case "Square 1:1":
                                img.setImageResource(drawable.ic_guide_insta_1_1);
                                break;
                            case "Square 4:3":
                                img.setImageResource(drawable.ic_guide_insta_4_3);
                                break;
                            case "Square 16:9":
                                img.setImageResource(drawable.ic_guide_insta_16_9);
                                break;
                            case "Diagonal Type 1":
                                img.setImageResource(drawable.ic_guide_diagonal_type_1_4_3);
                                break;
                            case "Diagonal Type 2":
                                img.setImageResource(drawable.ic_guide_diagonal_type_2_4_3);
                                break;
                            case "Diagonal Type 3":
                                img.setImageResource(drawable.ic_guide_diagonal_type_3);
                                break;
                            case "Diagonal Type 4":
                                img.setImageResource(drawable.ic_guide_diagonal_type_4);
                                break;
                            case "Diagonal Type 5":
                                img.setImageResource(drawable.ic_guide_diagonal_type_5);
                                break;
                            case "Golden Ratio":
                                img.setImageResource(drawable.ic_guide_golden_ratio_type_1_4_3);
                                break;
                            case "Golden Hybrid":
                                img.setImageResource(drawable.ic_guide_golden_spriral_ratio_4_3);
                                break;
                            case "Golden R/S 1":
                                img.setImageResource(drawable.ic_guide_golden_fuse1_4_3);
                                break;
                            case "Golden R/S 2":
                                img.setImageResource(drawable.ic_guide_golden_fusion2_4_3);
                                break;
                            case "Golden Triangle":
                                img.setImageResource(drawable.ic_guide_golden_triangle_4_3);
                                break;
                            case "Group POV Five":
                                img.setImageResource(drawable.ic_guide_groufie_five);
                                break;
                            case "Group POV Three":
                                img.setImageResource(drawable.ic_guide_groufie_three);
                                break;
                            case "Group POV Potrait":
                                img.setImageResource(drawable.ic_guide_groupshot_potrait);
                                break;
                            case "Group POV Full":
                                img.setImageResource(drawable.ic_guide_groupshot_fullbody);
                                break;
                            case "Group POV Elvated":
                                img.setImageResource(drawable.ic_guide_groupshot_elevated_pov);
                                break;
                            case "Group by Depth":
                                img.setImageResource(drawable.ic_guide_groupshot_outfocusing);
                                break;
                            case "Group Center Lead":
                                img.setImageResource(drawable.ic_guide_groupshot_center_leader);
                                break;
                            case "Center Type x":
                                img.setImageResource(drawable.ic_guide_center_type_1_4_3);
                                break;
                            case "Center Type +":
                                img.setImageResource(drawable.ic_guide_center_type_2_4_3);
                                break;
                            case "None":
                                img.setImageBitmap(null);
                                break;
                        }
                        img.invalidate();
                    }
                    else
                    {
                        switch (str) {
                            case "Golden Spiral":
                                img.setImageResource(drawable.ic_guide_golden_spiral_16_9);
                                break;
                            case "Golden Triangle":
                                img.setImageResource(drawable.ic_golden_triangle_16_9);
                                break;
                            case "Rule Of Thirds":
                                img.setImageResource(drawable.ic_guide_rule_3rd_16_9);
                                break;
                            case "Center Type x":
                                img.setImageResource(drawable.ic_guide_center_type_1_4_3);
                                break;
                            case "Center Type +":
                                img.setImageResource(drawable.ic_guide_center_type_2_4_3);
                                break;
                            case "Square 1:1":
                                img.setImageResource(drawable.ic_guide_insta_1_1);
                                break;
                            case "Square 4:3":
                                img.setImageResource(drawable.ic_guide_insta_4_3);
                                break;
                            case "Square 16:9":
                                img.setImageResource(drawable.ic_guide_insta_16_9);
                                break;
                            case "None":
                                img.setImageBitmap(null);
                                break;
                        }
                        img.invalidate();

                    }
                }
            });
    }

    @Override
    public void onParameterValueChanged(String val) {
        SetViewG(val);
    }

    @Override
    public void onParameterIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onParameterIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onParameterValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    private final I_ModeParameterEvent previewSizeChanged = new I_ModeParameterEvent() {
        @Override
        public void onParameterValueChanged(String val) {
            String img = appSettingsManager.getString(AppSettingsManager.SETTING_GUIDE);
            if (val != null && !val.equals("")&& img != null && !img.equals("") && !img.equals("None")) {
                String[] size = val.split("x");
                quckRationMath = Float.valueOf(size[0]) / Float.valueOf(size[1]);
                SetViewG(img);
            }
        }

        @Override
        public void onParameterIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

    @Override
    public void ParametersLoaded(CameraWrapperInterface cameraWrapper)
    {
        if (cameraUiWrapper.GetParameterHandler().PreviewSize != null)
            cameraUiWrapper.GetParameterHandler().PreviewSize.addEventListner(previewSizeChanged);
    }
}

