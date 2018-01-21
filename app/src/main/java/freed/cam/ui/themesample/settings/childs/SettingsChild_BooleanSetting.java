package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.troop.freedcam.R;

import freed.settings.mode.BooleanSettingModeInterface;
import freed.settings.mode.GlobalBooleanSettingMode;

/**
 * Created by KillerInk on 05.01.2018.
 */

public class SettingsChild_BooleanSetting extends LinearLayout
{
    private TextView description;

    private TextView headerText;
    private Switch aSwitch;
    private BooleanSettingModeInterface booleanSettingMode;

    public SettingsChild_BooleanSetting(Context context, final BooleanSettingModeInterface booleanSettingMode, int headerid, int descriptionid) {
        super(context);
        this.booleanSettingMode = booleanSettingMode;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.settings_booleansettingschild, this);
        headerText = (TextView) findViewById(R.id.header);
        aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setChecked(booleanSettingMode.get());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                booleanSettingMode.set(isChecked);
            }
        });
        description = (TextView) findViewById(R.id.description);
        headerText.setText(getResources().getText(headerid));
        description.setText(getResources().getText(descriptionid));
    }
}
