package freed.cam.ui.guide;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.troop.freedcam.R.drawable;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by George on 1/19/2015.
 */
@AndroidEntryPoint
public class GuideHandler extends Fragment {
    private ImageView img;
    private CameraWrapperInterface cameraUiWrapper;
    private float quckRationMath;
    private final String TAG = GuideHandler.class.getSimpleName();
    @Inject
    SettingsManager settingsManager;

    public static GuideHandler getInstance()
    {
        return new GuideHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container,null);
        View view = inflater.inflate(layout.cameraui_guides_fragment, container, false);
        img = view.findViewById(id.imageViewGyide);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraUiWrapper !=  null && cameraUiWrapper.getParameterHandler() != null && cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_SIZE) != null)
            setAspectRation(cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_SIZE).getStringValue());
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void setCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        ((AbstractParameter)cameraUiWrapper.getParameterHandler().get(SettingKeys.GUIDE_LIST)).addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                SetViewG(settingsManager.getGlobal(SettingKeys.GUIDE_LIST).get());
            }
        });
        //cameraUiWrapper.getParameterHandler().get(SettingKeys.GuideList).addEventListner(this);
        Log.d(TAG, "setCameraUiWrapper SetViewG()");
        if (img != null)
            SetViewG(settingsManager.getGlobal(SettingKeys.GUIDE_LIST).get());
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

    private void setAspectRation(String val) {
        Log.d(TAG, "I_ModeParameterEvent SetViewG()");
        String img = settingsManager.getGlobal(SettingKeys.GUIDE_LIST).get();
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

}

