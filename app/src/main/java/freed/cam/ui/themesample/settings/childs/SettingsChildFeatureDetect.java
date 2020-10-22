package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.view.View;

import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;

import freed.ActivityInterface;

/**
 * Created by troop on 20.07.2017.
 */

public class SettingsChildFeatureDetect extends SettingsChildMenu
{

    private ActivityInterface activityInterface;

    public SettingsChildFeatureDetect(Context context, int headerid, int descriptionid, ActivityInterface activityInterface) {
        super(context, headerid, descriptionid);
        this.activityInterface = activityInterface;
        this.valueText.setText("");
    }

    @Override
    public void onClick(View v)
    {
        activityInterface.runFeatureDetector();
    }


    @Override
    public void SetUiItemClickListner(SettingsChildClick menuItemClick) {

    }

    @Override
    public void SetParameter(ParameterInterface parameter) {

    }


    @Override
    public String[] GetValues() {
        return null;
    }

    @Override
    public void SetValue(String value) {

    }

    @Override
    public void onModuleChanged(String module) {
    }
}
