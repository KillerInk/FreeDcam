package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.R;

import freed.cam.ui.themesample.SettingsChildAbstract;

/**
 * Created by Ingo on 02.10.2016.
 */

public class UiSettingsChildAeLock extends UiSettingsChild implements SettingsChildAbstract.SettingsChildClick {
    public UiSettingsChildAeLock(Context context) {
        super(context);
    }

    public UiSettingsChildAeLock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetUiItemClickListner(SettingsChildClick menuItemClick) {
        SetMenuItemClickListner(this,false);
    }

    @Override
    public void onSettingsChildClick(SettingsChildAbstract item, boolean fromLeftFragment) {
        if (parameter == null)
            return;
        if (parameter.getStringValue().equals(getResources().getString(R.string.true_))) {
            parameter.setStringValue(getResources().getString(R.string.false_), true);
        }
        else{
            parameter.setStringValue(getResources().getString(R.string.true_),true);}
    }
}
