package com.troop.freedcam.ui.handler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 25.09.2014.
 */
public class HelpOverlayHandler extends LinearLayout
{
    CheckBox checkBox;
    public AppSettingsManager appSettingsManager;

    public HelpOverlayHandler(Context context) {
        super(context);
        init(context);
    }

    public HelpOverlayHandler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public HelpOverlayHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.help_overlay, this);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        Button okButton = (Button)findViewById(R.id.button);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                appSettingsManager.setshowHelpOverlay(!checkBox.isChecked());
                setVisibility(GONE);
            }
        });
    }
}
