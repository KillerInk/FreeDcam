package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.view.View;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.ParameterInterface;

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
    public void SetStuff(ActivityInterface fragment_activityInterface, String settingvalue ) {

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
