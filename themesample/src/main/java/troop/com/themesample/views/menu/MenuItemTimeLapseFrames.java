package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import java.util.ArrayList;

import troop.com.themesample.R;
import troop.com.themesample.subfragments.Interfaces;

/**
 * Created by troop on 29.08.2015.
 */
public class MenuItemTimeLapseFrames extends LinearLayout
{
    Button plus;
    Button minus;
    EditText editText;
    Context context;

    final float min = 0.1f;
    final float max = 30;
    float current;
    final float mover = 0.1f;
    final float bigmover = 1;
    AppSettingsManager appSettingsManager;
    String settingsname;


    public MenuItemTimeLapseFrames(Context context) {
        super(context);
        init(context, null);
    }

    public MenuItemTimeLapseFrames(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, null);
    }
    protected void init(Context context, AttributeSet attributeSet)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_childs_number, this);
        this.plus = (Button)findViewById(R.id.button_plus);
        this.minus = (Button)findViewById(R.id.button_minus);
        this.editText = (EditText)findViewById(R.id.editText_number);
        this.plus.setClickable(true);
        this.minus.setClickable(true);

        //this.setClickable(false);

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((current - bigmover) >= 1)
                    current -= bigmover;
                else if (current - mover > min)
                    current -= mover;
                setCurrent(current);
            }
        });
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current >= 1 && current + bigmover <= max)
                    current += bigmover;
                else if (current + mover <= 1) {
                    current += mover;
                }
                setCurrent(current);

            }
        });

    }

    public void setCurrent(float current)
    {
        String form = String.format("%.1f", current).replace(",", ".");
        try {

            current = Float.parseFloat(form);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        appSettingsManager.setString(settingsname, current+"");
        editText.setText(current + " fps");
    }

    public void SetStuff(AppSettingsManager appSettingsManager, String settingvalue) {
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingvalue;

        String fps = this.appSettingsManager.getString(settingsname);
        if (fps == null || fps.equals(""))
            fps = "30";
        editText.setText(fps + " fps");
        current = Float.parseFloat(fps);
    }
}
