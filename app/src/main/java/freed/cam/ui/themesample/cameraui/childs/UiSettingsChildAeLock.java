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
    public void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment) {
        if (parameter == null)
            return;
        if (parameter.GetStringValue().equals(getResources().getString(R.string.true_))) {
            parameter.SetValue(getResources().getString(R.string.false_), true);
        }
        else{
            parameter.SetValue(getResources().getString(R.string.true_),true);}
    }
}
