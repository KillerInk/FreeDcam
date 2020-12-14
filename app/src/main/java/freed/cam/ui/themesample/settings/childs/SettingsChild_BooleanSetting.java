package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.troop.freedcam.R;

import freed.settings.mode.BooleanSettingModeInterface;

/**
 * Created by KillerInk on 05.01.2018.
 */

public class SettingsChild_BooleanSetting extends LinearLayout implements CompoundButton.OnCheckedChangeListener
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
        headerText = findViewById(R.id.header);
        aSwitch = findViewById(R.id.switch1);
        aSwitch.setChecked(booleanSettingMode.get());
        aSwitch.setOnCheckedChangeListener(this);
        description = findViewById(R.id.description);
        headerText.setText(getResources().getText(headerid));
        description.setText(getResources().getText(descriptionid));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        booleanSettingMode.set(isChecked);
    }
}
