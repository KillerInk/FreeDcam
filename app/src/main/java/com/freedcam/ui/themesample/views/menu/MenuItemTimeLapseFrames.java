package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.freedcam.ui.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R;

/**
 * Created by troop on 29.08.2015.
 */
public class MenuItemTimeLapseFrames extends LinearLayout
{
    private final String TAG = MenuItemTimeLapseFrames.class.getSimpleName();
    private Button plus;
    private Button minus;
    private EditText editText;
    private Context context;

    private final float min = 0.1f;
    private final float max = 30;
    private float current;
    private final float mover = 0.1f;
    private final float bigmover = 1;
    private String settingsname;


    public MenuItemTimeLapseFrames(Context context) {
        super(context);
        init(context);
    }

    public MenuItemTimeLapseFrames(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    private void init(Context context)
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
        editText.setText(current + " fps");
    }

    private void setCurrent(float current)
    {
        String form = String.format("%.1f", current).replace(",", ".");
        try {

            current = Float.parseFloat(form);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        AppSettingsManager.APPSETTINGSMANAGER.setString(settingsname, current + "");
        editText.setText(current + " fps");
    }

    public void SetStuff() {
        this.settingsname = AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME;
        String fps = "";
        fps = AppSettingsManager.APPSETTINGSMANAGER.getString(settingsname);
        if (fps == null || fps.equals(""))
            fps = "30";
        editText.setText(fps + " fps");
        Logger.d(TAG, "set to " + fps);
        current = Float.parseFloat(fps);
    }
}
