package freed.cam.ui.guide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.R.drawable;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by George on 1/19/2015.
 */
public class GuideHandler extends Fragment implements ParameterEvents {
    private ImageView img;
    private CameraWrapperInterface cameraUiWrapper;
    private float quckRationMath;
    private final String TAG = GuideHandler.class.getSimpleName();


    public static GuideHandler getInstance()
    {
        GuideHandler g = new GuideHandler();
        return g;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container,null);
        View view = inflater.inflate(layout.cameraui_guides_fragment, container, false);
        img = (ImageView) view.findViewById(id.imageViewGyide);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraUiWrapper !=  null && cameraUiWrapper.getParameterHandler() != null && cameraUiWrapper.getParameterHandler().get(Settings.PreviewSize) != null)
            previewSizeChanged.onStringValueChanged(cameraUiWrapper.getParameterHandler().get(Settings.PreviewSize).GetStringValue());
    }
    @Override
    public void onPause(){
        super.onPause();

    }

    public void setCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.getParameterHandler().get(Settings.GuideList).addEventListner(this);
        Log.d(TAG, "setCameraUiWrapper SetViewG()");
        if (img != null)
            SetViewG(SettingsManager.get(Settings.GuideList).get());
    }

    private void SetViewG(final String str)
    {
        BitmapRessourceWorkerTask task = new BitmapRessourceWorkerTask(img, getResources());
        if (quckRationMath < 1.44f) {
            switch (str) {
                case "Golden Spiral":
                    task.execute(drawable.ic_guide_golden_spiral_4_3);
                    break;
                case "Rule Of Thirds":
                    task.execute(drawable.ic_guide_rule_3rd_4_3);
                    break;
                case "Square 1:1":
                    task.execute(drawable.ic_guide_insta_1_1);
                    break;
                case "Square 4:3":
                    task.execute(drawable.ic_guide_insta_4_3);
                    break;
                case "Square 16:9":
                    task.execute(drawable.ic_guide_insta_16_9);
                    break;
                case "Diagonal Type 1":
                    task.execute(drawable.ic_guide_diagonal_type_1_4_3);
                    break;
                case "Diagonal Type 2":
                    task.execute(drawable.ic_guide_diagonal_type_2_4_3);
                    break;
                case "Diagonal Type 3":
                    task.execute(drawable.ic_guide_diagonal_type_3);
                    break;
                case "Diagonal Type 4":
                    task.execute(drawable.ic_guide_diagonal_type_4);
                    break;
                case "Diagonal Type 5":
                    task.execute(drawable.ic_guide_diagonal_type_5);
                    break;
                case "Golden Ratio":
                    task.execute(drawable.ic_guide_golden_ratio_type_1_4_3);
                    break;
                case "Golden Hybrid":
                    task.execute(drawable.ic_guide_golden_spriral_ratio_4_3);
                    break;
                case "Golden R/S 1":
                    task.execute(drawable.ic_guide_golden_fuse1_4_3);
                    break;
                case "Golden R/S 2":
                    task.execute(drawable.ic_guide_golden_fusion2_4_3);
                    break;
                case "Golden Triangle":
                    task.execute(drawable.ic_guide_golden_triangle_4_3);
                    break;
                case "Group POV Five":
                    task.execute(drawable.ic_guide_groufie_five);
                    break;
                case "Group POV Three":
                    task.execute(drawable.ic_guide_groufie_three);
                    break;
                case "Group POV Potrait":
                    task.execute(drawable.ic_guide_groupshot_potrait);
                    break;
                case "Group POV Full":
                    task.execute(drawable.ic_guide_groupshot_fullbody);
                    break;
                case "Group POV Elvated":
                    task.execute(drawable.ic_guide_groupshot_elevated_pov);
                    break;
                case "Group by Depth":
                    task.execute(drawable.ic_guide_groupshot_outfocusing);
                    break;
                case "Group Center Lead":
                    task.execute(drawable.ic_guide_groupshot_center_leader);
                    break;
                case "Center Type x":
                    task.execute(drawable.ic_guide_center_type_1_4_3);
                    break;
                case "Center Type +":
                    task.execute(drawable.ic_guide_center_type_2_4_3);
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
                    task.execute(drawable.ic_guide_golden_spiral_16_9);
                    break;
                case "Golden Triangle":
                    task.execute(drawable.ic_golden_triangle_16_9);
                    break;
                case "Rule Of Thirds":
                    task.execute(drawable.ic_guide_rule_3rd_16_9);
                    break;
                case "Center Type x":
                    task.execute(drawable.ic_guide_center_type_1_4_3);
                    break;
                case "Center Type +":
                    task.execute(drawable.ic_guide_center_type_2_4_3);
                    break;
                case "Square 1:1":
                    task.execute(drawable.ic_guide_insta_1_1);
                    break;
                case "Square 4:3":
                    task.execute(drawable.ic_guide_insta_4_3);
                    break;
                case "Square 16:9":
                    task.execute(drawable.ic_guide_insta_16_9);
                    break;
                case "None":
                    img.setImageBitmap(null);
                    break;
            }
            img.invalidate();

        }
    }


    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String value) {
        if (isAdded())
            SetViewG(value);
    }

    private final ParameterEvents previewSizeChanged = new ParameterEvents() {
        @Override
        public void onIsSupportedChanged(boolean value) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean value) {

        }

        @Override
        public void onIntValueChanged(int current) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onStringValueChanged(String val) {
            Log.d(TAG, "I_ModeParameterEvent SetViewG()");
            String img = SettingsManager.get(Settings.GuideList).get();
            if (val != null
                    && !TextUtils.isEmpty(val)
                    && img != null
                    && !TextUtils.isEmpty(img)
                    && !img.equals("None")) {
                String[] size = val.split("x");
                quckRationMath = Float.valueOf(size[0]) / Float.valueOf(size[1]);
                SetViewG(img);
            }
        }

    };

}

