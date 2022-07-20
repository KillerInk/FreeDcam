package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.troop.freedcam.R;
import com.troop.freedcam.databinding.SettingsBooleansettingschildBinding;

import freed.settings.mode.BooleanSettingModeInterface;

/**
 * Created by KillerInk on 05.01.2018.
 */

public class SettingsChild_BooleanSetting extends LinearLayout implements CompoundButton.OnCheckedChangeListener
{
    private final BooleanSettingModeInterface booleanSettingMode;
    SettingsBooleansettingschildBinding binding;

    public SettingsChild_BooleanSetting(Context context, final BooleanSettingModeInterface booleanSettingMode, int headerid, int descriptionid) {
        super(context);
        this.booleanSettingMode = booleanSettingMode;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater,R.layout.settings_booleansettingschild,this,true);
        binding.setParameter(booleanSettingMode);
        binding.switch1.setOnCheckedChangeListener(this);
        binding.header.setText(getResources().getText(headerid));
        binding.description.setText(getResources().getText(descriptionid));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!buttonView.isPressed()) {
            return;
        }
        booleanSettingMode.set(isChecked);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener checkedChangeListener)
    {
        binding.switch1.setOnCheckedChangeListener(checkedChangeListener);
    }
}
